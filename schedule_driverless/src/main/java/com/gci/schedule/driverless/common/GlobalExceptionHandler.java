package com.gci.schedule.driverless.common;

import com.gci.schedule.driverless.bean.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常同意处理器
 *
 * @author liangzc
 * @date 2023年4月18日
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R handleTypeMismatchException(BusinessException exception) {
        return R.error(exception.getMessage());
    }
}
