//配车计划配置
var vehiclePlanModule = function () {

    //限制重复点击生成排班计划的时间标志
    var timeoutflag = null;

    //当前选择的是模板配车还是自定义配车 1：自定义配车 2：模板配车
    var setCarFlag = 1;

    var routeStaCompete = [];
    var routeStaCompeteUp = [];
    var routeStaCompeteDown = [];

    var busNumberConfigData = {
        upFirstBusNumberOptimal: "",//行出车配车数(最优)
        downFirstBusNumberOptimal: "",//下行出车配车数(最优)
        singleBusNumberOptimalUp: "",//上行单班车最优配车数
        singleBusNumberOptimalDown: "",//下行单班车最优配车数

        upFirstBusNumberPreset: "",//上行出车配车数(预设)
        downFirstBusNumberPreset: "",//下行出车配车数(预设)
        singleBusNumberPresetUp: "",//上行单班车预设配车数
        singleBusNumberPresetDown: "",//下行单班车预设配车数
    };

    //打开配车计划弹出框
    var showVehiclePlanDialog = function () {
        if ($('#vehiclePlanDialog')) {
            $('#vehiclePlanDialog').dialog("destroy");
        }
        var routeId = $('#routeSelect').val();
        var planDate = $('#runDate').datebox("getValue");

        if (routeId == null || routeId == '' || runDate == null || runDate == '') {
            $.messager.alert('消息', '请选择参数！', 'info');
            return;
        }

        var routeName = $('#routeSelect').select2('data').text;
        var dayNum = new Date(planDate).getDay();
        dayNum = getWeekDayStr(dayNum);

        // vehiclePlanModule.selectByRouteIdAndPlanDate(routeId, planDate);//配车配置表查詢

        var html = '<div id="vehiclePlanDialog"><div style="margin-top: 5%">' +
            '<button onclick="vehiclePlanModule.toLastDay()" style="margin-left:20px;margin-right: 20px;float: left">上一天</button>' +
            '<span id="vehicleTitle" style="margin-left: 11%;font-size: large;">线路：' + routeName + '     日期：' + planDate + '    ' + dayNum + '<span/>' +
            '<button onclick="vehiclePlanModule.toNextDay()" style="margin-left: 20px;margin-right: 20px;float: right">下一天</button></div>' +
            '<br><div>&nbsp;&nbsp;' +
            // '竞争线路：<div id="routeSelectCompete"></div>' +
            '<label id="festivalLabel" style="margin-left: 20px">客流参考日期：<input type="text" id="referenceRunDate"  style="width:100px;margin-right: 30px" /></label>' +
            '</div>' +
            '<fieldset style="margin: 30px 10px 10px;border: 2px solid #b6c3c3;"  id="optimalSettingField" >' +
            // '<legend style="font-size:17px;"><B>最优配车设置</B></legend>' +
            '<form name="optimalSettingForm" >' +
            '<table style="width:100%;">' +
            /*'<tr>' +
            '<td style="text-align: right;width: 20%;">上行总配车数：</td><td style="width: 16%;"><input type="text" disabled style="width: 80px; border: 0px;" id ="upFirstBusNumberOptimal" value="' + busNumberConfigData.upFirstBusNumberOptimal + '" onchange="onlyNumberGEZero(this);" /></td>' +
            '<td style="text-align: right;width: 30%;">下行总配车数：</td><td style="width: 16%;"><input type="text" disabled style="width: 80px; border: 0px;" id="downFirstBusNumberOptimal" value="' + busNumberConfigData.downFirstBusNumberOptimal + '" onchange="onlyNumberGEZero(this);" /></td>' +
            '<tr>' +
            '<td style="text-align: right;width: 20%;">上行单班配车数：</td><td style="width: 16%;"><input type="text" disabled style="width: 80px; border: 0px;" id="singleBusNumberOptimalUp" value="' + busNumberConfigData.singleBusNumberOptimalUp + '" onchange="onlyNumberGEZero(this);" /></td>' +
            '<td style="text-align: right;width: 30%;">下行单班配车数：</td><td style="width: 16%;"><input type="text" disabled style="width: 80px; border: 0px;" id="singleBusNumberOptimalDown" value="' + busNumberConfigData.singleBusNumberOptimalDown + '" onchange="onlyNumberGEZero(this);" /></td>' +
            '<tr>' +*/
            '<td style="text-align: right;width: 20%;"></td><td style="width: 16%;" ></td>' +
            '<td style="text-align: right;width: 30%;"></td><td style="width: 16%;" ><button type="button" onclick="vehiclePlanModule.optimalGenerate();" id="optimalGenerate">生成排班</button></td>' +
            '</table>' +
            '</form>' +
            '</fieldset>' +
            //最优配车设置end
            //预设配车设置start
            '</div>';

        $('body').append(html);



        $('#vehiclePlanDialog').dialog({
            title: '配车计划配置',
            width: 700,
            height: 400,
            closed: false,
            cache: false,
            modal: true,
            buttons: [{
                text: '关闭',
                handler: function () {
                    busNumberConfigData = {
                        upFirstBusNumberOptimal: "",//行出车配车数(最优)
                        downFirstBusNumberOptimal: "",//下行出车配车数(最优)
                        singleBusNumberOptimalUp: "",//上行单班车最优配车数
                        singleBusNumberOptimalDown: "",//下行单班车最优配车数

                        upFirstBusNumberPreset: "",//上行出车配车数(预设)
                        downFirstBusNumberPreset: "",//下行出车配车数(预设)
                        singleBusNumberPresetUp: "",//上行单班车预设配车数
                        singleBusNumberPresetDown: "",//下行单班车预设配车数
                    };

                    $('#vehiclePlanDialog').dialog("destroy");
                    scheduleTripModule.buildDatagrid();
                }
            }]
        });

        $('.panel-tool-close').click(function () {
            busNumberConfigData = {
                upFirstBusNumberOptimal: "",//行出车配车数(最优)
                downFirstBusNumberOptimal: "",//下行出车配车数(最优)
                singleBusNumberOptimalUp: "",//上行单班车最优配车数
                singleBusNumberOptimalDown: "",//下行单班车最优配车数

                upFirstBusNumberPreset: "",//上行出车配车数(预设)
                downFirstBusNumberPreset: "",//下行出车配车数(预设)
                singleBusNumberPresetUp: "",//上行单班车预设配车数
                singleBusNumberPresetDown: "",//下行单班车预设配车数
            };
        });

        var today = new Date(planDate);
        today.setDate(today.getDate() - 7);
        var todayStr = today.format("yyyy-MM-dd");

        $('#referenceRunDate').datebox({required: true});
        today = new Date();
        today.setDate(today.getDate() - 4);
        $('#referenceRunDate').datetimebox("calendar").calendar({
            validator: function (date) {
                return date.getTime() <= today.getTime();
            }
        });

        var timeStamp = today.getTime();
        var planTimeStamp = new Date(planDate).getTime() - 7 * 24 * 60 * 60 * 1000;

        if (planTimeStamp > timeStamp) {
            var day = Math.ceil((planTimeStamp - timeStamp) / (24 * 60 * 60 * 1000));
            day = Math.ceil(day / 7) * 7;
            var stampDate = new Date(planTimeStamp);
            stampDate.setDate(stampDate.getDate() - day);
            todayStr = stampDate.format("yyyy-MM-dd");
        }

        $('#referenceRunDate').datebox("setValue", todayStr);

        /*$("#routeSelectCompete").select2({
            placeholder: '请选择线路',
            width: 150,
            data: routeDateAll 
        });*/

    };

    function getLastYearYestoday(date) {
        var strYear = date.getFullYear() - 1;
        var strDay = date.getDate();
        var strMonth = date.getMonth() + 1;
        if (strMonth < 10) {
            strMonth = "0" + strMonth;
        }
        if (strDay < 10) {
            strDay = "0" + strDay;
        }
        datastr = strYear + "-" + strMonth + "-" + strDay;
        return datastr;
    }

    var toLastDay = function () {
        changeDay(-1);
    };

    var toNextDay = function () {
        changeDay(1);
    };

    function changeDay(day) {

        var runDate = $('#runDate').datebox("getValue");

        var today = new Date(runDate);

        today.setDate(today.getDate() + day);
        var todayStr = today.format("yyyy-MM-dd");


        $('#runDate').datebox("setValue", todayStr);

        busNumberConfigData = {
            upFirstBusNumberOptimal: "",//行出车配车数(最优)
            downFirstBusNumberOptimal: "",//下行出车配车数(最优)
            singleBusNumberOptimalUp: "",//上行单班车最优配车数
            singleBusNumberOptimalDown: "",//下行单班车最优配车数

            upFirstBusNumberPreset: "",//上行出车配车数(预设)
            downFirstBusNumberPreset: "",//下行出车配车数(预设)
            singleBusNumberPresetUp: "",//上行单班车预设配车数
            singleBusNumberPresetDown: "",//下行单班车预设配车数
        };

        $('#vehiclePlanDialog').dialog("destroy");
        showVehiclePlanDialog();
    }

    function getWeekDayStr(i) {
        var result;
        switch (i) {
            case 0:
                result = '星期天';
                break;
            case 1:
                result = '星期一';
                break;
            case 2:
                result = '星期二';
                break;
            case 3:
                result = '星期三';
                break;
            case 4:
                result = '星期四';
                break;
            case 5:
                result = '星期五';
                break;
            case 6:
                result = '星期六';
                break;
            default:
                break;
        }
        return result;
    }


    //最优配车生成
    var optimalGenerate = function () {
        $('#optimalGenerate').attr("disabled", true);
        scheduleTripModule.generateTest();
    }
    //配车配置表查詢
    var selectByRouteIdAndPlanDate = function (routeId, planDate) {
        var path = "/busNumberConfig/selectByRouteIdAndPlanDate";
        var success = function (json) {
            if (json.code !== 0) {
                alert(json.retMsg);
                return;
            }

            if (json.data != null) {
                var data = json.data;
                //最优
                $('#upFirstBusNumberOptimal').val(data.upFirstBusNumberOptimal == null ? "" : data.upFirstBusNumberOptimal);
                $('#downFirstBusNumberOptimal').val(data.downFirstBusNumberOptimal == null ? "" : data.downFirstBusNumberOptimal);
                $('#singleBusNumberOptimalUp').val(data.singleBusNumberOptimalUp == null ? "" : data.singleBusNumberOptimalUp);
                $('#singleBusNumberOptimalDown').val(data.singleBusNumberOptimalDown == null ? "" : data.singleBusNumberOptimalDown);

                busNumberConfigData = data;

            }
        };

        var data = {routeId: routeId, planDate: planDate};

        ajaxClient3(path, "get", data, success, 10000, false);
    };

    return {
        showVehiclePlanDialog: showVehiclePlanDialog,
        busNumberConfigData: busNumberConfigData,
        optimalGenerate: optimalGenerate,
        selectByRouteIdAndPlanDate: selectByRouteIdAndPlanDate,
        setCarFlag: setCarFlag,
        toNextDay: toNextDay,
        toLastDay: toLastDay,
    };

}();

