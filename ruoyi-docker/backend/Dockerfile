# 使用Eclipse Temurin JDK 8 JRE作为基础镜像（项目使用Java 1.8）
FROM eclipse-temurin:8-jre

# 设置工作目录
WORKDIR /app

# 创建文件上传目录
RUN mkdir -p /home/dreamcampus/uploadPath

# 创建配置目录
RUN mkdir -p /app/config

# 将应用的jar文件复制到工作目录
COPY jar/ruoyi-admin.jar app.jar

# 复制外部配置文件（如果存在，会覆盖jar包内的配置）
# 使用通配符时，如果文件不存在不会报错
COPY config/ /app/config/

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m"

# 暴露8080端口
EXPOSE 8080

# 定义启动命令
# 使用外部配置文件（如果存在），否则使用jar包内配置
ENTRYPOINT ["sh", "-c", "if [ -f /app/config/application-prod.yml ]; then java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -Dspring.config.location=classpath:/application.yml,classpath:/application-druid.yml,file:/app/config/application-prod.yml -jar app.jar; else java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar; fi"]