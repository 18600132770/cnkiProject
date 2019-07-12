package com.cnki.spark.model;

import lombok.Data;

/**
 * Neo4j增量导入信息javaBean
 * @Author huag
 * 2019-06-24
 */
@Data
public class Neo4jLoadBean {

    String url;
    String user;
    String password;

    String csvUrl;
    String label;
    String type;

}
