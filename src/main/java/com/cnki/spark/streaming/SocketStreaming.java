package com.cnki.spark.streaming;

import com.cnki.spark.conf.ConfigurationManager;
import com.cnki.spark.constant.Constants;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * SparkStreaming socket通信
 * @Author huag
 * 2019-06-21
 */
public class SocketStreaming {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("SocketStreaming");

        String ip = ConfigurationManager.getProperty(Constants.SPARK1_CNKI_IP);

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("spark1.cnki", 6666);

        JavaDStream<String> flatmap = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());

        JavaPairDStream<String, Integer> mapToPair = flatmap.mapToPair(word -> new Tuple2<>(word, 1));

        JavaPairDStream<String, Integer> javaPairDStream = mapToPair.reduceByKey(((v1, v2) -> v1 + v2));

        javaPairDStream.print();

        JavaPairDStream<String, Integer> streamWindow = mapToPair.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        }, Durations.seconds(60), Durations.seconds(10));

//        streamWindow.foreachRDD(new VoidFunction2<JavaPairRDD<String, Integer>, Time>() {
//            @Override
//            public void call(JavaPairRDD<String, Integer> v1, Time v2) throws Exception {
//
//            }
//        });

        jssc.start();
        try {
            jssc.awaitTermination();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
