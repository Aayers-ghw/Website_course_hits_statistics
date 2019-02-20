package com.imooc.spark.project.dao

import com.imooc.spark.project.domain.CourseClickCount
import com.imooc.spark.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * 实战课程点击数数据访问层
  */
object CourseClickCountDAO {

  val tableName = "course_clickcount";
  val cf = "info"
  val qualifer = "click_count"

  /**
    * 保存数据到HBase
    * @param list CourseClickCount集合
    */
  def save(list: ListBuffer[CourseClickCount]): Unit = {

    val table = HBaseUtils.getInstance().getTable(tableName)

    for(ele <- list){
      table.incrementColumnValue(Bytes.toBytes(ele.day_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count
        )
    }

  }

  /**
    * 根据rowkey查询值
    * @param day_course
    * @return
    */
  def count(day_course: String): Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_course))
    val value = table.get(get).getValue(cf.getBytes(), qualifer.getBytes())

    if(value == null){
      0l
    }else{
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {

    val list = new ListBuffer[CourseClickCount]
    list.append(CourseClickCount("20181111_8", 8))
    list.append(CourseClickCount("20181111_9", 9))
    list.append(CourseClickCount("20181111_1", 100))

    save(list)

    println(count("20181111_8"))
    println(count("20181111_9"))
    println(count("20181111_1"))
  }
}
