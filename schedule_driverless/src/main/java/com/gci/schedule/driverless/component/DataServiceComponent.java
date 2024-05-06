package com.gci.schedule.driverless.component;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.util.HttpClientUtils;
import com.gci.schedule.driverless.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author liangzc
 * @date 2024/4/30 9:46
 **/
@Slf4j
@Component
public class DataServiceComponent {

    @Value("${bigData.service.url}")
    public String DATA_SERVICE_URL;

    /**
     * 请求大数据信息
     *
     * @param params
     * @return
     */
    public String invoke(JSONObject params) {
        try {
            log.info("大数据请求信息，请求参数:{}", params);
            JSONObject jsonObject = HttpClientUtils.httpPost2(DATA_SERVICE_URL, params);
            log.info("大数据请求信息，请求结果:{}", (Objects.isNull(jsonObject) ? null : jsonObject.toJSONString()));
            if (Objects.isNull(jsonObject)) {
                return null;
            }
            return jsonObject.toJSONString();
        } catch (Exception e) {
            log.error("大数据请求异常", e);
        }
        return null;
    }
}
