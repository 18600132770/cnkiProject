package com.cnki.spark.workflow;

import com.alibaba.fastjson.JSON;
import com.cnki.spark.entity.model.CpNode;

import java.util.*;

public class MockDataUtils {

    /**
     * 由于是测试数据，设置spark任务的参数
     * @param cpNodeList
     */
    public static List<CpNode> setCpNodeSparkParams(List<CpNode> cpNodeList){
        cpNodeList.forEach(cpNode -> {
            cpNode.setMaster("local");
            cpNode.setMode("client");
            cpNode.setName("SortAlgorithm");
            cpNode.setCpType("dataType");
            cpNode.setSparkTaskName("com.cnki.spark.core.SortAlgorithm");//数据库中获得
//            cpNode.setSparkTaskName("com.cnki.spark.test.HfsTest");//数据库中获得
//            cpNode.setSparkTaskName("com.cnki.spark.test.RabbitMQSparkTest");//数据库中获得
//            cpNode.setSparkTaskName("com.cnki.spark.workflow.Node3");//数据库中获得
            cpNode.setJar("cnkiProject-1.0-SNAPSHOT.jar");
            cpNode.setOpts("--driver-memory 5g   --executor-memory 5g ");//数据库中获得
            Map<String, String> map = new HashMap<>();
            map.put("RabbitMQ","1");
            map.put("kafka","3");
            String mapString = JSON.toJSONString(map);
            map.clear();
            map.put("data",mapString);
//            cpNode.setParams(map);//单个节点接收的参数
            cpNode.setFile("/user/hue/oozie/spark-jar/cnkiProject-1.0-SNAPSHOT.jar#cnkiProject-1.0-SNAPSHOT.jar");
            cpNode.setFailTargetId("Kill");//可以指向Kill、Email
        });
        return cpNodeList;
    }

    /**
     * CpNode模拟数据生成方法
     *        1    2
     *  1是数据源
     * @return
     */
    public static List<CpNode> mockData0(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("3d789b67-4703-494f-92a0-e8e84ce8102a");
        cpNode1.isStart = true;
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("82ef9d97-3bf4-400b-82a8-8575001bc941");
        cpNode2.setSparkTaskName("com.cnki.spark.core.SortAlgorithm");//数据库中获得
        cpNode2.setInput(new ArrayList(Arrays.asList("3d789b67-4703-494f-92a0-e8e84ce8102a")));
        cpNode2.setSparkTaskName("SparkCalculateTask1");
        Map<String, String> map = new HashMap<>();
        map.put("selectedColumn", "id(id)");
        map.put("selectedStyle", "asc");
        String string = JSON.toJSONString(map);
        map.clear();
        map.put("data", string);
        cpNode2.setParams(map);

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          2
     *      1       4
     *          3
     * @return
     */
    public static List<CpNode> mockData1(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.setInput(new ArrayList(Arrays.asList("001")));
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("003");
        cpNode3.setInput(new ArrayList(Arrays.asList("001")));
        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("002", "003")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          2   4
     *      1              5
     *          3
     * @return
     */
    public static List<CpNode> mockData2(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.setInput(new ArrayList(Arrays.asList("001")));
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("003");
        cpNode3.setInput(new ArrayList(Arrays.asList("001")));
        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("002")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode5 = new CpNode();
        cpNode5.setId("005");
        cpNode5.setInput(new ArrayList(Arrays.asList("003", "004")));
        cpNode5.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);
        cpNodeList.add(cpNode5);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          1
     *              3  -> 4
     *          2
     * 1，2数据源，传递给3
     * @return
     */
    public static List<CpNode> mockData3(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.isStart = true;
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.isStart = true;
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("003");
        cpNode3.setInput(new ArrayList(Arrays.asList("001", "002")));
        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("003")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          1
     *          2   -> 4  -> 5
     *          3
     *
     * 1，2数据源，传递给3
     * @return
     */
    public static List<CpNode> mockData4(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.isStart = true;
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.isStart = true;
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("003");
        cpNode3.isStart = true;
        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("001", "002", "003")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode5 = new CpNode();
        cpNode5.setId("005");
        cpNode5.setInput(new ArrayList(Arrays.asList("004")));
        cpNode5.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);
        cpNodeList.add(cpNode5);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          1
     *             -> 3  -> 4
     *          2               ->   6
     *                      5
     * @return
     */
    public static List<CpNode> mockData5(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("003");
        cpNode3.setInput(new ArrayList(Arrays.asList("001", "002")));
        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("003")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode5 = new CpNode();
        cpNode5.setId("005");
        cpNode5.isStart = true;
        cpNode5.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode6 = new CpNode();
        cpNode6.setId("006");
        cpNode6.setInput(new ArrayList(Arrays.asList("004", "005")));
        cpNode6.setSparkTaskName("SparkCalculateTask2");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);
        cpNodeList.add(cpNode5);
        cpNodeList.add(cpNode6);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          1
     *             -> 3  -> 4
     *          2
     * @return
     */
    public static List<CpNode> mockData6(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("003");
        cpNode3.setInput(new ArrayList(Arrays.asList("001", "002")));
        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("003")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          1                       6
     *          2   ->  4   ->  5
     *          3                       7
     * @return
     */
    public static List<CpNode> mockData7(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.setSparkTaskName("SparkCalculateTask1");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("001", "002")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode5 = new CpNode();
        cpNode5.setId("005");
        cpNode5.setInput(new ArrayList(Arrays.asList("004")));
        cpNode5.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode6 = new CpNode();
        cpNode6.setId("006");
        cpNode6.setInput(new ArrayList(Arrays.asList("005")));
        cpNode6.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode7 = new CpNode();
        cpNode7.setId("007");
        cpNode7.setInput(new ArrayList(Arrays.asList("005")));
        cpNode7.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
        cpNodeList.add(cpNode4);
        cpNodeList.add(cpNode5);
        cpNodeList.add(cpNode6);
        cpNodeList.add(cpNode7);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     * CpNode模拟数据生成方法
     *          1                       6
     *          2   ->  4   ->  5               ->      8
     *          3                       7
     * @return
     */
    public static List<CpNode> mockData8(){
        List<CpNode> cpNodeList = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("001");
        cpNode1.setSparkTaskName("SparkReadHdfsFile1");

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("002");
        cpNode2.setSparkTaskName("SparkCalculateTask1");

//        CpNode cpNode3 = new CpNode();
//        cpNode3.setId("003");
//        cpNode3.setSparkTaskName("SparkCalculateTask2");

        CpNode cpNode4 = new CpNode();
        cpNode4.setId("004");
        cpNode4.setInput(new ArrayList(Arrays.asList("001", "002")));
        cpNode4.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode5 = new CpNode();
        cpNode5.setId("005");
        cpNode5.setInput(new ArrayList(Arrays.asList("004")));
        cpNode5.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode6 = new CpNode();
        cpNode6.setId("006");
        cpNode6.setInput(new ArrayList(Arrays.asList("005")));
        cpNode6.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode7 = new CpNode();
        cpNode7.setId("007");
        cpNode7.setInput(new ArrayList(Arrays.asList("005")));
        cpNode7.setSparkTaskName("SparkUpLoadFileTask");

        CpNode cpNode8 = new CpNode();
        cpNode8.setId("008");
        cpNode8.setInput(new ArrayList(Arrays.asList("006", "007")));
        cpNode8.setSparkTaskName("SparkUpLoadFileTask");

        cpNodeList.add(cpNode1);
        cpNodeList.add(cpNode2);
//        cpNodeList.add(cpNode3);
        cpNodeList.add(cpNode4);
        cpNodeList.add(cpNode5);
        cpNodeList.add(cpNode6);
        cpNodeList.add(cpNode7);
        cpNodeList.add(cpNode8);

        return setCpNodeSparkParams(cpNodeList);
    }

    /**
     *      1   ->    2     ->      3
     *
     *  数据源     SortAlgorithm   OutputAction
     *
     * @return
     */
    public static List<CpNode> mockData9(){
        List<CpNode> list = new ArrayList<>();

        CpNode cpNode1 = new CpNode();
        cpNode1.setId("cd09f499-f40b-4914-82b9-8ea42677cd58");
        cpNode1.setCpType("input");
        cpNode1.isStart = true;

        CpNode cpNode2 = new CpNode();
        cpNode2.setId("3d789b67-4703-494f-92a0-e8e84ce8102a");
        cpNode2.setInput(new ArrayList<>(Arrays.asList("cd09f499-f40b-4914-82b9-8ea42677cd58")));
        cpNode2.setMaster("yarn");
        cpNode2.setMode("cluster");
        cpNode2.setName("SortAlgorithm");
        cpNode2.setCpType("sort");
        cpNode2.setSparkTaskName("com.cnki.spark.core.SortAlgorithm");//数据库中获得
        cpNode2.setJar("cnkiProject-1.0-SNAPSHOT.jar");
        cpNode2.setOpts("--driver-memory 5g   --executor-memory 5g ");//数据库中获得
        Map<String, String> map = new HashMap<>();
        String mapString = "{\"itemsColumn\":[{\"label\":\"id(id)\",\"value\":\"id(id)\"},{\"label\":\"name(name)\",\"value\":\"name(name)\"}],\"dictionary\":[{\"name\":\"id(id)\"},{\"datatype\":\"INT\",\"name\":\"id(id)\"},{\"datatype\":\"VARCHAR\",\"name\":\"name(name)\"}],\"selectedColumn\":\"id(id)\",\"itemsStyle\":[{\"label\":\"升序\",\"value\":\"asc\"},{\"label\":\"降序\",\"value\":\"desc\"}],\"selectedMethod\":\"memory\",\"itemsMethod\":[{\"label\":\"内存计算\",\"value\":\"memory\"},{\"label\":\"云计算\",\"value\":\"cloud\"}],\"sortExID\":\"cd09f499-f40b-4914-82b9-8ea42677cd58\",\"selectedStyle\":\"desc\"}";
        map.put("data", mapString);
        cpNode2.setParams(map);//单个节点接收的参数
        cpNode2.setFile("/user/hue/oozie/spark-jar/cnkiProject-1.0-SNAPSHOT.jar#cnkiProject-1.0-SNAPSHOT.jar");
        cpNode2.setFailTargetId("Kill");//可以指向Kill、Email

        CpNode cpNode3 = new CpNode();
        cpNode3.setId("82ef9d97-3bf4-400b-82a8-8575001bc941");
        cpNode3.setInput(new ArrayList<>(Arrays.asList("3d789b67-4703-494f-92a0-e8e84ce8102a")));
        cpNode3.setMaster("yarn");
        cpNode3.setMode("cluster");
        cpNode3.setName("OutputAction");
        cpNode3.setCpType("output");
        cpNode3.setSparkTaskName("com.cnki.spark.output.OutputAction");//数据库中获得
        cpNode3.setJar("cnkiProject-1.0-SNAPSHOT.jar");
        cpNode3.setOpts("--driver-memory 5g   --executor-memory 5g ");//数据库中获得
        Map<String, String> map3 = new HashMap<>();
        String mapString3 = "{\"dictionary\":[{\"datatype\":\"VARCHAR\",\"name\":\"标题\"},{\"datatype\":\"VARCHAR\",\"name\":\"分类\"},{\"datatype\":\"VARCHAR\",\"name\":\"内容\"}],\"fileNm\":\"test5\",\"pmmlNm\":\"\"}";
        map3.put("data", mapString3);
        cpNode3.setParams(map3);//单个节点接收的参数
        cpNode3.setFile("/user/hue/oozie/spark-jar/cnkiProject-1.0-SNAPSHOT.jar#cnkiProject-1.0-SNAPSHOT.jar");
        cpNode3.setFailTargetId("Kill");//可以指向Kill、Email

        list.add(cpNode1);
        list.add(cpNode2);
        list.add(cpNode3);

        return list;
    }


}
