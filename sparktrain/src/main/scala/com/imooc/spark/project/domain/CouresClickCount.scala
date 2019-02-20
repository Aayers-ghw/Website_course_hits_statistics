package com.imooc.spark.project.domain

/**
  * 实战课程点击数实体类
  * @param day_course 对应的就是HBase中的rowkey，20181222-1
  * @param click_count  对应的20181222_1的访问总数
  */
case class CourseClickCount (day_course:String, click_count:Long)
