package com.cnki.spark.conf;

import java.io.InputStream;
import java.util.Properties;

/**
 * 配置管理组件
 * @Author huag
 * 2019-06-14
 */
public class ConfigurationManager {

    private static Properties properties = new Properties();

    static {
        try {
            InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream("my.properties");
            properties.load(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获得配置项中指定key对应的value
     * @param key
     * @return
     */
    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    /**
     * 获取整数型的配置项
     * @param key
     * @return
     */
    public static Integer getInteger(String key){
        String value = properties.getProperty(key);
        try {
            return Integer.valueOf(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取Long类型的配置项
     * @param key
     * @return
     */
    public static Long getLong(String key){
        String value = properties.getProperty(key);
        try {
            return Long.valueOf(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 获取Boolean类型的配置项
     * @param key
     * @return
     */
    public static Boolean getBoolean(String key){
        String value = properties.getProperty(key);
        try {
            return Boolean.valueOf(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
