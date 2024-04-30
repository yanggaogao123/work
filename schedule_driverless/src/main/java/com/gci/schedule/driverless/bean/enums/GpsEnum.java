package com.gci.schedule.driverless.bean.enums;

/**
 * @Author: 蔡玄彬
 * @Description: 坐标相关枚举
 * @Date: 2020-07-18 15:32
 * @version: v1.0
 * @Modified by:
 **/
public enum GpsEnum {
    GPS84_TO_BD09(1,"84转百度"),
    BD09_TO_GPS84(2,"百度转84"),
    GPS84_TO_GCJ02(3,"84转高德"),
    GCJ02_TO_GPS84(4,"高德转84"),
    BD09_TO_GCJ02(5,"百度转高德"),
    GCJ02_TO_BD09(6,"高德转百度"),

    ;
    private int code;
    private String name;

    GpsEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }
}