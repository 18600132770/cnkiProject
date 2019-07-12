package com.cnki.spark.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * hdfs读写文件工具
 * @Author huag
 * 2019-06-17
 */
public class HdfsFileUtils {

    public static FileSystem getFileSystem() throws Exception{
        //core-site.xml     core-default.xml    hdfs-site.xml   hdfs-default.xml
        Configuration conf = new Configuration();
        //get filesystem
        FileSystem fileSystem = FileSystem.get(conf);
        return fileSystem;
    }

    /**
     * 从hdfs读文件
    * @param fileName
     * @throws Exception
     */
    public static void readFile(String fileName) throws Exception{
        System.setProperty("HADOOP_USER_NAME","hdfs");
        FileSystem fileSystem = getFileSystem();

        Path path = new Path(fileName);

        FSDataInputStream inputStream = fileSystem.open(path);

        try {
            IOUtils.copyBytes(inputStream, System.out, 4096, false);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeStream(inputStream);
        }
    }

    /**
     * 将本地文件上传到hdfs
     * @param localFile 本地文件路径
     * @param hdfsFile  保存在hdfs上的文件
     * @throws Exception
     */
    public static void uploadFile(String localFile, String hdfsFile) throws Exception{
        System.setProperty("HADOOP_USER_NAME","hdfs");
        FileSystem fileSystem = getFileSystem();
        Path wirtePath = new Path(hdfsFile);
        FSDataOutputStream outputStream = fileSystem.create(wirtePath);
        FileInputStream inputStream = new FileInputStream(new File(localFile));
        try {
            IOUtils.copyBytes(inputStream, outputStream, 4096, false);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }

    }

    /**
     * 将一个数据流上传到hdfs
     * @param string
     * @param hdfsFile
     * @throws Exception
     */
    public static void uploadFileByStringBytes(String string, String hdfsFile) throws Exception{
        System.setProperty("HADOOP_USER_NAME","hdfs");
        FileSystem fileSystem = getFileSystem();
        Path wirtePath = new Path(hdfsFile);
        FSDataOutputStream outputStream = fileSystem.create(wirtePath);
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        try {
            IOUtils.copyBytes(inputStream, outputStream, 4096, false);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }

    }

    public static void main(String[] args) throws Exception{

        System.setProperty("HADOOP_USER_NAME","hdfs");

        //读文件测试
//        String fileName = "hdfs://spark1.cnki:8020/spark-test/ml/Kmeans.csv";
        String fileName = "hdfs://spark1.cnki:8020/DataAnalyse/1188/output/3d789b67-4703-494f-92a0-e8e84ce8102a.csv";
        readFile(fileName);

        //写文件测试
//        String localFile = "data/Kmeans.csv";
//        String hdfsFile = "hdfs://spark1.cnki:8020/spark-result/Kmeans.csv";
//
//        uploadFile(localFile, hdfsFile);

    }

}
