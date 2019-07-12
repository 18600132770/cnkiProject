package com.cnki.spark.entity.model;

import lombok.Data;

import java.util.List;

/**
 * fork节点对象-连接节点
 * @Author huag
 * 2019-06-26
 */
@Data
public class ForkNode {

    String id;
    List<String> targetIds;



}
