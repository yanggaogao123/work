/**
 *  排班计划
 */
var scheduleTripModule = function () {

    var str = '';
    var busNameOld = ""; //旧的busName
    var regex1 = /\((.+?)\)/g;   // () 小括号
    var regex2 = /\[(.+?)\]/g;   // [] 中括号
    var regex3 = /\{(.+?)\}/g;  // {} 花括号，大括号
    var regex4 = /\（(.+?)\）/g;   // （）中文括号

    //展示排班计划列表
    var buildDatagrid = function () {


        var routeId = $('#routeSelect').val();
        var runDate = $('#runDate').datebox("getValue");
        var planType = 3;

        if (routeId == null || routeId == '' || runDate == null || runDate == '') {
            $.messager.alert('消息', '请选择参数！', 'info');
            return;
        } else {
            runDate += " 00:00:00";
        }

        //如果在22点30分前修改计划或挂车，则需要重新同步当天的计划，如果在22点30分之后，则是明天的计划
        var today = new Date();
        var hour = today.getHours();
        var minute = today.getMinutes();

        if ($('#syncPlanInput') != undefined) {
            $('#syncPlanInput').remove();
        }
        var i = 0;
        if (hour > 22) {
            i = 1;
            today.setDate(today.getDate() + i);
        } else if (hour === 22) {
            if (minute > 30) {
                i = 1;
                today.setDate(today.getDate() + i);
            }
        }

        var syncPlanInput = '<input type="button" id="syncPlanInput" value="同步计划" onclick="scheduleTripModule.syncPlan(' + i + ')"/>';

        var todayStr = today.format("yyyy-MM-dd") + ' 00:00:00';
        if (runDate === todayStr) {
            $('.headMenu').append(syncPlanInput);
        }

       /* //获取首末站 start
        var firstStationNameUp, firstStationNameDown;
        var baseDto = ajaxClientRest('/route/getRouteUpDownInfo/' + routeId);
        if (baseDto != null && baseDto.data != null) {
            $.each(baseDto.data, function (i) {
                if (baseDto.data[i].direction == 0) {
                    firstStationNameUp = baseDto.data[i].firstStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
                } else if (baseDto.data[i].direction = 1) {
                    firstStationNameDown = baseDto.data[i].firstStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");

                }
            })
        }
        //获取首末站 end

        //调用另一个接口获取首末站，使用ID判断是否短线
        var upStationId, downStationId;
        var result = ajaxClientRest('/route/getRouteUpDownInfo2/' + routeId);
        if (result.code == 0) {
            upStationId = result.stationInfo.up.routeStationId;
            downStationId = result.stationInfo.down.routeStationId;
        }*/

        //获取列表
        var ruleData = {};
        var max = 0;

        $.ajax({
            method: "POST",
            url: "./schedule/getScheduleBySort",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "routeId": routeId,
                "runDate": runDate,
                "planType": planType
            }),
            dataType: "json",
            timeout: 10000, //超时时间设置，单位毫秒
            async: false,
            success: function (res) {
                if (res.retCode === 0) {
                    ruleData = res.data;
                    if (res.data.scheduleBusList != undefined && res.data.scheduleBusList != null && res.data.scheduleBusList.length > 0) {
                        $.each(res.data.scheduleBusList, function (i, rule) {
                            if (rule.scheduleList.length > max)
                                max = rule.scheduleList.length;

                        });
                    } else {
                        //清空列表
                        $('#datagrid').datagrid('loadData', []);
                        $('#census').html("");
                        $.messager.alert('消息', '没有数据,请按[生成]按钮！', 'info');
                        return;
                    }
                } else {
                    $('#datagrid').datagrid('loadData', []);
                    $('#census').html("");
                    $.messager.alert('消息', res.retMsg, 'info');
                }
            },
            error: function (res) {
                alert("排班计划查询有误");
            }
        });

        var ruleColumnsHead = [];

        //创建两个终点车站
        for (var i = 0; i < ruleData.firstRouteStaList.length; i++) {
            var routeStationName = ruleData.firstRouteStaList[i].routeStationName;
            //因为后台是从下行开始遍历
            /*if (routeStationName == null) {
                routeStationName = firstStationNameDown;
            }*/
            routeStationName = routeStationName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
            if (routeStationName.length > 10) {
                routeStationName = routeStationName.substr(0, 10);
            }
            ruleColumnsHead.push({
                title: routeStationName,
                rowspan: 1,
                colspan: 2,
                align: 'center',
                width: fixWidth(0.7 / max),
            });

        }

        var ruleColumnsMain = [];
        for (var i = 1; i <= max; i++) {

            ruleColumnsMain.push(
                {
                    field: '100' + i, 'title': '开始时间', align: 'center', formatter: function (value, row) {
                        var a = Number(this.field.replace('100', '')) - 1;
                        if (row.scheduleList[a] && row.scheduleList[a].tripBeginTime != null) {
                            var text = row.scheduleList[a].tripBeginTime.substr(11, 5);
                            /*var lastRouteStaName = row.scheduleList[a].lastRouteStaName;
                            var lastRouteStaId = row.scheduleList[a].lastRouteStaId;
                            lastRouteStaName = lastRouteStaName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
                            if ((lastRouteStaName != firstStationNameUp && lastRouteStaName != firstStationNameDown) && (lastRouteStaId != upStationId && lastRouteStaId != downStationId)) {
                                text += row.scheduleList[a].lastRouteStaName;
                            }*/
                            return text;
                        } else {
                            return '';
                        }
                    }
                },
                {
                    field: '200' + i, 'title': '结束时间', align: 'center', formatter: function (value, row) {
                        var b = Number(this.field.replace('200', '')) - 1;
                        if (row.scheduleList[b] && row.scheduleList[b].tripEndTime != null) {
                            var text = row.scheduleList[b].tripEndTime.substr(11, 5);
                            // var firstRouteStaName = row.scheduleList[b].firstRouteStaName ;
                            // firstRouteStaName = firstRouteStaName.replace(regex1,"").replace(regex2,"").replace(regex3,"").replace(regex4,"").replace(/[0-9]/g, "");
                            // if(firstRouteStaName!=firstStationNameUp&&firstRouteStaName!=firstStationNameDown){
                            //     text+=row.scheduleList[b].lastRouteStaName;
                            // }
                            return text;
                        } else {
                            return '';
                        }
                    }
                }
            );

        }
        var ruleColumns = [ruleColumnsHead, ruleColumnsMain];

        //车号固定 start
        var frozenColumnsHead = [
            {title: '车号', rowspan: 1, colspan: 1, align: 'center', width: fixWidth(0.7 / max)},
            {title: '[--车辆名称--]', rowspan: 1, colspan: 1, align: 'center', width: fixWidth(0.7 / max)}
           /* {title: '总行车时间', rowspan: 1, colspan: 1, align: 'center', width: fixWidth(0.7 / max)},
            {title: '总营运时间', rowspan: 1, colspan: 1, align: 'center', width: fixWidth(0.7 / max)},
            {title: '停站时间', rowspan: 1, colspan: 1, align: 'center', width: fixWidth(0.7 / max)}*/
        ];
        var colNum = 0;
        var frozenColumnsMain = [
            {
                field: '3001', align: 'center', formatter: function (value, row) {
                    return row.busNameFull;
                }
            }, {
                field: '3002', align: 'center', formatter: function (value, row) {
                    colNum++;
                    var thisId = "middle_" + colNum;
                    var busName = row.busName == null ? "" : row.busName;
                    var html = '<input type="button" id="middle_' + colNum + '"  onclick="scheduleTripModule.buildBusSelect(event)"style="width: 100%;border:0px;" ' +
                        ' firstDirection ="' + row.firstDirection + '"  firstDirection firstBusNumber ="' + row.firstBusNumber + '"  value="' + busName + '" / >';
                    return html;
                }
            }/*, {
                field: '3003', align: 'center', formatter: function (value, row) {
                    return row.totalDuration;
                }
            }, {
                field: '3004', align: 'center', formatter: function (value, row) {
                    return row.runDuration;
                }
            }, {
                field: '3005', align: 'center', formatter: function (value, row) {
                    return row.anchorDuration;
                }
            }*/];
        var frozenColumns = [frozenColumnsHead, frozenColumnsMain]
        //车号固定 end


        $('#datagrid').datagrid({
            //title: "排班计划(当前为" + ruleData.planType + "计划，最后一次生成计划的时间为" + ruleData.updateTime + ")",
            title: "排班计划",
            rownumbers: false,
            fitColumns: true,
            pagination: false,
            // data:ruleData,
            data: ruleData.scheduleBusList,
            frozenColumns: frozenColumns, //固定列
            columns: ruleColumns, //普通列
            showFooter: true
        });

        /*str = "统计参数:线路总营运里程 <b>" + ruleData.runMileage + "</b> 公里,高峰营运里程 <b>" + ruleData.highPeakMileage + "</b> 公里,总车次 <b>" +
            ruleData.runBusNum + "</b> 次,短线车次 <b>" + ruleData.shortLineBusCount + "</b> 次,高峰车次占比 <b>" + ruleData.highPeakBusNum + "</b>/<b>" + ruleData.runBusNum + "</b> (<b>" + percentNum(ruleData.highPeakBusNum, ruleData.runBusNum) + "</b>) ,最长总行车时长的车 <b>" +
            ruleData.busNameMaxTime + "</b> ,时间是 <b>" + ruleData.totalTimeMax + "</b> 小时,最短总行车时长的车 <b>" + ruleData.busNameMinTime + "</b> ,时间是 <b>" +
            ruleData.totalTimeMin + "</b> 小时,计划配车数 <b>" + ruleData.planBusNum + "</b> 辆车,其中单班车数为 <b>" + ruleData.singleBusNum + "</b> 辆,已经挂载了 <b>" + ruleData.busNum + "</b> 辆车";

        $('#census').html(str);

        //非全程的站点背景
        var titleGroup = $('.datagrid-cell-group');
        for (i = 5; i < titleGroup.length; i++) {
            var routeStaName = $(titleGroup[i]).html();
            routeStaName = routeStaName.replace(regex1, "").replace(regex2, "").replace(regex3, "").replace(regex4, "").replace(/[0-9]/g, "");
            if (firstStationNameUp.length > 10 || firstStationNameDown.length > 10) {
                if (firstStationNameUp.substr(0, 10) !== routeStaName && firstStationNameDown.substr(0, 10) !== routeStaName) {
                    $(titleGroup[i]).css({"background-color": "#0FE9E0"})
                }
            } else {
                if (routeStaName != firstStationNameUp && routeStaName != firstStationNameDown) {
                    $(titleGroup[i]).css({"background-color": "#0FE9E0"})
                }
            }
        }*/


        function percentNum(num, num2) {
            return (Math.round(num / num2 * 10000) / 100.00 + "%"); //小数点后两位百分比
        }

    }



    //datagrid宽度支持百分比形式
    function fixWidth(percent) {
        return document.body.clientWidth * percent; //这里你可以自己做调整
    }


    //生成排班计划(新)
    var generateTest = function () {
        var routeId = $('#routeSelect').val();
        var runDate = $('#runDate').datebox("getValue");

        if (routeId == null || routeId == '' || runDate == null || runDate == '') {
            $.messager.alert('消息', '请选择参数！', 'info');
            $('#optimalGenerate').attr("disabled", false);
            return;
        } else {
            runDate += " 00:00:00";
        }
        var requestData;
        var festivalDay = $('#referenceRunDate').datebox("getValue");

        /*var routeIdCompete = $('#routeSelectCompete').val();
        if (routeIdCompete == null || routeIdCompete === '') {
            $.messager.alert('消息', '请选择竞争线路！', 'info');
            $('#optimalGenerate').attr("disabled", false);
            return;
        }*/

        requestData = {
            "routeId": routeId,
            "runDate": runDate,
            "passengerData": festivalDay
            // "comRouteIds" : routeIdCompete
        };

        //生成
        $.ajax({
            method: "POST",
            url: "./schedule/generateSupportSchedule",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(requestData),
            dataType: "json",
            timeout: 600000, //超时时间设置，单位毫秒
            async: true,
            success: function (res) {
                if (res.code !== 0) {
                    alert('生成失败(新)！' + res.retMsg);
                    $('#optimalGenerate').attr("disabled", false);
                } else {
                    //配车配置表查詢
                    // vehiclePlanModule.selectByRouteIdAndPlanDate(routeId, runDate);
                    alert("生成成功！");
                    $('#optimalGenerate').attr("disabled", false);
                }

            },
            error: function (res) {
                alert("生成请求失败(新)！")
                $('#optimalGenerate').attr("disabled", false);
            }
        });

    }


    //挂车
    function mountBus() {

    }



    return {
        buildDatagrid: buildDatagrid,
        mountBus: mountBus,
        generateTest: generateTest
    };

}();