//获取项目地址
function getRootPath() {
    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
    var curWwwPath = window.document.location.href;
    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPaht = curWwwPath.substring(0, pos);
    //获取带"/"的项目名，如：/uimcardprj
    var projectName = pathName.substring(0, pathName.substr(10).indexOf('/') +1);
    //获取项目名和项目名下一级，如：/uimcardprj/share
    var projectName1=pathName.split("/");
    return (localhostPaht +"/"+ projectName1[1]);
//    return thisHREF = document.location.href;
}

function GetQueryString(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}

//写cookies
function setCookie(name,value,expiresTime)
{
    var exp = new Date();
    exp.setTime(exp.getTime() + expiresTime);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

//读取cookies
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}

// js获取URL中的参数
// 适应以下两种模式，来获取url参数值：
// /User/vip_card_manager/useless/219/id/18
// /User/vip_card_manager?useless=219&id=18
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    var q = window.location.pathname.substr(1).match(reg_rewrite);
    if(r != null){
        return unescape(r[2]);
    }else if(q != null){
        return unescape(q[2]);
    }else{
        return null;
    }
}
//判断环线
function isLoopLine(routeId) {
    var regex1 = /\((.+?)\)/g;   // () 小括号
    var regex2 = /\[(.+?)\]/g;   // [] 中括号
    var regex3 = /\{(.+?)\}/g;  // {} 花括号，大括号
    var regex4 = /\（(.+?)\）/g;   // （）中文括号
    var isLoopline = undefined;
    //获取首末站 start
    var firstStationNameUp, firstStationNameDown;
    var lastStationNameUp, lastStationNameDown;
    var firstTimeUp, firstTimeDown;
    var latestTimeUp, latestTimeDown;
    var baseDto = ajaxClientRest('/route/getRouteUpDownInfo/' + routeId);
    if (baseDto != null && baseDto.data != null) {
        $.each(baseDto.data, function (i) {
            if (baseDto.data[i].direction == 0) {
                firstStationNameUp = baseDto.data[i].firstStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
                lastStationNameUp = baseDto.data[i].lastStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
                firstTimeUp = baseDto.data[i].firstTime;
                latestTimeUp = baseDto.data[i].latestTime;
            } else if (baseDto.data[i].direction = 1) {
                firstStationNameDown = baseDto.data[i].firstStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
                lastStationNameDown = baseDto.data[i].lastStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
                firstTimeDown = baseDto.data[i].firstTime;
                latestTimeDown = baseDto.data[i].latestTime;
            }
        })
    }
    //获取首末站 end

    //判断环线
    isLoopline = false;
    if(firstStationNameUp && lastStationNameUp && (firstStationNameUp == lastStationNameUp)) {
        isLoopline = true;
    }
    if(firstStationNameDown && lastStationNameDown && (firstStationNameDown == lastStationNameDown)) {
        isLoopline = true;
    }


    return isLoopline;
}


//当前登录用户的sessionId
var userSessionId = getQueryString('sessionId');
var userPath = getQueryString('path');
var userParamString = getQueryString('paramString');

var ajaxClient = function(url,type,data,success){
    var result = null;
    var path=getRootPath()+url;
    $.ajax({
        url:path,
        timeout : 6000,
        type : type,
        async : false,
        // dataType:'json',
        data:data,
        success:function(json){
            if (json.retCode == '402') {
                if (checkSession()){
                    ajaxClient(url,type,data,success);
                }
            } else if (json.retCode == '401') {
                //校验失败
                alert(json.retMsg);
            } else {
                success(json);
            }
        },
        error:function(err){
            console.error(err)
        }
    });
    return result;
};


var ajaxClient2 = function(url,type,data,success){
    var result = null;
    var path=getRootPath()+url;
    $.ajax({
        url:path,
        timeout : 6000,
        type : type,
        async : false,
        // dataType:'json',
        data:JSON.stringify(data),
        contentType: "application/json",
        success:function(json){
            if (json.retCode == '402') {
                if (checkSession()){
                    ajaxClient2(url,type,data,success);
                }
            } else if (json.retCode == '401') {
                //校验失败
                alert(json.retMsg);
            } else {
                success(json);
            }
        },
        error:function(err){
            console.error(err)
        }
    });
    return result;
};

var ajaxClient3 = function (url,type,data,success,timeout,async) {
    var path=getRootPath()+url;
    $.ajax({
        url:path,
        timeout : timeout,
        type : type,
        async : async,
        data:data,
        success:function(json){
            if (json.retCode == '402') {
                if (checkSession()){
                    ajaxClient3(url,type,data,success,timeout,async);
                }
            } else if (json.retCode == '401') {
                //校验失败
                alert(json.retMsg);
            } else {
                success(json);
            }
        },
        error:function(err){
            console.error(err)
        }
    });
};


var ajaxClientRest = function(url){
    var result = null;
    $.ajax({
        url:getRootPath()+url,
        timeout : 6000,
        type: 'GET',
        async : false,
        success:function(json){
            if (json.retCode == '402') {
                if (checkSession()){
                    result = ajaxClientRest(url);
                }
            } else if (json.retCode == '401') {
                //校验失败
                alert(json.retMsg);
            } else {
                result = json;
            }
        },
        error:function(err){
            console.error(err)
        }
    });
    return result;
};

var checkSession = function(){
    var result = false;
    $.ajax({
        url:getRootPath()+'/query/getCurrentOrganId?paramString=' + userParamString + '&sessionId=' + userSessionId + '&path=' + userPath,
        timeout : 6000,
        type: 'GET',
        async : false,
        success:function(json){
            if (json.retCode == '402' || json.retCode == '401') {
                alert(json.retMsg)
            } else {
                result = true;
            }
        },
        error:function(err){
            console.info(err)
        }
    });
    return result;
};

//时分校验
function checkTime(time){
    var DATE_FORMAT = /^[0-5]?[0-9]{1}:[0-5]?[0-9]{1}$/; //"00:00"
    if(DATE_FORMAT.test(time)&&time.length==5){
        return true;
    } else {
        return false;
    }
}

//时间范围判断
function checkTimeRange(checkTime,beginTime,endTime){
    if(checkTime>beginTime&&checkTime<endTime){
        return true;
    }
    return false;
}
//前面补0
function  addPreZero(val,valLength){
    var t = (val+'').length;
    for(var i=0; i<valLength-t; i++){
        val = '0'+val;
    }
    return val;
}

//只能输大于0的整数
function onlyNumber(o) {
    if (o.value.length == 1) {
        o.value = o.value.replace(/[^1-9]/g, '');
    } else {
        o.value = o.value.replace(/\D/g, '');
    }
}
//只能输大于等于0的整数
function onlyNumberGEZero(o) {
    if (o.value.length == 1) {
        o.value = o.value.replace(/[^0-9]/g, '');
    } else {
        o.value = o.value.replace(/\D/g, '');
    }
}
//
//判断是不是一个数字 或者 一个字符串里全是数字
function isNumber (value) {
    if (value === undefined || value === null || value === '') {
        return false
    }
    if (typeof(value) === 'string') {
        //正整数
        var reNumber = /^\d+$/;
        //负整数
        var reNeNumber = /^-\d+$/;
        //正实数
        var reRealNumber1 = /^[1-9]\d*[.]\d+$/;  //非零开头
        var reRealNumber2 = /^0[.]\d+$/; //零开头
        //负实数
        var reNeRealNumber1 = /^-[1-9]\d*[.]\d+$/;  //非零开头
        var reNeRealNumber2 = /^-0[.]\d+$/; //零开头

        if (
            reNumber.test(value)
            || reNeNumber.test(value)
            || reRealNumber1.test(value)
            || reRealNumber2.test(value)
            || reNeRealNumber1.test(value)
            || reNeRealNumber2.test(value)
        ) {
            return true
        }
        else {
            return false
        }
    }
    else if (typeof(value) === 'number') {
        return true
    }
    else {
        return false
    }
}
//js 对象转url参数
var urlEncode = function(param, key, encode) {
    if (param==null) return '';
    var paramStr = '';
    var t = typeof (param);
    if (t == 'string' || t == 'number' || t == 'boolean') {
        paramStr += '&' + key + '='  + ((encode==null||encode) ? encodeURIComponent(param) : param);
    } else {
        for (var i in param) {
            var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i)
            paramStr += urlEncode(param[i], k, encode)
        }
    }
    return paramStr;

}

//日期格式化转换方法
Date.prototype.format = function(fmt) {
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)) {
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }
    for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
}

//JS利用日期判断星期几,一星期的开始是从星期日开始
function getMyDay(date) {
    var week;
    if (date.getDay() == 0) week = "星期日";
    if (date.getDay() == 1) week = "星期一";
    if (date.getDay() == 2) week = "星期二";
    if (date.getDay() == 3) week = "星期三";
    if (date.getDay() == 4) week = "星期四";
    if (date.getDay() == 5) week = "星期五";
    if (date.getDay() == 6) week = "星期六";
    return week;
}


//JS利用星期几数字判断星期几
function getMyDayBySort(Num) {
    var week;
    if (Num == 1) week = "星期日";
    if (Num == 2) week = "星期一";
    if (Num == 3) week = "星期二";
    if (Num == 4) week = "星期三";
    if (Num == 5) week = "星期四";
    if (Num == 6) week = "星期五";
    if (Num == 7) week = "星期六";
    return week;
}
//获取某个日期所在周周一的日期
function GetMonday(dd) {
    var week = dd.getDay(); //获取时间的星期数
    var minus = week ? week - 1 : 6;
    dd.setDate(dd.getDate() - minus); //获取minus天前的日期
    var y = dd.getFullYear();
    var m = dd.getMonth() + 1; //获取月份
    var d = dd.getDate();
    return y + "-" + m + "-" + d;
}

//保持时间段连续性
//匹配参数保持跟[基础参数设置]中上/下行的首/末班时间
//baseParams={firstTimeUp:"",latestTimeUp:"",firstTimeDown:"",latestTimeDown:""}
//matchStatus:true,false
function matchBaseParams( baseParams, dataDto) {
    var  warningText = "你设置的时间不连续！！！";
    var thisTypeParams={firstTimeUp:"",latestTimeUp:"",firstTimeDown:"",latestTimeDown:""} ;
    var upPreBusEndTime =""; //上行前一辆车辆的结束时间
    var downPreBusEndTime =""; //下行前一辆车辆的结束时间
    var matchStatus = true ;
    if(dataDto!=null&&dataDto.data!=null){
        //保持时间段连续性
        $.each(dataDto.data,function (i) {
            var direction=dataDto.data[i].direction;
            var beginTimeVal=dataDto.data[i].beginTime;
            var beginTime=beginTimeVal.substr(0,2)+":"+beginTimeVal.substr(2);

            var endTimeVal=dataDto.data[i].endTime;
            var endTime = endTimeVal.substr(0,2)+":"+endTimeVal.substr(2);

            if(direction==0){
                if(thisTypeParams.firstTimeUp==""){
                    thisTypeParams.firstTimeUp=beginTime;
                }
                if(upPreBusEndTime!=""&&upPreBusEndTime!=beginTime){
                    matchStatus=false;
                }
                upPreBusEndTime=endTime;
            }
            if(direction==1){
                if(thisTypeParams.firstTimeDown==""){
                    thisTypeParams.firstTimeDown=beginTime;
                }
                if(downPreBusEndTime!=""&&downPreBusEndTime!=beginTime){
                    matchStatus=false;
                }
                downPreBusEndTime=endTime;
            }
        })
    }
    //线路的末班车时间
    thisTypeParams.latestTimeUp =upPreBusEndTime ;
    thisTypeParams.latestTimeDown =downPreBusEndTime ;

    //是否匹配
    if(!matchStatus){
        //alert(warningText);
        return  warningText;
    }

    //匹配参数保持跟[基础参数设置]中上/下行的首/末班时间
    if(thisTypeParams instanceof  Object && baseParams instanceof  Object){
        if((baseParams.firstTimeUp<thisTypeParams.firstTimeUp)||(baseParams.latestTimeUp>thisTypeParams.latestTimeUp)
            ||(baseParams.firstTimeDown<thisTypeParams.firstTimeDown)||(baseParams.latestTimeDown>thisTypeParams.latestTimeDown)){
            //alert(warningText);
            return  warningText;
        }
    }
    return "";
}

function deepClone(target) {
    // 定义一个变量
    let result;
    // 如果当前需要深拷贝的是一个对象的话
    if (typeof target === 'object') {
        // 如果是一个数组的话
        if (Array.isArray(target)) {
            result = []; // 将result赋值为一个数组，并且执行遍历
            for (let i in target) {
                // 递归克隆数组中的每一项
                result.push(deepClone(target[i]))
            }
            // 判断如果当前的值是null的话；直接赋值为null
        } else if (target === null) {
            result = null;
            // 判断如果当前的值是一个RegExp对象的话，直接赋值
        } else if (target.constructor === RegExp) {
            result = target;
        } else {
            // 否则是普通对象，直接for in循环，递归赋值对象的所有值
            result = {};
            for (let i in target) {
                result[i] = deepClone(target[i]);
            }
        }
        // 如果不是对象的话，就是基本数据类型，那么直接赋值
    } else {
        result = target;
    }
    // 返回最终结果
    return result;
}

function fixWidth(percent) {
    return document.body.clientWidth * percent; //这里你可以自己做调整
}