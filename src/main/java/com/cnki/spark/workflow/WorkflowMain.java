package com.cnki.spark.workflow;

import com.cnki.spark.entity.model.CpNode;

import java.util.List;

/**
 * 解析List<CpNode>，将节点集合解析成workflow.xml文件
 * @Author huag
 * 2019-06-26
 */
public class WorkflowMain {

    public static void main(String[] args) {

        String processId = "2019070903";//任务流程图id
        List<CpNode> cpNodeList = MockDataUtils.mockData9();//模拟数据，id最好以spark-开头

        String oozieUrl = "http://10.170.128.57:11000/oozie";
        String nameNode = "hdfs://spark1.cnki:8020";
        String jobTracker = "spark1.cnki:8032";
        String hfsUploadUrl = "http://192.168.100.92:8080/api/Resource/UploadFile";
        String hfsDownloadUrl = "http://192.168.100.92:8080/getfile/index?filename=";

        WorkflowUtils.generateWorkflowFile(cpNodeList, processId, hfsUploadUrl, hfsDownloadUrl);//传入流程图applcation名，可以是流程图唯一id
        OozieUtils.workflowFileUploadToHdfs(processId);//将本地oozie配置文件上传到hdfs，传入流程图唯一id

        String oozieJobId = OozieUtils.runOozieJob(oozieUrl, nameNode, jobTracker, processId);

        System.out.println(oozieJobId);

    }

}
