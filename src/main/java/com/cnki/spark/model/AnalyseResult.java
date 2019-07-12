package com.cnki.spark.model;

import lombok.Data;

import java.util.Date;

/**
 * 算法分析结果
 * @Author huag
 * 2019-07-05
 */
@Data
public class AnalyseResult {
    private Integer analyseresultid;
    private Integer dataanalyseid;
    private String analyseresultname;
    private String nodeid;
    private String hfskey;
    private Integer status;
    private String createuser;
    private Date createtime;
    private String modifyuser;
    private Date modifytime;
}