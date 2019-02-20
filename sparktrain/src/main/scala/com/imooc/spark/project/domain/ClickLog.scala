package com.imooc.spark.project.domain

/**
  * 清洗后的日志文件
  * @param ip 日志访问的的IP地址
  * @param time 日志访问的时间
  * @param courseID 日志访问的实战课程编号
  * @param statusCode 日志访问的状态吗
  * @param referer  日志访问的referer
  */
case class ClickLog(ip:String, time:String, courseID:Int, statusCode:Int, referer:String)
