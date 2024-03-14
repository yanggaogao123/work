import Vue from 'vue';
import App from './App.vue';
import router from './router';
import axios from 'axios';
import Vant from 'vant';
import 'vant/lib/index.css';
import VConsole from 'vconsole';
import Antd from 'ant-design-vue';
// import zhCN from 'ant-design-vue/lib/locale/zh_CN.js';
import * as echarts from 'echarts';
Vue.prototype.$echarts = echarts;

import 'ant-design-vue/dist/antd.css';
// import { Upload } from "ant-design-vue";
// Vue.use(Upload);
Vue.use(Vant);
Vue.use(Antd);
// 设置 Ant Design Vue 组件库的全局语言为中文
// Antd.LocaleProvider({ locale: zhCN });

import { Toast } from 'vant';
Vue.use(Toast);
import { Uploader } from 'vant';
Vue.use(Uploader);
Vue.config.productionTip = false;
Vue.prototype.$axios = axios;
axios.defaults.headers.post['Content-Type'] = 'application/json';
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

function mainStr() {
  setTimeout(() => {
    if (window.signSuccess) {
      new Vue({
        router,
        render: (h) => h(App),
      }).$mount('#app');

      Vue.use(Vant);
    } else {
      mainStr();
    }
  }, 10);
}

if (process.env.NODE_ENV === 'development') {
  //   new VConsole()
}
mainStr();

router.beforeEach((to, from, next) => {
  /* 路由发生变化修改页面title */
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  next();
});
