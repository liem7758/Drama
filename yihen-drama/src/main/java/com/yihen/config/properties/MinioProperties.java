package com.yihen.config.properties;


import com.yihen.constant.MinioConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * MinIO API 地址，供 {@link io.minio.MinioClient} 在容器/内网中访问（如 http://minio:9000）。
     */
    private String endPoint;
    /**
     * 写入数据库、返回给浏览器的文件 URL 前缀。不配置时与 {@link #endPoint} 相同。
     * 部署在 Docker 时需设为外网或宿主机可达地址（如 http://服务器IP:9000）。
     */
    private String publicEndPoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    public String getEndPointNormalized() {
        return trimTrailingSlash(endPoint);
    }

    public String getPublicBaseUrl() {
        if (StringUtils.hasText(publicEndPoint)) {
            return trimTrailingSlash(publicEndPoint);
        }
        return getEndPointNormalized();
    }

    public String buildPublicObjectUrl(String objectName) {
        return getPublicBaseUrl() + "/" + MinioConstant.BUCKET_NAME + "/" + objectName;
    }

    /**
     * 从库里存的完整 URL 中解析出对象 key；兼容历史数据中 http://minio:9000/...、http://127.0.0.1/... 等任意主机。
     */
    public String parseObjectKeyFromStoredUrl(String storedUrl) {
        if (!StringUtils.hasText(storedUrl)) {
            return storedUrl;
        }
        String marker = "/" + MinioConstant.BUCKET_NAME + "/";
        int i = storedUrl.indexOf(marker);
        if (i >= 0) {
            return storedUrl.substring(i + marker.length());
        }
        return storedUrl;
    }

    private static String trimTrailingSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return url == null ? "" : url.trim();
        }
        String t = url.trim();
        while (t.endsWith("/")) {
            t = t.substring(0, t.length() - 1);
        }
        return t;
    }
}
