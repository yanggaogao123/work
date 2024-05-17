
$(function () {
    var searchPattern = new RegExp('');
    var mavRouteCountMeassage = '最多选择?条线路';
    var minRouteCountMeassage = '最少选择?条线路';
    var $selected ;
    var $routes;
    var routeList ={};

    /**
     * 默认值 defaultData
     * @param organIdList
     * @param maxRouteCount
     * @param minRouteCount
     * @param buttonHandler
     * */
    var routeOrganSelect = function (params) {
    	var defaultData = {
                organIdList:0,
    	        maxRouteCount:1,
    	        minRouteCount:1,
    	        buttonHandlerContext:null,
    	        buttonHandler:function (routes) {
    	            console.log(routes);
    	        },
    	    };
    	$.extend(defaultData,params);

        var $home = $('<div/>').appendTo($('body')).window({
            width:600,
            top:0,
            left:0,
            title:'选择线路',
            closed:true,
            resizable:false,
            draggable:false,
            collapsible:false,
            minimizable:false,
            maximizable:false,
            modal:true
        });


        //组装机构
        var organTree = $('<div style="margin-top:20px ;margin-left:90px;"><select id="organTree" class="easyui-combotree" style="width:400px;"> </div> </select>')
            .appendTo($home);
        $($('.window-body')[0]).css({ "background-color": "#EFF5FF"}); //背景颜色
        var userOrganId = $('#organId').html();
        $.ajax({
            type:"get",
            url:getRootPath()+'/routeGenerate/organTree',
            success:function(json){
                var organTreedata =JSON.parse(json.data)
                console.log(organTreedata)
                $('#organTree').combotree({
                    value:userOrganId,
                    multiple: true, //这个选项设置多选功能
                    data:JSON.parse(json.data),
                    onChange:function(newValue, oldValue){
                        var organIdArray=$("#organTree").combotree("getValues") ;
                        var thisOrganTreedata =[];
                        thisOrganTreedata = $.extend(true,thisOrganTreedata, organTreedata); //深度拷贝
                        var targetOrganIdArray =myOrganTree.getTargetOrganIds(organIdArray,thisOrganTreedata);
                        defaultData.organIdList =targetOrganIdArray.toString();
                        buildRoute(defaultData,false);
                    },

                });


            }
        });

        var searchDiv = $('<div class="route_select_div route_select_search"/>')
            .append($('<span/>').text('请输入过滤条件:'))
            .appendTo($home);
        //选择线路框
        $('<input type="text"/>').keyup(function () {
            searchPattern = new RegExp($(this).val());
            $.each(window.routeList,function (index, elem) {
                var organIdArray =$("#organTree").combotree("getValues");
                //没有选择机构
                if(organIdArray.length==0) return ;
                if (!searchPattern.test(elem.routeName)) {//不匹配
                    elem.$div.hide();
                } else if (selectedRoute[elem.routeId] === undefined) {//匹配，并且，没有被选中
                    elem.$div.show();
                }
            });
        }).appendTo(searchDiv);
        //发送
        $('<input type="button" class="button_submit_normal"/>').click(function () {
            var selectedRouteList = [];
            var $route;
            //保证选择顺序
            $selected.children().each(function (index, elem) {
                $route = $(this);
                selectedRouteList.push({
                    routeName:$route.attr('routeName'),
                    routeId:$route.attr('routeId')
                });
            });
            if (selectedRouteList.length == 0) {
                alert('请选择线路');
            } else if (selectedRouteList.length > defaultData.maxRouteCount) {
                alert(mavRouteCountMeassage.replace('?', defaultData.maxRouteCount));
            } else if (selectedRouteList.length < defaultData.minRouteCount) {
                alert(minRouteCountMeassage.replace('?', defaultData.minRouteCount));
            } else {
                defaultData.buttonHandler.call(defaultData.buttonHandlerContext, selectedRouteList);
                $home.window('close');
            }

        }).appendTo(searchDiv);


        $selected = $('<div class="route_select_div route_select_selected"/>').appendTo($home);
        $routes = $('<div class="route_select_div route_select_routes"/>').appendTo($home);
            // routeList = [],
        var     selectedRoute = {},
            $routeCheckboxObj = {};
        //组装线路多选框
        buildRoute(defaultData,true);

        return {
        	open:function (routeIdStr) {
        		$selected.children().each(function (index, elem) {
                    $(this).children(':first').trigger('click');
                });
        		if(routeIdStr){
                    var routeIdArray = routeIdStr.split(',');
        			$.each(routeIdArray,function(index,elem){
        			    if($routeCheckboxObj[elem]!=undefined){
                            $routeCheckboxObj[elem].trigger('click');
                        }
        			});
        		}
	            $home.window('open');
	        }
        };
        //组装线路多选框
        function buildRoute(defaultData,firstFlag) {
            //没选择机构下
            if(defaultData.organIdList.length==0){
                $.each(window.routeList,function (index,route) {
                    $(".route_select_selected [routeid="+route.routeId+"]").remove();//删除已勾选
                    $(".route_select_routes [routeid="+route.routeId+"]").hide();//隐藏待勾选
                    return;
                });
                return;
            }
            $.ajax({
                type:"post",
                url:getRootPath() + '/routeSchedulePlan/getOrganScheduleRouteList',
                data:urlEncode(defaultData).slice(1),
                dataType:"json",
                async:false,
                success:function (json) {
                    if (json.code==0 && $.type(json.data) === 'array') {

                        var routeIdSelected=[]; //已经选择线路集合
                        var routes =[]; //可以选择的线路集合
                        $selected.children().each(function (index, elem) {
                            $route = $(this);
                            routeIdSelected.push($route.attr('routeId'));
                        });
                        $routes.children().each(function (index, elem) {
                            $route = $(this);
                            routes.push($route.attr('routeId'));
                        });
                        //第一次加载
                        if(firstFlag){
                            window.routeList = json.data;
                            $.each(json.data,function (i,elem) {
                                $routeCheckboxObj[elem.routeId] = $('<input type="checkbox"/>').click(function () {
                                    selectedAdd(elem);
                                    return false;
                                });
                                elem.$div = $('<div class="route_select_route"/>')
                                    .attr('routeId', elem.routeId)
                                    .attr('routeName', elem.routeName)
                                    .attr('title', elem.routeName)
                                    .append($routeCheckboxObj[elem.routeId]).append(elem.routeName).appendTo($routes);
                            });
                        }else{
                            var thisRouteList =json.data;
                            $.each(window.routeList,function (index, route) {
                                var existCheck = false; //是否已经勾选区域
                                var existWait = false; //是否存在待勾选区域
                                $.each(thisRouteList,function (i,elem) {
                                    if(route.routeId==elem.routeId){
                                        existWait =true;
                                        $.each(routeIdSelected,function (j,routeId) {
                                            if(route.routeId==routeId){
                                                existCheck =true;
                                            }
                                        });

                                    }
                                });
                                if(!existCheck){
                                    $(".route_select_selected [routeid="+route.routeId+"]").remove();//删除已经勾选
                                }
                                if (existWait&&!existCheck){
                                    $('.route_select_routes [routeid="'+route.routeId+'"]').show() ; //显示待勾选
                                }else{
                                    $('.route_select_routes [routeid="'+route.routeId+'"]').hide() ; //隐藏待勾选
                                }
                            });
                        }


                    } else {
                        alert(data.msg);
                    }
                }
            });
        }

        function selectedAdd(route) {
            if (defaultData.maxRouteCount == 1) {
                for (var key in selectedRoute) {
                    selectedRemove(selectedRoute[key]);
                }
            }else {
                var checkedCount = 1;
                for(var key in selectedRoute){
                    checkedCount++;
                }
                if (checkedCount > defaultData.maxRouteCount) {
                    alert(mavRouteCountMeassage.replace('?', defaultData.maxRouteCount));
                    return;
                }
            }
            selectedRoute[route.routeId] = route;
            route.$div.hide();
            route.$selectDiv = $('<div class="route_select_route"/>')
                .attr('routeId', route.routeId)
                .attr('routeName', route.routeName)
                .attr('title', route.routeName)
                .append($('<input type="checkbox"/>').attr('checked', 'checked').click(function () {
                    selectedRemove(route);
                })).append(route.routeName)
                .appendTo($selected);
        }

        function selectedRemove(route) {
            delete selectedRoute[route.routeId];
            route.$selectDiv.remove();
            if (searchPattern.test(route.routeName)&& route.$div!=null) {//匹配搜索框的值的显示
                route.$div.show();
            }
        }
    };
    $.extend({
        routeOrganSelect : routeOrganSelect
	});
});
