package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.component.DataServiceComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 大数据统用接口
 *
 * @author liangzc
 * @date 2024/4/30 9:54
 **/
@RestController
@RequestMapping("/dataService")
public class DataServiceController {

    @Autowired
    private DataServiceComponent dataServiceComponent;

    /**
     * 获取数据列表
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R list(@RequestBody JSONObject jsonObject) {
        if (Objects.isNull(jsonObject)) {
            return R.error("参数错误");
        }
        String resp = dataServiceComponent.invoke(jsonObject);
        return R.ok().put("data", resp);
    }

}
