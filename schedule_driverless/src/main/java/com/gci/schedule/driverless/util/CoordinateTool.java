package com.gci.schedule.driverless.util;


import com.gci.schedule.driverless.bean.enums.GpsEnum;

/**
 * @Author: 蔡玄彬
 * @Description: 坐标转换类
 * @Date: 2020-08-18 14:37
 * @version: v1.0
 * @Modified by:
 **/
public class CoordinateTool
{


    //百度坐标系转火星坐标系 (GCJ-02) 的转换算法
    //internal static CoordinateModel ChangerCoordinateModelgcj2bd(CoordinateModel coordinateModel)
    //{

    //    double x = coordinateModel.Lon - 0.0065;
    //    double y = coordinateModel.Lat - 0.006;
    //    double x_pi = x / 180.0;
    //    double z = Math.Sqrt(x * x + y * y) - 0.00002 * Math.Sin(y * x_pi);
    //    double theta = Math.Atan2(y, x) - 0.000003 * Math.Cos(x * x_pi);

    //    double longitudeMars = z * Math.Cos(theta);
    //    double latitudeMars = z * Math.Sin(theta);

    //    return new CoordinateModel(longitudeMars, latitudeMars);
    //}

    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;

    /// <summary>
    /// GPS坐标系转高德
    /// </summary>
    /// <param name="wgLoc"></param>
    /// <returns></returns>
    public static double[] gps84_To_Gcj02(double lon, double lat)
    {
        //偏移量转换--只针对调度存量的数据
//        double[] doubles = transLonLat(lon, lat);
//        lon = doubles[0];
//        lat = doubles[1];

        //如果在国外，则默认不进行转换
        if (outOfChina(lat, lon))
        {
            return new double[]{retain6(lon), retain6(lat)};
        }
        double dLat = transformLat1(lon - 105.0,lat - 35.0);
        double dLon = transformLon1(lon - 105.0,lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        return new double[]{retain6(lon + dLon), retain6(lat + dLat)};
    }

    private static double transformLat1(double x, double y)
    {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(x > 0 ? x : -x);
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
                * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon1(double x, double y)
    {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(x > 0 ? x : -x);
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
                / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean outOfChina(double lat, double lon)
    {
        if (lon < 72.004 || lon > 137.8347) {
            return true;
        }
        if (lat < 0.8293 || lat > 55.8271){
            return true;
        }
        return false;
    }


    // 坐标整体转换方法
    public static double[] transform(double lon, double lat)
    {

        //Point localHashMap = new Point(lat, lon);
        if (outOfChina(lat, lon))
        {
            //localHashMap.put("lon", Double.valueOf(lon));
            //localHashMap.put("lat", Double.valueOf(lat));
            return new double[]{retain6(lon), retain6(lat)};
        }
        double dLat = transformLat1(lon - 105.0, lat - 35.0);
        double dLon = transformLon1(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{retain6(mgLon), retain6(mgLat)};
    }


    // 将GCJ坐标系转换为WGS坐标系方法 (高德转原始)
    public static double[] gcj02_To_Gps84(double lon, double lat)
    {
        try
        {
            double wgs_lon = lon- (transform(lon, lat)[0] - lon);
            double wgs_lat = lat - (transform(lon, lat)[1] - lat);
            return new double[]{retain6(wgs_lon), retain6(wgs_lat)};
        }
        catch(Exception e) {
            return new double[]{retain6(lon), retain6(lat)};
        }
    }

    static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    /// <summary>
    /// 中国正常坐标系GCJ02协议的坐标，转到 百度地图对应的 BD09 协议坐标
    /// </summary>
    /// <param name="lat"></param>
    /// <param name="lng"></param>
//    public static void Convert_GCJ02_To_BD09(ref double lat, ref double lng)
//    {
//
//        double x = lng, y = lat;
//
//        double z = Math.Sqrt(x * x + y * y) + 0.00002 * Math.Sin(y * x_pi);
//
//        double theta = Math.Atan2(y, x) + 0.000003 * Math.Cos(x * x_pi);
//
//        lng = z * Math.Cos(theta) + 0.0065;
//
//        lat = z * Math.Sin(theta) + 0.006;
//
//    }

    /// <summary>
    /// 百度地图对应的 BD09 协议坐标，转到 中国正常坐标系GCJ02协议的坐标
    /// </summary>
    /// <param name="lat"></param>
    /// <param name="lng"></param>
//    public static void Convert_BD09_To_GCJ02(ref double lat, ref double lng)
//    {
//
//        double x = lng - 0.0065, y = lat - 0.006;
//
//        double z = Math.Sqrt(x * x + y * y) - 0.00002 * Math.Sin(y * x_pi);
//
//        double theta = Math.Atan2(y, x) - 0.000003 * Math.Cos(x * x_pi);
//
//        lng = z * Math.Cos(theta);
//
//        lat = z * Math.Sin(theta);
//
//    }

    /**
     * 保留小数点后六位
     *
     * @param num
     * @return
     */
    private static double retain6(double num) {
        String result = String.format("%.6f", num);
        return Double.valueOf(result);
    }

    /**
     * 转换
     *
     * @param lon  经度 x
     * @param lat  纬度 y
     * @param type 转换类型
     *             1: 84 转 百度
     *             2: 百度 转 84
     *             3: 84 转 高德
     *             4: 高德 转 84
     *             5: 百度 转 高德
     *             6: 高德 转 百度
     * @return {经度,纬度} 数组
     */
    public static double[] transform(double lon, double lat, GpsEnum type) {
        switch (type.getCode()) {
            //3: 84 转 高德
            case 3:
                return gps84_To_Gcj02(lon, lat);
            //4: 高德 转 84
            case 4:
                return gcj02_To_Gps84(lon, lat);
            default:
        }
        return new double[]{lon, lat};
    }

    /**
     * 只针对调度系统的存量数据有效
     * GPS转高德经纬度偏移量
     * @param lon 经度
     * @param lat 纬度
     */
    private static double[] transLonLat(double lon, double lat){
        return new double[]{lon + XwCommonConstant.GPS_GD_LONGITUDE,lat + XwCommonConstant.GPS_GD_LATITUDE};
    }

    public static void main(String[] args) {
        //113.338662,23.134349
        double[] transform = transform(113.338662, 23.134349, GpsEnum.GCJ02_TO_GPS84);
        System.out.println(transform[0]+","+transform[1]);
    }


}