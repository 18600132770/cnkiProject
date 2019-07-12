package com.cnki.spark.utils;

/**
 * 将spark的oozie任务配置文件上传到hdfs上
 * @Author huag
 * 2019-06-25
 */
public class OozieFileUpload {

    public static void main(String[] args) {

        String oozieTaskId = "hue-oozie-00000001.08";

        System.setProperty("HADOOP_USER_NAME","admin");

        String localFile_hive_site = "data/oozie/hive-site.xml";
        String localFile_job_properties = "data/oozie/job.properties";
        String localFile_workflow = "data/oozie/workflow.xml";

        String hdfsFile_hive_site = "/user/hue/oozie/workspaces/" + oozieTaskId + "/lib/hive-site.xml";
        String hdfsFile_job_properties = "/user/hue/oozie/workspaces/" + oozieTaskId + "/job.properties";
        String hdfsFile_workflow = "/user/hue/oozie/workspaces/" + oozieTaskId + "/workflow.xml";

        try {
            HdfsFileUtils.uploadFile(localFile_hive_site, hdfsFile_hive_site);
            HdfsFileUtils.uploadFile(localFile_job_properties, hdfsFile_job_properties);
            HdfsFileUtils.uploadFile(localFile_workflow, hdfsFile_workflow);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
