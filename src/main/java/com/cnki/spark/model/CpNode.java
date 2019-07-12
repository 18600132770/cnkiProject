package com.cnki.spark.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据分析流程化配置中的节点数据存放对象
 * @Author huag
 * 2019-06-17
 */
@Data
public class CpNode {

    String id;//组件id
    String cpType;//组件类型type，调用相关的服务
    String data;//组件数据
    List<String> input;//组件输入，所有指向当前节点的上一组id集合，calculate后的sourceId最多就一个
    Map<String, Object> output;//当前组件输出数据

    boolean isUploadHFS;    //是否需要上传文件

    public boolean isStart = false; //true表示该节点是数据源，文件名是id.csv
    public boolean isExecuted = false;

    List<String> targetIds; //target节点，calculate后的targetIds最多就一个
    boolean isForked;//是否已经被fork
    boolean isJoined;//是否已经被join

    String master;//连接的集群管理器 yarn local local[N] local[*]
    String mode;//client cluster
    String name;//sparkTaskName
    String jar;//spark工程jar包

    String sparkTaskName;//spark任务class名
    String opts;//spark application运行资源设置

    String file;//spark jar包路径

    Map<String, String> previousCpNodeMap;//本节点source节点的<id, type>集合
    Map<String, String> params;//输入参数

//    String successTargetId;//成功后指向的节点id
    String failTargetId;//失败后指向的节点id


}
