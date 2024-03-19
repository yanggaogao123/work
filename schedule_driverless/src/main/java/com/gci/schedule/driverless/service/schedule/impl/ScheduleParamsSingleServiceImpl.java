package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamShift;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsSingle;
import com.gci.schedule.driverless.common.MyException;
import com.gci.schedule.driverless.mapper.ScheduleParamsSingleMapper;
import com.gci.schedule.driverless.mapper.ScheduleTemplateDetailMapper;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsSingleService;
import com.gci.schedule.driverless.util.DateUtil;
import com.gci.schedule.driverless.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("scheduleParamsSingleService")
public class ScheduleParamsSingleServiceImpl implements ScheduleParamsSingleService {


    @Autowired
    private ScheduleParamsSingleMapper scheduleParamsSingleMapper;

    @Autowired
    private ScheduleTemplateDetailMapper scheduleTemplateDetailMapper;

    @Transactional
    @Override
    public int updateByTemplateId(Map<String, Object> params) {
        scheduleParamsSingleMapper.delByTemplateId(Integer.valueOf(params.get("templateId").toString()));
        params.put("templateId", params.get("templateId"));
        params.put("updateUser", params.get("updateUser"));
        int i = scheduleParamsSingleMapper.insert(params);

        //更新班别
        scheduleParamsSingleMapper.delShiftByTemplateId(Integer.valueOf(params.get("templateId").toString()));
        String shiftList = params.get("shiftList").toString();
        if (StringUtil.isNotBlank(shiftList)) {
            JSONArray jsonArray = JSON.parseArray(shiftList);
            List<Map> maps = jsonArray.toJavaList(Map.class);
            for (Map map : maps) {
                int shiftType = Integer.parseInt(map.get("shiftType").toString());
                String startWorkTime1 = map.get("startWorkTime1").toString();
                String endWorkTime1 = map.get("endWorkTime1").toString();
                String startWorkTime2 = map.get("startWorkTime2").toString();
                String endWorkTime2 = map.get("endWorkTime2").toString();

                ScheduleParamShift shift = new ScheduleParamShift(Long.parseLong(params.get("routeId").toString()), Long.parseLong(params.get("templateId").toString()), shiftType, 1, startWorkTime1, endWorkTime1, params.get("updateUser").toString());
                scheduleParamsSingleMapper.saveShift(shift);

                if (StringUtil.isNotBlank(startWorkTime2) && StringUtil.isNotBlank(endWorkTime2)) {
                    shift.setStartTime(startWorkTime2);
                    shift.setEndTime(endWorkTime2);
                    shift.setOrderNumber(2);
                    scheduleParamsSingleMapper.saveShift(shift);
                }

            }
        }

        return i;
    }

    @Override
    public ScheduleParamsSingle getByTemplateId(Integer templateId) {
        ScheduleParamsSingle single = scheduleParamsSingleMapper.getByTemplateId(templateId);
        if (Objects.isNull(single)) {
            throw new MyException("500", "该模板没有对应的单班车模板数据");
        }
        List<ScheduleParamShift> shiftByTemplateId = scheduleParamsSingleMapper.getShiftByTemplateId(templateId);
        single.setShifts(shiftByTemplateId);
        return single;
    }

    @Override
    public List<ScheduleParamsSingle> selectList() {
        return scheduleParamsSingleMapper.selectList();
    }

    @Override
    public List<ScheduleParamsSingle> selectByRouteId(Long routeId) {
        return scheduleParamsSingleMapper.selectByRouteId(routeId);
    }

    @Override
    public List<Integer> getShiftsTypeByRouteIdAndPlanDate(Integer routeId, Date date, Integer ptemplateId) {
        if (Objects.isNull(routeId) || Objects.isNull(date)) {
            throw new MyException("500", "请选择线路和日期");
        }
        if (Objects.isNull(ptemplateId)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            ptemplateId = scheduleTemplateDetailMapper.getTemplateIdByRouteIdAndDay(routeId, week);
            if (Objects.isNull(ptemplateId)) {
                throw new MyException("500", "该线路尚未配置排班参数");
            }
        }
        List<ScheduleParamShift> shifts = scheduleParamsSingleMapper.getShiftByTemplateId(ptemplateId);
        List<Integer> collect = shifts.stream().map(ScheduleParamShift::getShiftType).distinct().sorted().collect(Collectors.toList());
        return collect;
    }

}
