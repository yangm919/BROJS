FROM openjdk:8-jdk-alpine

WORKDIR /app

# 复制 JAR 文件
COPY yuoj-backend-gateway-0.0.1-SNAPSHOT.jar gateway.jar
COPY yuoj-backend-user-service-0.0.1-SNAPSHOT.jar user-service.jar
COPY yuoj-backend-question-service-0.0.1-SNAPSHOT.jar question-service.jar
COPY yuoj-backend-judge-service-0.0.1-SNAPSHOT.jar judge-service.jar

EXPOSE 8101 8102 8103 8104

# 使用环境变量启动
CMD ["java", "-jar", "gateway.jar"] 