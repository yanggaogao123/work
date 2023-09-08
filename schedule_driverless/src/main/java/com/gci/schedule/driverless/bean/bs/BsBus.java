package com.gci.schedule.driverless.bean.bs;

import lombok.Data;

import java.util.Date;

@Data
public class BsBus {
    private Long busId;

    private Long version;

    private String busCode;

    private String busName;

    private String createUser;

    private Date dateCreated;

    private Long equipmentId;

    private String isActive;

    private Date lastUpdated;

    private String numberPlate;

    private String obuChipCode;

    private String obuId;

    private Long organId;

    private String phoneNumber;

    private String remark;

    private Long routeId;

    private String simChipCode;

    private String simCode;

    private Long simId;

    private String busType;

    private String busNo;

    private Short isFacilities;

    private Long routeIdNight;

    private Integer carryCapacity;

    private String auditResult;

    private Date auditTime;

    private String auditUser;

    private String busStatus;

    private String publishStatus;

}