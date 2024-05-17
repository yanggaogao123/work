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

//  做个全屏的构造函数
var FullScreen = function (elem) {
    this.elem = elem;
}
//  全屏
FullScreen.prototype.in = function () {
    if (this.elem.requestFullscreen) {
        this.elem.requestFullscreen();
    } else if (this.elem.webkitRequestFullscreen) {
        this.elem.webkitRequestFullscreen();
    } else if (this.elem.mozRequestFullScreen) {
        this.elem.mozRequestFullScreen();
    } else if (this.elem.msRequestFullscreen) {
        //  IE的实现没有实测过，不过据网上的资料说IE11以下可实现的
        this.elem.msRequestFullscreen();
    }
}
//  退出全屏
FullScreen.prototype.out = function () {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
    }
}


/**
 * 把扁平化的数组转成树形结构
 * @param list
 * @param idAttr
 * @param parentAttr
 * @param childrenAttr
 * @returns {Array}
 */
function treeify(list, idAttr, parentAttr, childrenAttr) {
    if (!idAttr) idAttr = 'id';
    if (!parentAttr) parentAttr = 'parentId';
    if (!childrenAttr) childrenAttr = 'children';
    var treeList = [];
    var lookup = {};
    list.forEach(function(obj) {
        lookup[obj[idAttr]] = obj;
        obj[childrenAttr] = [];
    });
    list.forEach(function(obj) {
        if (obj[parentAttr] != null && obj[parentAttr] != 0) {
            lookup[obj[parentAttr]][childrenAttr].push(obj);
        } else {
            treeList.push(obj);
        }
        // if (!obj['depth']) {
        //     if (obj[parentAttr] == null || obj[parentAttr] == 0) {
        //         obj['depth'] = 0;
        //     }else{
        //         console.log(lookup[obj[parentAttr]]['depth']);
        //         obj['depth'] = Number(lookup[obj[parentAttr]]['depth'])+1;
        //     }
        // }
    });
    return treeList;
};

function treeifyReplace(list,idAttr,parentAttr,childrenAttr){
    if (!idAttr) idAttr = 'id';
    if (!parentAttr) parentAttr = 'parentId';
    if (!childrenAttr) childrenAttr = 'children';
    var treeList = [];
    var lookup = {};
    list.forEach(function (obj) {
        lookup[obj[idAttr]] = obj;
        obj[childrenAttr] = [];
    });
    list.forEach(function (obj) {
        if (obj[parentAttr] != null && obj[parentAttr] != 0) {
            if (lookup[obj[parentAttr]] != null || lookup[obj[parentAttr]] != undefined) {
                lookup[obj[parentAttr]][childrenAttr].push(obj);
            } else {
                treeList.push(obj);
            }
        } else {
            treeList.push(obj);
        }
        if (!obj['areaName']){
            obj['areaName'] = obj['deviceName']
            if(!obj['deviceName']){
                 obj['areaName'] = obj['channelName']
            }
           
        }
    });
    return treeList;
}


function treeifyWithoutZero(list, idAttr, parentAttr, childrenAttr) {
    if (!idAttr) idAttr = 'id';
    if (!parentAttr) parentAttr = 'parentId';
    if (!childrenAttr) childrenAttr = 'children';
    var treeList = [];
    var lookup = {};
    list.forEach(function(obj) {
        lookup[obj[idAttr]] = obj;
        obj[childrenAttr] = [];
    });
    list.forEach(function(obj) {
        if (obj[parentAttr] != null) {
            if (lookup[obj[parentAttr]] != null || lookup[obj[parentAttr]] != undefined) {
                lookup[obj[parentAttr]][childrenAttr].push(obj);
            } else {
                treeList.push(obj);
            }

        } else {
            treeList.push(obj);
        }
        if (!obj['areaName']) {
            obj['areaName'] = obj['deviceName']
            if (!obj['deviceName']) {
                obj['areaName'] = obj['channelName']
            }

        }
    });
    return treeList;
};

function deptTreeify(list, idAttr, parentAttr, childrenAttr) {
    if (!idAttr) idAttr = 'id';
    if (!parentAttr) parentAttr = 'parentId';
    if (!childrenAttr) childrenAttr = 'children';
    var treeList = [];
    var lookup = {};
    var parentId = list[0].parentId;
    list.forEach(function (obj) {
        lookup[obj[idAttr]] = obj;
        obj[childrenAttr] = [];
    });
    list.forEach(function (obj) {
        if (obj[parentAttr] != null && obj[parentAttr] != parentId) {
            lookup[obj[parentAttr]][childrenAttr].push(obj);
        } else {
            treeList.push(obj);
        }
    });
    return treeList;
};


function hasPermission(perm) {
    // 从本地会话中获取登录用户的所有权限
    if (perm == null || perm == '') {
        return true;
    }
    var perms = sessionStorage.getItem('shiro.user.perms');
    var permsJSON = JSON.parse(perms);
    
    var flag = false;
    var len = permsJSON.length;
    for(var i = 0; i < len; i++) {
        if (perm == permsJSON[i]) {
            flag = true;
            break;
        }
    }
    return flag;
}

// 转换多选数组
function changeJson(arr){
    // if (arr && arr.length == 0) {
    //     return null;
    // }
    // var realArr = [];
    // var kLength = null;
    // console.log(arr);
    // var arrLen = arr.length;
    // for (var i = 0; i < arrLen; i++) {
    //     var pId = arr[i].parentId;
    //     if (realArr.length == 0) {
    //         realArr.push(arr[0]);
    //     }else{
    //         kLength = realArr.length;
    //         for (var k = 0; k < kLength; k++) {
    //             if (realArr[k].id != pId) {
    //                 // console.log(arr[i]);
    //                 realArr.push(arr[i]);
    //             }
    //         }
    //     }
        
    // }
    console.log(arr);
    return arr;
}


// XML转成json
function XmlToJson() {}
XmlToJson.prototype.setXml = function (xml) {
    if (xml && typeof xml == "string") {
        this.xml = document.createElement("div");
        this.xml.innerHTML = xml;
        this.xml = this.xml.getElementsByTagName("*")[0];
    } else if (typeof xml == "object") {
        this.xml = xml;
    }
};
XmlToJson.prototype.getXml = function () {
    return this.xml;
};
XmlToJson.prototype.parse = function (xml) {
    this.setXml(xml);
    return this.convert(this.xml);
};
XmlToJson.prototype.convert = function (xml) {
    if (xml.nodeType != 1) {
        return null;
    }
    var obj = {};
    obj.xtype = xml.nodeName.toLowerCase();
    var nodeValue = (xml.textContent || "").replace(/(\r|\n)/g, "").replace(/^\s+|\s+$/g, "");

    if (nodeValue && xml.childNodes.length == 1) {
        obj.text = nodeValue;
    }
    if (xml.attributes.length > 0) {
        for (var j = 0; j < xml.attributes.length; j++) {
            var attribute = xml.attributes.item(j);
            obj[attribute.nodeName] = attribute.nodeValue;
        }
    }
    if (xml.childNodes.length > 0) {
        var items = [];
        for (var i = 0; i < xml.childNodes.length; i++) {
            var node = xml.childNodes.item(i);
            var item = this.convert(node);
            if (item) {
                items.push(item);
            }
        }
        if (items.length > 0) {
            obj.items = items;
        }
    }
    return obj;
};
$.ajaxSetup({cache:false});


//easyUi dataGrid配置
$.extend($.fn.datagrid.methods, {
    editCell: function(jq,param){
        return jq.each(function(){
            var opts = $(this).datagrid('options');
            var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
            for(var i=0; i<fields.length; i++){
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor1 = col.editor;
                if (fields[i] != param.field){
                    col.editor = null;
                }
            }
            $(this).datagrid('beginEdit', param.index);
            for(var i=0; i<fields.length; i++){
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor = col.editor1;
            }
        });
    }
});

/*
		 * 为datagrid增加timespinner编辑器扩展
		 */
$.extend($.fn.datagrid.defaults.editors, {
    timespinner : {
        init : function (container, options) {
            var input = $('<input/>').appendTo(container);
            input.width(80);
            input.timespinner(options);
            return input;
        },
        getValue : function (target) {
            var val = $(target).timespinner('getValue');
            return val;
        },
        setValue : function (target, value) {
            if(value!=null){
                if (value.length>=16)
                    value=value.substring(11, 16);
            }
            $(target).timespinner('setValue', value);
        },
        resize : function (target, width) {
            var input = $(target);
            if ($.boxModel == true) {
                input.resize('resize', width - (input.outerWidth() - input.width()));
            } else {
                input.resize('resize', width);
            }
        }
    }
});


/*
		 * 为datagrid增加下拉框编辑器扩展
		 */
$.extend($.fn.datagrid.defaults.editors, {
    select2 : {
        init : function (container, options) {
            var input = $('<a style="margin-left:5px;" id="username" data-value="" context="'+options.url+'"/>').appendTo(container);
            input.width(80);
            return input;
        },
        getValue : function (target) {
            var val = target.attr("data-value");
            return val;
        },
        setValue : function (target, value) {
            var str = target.attr("context");
            target.attr("data-value",value);
            var results = function () {
                var result = [];
                $.ajax({
                    url: getRootPath() + str,
                    async: false,
                    type: "post",
                    data: {"keyword":value},
                    success: function (data, status) {
                        var flag = 0;
                        $.each(data, function (key, valu) {
                            result.push({ value: valu.busId, text: valu.busName });
                        });
                    }
                });
                return result;
            };
            var emptytext = "其他车辆";
            if(value == "" || value == undefined )
                emptytext = "未录入车辆信息";
            $(target).editable({
                inputclass:"width:300px;",
                type: "select2",
                source: results,
                disabled: false,
                emptytext: emptytext,
                mode: "popup",
                showbuttons:false,
                display: function(value) {
                    if(value != undefined && value != "") {
                        var q;
                        // $.ajax({
                        //     url: ctx + "/triplog/getDriverById/" + value,
                        //     async: false,
                        //     type: "get",
                        //     data: {},
                        //     success: function (data, status) {
                        //         q = data.driverName+"-"+data.qualification;
                        //     }
                        // });
                        $(this).text(value);
                    }
                },
                validate: function (value) {
                    if (!$.trim(value)) {
                        return '不能为空';
                    }
                    target.attr("data-value",value);
                }
            });
        },
        resize : function (target, width) {
            var input = $(target);
            if ($.boxModel == true) {
                input.resize('resize', width - (input.outerWidth() - input.width()));
            } else {
                input.resize('resize', width);
            }
        }
    }
});


/**
 * 克隆对象方法
 * @param obj
 * @return
 */
function clone(obj){
    var o, obj;
    if (obj.constructor == Object){
        o = new obj.constructor();
    }else{
        o = new obj.constructor(obj.valueOf());
    }
    for(var key in obj){
        if ( o[key] != obj[key] ){
            if ( typeof(obj[key]) == 'object' ){
                o[key] = clone(obj[key]);
            }else{
                o[key] = obj[key];
            }
        }
    }
    o.toString = obj.toString;
    o.valueOf = obj.valueOf;
    return o;
}