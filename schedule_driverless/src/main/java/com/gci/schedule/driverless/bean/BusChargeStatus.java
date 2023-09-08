package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.Date;

@Data
public class BusChargeStatus {
    //collection_time,charge_status,old_charge_status,soc,obuid,bus_id
    private Date collection_time;
    private String charge_status;
    private String old_charge_status;
    private String soc;
    private String obuid;
    private String bus_id;
}
