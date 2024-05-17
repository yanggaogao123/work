//一般直接写在一个js文件中
layui.use(['layer', 'form'], function () {
    var layer = layui.layer
        , form = layui.form;
});
layui.use('laydate', function () {
    var laydate = layui.laydate;

    if (getQueryString("routeId") === null && getQueryString('runDate') === null) {
        laydate.render({
            elem: '#time', //指定元素
            type: 'date',
            theme: '#00bfff',
            value: new Date()
        });
    } else {
        laydate.render({
            elem: '#time', //指定元素
            type: 'date',
            theme: '#00bfff',
            value: new Date(getQueryString('runDate'))
        });
    }

});

//var planType = "0";

var vm = new Vue({
    el: '#wrap',
    data() {
        return {
            timeList: [],
            busInfo: [],
            demoBusInfo: [],
            // 横向时间轴的分钟数
            allTime: "",
            // demoCss:{
            //     left:"40%",
            //     width:"6%"
            // },
            infoList: [],
            date: '',
            firstRouteStaName: '',
            lastRouteStaName: '',

            leftTime: "5:45",
            rightTime: "23:15",
        }
    },
    methods: {

        search() {
            let line = $("#routeSelect").select2('val');
            let time = $("#time").val();
            console.log(line);
            console.log(time);

            if (time == '' || line == '') {
                alert('请选择线路和日期');
            } else {
                //获取首末站点名称
                this.getFirstLastRouteStaName(line);
                this.date = time;
                this.takeData(line, time);
            }
        },


        // 横向坐标轴
        createdTimeList() {

            let rightDate;

            if (parseInt(this.rightTime.substr(0,2)) >= '24') {
                let tmp = parseInt(this.rightTime.substr(0,2)) - 24;
                rightDate = '2019-09-25 0'+ tmp +':' + this.rightTime.substr(3,2);
            } else {
                rightDate = '2019-09-24 ' + this.rightTime;
            }

            this.allTime = this.GetDateDiff('2019-09-24 ' + this.leftTime, rightDate, "minute");
            console.log(this.allTime);
            let timeSize = this.allTime / 30;


            let a = 6;
            let b = 0;
            let c = 0;

            let hour = this.leftTime.substr(0, 2);
            let min = this.leftTime.substr(3, 2);

            var startFlag;

            if (min == '15') {
                a = parseInt(hour);
                b = 3;
                c = 0;
                startFlag = true;
            } else if (min == '45') {
                a = parseInt(hour) + 1;
                b = 0;
                c = 0;
                startFlag = false;
            }

            /*let a = 6;
            let b = 0;
            let c = 0;*/
            this.timeList = [];
            if (startFlag) {
                for (var i = 0; i < timeSize; i++) {
                    let d = a + ':' + b + c;
                    this.timeList.push(d);
                    // 单数
                    if (i % 2 == 0) {
                        b = 0;
                        a += 1;
                        if (a >=24) {
                            a = a-24;
                        }
                        // 双数
                    } else {
                        b = 3;
                    }
                }
            } else {
                for (var i = 0; i < timeSize; i++) {
                    let d = a + ':' + b + c;
                    this.timeList.push(d);
                    // 单数
                    if (i % 2 != 0) {
                        b = 0;
                        a += 1;
                        if (a >=24) {
                            a = a-24;
                        }
                        // 双数
                    } else {
                        b = 3;
                    }
                }
            }

            console.log(this.timeList);
        },

        // 提取数据
        takeData(bs, rd) {
            let routeId = bs;
            let runDate = rd;

            /*if (getQueryString('planType') !== null) {
                planType = getQueryString('planType');
            }*/

            var path = "/schedule/getDriverlessDetail";
            var data = {
                "routeId": routeId,
                "runDate": runDate + ' 00:00:00',
                //"planType" : planType
            };
            var success = function(json) {
                if (json.retCode != 0) {
                    alert(json.retMsg);
                    return;
                }

                vm.busInfo = json;
                console.log(vm.busInfo);

                vm.setLeftAndRightTime(json.data);

                let diffArr = vm.formateArrDataA(vm.busInfo.data, 'busNumber');

                let busArr = [];
                for (let i in diffArr) {
                    let a = diffArr[i];
                    // console.log(a);
                    let arr = vm.formateArrDataA(a, 'firstDirection');
                    for (let k in arr) {
                        let b = arr[k];
                        busArr.push(b);
                    }

                }
                console.log(busArr);

                for (let i in busArr) {
                    //班次
                    var count = 0;
                    //总行车时间,总营运时间,总停站时间
                    var totalTime = 0, runTime = 0, stopTime = 0;
                    for (let k in busArr[i]) {
                        let a = busArr[i][k];
                        // console.log(a);
                        let st = a.tripBeginTime;
                        let et = a.tripEndTime;
                        // console.log(st);
                        // console.log(et);
                        let time = vm.GetDateDiff(st, et, "minute");
                        // console.log(time);
                        let wid = time / vm.allTime * 100;
                        let d = rd + ' 0' + vm.leftTime + ':00';
                        let left = vm.GetDateDiff(d, st, "minute");
                        // console.log(left);
                        let dis = left / vm.allTime * 100;
                        // console.log(dis);
                        a.styleObject = {};
                        a.styleObject.left = dis + '%';
                        a.styleObject.width = wid + '%';
                        if (a.serviceType != -32 && a.serviceType != -78) {
                            count++;
                            //计算营运时间
                            runTime+=time;
                            //计算停站时间
                            if (k <  busArr[i].length - 1) {
                                let a1 = busArr[i][k];
                                let a2 = busArr[i][parseInt(k)+1];
                                let t1 = a1.tripEndTime;
                                let t2 = a2.tripBeginTime;
                                stopTime += vm.GetDateDiff(t1, t2, "minute");
                            }
                        }
                    }

                    totalTime = Math.fround((runTime + stopTime)/60).toFixed(2);
                    runTime = Math.fround(runTime/60).toFixed(2);
                    stopTime = Math.fround(stopTime/60).toFixed(2);
                    busArr[i][0].count = count;
                    busArr[i][0].totalTime = totalTime;
                    busArr[i][0].runTime = runTime;
                    busArr[i][0].stopTime = stopTime;

                }
                vm.infoList = busArr;
            };
            ajaxClient2(path, "post", data, success);
        },

        setLeftAndRightTime(list) {
            if (list.length < 1) {
                return;
            }

            //该线路该天计划的最早时间 hh:mm
            let start = list[0]['tripBeginTime'].substr(11, 5);
            let startDay = list[0]['tripBeginTime'].substr(8, 2);
            //该线路该天计划的最晚时间
            let end = list[0]['tripEndTime'].substr(11, 5);
            for (let i = 1; i < list.length; i++) {
                /*let tempStart = list[i]['tripBeginTime'].substr(12, 4);
                let startHourTemp = tempStart.substr(0, 1);
                let startMinTemp = tempStart.substr(2, 2);
                let startHour = start.substr(0, 1);
                let startMin = start.substr(2, 2);
                if (parseInt(startHourTemp) < parseInt(startHour)) {
                    start = tempStart;
                } else if (parseInt(startHourTemp) > parseInt(startHour)) {
                } else {
                    if (parseInt(startMinTemp) < parseInt(startMin)) {
                        start = tempStart;
                    }
                }*/
                let endDay = list[i]['tripEndTime'].substr(8, 2);

                let tempEnd = list[i]['tripEndTime'].substr(11, 5);
                let endHourTemp = tempEnd.substr(0, 2);
                let endMinTemp = tempEnd.substr(3, 2);
                let endHour = end.substr(0, 2);
                let endMin = end.substr(3, 2);
                if (parseInt(endDay) > parseInt(startDay)) {
                    endHourTemp = (24 + parseInt(endHourTemp)) + "";
                }
                /*if (endHourTemp == '00') {
                    endHourTemp = '24';
                }*/
                if (parseInt(endHourTemp) > parseInt(endHour)) {
                    end = endHourTemp + ':' + endMinTemp;
                } else if (parseInt(endHourTemp) < parseInt(endHour)) {
                } else {
                    if (parseInt(endMinTemp) > parseInt(endMin)) {
                        end = endHourTemp + ':' + endMinTemp;
                    }
                }
            }

            let startHour = start.substr(0, 2);
            let startMin = start.substr(3, 2);

            if (parseInt(startMin) < 15) {
                this.leftTime = (parseInt(startHour) - 1) + ':45';
            } else if (parseInt(startMin) > 45) {
                this.leftTime = startHour + ':45';
            } else {
                this.leftTime = startHour + ':15';
            }

            let endHour = end.substr(0, 2);
            let endMin = end.substr(3, 2);

            if (parseInt(startHour) > parseInt(endHour)) {
                endHour = (24 + parseInt(endHour)) + "";
            }

            /*if (endHour == '00') {
                endHour = '24';
            }*/

            if (parseInt(endMin) < 15) {
                this.rightTime = endHour + ':15';
            } else if (parseInt(endMin) > 45) {
                this.rightTime = (parseInt(endHour) + 1) + ':15';
            } else {
                this.rightTime = endHour + ':45';
            }

            this.createdTimeList();
        },

        // 计算时间
        GetDateDiff(startTime, endTime, diffType) {
            //将xxxx-xx-xx的时间格式，转换为 xxxx/xx/xx的格式
            startTime = startTime.replace(/\-/g, "/");
            endTime = endTime.replace(/\-/g, "/");
            //将计算间隔类性字符转换为小写
            diffType = diffType.toLowerCase();
            var sTime = new Date(startTime); //开始时间
            var eTime = new Date(endTime); //结束时间
            //作为除数的数字
            var timeType = 1;
            switch (diffType) {
                case "second":
                    timeType = 1000;
                    break;
                case "minute":
                    timeType = 1000 * 60;
                    break;
                case "hour":
                    timeType = 1000 * 3600;
                    break;
                case "day":
                    timeType = 1000 * 3600 * 24;
                    break;
                default:
                    break;
            }
            return parseInt((eTime.getTime() - sTime.getTime()) / parseInt(timeType));
        },

        // 根据值分组
        formateArrDataA(initialArr, name) {
            // 判定传参是否符合规则
            if (!(initialArr instanceof Array)) {
                return '请传入正确格式的数组'
            }
            if (!name) {
                return '请传入对象属性'
            }
            //先获取一下这个数组中有多少个"name"
            let nameArr = []
            for (let i in initialArr) {
                if (nameArr.indexOf(initialArr[i][`${name}`]) === -1) {
                    nameArr.push(initialArr[i][`${name}`])
                }
            }
            //新建一个包含多个list的结果对象
            let tempObj = {}
            // 根据不同的"name"生成多个数组
            for (let k in nameArr) {
                for (let j in initialArr) {
                    if (initialArr[j][`${name}`] == nameArr[k]) {
                        // 每次外循环时新建一个对应"name"的数组, 内循环时当前数组不变
                        tempObj[nameArr[k]] = tempObj[nameArr[k]] || []
                        tempObj[nameArr[k]].push(initialArr[j])
                    }
                }
            }
            return tempObj
        },

        // css数据组成
        cssData() {
            let busArr = this.infoList;
            for (let i in busArr) {
                for (let k in busArr[i]) {
                    let a = busArr[i][k];
                    // console.log(a);

                    let st = a.tripBeginTime;
                    let et = a.tripEndTime;
                    // console.log(st);
                    // console.log(et);
                    let time = this.GetDateDiff(st, et, "minute");
                    // console.log(time);
                    let wid = time / this.allTime * 100;

                    let left = this.GetDateDiff("2019-09-19 05:45:00", st, "minute");
                    // console.log(left);
                    let dis = left / this.allTime * 100;
                    // console.log(dis);
                    // this.demoCss.left = dis + '%';
                    // this.demoCss.width = wid + '%';
                    a.styleObject = {};
                    a.styleObject.left = dis + '%';
                    a.styleObject.width = wid + '%';
                    // a.left = dis;
                    // a.dis = wid;
                }
            }
            this.infoList = busArr;
        },
        //单元格样式
        setClassStyle(itemss) {
            let itemssThat = itemss;
            let style = '';
            if (itemssThat.serviceType < 0) {
                style += 'single-stop ' //单班中停
            } else if (itemssThat.serviceType != 1) {
                style += 'not-long ' //非全程
                style += itemssThat.direction == '0' ? 'up' : 'down';
            } else {
                style += itemssThat.direction == '0' ? 'up' : 'down';
            }
            return style;
        },
        //车站名称 colNum ：1 单元格第一层，2 单元格第二层
        setStationName(itemss, colNum) {
            let itemssThat = itemss;
            let stationName = "";
            let tripBeginTime = itemssThat.tripBeginTime.split(" ")[1].split(":")[0] + ":" + itemssThat.tripBeginTime.split(" ")[1].split(":")[1];
            let tripEndTime = itemssThat.tripEndTime.split(" ")[1].split(":")[0] + ":" + itemssThat.tripEndTime.split(" ")[1].split(":")[1];
            if (colNum == 1) {
                if (itemssThat.serviceType == 1) {
                    stationName = tripBeginTime;
                } else {
                    if (itemssThat.firstRouteStaName != null
                        && itemssThat.firstRouteStaName != vm.firstRouteStaName && itemssThat.firstRouteStaName != vm.lastRouteStaName) {
                        stationName = itemssThat.firstRouteStaName;
                    } else {
                        stationName = tripBeginTime;
                    }
                }

            } else {
                if (itemssThat.serviceType == 1) {
                    stationName = tripEndTime;
                } else {
                    if (itemssThat.lastRouteStaName != null
                        && itemssThat.lastRouteStaName != vm.firstRouteStaName && itemssThat.lastRouteStaName != vm.lastRouteStaName) {
                        stationName = itemssThat.lastRouteStaName;
                    } else {
                        stationName = tripEndTime;
                    }
                }
            }

            return stationName;
        },
        //显示班次信息
        showScheduleTips() {
            let id = event.target.id;
            var idArray = id.split("_");
            var index = idArray[0];
            var j = idArray[1];
            var line = vm.infoList[index][j];
            var firstRouteStaName = line.firstRouteStaName == null ? "" : line.firstRouteStaName;
            var lastRouteStaName = line.lastRouteStaName == null ? "" : line.lastRouteStaName;
            var tripBeginTime = line.tripBeginTime == null ? "" : line.tripBeginTime;
            var tripEndTime = line.tripEndTime == null ? "" : line.tripEndTime;
            var serviceName = line.serviceName == null ? "" : line.serviceName;
            var serviceType = line.serviceType == null ? "" : line.serviceType;
            var title = (line.firstDirection=='0'?'上':'下')+(line.busNumber)+'('+(line.busName == null?'未挂车':line.busName)+')';
            var content = '<div>' + title + '</div>' +
                '<div>任务类型：' + serviceName + '</div>' +
                '<div>开始站点：' + firstRouteStaName + '</div>' +
                '<div>结束站点：' + lastRouteStaName + '</div>' +
                '<div>开始时间：' + tripBeginTime + '</div>' +
                '<div>结束时间：' + tripEndTime + '</div>';
            if(serviceType == -32 || serviceType == -78) {
                content += '<div>' + serviceName + '时长：';
            } else {
                content += '<div>运营时长：';
            }
            if (tripBeginTime == "" || tripEndTime == "") {
                content += '</div>';
            } else {
                var runTime = Math.round((new Date(tripEndTime).getTime() - new Date(tripBeginTime).getTime()) /60000);
                content += (runTime + '分钟</div>');
            }
            layer.tips(content, "#" + event.target.id, {
                tips: [1, "#000"], area: ['250px', 'auto']
            });
        },
        //隐藏班次信息
        hideScheduleTips() {
            let that = this;
            layer.closeAll();
        },
        showLineTips() {
            let id = event.target.id;
            var idArray = id.split("_");
            var j = idArray[1];
            var line = vm.infoList[j][0];
            var totalTime = line.totalTime == null ? "" : line.totalTime;
            var runTime = line.runTime == null ? "" : line.runTime;
            var stopTime = line.stopTime == null ? "" : line.stopTime;
            var count = line.count == null ? "" : line.count;
            var title = (line.firstDirection=='0'?'上':'下')+(line.busNumber)+'('+(line.busName == null?'未挂车':line.busName)+')';
            var content = '<div>' + title + '</div>' +
                '<div>总行车时间：' + totalTime + '小时</div>' +
                '<div>总营运时间：' + runTime + '小时</div>' +
                '<div>总停站时间：' + stopTime + '小时</div>' +
                '<div>运营班次：' + count + '</div>';
            layer.tips(content, "#" + event.target.id, {
                tips: [1, "#000"], area: ['250px', 'auto']
            });
        },
        //获取首末站点名称
        getFirstLastRouteStaName(routeId) {
            let that = this;
            //线路站点-查询线路站点
            var path = "/routeStation/getListByRouteId/" + routeId;
            var success = function(data) {
                console.log(data);
                if (data.code != 0) {
                    return;
                }
                var allArr = data.data;
                $.each(allArr, function (i, value) {
                    if (value.stationMark == 0) {
                        vm.firstRouteStaName = value.routeStationName;
                    } else if (value.stationMark == 3) {
                        vm.lastRouteStaName = value.routeStationName;

                    }

                })
            };

            ajaxClient2(path, "get", null, success);
        }
    },
    created() {
        this.createdTimeList();
        // this.takeData();
    },
    mounted() {
        // this.cssData();
    }
})