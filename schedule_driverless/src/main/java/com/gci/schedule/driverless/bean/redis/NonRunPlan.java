package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-08-10 10:27
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NonRunPlan {

    private Long routeId;
    private Long busId;
    private String busName;
    private Integer serviceType;
    private Long taskId;
    @JSONField(name = "startTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    private String remark;
    private Long firstStationId;
    private Integer status;
    private String green2RedLog;
    private Long lastStationId;//充电调度用
    private Long chargeLoginfId;//充电调度用
    private Long bigdataRecordId;//充电调度用
    private Integer chargeEndSoc;//充电调度用
    private Integer chargeStartSoc;//充电调度用
    private String direction;//复行方向，充电调度用
    private String chargeStatus;//0：置换，1：充电，2：充电完成
    private Long adjustBusId;//充电调度用
    private String adjustBusName;//充电调度用
    //计划调位，0:按车的顺序发，1:按司机的顺序发
    private Integer busSort; //充电调度用
    private String type;//0:中途补电，1:总站补电

    @JSONField(serialize = false)
    public Boolean isRed() {
        return status != null && status == 1;
    }

    @JSONField(serialize = false)
    public Boolean isYellow() {
        return status != null && status == 2;
    }

    /*{
            "busId": 19234232,
            "busName": "2061",
            "serviceType": 1,                   //任务类型
            "taskId":-223424,                   //taskId
            "startTime": "2020-05-13 20:00:00", //时间
            "remark":"晚上20点补点",             //备注
            "firstStationId": 234232,           //任务开始站点ID
            "status":0                          //0:默认值 1:红色(到站下发) 2:黄色(调头后到站下发)
    }*/
}
