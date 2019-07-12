package com.cnki.spark.utils;

import com.cnki.spark.conf.ConfigurationManager;
import com.cnki.spark.constant.Constants;
import org.apache.spark.SparkConf;

/**
 * spark工具类
 * @Author huag
 * 2019-07-09
 */
public class SparkUtils {

    /**
     * 根据配置文件中得设置决定是否进行本地测试
     * @param conf
     */
    public static void setMaster(SparkConf conf){
        boolean local = ConfigurationManager.getBoolean(Constants.SPARK_LOCAL);
        if(local){
            conf.setMaster("local");
        }
    }

}
