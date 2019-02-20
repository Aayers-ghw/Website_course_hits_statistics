# Website_course_hits_statistics
基于Spark Streaming对课程网站用户行为实时分析，用Spring Boot构建Web项目，使用charts构建动态数据可视化

### 项目实战  
- 需求说明
- 互联网访问日志概述
- 功能开发及本地运行
- 生产环境运行

### 需求说明
- 今天到现在为止实战课程的访问量
- 今天到现在为止从收索引擎引流过来的实战课程的访问量

### 为什么要记录用户访问行为日志
- 网站页面的访问量
- 网站的黏性
- 推荐

### 用户行为日志内容
![Alt text](https://i.loli.net/2019/02/20/5c6d125c5ffc3.png)

### 用户行为日志分析的意义
- 网站的眼睛
- 网站的神经
- 网站的大脑

### 使用python脚本实时产生数据
- Python实时日志产生器  
Linux crontab  
&emsp;网站：https://tool.lu/crontab/  
&emsp;每一分钟执行一次的crontab表达式：*/1 * * * *  
$ crontab -e  
&emsp;*/1 * * * * /home/hadoop/data/project/log_generator.sh

![Alt text](https://i.loli.net/2019/02/20/5c6d14810cbc9.png)

### 打通Flume&Kafka&Spark Streaming线路
对接python日志生产器输出的日志到Flume  
streaming_project.conf  

选型：access.log	==>		控制台输出  
&emsp;exec  
&emsp;memory  
&emsp;logger  

steaming_project.conf  

exec-memory-logger.sources = exec-source  
exec-memory-logger.sinks = logger-sink  
exec-memory-logger.channels = memory-channel  
 
exec-memory-logger.sources.exec-source.type = exec  
exec-memory-logger.sources.exec-source.command = tail -F /home/hadoop/data/project/logs/access.log  
exec-memory-logger.sources.exec-source.shell = /bin/bash -c  

exec-memory-logger.channels.memory-channel.type = memory  

exec-memory-logger.sinks.logger-sink.type = logger

exec-memory-logger.sources.exec-source.channels = memory-channel
exec-memory-logger.sinks.logger-sink.channel = memory-channel


启动  
flume-ng agent \  
--name exec-memory-logger \  
--conf $FLUME_HOME/conf \  
--conf-file /home/hadoop/data/project/streaming_project.conf /  
-Dflume.root.logger = INFO,console  

日志 ==> Flume ==> Kafka  
&emsp;启动zk：./zkServer.sh start  
&emsp;启动Kafka Server：kafka-server-start.sh -daemon /home/hadoop/app/kafka_2.11-0.9.0.0/config/server.properties  
&emsp;修改Flume配置文件使得flume sink数据到kafka  

streaming_project2.conf  

exec-memory-kafka.sources = exec-source  
exec-memory-kafka.sinks = kafka-sink  
exec-memory-kafka.channels = memory-channel  

exec-memory-kafka.sources.exec-source.type = exec  
exec-memory-kafka.sources.exec-source.command = tail -F /home/hadoop/data/project/logs/access.log  
exec-memory-kafka.sources.exec-source.shell = /bin/bash -c  
  
exec-memory-kafka.channels.memory-channel.type = memory  

exec-memory-kafka.sinks.kafka-sink.type = org.apache.flume.sink.kafka.KafkaSink  
exec-memory-kafka.sinks.kafka-sink.topic = streamingtopic  
exec-memory-kafka.sinks.kafka-sink.brokerList = hadoop-5:9092  
exec-memory-kafka.sinks.kafka-sink.requiredAcks = 1  
exec-memory-kafka.sinks.kafka-sink.BatchSize = 5  

exec-memory-kafka.sources.exec-source.channels = memory-channel  
exec-memory-kafka.sinks.kafka-sink.channel = memory-channel  

flume-ng agent \  
--name exec-memory-kafka \  
--conf $FLUME_HOME/conf \  
--conf-file /home/hadoop/data/project/streaming_project2.conf \  
-Dflume.root.logger = INFO,console  

kafka-console-consumer.sh --zookeeper hadoop-5:2181 --topic streamingtopic   

### 打通Flume&Kafka&Spark Streaming线路   

- 在Spark应用程序接收到数据并完成记录数统计  

### 数据清洗操作：从原始日志中取出我们所需要的字段信息就可以了  

数据清洗结果类似如下：  
ClickLog(98.63.29.168,20190216025202,128,500,-)  
ClickLog(55.143.132.29,20190216025202,112,200,-)  
ClickLog(46.124.167.55,20190216025202,146,404,-)  
ClickLog(72.30.167.168,20190216025202,131,200,-)  
ClickLog(55.156.187.63,20190216025202,128,500,-)  
ClickLog(63.132.98.168,20190216025202,131,500,https://search.yahoo.com/search?p=Spark Streaming实战)  
ClickLog(98.29.63.46,20190216025202,131,200,-)  
ClickLog(98.10.63.168,20190216025202,131,500,-)  
ClickLog(132.156.10.30,20190216025501,145,500,-)  
ClickLog(72.46.30.168,20190216025501,112,200,-)  
 
到数据清洗完为止，日志中只包含了实战课程的日志  

### 功能：统计今天到现在为止实战课程的访问量  

功能1：今天到现在为止	实战课程	的访问量  
&emsp;&emsp;yyyyMMdd	courseid  

使用数据库来进行存储我们的统计结果  
&emsp;&emsp;Spark&emsp;Steaming把统计结果写入到数据库里面  
&emsp;&emsp;可视化前端：yyyyMMdd&emsp;&emsp;courseid&emsp;&emsp;把数据库里面的统计结果展示出来  

选择什么数据库作为统计结果的存储呢？  
&emsp;&emsp;RDBMS：MySQL、Oracle...  
&emsp;&emsp;&emsp;&emsp;day&emsp;&emsp;course_id&emsp;&emsp;click_count  
&emsp;&emsp;&emsp;&emsp;20181111&emsp;&emsp;1&emsp;&emsp;10  
&emsp;&emsp;&emsp;&emsp;20181111&emsp;&emsp;1&emsp;&emsp;10  

&emsp;&emsp;&emsp;&emsp;下一个批次数据进来以后：  
&emsp;&emsp;&emsp;&emsp;20181111  +   1  ==>  click_count  +   下一个批次的统计结果==>写入到数据库中  

&emsp;&emsp;NoSQL：HBase、Redis...  
&emsp;&emsp;&emsp;&emsp;HBase：一个API就能搞定，非常方便  
&emsp;&emsp;&emsp;&emsp;&emsp;20181111   +    1   ==>  click_count	   +   下一个批次的统计结果  
&emsp;&emsp;&emsp;&emsp;选择HBase的一个原因所在  

&emsp;&emsp;前提：启动HDFS、zookeeper、HBase  

&emsp;&emsp;HBase表设计  
&emsp;&emsp;&emsp;&emsp;创建表  
&emsp;&emsp;&emsp;&emsp;&emsp;create 'course_clickcount', 'info'  
&emsp;&emsp;&emsp;&emsp;Rowkey设计  
&emsp;&emsp;&emsp;&emsp;&emsp;day_courseid  

如何使用Scala来操作HBase  

### 功能：统计今天到现在为止从搜索引擎引流过来的实战课程的访问量  

HBase表设计   
create 'course_search_clickcount', 'info'  
rowkey设计：也是根据我们的业务需求来的  

20181111 + search ＋1  

### 将项目运行在服务器环境中  

- 编译打包  
- 运行  

项目打包：mvn clean package -DskipTests  
报错：[ERROR] /Users/rocky/source/work/sparktrain/src/main/scala/com/imooc/spark/project/dao/CourseClickCountDAO.scala:4:error:object HBaseUtils is not a member of package com.imooc.spark.project.utils  

spark-submit --master local[5] \  
--class com.imooc.spark.project.spark.ImoocStatStreamingApp \  
/home/hadoop/lib/sparktrain-1.0.jar \  
192.168.124.135:2181 test streamingtopic 1  

报错：  
Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/spark/streaming/kafka/KafkaUtils$  

去官网上查阅  
解决：  
spark-submit --master local[5] \  
--class com.imooc.spark.project.spark.ImoocStatStreamingApp \  
--packages org.apache.spark:spark-streaming-kafka-0-8_2.11:2.2.0 \  
/home/hadoop/lib/sparktrain-1.0.jar \  
192.168.124.135:2181 test streamingtopic 1  
 
报错：  
ERROR Executor: Exception in task 4.0 in stage 3.0 (TID 4)  
java.lang.NoClassDefFoundError: org/apache/hadoop/hbase/client/HBaseAdmin  

解决：  
spark-submit --master local[5] \  
--jars $(echo /home/hadoop/app/hbase-1.2.0-cdh5.7.0/lib/*.jar | tr ' ' ',') \  
--class com.imooc.spark.project.spark.ImoocStatStreamingApp \  
--packages org.apache.spark:spark-streaming-kafka-0-8_2.11:2.2.0 \  
/home/hadoop/lib/sparktrain-1.0.jar \  
192.168.124.135:2181 test streamingtopic 1  
 
提交作业时，注意事项：  
1）--packages的使用  
2）--jars的使用  
 
### 可视化  

- 为社么需要可视化  
- Spring Boot构建Web项目  
- 使用charts构建静态数据可视化  
- 使用charts构建动态数据可视化  
- 阿里云DataV数据可视化  
 
Spring Boot整合Echarts动态获取HBase的数据  
1）动态的传递进去当天的时间  
&emsp;&emsp;a）在代码中写死  
&emsp;&emsp;b）怎么查询昨天的、前天？  
&emsp;&emsp;&emsp;&emsp;在页面中放一个时间插件(jQuery插件)，默认只取当天的数据  
2）自动刷新展示图  
&emsp;&emsp;每隔多久发送一个请求去刷新当前的数据供展示	  

统计慕课网当天实战课程从搜索引擎过来的点击量  
&emsp;&emsp;数据已经在HBase中有的  
&emsp;&emsp;通过Echarts整合Spring Boot方式自己来实现  

### Spring Boot项目部署到服务器上运行  

java -jar web-0.0.1-SNAPSHOT.jar  
 
### 使用DataV展示统计结果  

DataV功能说明  
1）	点击量省排名/运营商访问占比  
&emsp;&emsp;Spark SQL项目实战课程：通过IP就能解析到省份、城市、运营商  
2）	浏览器访问占比/操作系统占比  
&emsp;&emsp;Hadoop项目：user Agent  

DataV访问的数据库(MySQL)，需要能够在公网上访问   

### 成果展示
![Alt text](https://i.loli.net/2019/02/20/5c6d1258bb566.png)