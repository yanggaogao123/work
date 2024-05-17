package com.gci.schedule.driverless.bean.common;

import lombok.Data;

/**
 * @author TheOne
 * @create 2018-07-2018/7/4 15:28
 */
@Data
public class CodeResp<T> {

    private Integer code=0;

    private T data;
}
