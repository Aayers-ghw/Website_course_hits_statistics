package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 使用Spark Streaming处理文件(local/hdfs)系统的数据
  */
object FileWordCount {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("FileWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val lines = ssc.textFileStream("C:\\Users\\1515182041\\Desktop\\data");
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _);
    result.print()

    ssc.start()
    ssc.awaitTermination()

  }
}
