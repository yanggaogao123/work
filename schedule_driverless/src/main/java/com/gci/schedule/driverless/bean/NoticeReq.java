package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知指令
 *
 * @author liangzc
 * @date 2023年5月29日 16点44分
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeReq {
    /**
     * 线路编码
     */
    private String routeCode;

    /**
     *
     */
    private Boolean relayAble;

    /**
     * 终端id
     */
    private String obuId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 通知内容
     */
    private String mark;
}
