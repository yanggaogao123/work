package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class ZeroSixBusParams {
    private String obuid;

    private Boolean reply_flag;

    private String data_serial;

    private String package_size;

    private String package_type;

    private Boolean header_valid;

    private String segno;

    //指令状态(0-不同意调度，1-同意调度，2-已阅读)
    private String dispatch_flag;

    private Boolean body_valid;

    private String ip;

    private String port;

    private String redis_time;

    private String _id_;
}
