<template>
  <a-locale-provider :locale="zhCN">
    <div id="app" v-cloak>
      <!-- <div id="nav">
      <router-link to="/">Home</router-link> |
      <router-link to="/about">About</router-link>
    </div>-->
      <router-view />
    </div>
  </a-locale-provider>
</template>

<script>
import zhCN from 'ant-design-vue/lib/locale/zh_CN.js';
import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

import { verifyChannel } from '@/api/api';
export default {
  name: 'app',
  data() {
    return { zhCN };
  },
  created() {
    // this.verify();
  },
  methods: {
    verify() {
      let that = this;
      let href = window.location.href;
      sessionStorage.setItem('href', JSON.stringify(href));
      let path = window.location.href.split('?');

      console.log(path);
      let channel = this.getUrlParam('channel');
      console.log(channel);
      verifyChannel({
        channel: channel,
      }).then(function (res) {
        console.log(res);
        if (res.retCode != 0) {
          that.$router.push({ name: 'about' });
        }
      });
    },
    getUrlParam(name) {
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)'); //构造一个含有目标参数的正则表达式对象
      var r = window.location.search.substr(1).match(reg); //匹配目标参数
      if (r != null) return decodeURI(r[2]);
      return null; //返回参数值
    },
  },
};
</script>

<style lang="less">
#app {
  width: 100%;
  height: 100%;
  min-height: 100%;
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
[v-cloak] {
  display: none;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
