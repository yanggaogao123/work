package com.gci.schedule.driverless.bean.bs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriplogSimpleDto {

    private Long triplogId;

    private Long busId;

    private String direction;

    private Long fromStationId;

    private String fromStationName;

    private Date runDate;

    private Long toStationId;

    private String toStationName;

    private Date triplogBeginTime;

    private String serviceType;

    private Date triplogEndTime;

    private Long anchorDuration;

    private String runType;

    private Long runDuration;

    private Double runMileage;

    @Override
    public String toString() {
        return "TriplogSimpleDto{" +
                "triplogId=" + triplogId +
                ", busId=" + busId +
                ", direction='" + direction + '\'' +
                ", fromStationId=" + fromStationId +
                ", fromStationName='" + fromStationName + '\'' +
                ", runDate=" + runDate +
                ", toStationId=" + toStationId +
                ", toStationName='" + toStationName + '\'' +
                ", triplogBeginTime=" + triplogBeginTime +
                ", serviceType='" + serviceType + '\'' +
                ", triplogEndTime=" + triplogEndTime +
                ", anchorDuration=" + anchorDuration +
                ", runType='" + runType + '\'' +
                ", runDuration=" + runDuration + '\'' +
                ", runMileage=" + runMileage +
                '}';
    }
}
