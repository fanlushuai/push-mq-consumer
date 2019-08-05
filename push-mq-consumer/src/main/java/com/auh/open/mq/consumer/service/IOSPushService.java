package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.dto.push.PushDTO;
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

    /**
     * //todo 改成你自己的配置
     */
    private static final String IOS_PACKAGE = "com.xxx.xxx";

    @Autowired
    private ApnsClient apnsClient;

    public void push(PushDTO pushDTO) {
        ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody(pushDTO.getContent());
        payloadBuilder.setAlertTitle(pushDTO.getTitle());

        final String payload = payloadBuilder.buildWithDefaultMaximumLength();
        //  哥的token   "e87b523070684e5581ed5726a2f664a8bd10da9d4b6384e0a7028c8899dd58c1"
        final String token = TokenUtil.sanitizeTokenString(pushDTO.getToken());

        final SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, IOS_PACKAGE, payload);

        final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                sendNotificationFuture = apnsClient.sendNotification(pushNotification);

        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    sendNotificationFuture.get();

            if (pushNotificationResponse.isAccepted()) {
                log.info("Push notification accepted by APNs gateway.");
            } else {
                log.error("Notification rejected by the APNs gateway: {} {}", pushNotificationResponse.getRejectionReason(), pushDTO.getToken());

                if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                    log.error("the token is invalid as of {} {}", pushNotificationResponse.getTokenInvalidationTimestamp(), pushDTO.getToken());
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
