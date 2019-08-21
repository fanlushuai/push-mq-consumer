package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.dto.push.PushByTokenDTO;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * https://github.com/relayrides/pushy
 */
@Slf4j
@Service
public class IOSPushService {

    @Autowired
    private ApnsClient apnsClient;

    private static final String IOS_PACKAGE = "com.open.xxx";

    public void push(PushByTokenDTO pushByTokenDTO) {
        ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody(pushByTokenDTO.getContent());
        payloadBuilder.setAlertTitle(pushByTokenDTO.getTitle());
        if (pushByTokenDTO.getCustom() != null) {
            pushByTokenDTO.getCustom().forEach(payloadBuilder::addCustomProperty);
        }

        final String payload = payloadBuilder.buildWithDefaultMaximumLength();

        for (String pushToken : pushByTokenDTO.getPushTokens()) {
            push(pushToken, payload);
        }
    }

    public void push(String pushToken, String payload) {
        final String token = TokenUtil.sanitizeTokenString(pushToken);

        final SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, IOS_PACKAGE, payload);

        final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                sendNotificationFuture = apnsClient.sendNotification(pushNotification);

        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    sendNotificationFuture.get();

            if (pushNotificationResponse.isAccepted()) {
                log.info("Push notification accepted by APNs gateway.");
            } else {
                log.error("Notification rejected by the APNs gateway: {} {}", pushNotificationResponse.getRejectionReason(), pushToken);

                if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                    //todo 失效token处理
                    log.error("the token is invalid as of {} {}", pushNotificationResponse.getTokenInvalidationTimestamp(), pushToken);
                }
            }
        } catch (final ExecutionException e) {
            log.error("Failed to send push notification.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
