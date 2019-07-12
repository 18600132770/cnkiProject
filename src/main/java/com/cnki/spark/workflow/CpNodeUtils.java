package com.cnki.spark.workflow;

import com.cnki.spark.entity.model.CpNode;
import com.cnki.spark.entity.model.ForkNode;
import com.cnki.spark.entity.model.JoinNode;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * CpNode对象测试工具类
 * @Author huag
 * 2019-06-26
 */
public class CpNodeUtils {



    /**
     * 获取start节点
     * @param cpNodeList
     * @return
     */
    public static String getStartTaskId(List<CpNode> cpNodeList, List<JoinNode> joinNodeList, List<ForkNode> forkNodeList){
        //1、统计cpNode节点所有的targetId
        Set<String> idSet = new HashSet<>();

        String startId = new String();
        for (CpNode cpNode :
                cpNodeList) {
            if (cpNode.getInput() == null || cpNode.getInput().size() == 0) {
                startId = cpNode.getId();
                break;
            }
            idSet.addAll(cpNode.getTargetIds());
        }

        if(StringUtils.isBlank(startId)){

            for (JoinNode joinNode :
                    joinNodeList) {
                if(!idSet.contains(joinNode.getId())){
                    startId = joinNode.getId();
                }

            }
        }

        if(StringUtils.isBlank(startId)){
            for (ForkNode forkNode :
                    forkNodeList) {
                if (!idSet.contains(forkNode.getId())){
                    startId = forkNode.getId();
                }
            }
        }
        
        return startId;
    }

    /**
     * 计算所有节点的targetIds
     * @param cpNodeList
     */
    public static void calculateTargetIds(List<CpNode> cpNodeList){
        //将所有节点的id和本节点的input集合中的id添加字符spark-
        cpNodeList.forEach(cpNode -> {
            cpNode.setId("spark-" + cpNode.getId());
            List<String> input = cpNode.getInput();
            List<String> inputNew = new ArrayList<>();
            if(input != null && input.size()>0){
                input.forEach(id -> {
                    inputNew.add("spark-" + id);
                });
                cpNode.setInput(inputNew);
            }
        });

        cpNodeList.stream().forEach(cpNode -> {
            String id = cpNode.getId();
            Set<String> targetIds = new HashSet<>();
            cpNodeList.stream().forEach(node -> {
                List<String> sourceIds = node.getInput();//sourceIds
                if(sourceIds != null && sourceIds.size()>0){
                    sourceIds.stream().forEach(sourceId -> {
                        if(StringUtils.isNotBlank(sourceId) && sourceId.equals(id)){
                            targetIds.add(node.getId());
                        }
                    });
                }
            });
            cpNode.setTargetIds(new ArrayList<>(targetIds));
        });
    }

    /**
     * 计算有多少个fork节点，并创建ForkNode对象（自己的id和指向的节点的id）
     * fork前的节点的targetid指向本fork的id
     * fork指向原节点的targetId
     * @param cpNodeList
     * @return
     */
    public static List<ForkNode> calculateForkNodes(List<CpNode> cpNodeList){

        List<ForkNode> forkNodeList = new ArrayList<>();

        cpNodeList.stream().forEach(cpNode -> {
            List<String> targetIds = cpNode.getTargetIds();
            //1、遍历所有节点，寻找有多个target的节点，此时就该产生fork对象了
            if(targetIds != null && targetIds.size() > 1){
                ForkNode forkNode = new ForkNode();
                String uuid = "fork-" + UUID.randomUUID().toString().replace("-", "");
                forkNode.setId(uuid);
                forkNode.setTargetIds(targetIds);
                forkNodeList.add(forkNode);

                //2、cpNode节点的target指向forkId
                cpNode.setTargetIds(Arrays.asList(forkNode.getId()));
                cpNode.setForked(true);

                //3、cpNode节点原来target节点的sourceId全部变为forkId
                for (String targetId :
                        targetIds) {
                    cpNodeList.stream().forEach(cpNode1 -> {
                        if(StringUtils.isNotBlank(targetId) && targetId.equals(cpNode1.getId())){
                            cpNode1.setInput(Arrays.asList(forkNode.getId()));
                        }
                    });

                }

            }
        });

        return forkNodeList;

    }


    /**
     * 计算有多少个join节点，并创建JoinNode对象（自己的id和指向的节点的id）
     * 原节点的source节点指向joinNode
     * joinNode节点指向原节点
     * @param cpNodeList
     * @return
     */
    public static List<JoinNode> calculateJoinNodes(List<CpNode> cpNodeList){

        List<JoinNode> joinNodeList = new ArrayList<>();
        List<CpNode> targetIsNullCpNodeList = new ArrayList<>();

        cpNodeList.stream().forEach(cpNode -> {
            List<String> sourceIds = cpNode.getInput();
            //1、遍历所有节点，某一个节点有多个sourceId，那么就该添加joinNode
            if(sourceIds != null && sourceIds.size() > 1){
                JoinNode joinNode = new JoinNode();
                String uuid = "join-" + UUID.randomUUID().toString().replace("-", "");
                joinNode.setId(uuid);
                joinNode.setTarget(cpNode.getId());
                joinNodeList.add(joinNode);

                //2、cpNode节点的sourceId指向joinNode
                cpNode.setInput(Arrays.asList(joinNode.getId()));
                cpNode.setJoined(true);

                //3、cpNode节点原来source节点的targetId全部指向joinNode
                for (String sourceId :
                        sourceIds) {
                    cpNodeList.stream().forEach(cpNode1 -> {
                        if(StringUtils.isNotBlank(sourceId) && sourceId.equals(cpNode1.getId())){
                            cpNode1.setTargetIds(Arrays.asList(joinNode.getId()));
                        }
                    });
                }

            }

            //4、超过两个cpNode节点没有targetId，那么就需要创建一个joinNode，并将这些没有targetId的cpNode节点指向该joinNode
            List<String> targetIds = cpNode.getTargetIds();
            if(targetIds == null || targetIds.size() == 0){
                targetIsNullCpNodeList.add(cpNode);
            }

        });

        //5、此时出现了超过两个cpNode节点没有targetId,创建一个joinNode，并将这个joinNode的targetId指向End
        if(targetIsNullCpNodeList != null && targetIsNullCpNodeList.size() >1){
            JoinNode joinNode = new JoinNode();
            String uuid = "join-" + UUID.randomUUID().toString().replace("-", "");
            joinNode.setId(uuid);
            joinNode.setTarget("End");
            joinNodeList.add(joinNode);

            targetIsNullCpNodeList.forEach(cpNode -> {
                cpNode.setTargetIds(new ArrayList<>(Arrays.asList(joinNode.getId())));
            });

        }

        return joinNodeList;

    }

    /**
     * 生成具有唯一开始节点的流程图
     * @param cpNodeList
     * @param forkNodeList
     */
    public static void generateOnlyStartNode(List<CpNode> cpNodeList, List<ForkNode> forkNodeList){
        List<CpNode> list = new ArrayList<>();
        cpNodeList.forEach(cpNode -> {
            if(cpNode.getInput() == null || cpNode.getInput().size() == 0){
                list.add(cpNode);
            }
        });

        if(list != null && list.size() > 1){
            ForkNode forkNode = new ForkNode();
            String uuid = "fork-" + UUID.randomUUID().toString().replace("-", "");
            forkNode.setId(uuid);
            List<String> targetIds = new ArrayList<>();
            list.forEach(cpNode -> {
                cpNode.setInput(Arrays.asList(forkNode.getId()));
                String id = cpNode.getId();
                targetIds.add(id);
                cpNode.setInput(Arrays.asList(forkNode.getId()));
            });
            forkNode.setTargetIds(targetIds);
            forkNodeList.add(forkNode);
        }
    }

    /**
     * 整理数据源，将数据源的节点作为参数放入target节点，并将target节点的sourceIds中的本数据源的id删除
     *
     * @param cpNodeList
     */
    public static void organizeDataSource(List<CpNode> cpNodeList){

        //1、遍历所有cpNode，将本节点的source节点的<id, type>添加到previousCpNodeMap中
        cpNodeList.forEach(cpNode -> {
            if(cpNode.getInput() != null && cpNode.getInput().size() > 0){
                List<String> sourceCpNodeIdList = cpNode.getInput();
                sourceCpNodeIdList.forEach(sourceCpNodeId -> {
                    cpNodeList.forEach(sourceCpNode -> {
                        if(StringUtils.isNotBlank(sourceCpNodeId) && sourceCpNodeId.equals(sourceCpNode.getId())){
                            Map<String, String> previousCpNodeMap = cpNode.getPreviousCpNodeMap();
                            if(previousCpNodeMap != null && previousCpNodeMap.size() > 0){
                                previousCpNodeMap.put(sourceCpNode.getId(), sourceCpNode.getCpType());
                            }else {
                                Map<String, String> map = new HashMap<>();
                                map.put(sourceCpNode.getId(), sourceCpNode.getCpType());
                                previousCpNodeMap = map;
                            }
                            cpNode.setPreviousCpNodeMap(previousCpNodeMap);
                        }
                    });
                });
            }
        });

        //1、记录需要删除的节点
        List<CpNode> cpNodeListNeedDeleted = new ArrayList<>();
        cpNodeList.forEach(cpNode -> {
            if(cpNode.isStart == true){
                cpNodeListNeedDeleted.add(cpNode);
            }
        });
        //2、修改数据源节点target节点的sourceIds和参数
        cpNodeListNeedDeleted.forEach(cpNodeNeedDeleted->{
            List<String> targetIds = cpNodeNeedDeleted.getTargetIds();
            if(targetIds != null && targetIds.size() > 0){
                targetIds.forEach(targetId ->{
                    cpNodeList.forEach(cpNode -> {
                        if(StringUtils.isNotBlank(cpNode.getId()) && cpNode.getId().equals(targetId)){
                           //3、将数据源节点和target节点的连接切断
                           List<String> input = cpNode.getInput();
                           if(input != null && input.size() > 0){
                               input.remove(cpNodeNeedDeleted.getId());
                               cpNode.setInput(input);
                           }
                        }
                    });
                });
            }

        });

        cpNodeList.removeAll(cpNodeListNeedDeleted);

    }





}
