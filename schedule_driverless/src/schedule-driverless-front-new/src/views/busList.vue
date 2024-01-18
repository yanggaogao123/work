<template>
    <div class="bus-list">
        <nav class="nav">
            <div class="nav-left">
                <div class="bus-line">
                    {{firstStationName}}
                    <i class="fa fa-arrow-right"></i>
                    {{lastStationName}}
                </div>
                <div class="bus-inform">
                    <span>首 {{staticsData.ft}}</span>
                    <span>末 {{staticsData.lt}}</span>
                </div>
            </div>
        </nav>
        <section id="mainContent">
            <div class="go-his">
                <van-icon class="logo-time" name="underway-o" />
                <span @click="goBusListHis">发车时刻表</span>
            </div>
            <ul class="content">
                <span class="start">起</span>
                <li class="clearfix" v-for="(item,index) in realData.rs" :key="item.sni"
                    :class="item.rsn == thisSN?'place':''">
                    <div class="bg"></div>
                    <template v-if="item.bus">
                        <div class="bus" v-if="item.bus.bl.length>0" @click="goBusBlListTime(item)">
                            <span class="number">{{item.bus.bl.length>1?item.bus.bl.length+'辆':''}}</span>
                            <template>
                                <span class="img"
                                    v-if="item.bus.bl[0].serviceType == 1 || item.bus.bl[0].serviceType == 8"></span>
                                <span class="img-fast"
                                    v-if="item.bus.bl[0].serviceType == 3 || item.bus.bl[0].serviceType == 7 || item.bus.bl[0].serviceType == 9"></span>
                                <span class="img-short"
                                    v-if="item.bus.bl[0].serviceType == 2 || item.bus.bl[0].serviceType == 4 || item.bus.bl[0].serviceType == 5 || item.bus.bl[0].serviceType == 6 || item.bus.bl[0].serviceType == 10 || item.bus.bl[0].serviceType == 11"></span>
                            </template>
                        </div>
                        <div class="bus-middle" v-if="item.bus.bbl.length>0" @click="goBusBblListTime(item)">
                            <span class="number"></span>
                            <template>
                                <span class="img"
                                    v-if="item.bus.bbl[0].serviceType == 1 || item.bus.bbl[0].serviceType == 8"></span>
                                <span class="img-fast"
                                    v-if="item.bus.bbl[0].serviceType == 3 || item.bus.bbl[0].serviceType == 7 || item.bus.bbl[0].serviceType == 9"></span>
                                <span class="img-short"
                                    v-if="item.bus.bbl[0].serviceType == 2 || item.bus.bbl[0].serviceType == 4 || item.bus.bbl[0].serviceType == 5 || item.bus.bbl[0].serviceType == 6 || item.bus.bbl[0].serviceType == 10 || item.bus.bbl[0].serviceType == 11"></span>
                            </template>
                        </div>
                    </template>

                    <template>
                        <div class="fl" :class="index==0?'line-spec':'line'" v-if="item.block == 0 || item.block == 1">
                            <div></div>
                        </div>
                        <div class="yellow fl" :class="index==0?'line-spec':'line'"
                            v-else-if="item.block == 2 || item.block == 3">
                            <div></div>
                        </div>
                        <div class="red fl" :class="index==0?'line-spec':'line'" v-else>
                            <div></div>
                        </div>
                    </template>
                    <div class="station fl" @click="changeRouteStation(item.rsi,item.rsn)">
                        <span class="title">{{index+1}}</span>
                        <span class="name">{{item.rsn}}</span>
                        <span class="subway" v-for="(ktem,kndex) in item.swi" :style="{background:ktem.color}">
                            <img src="../assets/y.png" alt />
                            <span>{{ktem.name.split('号线')[0]}}</span>
                        </span>
                        <span class="b" v-if="item.brt == 1">B</span>
                    </div>
                    <div class="location fl">
                        <div class="bus-info">
                            <span class="title">{{index+1}}</span>
                            <span class="name">{{item.rsn}}</span>
                            <span class="subway" v-for="(ktem,kndex) in item.swi" :style="{background:ktem.color}">
                                <img src="../assets/y.png" alt />
                                <span>{{ktem.name.split('号线')[0]}}</span>
                            </span>
                            <span class="b" v-if="item.brt == 1">B</span>
                        </div>

                        <p class="tips">距离您最近的三趟车</p>
                        <template v-for="(ytem,yndex) in item.time">
                            <div class="bus-time bus-arrival" v-if="ytem.count == 0">
                                <p class="state">已进站</p>
                                <p class="plate-number">{{ytem.plateNumber}}</p>
                            </div>
                            <div class="bus-time bus-coming" v-if="ytem.count > 0">
                                <p class="state">
                                    {{ytem.time}}
                                    <span>分</span>&nbsp;{{ytem.count}}
                                    <span>站</span>
                                </p>
                                <p class="plate-number">{{ytem.plateNumber}}</p>
                            </div>
                            <div class="bus-time bus-notsend" v-if="ytem.count == -1 ">
                                <p class="state" v-if="ytem.fbt == '' &&ytem.count == -1">尚未发车</p>
                                <p class="state" v-if="ytem.fbt == -2 &&ytem.count == -1">待发车</p>
                                <p class="state" v-if="ytem.fbt != -2&&ytem.fbt != '' &&ytem.count == -1">
                                    <span>预计</span>{{ytem.fbt}}<span>发车</span></p>
                                <p class="plate-number" v-if="(ytem.fbt == -2||ytem.fbt == '' )&&ytem.count == -1">无车辆信息
                                </p>
                                <p class="plate-number" v-if="ytem.fbt != -2&&ytem.fbt != '' &&ytem.count == -1">仅供参考
                                </p>
                            </div>
                        </template>
                    </div>
                </li>
                <span class="end">终</span>
            </ul>
        </section>
        <footer>

            <ul>
                <li @click="turnAround">
                    <img src="../assets/return.png" alt />
                    <span>反向</span>
                </li>
                <li @click="reFlash">
                    <img src="../assets/refresh.png" alt />
                    <span>刷新</span>
                </li>
            </ul>
        </footer>
    </div>
</template>

<script>
    import {
        postStatics,
        postDynamic
    } from "@/api/api";
    export default {
        name: "busList",
        data() {
            return {
                routeId: "",
                direction: "direction",
                routeStationId: "routeStationId",
                type: "type",
                latitude: "latitude",
                longitude: "longitude",
                stationName: {},
                thisSN: null,
                scrollTop: 0,
                routeStationName: null,
                firstStationName: null,
                lastStationName: null,
                realData: {
                    com: null,
                    ft: null,
                    lt: null,
                    price: null,
                    ri: null,
                    rn: null,
                    rs: [{}]
                },
                staticsData: {
                    com: null,
                    ft: null,
                    lt: null,
                    price: null,
                    ri: null,
                    rn: null,
                    rs: [{}]
                },
                myScroll: null,
                dynamicData: null,
                busInfo: {},
                localtion: {}
            };
        },
        created() {
            //从sessionStorage中拿出数据
            this.initData();
            // 加载静态数据，动态数据
            this.loadStaticsDatas();
            this.loadDynamicDatas();
        },
        mounted(){
            
            // window.onscroll = function() {
            //     //为了保证兼容性，这里取两个值，哪个有值取哪一个
            //     //scrollTop就是触发滚轮事件时滚轮的高度
            //     let a = document.querySelector('.place');
            //     let b = document.querySelector('.content');
            //     console.log(a.offsetTop);
            //     b.scrollTop = 100;
            //     var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
            //     console.log("滚动距离" + scrollTop);
            // }
            
        },
        methods: {
            initData() {
                this.busInfo = JSON.parse(sessionStorage.getItem("busInfo"));
                this.localtion = JSON.parse(sessionStorage.getItem("localtion"));
                this.thisSN = JSON.parse(sessionStorage.getItem("busStation"));
            },

            loadStaticsDatas: function () {
                // 加载基础数据
                postStatics({
                    routeId: this.busInfo.ri,
                    direction: this.busInfo.d
                }).then(
                    res => {
                        if (res.retCode == 0) {
                            this.staticsData = res.retData;
                            // this.realData = res.retData;
                            // console.log(this.realData);
                            // 判断是否可以匹配到线路站点ID
                            var len = res.retData.rs.length;
                            console.log(this.localtion);
                            if (
                                this.localtion.localLng != null &&
                                this.localtion.localLat != null
                            ) {
                                //   postNearStationByRoute({
                                //     routeId: this.busInfo.ri,
                                //     direction: this.busInfo.d,
                                //     latitude: this.localtion.localLat,
                                //     longitude: this.localtion.localLng,
                                //     gps: 1
                                //   }).then(res => {
                                //     console.log(res);
                                //     if (res.retCode == 0) {
                                //       for (var i = 0; i < len; i++) {
                                //         if (this.stationName == response.retData.rs[i].rsn) {
                                //           that.routeStationId = response.retData.rs[i].rsi;
                                //           break;
                                //         }
                                //       }
                                //     }
                                //   });
                            }
                        } else {
                            Dialog({
                                message: res.retMsg
                            });
                        }
                    }
                );
            },
            loadDynamicDatas: function () {
                let that = this;
                postDynamic({
                    routeId: this.busInfo.ri,
                    routeStationId: this.busInfo.rsi,
                    direction: this.busInfo.d
                }).then(res => {
                    if (res.retCode == 0) {
                        this.dynamicData = res.retData;
                        console.log(this.dynamicData);
                        this.realData = this.staticsData;
                        console.log(that.realData);
                        for (var i = 0; i < that.realData.rs.length; i++) {
                            // that.realData.rs[i].block = this.dynamicData.block[i];
                            // that.realData.rs[i].bus = this.dynamicData.bus[i];
                            // that.realData.rs[i].time = this.dynamicData.time;
                            // that.$set(that.realData.rs[i],, {block:this.dynamicData.block[i],bus:this.dynamicData.bus[i],time:this.dynamicData.time});
                            that.$set(that.realData.rs[i], 'block', this.dynamicData.block[i]);
                            that.$set(that.realData.rs[i], 'bus', this.dynamicData.bus[i]);
                            that.$set(that.realData.rs[i], 'time', this.dynamicData.time);
                        }
                        console.log(that.realData);
                        this.firstStationName = that.realData.rs[0].rsn;
                        this.lastStationName = that.realData.rs[that.realData.rs.length - 1].rsn;

                    } else {
                        Dialog({
                            message: res.retMsg
                        });
                    }
                });
            },
            // 点击定位站点
            changeRouteStation: function (rsi, rsn) {
                console.log(123);
                localStorage.setItem("rsi", rsi);
                this.routeStationId = rsi;
                // this.busInfo.rsi = rsi;
                this.$set(this.busInfo, 'rsi', rsi);
                this.thisSN = rsn;
                this.loadDynamicDatas();
            },
            // 点击车辆
            clickBus: function (buses) {
                localStorage.setItem("startStationName", this.staticsData.rs[0].rsn);
                localStorage.setItem(
                    "endStationName",
                    this.staticsData.rs[this.staticsData.rs.length - 1].rsn
                );
                if (buses.length > 1) {
                    // 获取出发站点，以及终点站
                    localStorage.setItem("buses", JSON.stringify(buses));
                    window.location.href = "bus_list.html";
                } else {
                    localStorage.setItem("bus", JSON.stringify(buses[0]));
                    window.location.href = "bus_list_station.html";
                }
            },
            // 用户点击反向操作
            turnAround: function () {
                if (this.busInfo.d == "0") {
                    this.busInfo.d = "1";
                } else if (this.busInfo.d == "1") {
                    this.busInfo.d = "0";
                }
                // 重新加载数据
                this.loadStaticsDatas();
                this.loadDynamicDatas();
            },
            reFlash: function () {
                // 重新加载数据
                // this.loadStaticsDatas();
                this.loadDynamicDatas();
            },
            goBusBlListTime(item) {
                this.$router.push({
                    name: 'busListTime',
                    params: {
                        busStatics: {
                            id: item.bus.bl[0].id,
                            subId: item.bus.bl[0].subId,
                            plateNumber: item.bus.bl[0].plateNumber
                        }
                    }
                })
            },
            goBusBblListTime(item) {
                this.$router.push({
                    name: 'busListTime',
                    params: {
                        busStatics: {
                            id: item.bus.bbl[0].id,
                            subId: item.bus.bbl[0].subId,
                            plateNumber: item.bus.bbl[0].plateNumber
                        }
                    }
                })
            },
            goBusListHis() {
                this.$router.push({
                    name: 'busListHis',
                    params: {
                        busStatics: {
                            rn: this.staticsData.rn,
                            com: this.staticsData.com,
                            firstStationName: this.firstStationName,
                            lastStationName: this.lastStationName,
                            d: this.busInfo.d
                        }
                    }
                })
            }
        }
    };
</script>

<style scoped lang="less">
    @background: #dfdfdf;
    @blue: #24a1ee;
    @gray: #a1a1a1;
    @line: #dfdfdf;
    @height: 2.88;

    .bus-list {
        width: 100%;
        height: 100%;
        position: relative;
        font-size: 1.2rem * @height;

        .nav {
            background: #fff;
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 999;
            height: 72px * @height;
            box-sizing: border-box;
            border-bottom: 1px * @height solid @line;

            .nav-left {
                display: inline-block;
                width: 75%;
                margin: 20px * @height 0 5px * @height 12px * @height;
                overflow: hidden;

                .bus-line {
                    width: 100%;
                    font-size: 1.4rem * @height;
                    height: 22px * @height;
                    overflow: hidden;

                    span {
                        display: inline-block;
                    }

                    span:nth-child(3) {
                        overflow: hidden;
                        height: 22px * @height;
                    }
                }

                .bus-inform {
                    margin-top: 5px * @height;

                    span {
                        font-size: 1.1rem * @height;
                        color: #a1a1a1;
                    }

                    span:nth-child(2) {
                        margin: 0 10px * @height;
                    }
                }
            }
        }

        #mainContent {
            padding-top: 72px * @height;
            padding-bottom: 48px * @height;
            width: 100%;
            overflow-x: hidden;
            position:relative;

            .go-his {
                position: fixed;
                background: @blue;
                top: 100px * @height;
                right: -20px;
                z-index: 1000;
                padding: 0 10px*@height;
                font-size: 1.2rem*@height;
                color: #fff;
                border-radius: 50px;

                .logo-time {
                    display: inline-block;
                    font-size: 1.2rem*@height;
                    line-height: 50px;
                    vertical-align: middle;
                }

                span {
                    display: inline-block;
                }
            }

            .content {
                margin: 14px * @height 0 0 16%;
                .start {
                    display: inline-block;
                    width: 22px * @height;
                    height: 22px * @height;
                    font-size: 1.2rem * @height;
                    background: @blue;
                    color: #fff;
                    border-radius: 50px * @height;
                    box-sizing: border-box;
                    padding: 2px * @height 0 0 5px * @height;
                }

                .end {
                    display: inline-block;
                    width: 22px * @height;
                    height: 22px * @height;
                    font-size: 1.2rem * @height;
                    background: @blue;
                    color: #fff;
                    border-radius: 50px * @height;
                    box-sizing: border-box;
                    padding: 2px * @height 0 0 5px * @height;
                }

                li {
                    position: relative;
                    height: 46px * @height;

                    .bg {
                        display: none;
                    }

                    .bus {
                        position: absolute;
                        display: block;
                        right: 102%;
                        width: 50px * @height;
                        top: -10%;

                        .number {
                            display: inline-block;
                            font-size: 1.2rem * @height;
                            width: 20px * @height;
                            vertical-align: top;
                            padding-top: 3px * @height;
                            margin-right: 2px * @height;
                            color: #0099cc;
                        }

                        .img {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busn.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-fast {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busf.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-short {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/buss.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }
                    }

                    .bus-middle {
                        position: absolute;
                        display: block;
                        right: 102%;
                        width: 50px * @height;
                        top: 30%;

                        .number {
                            display: inline-block;
                            font-size: 1.2rem * @height;
                            width: 20px * @height;
                            vertical-align: top;
                            padding-top: 3px * @height;
                            margin-right: 2px * @height;
                            color: #0099cc;
                        }

                        .img {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busn.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-fast {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busf.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-short {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/buss.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }
                    }

                    .line-spec {
                        display: inline-block;
                        width: 5px * @height;
                        height: 100%;
                        background: #49b993;
                        text-align: center;
                        margin-left: 7px * @height;

                        div {
                            width: 1px * @height;
                            border-left: 1px * @height dashed #fff;
                            height: 100%;
                            //                  background: #fff;
                            margin: 0 auto;
                        }
                    }

                    .line {
                        display: inline-block;
                        width: 5px * @height;
                        height: 100%;
                        background: #49b993;
                        text-align: center;
                        margin-left: 7px * @height;

                        //              margin-top:-5%;
                        div {
                            width: 1px * @height;
                            border-left: 1px * @height dashed #fff;
                            height: 100%;
                            //                  background: #fff;
                            margin: 0 auto;
                        }
                    }

                    .yellow {
                        background: #dace4a;
                    }

                    .red {
                        background: #d15264;
                    }

                    .line:before {
                        content: "";
                        display: inline-block;
                        width: 5px * @height;
                        height: 5px * @height;
                        background: #ffffff;
                        border-radius: 50px * @height;
                        position: absolute;
                        top: -2px * @height;
                        left: 7px * @height;
                    }

                    .station {
                        display: inline-block;
                        position: absolute;
                        width: 86%;
                        top: -8px * @height;
                        border-bottom: 1px * @height solid #e6e6e6;
                        margin-left: 10px * @height;
                        padding-bottom: 14px * @height;

                        span {
                            display: inline-block;
                        }

                        .subway {
                            display: inline-block;
                            padding: 0 4px * @height;
                            font-size: 1rem * @height;
                            line-height: 14px * @height;
                            color: #fff;
                            border-radius: 3px * @height;
                            text-align: center;
                            margin-left: 8px * @height;
                            vertical-align: middle;

                            img {
                                display: inline-block;
                                width: 8px * @height;
                            }
                        }

                        .bus-info {
                            width:100%;
                            overflow:hidden;
                            .title {
                                display: inline-block;
                                width: 20px * @height;
                                font-size: 0.8rem * @height;
                                color: #a1a1a1;
                                text-align: center;
                                vertical-align: middle;
                                margin-top: 5px * @height;
                            }

                            .name {
                                font-size: 1.5rem * @height;
                                max-width: 80%;
                                overflow-x: hidden;
                                height: 1.5rem * @height;
                                vertical-align: middle;
                            }

                            .b {
                                font-size: 1rem * @height;
                                font-weight: bold;
                                color: #ff954a;
                                margin-left: 5px * @height;
                                vertical-align: middle;
                                margin-top: 2px * @height;
                            }

                            .subway {
                                display: inline-block;
                                padding: 0 4px * @height;
                                font-size: 1rem * @height;
                                line-height: 14px * @height;
                                color: #fff;
                                border-radius: 3px * @height;
                                text-align: center;
                                margin-left: 8px * @height;
                                vertical-align: middle;

                                img {
                                    display: inline-block;
                                    width: 8px * @height;
                                }
                            }

                            .subway-line1 {
                                background: #f9e001;
                            }

                            .subway-line2 {
                                background: #0067cc;
                            }

                            .subway-line3 {
                                background: #ea6632;
                            }

                            .subway-line4 {
                                background: #009801;
                            }

                            .subway-line5 {
                                background: #ff0000;
                            }

                            .subway-line6 {
                                background: #8c1f5d;
                            }

                            .subway-line7 {
                                background: #3fa37e;
                            }

                            .subway-line8 {
                                background: #00a2cc;
                            }

                            .subway-line-gf {
                                background: #cae987;
                            }

                            .subway-line-apm {
                                background: #00a2cb;
                            }
                        }

                        .hidden {
                            color: red;
                        }
                    }

                    .location {
                        display: none;
                    }
                }

                li:nth-child(2) {
                    .line:before {
                        content: "";
                        display: inline-block;
                        width: 5px * @height;
                        height: 5px * @height;
                        background: #ffffff;
                        border-radius: 50px * @height;
                        position: absolute;
                        top: -2px * @height;
                        left: 7px * @height;
                    }
                }

                .place {
                    height: 120px * @height;

                    .bg {
                        display: block;
                        position: absolute;
                        background: #dcf1fc;
                        width: 97%;
                        height: 100%;
                        z-index: -1;
                        right: 0;
                        top: -16px * @height;
                    }

                    .bus {
                        position: absolute;
                        right: 102%;

                        .number {
                            color: @blue;
                        }

                        .img {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busn.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-fast {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busf.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-short {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/buss.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }
                    }

                    .bus-middle {
                        position: absolute;
                        right: 102%;

                        .number {
                            color: #56be4d;
                        }

                        .img {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busn.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-fast {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/busf.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }

                        .img-short {
                            display: inline-block;
                            width: 22px * @height;
                            height: 22px * @height;
                            background: url("../assets/buss.png") no-repeat;
                            background-size: 22px * @height 22px * @height;
                        }
                    }

                    .line:before {
                        content: "";
                        display: inline-block;
                        width: 20px * @height;
                        height: 20px * @height;
                        background: url(../assets/dot.png) no-repeat;
                        background-size: 20px * @height 20px * @height;
                        position: absolute;
                        top: -8px * @height;
                        left: 0;
                    }

                    .station {
                        display: none;
                    }

                    .location {
                        display: inline-block;
                        position: absolute;
                        width: 86%;
                        top: -8px * @height;
                        border-bottom: 1px * @height solid #e6e6e6;
                        margin-left: 10px * @height;
                        padding-bottom: 2px * @height;

                        .bell {
                            display: inline-block;
                            position: absolute;
                            width: 22px * @height;
                            height: 22px * @height;
                            // background: url(../img/bell.png) no-repeat;
                            background-size: 22px * @height 22px * @height;
                            top: 2px * @height;
                            right: 2px * @height;
                        }

                        .bus-info {
                            width:100%;
                            height: 64px;
                            overflow-x: auto;
                            white-space: nowrap;
                            .title {
                                display: inline-block;
                                width: 20px * @height;
                                font-size: 0.8rem * @height;
                                color: #56be4d;
                                text-align: center;
                                vertical-align: middle;
                                padding-top: 3px * @height;
                            }

                            .name {
                                font-size: 1.5rem * @height;
                                max-width: 80%;
                                overflow-x: hidden;
                                height: 1.5rem * @height;
                                vertical-align: middle;
                            }

                            .b {
                                font-size: 1rem * @height;
                                font-weight: bold;
                                color: #ff954a;
                                margin-left: 5px * @height;
                                vertical-align: middle;
                            }

                            .subway {
                                display: inline-block;
                                padding: 0 4px * @height;
                                line-height: 14px * @height;
                                color: #fff;
                                border-radius: 3px * @height;
                                text-align: center;
                                margin-left: 8px * @height;

                                img {
                                    display: inline-block;
                                    width: 12px * @height;
                                    margin-top: 2px * @height;
                                }

                                span {
                                    font-size: 1.2rem * @height;
                                    line-height: 14px * @height;
                                }
                            }

                            .subway-line1 {
                                background: #f9e348;
                            }

                            .subway-line2 {
                                background: #2778cf;
                            }

                            .subway-line3 {
                                background: #ea774f;
                            }

                            .subway-line4 {
                                background: #2ca32f;
                            }

                            .subway-line5 {
                                background: #fc2632;
                            }

                            .subway-line6 {
                                background: #96396d;
                            }

                            .subway-line7 {
                                background: #57ab8c;
                            }

                            .subway-line8 {
                                background: #30abcf;
                            }

                            .subway-line9 {
                                background: #a4cd6e;
                            }

                            .subway-line13 {
                                background: #b0bf5d;
                            }

                            .subway-line14 {
                                background: #7f3139;
                            }

                            .subway-line21 {
                                background: #38488d;
                            }

                            .subway-line-gf {
                                background: #cfe998;
                            }

                            .subway-line-apm {
                                background: #30abcf;
                            }
                        }

                        .tips {
                            color: #a1a1a1;
                            font-size: 0.8rem * @height;
                            margin-left: 10px * @height;
                        }

                        .bus-time {
                            display: inline-block;
                            width: 30%;
                            height: 58px * @height;
                            border-radius: 5px * @height;
                            margin: 6px * @height 0 0 2%;
                            position: relative;

                            p {
                                position: absolute;
                                display: block;
                                width: 100%;
                                text-align: center;
                                font-size: 1.5rem * @height;
                                color: #ffffff;
                                margin: 0 auto;
                            }

                            .state {
                                top: 2px * @height;

                                span {
                                    font-size: 0.9rem * @height;
                                }
                            }

                            .plate-number {
                                font-size: 1rem * @height;
                                bottom: 2px * @height;
                            }
                        }

                        .bus-arrival {
                            background: #0099cc;
                        }

                        .bus-coming {
                            background: #5ebedd;
                        }

                        .bus-notsend {
                            background: #91daf2;
                        }
                    }
                }
            }
        }

        footer {
            position: fixed;
            width: 100%;
            height: 44px * @height;
            box-sizing: border-box;
            border-top: 1px * @height solid @line;
            left: 0;
            bottom: 0;
            background: #fff;

            ul {
                width: 100%;
                height: 100%;
                display: flex;

                li {
                    flex: 1;
                    width: 100%;
                    text-align: center;
                    img {
                        display: block;
                        width: 14px * @height;
                        height: 14px * @height;
                        margin: 6px * @height auto 2px * @height auto;
                    }

                    span {
                        display: block;
                    }
                }
            }
        }
    }
</style>