package com.yupi.yuojbackendgateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableAsync
public class YuojBackendGatewayApplication {
    public static void main(String[] args) {
        // 添加系统属性来延迟Nacos客户端启动
        System.setProperty("nacos.client.naming.push.enabled", "false");
        System.setProperty("nacos.client.naming.push.empty.service.clean", "false");
        SpringApplication.run(YuojBackendGatewayApplication.class, args);
    }
}
