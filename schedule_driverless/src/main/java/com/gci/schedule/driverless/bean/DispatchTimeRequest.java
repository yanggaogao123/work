package com.gci.schedule.driverless.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DispatchTimeRequest {

    /**
     * 总站已调度短线时间
     */
    @JsonFormat(pattern = "yyyyMMdd HHmmss", timezone = "GMT+8")
    private List<Date> planTimeShortList;

    private Long routeId;

    private String routeName;

    private int direction;

    private List<BusDepart> busDepartList;

    private List<BusArrival> busArrivalList;

    private int maxIntervalSchedule;//排班参数最大间隔

    private int maxIntervalDispatch;//简图参数最大间隔

    private int peakMaxIntervalDispatch;//简图参数高峰最大间隔

    private int isPickingRoute;
}
