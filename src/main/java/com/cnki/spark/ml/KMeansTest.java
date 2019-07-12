package com.cnki.spark.ml;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * KMeans算法
 * @Author huag
 * 2019-06-14
 */
public class KMeansTest {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("KMeansTest")
                .getOrCreate();

        Dataset<Row> dataset = spark.read().format("libsvm").load("hdfs://spark1.cnki:8020/spark-test/ml/sample_kmeans_data.txt");

        dataset.show();

        int k = 2;
        int maxIterations = 100;
        long seed = 1L;

        KMeans kMeans = new KMeans()
                .setK(k)
                .setMaxIter(maxIterations)
                .setSeed(seed);

        KMeansModel model = kMeans.fit(dataset);

        //使用平方差来评估
        double WSSSE = model.computeCost(dataset);
        System.out.println("平方差 = " + WSSSE);

        Vector[] centers = model.clusterCenters();

        for (Vector vector :
                centers) {
            System.out.println(vector);
        }

        spark.stop();

    }

}
