package com.imooc.spark

import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark Streaming对接Kafka
  */
object KafkaStreamingApp {

  def main(args: Array[String]): Unit = {

    if(args.length != 4) {
      System.err.println("Usage: KafkaStreamingApp <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }

    val Array(zkQuorum, group, topics, numThreads) = args

    val sparkConf = new SparkConf().setMaster("local[2]")
      .setAppName("KafkaStreamingApp")
    val sc = new SparkContext(sparkConf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5))

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

    //TODO... Spark Streaming如何对接 Kafka
    val messages = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    messages.print()
    messages.map(_._2).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
