package com.gci.schedule.driverless.feign.config;

import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-16 11:32 上午
 * @version: v1.0
 * @Modified by:
 **/
public class FastJsonDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.body() == null)
            return null;
        try {
            return JSONObject.parseObject(response.body().asInputStream(), type);
        } catch (IOException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw IOException.class.cast(e.getCause());
            }
            throw e;
        }
    }
}
