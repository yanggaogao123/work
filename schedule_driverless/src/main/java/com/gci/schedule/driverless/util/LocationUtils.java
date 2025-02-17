package com.gci.schedule.driverless.util;

import com.gci.schedule.driverless.bean.bs.BsStation;
import com.gci.schedule.driverless.bean.common.BsData;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
@Slf4j
public class LocationUtils {

    public static Double getDistance(Long fromStationId, Long toStationId) {
        BsStation fromStation=null;
        BsStation toStation=null;
        try{
            //切换数据源apts
            DataSourceUtil.setDB("db2");
            fromStation = BsData.getStation(fromStationId);
            toStation = BsData.getStation(toStationId);
        } catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }finally {
            DataSourceUtil.clearDB();
            DataSourceUtil.setDB("db1");
        }
        if (fromStation == null || toStation == null) {
            return null;
        }
        return getDistance(fromStation.getLatitudedDis(), fromStation.getLongitudedDis(),
                toStation.getLatitudedDis(), toStation.getLongitudedDis());
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param sourceLat
     * @param sourceLon
     * @param targetLat
     * @param targetLon
     * @return
     */
    public static Double getDistance(double sourceLat, double sourceLon, double targetLat, double targetLon) {
        GlobalCoordinates source = new GlobalCoordinates(sourceLat, sourceLon);
        GlobalCoordinates target = new GlobalCoordinates(targetLat, targetLon);
        return getDistanceMeter(source, target, Ellipsoid.WGS84);
    }

    public static Double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid) {
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);
        return geoCurve.getEllipsoidalDistance();
    }

    public static void main(String[] args) {
        double distance = getDistance(23.092284, 113.280668,
                23.0816667, 113.2780556);
        System.out.println(distance);
    }

}
