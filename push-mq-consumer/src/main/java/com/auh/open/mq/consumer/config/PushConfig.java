package com.auh.open.mq.consumer.config;

import com.tencent.xinge.XingeApp;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * 测试配置
 * APP ID 9ba7dbbf41ed3
 * SECRET KEY 3362d3acf9375457f8cd28f5e07bbd4c
 * ACCESS ID 2100340184
 * ACCESS KEY AE24Z7Y3MS1G
 */
@Configuration
public class PushConfig {

    @Value("${push.android.xg.accessId:2100340184}")
    private Long androidAccessId;

    @Value("${push.android.xg.appId:9ba7dbbf41ed3}")
    private String androidAppId;

    @Value("${push.android.xg.secretKey:3362d3acf9375457f8cd28f5e07bbd4c}")
    private String androidSecretKey;

    @Value("${push.ios.iosPush.host:http://jd.com}")
    private String iOSPushHost;

    @Autowired
    private EnvConfig envConfig;

    /**
     * android 推送用信鸽
     */
    @Bean
    public XingeApp xgAndroidApi() {
        return new XingeApp.Builder()
                .appId(androidAppId)
                .secretKey(androidSecretKey)
                .build();
    }

    /**
     * ios 推送自己实现
     */
    @Bean
    public ApnsClient apnsClient() throws IOException {
        //todo 请替换自己的证书
        //注意，配置上采用了线上的证书和推送地址。因为ios是基于token的推送。所以。只要控制别全推就是安全的。
        ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                .setClientCredentials(new ClassPathResource("cer.p12").getInputStream(), "pw")
                .build();
        return apnsClient;
    }

}
