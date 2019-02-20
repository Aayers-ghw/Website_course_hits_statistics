package com.imooc.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming 整合Flume的第一种方式
  */
object FlumePushWordCount {

  def main(args: Array[String]): Unit = {

    if (args.length != 2){
      System.out.println("Usage: FlumePushWordCount <hostname> <port>")
      System.exit(1)
    }

    val Array(hostname, port) = args;

    val sparkConf = new SparkConf().setAppName("FlumePushWordCount")
    val sc = new SparkContext(sparkConf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5))

    //TODO... 如何使用Spark Streaming整合Flume
    val flumeStream = FlumeUtils.createStream(ssc, hostname, port.toInt)
    flumeStream.map(x => new String(x.event.getBody.array()).trim)
        .flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
