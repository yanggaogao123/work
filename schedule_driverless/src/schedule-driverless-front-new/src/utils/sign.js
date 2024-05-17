import crypto from 'crypto';
// import {lib} from '@/utils/libxxtenc';
// import {encode_xxt} from '@/utils/xxt';

/**
 * sha1方法
 * @param {*} data 
 */
function digestSha1(data) {
    const hash = crypto.createHash('sha1');
    hash.update(data)
    return hash.digest('hex')
}

/**
 * uri地址转Object, 输入字符串
 * @param {*} uri name=xi&age=58
 */
function parseQueryString(uri){
    var obj = {}
    if(!uri ){
        return obj
    }else{
        let paraArr = uri.split('&')
        for(let i in paraArr){
            let keyValue = paraArr[i].split('=')
            let key = keyValue[0]
            let value = keyValue[1]
            obj[key] = value
        }
        return obj  
    }
}

/**
 * uri地址预处理，排序后输出
 * @param {*} uri 
 */
function getSortedQueryString(uri){
    let map = parseQueryString(uri)
    let str = ''
    let sortedKeys = Object.keys(map).sort(function(a,b){
        if(a < b){
            return -1
        }
        if(a > b){
            return 1
        }
        return 0
    })
    for(let i in sortedKeys){
        let key = sortedKeys[i]
        let value = map[key]
        str = str+key+value
    }
    return str
}

/**
 * 
 * @param {*} header 必须包含appid, reqpara, reqtime
 * @param {*} sq是查询字符串，如name=xi&age=58
 * @param {*} sbody是请求字符串, json
 */
function sign(header, k, searchQuery, searchBody){


    let appid = header.appid
    let reqpara = header.reqpara
    let reqtime = header.reqtime
    let signValue = ''

    // 请求查询串预处理
    let qs = ''
    if(!!searchQuery){
        qs = getSortedQueryString(searchQuery)
    }

    // 相连
    let str = ''
    str += k
    str += 'appid' + appid
    if(!!searchBody){
        str += 'qbody' + searchBody        
    }
    if(!!qs){
        str += 'qs' + qs
    }
    str += 'reqpara' + reqpara
    str += 'reqtime' + reqtime
    str += k
    //console.info('加签字符串:' + str)

    // 签名
    let hash = digestSha1(str).toUpperCase();
    
    // console.info("加签1：" + hash)
    // signValue = hash.toUpperCase();
    // signValue = encode_xxt(reqpara, reqtime, qs, searchBody);
    // console.info('签名值' + signValue)

    

    signValue = encode_xxt(reqpara, reqtime + "", qs, searchBody);
    if(hash != signValue){
        // console.info("加签失败|" + hash + "|" + signValue)
    }else{
        console.info("valid request")
    }
    return hash
}

export {
    sign,    
    digestSha1,
    parseQueryString,
    getSortedQueryString
}