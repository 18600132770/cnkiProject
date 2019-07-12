package com.cnki.spark.utils;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

/**
 * Hdfs上传下载工具 textFile saveAsTextFile
 * 2019-06-14
 * @Author huag
 */
public class HdfsUtils {

    /**
     * 从hdfs下载文件
     * @param spark
     * @param path
     * @return
     */
    public static JavaRDD<String> downLoadFile(SparkSession spark, String path){
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        JavaRDD<String> fileRDD = sc.textFile(path);
        return fileRDD;
    }

    /**
     * 上传文件到hdfs
     * @param rdd
     * @param path
     */
    public static void uploadFile(JavaRDD rdd, String path){
        rdd.saveAsTextFile(path);
    }



    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("MysqlConnectTest")
                .master("local")
                .getOrCreate();

        String path = "hdfs://spark1.cnki:8020/spark-test/trainSegmenagriculture*";

        JavaRDD<String> hdfsFileRDD = HdfsUtils.downLoadFile(spark, path);

        hdfsFileRDD.foreach(lines -> System.out.println(lines));

        HdfsUtils.uploadFile(hdfsFileRDD, "hdfs://spark1.cnki:8020/spark-result/wordCountResult0000");

        spark.stop();

    }


}
