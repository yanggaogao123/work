package com.gci.schedule.driverless.bean;

import lombok.Data;

/**
 * 里程预测接收类
 */
@Data
public class Predictmileage {
    private String obuid;
    private String route_id;
    private Integer pcount;
    private Double predict_mileage;
    //车辆历史最小剩余soc
    private Integer min_soc;

    @Override
    public String toString() {
        return "Predictmileage{" +
                "obuid='" + obuid + '\'' +
                ", route_id='" + route_id + '\'' +
                ", pcount=" + pcount +
                ", predict_mileage=" + predict_mileage +
                ", min_soc=" + min_soc +
                '}';
    }
}
