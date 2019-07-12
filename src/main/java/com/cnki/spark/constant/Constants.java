package com.cnki.spark.constant;

/**
 * 常量接口
 * @Author huag
 * 2019-06-17
 */
public interface Constants {

    /**
     * 项目配置相关常量
     */
    String JDBC_DRIVER = "jdbc.driver";
    String JDBC_DATASOURCE_SIZE = "jdbc.datasource.size";

    String JDBC_UL = "jdbc.url";
    String JDBC_USER = "jdbc.user";
    String JDBC_PASSWORD = "jdbc.password";

    String JDBC_URL_PROD = "jdbc.url.prod";
    String JDBC_USER_PROD = "jdbc.user.prod";
    String JDBC_PASSWORD_PROD = "jdbc.password.prod";

    String JDBC_URL_PARAMS = "jdbc.url.params";
    String JDBC_USER_PARAMS = "jdbc.user.params";
    String JDBC_PASSWORD_PARAMS = "jdbc.password.params";

    String SPARK1_CNKI_IP="saprk1.cnki.ip";

    String SPARK_LOCAL = "spark.local";
    String SPARK_LOCAL_TASKID_SESSION = "spark.local.taskid.session";
    String SPARK_LOCAL_TASKID_PAGE = "spark.local.taskid.page";
    String SPARK_LOCAL_TASKID_PRODUCT = "spark.local.taskid.product";
    String SPARK_PROJECT_JAR_HDFS_URL = "spark.project.jar.hdfs.url";

    /**
     * RabbitMQ
     */
    String RABBITMQ_HOST_LOCAL = "rabbitmq.host.local";
    String RABBITMQ_LOCAL = "rabbitmq.local";
    String RABBITMQ_HOST = "rabbitmq.host";
    String RABBITMQ_PORT = "rabbit.port";
    String RABBITMQ_USERNAME = "rabbit.username";
    String RABBITMQ_PASSWORD = "rabbit.password";
    String RABBITMQ_HDFS_TO_HFS = "rabbitmq.hdfs.to.hfs";

    /**
     * workflow spark sort 排序算法 输入参数测试
     */
    String WORKFLOW_SPARK_SORT_ARG0 = "workflow.spark.sort.arg0";
    String WORKFLOW_SPARK_SORT_ARG1 = "workflow.spark.sort.arg1";
    String WORKFLOW_SPARK_SORT_ARG2 = "workflow.spark.sort.arg2";
    String WORKFLOW_SPARK_SORT_ARG3 = "workflow.spark.sort.arg3";
    String WORKFLOW_SPARK_SORT_ARG4 = "workflow.spark.sort.arg4";

    /**
     * workflow spark filter 过滤算法 输入参数测试
     */
    String WORKFLOW_SPARK_FILTER_ARG0 = "workflow.spark.filter.arg0";
    String WORKFLOW_SPARK_FILTER_ARG1 = "workflow.spark.filter.arg1";
    String WORKFLOW_SPARK_FILTER_ARG2_1 = "workflow.spark.filter.arg2.1";
    String WORKFLOW_SPARK_FILTER_ARG2_2 = "workflow.spark.filter.arg2.2";
    String WORKFLOW_SPARK_FILTER_ARG2_3 = "workflow.spark.filter.arg2.3";
    String WORKFLOW_SPARK_FILTER_ARG3 = "workflow.spark.filter.arg3";
    String WORKFLOW_SPARK_FILTER_ARG4 = "workflow.spark.filter.arg4";

    /**
     * workflow 数据输出 输入参数设置
     */
    String WORKFLOW_SPARK_OUTPUT_ARG0 = "workflow.spark.output.arg0";
    String WORKFLOW_SPARK_OUTPUT_ARG1 = "workflow.spark.output.arg1";
    String WORKFLOW_SPARK_OUTPUT_ARG2 = "workflow.spark.output.arg2";
    String WORKFLOW_SPARK_OUTPUT_ARG3 = "workflow.spark.output.arg3";
    String WORKFLOW_SPARK_OUTPUT_ARG4 = "workflow.spark.output.arg4";


}
