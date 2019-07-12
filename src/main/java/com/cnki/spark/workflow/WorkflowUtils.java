package com.cnki.spark.workflow;

import com.alibaba.fastjson.JSON;
import com.cnki.spark.entity.model.CpNode;
import com.cnki.spark.entity.model.ForkNode;
import com.cnki.spark.entity.model.JoinNode;
import org.xml.sax.InputSource;

import java.io.FileWriter;
import java.io.StringReader;
import java.util.List;

/**
 * workflow.xml文件生成工具
 * @Author huag
 * 2019-06-26
 */
public class WorkflowUtils {

    /**
     * 获取所有fork节点的workflow内容
     * @return
     */
    public static String getForkWorkflow(List<ForkNode> forkNodeList){
        StringBuffer forkWorkflow = new StringBuffer();
        forkNodeList.stream().forEach(forkNode -> {
            forkWorkflow.append("    <fork name=\"")
                    .append(forkNode.getId())
                    .append("\">")
                    .append("\n");

            List<String> targetIds = forkNode.getTargetIds();

            targetIds.stream().forEach(targetId ->{
                forkWorkflow.append("        <path start=\"")
                        .append(targetId)
                        .append("\" />")
                        .append("\n");
            });

            forkWorkflow.append("    </fork>")
                    .append("\n");
        });
        return forkWorkflow.toString();
    }

    /**
     * 获取所有JoinNode节点的workflow内容
     * @param joinNodeList
     * @return
     */
    public static String getJoinWorkflow(List<JoinNode> joinNodeList){

        StringBuffer joinWorkflow = new StringBuffer();

        joinNodeList.stream().forEach(joinNode -> {
            joinWorkflow.append("    <join name=\"")
                    .append(joinNode.getId())
                    .append("\" to=\"")
                    .append(joinNode.getTarget())
                    .append("\"/>")
                    .append("\n");
        });

        return joinWorkflow.toString();
    }

    public static String getSparkWorkflow(List<CpNode> cpNodeList, String hfsUploadUrl, String hfsDownloadUrl){
        StringBuffer sparkWorkflow = new StringBuffer();
        cpNodeList.stream().forEach(cpNode -> {
            sparkWorkflow.append("    <action name=\"")
                    .append(cpNode.getId())
                    .append("\">\n");
            sparkWorkflow.append("        <spark xmlns=\"uri:oozie:spark-action:0.2\">\n" +
                    "            <job-tracker>${jobTracker}</job-tracker>\n" +
                    "            <name-node>${nameNode}</name-node>\n" +
                    "            <master>" + cpNode.getMaster() + "</master>\n" +
                    "            <mode>" + cpNode.getMode() + "</mode>\n" +
                    "            <name>" + cpNode.getName() + "</name>\n");

            sparkWorkflow.append("            <class>")
                    .append(cpNode.getSparkTaskName())
                    .append("</class>\n");

            sparkWorkflow.append("            <jar>")
                    .append(cpNode.getJar())
                    .append("</jar>\n");

            sparkWorkflow.append("            <spark-opts>")
                    .append(cpNode.getOpts())
                    .append("</spark-opts>\n");

            sparkWorkflow.append("              <arg>") //第一个参数：hfs服务器上传文件路径
                    .append(hfsUploadUrl)
                    .append("</arg>\n");

            sparkWorkflow.append("              <arg>") //第二个参数：hfs服务器下载文件路径
                    .append(hfsDownloadUrl)
                    .append("</arg>\n");

            sparkWorkflow.append("              <arg>") //第三个参数：data json串
                    .append(cpNode.getData().replace("<", "lessAndLessAndLess"))
                    .append("</arg>\n");

            sparkWorkflow.append("              <arg>") //第四个参数：本节点id
                    .append(cpNode.getId())
                    .append("</arg>\n");

            if(cpNode.getPreviousCpNodeMap() != null && cpNode.getPreviousCpNodeMap().size() > 0){//第五个参数：source节点Map<id, type> 的JSON串
                String previsousCpNodeJSON = JSON.toJSONString(cpNode.getPreviousCpNodeMap());
                sparkWorkflow.append("              <arg>")
                        .append(previsousCpNodeJSON)
                        .append("</arg>\n");
            }

            sparkWorkflow.append("            <file>")
                    .append(cpNode.getFile())
                    .append("</file>\n");

            sparkWorkflow.append("        </spark>\n");

            if(cpNode.getTargetIds() != null && cpNode.getTargetIds().size() >0){
                sparkWorkflow.append("        <ok to=\"")
                        .append(cpNode.getTargetIds().get(0))
                        .append("\"/>\n");
            }else{
                sparkWorkflow.append("        <ok to=\"End\"/>\n");
            }

            sparkWorkflow.append("        <error to=\"" + cpNode.getFailTargetId() + "\"/>\n" +     //以后可以修改为失败后指向Email
                    "    </action>\n");

        });

        return sparkWorkflow.toString();
    }

    /**
     * 生成失败节点workflow
     * @return
     */
    public static String getKillWorkflow(){
        return new String("    <kill name=\"Kill\">\n" +
                "        <message>Action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]</message>\n" +
                "    </kill>\n");
    }

    /**
     * 生成End节点workflow
     * @return
     */
    public static String getEndWorkflow(){
        return new String(" <end name=\"End\"/>\n");
    }

    /**
     * 生成workflow-app
     * @return
     */
    public static String getWorkflowAppStart(String workflowAppName){
        return new String("<workflow-app name=\""+ workflowAppName +"\" xmlns=\"uri:oozie:workflow:0.5\">\n");
    }

    /**
     * 生成workflow-app
     * @return
     */
    public static String getWorkflowAppEnd(){
        return new String("</workflow-app>\n");
    }


    /**
     * 生成<start to=""/>
     * @param startId
     * @return
     */
    public static String getStartWorkflow(String startId){
        return new String("    <start to=\"" + startId + "\"/>\n");
    }

    /**
     * 本地生成workflow.xml文件
     * @param cpNodeList
     * @param applicationName
     */
    public static void generateWorkflowFile(List<CpNode> cpNodeList, String applicationName, String hfsUploadUrl, String hfsDownloadUrl){

        CpNodeUtils.calculateTargetIds(cpNodeList);//计算所有节点的targetIds

        CpNodeUtils.organizeDataSource(cpNodeList);//整理数据源，将数据源的节点作为参数放入target节点，并将target节点的sourceIds中的本数据源的id删除

        List<ForkNode> forkNodeList = CpNodeUtils.calculateForkNodes(cpNodeList);//分析流程图，计算并生成fork节点

        List<JoinNode> joinNodeList = CpNodeUtils.calculateJoinNodes(cpNodeList);//分析流程图，计算并生成join节点

        CpNodeUtils.generateOnlyStartNode(cpNodeList, forkNodeList);//生成具有唯一开始节点的流程图

        String startTaskId = CpNodeUtils.getStartTaskId(cpNodeList, joinNodeList, forkNodeList);//分析流程图，计算开始节点

        System.out.println(cpNodeList);

        String sparkWorkflow = getSparkWorkflow(cpNodeList, hfsUploadUrl, hfsDownloadUrl);
        String forkWorkflow = getForkWorkflow(forkNodeList);
        String joinWorkflow = getJoinWorkflow(joinNodeList);
        String endWorkflow = getEndWorkflow();
        String killWorkflow = getKillWorkflow();
        String workflowAppStart = getWorkflowAppStart(applicationName);//可以将本次流程的唯一id放在这里
        String workflowAppEnd = getWorkflowAppEnd();
        String startWorkflow = getStartWorkflow(startTaskId);

        InputSource inputSource = new InputSource(new StringReader(workflowAppStart));

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("data/oozie/workflow.xml");
            fileWriter.write(workflowAppStart);
            fileWriter.write(startWorkflow);
            fileWriter.write(killWorkflow);
            fileWriter.write(sparkWorkflow);
            fileWriter.write(forkWorkflow);
            fileWriter.write(joinWorkflow);
            fileWriter.write(endWorkflow);
            fileWriter.write(workflowAppEnd);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
