import Vue from 'vue';
import axios from 'axios';
import store from '@/store';
import { VueAxios } from './axios';
import { Modal, notification } from 'ant-design-vue';
import { ACCESS_TOKEN, TENANT_ID } from '@/store/mutation-types';

/**
 * 【指定 axios的 baseURL】
 * 如果手工指定 baseURL: '/gci-boot'
 * 则映射后端域名，通过 vue.config.js
 * @type {*|string}
 */
// let apiBaseUrl = window._CONFIG['domianURL'] || "/gci-boot";
let apiBaseUrl = process.env.VUE_APP_DOMAIN_URL + process.env.VUE_APP_BASE_API;
console.log('apiBaseUrl= ', apiBaseUrl);
// 创建 axios 实例
const service = axios.create({
    //baseURL: '/gci-boot',
    baseURL: apiBaseUrl, // api base_url
    timeout: 9000 // 请求超时时间
});

const err = error => {
    if (error.response) {
        let data = error.response.data;
        const token = Vue.ls.get(ACCESS_TOKEN);
        console.log('------异常响应------', token);
        console.log('------异常响应------', error.response.status);
        switch (error.response.status) {
            case 403:
                notification.error({ message: '系统提示', description: '拒绝访问', duration: 4 });
                break;
            case 500:
                //notification.error({ message: '系统提示', description:'Token失效，请重新登录!',duration: 4})
                if (token && data.message.includes('Token失效')) {
                    // update-begin- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
                    // store.dispatch('Logout').then(() => {
                    //     window.location.reload()
                    // })
                    Modal.error({
                        title: '登录已过期',
                        content: '很抱歉，登录已过期，请重新登录',
                        okText: '重新登录',
                        mask: false,
                        onOk: () => {
                            store.dispatch('Logout').then(() => {
                                Vue.ls.remove(ACCESS_TOKEN);
                                // window.location.reload()
                                // 修复token过期，登录页面重复跳转的问题。
                                try {
                                    let path = window.document.location.pathname;
                                    console.log('location pathname -> ' + path);
                                    if (path != '/' && path.indexOf('/user/login') == -1) {
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    window.location.reload();
                                }
                            });
                        }
                    });
                    // update-end- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
                } else if (token && data.message.includes('禁止继续登录')) {
                    Modal.error({
                        title: '登录已过期',
                        content: '已登录会话超过限制人数，禁止继续登录',
                        okText: '重新登录',
                        mask: false,
                        onOk: () => {
                            store.dispatch('Logout').then(() => {
                                Vue.ls.remove(ACCESS_TOKEN);
                                // window.location.reload()
                                // 修复token过期，登录页面重复跳转的问题。
                                try {
                                    let path = window.document.location.pathname;
                                    console.log('location pathname -> ' + path);
                                    if (path != '/' && path.indexOf('/user/login') == -1) {
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    window.location.reload();
                                }
                            });
                        }
                    });
                } else if (token && data.message.includes('您已被踢出')) {
                    Modal.error({
                        title: '登录已过期',
                        content: '已登录会话超过限制人数，您已被踢出',
                        okText: '重新登录',
                        mask: false,
                        onOk: () => {
                            store.dispatch('Logout').then(() => {
                                Vue.ls.remove(ACCESS_TOKEN);
                                // window.location.reload()
                                // 修复token过期，登录页面重复跳转的问题。
                                try {
                                    let path = window.document.location.pathname;
                                    console.log('location pathname -> ' + path);
                                    if (path != '/' && path.indexOf('/user/login') == -1) {
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    window.location.reload();
                                }
                            });
                        }
                    });
                }
                break;
            case 404:
                notification.error({ message: '系统提示', description: '很抱歉，资源未找到!', duration: 4 });
                break;
            case 504:
                notification.error({ message: '系统提示', description: '网络超时' });
                break;
            case 401:
                notification.error({ message: '系统提示', description: '未授权，请重新登录', duration: 4 });
                if (token) {
                    store.dispatch('Logout').then(() => {
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    });
                }
                break;
            case 510:
                notification.error({
                    message: '系统提示',
                    description: '您已在其他浏览器登录，请重新登录!',
                    duration: 10
                });
                if (token) {
                    store.dispatch('Logout').then(() => {
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    });
                }
                break;
            default:
                notification.error({
                    message: '系统提示',
                    description: data.message,
                    duration: 4
                });
                break;
        }
    }
    return Promise.reject(error);
};

// request interceptor
service.interceptors.request.use(
    config => {
        const token = Vue.ls.get(ACCESS_TOKEN);
        if (token) {
            config.headers['X-Access-Token'] = token; // 让每个请求携带自定义 token 请根据实际情况自行修改
        }
        let tenantid = Vue.ls.get(TENANT_ID);
        if (!tenantid) {
            tenantid = 0;
        }
        config.headers['X-TENANT-ID'] = tenantid;
        if (config.method == 'get') {
            if (config.url.indexOf('sys/dict/getDictItems') < 0) {
                config.params = {
                    _t: Date.parse(new Date()) / 1000,
                    ...config.params
                };
            }
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// response interceptor
service.interceptors.response.use(response => {
    return response.data;
}, err);

// let apiBaseUrl = window._CONFIG['domianURL'] || "/gci-boot";
let apiBaseUrlNew = process.env.VUE_APP_DOMAIN_URL + '/schedule-driverless';
console.log('apiBaseUrl= ', apiBaseUrlNew);
// 创建 axios 实例
const serviceNew = axios.create({
    //baseURL: '/gci-boot',
    baseURL: apiBaseUrlNew, // api base_url
    timeout: 9000 // 请求超时时间
});

const errNew = error => {
    if (error.response) {
        let data = error.response.data;
        const token = Vue.ls.get(ACCESS_TOKEN);
        console.log('------异常响应------', token);
        console.log('------异常响应------', error.response.status);
        switch (error.response.status) {
            case 403:
                notification.error({ message: '系统提示', description: '拒绝访问', duration: 4 });
                break;
            case 500:
                //notification.error({ message: '系统提示', description:'Token失效，请重新登录!',duration: 4})
                if (token && data.message.includes('Token失效')) {
                    // update-begin- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
                    // store.dispatch('Logout').then(() => {
                    //     window.location.reload()
                    // })
                    Modal.error({
                        title: '登录已过期',
                        content: '很抱歉，登录已过期，请重新登录',
                        okText: '重新登录',
                        mask: false,
                        onOk: () => {
                            store.dispatch('Logout').then(() => {
                                Vue.ls.remove(ACCESS_TOKEN);
                                // window.location.reload()
                                // 修复token过期，登录页面重复跳转的问题。
                                try {
                                    let path = window.document.location.pathname;
                                    console.log('location pathname -> ' + path);
                                    if (path != '/' && path.indexOf('/user/login') == -1) {
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    window.location.reload();
                                }
                            });
                        }
                    });
                    // update-end- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
                } else if (token && data.message.includes('禁止继续登录')) {
                    Modal.error({
                        title: '登录已过期',
                        content: '已登录会话超过限制人数，禁止继续登录',
                        okText: '重新登录',
                        mask: false,
                        onOk: () => {
                            store.dispatch('Logout').then(() => {
                                Vue.ls.remove(ACCESS_TOKEN);
                                // window.location.reload()
                                // 修复token过期，登录页面重复跳转的问题。
                                try {
                                    let path = window.document.location.pathname;
                                    console.log('location pathname -> ' + path);
                                    if (path != '/' && path.indexOf('/user/login') == -1) {
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    window.location.reload();
                                }
                            });
                        }
                    });
                } else if (token && data.message.includes('您已被踢出')) {
                    Modal.error({
                        title: '登录已过期',
                        content: '已登录会话超过限制人数，您已被踢出',
                        okText: '重新登录',
                        mask: false,
                        onOk: () => {
                            store.dispatch('Logout').then(() => {
                                Vue.ls.remove(ACCESS_TOKEN);
                                // window.location.reload()
                                // 修复token过期，登录页面重复跳转的问题。
                                try {
                                    let path = window.document.location.pathname;
                                    console.log('location pathname -> ' + path);
                                    if (path != '/' && path.indexOf('/user/login') == -1) {
                                        window.location.reload();
                                    }
                                } catch (e) {
                                    window.location.reload();
                                }
                            });
                        }
                    });
                }
                break;
            case 404:
                notification.error({ message: '系统提示', description: '很抱歉，资源未找到!', duration: 4 });
                break;
            case 504:
                notification.error({ message: '系统提示', description: '网络超时' });
                break;
            case 401:
                notification.error({ message: '系统提示', description: '未授权，请重新登录', duration: 4 });
                if (token) {
                    store.dispatch('Logout').then(() => {
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    });
                }
                break;
            case 510:
                notification.error({
                    message: '系统提示',
                    description: '您已在其他浏览器登录，请重新登录!',
                    duration: 10
                });
                if (token) {
                    store.dispatch('Logout').then(() => {
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    });
                }
                break;
            default:
                notification.error({
                    message: '系统提示',
                    description: data.message,
                    duration: 4
                });
                break;
        }
    }
    return Promise.reject(error);
};

// request interceptor
serviceNew.interceptors.request.use(
    config => {
        const token = Vue.ls.get(ACCESS_TOKEN);
        if (token) {
            config.headers['X-Access-Token'] = token; // 让每个请求携带自定义 token 请根据实际情况自行修改
        }
        let tenantid = Vue.ls.get(TENANT_ID);
        if (!tenantid) {
            tenantid = 0;
        }
        config.headers['X-TENANT-ID'] = tenantid;
        if (config.method == 'get') {
            if (config.url.indexOf('sys/dict/getDictItems') < 0) {
                config.params = {
                    _t: Date.parse(new Date()) / 1000,
                    ...config.params
                };
            }
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// response interceptor
serviceNew.interceptors.response.use(response => {
    return response.data;
}, errNew);

const installer = {
    vm: {},
    install(Vue, router = {}) {
        Vue.use(VueAxios, router, service);
    }
};

export { installer as VueAxios, service as axios, serviceNew as newAxios };
