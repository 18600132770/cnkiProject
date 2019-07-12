package com.cnki.spark.dao.factory;

import com.cnki.spark.dao.IAnalyseResultDao;
import com.cnki.spark.dao.ITaskDao;
import com.cnki.spark.dao.impl.AnalyseResultDaoImpl;
import com.cnki.spark.dao.impl.TaskDaoImpl;

/**
 * Dao工厂类
 */
public class DaoFactory {

    public static ITaskDao getTaskDao(){
        return new TaskDaoImpl();
    }

    public static IAnalyseResultDao getAnalyseResukltDao(){
        return new AnalyseResultDaoImpl();
    }

}
