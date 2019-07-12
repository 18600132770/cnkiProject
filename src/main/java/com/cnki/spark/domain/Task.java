package com.cnki.spark.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务
 * @Author huag
 * 2019-06-17
 */
@Data
public class Task implements Serializable {

    private long taskid;
    private String taskName;
    private String createTime;
    private String startTime;
    private String finishTime;
    private String taskType;
    private String taskStatus;
    private String taskParam;

}
