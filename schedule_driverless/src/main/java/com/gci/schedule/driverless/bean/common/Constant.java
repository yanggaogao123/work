package com.gci.schedule.driverless.bean.common;


import com.gci.schedule.driverless.util.DateUtil;

import java.util.Date;

public class Constant {

    public enum HintCode {
        FR_NO_SIGN("FR_NO_SIGN", "{0}|{1}{2},系统将在{3}顶位,若车不能到达请设置失班或重新挂车后同步计划!"),
        FR_NO_LEAVE_PARKING("FR_NO_LEAVE_PARKING", "{0}|{1}离{2}{3}米,{4}到{5}需要{6}分钟,请及时安排出场!"),
        NO_PLAN("NO_PLAN", "计划已发完,如有异常:请检查计划是否有挂车、线路是否发布过、是否有超过末班时间的更纸,调整后点击同步计划!"),
        SWITCH("SWITCH", "{0}发班开关已关闭,请打开开关!"),
        TAIL_DEL_NONRUNPLAN("TAIL_DEL_NONRUNPLAN", "车:{0}非营运任务已删除,请确认车:{1}是否为当天尾车!"),

        ADJUST_POSITION("ADJUST_POSITION", "车:{0}等待调位!"),
        TWO_LONG_ONE_SHORT("TWO_LONG_ONE_SHORT", "营运任务发班失败,原因:车{0}为两长一短任务,上趟全程{1}之后已有短线任务!"),
        SHORT_STOP_TO_LONG("SHORT_STOP_TO_LONG", "营运任务发班失败,原因:车{0}当前时间+停站{1},上趟{2},下趟全程最大发班预计{3}!"),
        SHORT_STOP_TOO_LONG("SHORT_STOP_TOO_LONG", "{0}跟车位发班时间{1},停站{2}大于最大停站{3},请确认是否前插,前插时间{4}!"),
        DEL_SHORT_BY_TRIPDIFF("DEL_SHORT_BY_TRIPDIFF", "{0}营运任务已删除,任务在{1}趟设置,非本趟设置!"),
        DEL_SHORT_BY_DIRECTION("DEL_SHORT_BY_DIRECTION", "{0}营运任务已删除,任务方向与当前执行任务方向一致!"),
        ERROR_CYCLE_TASK("ERROR_CYCLE_TASK", "车{0}当前站{1}无法匹配循环营运任务去程末站{2}与回程末站{3},请重新设置!"),
        DEL_AP("DEL_AP", "调位已删除,原因:车{0}设置的调位已失效,调位在{1}之前设置!"),
        HAVE_REDISPATCH("HAVE_REDISPATCH", "车{0}等待发班重打确认!"),
        TAIL2_WAIT_TAIL("TAIL2_WAIT_TAIL", "尾二车:{0}等待尾车到站再发班!"),
        TAIL_WAIT_TAIL2("TAIL_WAIT_TAIL2", "尾车等待尾二车:{0}到站再发班!"),
        TEMPLATE_ID_IS_NULL("TEMPLATE_ID_IS_NULL", "排班参数设置未配置模版日期!"),
        DISPATCHED_RE_DISPATCH("DISPATCHED_RE_DISPATCH", "到站预测发生变化,已调度车将重打:{0}!"),
        LAST_ROUND_BUS_NUM("LAST_ROUND_BUS_NUM", "车:{0}调度后,{1}尾车前(含尾车)可发全程车辆数:{2},如有错误请点击'尾轮'按钮进行设置!"),
        MILEAGE_INSUFFIC("MILEAGE_INSUFFIC", "车{0}建议预设任务{1},剩余续航{2}km,计划{3}km!"),
        NO_FORECAST("NO_FORECAST", "获取不到到站预测时间,请人工发班!获取不到的车辆有{0}"),
        TAIL_BUS_LATE("TAIL_BUS_LATE", "{0}尾车{1}预测时间{2}晚于最晚发班时间{3},请调整尾车!"),
        FALL_BACK_LATE("FALL_BACK_LATE", "车{0}预测{1},以最大间隔{2}倒推时间{3}晚于最晚发班时间{4},请调整尾车!"),
        LATEST_TRIP_CAL("LATEST_TRIP_CAL", "车{0}发班时间{1},{2}尾车{3}前平均间隔{4}、最大车距间隔{5}、均衡间隔{6};计算最早:{7}、最晚:{8}!"),
        LATEST_TRIP_SHORT_LINE("LATEST_TRIP_SHORT_LINE", "车{0}建议预设任务:{1},{2}出车当前车位未在{3}尾车{4}~{5}尾车{6}之间!"),
        SINGLE_SHIFT_MIDDLE_STOP_CONFIRM("SINGLE_SHIFT_MIDDLE_STOP_CONFIRM", "系统在12:00设置尾车，当前单班车有{0}，请确认!"),
        OVERTAKE_BUS("OVERTAKE_BUS", "{0}爬头，等待一分钟再调度!"),
        REGULAR_BUS("REGULAR_BUS", "定点时段{0}-{1},间隔:{2}!"),
        ARRIVE_STA_GO("ARRIVE_STA_GO", "到站即走,停站:{0}!"),
        ALL_DAY_REGULAR_BUS("ALL_DAY_REGULAR_BUS", "全天定点,上趟:{0},计划上趟:{1},计划:{2};{3}"),
        RULE_SHORT("RULE_SHORT", "短线发班规则{0}-{1},规则:{2},全程间隔:{3},短线停站:{4}!"),
        RULE_PARKING("RULE_PARKING", "总站堆车{0}-{1},最大停车数:{2},最小停站:{3}!"),
        STORE_BUS("STORE_BUS", "平峰囤车{0}-{1},{2}发17:00,{3}发班时间:{4},原发班时间:{5}!"),
        SUPPORT_PEAK("SUPPORT_PEAK", "{0}已设营运任务,支援{1}计划时间{2}！"),
        OVER_DURATION("OVER_DURATION", ""),
        CHARGE_CHANGE_BUS("CHARGE_CHANGE_BUS", ""),
        NEEDCHARGE_BUS_INFO("NEEDCHARGE_BUS_INFO", ""),

        SIGN_IN_NOTICE("SIGN_IN_NOTICE", "请车长{0}前在{1}运营签到，首轮计划{2}"),
        /**
         * 最大间隔设置异常提示
         */
        MAX_INTERVAL_SETTING_ERROR("MAX_INTERVAL_SETTING_ERROR", ""),

        /**
         * 复行不可提前提示
         */
        RESUME_RUN_CAN_NOT_ADVANCE("RESUME_RUN_CAN_NOT_ADVANCE", "{0}等待发班，复行时间{1}，不可提前"),

        /**
         * 车辆距离起始点
         */
        BUS_DISTANCE_CHECK("BUS_DISTANCE_CHECK", "{0}【{1}】距离{2}【{3}】米，待车辆到达起点{4}米内且运营签到触发自动发班"),

        /**
         * 尾二等尾车通知
         */
        TAIL2BUS_WAIT_TAILBUS_NOTICE("TAIL2BUS_WAIT_TAILBUS_NOTICE", "等待尾车【{0}】到站再发班，预计{1}分钟后发车，具体发班时间等待调度指令。"),
        TAILBUS_WAIT_TAIL2BUS_NOTICE("TAILBUS_WAIT_TAIL2BUS_NOTICE", "等待尾二车【{0}】到站再发班，预计{1}分钟后发车，具体发班时间等待调度指令。"),

        /**
         * 尾二等尾车可能断位通知
         */
        TAIL2BUS_WAIT_TAILBUS_2_MIN("TAIL2BUS_WAIT_TAILBUS_2_MIN", "尾二等尾车，后车到站预测计划晚点可能导致尾二断位，如需按计划发班，可以取消挂起。"),

        /**
         * 切换路线确认
         */
        CHANGE_ROUTE_CONFIRM("CHANGE_ROUTE_CONFIRM", "【{0}】发班时间{1}【{2}】，驾驶员{3}考勤，是否切换车辆到【{4}】?"),

        ;


        private final String hintCode;
        private final String hint;

        HintCode(String hintCode, String hint) {
            this.hintCode = hintCode;
            this.hint = hint;
        }

        public String getHintCode() {
            return hintCode;
        }

        public String getHint() {
            return hint;
        }
    }

    public static final class EatStatus {
        public static final Integer NOT_NEED_EAT = 0;     //总站未调度
        public static final Integer NEED_EAT = 1;     //途中
    }

    /**
     * 午餐交接班是否安排吃饭确认
     */
    public static final class EatConfirm {
        public static final Integer NOT_CONFIRM = 0;     // 未确认
        public static final Integer CONFIRM = 1;     //已确认
    }

    /**
     * 运营状态
     */
    public static final class RunBusStatus {
        public static final String NOT_DISPATCH = "0";     //总站未调度
        public static final String ON_TRIP = "1";     //途中
        public static final String UN_RUN = "2";     //非运营
        public static final String DISPATCHED = "3";     //总站已调度
    }

    /**
     * 任务类型
     */
    public static final class ServiceType {
        public static final String FULL_LINE = "1";
        public static final String FULL_FAST_LINE = "9";
        public static final String SHORT_LINE = "2";
        public static final String SHORT_FAST_LINE = "10";
        public static final String SECTION_LINE = "4";
        public static final String SECTION_FAST_LINE = "11";
        public static final String LEAVE_PARKING = "-8";
    }

    /**
     * 进出类型
     */
    public static final class AdFlag {
        public static final String ENTER = "1";
        public static final String OUT = "0";
    }

    /**
     * 运营方向
     */
    public static final class Direction {
        public static final String UP = "0";  //上行
        public static final String DOWN = "1"; //下行
        public static final String ALL = "2"; //上下行
    }

    /**
     * 计划类型
     */
    public static final class PlanType {
        public static final String FIRSTROUND = "1"; //首轮计划
        public static final String ARRIVESTATION = "0"; //到站计划
        public static final String FIXED = "2"; //固定计划
    }

    /**
     * 锁标示
     */
    public static final class SwitchFlag {
        public static final String ALL_CLOSE = "0";//全关
        public static final String ONLY_FR = "1";//只开首轮
        public static final String ONLY_AS = "2";//只开到站
        public static final String ALL_OPEN = "3";//全开
    }

    public static final class CalCulType {
        public static final String DISPATCH = "0";//用于调度
        public static final String FORECAST = "1";//用于计算
    }

    public static final class PeakType {
        public static final String MORNING_PEAK = "1";//早高峰
        public static final String EVENING_PEAK = "2";//晚高峰
    }

    public static final class RuleShortType {
        public static final String TWO_LONG_ONE_SHORT = "0";
        public static final String TWO_LONG_TWO_SHORT = "1";
        public static final String TWO_LONG_MORE_SHORT = "2";
    }

    public static final class ShiftType {
        public static final String MORNING_SHIFT = "0";//,"早班"),
        public static final String EVENING_SHIFT = "1";//,"晚班"),
        public static final String MIDDLE_SHIFT = "2";//,"中班"),
        public static final String DOUBLE_SHIFT_MIDDLE_STOP = "3";//,"双班中停"),
        public static final String SINGLE_SHIFT_MIDDLE_STOP = "4";//,"单班中停"),
        public static final String DOUBLE_SHIFT_NOT_MIDDLE_STOP = "5";//,"双班");
    }

    public static Date getPeakBeginTime(String peak) {
        String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
        if (PeakType.MORNING_PEAK.equals(peak)) {
            return new Date(DateUtil.str2Timestamp(today + " 07:00:00", "yyyy-MM-dd HH:mm:ss"));
        } else {
            return new Date(DateUtil.str2Timestamp(today + " 17:00:00", "yyyy-MM-dd HH:mm:ss"));
        }
    }

    public static Date getPeakEndTime(String peak) {
        String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
        if (PeakType.MORNING_PEAK.equals(peak)) {
            return new Date(DateUtil.str2Timestamp(today + " 09:00:00", "yyyy-MM-dd HH:mm:ss"));
        } else {
            return new Date(DateUtil.str2Timestamp(today + " 19:00:00", "yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * 是否高峰
     *
     * @param current
     * @return
     */
    public static boolean isPeak(Date current) {
        // 早高峰 || 晚高峰
        if ((!current.before(getPeakBeginTime(PeakType.MORNING_PEAK)) && current.before(getPeakEndTime(PeakType.MORNING_PEAK)))
                || (!current.before(getPeakBeginTime(PeakType.EVENING_PEAK)) && current.before(getPeakEndTime(PeakType.EVENING_PEAK)))) {
            return true;
        }
        return false;
    }
}
