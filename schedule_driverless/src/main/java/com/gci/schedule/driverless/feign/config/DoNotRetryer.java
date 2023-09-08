package com.gci.schedule.driverless.feign.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-16 4:26 下午
 * @version: v1.0
 * @Modified by:
 **/
@Slf4j
public class DoNotRetryer implements Retryer {
    @Override
    public void continueOrPropagate(RetryableException e) {
        log.error("ERROR ", e);
        throw e;
    }

    @Override
    public Retryer clone() {
        return null;
    }
}
