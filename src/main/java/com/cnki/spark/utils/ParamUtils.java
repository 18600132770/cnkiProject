package com.cnki.spark.utils;

/**
 * 节点接收的参数处理工具类
 * @Author huag
 * 2019-07-10
 */
public class ParamUtils {

    /**
     * 根据hdfsUploadUrl获得taskId
     * @param hdfsUploadUrl
     * @return
     */
    public static Integer getTaskId(String hdfsUploadUrl){
        String taskId = hdfsUploadUrl.split("/")[hdfsUploadUrl.split("/").length-2];
        return NumberUtils.stringToInteger(taskId);
    }

}
