//webSocket工具
//path:webSocket地址：
//openFun：打开执行的方法
//openFunParams：打开方法的参数
//msgFun：接受信息执行的方法
//msgFunParams：接受信息方法的参数
//closeFun：关闭执行的方法
//closeFunParams：关闭执行方法的参数
function webSocketUtil(path,openFun,openFunParams,msgFun,msgFunParams,closeFun,closeFunParams) {
    if('WebSocket' in window)
    {
        var url = 'ws://' + window.location.host +window.contextPath + path;
        var sock = new WebSocket(url);      //打开WebSocket
    }else
    {
        alert("你的浏览器不支持WebSocket");
    }

    sock.onopen = function(path) {          //处理连接开启事件
        console.log('Opening');
        var data={"message":"开启"+path+"的webSocket","routeCode":routeCode};
        sock.send(JSON.stringify(data));
        if(openFun){
            openFun(openFunParams)
        }
    };

    sock.onmessage = function(e,fun) {      //处理信息
        e = e || event;        //获取事件，这样写是为了兼容IE浏览器
        console.log(e.data);
        if(msgFun){
            msgFun(msgFunParams);

        }
    };

    sock.onclose = function() {         //处理连接关闭事件
        console.log('Closing');
        if(closeFun){
            closeFun(closeFunParams);
        }
        sock.close();
    };

    //连接发生错误的回调方法
    sock.onerror = function () {
        sock.close();
    };

    return sock;
}