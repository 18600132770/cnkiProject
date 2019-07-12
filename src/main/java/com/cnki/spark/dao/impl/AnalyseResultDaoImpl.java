package com.cnki.spark.dao.impl;

import com.cnki.spark.dao.IAnalyseResultDao;
import com.cnki.spark.entity.model.AnalyseResult;
import com.cnki.spark.jdbc.JDBCHelper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 分析算法计算结果保存实现类
 * @Author huag
 * 2019-07-05
 */
public class AnalyseResultDaoImpl implements IAnalyseResultDao {

    /**
     * 插入单个对象
     * @param analyseResult
     */
    @Override
    public void insert(AnalyseResult analyseResult) {
        String sql = "INSERT INTO analyseresult VALUES (?,?,?,?,?,?,?,now(),?,?)";

        List<Object[]> paramsList = new ArrayList<Object[]>();

        Object[] params = new Object[]{
                analyseResult.getAnalyseresultid(),
                analyseResult.getDataanalyseid(),
                analyseResult.getAnalyseresultname(),
                analyseResult.getNodeid(),
                analyseResult.getHfskey(),
                analyseResult.getStatus(),
                analyseResult.getCreateuser(),
//                analyseResult.getCreatetime(),
                analyseResult.getModifyuser(),
                analyseResult.getModifytime()
        };

        paramsList.add(params);

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        jdbcHelper.executeBatch(sql, paramsList);
    }

    /**
     * 批量插入算法分析结果
     * @param analyseResultList
     */
    @Override
    public void insertBatch(List<AnalyseResult> analyseResultList) {
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();

        String sql = "INSERT INTO analyseresult VALUES (?,?,?,?,?,?,?,?,?,?)";

        List<Object[]> paramsList = new ArrayList<>();

        analyseResultList.forEach(analyseResult -> {
            Object[] params = new Object[]{analyseResult.getAnalyseresultid(),
                    analyseResult.getDataanalyseid(),
                    analyseResult.getAnalyseresultname(),
                    analyseResult.getNodeid(),
                    analyseResult.getHfskey(),
                    analyseResult.getStatus(),
                    analyseResult.getCreateuser(),
                    analyseResult.getCreatetime(),
                    analyseResult.getModifyuser(),
                    analyseResult.getModifytime()
            };
            paramsList.add(params);
        });

        jdbcHelper.executeBatch(sql, paramsList);

    }

    /**
     * 根据唯一主键查找
     * @param analyseResultID
     * @return
     */
    @Override
    public AnalyseResult findById(int analyseResultID) {

        final AnalyseResult analyseResult = new AnalyseResult();

        String sql = "select * from analyseresult where AnalyseResultID = ?";
        Object[] params = new Object[]{analyseResultID};

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();
        jdbcHelper.executeQuery(sql, params, new JDBCHelper.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                if(rs.next()){
                    Integer analyseresultid = rs.getInt(1);
                    Integer dataanalyseid = rs.getInt(2);
                    String analyseresultname = rs.getString(3);
                    String nodeid = rs.getString(4);
                    String hfskey = rs.getString(5);
                    Integer status = rs.getInt(6);
                    String createuser = rs.getString(7);
                    Date createtime = rs.getDate(8);
                    String modifyuser = rs.getString(9);
                    Date modifytime = rs.getDate(10);

                    analyseResult.setAnalyseresultid(analyseresultid);
                    analyseResult.setDataanalyseid(dataanalyseid);
                    analyseResult.setAnalyseresultname(analyseresultname);
                    analyseResult.setNodeid(nodeid);
                    analyseResult.setHfskey(hfskey);
                    analyseResult.setStatus(status);
                    analyseResult.setCreateuser(createuser);
                    analyseResult.setCreatetime(createtime);
                    analyseResult.setModifyuser(modifyuser);
                    analyseResult.setModifytime(modifytime);
                }
            }
        });

        return analyseResult;
    }


}
