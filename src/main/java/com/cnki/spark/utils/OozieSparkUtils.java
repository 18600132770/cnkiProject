package com.cnki.spark.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

import java.util.Properties;

/**
 * Oozie运行oozie上的spark任务
 */
@Slf4j
public class OozieSparkUtils {

    public static void main(String[] args) {

        String oozieTaskId = "hue-oozie-00000001.08";

        OozieClient wc = new OozieClient("http://10.170.128.57:11000/oozie");

        Properties conf = wc.createConfiguration();

        conf.setProperty("nameNode", "hdfs://spark1.cnki:8020");
        conf.setProperty("jobTracker", "spark1.cnki:8032");

        conf.setProperty("hue-id-w", "0023");
        conf.setProperty("oozie.wf.application.path", "/user/hue/oozie/workspaces/" + oozieTaskId);
        conf.setProperty("oozie.use.system.libpath", "True");
        conf.setProperty("dryrun", "False");
        conf.setProperty("security_enabled", "False");
        conf.setProperty("send_email", "False");
        conf.setProperty("user.name", "admin");
        conf.setProperty("oozie.wf.rerun.failnodes", "false");
        conf.setProperty("mapreduce.job.user.name", "admin");


        try {
            String jobId = wc.run(conf);
            System.out.println(jobId);
        } catch (OozieClientException e) {
            e.printStackTrace();
        }

    }

}
