package com.gci.schedule.driverless.feign.config;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-16 3:43 下午
 * @version: v1.0
 * @Modified by:
 **/
public class Slf4jLogger extends Logger {
    private final org.slf4j.Logger logger;

    public Slf4jLogger() {
        this(Logger.class);
    }

    public Slf4jLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public Slf4jLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    protected void logRequest(String configKey, Level logLevel, Request request) {
        super.logRequest(configKey, logLevel, request);

    }

    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
    }

    protected void log(String configKey, String format, Object... args) {
        this.logger.debug(String.format(methodTag(configKey) + format, args));
    }
}