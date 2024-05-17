package com.gci.schedule.driverless.bean.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author TheOne
 * @create 2018-07-2018/7/4 15:28
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ApiResp<T> {

    public static final String SUCCESS = "success";
    public static final Integer SUCCESS_CODE = 200;

    public static final Integer FAIL_CODE = 500;

    private Integer retCode = 0;

    private String retMsg = SUCCESS;

    private T retData;

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public T getRetData() {
        return retData;
    }

    public void setRetData(T retData) {
        this.retData = retData;
    }

    public ApiResp() {
    }

    public ApiResp(Integer retCode, T retData) {
        this.retCode = retCode;
        this.retData = retData;
    }

    public ApiResp(Integer retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    /**
     * 成功响应
     *
     * @param retData
     * @param <T>
     * @return
     */
    public static <T> ApiResp success(T retData) {
        return new ApiResp(SUCCESS_CODE, retData);
    }

    /**
     * 失败响应
     *
     * @param retMsg
     * @return
     */
    public static ApiResp fail(String retMsg) {
        return new ApiResp(FAIL_CODE, retMsg);
    }
}
