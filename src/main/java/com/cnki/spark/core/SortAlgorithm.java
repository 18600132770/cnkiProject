package com.cnki.spark.core;

import com.alibaba.fastjson.JSON;
import com.cnki.spark.conf.ConfigurationManager;
import com.cnki.spark.constant.Constants;
import com.cnki.spark.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.FileWriter;
import java.util.*;

/**
 * 排序算法
 * @Author huag
 * 2019-07-03
 */
public class SortAlgorithm {

    static String hfsFileDownloadUrl = null;

    public static void main(String[] args){

        String arg0 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_SORT_ARG0);
        String arg1 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_SORT_ARG1);
        String arg2 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_SORT_ARG2);
        String arg3 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_SORT_ARG3);
        String arg4 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_SORT_ARG4);

        if(ConfigurationManager.getBoolean(Constants.SPARK_LOCAL)){
            args = new String[]{arg0, arg1, arg2, arg3, arg4};
        }

        if(args == null || args.length == 0){
            return;
        }

        SparkSession spark = SparkSession
                .builder()
//                .master("local")
                .appName("SortAlgorithm")
                .getOrCreate();

        String hdfsUploadUrl = args[0];//hdfs计算结果保存路径
        String hdfsDownloadUrl = args[1];//hdfs数据源路径
        String jsonString = JSONUtils.replaceLessChar(args[2]);//json格式的本节点任务参数信息  排序字段和排序方式

        String selectedColumn = null;
        String selectedStyle = null;
        try {
            System.out.println("开始解析selectColumn selectedStyle");
            if(StringUtils.isNotBlank(jsonString)){
                Map<String, String> previousCpNodeMap = JSON.parseObject(jsonString, Map.class);
                selectedColumn = previousCpNodeMap.get("selectedColumn");
                selectedStyle = previousCpNodeMap.get("selectedStyle");
                System.out.println(selectedColumn);
                System.out.println(selectedStyle);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String cpNodeId = args[3].split("spark-")[1];//本节点id

        if(args.length > 4){
            String previousCpNodeString = args[4];//source节点的Map<id, type>集合的json格式
            Map<String, String> previousCpNodeMap = JSON.parseObject(previousCpNodeString, Map.class);
            if(previousCpNodeMap != null && previousCpNodeMap.size() > 0){
                previousCpNodeMap.entrySet().parallelStream().forEach(previousCpNode ->{
                    String id = previousCpNode.getKey();
                    String value = (String)previousCpNode.getValue();
                    System.out.println("id = " + id);
                    hfsFileDownloadUrl = hdfsDownloadUrl + "/" + id.split("spark-")[1] + ".csv";
                });
            }

        }



        Dataset<Row> csv = spark.read().option("header", "true").option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ").csv(hfsFileDownloadUrl);

        csv.registerTempTable("tempTable");

        Dataset<Row> datasetResult = spark.sql("select * from tempTable order by `" + selectedColumn + "` " + selectedStyle);

        datasetResult.show();

//        datasetResult.write().option("header", "true").csv(hfsUploadUrl+"/"+cpNodeId+".csv");

        String csvString = CSVUtils.datasetToCSVString(datasetResult);

        try {
            HdfsFileUtils.uploadFileByStringBytes(csvString, hdfsUploadUrl+"/"+cpNodeId+".csv");
        }catch (Exception e){
            e.printStackTrace();
        }

        spark.close();

    }

}
