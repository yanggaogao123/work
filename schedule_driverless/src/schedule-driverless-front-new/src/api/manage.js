import {
  axios
} from '@/utils/requestUtil'

//post
export function postAction(url, parameter) {
  return axios({
    url: url,
    method: 'post',
    data: JSON.stringify({
      data: parameter,
      reqpara: {
        devtype: 7,
        versiontype:7
      }
    })
  })
}

// real post
export function realPostAction(url, parameter) {
    return axios({
      url: url,
      method: 'post',
      data: parameter
    })
  }

//post method= {post | put}
export function httpAction(url, parameter, method) {
  return axios({
    url: url,
    method: method,
    data: parameter
  })
}

//put
export function putAction(url, parameter) {
  return axios({
    url: url,
    method: 'put',
    data: parameter
  })
}

//get
export function getAction(url, parameter) {
  return axios({
    url: url,
    method: 'get',
    params: parameter
  })
}

//deleteAction
export function deleteAction(url, parameter) {
  return axios({
    url: url,
    method: 'delete',
    params: parameter
  })
}