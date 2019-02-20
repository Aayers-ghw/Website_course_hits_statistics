package com.imooc.spark

import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark Streaming对接Kafka方式二
  */
object KafkaDirectWordCount {

  def main(args: Array[String]): Unit = {

    if(args.length != 2) {
      System.err.println("Usage: KafkaDirectWordCount <brokers> <topics>")
      System.exit(1)
    }

    val Array(brokers, topics) = args

    val sparkConf = new SparkConf().setMaster("local[2]")
      .setAppName("KafkaDirectWordCount")
    val sc = new SparkContext(sparkConf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5))

    val topicsSet = topics.split(",").toSet
    val kafakParams = Map[String, String]("metadata.broker.list" -> brokers)

    //TODO... Spark Streaming如何对接 Kafka
//    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
//      ssc, kafakParams, topicsSet
//    )

//    messages.print()
//    messages.map(_._2).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
