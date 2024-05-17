package com.gci.schedule.driverless.feign.config;

import com.alibaba.fastjson.JSONObject;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.reflect.Type;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-17 9:42 上午
 * @version: v1.0
 * @Modified by:
 **/
public class FastJsonEncoder implements Encoder {
    @Override
    public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
        try {
            requestTemplate.body(JSONObject.toJSONString(o));
        } catch (Exception e) {
            throw new EncodeException(e.getMessage(), e);
        }
    }
}
