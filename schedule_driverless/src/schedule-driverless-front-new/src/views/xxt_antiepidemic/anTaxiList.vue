<template>
    <div id="anTaxiList">
        <ul id="wrap" v-cloak>
            <div id="container" hidden></div>
            <li v-for="(item,index) in list">
                <div class="area">{{item.area}}</div>
                <div class="address">{{item.address}}</div>
                <div class="time">服务时间{{item.serviceTime}}</div>
                <div class="btn-go" @click="goHere(item)">
                    <img src="../../assets/disinfectionList/go.png" alt="">
                </div>
                <div class="dis" v-if="item.dis">距离{{item.dis/1000}}公里</div>
            </li>
        </ul>
    </div>
</template>

<script>
    import {
        disinfectionList
    } from "@/api/api";
    import axios from 'axios';
    // import Toast from 'path/to/@vant/weapp/dist/toast/toast';
    export default {
        name: 'anTaxiList',
        data() {
            return {
                map: "",
                geolocation: "",
                // localLng: '113.28227',
                // localLat: '23.12566',
                localLng: '',
                localLat: '',
                list: [],
            }
        },
        created() {

        },
        mounted() {
            this.mapInit();
            this.getList();
        },
        methods: {
            mapInit() {
                let that = this;
                this.map = new AMap.Map('container', {
                    resizeEnable: true,
                });

                //生成定位
                AMap.plugin('AMap.Geolocation', function() {
                    that.geolocation = new AMap.Geolocation({
                        enableHighAccuracy: true, //是否使用高精度定位，默认:true
                        timeout: 2000, //超过10秒后停止定位，默认：无穷大
                        // buttonOffset: new AMap.Pixel(10, 120), //定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
                        panToLocation: true,
                        zoomToAccuracy: true
                    });
                    console.log(geolocation);
                    geolocation.getCurrentPosition();
                    AMap.event.addListener(geolocation, 'complete', onComplete); //返回定位信息
                    AMap.event.addListener(geolocation, 'error', onError); //返回定位出错信息
                });
                this.geoLocation();
                // that.geoLocation();
                // console.log(this.map);

                //解析定位结果
                function onComplete(data) {
                    console.log(3)
                    console.log(data);
                    that.localLat = data.position.lat;
                    that.localLng = data.position.lng;
                    that.getList();
                    console.log(5)
                }

                //解析定位错误信息
                function onError(data) {
                    that.$toast.fail('定位失败，请重试');
                    // my_loc = null;
                    // $("#all-loading").hide();
                    // layer.closeAll();
                }
            },
            geoLocation() {
                let that = this;
                    //优先通过android和IOS获取用户坐标
                    var json = common.getUserInfo();
                    console.log(1)
                    console.log(json)
                    console.log(2)
                    if (json && json.lng && json.lat) {
                        var data = new Object();
                        data.position = {
                            lat: json.lat - 0.002706,
                            lng: json.lng + 0.005306,
                            getLat: function() {
                                return json.lat;
                            },
                            getLng: function() {
                                return json.lng;
                            }
                        };
                        this.localLat = json.lat - 0.002706;
                        this.localLng = json.lng + 0.005306;
                        this.version = json.version
                        data.accuracy = 0;
                        data.gps = 1;
                        if (typeof onComplete == "function") {
                            onComplete(data);
                        }
                    } else {
                        if (typeof AMap == "undefined") {
                            if (typeof onComplete == "function") {
                                onComplete();
                            }
                            return;
                        }
                        console.log(this.map);
                        that.geolocation.getCurrentPosition();
                    }
                },
            getRad(d) {
                return (d * Math.PI) / 180.0;
            },
            mapDistance(longitude, latitude) {
                var radLat1 = this.getRad(this.localLat);
                var radLat2 = this.getRad(latitude);

                var a = radLat1 - radLat2;
                var b = this.getRad(this.localLng) - this.getRad(longitude);

                var s =
                    2 *
                    Math.asin(
                        Math.sqrt(
                            Math.pow(Math.sin(a / 2), 2) +
                            Math.cos(radLat1) *
                            Math.cos(radLat2) *
                            Math.pow(Math.sin(b / 2), 2)
                        )
                    );
                s = s * 6378137.0;
                return Math.round(s);
            },
            goHere(data) {
                console.log(data);
                // let that = this;
                var u = navigator.userAgent,
                    app = navigator.appVersion;
                var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
                var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
                var startLocation = [this.localLng, this.localLat];
                var endLocation = [data.longitude * 1, data.latitude * 1]; //转换为数字
                var arrName = data.address;
                // type = 0公交 type = 1驾车 type = 2步行
                var type = "1";
                console.log(startLocation)
                console.log(endLocation)
                if (isAndroid) {
                    // alert(isAndroid)
                    // if (typeof AndroidApp != "undefined") {
                    // alert(JSON.stringify(AndroidApp));
                    if (this.version < "4.0.7") {
                        Nav.startNav(startLocation, endLocation, "我的位置", arrName);
                    } else {
                        Nav.startNav(startLocation, endLocation, "我的位置", arrName, type);
                    }
                    // }
                } else if (isiOS) {
                    if (typeof iOSApp != "undefined") {
                        window.GCI.startNav(startLocation, endLocation, "我的位置", arrName, type);
                    }
                } else {
                    if (typeof AMap == "undefined") {
                        $.showWarring("高德服务不可用,请稍候再试", true);
                        return;
                    }
                    common.setSessionStorage("trip_plan", {
                        "start": "我的位置",
                        "start_location": localLngLat,
                        "end": arrName,
                        "end_location": arrayLngLat
                    });
                    location.href = "../../trip/car?xxt_title=" + common.getUrlParam("xxt_title");
                }
            },
            getList() {
                let that = this;
                // console.log(`${process.env.VUE_APP_LIST_API}/disinfection/list`);
                axios.get(`${process.env.VUE_APP_LIST_API}/disinfection/list`).then((res) => {
                    console.log(res);
                    if (res.data.retCode != 0) {
                        return
                    }
                    if (that.localLat != '') {
                        let list = res.data.retData;

                        let newList = list.map((item, i) => {
                            item.dis = that.mapDistance(item.longitude, item.latitude)
                            return item;
                        })
                        newList.sort(function(a, b) {
                            return a.dis - b.dis
                        });
                        console.log(newList);
                        that.list = newList;

                    } else {
                        that.list = res.data.retData;
                    }

                    console.log(that.list);
                })
            }
        }
    }
</script>

<style scoped lang='less'>
    html,
    body {
        width: 100%;
    }

    #wrap {
        width: 100%;
        color: #333333;
    }

    [v-cloak] {
        display: none
    }

    #container {
        width: 100px;
        height: 100px;
    }

    #wrap li {
        width: 90%;
        /* height: 108px; */
        box-sizing: border-box;
        border: 1px solid #dfdfdf;
        border-radius: 10px;
        margin: 10px auto;
        position: relative;
    }

    #wrap li .area {
        font-size: 1.6rem;
        font-weight: 600;
        margin: 10px 0 0 4%;
    }

    #wrap li .address {
        width: 70%;
        font-size: 1.6rem;
        margin: 6px 0 0 4%;
    }

    #wrap li .time {
        font-size: 1.2rem;
        color: #888888;
        margin: 6px 0 10px 4%;
    }

    #wrap .btn-go {
        position: absolute;
        width: 40px;
        height: 40px;
        top: 0;
        bottom: 0;
        margin: auto 0;
        right: 6%;
    }

    #wrap .btn-go img {
        width: 100%;
        height: 100%;
    }

    #wrap .dis {
        position: absolute;
        font-size: 1.2rem;
        color: #888888;
        right: 10px;
        bottom: 10px;
    }
</style>