package com.imooc.spark.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

/**
 * Kafka生产者
 */
public class KafkaProducer extends Thread {

    private String topic;

    private Producer<Integer, String> producer;

    public KafkaProducer(String topic) {
        this.topic = topic;

        Properties properties = new Properties();

        properties.put("zookeeper.connect", KafkaProperties.ZK);
        properties.put("metadata.broker.list", KafkaProperties.BROKER_LIST);
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");

        producer = new Producer<Integer, String>(new ProducerConfig(properties));
    }

    @Override
    public void run() {

        int messageNo = 1;

        while (messageNo <= 10) {
            String message = "message_" + messageNo;
//            producer.send(new KeyedMessage<Integer, String>(topic, message));

            producer.send(new KeyedMessage<Integer, String>(topic, message));
            messageNo++;

            System.out.println("Send:" + message);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
