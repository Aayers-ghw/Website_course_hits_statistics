package com.imooc.spark

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 使用Spark Streaming完成有状态统计,并将结果写到MySQL数据库中
  */
object ForeachRDDApp {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("ForeachRDDApp")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val lines = ssc.socketTextStream("192.168.124.135", 9999)
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    result.print()   //此处仅仅是将统计结果输到控制台

    //TODO... 将结果写入到MySQL
    result.foreachRDD(rdd =>{

      rdd.foreachPartition(partitionOfRecords => {
          val connection = createConnection()
          partitionOfRecords.foreach(record => {
            val sql = "insert into wordcount(word, wordcount) values('" + record._1 + "', '" + record._2 +"')"
            connection.createStatement().execute(sql)
          })
          connection.close()
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * 获取MySQL连接
    */
  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection(
      "jdbc:mysql://localhost:3306/imooc_spark",
      "root",
      "123456")
  }
}
