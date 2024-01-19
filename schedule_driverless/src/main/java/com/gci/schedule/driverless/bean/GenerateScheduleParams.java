package com.gci.schedule.driverless.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class GenerateScheduleParams {
    private Long routeId;

    //上行配车数
    private Integer upBusNum;

    //下行配车数
    private Integer downBusNum;

    //上行首班时间 0630
    private String upFristTime;

    //上行末班时间 2230
    private String upLastTime;

    //下行首班时间 0600
    private String downFirstTime;

    //下行末班时间 2200
    private String downLastTime;

    //最小停站时长  分钟
    private Integer minParkTime;

    //最大停战时长  分钟
    private Integer maxParkTime;

    //车内容量
    private Integer passengerNum;

    private Date runDate;

    //客流参考日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date passengerData;

    private Long supportRouteId;

    //最优计划：1，预设计划：2
    private Integer planType;

    //上行单班车
    private Integer singleBusUp;

    //下行单班车
    private Integer singleBusDwon;

    //上行早半班车
    private Integer earlyHalfBusUp;

    //下行早半班车
    private Integer earlyHalfBusDown;

    //单班开始时间 1500
    private String singleBeginTime;

    //单班结束时间1900
    private String singleEndTime;

    //早半班开始时间
    private String earlyBeginTime;

    //早半班结束时间
    private String earlyEndTime;

    public Date getUpFristDate(){
        if(Objects.isNull(this.upFristTime)){
            return null;
        }
        return DateUtil.str2Date((DateUtil.date2Str(runDate,DateUtil.yyyyMMdd)+this.upFristTime),DateUtil.yyyyMMddHHmm);
    }

    public Date getUpLastDate(){
        if(Objects.isNull(this.upLastTime)){
            return null;
        }
        return DateUtil.str2Date((DateUtil.date2Str(runDate,DateUtil.yyyyMMdd)+this.upLastTime),DateUtil.yyyyMMddHHmm);
    }

    public Date getDownFirstDate(){
        if(Objects.isNull(this.downFirstTime)){
            return null;
        }
        return DateUtil.str2Date((DateUtil.date2Str(runDate,DateUtil.yyyyMMdd)+this.downFirstTime),DateUtil.yyyyMMddHHmm);
    }

    public Date getDownLastDate(){
        if(Objects.isNull(this.downLastTime)){
            return null;
        }
        return DateUtil.str2Date((DateUtil.date2Str(runDate,DateUtil.yyyyMMdd)+this.downLastTime),DateUtil.yyyyMMddHHmm);
    }
}
