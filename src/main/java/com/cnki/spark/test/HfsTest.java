package com.cnki.spark.test;

import com.cnki.spark.utils.HfsUtils;
import com.cnki.spark.utils.HttpClientHelper;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hfs上传文件测试
 * @Author huag
 * 2019-07-02
 */
public class HfsTest {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("HdfsUtils")
                .getOrCreate();

        String hfsDownloadFileUrl = "http://192.168.100.92:8080/getfile/index?filename=Kmeans.csv";
        String serverUrl = "http://192.168.100.92:8080/api/Resource/UploadFile";
        String csvFilePath = "data/hfs20190706.csv";
        String fileName = "hfs20190706.csv";
        Map<String, String> map = new HashMap<>();
        map.put("fileName", fileName);

        List<String> list = HfsUtils.downloadFromHfs(hfsDownloadFileUrl);

        List<StructField> fields = new ArrayList<StructField>();

        if(list!=null&&list.size()>0){

            List<Row> rowList = new ArrayList<>();

            for (int i = 0; i<list.size(); i++){
                if(i==0){
                    String[] titles = list.get(0).split(",");
                    for(int j = 0;j<titles.length;j++){
                        fields.add(DataTypes.createStructField(titles[j], DataTypes.StringType, true));
                    }

                }else{
                    Row row = RowFactory.create(list.get(i).split(","));
                    rowList.add(row);
                }
            }

            StructType schema = DataTypes.createStructType(fields);
            Dataset<Row> dataFrame = spark.createDataFrame(rowList, schema);

            dataFrame.show(1000);


        }


        try {
            String resp = HttpClientHelper.getInstance().uploadFileImpl(serverUrl, csvFilePath, fileName, map);
            System.out.println(resp);
        }catch (Exception e){
            e.printStackTrace();
        }



    }

}
