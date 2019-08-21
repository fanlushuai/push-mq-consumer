package com.auh.open.mq.consumer.util;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.Iterator;

public class ObjectUtil {

    public static boolean isDiff(Object objA, Object objB, String... excludeKey) {
        JSONObject jsonObjectA = new JSONObject(objA);
        Iterator<String> keys = jsonObjectA.keys();
        JSONObject jsonObjectB = new JSONObject(objB);
        for (Iterator<String> it = keys; it.hasNext(); ) {
            String key = it.next();
            if (ArrayUtils.contains(excludeKey, key)) {
                continue;
            }

            if (!ObjectUtils.nullSafeEquals(jsonObjectA.get(key), jsonObjectB.get(key))) {
                return false;
            }
        }

        return true;
    }

}
