package com.gci.schedule.driverless.bean.dto;

import lombok.Data;

/**
 * @author liangzc
 * @date 2023/8/7 16:06
 **/
@Data
public class ChangeRouteConfirmDTO {
    private String ck_type;
    private String route_id;
    private String route_name;
    private String employee_id_orig;
    private String employee_name;
    private String route_id_orig;
    private String ck_type_orig;
    private String employee_name_orig;
    private String employee_id;
    private String obuid;
    private String route_name_orig;
    private String trip_start_time;
    private String bus_id;
    private String run_status;
    private String bus_name;
}


/*
{
    "ck_type": "3",
    "route_id": "388",
    "route_name": "842路",
    "employee_id_orig": "21882",
    "employee_name": "萧志勇",
    "route_id_orig": "3790",
    "ck_type_orig": "3",
    "employee_name_orig": "萧志勇",
    "employee_id": "21882",
    "obuid": "902379",
    "route_name_orig": "979路",
    "trip_start_time": "20230801 140400",
    "bus_id": "3005885",
    "run_status": "3",
    "bus_name": "00395"
}*/
