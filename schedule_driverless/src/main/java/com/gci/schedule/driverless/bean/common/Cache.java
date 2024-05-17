package com.gci.schedule.driverless.bean.common;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

@Slf4j
public class Cache {

    public static boolean refuelHint = true;

    public static boolean earlyDispatched = true;

    public static final HashSet<String> EXCEPT_SERVICE_TYPE = new HashSet<String>() {
        {
            add(Constant.ServiceType.FULL_FAST_LINE);
            add(Constant.ServiceType.SHORT_LINE);
            add(Constant.ServiceType.SHORT_FAST_LINE);
            add(Constant.ServiceType.SECTION_LINE);
            add(Constant.ServiceType.SECTION_FAST_LINE);
            add("7");
        }
    };

    public static HashSet<Long> LEAVE_PARKING_ROUTE = new HashSet<Long>() {{
        add(2270L);
    }};

    public static HashSet<Long> TRIPLOG_CONFIRM_TRIP_ROUTE = new HashSet<Long>() {{
        //20220523
        add(274L);
        //20220530
        add(427L);
    }};

    public static HashSet<Long> ZHENBAO_ALLDAY_REGULAR_ROUTE = new HashSet<Long>() {{
        add(14531L);
        add(14530L);
        //20220418
        add(6090L);
        add(10173L);
        add(2235L);
        add(491L);
        //20220428
        add(34651L);
        //20230421
        add(250L);
    }};

    public static HashSet<Long> STORE_BUS_ROUTE = new HashSet<Long>() {{
        add(145L);
        add(176L);
        //20220427
        add(202L);
        //20220921
        add(178L);
    }};

    public static HashSet<Long> SUPPORT_BROKEN_ROUTE = new HashSet<Long>() {{
        add(274L);
    }};

    public static HashSet<Long> CHANGE_BUS_ROUTE = new HashSet<Long>() {{
        add(499L);
    }};

    public static boolean shortLineHint = true;

}
