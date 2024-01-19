import {
    getAction,
    deleteAction,
    putAction,
    postAction,
    httpAction,
    realPostAction
} from '@/api/manage'


//根据经纬度获取附近站点信息
const postByCoord = (params) => postAction("/xxt-api/bus/stationName/getStationNameByCoordination", params);
//根据站点名id获取线路信息
const postByStationNameId = (params) => postAction("/xxt-api/bus/route/getRouteByStationNameId", params);
//根据站点名id获取线路预测信息
const postByForecastBus = (params) => postAction("/xxt-api/bus/forecast/getStationNameForecast", params);

//获取车辆静态数据
const postStatics = (params) => postAction("/xxt-api/bus/route/getRouteStatic", params);
//获取车辆动态数据
const postDynamic = (params) => postAction("/xxt-api/bus/route/getRouteDynamic", params);

//获取车辆发车时间表
const postByDeparturePlan = (params) => postAction("/xxt-api/bus/timePlan/getByRouteAndDirection", params);

//获取车辆时间预测
const postBySubId = (params) => postAction("/xxt-api/bus/routeSub/getRouteSubForecast", params);

//根据名称获取线路信息
const postByName = (params) => postAction("/wxxt-api/system/search/getByName", params);
//根据经纬度获取最近站点
const postByNearStation = (params) => postAction("/wxxt-api/bus/routeStation/getNearStationByRoute", params);

//根据广告位获取广告图片
const postAd = (params) => postAction("/wxxt-api/system/advertise/getAd", params);
//根据点击广告上传点击次数
const actionClick = (params) => postAction("/wxxt-api/system/advertise/actionClick", params);

//获取微信jsticket接口
const getJsTicket = (params) => postAction("/wxxt-api/weixin/weixinJs/getJsTicket", params);

// 渠道标识验证接口
const verifyChannel = (params) => postAction("/wxxt-api/system/config/verifyChannel",params);

// trainTimeTable
const trainTimeTable = (params) => postAction("/wxxt-api/system/trainTimeTable/getList",params);

// flight
const flightTimeTable = (params) => postAction("/wxxt-api/system/flightTimeTable/getList",params);

// flightSearch
const flightSearch = (params) => postAction("/wxxt-api/system/flightTimeTable/findList",params);

// getAirPortCode
const getAirPortCode = (params) => postAction("/wxxt-api/system/flight/getAirPortCode",params);

//消毒点
const disinfectionList = (params) =>postAction("/wxxt-api/system/disinfection/list",params);

// 全市概况
const getIndexByArea = (params) => postAction("/wxxt-api/system/traffic/getIndexByArea",params);

// 重点区域交通指数
const getZoneIndex = (params) => postAction("/wxxt-api/system/traffic/getZoneIndex",params);

// 重点通道交通指数
const getRoadIndex = (params) => postAction('/wxxt-api/system/traffic/getRoadIndex',params);

export {
    postByCoord,
    postByStationNameId,
    postByForecastBus,
    postStatics,
    postDynamic,
    postByDeparturePlan,
    postBySubId,
    postByName,
    postByNearStation,
    postAd,
    actionClick,
    getJsTicket,
    verifyChannel,
    trainTimeTable,
    flightTimeTable,
    flightSearch,
    getAirPortCode,
    disinfectionList,
    getZoneIndex,
    getIndexByArea,
    getRoadIndex
}
