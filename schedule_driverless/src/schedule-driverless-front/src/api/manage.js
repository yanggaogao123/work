import Vue from 'vue';
import { axios, newAxios } from '@/utils/request';

const api = {
    user: '/api/user',
    role: '/api/role',
    service: '/api/service',
    permission: '/api/permission',
    permissionNoPager: '/api/permission/no-pager'
};

export default api;

//post
export function postAction(url, parameter) {
    return axios({
        url: url,
        method: 'post',
        data: parameter
    });
}

//post method= {post | put}
export function httpAction(url, parameter, method) {
    return axios({
        url: url,
        method: method,
        data: parameter
    });
}

//put
export function putAction(url, parameter) {
    return axios({
        url: url,
        method: 'put',
        data: parameter
    });
}

//get
export function getAction(url, parameter) {
    return axios({
        url: url,
        method: 'get',
        params: parameter
    });
}

//deleteAction
export function deleteAction(url, parameter) {
    return axios({
        url: url,
        method: 'delete',
        params: parameter
    });
}

export function getUserList(parameter) {
    return axios({
        url: api.user,
        method: 'get',
        params: parameter
    });
}

export function getRoleList(parameter) {
    return axios({
        url: api.role,
        method: 'get',
        params: parameter
    });
}

export function getServiceList(parameter) {
    return axios({
        url: api.service,
        method: 'get',
        params: parameter
    });
}

export function getPermissions(parameter) {
    return axios({
        url: api.permissionNoPager,
        method: 'get',
        params: parameter
    });
}

// id == 0 add     post
// id != 0 update  put
export function saveService(parameter) {
    return axios({
        url: api.service,
        method: parameter.id == 0 ? 'post' : 'put',
        data: parameter
    });
}

/**
 * 下载文件 用于excel导出
 * @param url
 * @param parameter
 * @returns {*}
 */
export function downFile(url, parameter) {
    return axios({
        url: url,
        params: parameter,
        method: 'get',
        responseType: 'blob',
        timeout: 120000
    });
}

/**
 * 下载文件
 * @param url 文件路径
 * @param fileName 文件名
 * @param parameter
 * @returns {*}
 */
export function downloadFileByFileName(url, fileName, parameter) {
    return downFile(url, parameter).then(data => {
        if (!data || data.size === 0) {
            Vue.prototype['$message'].warning('文件下载失败');
            return;
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(new Blob([data]), fileName);
        } else {
            let url = window.URL.createObjectURL(new Blob([data]));
            let link = document.createElement('a');
            link.style.display = 'none';
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link); //下载完成移除元素
            window.URL.revokeObjectURL(url); //释放掉blob对象
        }
    });
}

/**
 * 文件上传 用于富文本上传图片
 * @param url
 * @param parameter
 * @returns {*}
 */
export function uploadAction(url, parameter) {
    return axios({
        url: url,
        data: parameter,
        method: 'post',
        headers: {
            'Content-Type': 'multipart/form-data' // 文件上传
        }
    });
}

/**
 * 获取文件服务访问路径
 * @param avatar
 * @param subStr
 * @returns {*}
 */
export function getFileAccessHttpUrl(avatar, subStr) {
    if (!subStr) subStr = 'http';
    if (avatar && avatar.startsWith(subStr)) {
        return avatar;
    } else {
        if (avatar && avatar.length > 0 && avatar.indexOf('[') == -1) {
            return window._CONFIG['staticDomainURL'] + '/' + avatar;
        }
    }
}

/**
 *  导出,获取异步结果
 * @param url
 * @param fileName
 * @param parameter
 * @returns {Promise<any>}
 */
export function downloadFileAwait(url, fileName, parameter) {
    return new Promise((resolve, reject) => {
        downFile(url, parameter)
            .then(data => {
                if (!data || data.size === 0) {
                    Vue.prototype['$message'].warning('文件下载失败');
                    resolve();
                    return;
                }
                //  接受后台返回的错误信息
                if (data.type === 'application/json') {
                    let reader = new FileReader();
                    reader.onload = e => {
                        let info = JSON.parse(e.target.result);
                        Vue.prototype['$message'].warning(info.message);
                    };
                    reader.readAsText(data);
                    resolve();
                    return;
                }
                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    window.navigator.msSaveBlob(new Blob([data]), fileName);
                } else {
                    let url = window.URL.createObjectURL(new Blob([data]));
                    let link = document.createElement('a');
                    link.style.display = 'none';
                    link.href = url;
                    link.setAttribute('download', fileName);
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link); //下载完成移除元素
                    window.URL.revokeObjectURL(url); //释放掉blob对象
                }
                resolve();
            })
            .catch(error => {
                console.log(error);
                reject();
            });
    });
}

export function delByIdsAction(url, ids) {
    return axios({
        url: url,
        method: 'delete',
        data: ids
    });
}

/*new part*/
//post
export function postActionNew(url, parameter) {
    return newAxios({
        url: url,
        method: 'post',
        data: parameter
    });
}

//post method= {post | put}
export function httpActionNew(url, parameter, method) {
    return newAxios({
        url: url,
        method: method,
        data: parameter
    });
}

//put
export function putActionNew(url, parameter) {
    return newAxios({
        url: url,
        method: 'put',
        data: parameter
    });
}

//get
export function getActionNew(url, parameter) {
    return newAxios({
        url: url,
        method: 'get',
        params: parameter
    });
}
