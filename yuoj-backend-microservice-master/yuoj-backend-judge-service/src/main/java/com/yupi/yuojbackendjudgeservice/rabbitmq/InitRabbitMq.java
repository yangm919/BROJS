package com.yupi.yuojbackendjudgeservice.rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
public class InitRabbitMq {
    public static void doInit() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            // 创建队列，随机分配一个队列名称
            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("Message queue started successfully");
        } catch (Exception e) {
            log.error("Message queue failed to start");
        }
    }
    public static void main(String[] args) {
        doInit();
    }
}
