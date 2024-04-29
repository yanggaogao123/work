package com.gci.schedule.driverless.dynamic.exception;


import com.gci.schedule.driverless.dynamic.enums.ResultEnum;

/**
 * Created by liuchenyu on 2017/7/21.
 * Desc:自定义异常
 */
public class MyException extends RuntimeException {
    private String code;

    private String unShowMsg;

    public MyException(String message) {
        this("500", message);
    }

    public MyException(String code, String message) {
        super(message);
        this.code = code;
    }

    public MyException(String code, String message, String unShowMsg) {
        super(message);
        this.code = code;
        this.unShowMsg = unShowMsg;
    }

    //利用枚举类型来维护错误码(code)与错误信息(msg)的关系,方便管理code码
    public MyException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getUnShowMsg() {
        return unShowMsg;
    }

    public void setUnShowMsg(String unShowMsg) {
        this.unShowMsg = unShowMsg;
    }
}
