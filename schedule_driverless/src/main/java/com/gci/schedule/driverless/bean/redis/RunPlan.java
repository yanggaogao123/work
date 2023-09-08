package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-08-20 17:26
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunPlan {

    private NonRunPlan nonrun;

    private Long routeId;
    private String direction;
    private Long busId;
    private String busName;
    private Integer serviceType;
    private Long taskId;
    private Long firstStationId;

    @JSONField(name = "planTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date planTime;

    private Long minStop;    //最小停站时间
    private Integer enterOrder; //是否按车位发班 0:否 1:是

    @JSONField(name = "tripBeginTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date tripBeginTime;

    private Integer twoLongOneShort; //是否两长一短
    private Integer cycleTask;       //是否循环任务
    private Integer returnFullLine;  //回总站是否跑全程
    private RunPlan backRunTask;     //回程任务

    private String remark;
    private String userId;
    private Integer createType;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(serialize = false)
    public Boolean isFrPlan() {
        return planTime != null;
    }

    @JSONField(serialize = false)
    public Boolean isReturnFullLine() {
        return returnFullLine != null && returnFullLine == 1;
    }

    @JSONField(serialize = false)
    public Boolean isEnterOrder() {
        return enterOrder != null && enterOrder == 1;
    }

    @JSONField(serialize = false)
    public Boolean isTwoLongOneShort() {
        return twoLongOneShort == null || twoLongOneShort == 1;
    }

    @JSONField(serialize = false)
    public Boolean isTwoLongMoreShort() {
        return twoLongOneShort != null && twoLongOneShort == 0;
    }

    @JSONField(serialize = false)
    public Boolean isArriveGo() {
        return twoLongOneShort != null && twoLongOneShort == 2;
    }

    @JSONField(serialize = false)
    public Boolean isCycleTask() {
        return cycleTask != null && cycleTask == 1;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"nonrun\":")
                .append(nonrun);
        sb.append(",\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"serviceType\":")
                .append(serviceType);
        sb.append(",\"taskId\":")
                .append(taskId);
        sb.append(",\"firstStationId\":")
                .append(firstStationId);
        sb.append(",\"planTime\":\"")
                .append(planTime).append('\"');
        sb.append(",\"minStop\":")
                .append(minStop);
        sb.append(",\"enterOrder\":")
                .append(enterOrder);
        sb.append(",\"tripBeginTime\":\"")
                .append(tripBeginTime).append('\"');
        sb.append(",\"twoLongOneShort\":")
                .append(twoLongOneShort);
        sb.append(",\"cycleTask\":")
                .append(cycleTask);
        sb.append(",\"returnFullLine\":")
                .append(returnFullLine);
        sb.append(",\"backRunTask\":")
                .append(backRunTask);
        sb.append(",\"remark\":\"")
                .append(remark).append('\"');
        sb.append(",\"userId\":\"")
                .append(userId).append('\"');
        sb.append(",\"createType\":")
                .append(createType);
        sb.append(",\"createTime\":\"")
                .append(createTime).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
