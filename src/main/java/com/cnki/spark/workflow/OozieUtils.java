package com.cnki.spark.workflow;

import com.cnki.spark.utils.HdfsFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

import java.util.Properties;

/**
 * 将spark的oozie任务配置文件上传到hdfs上
 * @Author huag
 * 2019-06-25
 */
@Slf4j
public class OozieUtils {

    /**
     * 将本地的workflow.xml job.properties hive-site.xml 文件上传到hdfs
     * @param processId
     */
    public static void workflowFileUploadToHdfs(String processId){
        System.setProperty("HADOOP_USER_NAME","admin");

        String localFile_hive_site = "data/oozie/hive-site.xml";
        String localFile_job_properties = "data/oozie/job.properties";
        String localFile_workflow = "data/oozie/workflow.xml";

        String hdfsFile_hive_site = "/user/hue/oozie/workspaces/hue-oozie-"+ processId + "/lib/hive-site.xml";
        String hdfsFile_job_properties = "/user/hue/oozie/workspaces/hue-oozie-" + processId + "/job.properties";
        String hdfsFile_workflow = "/user/hue/oozie/workspaces/hue-oozie-" + processId + "/workflow.xml";

        try {
            HdfsFileUtils.uploadFile(localFile_hive_site, hdfsFile_hive_site);
            HdfsFileUtils.uploadFile(localFile_job_properties, hdfsFile_job_properties);
            HdfsFileUtils.uploadFile(localFile_workflow, hdfsFile_workflow);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 运行oozie任务
     */
    public static String runOozieJob(String oozieUrl, String nameNode, String jobTracker, String processId){

        OozieClient wc = new OozieClient(oozieUrl);
        Properties conf = wc.createConfiguration();
        conf.setProperty("nameNode", nameNode);
        conf.setProperty("jobTracker", jobTracker);
        conf.setProperty("hue-id-w", processId);
        conf.setProperty("oozie.wf.application.path", "/user/hue/oozie/workspaces/hue-oozie-" + processId);
        conf.setProperty("oozie.use.system.libpath", "True");
        conf.setProperty("dryrun", "False");
        conf.setProperty("security_enabled", "False");
        conf.setProperty("send_email", "False");
        conf.setProperty("user.name", "admin");
        conf.setProperty("oozie.wf.rerun.failnodes", "false");
        conf.setProperty("mapreduce.job.user.name", "admin");

        try {
            String jobId = wc.run(conf);
            return jobId;
        } catch (OozieClientException e) {
            e.printStackTrace();
            return "fail";
        }
    }

}
