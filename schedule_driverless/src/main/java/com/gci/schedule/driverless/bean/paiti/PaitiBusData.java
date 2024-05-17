package com.gci.schedule.driverless.bean.paiti;


import com.gci.schedule.driverless.util.DateUtil;

import java.util.Date;

/**
 * @Author:
 * @Description: 派替接口参数
 * @Date:
 */
public class PaitiBusData {

    private String id;	//String	必填	计划Id
    private Integer startDirection;//	Integer	必填	发车方向
    private Integer startOrderNumber;//	Integer	必填	发车车序
    private String busName;//	String	必填	车辆名称
    private String numberPlate;//	String	必填	车牌号
    private String driverName;//	String	必填	司机姓名
    private String qualification;//	String	必填	司机资格证
    private String phoneNumber;//	String	选填	联系电话
    private String busStopCode; //	String	必填	站点编码
    private Date ckTime;  //	String	必填	签到时间	yyyy-MM-dd HH:mm:ss
    private String startTime;   //	String	必填	发车时间	HH:mm
    private Integer shiftType;   //	Integer	选填	班别	0：早半班；1：晚半班；2：中班；3：双班(中停);4：单班(中停)：5:双班

    private Long stopPlaceId;
    private String stopPlaceName;   //	String	选填	停场名称
    private String outPlaceMileage; //	String	选填	出场里程	公里




    private String routeCode;
    private Date planDate;
    private String groupKey;
    private String ckTimeStr;




    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":\"")
                .append(id).append('\"');
        sb.append(",\"startDirection\":")
                .append(startDirection);
        sb.append(",\"startOrderNumber\":")
                .append(startOrderNumber);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"numberPlate\":\"")
                .append(numberPlate).append('\"');
        sb.append(",\"driverName\":\"")
                .append(driverName).append('\"');
        sb.append(",\"qualification\":\"")
                .append(qualification).append('\"');
        sb.append(",\"phoneNumber\":\"")
                .append(phoneNumber).append('\"');
        sb.append(",\"busStopCode\":\"")
                .append(busStopCode).append('\"');
        sb.append(",\"ckTime\":\"")
                .append(DateUtil.date2Str(ckTime,DateUtil.date_sdf)).append('\"');
        sb.append(",\"startTime\":\"")
                .append(startTime).append('\"');
        sb.append(",\"shiftType\":")
                .append(shiftType);
        if (stopPlaceId != null) {
            sb.append(",\"stopPlaceId\":").append(stopPlaceId);
        }
        sb.append(",\"stopPlaceName\":\"")
                .append(stopPlaceName).append('\"');
        if (outPlaceMileage != null) {
            sb.append(",\"outPlaceMileage\":\"").append(outPlaceMileage).append('\"');
        }
        sb.append(",\"routeCode\":\"")
                .append(routeCode).append('\"');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(Integer startDirection) {
        this.startDirection = startDirection;
    }

    public Integer getStartOrderNumber() {
        return startOrderNumber;
    }

    public void setStartOrderNumber(Integer startOrderNumber) {
        this.startOrderNumber = startOrderNumber;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusStopCode() {
        return busStopCode;
    }

    public void setBusStopCode(String busStopCode) {
        this.busStopCode = busStopCode;
    }

    public Date getCkTime() {
        return ckTime;
    }

    public void setCkTime(Date ckTime) {
        this.ckTime = ckTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getShiftType() {
        return shiftType;
    }

    public void setShiftType(Integer shiftType) {
        this.shiftType = shiftType;
    }

    public String getStopPlaceName() {
        return stopPlaceName;
    }

    public void setStopPlaceName(String stopPlaceName) {
        this.stopPlaceName = stopPlaceName;
    }

    public String getOutPlaceMileage() {
        return outPlaceMileage;
    }

    public void setOutPlaceMileage(String outPlaceMileage) {
        this.outPlaceMileage = outPlaceMileage;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public Long getStopPlaceId() {
        return stopPlaceId;
    }

    public void setStopPlaceId(Long stopPlaceId) {
        this.stopPlaceId = stopPlaceId;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getCkTimeStr() {
        return ckTimeStr;
    }

    public void setCkTimeStr(String ckTimeStr) {
        this.ckTimeStr = ckTimeStr;
    }
}
