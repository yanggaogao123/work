<template>
<div class="busIndexH5">
    <header>
        <div id="container" hidden></div>
        <van-swipe class="swiper" v-if="advert" :autoplay="3000" indicator-color="white">
            <van-swipe-item v-for="(item,index) in advert">
                <img :src="item.pic" @click="goUrl(item)" />
            </van-swipe-item>
        </van-swipe>
        <img v-if="advert == null" src="../assets/headImg.png" />
        <div id="ipt-search" @click="goSearchH5">
            <input type="text" placeholder="搜索线路、站台" />
        </div>
    </header>
    <section>
        <ul>
            <li v-for="(item,index) in stationList" :key="item.i" @click="goStationInfo(item)">
                <div class="left">
                    <i class="iconfont icon-busstopd"></i>
                </div>
                <div class="right">
                    <div class="st-tit">{{item.n}}</div>
                    <div class="st-num">途经{{item.c}}站公交车线路</div>
                    <div class="st-dis">
                        <span v-if="index == 0">最近</span>
                        {{item.distance}}米
                    </div>
                </div>
            </li>
        </ul>
    </section>
    <img id="btn-plan" @click="goPlan" src="../assets/btn-plan.png" alt />
</div>
</template>

<script>
import {
    postByCoord,
    postAd,
    actionClick,
    getJsTicket
} from "@/api/api";
import App from "@/utils/app.js";
export default {
    name: "busIndexH5",
    data() {
        return {
            map: "",
            localLng: "",
            localLat: "",
            stationList: {},
            advert: null
        };
    },
    created() {
        document.title = '广州交通 行讯通'
    },
    mounted() {
        this.getAd();
        // app获取定位
        this.appGetLocation();
        // 微信定位
        // this.getWeixinTicket();
        //获取当前位置的经纬度
        // this.getLocation();
        // //根据当前的经纬度，获取附近的站点信息
        // this.getStationList();
        //设置当前经纬度至sessionStorage中
        sessionStorage.setItem(
            "localtion",
            JSON.stringify({
                localLng: this.localLng,
                localLat: this.localLat
            })
        );

    },
    methods: {
        wxGetLocation() {},
        appGetLocation() {
            let that = this;
            //   旧方法
            //   setTimeout(() => {
            //     if (typeof window.callNative != "function") {
            //       console.log("不支持该方法");
            //       that.localLng = "113.325064";
            //       that.localLat = "23.137455";
            //       sessionStorage.setItem(
            //         "localtion",
            //         JSON.stringify({
            //           localLng: this.localLng,
            //           localLat: this.localLat
            //         })
            //       );
            //       that.getStationList();
            //       return;
            //     }
            //     window.callNative(
            //       "getGeolocationWGS84",
            //       function(data) {
            //         console.log(data);
            //         console.log("调用app定位成功");
            //         that.localLng = data.longitude;
            //         that.localLat = data.latitude;
            //         console.log("lng:" + that.localLng);
            //         console.log("lat:" + that.localLat);
            //         that.getStationList();
            //       },
            //       function(error) {
            //         console.log(error);
            //         console.log("调用app定位失败");
            //         that.localLng = "113.325064";
            //         that.localLat = "23.137455";
            //         that.getStationList();
            //       }
            //     );
            //   }, 1000);
            /***************************************************************************/

            // new Function
            if (typeof App.interFace != "function") {
                console.log("不支持该方法");
                that.localLng = "113.325064";
                that.localLat = "23.137455";
                sessionStorage.setItem(
                    "localtion",
                    JSON.stringify({
                        localLng: this.localLng,
                        localLat: this.localLat
                    })
                );
                that.getStationList();
                return;
            }
            App.interFace({
                action: "getGeolocationWGS84",
                Func: (data) => {
                    console.log(data);
                    if (data) {
                        // 成功回调 
                        console.log(data);
                        console.log("调用app定位成功");
                        var json = data
                        if (typeof data == 'string') {
                            json = JSON.parse(data);
                        }
                        that.localLng = json.longitude;
                        that.localLat = json.latitude;
                        console.log("lng:" + that.localLng);
                        console.log("lat:" + that.localLat);
                        that.getStationList();
                    } else {
                        console.log(error);
                        console.log("调用app定位失败");
                        that.localLng = "113.325064";
                        that.localLat = "23.137455";
                        that.getStationList();
                    }
                },
            });
        },
        //百度坐标转高德（传入经度、纬度）
        bd_decrypt(bd_lng, bd_lat) {
            var X_PI = (Math.PI * 3000.0) / 180.0;
            var x = bd_lng - 0.0065;
            var y = bd_lat - 0.006;
            var z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
            var theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
            var gg_lng = z * Math.cos(theta);
            var gg_lat = z * Math.sin(theta);
            return {
                lng: gg_lng,
                lat: gg_lat
            };
        },
        getLocation() {
            let map = this.map;
            let that = this;
            map = new AMap.Map("container", {
                zoom: 17, //级别
                center: [113.270758, 23.135589], //中心点坐标
                mapStyle: "amap://styles/macaron", //设置地图的显示样式
                viewMode: "3D" //使用3D视图
            });

            //生成定位
            map.plugin("AMap.Geolocation", function () {
                let geolocation = new AMap.Geolocation({
                    enableHighAccuracy: true, //是否使用高精度定位，默认:true
                    timeout: 2000, //超过10秒后停止定位，默认：无穷大
                    showButton: true,
                    // buttonOffset: new AMap.Pixel(10, 120), //定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
                    showMarker: true,
                    showCircle: false,
                    buttonPosition: "LB",
                    panToLocation: true,
                    zoomToAccuracy: true
                });
                map.addControl(geolocation);
                geolocation.getCurrentPosition();
                AMap.event.addListener(geolocation, "complete", onComplete); //返回定位信息
                AMap.event.addListener(geolocation, "error", onError); //返回定位出错信息
            });

            // 移动拖拽事件
            AMapUI.loadUI(["misc/PositionPicker"], function (PositionPicker) {
                var positionPicker = new PositionPicker({
                    mode: "dragMap",
                    map: map,
                    iconStyle: {
                        //自定义外观
                        // url: locLogo,
                        ancher: [24, 40],
                        size: [48, 74]
                    }
                });
                var bool = true;
                positionPicker.on("success", function (positionResult) {
                    console.log(positionResult.position);
                });
                var onModeChange = function (e) {
                    positionPicker.setMode(e.target.value);
                };
                positionPicker.start();
                map.panBy(0, 1);
            });

            //解析定位结果
            function onComplete(data) {
                //  var str=['定位成功'];
                console.log("定位成功");
                that.localLng = data.position.lng;
                that.localLat = data.position.lat;
                that.getStationList();
            }
            //解析定位错误信息
            function onError(data) {
                console.log(data);
                console.log("定位失败");

                that.getStationList();
            }

            this.map = map;
        },
        getAd() {
            postAd({
                positoinid: 10063
            }).then(res => {
                if (res.retCode == 0) {
                    console.log(res.retData);
                    if (res.retData.length > 0) {
                        this.advert = res.retData;
                    }
                }
            });
        },
        clickAd(item) {
            actionClick({
                adid: item.id
            }).then(res => {
                if (res.retCode == 0) {
                    console.log(res.retData);
                    if (res.retData.length > 0) {
                        this.advert = res.retData;
                    }
                }
            });
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
        getStationList() {
            var param = {
                longitude: this.localLng,
                latitude: this.localLat
            };
            let that = this;
            postByCoord(param).then(res => {
                if (res.retCode == 0) {
                    this.stationList = res.retData;
                    for (let i = 0; i < this.stationList.length; i++) {
                        this.$set(
                            this.stationList[i],
                            "distance",
                            this.mapDistance(
                                this.stationList[i].longitude,
                                this.stationList[i].latitude
                            )
                        );
                    }
                    console.log(this.stationList);
                }
            });
        },
        goStationInfo(item) {
            sessionStorage.setItem("stationInfo", JSON.stringify(item));
            this.$router.push({
                name: "busStationInfoH5"
            });
        },
        goSearchH5() {
            this.$router.push({
                name: "busSearchH5"
            });
        },
        goUrl(data) {
            if (data.type == 2) {
                this.clickAd(data);
                window.location.href = data.url;
            }
        },
        // getWeixinTicket() {
        //     let thisPageUrl = location.href.split("#")[0];
        //     getJsTicket({
        //         url: thisPageUrl
        //     }).then(res => {
        //         if (res.retCode == 0) {
        //             console.log(res);
        //             if (res.retCode != 0) {
        //                 return;
        //             }
        //             let data = res.retData;
                    // wx.config({
                    //     debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    //     appId: data.appId, // 必填，公众号的唯一标识
                    //     timestamp: data.timestamp, // 必填，生成签名的时间戳
                    //     nonceStr: data.nonceStr, // 必填，生成签名的随机串
                    //     signature: data.signature, // 必填，签名
                    //     jsApiList: ["getLocation"] // 必填，需要使用的JS接口列表
                    // });
                    // wx.getLocation({
                    //     type: "wgs84", // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                    //     success: function (res) {
                    //         var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
                    //         var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
                    //         var speed = res.speed; // 速度，以米/每秒计
                    //         var accuracy = res.accuracy; // 位置精度
                    //     }
                    // });
        //         }
        //     });
        // },
        goPlan() {
            this.$router.push({
                name: "busPlanningSearchH5"
            });
        }
    }
};
</script>

<style lang="less" scoped>
@import "../assets/font/iconfont.css";

@bg: #eeeeee;
@blue: #24a1ee;
@line: #e6e6e6;
@gray: #888;

.busIndexH5 {
    width: 100%;
    min-height: 100%;
    height: 100%;
    background: @bg;
    position: relative;

    header {
        background: #fff;
        height: 240px;
        padding-bottom: 10px;
        box-sizing: border-box;

        img {
            width: 100%;
            height: 180px;
        }

        .swiper {
            width: 100%;
            height: 180px;

            img {
                width: 100%;
                height: 100%;
            }
        }

        #ipt-search {
            width: 94.6%;

            height: 40px;
            margin: 0 auto;
            background: @bg;
            border-radius: 20px;

            input {
                display: block;
                width: 90%;
                height: 40px;
                border: none;
                background: @bg;
                font-size: 1.4rem;
                margin: 10px auto 0 auto;
            }
        }
    }

    section {
        height: calc(100% - 240px);

        ul {
            height: 100%;
            overflow: scroll;

            li {
                width: 94.6%;
                height: 60px;
                background: #fff;
                margin: 10px auto;
                border-radius: 5px;
                display: flex;

                .left,
                .right {}

                .left {
                    width: 10%;
                    flex: 1;
                    vertical-align: top;
                    text-align: center;

                    i {
                        display: block;
                        font-size: 1.6rem;
                        line-height: 60px;
                        color: @blue;
                    }
                }

                .right {
                    width: 90%;
                    flex: 9;
                    position: relative;

                    .st-tit {
                        position: absolute;
                        top: 10px;
                        font-size: 1.4rem;
                        font-weight: 600;
                    }

                    .st-num {
                        position: absolute;
                        bottom: 10px;
                        font-size: 1.2rem;
                        color: @gray;
                    }

                    .st-dis {
                        position: absolute;
                        bottom: 10px;
                        right: 10px;

                        span {
                            color: #f55d23;
                        }
                    }
                }
            }

            li:nth-last-child(1) {
                margin-bottom: 80px;
            }
        }
    }

    #btn-plan {
        position: fixed;
        width: 130px;
        bottom: 10px;
        left: 0;
        right: 0;
        margin: 0 auto;
    }
}
</style>>
