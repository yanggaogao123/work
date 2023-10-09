package com.gci.schedule.driverless.bean.paiti;


import com.gci.schedule.driverless.util.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * @Author:
 * @Description: 派替接口参数
 * @Date:
 */
public class PaitiData<T> {

    private String routeCode;//	String	必填	线路编码
    private Date planDate;//	String	必填	计划日期	yyyy-MM-dd
    private List<T> busList;//	JsonArray	必填	计划列表


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

    public List<T> getBusList() {
        return busList;
    }

    public void setBusList(List<T> busList) {
        this.busList = busList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeCode\":\"")
                .append(routeCode).append('\"');
        sb.append(",\"planDate\":\"")
                .append(DateUtil.date2Str(planDate,DateUtil.date_sdf)).append('\"');
        sb.append(",\"busList\":")
                .append(busList);
        sb.append('}');
        return sb.toString();
    }
}
