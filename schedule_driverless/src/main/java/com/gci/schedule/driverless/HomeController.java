package com.gci.schedule.driverless;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    /**
     * 跳转到帮助页面
     *
     * @return
     */
    @RequestMapping(value = "/help")
    public String help() {
        return "redirect:swagger-ui.html";
    }


    @RequestMapping(value = {"/dispatchParams"})
    public String dispatchParams() {
        return "dispatchParams/index.html";
    }

    @RequestMapping(value = {"/scheduleParams"})
    public String scheduleParams() {
        return "scheduleParams/index.html";
    }

    @RequestMapping(value = {"/scheduleParams2"})
    public String scheduleParams2() {
        return "scheduleParams2/index.html";
    }

    @RequestMapping(value = {"/scheduleAnalogy"})
    public String scheduleAnalogy() {
        return "scheduleAnalogy/index2.html";
    }

    @RequestMapping(value = {"/schedulePlanParking"})
    public String schedulePlanParking() {
        return "schedulePlanParking/schedule_plan_parking.html";
    }

    @RequestMapping(value = {"/scheduleTrip"})
    public String scheduleTrip(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "scheduleTrip/index.html";
    }

    @RequestMapping(value = {"/scheduleTrip2"})
    public String scheduleTrip2(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "scheduleTrip/index2.html";
    }

    @RequestMapping(value = {"/scheduleTrip3"})
    public String scheduleTrip3(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "scheduleTrip/index3.html";
    }

    @RequestMapping(value = {"/scheduleMonthlyTrip"})
    public String scheduleMonthlyTrip(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "scheduleMonthlyTrip/index.html";
    }

    @RequestMapping(value = {"/scheduleStationPassenger"})
    public String scheduleStationPassenger() {
        return "scheduleStationPassenger/index.html";
    }

    @RequestMapping(value = {"/routeGenerate"})
    public String routeGenerate(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "routeGenerate/index.html";
    }

    @RequestMapping(value = {"/organTree"})
    public String organTree(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "routeGenerate/organTree.html";
    }

    @RequestMapping(value = {"/routeSchedulePlan"})
    public String routeSchedulePlan(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "routeSchedulePlan/index.html";
    }

    @RequestMapping(value = {"/schedulePlanParkingNew"})
    public String schedulePlanParkingNew(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "schedulePlanParkingNew/index.html";
    }

    @RequestMapping(value = {"/tripLogParking"})
    public String tripLogParking(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "tripLog/index.html";
    }

    @RequestMapping(value = "/scheduleRouteConfig")
    public String scheduleRouteConfig(ModelMap modelMap, HttpServletRequest req){
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("userType", session.getAttribute("userType"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "scheduleRouteConfig/scheduleRouteConfig.html";
    }

    @RequestMapping(value = "/paiti")
    public String paiti(ModelMap modelMap, HttpServletRequest req){
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "paiti/paiti.html";
    }

    @RequestMapping(value = {"/ruyue"})
    public String ruyue(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "ruyue/index.html";
    }

    @RequestMapping(value = "/ruyueTriplog")
    public String ruyueTriplog(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "ruyueTriplog/ruyueTriplog.html";
    }

    /** 以下四个接口为提供给云脑进行跳转的页面 */
    @RequestMapping(value = {"/scheduleParamsForYunNao"})
    public String scheduleParamsForYunNao() {
        return "yunnao/scheduleParams/index.html";
    }

    @RequestMapping(value = {"/routeGenerateForYunNao"})
    public String routeGenerateForYunNao(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "yunnao/routeGenerate/index.html";
    }

    @RequestMapping(value = {"/routeSchedulePlanForYunNao"})
    public String routeSchedulePlanForYunNao(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "yunnao/routeSchedulePlan/index.html";
    }

    @RequestMapping(value = {"/scheduleAnalogyForYunNao"})
    public String scheduleAnalogyForYunNao() {
        return "yunnao/scheduleAnalogy/index2.html";
    }

    @RequestMapping(value = {"/unRunTask"})
    public String unRunTask(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "unRunTask/index.html";
    }

    @RequestMapping(value = {"/wasteTimeReport"})
    public String wasteTimeReport(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "wasteTimeReport/wasteTimeReport.html";
    }

    @RequestMapping(value = {"/passengerFlowInCarSearch"})
    public String passengerFlowInCarSearch(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "passengerFlowInCarSearch/passengerFlowInCarSearch.html";
    }

    @RequestMapping(value = "/planGenerateLog")
    public String planGenerateLog(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        modelMap.put("organId", session.getAttribute("organId"));
        return "planGenerateLog/planGenerateLog.html";
    }

    @RequestMapping(value = {"/scheduleDriverless"})
    public String scheduleDriverless(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "scheduleDriverless/index.html";
    }

    //轮位
    //线路轮位参数设置
    @RequestMapping(value = {"/rotationRule"})
    public String rotationRule(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "rotation/rule.html";
    }

    //线路出厂模板设置
    @RequestMapping(value = {"/rotationTemplate"})
    public String rotationTemplate(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "rotation/template.html";
    }

    //线路车序期初设置
    @RequestMapping(value = {"/rotationPlanLast"})
    public String rotationPlanLast(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "rotation/planLast.html";
    }

    //线路车辆轮位管理
    @RequestMapping(value = {"/rotationPlan"})
    public String rotationPlan(ModelMap modelMap, HttpServletRequest req) {
        HttpSession session = req.getSession();
        modelMap.put("userId", session.getAttribute("userId"));
        return "rotation/plan.html";
    }

}
