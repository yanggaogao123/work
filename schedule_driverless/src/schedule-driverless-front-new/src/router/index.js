import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
  // {
  //   path: '*',
  //   name: 'home',
  //   component: Home
  // },
  {
    path: '*',
    name: 'busTravelIndex',
    component: () => import('../views/busTravelIndex.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/travelIndex',
    name: 'busTravelIndex',
    component: () => import('../views/busTravelIndex.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busList',
    name: 'busList',
    component: () => import('../views/busList.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busListTime',
    name: 'busListTime',
    component: () => import('../views/busListTime.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busListHis',
    name: 'busListHis',
    component: () => import('../views/busListHis.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busIndexH5',
    name: 'busIndexH5',
    component: () => import('../views/busIndexH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busSearchH5',
    name: 'busSearchH5',
    component: () => import('../views/busSearchH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busListH5',
    name: 'busListH5',
    component: () => import('../views/busListH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busListSelectH5',
    name: 'busListSelectH5',
    component: () => import('../views/busListSelectH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busListTimeH5',
    name: 'busListTimeH5',
    component: () => import('../views/busListTimeH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busListHisH5',
    name: 'busListHisH5',
    component: () => import('../views/busListHisH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/xxtMap',
    name: 'xxtMap',
    component: () => import('../views/xxtMap.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busStationInfoH5',
    name: 'busStationInfoH5',
    component: () => import('../views/busStationInfoH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busPlanningSearchH5',
    name: 'busPlanningSearchH5',
    component: () => import('../views/busPlanningSearchH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busPlanningShowH5',
    name: 'busPlanningShowH5',
    component: () => import('../views/busPlanningShowH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/busPlanningInfoH5',
    name: 'busPlanningInfoH5',
    component: () => import('../views/busPlanningInfoH5.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
    },
  },
  {
    path: '/flightSearch',
    name: 'flightSearch',
    component: () => import('../views/flightSearch.vue'),
    meta: {
      title: '航班查询',
    },
  },
  {
    path: '/flightSearchList',
    name: 'flightSearchList',
    component: () => import('../views/flightSearchList.vue'),
    meta: {
      title: '城市列表',
    },
  },
  {
    path: '/flightList',
    name: 'flightList',
    component: () => import('../views/flightList.vue'),
    meta: {
      title: '航班列表',
    },
  },
  {
    path: '/flightInfo',
    name: 'flightInfo',
    component: () => import('../views/flightInfo.vue'),
    meta: {
      title: '详情',
    },
  },
  {
    path: '/flightTimetable',
    name: 'flightTimetable',
    component: () => import('../views/flightTimetable.vue'),
    meta: {
      title: '航班',
    },
  },
  {
    path: '/trainTimetable',
    name: 'trainTimetable',
    component: () => import('../views/trainTimetable.vue'),
    meta: {
      title: '地铁',
    },
  },
  {
    path: '/driverTraining',
    name: 'driverTraining',
    component: () => import('../views/driverTraining.vue'),
    meta: {
      title: '学车',
    },
  },
  {
    path: '/anTaxiList',
    name: 'anTaxiList',
    component: () => import('../views/xxt_antiepidemic/anTaxiList.vue'),
    meta: {
      title: '免费消毒服务点',
    },
  },
  {
    path: '/anIndex',
    name: 'anIndex',
    component: () => import('../views/xxt_antiepidemic/anIndex.vue'),
    meta: {
      title: '防疫服务',
    },
  },
  {
    path: '/weibo',
    name: 'weibo',
    component: () => import('../views/xxt_feedback/weibo.vue'),
    meta: {
      title: '交通建言',
    },
  },
  {
    path: '/feedback',
    name: 'feedback',
    component: () => import('../views/xxt_feedback/feedback.vue'),
    meta: {
      title: '留言',
    },
  },
  {
    path: '/bikeSharing',
    name: 'bikeSharing',
    component: () => import('../views/bikeSharing.vue'),
    meta: {
      title: '共享单车',
    },
  },
  {
    path: '/ii_route',
    name: 'ii_route',
    component: () => import('../views/xxt_industrial_internet/ii_route.vue'),
    meta: {
      title: '线路',
    },
  },
  {
    path: '/ii_station',
    name: 'ii_station',
    component: () => import('../views/xxt_industrial_internet/ii_station.vue'),
    meta: {
      title: '站点',
    },
  },
  {
    path: '/ii_bus',
    name: 'ii_bus',
    component: () => import('../views/xxt_industrial_internet/ii_bus.vue'),
    meta: {
      title: '车辆',
    },
  },
  {
    path: '/ii_driver',
    name: 'ii_driver',
    component: () => import('../views/xxt_industrial_internet/ii_driver.vue'),
    meta: {
      title: '司机',
    },
  },
  {
    path: '/trafficCity',
    name: 'trafficCity',
    component: () => import('../views/xxt_traffic/trafficCity.vue'),
    meta: {
      title: '交通指数',
    },
  },
  {
    path: '/trafficArea',
    name: 'trafficArea',
    component: () => import('../views/xxt_traffic/trafficArea.vue'),
    meta: {
      title: '交通指数',
    },
  },
  {
    path: '/trafficAccess',
    name: 'trafficAccess',
    component: () => import('../views/xxt_traffic/trafficAccess.vue'),
    meta: {
      title: '交通指数',
    },
  },
  {
    path: '/testApi',
    name: 'testApi',
    component: () => import('../views/xxt_traffic/testApi.vue'),
    meta: {
      title: '测试',
    },
  },
  {
    path: '/SchedulingSimulation',
    name: 'SchedulingSimulation',
    component: () => import('../views/driverlesscars/SchedulingSimulation.vue'),
    meta: {
      title: '排班仿真',
    },
  },
  {
    path: '/SchedulingSearch',
    name: 'SchedulingSearch',
    component: () => import('../views/driverlesscars/SchedulingSearch.vue'),
    meta: {
      title: '排班查询',
    },
  },
  {
    path: '/MonitoringDispatching',
    name: 'MonitoringDispatching',
    component: () => import('../views/driverlesscars/MonitoringDispatching.vue'),
    meta: {
      title: '调度监控',
    },
  },
  {
    path: '/MonitoringComparison',
    name: 'MonitoringComparison',
    component: () => import('../views/driverlesscars/MonitoringComparison.vue'),
    meta: {
      title: '调度对比',
    },
  },
  {
    path: '/BigScreen',
    name: 'BigScreen',
    component: () => import('../views/driverlesscars/BigScreen.vue'),
    meta: {
      title: '公交混编自动驾驶高效调度平台',
    },
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue'),
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

export default router;
