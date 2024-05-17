package com.gci.schedule.driverless.common;

import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.common.Result;
import com.gci.schedule.driverless.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

/**
 *
 * @author liuchenyu
 * @date 2017/7/21
 * Desc:全局异常捕获
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandle {



    @ExceptionHandler(value = Exception.class)        //需要捕获的异常类型
    @ResponseBody
    public Result handle(Exception e) {
        //判断是否是自定义异常
        if (e instanceof MyException) {
            MyException myException = (MyException) e;                 //自定义异常
            return ResultUtil.error(myException.getCode(), myException.getMessage(), myException.getUnShowMsg());

        } else {
            log.error("error={}", e);                 //控制台显示错误日志
            return ResultUtil.error("-1", e.getMessage());      //系统异常
        }
    }
    /**
     * 处理不带任何注解的参数绑定校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public R handleBingException(BindException e) {
        String errorMsg = e.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> ((FieldError)objectError).getField() + ((FieldError)objectError).getDefaultMessage())
                .collect(Collectors.joining(","));
        //"errorMsg": "name不能为空,age最小不能小于18"
        return R.error(500,errorMsg);
    }

    /**
     * 处理 @RequestBody参数校验异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> objectError.getDefaultMessage())
                .findFirst().orElse("参数校验错误");
        return R.error(500, errorMsg);
    }


}
