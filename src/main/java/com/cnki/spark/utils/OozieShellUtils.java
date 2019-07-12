package com.cnki.spark.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

import java.util.Properties;

/**
 * 运行shell脚本成功
 * @Author huag
 */
@Slf4j
public class OozieShellUtils {

    public static void main(String[] args) {

        System.setProperty("mapreduce.job.user.name", "admin");

        OozieClient wc = new OozieClient("http://10.170.128.57:11000/oozie");

        Properties conf = wc.createConfiguration();

        conf.setProperty("nameNode", "hdfs://spark1.cnki:8020");
        conf.setProperty("queueName", "default");
        conf.setProperty("examplesRoot", "examples");
        conf.setProperty("oozie.wf.application.path", "hdfs://spark1.cnki:8020/user/hue/oozie/workspaces/hue-oozie-1561443691.06");
//        conf.setProperty("oozie.wf.application.path", "/user/hue/oozie/workspaces/hue-oozie-1561111111.11");
//        conf.setProperty("oozie.wf.application.path", "/user/hue/oozie/workspaces/hue-oozie-1561440257.78");
        conf.setProperty("jobTracker", "spark1.cnki:8032");

        try {
            String jobId = wc.run(conf);
            System.out.println(jobId);
        } catch (OozieClientException e) {
            e.printStackTrace();
        }

    }

}
