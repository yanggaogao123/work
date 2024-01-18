<template>
    <div class="busPlanningShowH5">
        <div id="container" hidden></div>
        <!-- <header>
            <div class="ipt-search">
                <div class="left">
                    <div class="logo-loc"></div>
                    <div class="logo-line"></div>
                    <div class="logo-go"></div>
                </div>
                <div class="middle">
                    <input type="text" id="start-input" autocomplete="off" @focus="changeInput(0)" @keyup="mapSearch" class="start-input"
                        placeholder="您要从哪里出发" v-model="msg.stMsg">
                    <input type="text" id="end-input" autocomplete="off" @focus="changeInput(1)" @keyup="mapSearch" class="end-input"
                        placeholder="您要去哪里" v-model="msg.endMsg">
                </div>
                <div class="right">
                    <div class="btn-return" @click="changeType">
                        <img src="../assets/arrow.png" alt="">
                    </div>
                </div>
            </div>
            <ul class="list-search" v-show='show.slShow'>
                <li v-for="(item,index) in searchList" @click="listClick(item)">
                    {{item.name}}
                </li>
            </ul>
            <div class="bg" @click="bgClick" v-show='show.slShow'></div>
        </header> -->
        <select-input @mapList="dataInit"></select-input>
        <section>
            <div class="tab">
                <van-tabs v-model="activeName" title-inactive-color="#a1a1a1" title-active-color="#24a1ee"
                    color="#24a1ee" @click="tabClick">
                    <van-tab title="最快" name="LEAST_TIME"></van-tab>
                    <van-tab title="少步行" name="LEAST_WALK"></van-tab>
                    <van-tab title="少换乘" name="LEAST_TRANSFER"></van-tab>
                    <van-tab title="不坐地铁" name="NO_SUBWAY"></van-tab>
                </van-tabs>
            </div>
            <div class="content">
                <ul class="ul-list" v-show='!show.noDataLogo'>
                    <li v-for="(item,index) in resultList" @click="go(index)">
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
                        <span class="recommend" v-if="index == 0">推荐</span>
                    </li>
                </ul>
                <div class="no-data-logo" v-show="show.noDataLogo">
                    <img src="@/assets/no-data.png" alt="">
                    <p>抱歉，查询不到结果，建议您重新搜索</p>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
    import selectInput from '@/components/selectInput';
    export default {
        name: 'busPlanningShowH5',
        components:{
            selectInput
        },
        data() {
            return {
                activeName: 'LEAST_TIME',
                show: {
                    slShow: false,
                    noDataLogo: false,
                },
                msg: {
                    stMsg: '',
                    endMsg: ''
                },
                realAddress:'',
                myLoc: {
                    lng: "",
                    lat: ""
                },
                map: "",
                searchList: [],
                seInput: 0,
                locHis: [],
                resultList: [],
            }
        },
        created() {
            // this.dataInit();
        },
        watch: {
            // 'msg.endMsg'() {
            //     setTimeout(() => {
            //         if (this.msg.stMsg != '' && this.msg.endMsg != '') {
            //             this.mapResult(this.activeName);
            //         }
            //     }, 500);
                
            // },
            // 'msg.stMsg'() {
            //     setTimeout(() => {
            //         if (this.msg.stMsg != '' && this.msg.endMsg != '') {
            //             this.mapResult(this.activeName);
            //         }
            //     }, 500);
            // }
        },
        methods: {
            dataInit(x,y){
                console.log(x);
                console.log(y);
                this.resultList = x;
                this.msg.stMsg = y.stMsg;
                this.msg.endMsg = y.endMsg;


                 let that = this;
                let promise = new Promise(function (resolve, reject) {
                    that.map = new AMap.Map("container", {
                        resizeEnable: true
                    });

                    let loc = JSON.parse(sessionStorage.getItem("localtion"));
                    that.myLoc.lng = loc.localLng;
                    that.myLoc.lat = loc.localLat;
                    console.log(that.myLoc);

                    let search = JSON.parse(sessionStorage.getItem("loc"));

                    console.log(search);
                    that.msg.stMsg = search.stLoc;
                    that.msg.endMsg = search.endLoc;
                    if (search.stLoc == '我的位置' || search.endLoc == '我的位置') {
                        var lnglat = [that.myLoc.lng, that.myLoc.lat];
                        that.map.plugin('AMap.Geocoder', function () {
                            var geocoder = new AMap.Geocoder({
                                city: '广州'
                            })
                            geocoder.getAddress(lnglat, function (status, result) {
                                if (status === 'complete' && result.info === 'OK') {
                                    // result为对应的地理位置详细信息
                                    console.log(result);
                                    that.realAddress = result.regeocode.formattedAddress;
                                    console.log(that.realAddress);
                                    resolve();
                                }
                            })
                        })
                    } else {
                        resolve();
                    }

                }).then(function () {
                    console.log(2);
                    if (that.msg.stMsg != '' && that.msg.endMsg != '') {
                        that.mapResult(that.activeName);
                    }
                })
            },
            mapResult(type) {
                console.log('出来了');
                let that = this;
                this.map.plugin('AMap.Transfer', function () {
                    if (type == "LEAST_TIME") {
                        var transOptions = {
                            nightflag: true, // 是否计算夜班车
                            city: '广州市',
                            policy: AMap.TransferPolicy.LEAST_TIME //乘车策略
                        };
                    } else if (type == "LEAST_WALK") {
                        var transOptions = {
                            nightflag: true, // 是否计算夜班车
                            city: '广州市',
                            policy: AMap.TransferPolicy.LEAST_WALK //乘车策略
                        };
                    } else if (type == "LEAST_TRANSFER") {
                        var transOptions = {
                            nightflag: true, // 是否计算夜班车
                            city: '广州市',
                            policy: AMap.TransferPolicy.LEAST_TRANSFER //乘车策略
                        };
                    } else if (type == "NO_SUBWAY") {
                        var transOptions = {
                            nightflag: true, // 是否计算夜班车
                            city: '广州市',
                            policy: AMap.TransferPolicy.NO_SUBWAY //乘车策略
                        };
                    }


                    console.log(transOptions);

                    var transfer = new AMap.Transfer(transOptions);
                    //根据起、终点名称查询公交换乘路线
                    if (that.msg.stMsg == '我的位置') {
                        var points = [{
                                keyword: that.realAddress,
                            },
                            {
                                keyword: that.msg.endMsg,
                            }
                        ];
                        // console.log(points);
                    } else if (that.msg.endMsg == '我的位置') {
                        var points = [{
                                keyword: that.msg.stMsg,
                            },
                            {
                                keyword: that.realAddress,
                            }
                        ];
                    } else {
                        var points = [{
                                keyword: that.msg.stMsg,
                            },
                            {
                                keyword: that.msg.endMsg,
                            }
                        ];
                    }
                    // console.log(JSON.parse(JSON.stringify(points)));
                    transfer.search(points, function (status, result) {
                        console.log(result);
                        console.log(status);

                        if (result.info == 'NO_DATA') {
                            that.show.noDataLogo = true;
                        }

                        if (result.info == 'OK') {
                            that.show.noDataLogo = false;
                            sessionStorage.setItem('station', JSON.stringify({
                                originName: result.originName,
                                destinationName: result.destinationName
                            }))
                            that.resultList = result.plans;
                            console.log(that.con(that.resultList));
                            for (var i = 0; i < that.resultList.length; i++) {
                                // 计算时间
                                let time = that.resultList[i].time;
                                time = time / 60;
                                if (time >= 60) {
                                    let a = parseInt(time / 60);
                                    let b = time % 60;
                                    b = b.toFixed(0);
                                    time = a + "小时" + b + '分钟';
                                } else {
                                    console.log(time);
                                    time = time.toFixed(0);
                                    time = time + '分钟';
                                }
                                that.resultList[i].time = time;

                                // 处理地铁线路颜色
                                for (var y = 0; y < that.resultList[i].segments.length; y++) {
                                    if (that.resultList[i].segments[y].transit_mode == 'SUBWAY') {
                                        let name = that.resultList[i].segments[y].transit.lines[0].name;
                                        let line = that.resultList[i].segments[y].transit.lines[0];
                                        name = name.split('(')[0];
                                        if (name.indexOf('地铁') != '-1') {
                                            name = name.split('地铁')[1];
                                        }
                                        // console.log('name:' + that.con(name));
                                        switch (name) {
                                            case '1号线':
                                                line.color = '#FCD600';
                                                break;
                                            case '2号线':
                                                line.color = '#0065B3';
                                                break;
                                            case '3号线':
                                                line.color = '#FFA500';
                                                break;
                                            case '4号线':
                                                line.color = '#018237';
                                                break;
                                            case '5号线':
                                                line.color = '#C5003E';
                                                break;
                                            case '6号线':
                                                line.color = '#741D51';
                                                break;
                                            case '7号线':
                                                line.color = '#86B81C';
                                                break;
                                            case '8号线':
                                                line.color = '#008187';
                                                break;
                                            case '9号线':
                                                line.color = '#C5003E';
                                                break;
                                            case '13号线':
                                                line.color = '#818530';
                                                break;
                                            case '14号线':
                                                line.color = '#871C2A';
                                                break;
                                            case '14号线支线(知识城线)':
                                                line.color = '#871C2A';
                                                break;
                                            case '21号线':
                                                line.color = '#0C1335';
                                                break;
                                            case 'APM线':
                                                line.color = '#02B6E3';
                                                break;
                                            case '广佛线':
                                                line.color = '#B8D201';
                                                break;
                                            default:

                                        }
                                        that.$set(that.resultList[i].segments[y].transit, 'line', line);
                                        // console.log(line);
                                    }
                                }
                                console.log(that.con(that.resultList));
                                console.log(that.con(that.msg));
                                // that.$emit('mapList',that.resultList,that.msg);
                            }
                        }
                        // result即是对应的公交路线数据信息，相关数据结构文档请参考  https://lbs.amap.com/api/javascript-api/reference/route-search#m_TransferResult
                    });
                })

            },
            tabClick(name, title) {
                console.log(name);
                console.log(this.activeName);
                this.mapResult(this.activeName);
            },
            go(data){
                console.log(data);
                let that = this;
                sessionStorage.setItem('busPlan',JSON.stringify(this.resultList));
                sessionStorage.setItem('number',JSON.stringify(data));
                sessionStorage.setItem("loc",JSON.stringify({stLoc:that.msg.stMsg,endLoc:that.msg.endMsg}));
                this.$router.push({
                    name: 'busPlanningInfoH5'
                })
            },
            con(a) {
                return JSON.parse(JSON.stringify(a));
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

    .busPlanningShowH5 {
        width: 100%;
        height: 100%;
        overflow: hidden;

        header {
            width: 100%;
            height: 72x;
            position: relative;

            // background: #fff;
            .ipt-search {
                background: #F6F6F6;
                display: flex;
                width: 94.6%;
                height: 72px;
                box-sizing: border-box;
                margin: 10px auto;
                border-radius: 5px;
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                z-index: 260;

                .left {
                    flex: 1;
                    position: relative;

                    .logo-loc {
                        position: absolute;
                        width: 6px;
                        height: 6px;
                        border-radius: 4px;
                        border: 1px solid @blue;
                        left: 0;
                        right: 0;
                        top: 15px;
                        margin: 0 auto;
                    }

                    .logo-line {
                        position: absolute;
                        width: 1px;
                        height: 16px;
                        border-left: 1px dotted @bg;
                        left: 0;
                        right: 0;
                        margin: 0 auto;
                        top: 26px;
                    }

                    .logo-go {
                        position: absolute;
                        width: 6px;
                        height: 6px;
                        border-radius: 4px;
                        border: 1px solid #EF3636;
                        left: 0;
                        right: 0;
                        bottom: 15px;
                        margin: 0 auto;
                    }
                }

                .middle {
                    flex: 8;

                    input {
                        display: block;
                        width: 100%;
                        height: 36px;
                        border: none;
                        font-size: 1.4rem;
                        box-sizing: border-box;
                        padding: 0 10px;
                        background: #F6F6F6;
                    }
                }

                .right {
                    flex: 1;
                    position: relative;

                    .btn-return {
                        position: absolute;
                        width: 30px;
                        height: 30px;
                        left: 0;
                        right: 0;
                        top: 0;
                        bottom: 0;
                        margin: auto;

                        img {
                            display: block;
                            margin: auto;
                        }
                    }
                }
            }

            .list-search {
                width: 94.6%;
                height: 150px;
                box-sizing: border-box;
                border-radius: 5px;
                border: 1px solid @line;
                overflow: auto;
                position: absolute;
                z-index: 260;
                background: #fff;
                top: 80px;
                right: 0;
                left: 0;
                margin: 0 auto;

                li {
                    height: 30px;
                    box-sizing: border-box;
                    border-bottom: 1px solid @line;
                    line-height: 30px;
                    padding: 0 2.8%;
                }
            }

            .bg {
                position: fixed;
                width: 100%;
                height: 100%;
                z-index: 220;
                background: black;
                opacity: 0.4;
                top: 0;
                left: 0;
            }

        }

        section {
            margin-top: 92px;

            .tab {}

            .content {
                .ul-list {
                    li {
                        min-height: 70px;
                        box-sizing: border-box;
                        border-bottom: 1px solid @line;
                        position: relative;

                        .line-show {
                            min-height: 32px;
                            box-sizing: border-box;
                            padding: 12px 2.8% 6px 2.8%;

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

                        .recommend {
                            position: absolute;
                            line-height: 70px;
                            color: @blue;
                            right: 10px;
                            top: 0;
                        }
                    }
                }
                .no-data-logo{
                    width: 100%;
                    img{
                        display: block;
                        margin: 20px auto 0 auto;
                        width: 40%;
                    }
                    p{
                        text-align: center;
                        font-size: 1.4rem;
                        padding-top: 10px;
                    }
                }
            }
        }
    }
</style>