package com.gci.schedule.driverless.util;

/**
 * @Author: 蔡玄彬
 * @Description: 公共常量
 * @Date: 2020-07-16 19:02
 * @version: v1.0
 * @Modified by:
 **/
public class XwCommonConstant {
    /**
     * 报站经纬度
     */
    public static final String LNG="longitude";
    public static final String LAT="latitude";
    /**
     * 采集经纬度
     */
    public static final String LNGD="longituded";
    public static final String LATD="latituded";
    /**
     * 投影经纬度
     */
    public static final String LNGD_DIS="longitudedDis";
    public static final String LATD_DIS="latitudedDis";
    /**
     * 修正经纬度
     */
    public static final String LNG_FIX="longitudeFix";
    public static final String LAT_FIX="latitudeFix";

    /**
     * 经纬度拼接
     */
    public static final String LNG_LATS = "lnglats";
    /**
     * 经纬度
     */
    public static final String LON_LAT = "lonlat";
    /**
     * 路段轨迹点
     */
    public static final String ROAD_NODES="roadNodes";
    /**
     * 调度GPS转高德经度偏移量
     */
    public static final Double GPS_GD_LONGITUDE = 0.00531;
    /**
     * 调度GPS转高德纬度偏移量
     */
    public static final Double GPS_GD_LATITUDE = -0.00271;

    /**
     * 里程分母
     */
    public static final int MILEAGE_DENO = 1000;

    public static final String APP_FILE_UPLOAD_URL = "/appFile/";

    public static void main(String[] args) {
        String a ="1.11";
        Double b = Double.valueOf(a);
    }



}    