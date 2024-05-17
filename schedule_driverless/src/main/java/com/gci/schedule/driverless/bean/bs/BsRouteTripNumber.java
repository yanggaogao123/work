package com.gci.schedule.driverless.bean.bs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BsRouteTripNumber {
    private Long routeId;

    private String routeName;

    private Long organId;

    private String organName;

    private Integer tripNumber;

    private Date updateTime;
}
