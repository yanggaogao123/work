import Vue from 'vue'
import axios from 'axios'
import {
  VueAxios
} from './axios'
import {
  Toast
} from 'vant';

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // api base_url
  timeout: 6000 // 请求超时时间
})

const err = (error) => {
  if (error.response) {
    let data = error.response.data
    console.log("------异常响应------", error.response.status)
    switch (error.response.status) {
      case 403:
        Toast.fail('服务器拒绝访问');
        break
      case 500:
        Toast.fail('服务器异常，请稍后重试');
        break
      case 404:
        Toast.fail('很抱歉，资源未找到');
        break
      case 504:
        Toast.fail('网络超时');
        break
      default:
        Toast.fail(data.message);
        break
    }
  }
  return Promise.reject(error)
};

// request interceptor
service.interceptors.request.use(config => {
  if(config.method=='get'){
    config.params = {
      _t: Date.parse(new Date())/1000,
      ...config.params
    }
  }
  return config
},(error) => {
  return Promise.reject(error)
})

// response interceptor
service.interceptors.response.use((response) => {
    return response.data
  }, err)

const installer = {
  vm: {},
  install (Vue, router = {}) {
    Vue.use(VueAxios, router, service)
  }
}

export {
  installer as VueAxios,
  service as axios
}