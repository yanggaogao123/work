package com.gci.schedule.driverless.bean.common;

/**
 * @Author: yaozy
 * @Description:
 * @Date: Created in 14:56 2017/9/20.
 */
public class Result<T> {

    //状态码
    private String retCode;
    //请求结果状态信息(成功or失败)
    private String retMsg;
    //不显示的消息
    private String unShowMsg;
    //返回值
    private T retData;

    public Result() {
    }

    public Result(String retCode, String retMsg, T retData) {
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.retData = retData;
    }

    public Result(String retCode, String retMsg, T retData, String unShowMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.retData = retData;
        this.unShowMsg = unShowMsg;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
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

    public String getUnShowMsg() {
        return unShowMsg;
    }

    public void setUnShowMsg(String unShowMsg) {
        this.unShowMsg = unShowMsg;
    }
}
