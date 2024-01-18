import {
    getAction,
    deleteAction,
    putAction,
    postAction,
    httpAction,
    realPostAction
} from '@/api/manage'


//获取站点信息
const postByStation = (params) => postAction("/xxt-api/industrial/id/station", params);
//获取线路信息
const postByRoute = (params) => postAction("/xxt-api/industrial/id/route", params);
//获取公交信息
const postBytBus = (params) => postAction("/xxt-api/industrial/id/bus", params);

//获取司机信息
const postByEmp = (params) => postAction("/xxt-api/industrial/id/emp", params);

export {
    postByStation,
    postByRoute,
    postBytBus,
    postByEmp,
}
