<template>
    <div class="busPlanningInfoH5">
        <header>
            <van-swipe :height="90" @change="onChange" ref="swipe">
                <van-swipe-item class="swiper" v-for="(item,index) in busPlan">
                    <div class="line-show">
                        <template v-for="(ktem,kndex) in item.segments">
                            <div v-if="ktem.transit_mode == 'SUBWAY' " class="line-sub"
                                :style="{'background':ktem.transit.lines[0].color}">
                                <span>{{ktem.transit.lines[0].name.split("(")[0].indexOf('地铁')!="-1"?ktem.transit.lines[0].name.split("(")[0].split('地铁')[1]:ktem.transit.lines[0].name.split("(")[0]}}</span>
                            </div>
                            <div class="line-bus" v-if="ktem.transit_mode == 'BUS'">
                                <span>{{ktem.transit.lines[0].name.split("(")[0]}}</span>
                            </div>
                            <img v-if="ktem.transit_mode !='WALK'" class="sjx" src="../assets/sjx.png" alt="">
                        </template>
                    </div>
                    <div class="line-info">
                        <span>{{item.time}}</span>
                        <span>{{item.cost.toFixed(1)}}元</span>
                        <span>步行{{(item.walking_distance/1000).toFixed(2)}}公里</span>
                    </div>
                </van-swipe-item>
            </van-swipe>
        </header>
        <section>
            <ul class="info-list" v-for="(item,index) in busPlan" v-show="index == page">
                <li class="type-station">
                    <span class="logo"></span>
                    <div>起点：{{station.ss}}</div>
                </li>
                <template v-for="(ktem,k) in item.segments">
                    <li class="type-walk" v-if="ktem.transit_mode == 'WALK'">
                        <div class="logo">
                            <i class="iconfont icon-exchangewalk"></i>
                            <div class="logo-line"></div>
                        </div>
                        <div class="walk-info">{{ktem.instruction}}</div>
                        <p class="walk-time">约{{(ktem.time/60).toFixed(0)}}分钟</p>
                        <div class="logo-nav"></div>
                    </li>
                    <li class="type-bus" v-else-if="ktem.transit_mode == 'BUS'">
                        <div class="logo">
                            <img src="../assets/busLogo.png" alt="">
                        </div>

                        <div class="bus-name">
                            <div class="name">
                                <img src="../assets/busLogo.png" alt="">
                                <span>{{ktem.transit.lines[0].name.split("(")[0]}}</span>
                            </div>
                            <!-- <div class="name-other">或B1路/B34路/B14路</div> -->
                        </div>
                        <!-- <div class="bus-through">开往{{ktem.transit.lines[0].name.split("--")[1].split(")")[0]}}</div> -->
                        <div class="bus-station">
                            <div class="station-start">
                                <i class="iconfont icon-exchangein"></i>
                                <span>{{ktem.transit.on_station.name}}</span>
                            </div>
                            <div class="btn-through" @click="show1(ktem)">
                                <span>途径{{ktem.transit.via_num}}站</span>
                                <img class="logo-down" v-show='!ktem.isShow' src="../assets/down.png" alt="">
                                <img class="logo-up" v-show='ktem.isShow' src="../assets/up.png" alt="">
                            </div>
                            <ul class="station-ul" v-show='ktem.isShow'>
                                <li class="station-li" v-for="(ytem,y) in ktem.transit.via_stops">
                                    <div class="logo-station"></div>
                                    <div class="station-mid">{{ytem.name}}</div>
                                </li>
                            </ul>
                            <div class="station-end">
                                <i class="iconfont icon-exchangeout"></i>
                                <span>{{ktem.transit.off_station.name}}</span>
                            </div>
                        </div>
                    </li>
                    <li class="type-subway" v-else-if="ktem.transit_mode == 'SUBWAY'" :style="{'border-color':ktem.transit.lines[0].color}">
                        <div class="logo" :style="{'background':ktem.transit.lines[0].color}">
                            <img src="../assets/subwayLogo.png" alt="">
                        </div>

                        <div class="subway-name">
                            <div class="name" :style="{'background':ktem.transit.lines[0].color}">
                                <img src="../assets/subwayLogo.png" alt="">
                                <span>{{ktem.transit.lines[0].name.split("(")[0].indexOf('地铁')!="-1"?ktem.transit.lines[0].name.split("(")[0].split('地铁')[1]:ktem.transit.lines[0].name.split("(")[0]}}</span>
                            </div>
                            <div class="name-other">开往：{{ktem.instruction.split('--')[1].split(")")[0]}}</div>
                        </div>
                        <div class="subway-station">
                            <div class="station-start">
                                <i class="iconfont icon-exchangein" :style="{'color':ktem.transit.lines[0].color}"></i>
                                <span>{{ktem.transit.on_station.name}}</span>
                            </div>
                            <div class="btn-through" @click="show2(ktem)">
                                <span>途径{{ktem.transit.via_num}}站</span>
                                <img class="logo-down" v-show='!ktem.isShow' src="../assets/down.png" alt="">
                                <img class="logo-up" v-show='ktem.isShow' src="../assets/up.png" alt="">
                            </div>
                            <ul class="station-ul" v-show='ktem.isShow'>
                                <li class="station-li" v-for="(ytem,y) in ktem.transit.via_stops">
                                    <div class="logo-station" :style="{'border-color':ktem.transit.lines[0].color}"></div>
                                    <div class="station-mid">{{ytem.name}}</div>
                                </li>
                            </ul>
                            <div class="station-end" >
                                <i class="iconfont icon-exchangeout" :style="{'color':ktem.transit.lines[0].color}"></i>
                                <span>{{ktem.transit.off_station.name}}</span>
                            </div>
                        </div>
                    </li>
                </template>

                <li class="type-station">
                    <span class="logo"></span>
                    <div>终点：{{station.es}}</div>
                </li>
            </ul>
        </section>
    </div>
</template>

<script>
    export default {
        name: "busPlanningInfoH5",
        data() {
            return {
                busPlan: null,
                page: '',
                station: {
                    ss: '',
                    es: ''
                }
            }
        },
        created() {
            this.dataInit();
        },
        mounted() {
            console.log(this.$refs.swipe);
            this.$refs.swipe.swipeTo(this.page);
        },
        methods: {
            dataInit() {
                let data = JSON.parse(sessionStorage.getItem('busPlan'));
                this.busPlan = data;
                console.log(JSON.parse(JSON.stringify(this.busPlan)));

                let station = JSON.parse(sessionStorage.getItem('station'));
                this.station.ss = station.originName;
                this.station.es = station.destinationName;

                let number = JSON.parse(sessionStorage.getItem('number'));
                this.page = number;
            },
            onChange(index) {
                // console.log(index);
                this.page = index;
            },
            show1(ktem){
                if(!ktem.isShow){
                    this.$set(ktem,'isShow',false);
                    ktem.isShow = !ktem.isShow;
                }else{
                    ktem.isShow = !ktem.isShow;
                }
            },
            show2(ktem){
                if(!ktem.isShow){
                    this.$set(ktem,'isShow',false);
                    ktem.isShow = !ktem.isShow;
                }else{
                    ktem.isShow = !ktem.isShow;
                }
            }
        }
    }
</script>

<style scoped lang="less">
    @import "../assets/font/iconfont.css";
    @bg: #dfdfdf;
    @blue: #24a1ee;
    @gray: #a1a1a1;
    @line: #dfdfdf;

    .busPlanningInfoH5 {
        width: 100%;
        height: 100%;
        background: #FAFAFA;

        header {
            min-height: 70px;
            box-sizing: border-box;
            position: relative;

            .swiper {
                background: #fff;

                .line-show {
                    min-height: 32px;
                    box-sizing: border-box;
                    padding: 18px 2.8% 6px 2.8%;

                    .line-bus {
                        display: inline-block;
                        height: 20px;
                        border: 1px solid @blue;
                        border-radius: 10px;
                        padding: 0 4px;
                        vertical-align: top;

                        span {
                            display: inline-block;
                            color: @blue;
                            line-height: 20px;
                        }

                        span:before {
                            display: inline-block;
                            content: '';
                            width: 12px;
                            height: 12px;
                            background: url('../assets/logo-bus.png') no-repeat;
                            background-size: 12px 12px;
                            vertical-align: top;
                            margin: 4px 2px 0 2px;
                        }
                    }

                    .sjx {
                        display: inline-block;
                        width: 10px;
                        height: 12px;
                        margin: 0 4px;
                        padding: 4px 0;
                    }

                    .sjx:nth-last-child(1) {
                        display: none;
                    }

                    .line-sub {
                        display: inline-block;
                        height: 20px;
                        border-radius: 10px;
                        padding: 0 4px;
                        vertical-align: top;
                        background: @blue;

                        span {
                            display: inline-block;
                            color: #fff;
                            line-height: 20px;
                        }

                        span:before {
                            display: inline-block;
                            content: '';
                            width: 12px;
                            height: 12px;
                            background: url('../assets/logo-subway.png') no-repeat;
                            background-size: 12px 12px;
                            vertical-align: top;
                            margin: 4px 2px 0 2px;
                        }
                    }
                }

                .line-info {
                    width: 94.6%;
                    margin: 0 auto 6px auto;

                    span {
                        display: inline-block;
                        color: @gray;
                    }

                    span:nth-child(2) {
                        margin: 0 4px;
                    }
                }
            }

        }

        section {
            .info-list {
                position: relative;
                width: 95.6%;
                margin: 0 auto;

                .type-station {
                    background: #fff;
                    margin-top: 12px;
                    border: 1px solid #E4E4E4;
                    border-radius: 5px;
                    height: 38px;
                    position: relative;

                    .logo {
                        display: inline-block;
                        width: 6px;
                        height: 6px;
                        border-radius: 4px;
                        border: 1px solid @blue;
                        position: absolute;
                        top: 15px;
                        left: 3.6%;
                    }

                    div {
                        margin-left: 10%;
                        font-size: 1.4rem;
                        line-height: 38px;
                    }
                }

                .type-walk {
                    position: relative;
                    background: #fff;
                    margin-top: 12px;
                    border: 1px solid #E4E4E4;
                    border-radius: 5px;
                    height: 50px;

                    .logo {
                        position: absolute;
                        height: 100%;
                        top: 0;
                        left: 2.3%;
                        width: 10%;

                        .iconfont {
                            color: @gray;
                            font-size: 1.6rem;
                            line-height: 50px;
                            position: absolute;
                        }

                        .logo-line {
                            width: 2px;
                            height: 180%;
                            border-left: 1px dashed @gray;
                            position: absolute;
                            top: -40%;
                            left: 20%;
                            z-index: 1;
                        }
                    }

                    .walk-info {
                        font-size: 1.4rem;
                        margin: 8px 6% 0 10%;
                    }

                    .walk-time {
                        font-size: 1.2rem;
                        margin-left: 10%;
                        color: @gray;
                    }
                }

                .type-bus {
                    position: relative;
                    margin-top: 12px;
                    border-left: 3px solid @blue;
                    padding-left: 4.8%;
                    margin-left: 4.3%;

                    .logo {
                        width: 16px;
                        height: 16px;
                        border-radius: 8px;
                        background: @blue;
                        position: absolute;
                        left: -10px;
                        top: 0;
                        bottom: 0;
                        margin: auto 0;

                        img {
                            width: 70%;
                            position: absolute;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            margin: auto;
                        }
                    }

                    .bus-name {
                        .name {
                            display: inline-block;
                            background: @blue;
                            color: #fff;
                            font-size: 1.4rem;
                            line-height: 20px;
                            padding: 0 6px;
                            border-radius: 20px;

                            img {
                                display: inline-block;
                                width: 12px;
                                margin: 4px 2px 0 0;
                                vertical-align: top;
                            }
                        }

                        .name-other {
                            display: inline-block;
                            font-size: 1.2rem;
                            color: #767676;
                            margin-left: 4px;
                        }
                    }

                    .bus-through {
                        font-size: 1.4rem;
                        color: #767676;
                        margin: 10px 0 0 0;
                    }

                    .bus-station {
                        position: relative;

                        .station-start,
                        .station-end {
                            font-size: 1.2rem;
                            margin: 8px 0 8px 10px;

                            .iconfont {
                                font-size: 1.2rem;
                                margin-right: 6px;
                                color: @blue;
                            }
                        }

                        .btn-through {
                            position: absolute;
                            right: 10%;
                            top: -10px;
                            font-size: 1.2rem;

                            span {
                                display: inline-block;
                                vertical-align: top;
                            }

                            .logo-down {
                                display: inline-block;
                                width: 6px;
                                vertical-align: top;
                                margin: 3px 0 0 2px;
                            }

                            .logo-up {
                                display: inline-block;
                                width: 6px;
                                vertical-align: top;
                                margin: 6px 0 0 2px;
                            }
                        }

                        .station-ul {
                            .station-li {
                                margin: 0 0 0 11px;

                                .logo-station {

                                    display: inline-block;
                                    width: 6px;
                                    height: 6px;
                                    border-radius: 4px;
                                    border: 1px solid @blue;
                                    margin-right: 8px;
                                }

                                .station-mid {
                                    display: inline-block;
                                    color: #767676;
                                    font-size: 1.2rem;

                                }
                            }
                        }
                    }
                }

                .type-subway {
                    position: relative;
                    padding-top: 12px;
                    border-left: 3px solid @blue;
                    padding-left: 4.8%;
                    margin-left: 4.3%;

                    .logo {
                        width: 16px;
                        height: 16px;
                        border-radius: 8px;
                        background: @blue;
                        position: absolute;
                        left: -10px;
                        top: 0;
                        bottom: 0;
                        margin: auto 0;

                        img {
                            width: 70%;
                            position: absolute;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            margin: auto;
                        }
                    }

                    .subway-name {
                        .name {
                            display: inline-block;
                            background: @blue;
                            color: #fff;
                            font-size: 1.4rem;
                            line-height: 20px;
                            padding: 0 6px;
                            border-radius: 20px;

                            img {
                                display: inline-block;
                                width: 12px;
                                margin: 4px 2px 0 0;
                                vertical-align: top;
                            }
                        }

                        .name-other {
                            display: inline-block;
                            font-size: 1.2rem;
                            color: #767676;
                            margin-left: 4px;
                        }
                    }

                    .subway-through {
                        font-size: 1.4rem;
                        color: #767676;
                        margin: 10px 0 0 0;
                    }

                    .subway-station {
                        position: relative;

                        .station-start,
                        .station-end {
                            font-size: 1.2rem;
                            padding: 8px 0 8px 10px;

                            .iconfont {
                                font-size: 1.2rem;
                                margin-right: 6px;
                                color: @blue;
                            }
                        }

                        .btn-through {
                            position: absolute;
                            right: 10%;
                            top: -10px;
                            font-size: 1.2rem;

                            span {
                                display: inline-block;
                                vertical-align: top;
                            }

                            .logo-down {
                                display: inline-block;
                                width: 6px;
                                vertical-align: top;
                                margin: 3px 0 0 2px;
                            }

                            .logo-up {
                                display: inline-block;
                                width: 6px;
                                vertical-align: top;
                                margin: 6px 0 0 2px;
                            }
                        }

                        .station-ul {
                            .station-li {
                                margin: 0 0 0 11px;

                                .logo-station {

                                    display: inline-block;
                                    width: 6px;
                                    height: 6px;
                                    border-radius: 4px;
                                    border: 1px solid @blue;
                                    margin-right: 8px;
                                }

                                .station-mid {
                                    display: inline-block;
                                    color: #767676;
                                    font-size: 1.2rem;

                                }
                            }
                        }
                    }
                }
            }
        }
    }
</style>