var canvas={};
canvas.XInfo={};
canvas.XInfo.X_height=[];
canvas.width=0;
canvas.height=0;
canvas.widthX=0;//x轴高度
canvas.widthY=0;//y轴高度
canvas.spaceY=0;//Y间隔
canvas.spaceX=0;//X间隔
canvas.coordinate={};
canvas.coordinate.height=0;//x坐标高度
canvas.coordinate.width=0;//y坐标长度
canvas.origin={x:0,y:0};//原点的x，y坐标

canvas.init=function(canvasNode){ //初始化 根据画板的大小 设置坐标的初始位置
    canvas.width=canvasNode.width;
    canvas.height=canvasNode.height;
    canvas.widthY=canvas.height*0.97;
    canvas.widthX=canvas.width*0.88;
};

/**
 * 画条形图
 * @param  canvasNode canvas对象
 * @param coordinate  是一个对象 必须包含有 XInfo（X轴信息）,YInfo（Y轴信息）,color（条形图对应颜色，可以为空）,prrcent（每个条形图所占中的百分比）,都是数组
 */
canvas.drawBarChart=function(canvasNode,coordinate){
    canvas.init(canvasNode);    //初始化数据
    var context=canvas.get2DContext(canvasNode);    //获取画板对象
    var startLinePosX=canvas.width-canvas.widthX;   //坐标轴左上角位置(x坐标)
    var startLinePosY=canvas.height-canvas.widthY;  //坐标轴左上角位置(y坐标)
    context.beginPath();    //起始一条新路径
    context.moveTo(startLinePosX,startLinePosY);
    context.lineTo(startLinePosX,canvas.widthY);
    canvas.origin.x=startLinePosX;
    canvas.coordinate.height=canvas.widthY-startLinePosY;
    context.moveTo(startLinePosX,startLinePosY);
    context.lineTo(canvas.widthX,startLinePosY);
    canvas.origin.y=startLinePosY;
    canvas.coordinate.width=canvas.widthX-startLinePosX;
    context.fill();//填充
    context.stroke();//画线
    context.font="bold 20px 微软雅黑";
    canvas.drawArraw(context,{midX:startLinePosX,midY:canvas.widthY},true);
    canvas.drawArraw(context, {midX:canvas.widthX,midY:startLinePosY},false);

    /*  canvas.drawCoordinate(context,[10,20,30,40,50,60,70,80,90],null,null,true);
      canvas.drawCoordinate(context,['一月','二月','三月','四月','五月','六月','七月'],null,[0.3,0.4,0.5,0.9,0.19,0.8,0.78,0.99,0.12],false);
      */
    canvas.drawCoordinate(context,coordinate.YInfo,coordinate.color,coordinate.percent,false);
    canvas.drawCoordinate(context,coordinate.Xf,null,null,true);
    console.info(coordinate.Xf);
};

/**
 * @param context 画布
 * @param pos 画布位置 必须包含箭头中点位置:midX,midY,箭头左边最底高度：height,宽度
 */
canvas.drawArraw=function(context,pos,isVertical){
    context.moveTo(pos.midX,pos.midY);
    context.lineWidth+=context.lineWidth;
    if(!isVertical){
        context.lineTo(pos.midX-9,pos.midY+4);
        context.moveTo(pos.midX,pos.midY);
        context.lineTo(pos.midX-9,pos.midY-4);
        context.fill();//填充
        context.stroke();//画线
    }else{
        context.lineTo(pos.midX-4,pos.midY-9);
        context.moveTo(pos.midX,pos.midY);
        context.lineTo(pos.midX+4,pos.midY-9);
        context.fill();//填充
        context.stroke();//画线
    }
    context.lineWidth=context.lineWidth-1;
};


/**
 * 画坐标
 * @param 画布
 * @param x或者y的坐标信息
 * @param 是否是垂直方向
 */
canvas.drawCoordinate=function(context,XOrY_heightArr,color,percent,isVerticial){
    var xOrigin=canvas.origin.x-60;//原点x坐标
    var yOrigin=canvas.origin.y;//原点y坐标
    var widthX=canvas.coordinate.width;//x坐标轴宽度
    var heightY=canvas.coordinate.height;//y坐标轴高度
    var section=XOrY_heightArr==null?0:XOrY_heightArr.length;
    var info=XOrY_heightArr;
    var perSize=0;//每个坐标之间的距离
    var actWidth=widthX-canvas.width*0.02;
    var actHeight=heightY-canvas.height*0.05;
    var tmpColor=['#FFA500','#EE7AE9','#E0FFFF','#AAAAAA','#8E8E8E','#8B1A1A','#76EE00','#525252','342354','362575','257436','246356','#00EE00','#4B0082'];
    color=color==null?tmpColor:color;

    if(isVerticial&&section==0){
        info=canvas.XInfo.X_height;
    }

    if(isVerticial){
        perSize=actHeight/info.length;
        canvas.spaceY = perSize;
    }else{
        if(section==0){
            return;
        }
        perSize=actWidth/info.length;
        canvas.spaceX = perSize;
    }
    for(var i=1;i<=info.length;i++){
        if(isVerticial){
            context.moveTo(xOrigin-5,yOrigin+perSize*i);
            context.lineTo(xOrigin,yOrigin+perSize*i);
            context.stroke();
            context.fillText(info[i-1].name,xOrigin-110,yOrigin+perSize*i+4);
            var timelist = info[i-1].timelist;
            for(var j=0;j<timelist.length;j++){
                var startArr = timelist[j].start.split(":");
                var endArr  = timelist[j].end.split(":");
                var direction = timelist[j].direction;
                var serviceType = timelist[j].serviceType;
                var serviceName = timelist[j].serviceName;
                //设置矩形坐标位置
                var xorigin = xOrigin + ((startArr[0] - 6)*2+1) * canvas.spaceX + (canvas.spaceX/30*startArr[1]);
                var yorigin = yOrigin+perSize*i - perSize/3;
                var width = xOrigin + ((endArr[0] - 6)*2+1) * canvas.spaceX + canvas.spaceX/30*endArr[1] - xorigin;
                var heigth = perSize/3*2;
                if(direction == 0){
                    context.fillStyle ='lightblue';
                }else if(direction == 1){
                    context.fillStyle ='deepskyblue';
                }
                if(serviceType < 0){
                    context.fillStyle ='lime';
                }
                if(serviceType==2){
                    //短线 zyj
                    context.shadowBlur=20; //阴影的模糊级别：20
                    context.shadowColor ='red'; //红色的阴影
                }else {
                    context.shadowBlur=0;
                }
                context.fillRect(xorigin,yorigin,width,heigth);
                context.fillStyle='black';
                var textWidth = context.measureText(timelist[j].start+'-'+timelist[j].end).width;
                if(serviceType < 0){
                    textWidth = context.measureText(timelist[j].start+'-'+timelist[j].end+' ( '+serviceName+' )').width;
                    context.fillText(timelist[j].start+'-'+timelist[j].end+' ( '+serviceName+' )',xorigin+(width-textWidth)/2,yorigin+(heigth-16)/2+14);
                }else{
                    context.fillText(timelist[j].start+'-'+timelist[j].end,xorigin+(width-textWidth)/2,yorigin+(heigth-16)/2+14);
                }
                // context.fillText(timelist[j].start+'-'+timelist[j].end,xorigin+(width-textWidth)/2,yorigin+(heigth-16)/2+14);
                //context.fillText(timelist[j].end,xorigin+(width-80)/2,yorigin+(heigth-32)/2+34);
            }
        }else{
            context.moveTo(xOrigin+perSize*i,yOrigin-5);
            context.lineTo(xOrigin+perSize*i,yOrigin);
            context.stroke();
            var character=info[i-1];

            context.fillText(character,xOrigin+perSize*i-20,yOrigin-10);
            context.fillStyle =color[i];
        }
    }
};

/**
 * 获取画布
 */
canvas.get2DContext=function(canvasNode){
    return canvasNode.getContext("2d");
};

/**
 * 清空画布
 * @param canvasNode
 */
canvas.clearCanvas=function(canvasNode){
    var context=canvas.get2DContext(canvasNode);
    context.clearRect(0,0,canvasNode.width,canvasNode.height);
}