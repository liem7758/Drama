package com.yihen.http;


import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebClientFactory {

    private final Map<String, WebClient> cache = new ConcurrentHashMap<>();

    /** 连接建立超时（毫秒），大 Body 上传前也需尽快连上对端 */
    @Value("${yihen.http.client.connect-timeout-ms:60000}")
    private int connectTimeoutMs;

    /**
     * 等待完整 HTTP 响应的最长时间（秒）。分镜视频任务创建会携带 Base64 图 + 厂商侧排队，
     * 过短易触发 Reactor Netty：Connection prematurely closed BEFORE response。
     */
    @Value("${yihen.http.client.response-timeout-seconds:600}")
    private long responseTimeoutSeconds;

    public WebClient getWebClient(String baseUrl) {
        return cache.computeIfAbsent(baseUrl,
                url -> {
                    HttpClient httpClient = HttpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                            .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds));
                    return WebClient.builder()
                            .baseUrl(url)
                            .clientConnector(new ReactorClientHttpConnector(httpClient))
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .build();
                });
    }

    // 获取图片http请求发送器
    public WebClient getImgWebClient() {
        return cache.computeIfAbsent("IMG",
                url -> {

                    return WebClient.builder()
                            .defaultHeader("User-Agent", "Mozilla/5.0")
                            .defaultHeader("Accept", "image/*")
                            .build();
                }
        );
    }

    // 获取视频 http 请求发送器
    public WebClient getVideoWebClient() {
        return cache.computeIfAbsent("VIDEO",
                key -> WebClient.builder()
                        .defaultHeader("User-Agent", "Mozilla/5.0")
                        .defaultHeader("Accept", "video/*")
                        .build()
        );
    }


}
