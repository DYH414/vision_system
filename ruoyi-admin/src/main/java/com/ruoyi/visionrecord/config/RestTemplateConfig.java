package com.ruoyi.visionrecord.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * RestTemplate配置类
 * 
 * @author ruoyi
 */
@Configuration
public class RestTemplateConfig {

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 使用分离的连接超时和读取超时配置
        factory.setConnectTimeout(deepSeekConfig.getConnectTimeout());
        factory.setReadTimeout(deepSeekConfig.getReadTimeout());

        // 添加代理配置支持
        if (deepSeekConfig.isProxyEnabled() && deepSeekConfig.getProxyHost() != null
                && deepSeekConfig.getProxyPort() != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(deepSeekConfig.getProxyHost(), deepSeekConfig.getProxyPort()));
            factory.setProxy(proxy);
        }

        return new RestTemplate(factory);
    }
}