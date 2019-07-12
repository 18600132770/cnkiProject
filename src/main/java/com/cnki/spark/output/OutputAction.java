package com.cnki.spark.output;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cnki.spark.conf.ConfigurationManager;
import com.cnki.spark.constant.Constants;
import com.cnki.spark.dao.IAnalyseResultDao;
import com.cnki.spark.dao.factory.DaoFactory;
import com.cnki.spark.entity.model.AnalyseResult;
import com.cnki.spark.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileWriter;
import java.util.*;

/**
 * 数据输出类
 * 以后会修改，暂时先用这个
 * @Author haug
 * 2019-07-05
 */
public class OutputAction {

    static String hfsFileDownloadUrl = null;

    public static void main(String[] args) {

        String arg0 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_OUTPUT_ARG0);
        String arg1 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_OUTPUT_ARG1);
        String arg2 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_OUTPUT_ARG2);
        String arg3 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_OUTPUT_ARG3);
        String arg4 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_OUTPUT_ARG4);

        if(ConfigurationManager.getBoolean(Constants.SPARK_LOCAL)){
            args = new String[]{arg0, arg1, arg2, arg3, arg4};
        }

        SparkSession spark = SparkSession
                .builder()
//                .master("local")
                .appName("OutputAction")
                .getOrCreate();

        if(args == null || args.length == 0){
            return;
        }

        String hdfsUploadUrl = args[0];//hdfs计算结果保存路径/DataAnalyse/1188/output
        String hdfsDownloadUrl = args[1];//hdfs数据源路径/DataAnalyse/1188/input
        String jsonString = args[2];//json格式的本节点任务参数信息  排序字段和排序方式

        Integer taskId = ParamUtils.getTaskId(hdfsUploadUrl);

        String fileNm = null;
        if(StringUtils.isNotBlank(jsonString)){
            fileNm = (String)JSON.parseObject(jsonString).get("fileNm");
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
                    hfsFileDownloadUrl = hdfsUploadUrl + "/" + id.split("spark-")[1] + ".csv";
                });
            }

        }

        Dataset<Row> csv = spark.read().option("header", "true").option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ").csv(hfsFileDownloadUrl);
        csv.show();

        String csvString = CSVUtils.datasetToCSVString(csv);

        IAnalyseResultDao analyseResultDao = DaoFactory.getAnalyseResukltDao();
        AnalyseResult analyseResult = new AnalyseResult();
        analyseResult.setDataanalyseid(taskId);
        analyseResult.setAnalyseresultname(fileNm);
        analyseResult.setNodeid(cpNodeId);
        analyseResult.setHfskey(cpNodeId + ".csv");
        analyseResult.setCreateuser("huag");
        analyseResult.setModifyuser("huag");

        String hdfsUploadFileUrl = hdfsUploadUrl+"/"+cpNodeId+".csv";
        String hdfsUploadFileName = cpNodeId+".csv";

        try {
            HdfsFileUtils.uploadFileByStringBytes(csvString, hdfsUploadFileUrl);
            analyseResult.setStatus(1);
            RabbitMQHelper.pushMessageToRabbitMQ(hdfsUploadFileUrl + "," + hdfsUploadFileName);
            RabbitMQHelper.close();
        }catch (Exception e){
            analyseResult.setStatus(0);
            e.printStackTrace();
        }finally {
            analyseResultDao.insert(analyseResult);
            System.out.println("mysql数据写入完毕...");
        }

        spark.close();

    }

}
