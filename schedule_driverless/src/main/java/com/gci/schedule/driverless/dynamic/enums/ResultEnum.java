package com.gci.schedule.driverless.dynamic.enums;

/**
 * Created by liuchenyu on 2017/7/21.
 * Desc:枚举类型,维护自定义异常的code与msg
 */
public enum ResultEnum {
    SUCCESS("0","成功");
    private String code;
    private String msg;

    ResultEnum() {
    }

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
