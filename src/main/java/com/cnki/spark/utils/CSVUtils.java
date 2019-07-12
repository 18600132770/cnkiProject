package com.cnki.spark.utils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * CSV文件工具类
 */
public class CSVUtils implements Serializable {

    private static final long serialVersionUID = 1932300884253384890L;

    /**
     * 将dataset转换为csv（以逗号分隔的字符串）
     * 有个问题就是字符长度不能太长
     * @param dataset
     * @return
     */
    public static String datasetToCSVString(Dataset dataset){
        List<Row> rows = dataset.collectAsList();
        StringBuffer stringBuffer = new StringBuffer();
        String[] columns = dataset.columns();
        List<String> list = Arrays.asList(columns);
        list.forEach(column->{
            stringBuffer.append(column + ",");
        });

        stringBuffer.deleteCharAt(stringBuffer.length()-1).append("\n");

        for (Row row :
                rows) {
            for (int i = 0; i < row.length(); i++){
                stringBuffer.append(row.get(i) + ",");
            }
            stringBuffer.deleteCharAt(stringBuffer.length()-1).append("\n");
        }

        return stringBuffer.toString();
    }

}
