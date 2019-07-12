package com.cnki.spark.dao;

import com.cnki.spark.domain.Task;

/**
 * 任务管理DAO接口
 */
public interface ITaskDao {

    /**
     * 根据主键查任务
     * @param taskid
     * @return
     */
    Task findById(long taskid);

}
