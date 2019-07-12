package com.cnki.spark.utils;

import com.alibaba.fastjson.JSON;
import com.cnki.spark.conf.ConfigurationManager;
import com.cnki.spark.constant.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Map;

/**
 * RabbitMQ辅助组件
 * @Author huag
 * 2019-07-01
 */
public class RabbitMQHelper {

    private static Channel channel = null;
    private static Connection connection = null;
    private static ConnectionFactory factory = null;

    private RabbitMQHelper(){

    }

    /**
     * 单例模式创建Channel
     * @throws Exception
     */
    public static Channel getChannel() throws Exception{
        if(channel == null){
            synchronized (RabbitMQHelper.class){
                String RABBITMQ_HOST = null;
                if(ConfigurationManager.getBoolean(Constants.RABBITMQ_LOCAL) == true){
                    RABBITMQ_HOST = ConfigurationManager.getProperty(Constants.RABBITMQ_HOST_LOCAL);
                }else{
                    RABBITMQ_HOST = ConfigurationManager.getProperty(Constants.RABBITMQ_HOST);
                }
                Integer RABBITMQ_PORT = ConfigurationManager.getInteger(Constants.RABBITMQ_PORT);
                String RABBITMQ_USERNAME = ConfigurationManager.getProperty(Constants.RABBITMQ_USERNAME);
                String RABBITMQ_PASSWORD = ConfigurationManager.getProperty(Constants.RABBITMQ_PASSWORD);
                String RABBITMQ_HDFS_TO_HFS = ConfigurationManager.getProperty(Constants.RABBITMQ_HDFS_TO_HFS);
                // 创建连接工厂
                factory = new ConnectionFactory();
                //设置RabbitMQ地址
                factory.setHost(RABBITMQ_HOST);
                factory.setPort(RABBITMQ_PORT);
                factory.setUsername(RABBITMQ_USERNAME);
                factory.setPassword(RABBITMQ_PASSWORD);
                factory.setVirtualHost("/");
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.exchangeDeclare("(AMQP default)", "direct", true, false, null);
                channel.queueDeclare(RABBITMQ_HDFS_TO_HFS, true, false, false, null);
            }
        }
        return channel;
    }

    /**
     * 将map传递给消息队列RabbitMQ
     * @param map
     * @throws Exception
     */
    public static void pushMessageToRabbitMQ(Map<String, String> map) throws Exception{
        String RABBITMQ_HDFS_TO_HFS = ConfigurationManager.getProperty(Constants.RABBITMQ_HDFS_TO_HFS);
        String message = JSON.toJSONString(map);
        getChannel();
        channel.basicPublish("", RABBITMQ_HDFS_TO_HFS, null, map.toString().getBytes());
    }

    /**
     * 将string传递给消息队列RabbitMQ
     * @param string
     * @throws Exception
     */
    public static void pushMessageToRabbitMQ(String string) throws Exception{
        String RABBITMQ_HDFS_TO_HFS = ConfigurationManager.getProperty(Constants.RABBITMQ_HDFS_TO_HFS);
        getChannel();
        channel.basicPublish("", RABBITMQ_HDFS_TO_HFS, null, string.getBytes());
    }


    /**
     * 关闭channel connection
     * @throws Exception
     */
    public static void close() throws Exception{
        channel.close();
        connection.close();
    }





}
