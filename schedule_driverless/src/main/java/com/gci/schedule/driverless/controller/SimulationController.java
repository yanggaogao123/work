package com.gci.schedule.driverless.controller;

import com.gci.schedule.driverless.bean.AdrealInfo;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.ScheduleStatus;
import com.gci.schedule.driverless.bean.scheduleD.AdrealInfoVo;
import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessConfig;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import com.gci.schedule.driverless.service.schedule.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Controller("simulationController")
@RequestMapping("/simulation")
public class SimulationController {
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Resource
	private SimulationService simulationService;

	@Autowired
	private GenerateScheduleService generateScheduleService;


	@RequestMapping(value="/adrealInfo",method = RequestMethod.POST , produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	//进出站详情
	public R adrealInfo(HttpServletRequest request, @RequestBody Map<String, Object> json) throws ParseException{
		Long routeId = Long.valueOf(json.get("routeId").toString());
		String runDateStr = json.get("runDate").toString();
		Long supportRouteId = null;
		if(Objects.nonNull(json.get("supportRouteId"))){
			supportRouteId = Long.valueOf(json.get("supportRouteId").toString());
		}
		AdrealInfoVo vo = new AdrealInfoVo();
		List<AdrealInfo> mainList;
		List<AdrealInfo> subList;
		DyDriverlessConfig dyDriverlessConfig;
		if(Objects.nonNull(supportRouteId)){
			//常规线进出站信息
			mainList = simulationService.adrealInfo(routeId,runDateStr, ScheduleStatus.SUPPORTED_SCHEDULE.getValue());
			subList = simulationService.adrealInfo(supportRouteId,runDateStr, ScheduleStatus.SUPPORT_SCHEDULE.getValue());
			dyDriverlessConfig = generateScheduleService.getDyDriverlessConfig(routeId,supportRouteId,0);
		}else {
			//无人车进出站信息
			mainList = simulationService.adrealInfo(routeId,runDateStr, ScheduleStatus.DRIVERLESS_SCHEDULE.getValue());
			subList = simulationService.adrealInfo(routeId,runDateStr,ScheduleStatus.COMMON_SCHEDULE.getValue());
			dyDriverlessConfig = generateScheduleService.getDyDriverlessConfig(routeId,supportRouteId,1);
		}

		if(Objects.isNull(dyDriverlessConfig)){
			return R.error("排班线路配置信息不存在");
		}
		vo.setSimulationType(dyDriverlessConfig.getType());
		vo.setMainList(mainList);
		vo.setSubList(subList);
		return R.ok().put("data",vo);
	}

	@RequestMapping("/getMinPlanTime")
	@ResponseBody
	public R getMinPlanTimeByRouteIdAndPlanDate(String routeId, String planDate){
		String minPlanTime = simulationService.getMinPlanTimeByRouteIdAndPlanDate(routeId, planDate);
		return R.ok().put("minPlanTime", minPlanTime);
	}

}
