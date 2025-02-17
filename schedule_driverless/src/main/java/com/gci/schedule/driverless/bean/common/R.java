package com.gci.schedule.driverless.bean.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口返回数据模型
 *
 * @author TheOne
 * @create 2017-11-2017/11/2 10:06
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("retCode", 0);
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("retCode", code);
        r.put("retMsg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("retMsg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
