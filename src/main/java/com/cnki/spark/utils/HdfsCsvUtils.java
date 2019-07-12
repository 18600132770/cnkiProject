package com.cnki.spark.utils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * 本地读取csv文件
 * hdfs读取csv文件
 * dataset保存到hdfs
 */
public class HdfsCsvUtils {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("HdfsCsvUtils")
                .getOrCreate();

//        Dataset<Row> csv = spark.read().option("header", "true").csv("data/KMeans.csv");
        Dataset<Row> csv = spark.read().option("header", "true").csv("hdfs://spark1.cnki:8020/spark-test/ml/Kmeans.csv");

        csv.show();
//        System.out.println(csv.count());

//        csv.write().format("csv").save("/spark-result/ttt20190628");
//
//        Dataset<Row> csv1 = spark.read().option("header", "true").csv("/spark-result/ttt20190628");
//        csv1.show();
//        System.out.println("csv1.count = " + csv1.count());

        csv.registerTempTable("t1");

        Dataset<Row> sql = spark.sql("select * from t1 order by `1`");

        sql.show();

        spark.close();

    }

}
