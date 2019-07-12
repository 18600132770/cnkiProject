package com.cnki.spark.dao;

import com.cnki.spark.entity.model.AnalyseResult;

import java.util.List;

/**
 * 算法分析结果记录
 * @Author huag
 * 2019-07-05
 */
public interface IAnalyseResultDao {

    /**
     * 单独插入一行数据
     * @param analyseResult
     */
    void insert(AnalyseResult analyseResult);

    /**
     * 批量插入数据分析结果
     * @param analyseResultList
     */
    void insertBatch(List<AnalyseResult> analyseResultList);

    /**
     * 根据主键查询结果
     * @param id
     * @return
     */
    AnalyseResult findById(int id);

}
