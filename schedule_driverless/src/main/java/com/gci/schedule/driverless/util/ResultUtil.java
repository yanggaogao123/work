package com.gci.schedule.driverless.util;


import com.gci.schedule.driverless.bean.common.Result;

/**
 * @Author: yaozy
 * @Description:
 * @Date: Created in 14:53 2017/9/20.
 */
public class ResultUtil {
    //成功有返回对象
    public static Result success(Object object) {
        Result<Object> result = new Result<>();
        result.setRetCode("0");
        result.setRetMsg("成功");
        result.setRetData(object);
        return  result;
    }

    //成功没返回对象
    public static Result success() {
        return success(null);
    }

    //返回错误信息
    public static Result error(String code, String msg) {
        Result<Object> result = new Result<>();
        result.setRetMsg(msg);
        result.setRetCode(code);
        return result;
    }


    public static Result error(String code, String msg, String unShowMsg) {Result<Object> result = new Result<>();
        result.setRetMsg(msg);
        result.setRetCode(code);
        result.setUnShowMsg(unShowMsg);
        return result;
    }
}
