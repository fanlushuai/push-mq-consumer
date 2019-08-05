package com.auh.open.mq.consumer.util;

import com.auh.open.mq.common.consts.PlatForm;
import com.auh.open.mq.common.dto.push.PushAllDTO;
import com.auh.open.mq.common.dto.push.PushByTokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TestUtil {

    public static void main(String[] args) throws IOException {
        Set set = new HashSet<>();
        set.add("e87b523070684e5581ed5726a2f664a8bd10da9d4b6384e0a7028c8899dd58c1");

        System.out.println(new ObjectMapper().writeValueAsString(
                PushByTokenDTO.builder().pushId("2322323").platform(PlatForm.IOS).content("天气不错呦").title("早上好").pushTokens(set).build()));

        System.out.println(new ObjectMapper().writeValueAsString(
                PushAllDTO.builder().pushId("2322323").platform(PlatForm.IOS).content("天气不错呦").title("下午好").build()));


    }

}
