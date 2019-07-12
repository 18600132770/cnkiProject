package com.cnki.spark.test;

import com.cnki.spark.utils.RabbitMQHelper;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Spark中使用RabbitMQ消息队列
 * @Author huag
 * 2019-07-02
 */
public class RabbitMQSparkTest {

    public static void main(String[] args) throws Exception{

        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("RabbitMQSparkTest")
                .getOrCreate();

        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 2, 3, 4, 5));

        Integer sum = rdd.reduce((v1, v2) -> v1 + v2);

        System.out.println(sum);

        Map<String, String> map = new HashMap<>();


        for (int i = 0; i < 20; i++) {
            Thread.sleep(1000);
            map.put("RabbitMQ", String.valueOf(i));
            System.out.println("写入第 " + i + " 次");
            RabbitMQHelper.pushMessageToRabbitMQ(map);
        }

        RabbitMQHelper.close();

        spark.close();


    }

}
