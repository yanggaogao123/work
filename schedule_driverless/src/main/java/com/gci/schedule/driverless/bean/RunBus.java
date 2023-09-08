package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.bean.redis.RunPlan;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @Author:cjc
 * @Description:
 * @Date:2019/8/20
 * @Modified by:
 */
@Data
public class RunBus {
    private Long busId;
    private String busName;
    private String busCode;
    private String obuId;
    private String numberPlate;
    private String adFlag;
    private String speed;
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date adTime;//Date
    private String collectTime;
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date realBeginTime;//Date
    private String runStatus;
    private Long routeSubId;
    private Long taskId;
    private String routeSubName;
    private String routeStationName;
    private Long stationId;
    private Long firstStationId;
    private Long firstRouteStaId;
    private Long lastStationId;
    private String serviceName;
    private String serviceType;
    private String serviceNumber;
    private Long organId;
    private String organName;
    private String organCode;
    private Long employeeId;
    private String employeeName;
    private Long routeIdBelongTo;
    private String routeNameBelongTo;
    private String qualification;
    private String gpskey;
    private Double latitude;
    private Double longitude;
    private Long routeId;
    private String tripDefineName;
    private String lastStationName;
    private String direction;
    private String ckType;
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date ckTime;//Date
    private String routeCode;
    private String stationName;
    private String stationMark;
    private Long orderNumber;
    private Integer reportType;
    private String busStopCode;
    private String videoOnline; //  有这个字段是有安装设备。0-离线 1-在线
    //新能源车信息
    private Double soc;
    //工时信息
    private Double totalDuration;
    private Double runDuration;
    private Double anchorDuration;
    private Long lastRouteStaId;
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date redisTime;//Date
    private String vin;
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date tripBeginTime;//Date
    private Long videoUpdateTime;
    private Long routeStationId;
    private Long routeIdDispatch;

    private String shiftType;

    private Boolean adjustPosition;

    private Boolean frontIsNonRun;
    private Boolean frontIsShortLine;
    private Boolean behindIsShortLine;
    private Boolean aroundIsShortLine;

    private RunPlan runPlan;

    @JSONField(serialize = false)
    public Boolean isUnRunTask() {
        return getServiceType() != null && Integer.parseInt(getServiceType()) < 0;
    }

    @JSONField(serialize = false)
    public Boolean isRunTask() {
        return getServiceType() != null && Integer.parseInt(getServiceType()) > 0;
    }

    @JSONField(serialize = false)
    public Boolean isFullLineTask() {
        return getServiceType() != null && Constant.ServiceType.FULL_LINE.equals(getServiceType());
    }

    @JSONField(serialize = false)
    public Boolean tripBeginTimeIsPastTime() {
        return getTripBeginTime() != null && getTripBeginTime().before(new Date());
    }

    @JSONField(serialize = false)
    public Boolean redisTimeOut() {
        return getRedisTime() == null
                || new Date().getTime() - getRedisTime().getTime() > 2 * 60 * 1000;
    }

    @JSONField(serialize = false)
    public String getCkName() {
        if ("1".equals(ckType)) {
            return "上班签到";
        }
        if ("2".equals(ckType)) {
            return "运营签到";
        }
        if ("3".equals(ckType)) {
            return "交接班";
        }
        if ("5".equals(ckType)) {
            return "运营签退";
        }
        if ("6".equals(ckType)) {
            return "上班签退";
        }
        return "";
    }

    @JSONField(serialize = false)
    public String getRunStatusName() {
        if (Constant.RunBusStatus.NOT_DISPATCH.equals(runStatus)) {
            return "总站未调度";
        } else if (Constant.RunBusStatus.ON_TRIP.equals(runStatus)) {
            return "途中";
        } else if (Constant.RunBusStatus.UN_RUN.equals(runStatus)) {
            return "非运营";
        } else if (Constant.RunBusStatus.DISPATCHED.equals(runStatus)) {
            return "总站已调度";
        }
        return "";
    }

    @JSONField(serialize = false)
    public Boolean isSingleShiftMiddleStop() {
        return Constant.ShiftType.SINGLE_SHIFT_MIDDLE_STOP.equals(shiftType);
    }

    @JSONField(serialize = false)
    public Boolean isDoubleShift() {
        return Constant.ShiftType.DOUBLE_SHIFT_MIDDLE_STOP.equals(shiftType) || Constant.ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP.equals(shiftType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"routeCode\":\"")
                .append(routeCode).append('\"');
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"adFlag\":\"")
                .append(adFlag).append('\"');
        sb.append(",\"adTime\":\"")
                .append(adTime).append('\"');
        sb.append(",\"runStatus\":\"")
                .append(runStatus).append('\"');
        sb.append(",\"stationId\":")
                .append(stationId);
        sb.append(",\"stationName\":\"")
                .append(stationName).append('\"');
        sb.append(",\"stationMark\":\"")
                .append(stationMark).append('\"');
        sb.append(",\"orderNumber\":")
                .append(orderNumber);
        sb.append(",\"firstStationId\":")
                .append(firstStationId);
        sb.append(",\"lastStationId\":")
                .append(lastStationId);
        sb.append(",\"lastStationName\":\"")
                .append(lastStationName).append('\"');
        sb.append(",\"lastRouteStaId\":")
                .append(lastRouteStaId);
        sb.append(",\"serviceName\":\"")
                .append(serviceName).append('\"');
        sb.append(",\"serviceType\":\"")
                .append(serviceType).append('\"');
        sb.append(",\"latitude\":")
                .append(latitude);
        sb.append(",\"longitude\":")
                .append(longitude);
        sb.append(",\"tripDefineName\":\"")
                .append(tripDefineName).append('\"');
        sb.append(",\"ckType\":\"")
                .append(ckType).append('\"');
        sb.append(",\"ckTime\":\"")
                .append(ckTime).append('\"');
        sb.append(",\"redisTime\":\"")
                .append(redisTime).append('\"');
        sb.append(",\"tripBeginTime\":\"")
                .append(DateUtil.date2Str(tripBeginTime, DateUtil.time_sdf)).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
