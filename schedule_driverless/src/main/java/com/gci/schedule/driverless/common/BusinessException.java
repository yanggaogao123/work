package com.gci.schedule.driverless.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常
 *
 * @author liangzc
 * @date 2023年4月18日
 */
@Slf4j
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
