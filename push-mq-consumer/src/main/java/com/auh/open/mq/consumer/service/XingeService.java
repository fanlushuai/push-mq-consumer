package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.dto.push.*;
import com.auh.open.mq.consumer.config.EnvConfig;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.bean.*;
import com.tencent.xinge.push.app.PushAppRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 信鸽推送服务
 * https://xg.qq.com/docs/server_api/v2/rest.html
 */
@Service
@Slf4j
public class XingeService {

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private XingeApp androidXingeApp;

    private static final String RET_CODE = "ret_code";

    private static final int XINGE_SUSSES_CODE = 0;

    private static final int XINGE_UNFOUNDTAG_CODE = 11408;

    public XingeApp getXingeApp(Platform platform) {
        if (Platform.android.equals(platform)) {
            return androidXingeApp;
        }
        log.error("目前不打算使用信鸽的ios服务");
        return null;
    }

    private Message buildAndroidMessage(BasePushDTO basePushDTO) {
        MessageAndroid messageAndroid = new MessageAndroid();

        Map<String, String> map = new HashMap<>();
        map.put("link", basePushDTO.getLink());
        map.put("pushId", basePushDTO.getPushId());
        if (basePushDTO.getCustom() != null) {
            map.putAll(basePushDTO.getCustom());
        }
        messageAndroid.setCustom_content(map);

        ClickAction action = new ClickAction();

        messageAndroid.setAction(action);
        messageAndroid.setBuilder_id(0);
//        messageAndroid.setClearable(1);
//        messageAndroid.setIcon_res();
//        messageAndroid.setIcon_type(0);
//        messageAndroid.setLights();//是否呼吸灯
        messageAndroid.setN_id(0);// 若大于0，则会覆盖先前弹出的相同id通知；若为0，展示本条通知且不影响其他通知；若为-1，将清除先前弹出的所有通知，仅展示本条通知。选填，默认为0
        messageAndroid.setRing(1);//是否响铃
//        messageAndroid.setRing_raw();
//        messageAndroid.setSmall_icon();
        messageAndroid.setVibrate(1);//是否震动
//        messageAndroid.setStyle_id();// Web端设置是否覆盖编号的通知样式，0否，1是，选填。默认1

        Message message = new Message();
        message.setTitle(basePushDTO.getTitle());
        message.setContent(basePushDTO.getContent());
        message.setAndroid(messageAndroid);
//        message.setAccept_time();
        return message;
    }

    public boolean pushByTokens(PushByTokenDTO pushByTokenDTO) {
        Message message = buildAndroidMessage(pushByTokenDTO);

        PushAppRequest pushAppRequest = new PushAppRequest();

        pushAppRequest.setAudience_type(pushByTokenDTO.getPushTokens().size() == 1 ? AudienceType.token : AudienceType.token_list);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage(message);
//        pushAppRequest.setExpire_time(pushByTokenDTO.getExpiredTime());
//        pushAppRequest.setSend_time(pushByTokenDTO.getSendTime());
        pushAppRequest.setToken_list(new ArrayList<>(pushByTokenDTO.getPushTokens()));

        org.json.JSONObject ret = getXingeApp(Platform.android).pushApp(pushAppRequest.toString());
        log.info("pushSingleDevice {} {} ret{}", pushByTokenDTO.getPushTokens(), message, ret);
        if (ret.has(RET_CODE) && ret.getInt(RET_CODE) == XINGE_SUSSES_CODE) {
            return true;
        }
        return false;
    }

    public boolean pushAll(PushAllDTO pushAllDTO) {
        Message message = buildAndroidMessage(pushAllDTO);

        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setAudience_type(AudienceType.all);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage(message);

//        pushAppRequest.setExpire_time(pushByTokenDTO.getExpiredTime());
//        pushAppRequest.setSend_time(pushByTokenDTO.getSendTime());

        org.json.JSONObject ret = getXingeApp(Platform.android).pushApp(pushAppRequest.toString());
        log.info("pushSingleDevice {} {} ret{}", pushAllDTO, message, ret);
        if (ret.has(RET_CODE) && ret.getInt(RET_CODE) == XINGE_SUSSES_CODE) {
            return true;
        }
        return false;
    }

    public boolean pushByTags(PushByTagDTO pushByTagDTO) {
        log.error("暂时用不到。不实现了");
        return false;
    }


}
