# ========== 阶段 1：构建 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 写入阿里云 Maven 镜像源，避免直连 repo.maven.apache.org 超时
RUN mkdir -p /root/.m2 && printf '%s\n' \
    '<?xml version="1.0" encoding="UTF-8"?>' \
    '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>aliyunmaven</id>' \
    '      <mirrorOf>central</mirrorOf>' \
    '      <name>Aliyun Public Repository</name>' \
    '      <url>https://maven.aliyun.com/repository/public</url>' \
    '    </mirror>' \
    '  </mirrors>' \
    '</settings>' > /root/.m2/settings.xml

# 先只复制 pom，利用 Docker 层缓存：pom 不变时不会重复下载依赖
COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests

# 再复制源码并打包
COPY src ./src
RUN mvn -B clean package -DskipTests

# ========== 阶段 2：运行 ==========
FROM eclipse-temurin:17-jre

WORKDIR /app

# 时区设置为上海，避免日志时间与业务时间偏差
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 仅复制构建产物，不携带 Maven 与源码
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
