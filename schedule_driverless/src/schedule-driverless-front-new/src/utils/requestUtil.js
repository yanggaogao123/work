/**
      let object = {"data":{  "latitude":23.1405621824109,  "longitude":113.31941366589672,"range":500}}
      axios.post("/zuul/wxxt-api/system/search/getByGPS", JSON.stringify(object))
      .then(function(response){
        console.log(response);          
      })
      .catch(function (error) {
        // handle error
        console.log(error);
      })    
*/


import axios from 'axios'
import {
    sign,
    getSortedQueryString
} from './sign'

const instance = axios.create({
    timeout: 10000,
})

instance.defaults.headers.post['Content-Type'] = 'application/json';

instance.interceptors.request.use(function (config) {
    // Do something before request is sent
    let appid= process.env.VUE_APP_APPID
    let appkey = process.env.VUE_APP_APPKEY
    let reqtime = Date.parse(new Date())
    let reqpara = '{}'

    let headerObj = new Object()
    headerObj.appid = appid
    headerObj.reqtime = reqtime
    headerObj.reqpara = reqpara

    let signValue = null
    // post加签方式
    if (config.method == 'post') {
        let url = config.url
        config.url = url.split('?')[0]

        let data = config.data
        if (!!data) {
            if (typeof data == 'object') {
                data = JSON.stringify(data)
            }
        }
        signValue = sign(headerObj, appkey, null, data)
    }
    if (config.method == 'get') {
        // get加签
        let url = config.url
        let qs = null
        if (url.indexOf('?') != -1) {
            qs = null
        } else {
            let paraStr = url.substring(url.indexOf('?') + 1)
            qs = getSortedQueryString(paraStr)
        }
        signValue = sign(headerObj, appkey, qs, null)
    }

    // 计算签名值
    config.headers['gateway-version'] = '1.0'
    config.headers['appid'] = appid
    config.headers['reqpara'] = reqpara
    config.headers['reqtime'] = reqtime
    config.headers['sign'] = signValue

    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
})

instance.interceptors.response.use(function (response) {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    return response.data;
}, function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error);
})

export {
    instance as axios
}