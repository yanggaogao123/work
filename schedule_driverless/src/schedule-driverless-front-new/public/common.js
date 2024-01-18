//扩展JS自带的Date,增加格式化
Date.prototype.formate = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    var week = {
        "0": "\u65e5",
        "1": "\u4e00",
        "2": "\u4e8c",
        "3": "\u4e09",
        "4": "\u56db",
        "5": "\u4e94",
        "6": "\u516d"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "\u661f\u671f" : "\u5468") : "") + week[this.getDay() + ""]);
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
};

//自定义通用方法
common = {
    //下载图片,下载完成后进行回调
    loadImage: function (url, callback, data) {
        var img = new Image(); //创建一个Image对象，实现图片的预下载
        img.src = url;
        img.data = data;
        if (img.complete) { // 如果图片已经存在于浏览器缓存，直接调用回调函数
            callback.call(img);
            return; // 直接返回，不用再处理onload事件
        }
        img.onload = function () { //图片下载完毕时异步调用callback函数。
            callback.call(img);//将回调函数的this替换为Image对象
        };
    },
    //获取当前滚动位置,用于跳转到其他页面后在返回时恢复跳转前的状态
    getPageScroll: function () {
        var x = 0, y = 0;
        if (window.pageYOffset) {    // all except IE
            y = window.pageYOffset;
            x = window.pageXOffset;
        } else if (document.documentElement && document.documentElement.scrollTop) {    // IE 6 Strict
            y = document.documentElement.scrollTop;
            x = document.documentElement.scrollLeft;
        } else if (document.body) {    // all other IE
            y = document.body.scrollTop;
            x = document.body.scrollLeft;
        }
        return {x: x, y: y};
    },
    //通过参数名获取url中的参数值
    getUrlParam: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return "";
    },
    //获取参数名获取cookie中的参数值
    getCookie: function (name) {
        var reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
        var r = document.cookie.match(reg);
        if (r != null) return unescape(r[2]);
        return "";
    },
    //简单验证手机号码是否正确
    checkPhone: function (phone) {
        if (phone.match(/^(1[3|5|7|8])\d{9}$/)) {
            return true;
        }
        return false;
    },
    //检测身份证号码是否正确
    checkCard: function (card) {
        if (card.match(/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/)) {
            return true;
        }
        return false;
    },
    addJsonObject: function (json, key, value, maxNum) {
        if (!maxNum) maxNum = 999999999;
        json = common.delJsonObject(json, key);
        while (Object.getOwnPropertyNames(json).length >= maxNum) {
            for (var i in json) {
                delete json[i];
                break;
            }
        }
        json[key] = value;
        return json;
    },
    delJsonObject: function (json, key) {
        if (json[key]) {
            delete json[key];
        }
        return json;
    },
    addJsonArray: function (json, obj, maxNum) {
        if (!maxNum) maxNum = 999999999;
        json = common.delJsonArray(json, obj);
        while (json.length >= maxNum) {
            json.shift();
        }
        json.push(obj);
        return json;
    },
    delJsonArray: function (json, obj) {
        for (var i in json) {
            if (JSON.stringify(json[i]) == JSON.stringify(obj)) {
                json.splice(i, 1);
                break;
            }
        }
        return json;
    },
    addLocalStorage: function (key, obj, maxNum) {
        var json = common.getLocalStorage(key);
        json = common.addJsonArray(json, obj, maxNum);
        common.setLocalStorage(key, json);
    },
    addLocalStorageObj: function (key, name, value, maxNum) {
        var json = common.getLocalStorageObj(key);
        json = common.addJsonObject(json, name, value, maxNum);
        common.setLocalStorage(key, json);
    },
    setLocalStorage: function (key, json) {
        if (typeof json != "string") {
            json = JSON.stringify(json);
        }
        localStorage.setItem(key, json);
    },
    getLocalStorage: function (key) {
        var json;
        var value = localStorage.getItem(key);
        if (value) {
            try {
                json = JSON.parse(value);
                if (!(json instanceof Array)) {
                    json = new Array();
                }
            } catch (e) {
                json = new Array();
            }
        } else {
            json = new Array();
        }
        return json;
    },
    getLocalStorageObj: function (key) {
        var json;
        var value = localStorage.getItem(key);
        if (value) {
            try {
                json = JSON.parse(value);
                if (json instanceof Array) {
                    json = json[0] ? json[0] : new Object();
                }
            } catch (e) {
                json = new Object();
            }
        } else {
            json = new Object();
        }
        return json;
    },
    delLocalStorage: function (key, obj) {
        if (obj) {
            var json = common.getLocalStorage(key);
            json = common.delJsonArray(json, obj);
            common.setLocalStorage(key, json);
        } else {
            localStorage.removeItem(key);
        }
    },
    addSessionStorage: function (key, name, value, maxNum) {
        var json = common.getSessionStorage(key);
        json = common.addJsonObject(json, name, value, maxNum);
        common.setSessionStorage(key, json);
    },
    setSessionStorage: function (key, json) {
        if (typeof json != "string") {
            json = JSON.stringify(json);
        }
        sessionStorage.setItem(key, json);
    },
    getSessionStorage: function (key) {
        var json;
        var value = sessionStorage.getItem(key);
        if (value) {
            try {
                json = JSON.parse(value);
                if (json instanceof Array) {
                    json = json[0] ? json[0] : new Object();
                }
            } catch (e) {
                json = new Object();
            }
        } else {
            json = new Object();
        }
        return json;
    },
    delSessionStorage: function (key, name) {
        if (name) {
            var json = common.getSessionStorage(key);
            json = common.delJsonObject(json, name);
            common.setSessionStorage(key, json);
        } else {
            sessionStorage.removeItem(key);
        }
    },
    //转意符换成普通字符
    escape2Html: function (str) {
        var arrEntities = {'lt': '<', 'gt': '>', 'nbsp': ' ', 'amp': '&', 'quot': '"'};
        return str.replace(/&(lt|gt|nbsp|amp|quot);/ig, function (all, t) {
            return arrEntities[t];
        });
    },
    // &nbsp;转成空格
    nbsp2Space: function (str) {
        var arrEntities = {'nbsp': ' '};
        return str.replace(/&(nbsp);/ig, function (all, t) {
            return arrEntities[t];
        });
    },
    //通过android或者IOS或者url参数获取用户信息
    getUserInfo: function () {

        var json = undefined;
        var reqparaStr = undefined;
        if (typeof AndroidApp != "undefined") {
            reqparaStr = AndroidApp.getUserInfo();
        } else {
            if (typeof getUserInfo != "function") {
                var iFrame = document.createElement("iframe");
                document.body.appendChild(iFrame);
                iFrame.parentNode.removeChild(iFrame);
            }
            if(typeof getUserInfo == "function"){
                json = getUserInfo();
            }
            if(typeof iOSApp != "undefined"){
                json = JSON.parse(iOSApp.getUserInfo())
            }
        }
        if (!reqparaStr && !json) {
            reqparaStr = common.getUrlParam("reqpara") ? common.getUrlParam("reqpara") : "{}";
        }

        try {
            if (!json && reqparaStr) {
                json = JSON.parse(reqparaStr);
            }
        } catch (e) {
            json = new Object();
        }
        return json;
    },
    //对比相似度
    compare: function (x, y) {
        var z = 0;
        var s = x.length + y.length;
        ;
        x.sort();
        y.sort();
        var a = x.shift();
        var b = y.shift();
        while (a !== undefined && b !== undefined) {
            if (a === b) {
                z++;
                a = x.shift();
                b = y.shift();
            } else if (a < b) {
                a = x.shift();
            } else if (a > b) {
                b = y.shift();
            }
        }
        return z / s * 200;
    }
};

var pageinfo = {pageindex: 1, pagesize: 10};
var pagescroll;

if (sessionStorage.pageinfo) {
    pageinfo = common.getSessionStorage("pageinfo");
}

if (sessionStorage.pagescroll) {
    pagescroll = common.getSessionStorage("pagescroll");
}

$(function () {
    //文本框只能输入正整数
    $.fn.numberbox = function () {
        $(this).css("ime-mode", "disabled");
        this.bind("keypress", function (e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            return code >= 48 && code <= 57;
        });
        this.bind("blur", function () {
            if (isNaN(this.value) || this.value.indexOf(".") >= 0) {
                this.value = "";
            }
        });
        this.bind("dragenter", function () {
            return false;
        });
        this.bind("keyup", function () {
            if (isNaN(this.value) || this.value.indexOf(".") >= 0) {
                this.value = "";
            }
        });
    };

    //文本框只能输入正整数和X,用于身份证
    $.fn.cardbox = function () {
        $(this).css("ime-mode", "disabled");
        this.bind("keypress", function (e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            return (code >= 48 && code <= 57) || code == 88 || code == 120;
        });
        this.bind("dragenter", function () {
            return false;
        });
        this.bind("keyup", function () {
            var reg = /^(\d|X|x)*$/;
            if (reg.test(this.value) === false) {
                this.value = "";
            }
        });
    };

    //文本框不能输入某些特殊字符
    $("input,textarea").keyup(function () {
        this.value = this.value.replace(/["'<>%;]/g, "");
    });

    //jquery ajax 通用设置
    var loadingIndex = null;
    $.ajaxSetup({
        cache: false,
        timeout: 10000,
        data: {reqpara: JSON.stringify(common.getUserInfo())},
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if (loadingIndex != null) {
                $.hideLoading(loadingIndex);
                loadingIndex = null;
            }
            if (textStatus == "timeout") {
                if (typeof ajaxtimeout == "function") ajaxtimeout();
            }
        },
        beforeSend: function (XMLHttpRequest) {
            loadingIndex = $.showLoading();
        },
        complete: function (XMLHttpRequest, textStatus) {
            if (loadingIndex != null) {
                $.hideLoading(loadingIndex);
                loadingIndex = null;
            }
            var error = XMLHttpRequest.getResponseHeader("Error");
            if (error) {
                location.href = error;
            }
        }
    });
});

$.extend({
    //弹窗1
    showWarring: function (content, shadeClose) {
        var index = layer.open({
            shadeClose: shadeClose ? shadeClose : false,
            content: content
        });
        return index;
    },
    //弹窗1
    showTime: function (content, shade, time) {
        var index = layer.open({
            shade: shade ? shade : false,
            content: content,
            time: time ? time : 0
        });
        return index;
    },
    //显示loading
    showLoading: function (txt) {
        var pageScroll = common.getPageScroll();
        setTimeout('scroll(' + pageScroll.x + ',' + pageScroll.y + ')', 1);
        var index = layer.open({
            shadeClose: false,
            type: 2,
            content: txt ? txt : "",
            time: 2,
        });
        return index;
    },
    //隐藏loading
    hideLoading: function (index) {
        layer.close(index);
    },
    //弹窗2
    alert: function (content, title, endfunc) {
        var index = layer.open({
            title: title == "no_title" ? "" : [ //或直接title:'标题'
                title ? title : "提示",
                "background-color:#F90; color:white;" //标题样式
            ],
            content: content,
            end: endfunc
        });
        return index;
    },
    //弹窗3
    alert_ok: function (content, title, endfunc) {
        var index = layer.open({
            title: title == "no_title" ? "" : [ //或直接title:'标题'
                title ? title : "提示",
                "background-color:#F90; color:white;" //标题样式
            ],
            btn: ["确定"],
            content: content,
            end: endfunc
        });
        return index;
    },
    //确认框
    confirm: function (content, yesfunc, nofunc, cancelfunc, endfunc) {
        var index = layer.open({
            shadeClose: false,
            title: [ //或直接title:'标题'
                "确认",
                "background-color:#F90; color:white;" //标题样式
            ],
            content: content,
            btn: ["确定", "取消"],
            yes: yesfunc,
            no: nofunc,
            cancel: cancelfunc,
            end: endfunc
        });
        return index;
    },
    //没有title的确认框
    confirm_notitle: function (content, yesfunc, nofunc, endfunc) {
        var index = layer.open({
            shadeClose: false,
            content: content,
            btn: ["确定", "取消"],
            yes: yesfunc,
            no: nofunc,
            end: endfunc
        });
        return index;
    }
});