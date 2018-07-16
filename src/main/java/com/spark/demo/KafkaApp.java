package com.spark.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.*;

/**
 * spark streaming接受kafka数据计算word count
 */
public class KafkaApp {
    public static void main(String[] args) throws InterruptedException {
        String brokers = "192.168.38.135:9092";
        String topics = "test";
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("streaming word count");
        JavaSparkContext sc = new JavaSparkContext(conf);
        sc.setLogLevel("WARN");
        JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.seconds(1));

        Collection<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
        //kafka相关参数，必要！缺了会报错
        Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put("bootstrap.servers", brokers);
        kafkaParams.put("group.id", "group1");
        kafkaParams.put("enable.auto.commit", "true");
        kafkaParams.put("auto.commit.interval.ms", "1000");
        kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        ////如果没有初始化偏移量或者当前的偏移量不存在任何服务器上，可以使用这个配置属性
        ////可以使用这个配置，latest自动重置偏移量为最新的偏移量
        //        earliest
        //        当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
        //        latest
        //        当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
        //        none
        //        topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
        kafkaParams.put("auto.offset.reset","earliest");
        //Topic分区
//        Map<TopicPartition, Long> offsets = new HashMap<TopicPartition, Long>();
//        offsets.put(new TopicPartition(topics, 0), 2L);
        //通过KafkaUtils.createDirectStream(...)获得kafka数据，kafka相关参数由kafkaParams指定
        JavaInputDStream<ConsumerRecord<Object,Object>> lines = KafkaUtils.createDirectStream(
                ssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topicsSet, kafkaParams)
        );
        //这里就跟之前的demo一样了，只是需要注意这边的lines里的参数本身是个ConsumerRecord对象
        JavaPairDStream<String, Integer> counts =
                lines.flatMap(x -> Arrays.asList(x.value().toString().split(" ")).iterator())
                        .mapToPair(x -> new Tuple2<String, Integer>(x, 1))
                        .reduceByKey((x, y) -> x + y);
//        counts.print();
        //  可以打印所有信息，看下ConsumerRecord的结构
        counts.foreachRDD(rdd -> {
          rdd.foreach(x -> {
            System.out.println("word count is"+x);
          });
        });
        ssc.start();
        ssc.awaitTermination();
        ssc.close();

    }
}
