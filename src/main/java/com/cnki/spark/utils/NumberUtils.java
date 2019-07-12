package com.cnki.spark.utils;

import org.apache.commons.lang.StringUtils;

/**
 * 数字格式化工具
 * @Author huag
 * 2019-07-10
 */
public class NumberUtils {

    /**
     * 字符串转整数
     * @param string
     * @return
     */
    public static Integer stringToInteger(String string){
        if(StringUtils.isNotBlank(string)){
            return Integer.valueOf(string);
        }else {
            return null;
        }
    }

}
