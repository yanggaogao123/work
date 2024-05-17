package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

import java.util.Map;

@Data
public class ScheduleCountVo {
    //时段班次核载人数
    private Map<String,Object> mainUploadPeopleNumMap;
    private Map<String,Object> mainDownloadPeopleNumMap;
    private Map<String,Object> subUploadPeopleNumMap;
    private Map<String,Object> subDownloadPeopleNumMap;

    //时段客流人数
    private Map<String,Object> mainUpPassengerMap;
    private Map<String,Object> mainDownPassengerMap;
    private Map<String,Object> subUpPassengerMap;
    private Map<String,Object> subDownPassengerMap;

    //时段最高车内人数
    private Map<String,Object> mainUpMaxPeopleMap;
    private Map<String,Object> mainDownMaxPeopleMap;
    private Map<String,Object> subUpMaxPeopleMap;
    private Map<String,Object> subDownMaxPeopleMap;

    //时段发车班次
    private Map<String,Object> mainUpClassesMap;
    private Map<String,Object> mainDownClassesMap;
    private Map<String,Object> subUpClassesMap;
    private Map<String,Object> subDownClassesMap;

    //时段班次满载率
    private Map<String,Object> mainUpFullPercentMap;
    private Map<String,Object> mainDownFullPercentMap;
    private Map<String,Object> subUpFullPercentMap;
    private Map<String,Object> subDownFullPercentMap;

    //时段平均间隔
    private Map<String,Object> mainUpIntervalMap;
    private Map<String,Object> mainDownIntervalMap;
    private Map<String,Object> subUpIntervalMap;
    private Map<String,Object> subDownIntervalMap;

    //时段平均周转时间
    private Map<String,Object> mainUpIntersiteMap;
    private Map<String,Object> mainDownIntersiteMap;
    private Map<String,Object> subUpIntersiteMap;
    private Map<String,Object> subDownIntersiteMap;

    //表格内容
    private Map<String,Object> mainUpWordMap;
    private Map<String,Object> mainDownWordMap;
    private Map<String,Object> subUpWordMap;
    private Map<String,Object> subDownWordMap;
}
