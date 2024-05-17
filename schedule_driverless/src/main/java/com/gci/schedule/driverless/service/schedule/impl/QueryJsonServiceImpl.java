package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.service.schedule.QueryJsonService;
import com.gci.schedule.driverless.util.HttpClientUtils;
import com.gci.schedule.driverless.util.OrganTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("queryJsonService")
public class QueryJsonServiceImpl implements QueryJsonService {

	@Value("${aptsGjyn.service.url}")
	private String aptsGjynService;
	@Value("${aptsBase.organTree}")
	private String organTree;

	@Override
	public String organTree(String organId) {
		String url = aptsGjynService + organTree + organId;
		String data = "";
		if (OrganTreeUtil.organTreeMap.get(organId) == null) {
			data = HttpClientUtils.httpGetstr(url);
			OrganTreeUtil.organTreeMap.put(organId, data);
		} else {
			data = OrganTreeUtil.organTreeMap.get(organId);
		}
		return data;
	}


}
