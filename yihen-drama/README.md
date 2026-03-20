# yihen-drama（后端服务）

## 技术栈
- Java 17
- Spring Boot 3
- MyBatis-Plus
- MySQL 8 / Redis / RabbitMQ
- Elasticsearch 8 + IK/pinyin
- MinIO

## 本地运行

```bash
cd yihen-drama
mvn spring-boot:run
```

默认端口：`8080`

## 依赖服务

建议使用根目录编排启动中间件：

```bash
docker compose -f ../docker-compose.infra.yml up -d --build
```

## 关键配置

配置文件：`src/main/resources/application.yml`  
支持环境变量覆盖，核心项：
- `SPRING_DATASOURCE_*`
- `SPRING_DATA_REDIS_*`
- `SPRING_RABBITMQ_*`
- `SPRING_ELASTICSEARCH_URIS`
- `MINIO_*`
  - `MINIO_END_POINT`：Java SDK 连接 MinIO（Docker 内常用 `http://minio:9000`）。
  - `MINIO_PUBLIC_END_POINT`：**可选**。写入数据库、返回给前端的文件 URL 使用此主机（如 `http://服务器公网IP:9000` 或 HTTPS 域名）。不配置时与 `MINIO_END_POINT` 相同（本地直连 MinIO 时一般无需设置）。

## API 文档

启动后访问：
- `http://localhost:8080/doc.html`

## 初始化 SQL

- 文件：`sql/init_schema.sql`
- Docker MySQL 首次启动时自动执行

