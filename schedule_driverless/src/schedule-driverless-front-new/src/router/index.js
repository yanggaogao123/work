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
    name: 'home',
    component: () => import('../views/Home.vue'),
    meta: {
      // 页面标题title
      title: '广州交通 行讯通',
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
