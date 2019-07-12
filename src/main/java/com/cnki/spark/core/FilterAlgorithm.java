package com.cnki.spark.core;

import com.alibaba.fastjson.JSON;
import com.cnki.spark.conf.ConfigurationManager;
import com.cnki.spark.constant.Constants;
import com.cnki.spark.utils.CSVUtils;
import com.cnki.spark.utils.HdfsFileUtils;
import com.cnki.spark.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.apache.spark.sql.functions.col;
import java.util.Map;

/**
 * 筛选算法
 * @Author huag
 * 2019-07-10
 */
public class FilterAlgorithm {

    static String hfsFileDownloadUrl = null;

    public static void main(String[] args) {

        String arg0 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_FILTER_ARG0);
        String arg1 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_FILTER_ARG1);
        String arg2 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_FILTER_ARG2_3);
        String arg3 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_FILTER_ARG3);
        String arg4 = ConfigurationManager.getProperty(Constants.WORKFLOW_SPARK_FILTER_ARG4);

        if(ConfigurationManager.getBoolean(Constants.SPARK_LOCAL)){
            args = new String[]{arg0, arg1, arg2, arg3, arg4};
        }

        if(args == null || args.length == 0){
            return;
        }

        SparkSession spark = SparkSession
                .builder()
//                .master("local")
                .appName("FilterAlgorithm")
                .getOrCreate();

        String hdfsUploadUrl = args[0];//hdfs计算结果保存路径
        String hdfsDownloadUrl = args[1];//hdfs数据源路径
        String jsonString = JSONUtils.replaceLessChar(args[2]);//json格式的本节点任务参数信息  排序字段和排序方式

        String[] filterFields = JSONUtils.getArrayStringToArray(jsonString, "filterFields");//需要过滤掉的字段
        JSONArray fieldJSONArray = JSONUtils.getFieldJSONArray(jsonString, "lists");//json字符串中lists字段的json数组

        String cpNodeId = args[3].split("spark-")[1];//本节点id

        if(args.length > 4){
            String previousCpNodeString = args[4];//source节点的Map<id, type>集合的json格式
            java.util.Map<String, String> previousCpNodeMap = JSON.parseObject(previousCpNodeString, Map.class);
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

        Dataset<Row> filterFieldsDataset = csv.drop(filterFields);//过滤掉需要删除的字段的dataset

        for (int i = 0; i < fieldJSONArray.length(); i++){
            JSONObject jsonObject = fieldJSONArray.getJSONObject(i);
            String selectedFilterMethod = jsonObject.get("selectedFilterMethod").toString();
            String selectedFilterNotNull = jsonObject.has("selectedFilterNotNull")==true?jsonObject.get("selectedFilterNotNull").toString():null;
            String selectedItem = jsonObject.get("selectedItem").toString();
            if(!"range".equals(selectedFilterMethod)){  //  > ,  >= ,   <,   <= , =
                String filterValue = jsonObject.get("filterValue").toString();
                if(">".equals(selectedFilterMethod)){
                    filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).gt(filterValue));
                }else if(">=".equals(selectedFilterMethod)){
                    filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).$greater$eq(filterValue));
                }else if("<".equals(selectedFilterMethod)){
                    filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).$less(filterValue));
                }else if("<=".equals(selectedFilterMethod)){
                    filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).$less$eq(filterValue));
                }else if ("=".equals(selectedFilterMethod)){
                    filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).$eq$eq$eq(filterValue));
                }
            }else if("range".equals(selectedFilterMethod)){  //  range
                String leftValue = jsonObject.get("leftValue").toString();
                String rightValue = jsonObject.get("rightValue").toString();
                filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).between(leftValue, rightValue));
            }
            if(StringUtils.isNotBlank(selectedFilterNotNull)){
                filterFieldsDataset = filterFieldsDataset.filter(col(selectedItem).isNotNull());
            }
        }

//        filterFieldsDataset.show();

        String csvString = CSVUtils.datasetToCSVString(filterFieldsDataset);

        try {
            HdfsFileUtils.uploadFileByStringBytes(csvString, hdfsUploadUrl+"/"+cpNodeId+".csv");
        }catch (Exception e){
            e.printStackTrace();
        }

        spark.close();


    }

}
