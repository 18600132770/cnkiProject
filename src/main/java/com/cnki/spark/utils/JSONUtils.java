package com.cnki.spark.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;

import java.io.Serializable;

/**
 * json解析工具
 */
public class JSONUtils implements Serializable {

    private static final long serialVersionUID = 7941940164142488546L;

    /**
     * 获取json字符串中某一个filed字段的数组集合
     * "filterFields": ["lng(lng)","id(id)"]
     * @param jsonString
     * @param field
     * @return
     */
    public static String[] getArrayStringToArray(String jsonString, String field){

        if(StringUtils.isBlank(jsonString)){
            return null;
        }

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String arrayString = jsonObject.get(field).toString();//["lng(lng)","id(id)"]
        String[] array = arrayString.substring(1, arrayString.length()-1).split(",");
        for (int i = 0; i< array.length; i++){
            if(array[i].startsWith("\"")){
                array[i] = array[i].substring(1);
            }
            if(array[i].endsWith("\"")){
                array[i] = array[i].substring(0, array[i].length()-1);
            }
        }
        return  array;
    }

    /**
     * 获取jsonString字符串的field字段嵌套的json数组
     * @param jsonString
     * @param field
     * @return
     */
    public static JSONArray getFieldJSONArray(String jsonString, String field){
        if(StringUtils.isNotBlank(jsonString)){
            String fieldString = JSON.parseObject(jsonString).get(field).toString();
            return new JSONArray(fieldString);
        }else {
            return null;
        }

    }

    /**
     * 由于workflow传递进来的第三个参数可能会出现 < 符号，这个符号不符合xml文件规则
     * 所以和后端商定传递过来的 < 用lessAndLessAndLess替换了
     * 此出再将lessAndLessAndLess 替换为 <
     * @param jsonString
     * @return
     */
    public static String replaceLessChar(String jsonString){
        if(StringUtils.isNotBlank(jsonString)){
            return jsonString.replace("lessAndLessAndLess", "<");
        }else {
            return jsonString;
        }
    }

}
