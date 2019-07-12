package com.cnki.spark.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Hfs读数据和写数据
 * 2019-06-13
 * @Author huag
 */
public class HfsUtils {

    /**
     * 从Hfs下载文件
     * @param hfsDownloadUrl
     * @return
     */
    public static List<String> downloadFromHfs(String hfsDownloadUrl){
        List<String> list = new ArrayList();

//        list.add("关键字,age");

        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(hfsDownloadUrl);
        int code = 0;
        try {
            code = client.executeMethod(getMethod);
            if (code == 200) {
                InputStream inputStream = getMethod.getResponseBodyAsStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line;
                while((line = in.readLine())!=null){
                    if(line.startsWith("\uFEFF")){
                        System.out.println("出现编码错误UTF-8 windows");
                        line = line.replace("\uFEFF", "");
                    }
                    if(line.startsWith("\\uFEFF>")){
                        line = line.replace("\\uFEFF", "");
                        System.out.println("出现编码错误UTF-8 linux");
                    }
                    if(line.startsWith("<feff>")){
                        line = line.replace("<feff>", "");
                        System.out.println("出现编码错误UTF-8 linux");
                    }
                    list.add(line);
                    System.out.println(line);
                }
                System.out.println("读取数据文件完毕...........");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
