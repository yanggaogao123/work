<template>
    <div id="flightSearch" v-cloak>
        <nav class="tab">
            <ul class="flight_type">
                <li class="search_planeNo" @click="conShowClick(0)">
                    <span class="tab-btn">按航班号</span>
                    <div class="bottom-color" v-show="show.conShow"></div>
                </li>
                <li class="search_place" @click="conShowClick(1)">
                    <span class="tab-btn">按起降地</span>
                    <div class="bottom-color" v-show="show.conShow == false"></div>
                </li>
            </ul>
        </nav>
        <div class="con-planeNo" v-show="show.conShow">
            <div class="flight_ipt">
                <div class="flight_icon_plane"></div>
                <input class="ipt-planeNo" v-model="planeNoMsg" placeholder="请输入航班号" type="text">
            </div>
            <div class="flight_date">
                <div class="flight_icon_calendar"></div>
                <div class="flight_today" num="0" @click="planeNoClicktm(0)" :class="{'pick':show.planeNoToday}">今天</div>
                <div class="flight_tomorrow" num="1" @click="planeNoClicktm(1)" :class="{'pick':show.planeNoToday==false}">
                    明天</div>
            </div>
            <button class="flight_search" @click="planeNoSearch">查询</button>

            <div class="modal-history">
                <div class="flight_history clearfix">
                    <div class="history_tip fl">搜索历史</div>
                    <div class="fr btn-clear" @click="planeNoClearHis">
                        <div class="history_clear_icon"></div>
                        <div class="history_clear">清除历史</div>
                    </div>
                </div>
                <ul class="history_content_list">
                    <li class="history_content" v-for="(item,index) in planeNoHis">
                        <div class="his">
                            <span @click="planeNoHisClick(item)">{{item}}</span>
                            <div class="history_delete_icon" @click="planeNoDel(index)"></div>
                        </div>
                    </li>

                </ul>
            </div>
        </div>
        <div class="con-place" v-show="show.conShow == false">
            <div class="flight_place clearfix">
                <div class="start fl">
                    <span class="tit-start">出发地</span>
                    <div class="name-start" v-show="show.placeSearch">广州</div>
                    <div class="ipt-start" v-show="!show.placeSearch" :data="strMsg" @click="goCity">{{strMsg}}</div>
                </div>

                <img class="icon-change" @click="exchange" src="../assets/flight/change.png" alt="">

                <div class="arrival fr">
                    <span class="tit-arrival">到达地</span>
                    <div class="name-arrival" v-show="!show.placeSearch">广州</div>
                    <div class="ipt-arrival" v-show="show.placeSearch" :data="arrMsg" @click="goCity">{{arrMsg}}</div>
                </div>
            </div>

            <div class="flight_date">
                <div class="flight_icon_calendar"></div>
                <div class="flight_today" num="0" @click="placeClicktm(0)" :class="{'pick':show.placeToday}">今天
                </div>
                <div class="flight_tomorrow" num="1" @click="placeClicktm(1)" :class="{'pick':show.placeToday == false}">明天
                </div>
            </div>
            <button class="flight_search" @click="placeSearchIt()">查询</button>

            <div class="modal-history">
                <div class="flight_history clearfix">
                    <div class="history_tip fl">搜索历史</div>
                    <div class="fr btn-clear">
                        <div class="history_clear_icon"></div>
                        <div class="history_clear" @click="placeClearHis">清除历史</div>
                    </div>
                </div>
                <ul class="history_content_list">
                    <li class="history_content" v-for="(item,index) in placeHis">
                        <div class="his">
                            <span @click="placeHisClick(item)">{{item.name}}</span>
                            <div class="history_delete_icon" @click="placeDel(index)"></div>
                        </div>
                    </li>

                </ul>
            </div>
        </div>

    </div>
</template>

<script>
    // import "@/assets/font2/iconfont.css";
    import {
        flightSearch
    } from '@/api/api';
    export default {
        name: 'flightSearch',
        data() {
            return {
                show: {
                    // 按航班号、按起降地
                    conShow: true,
                    // 航班号，今明2天选择
                    planeNoToday: true,
                    // 其降低，今明2天选择
                    placeToday: true,
                    // 起降地出发到达转换
                    placeSearch: true
                },
                // 航班号历史记录
                planeNoHis: [
                    // 'QFF241',
                    // 'QFF242',
                    // 'QFF243',
                    // 'QFF244',
                    // 'QFF245',
                    // 'QFF246',
                ],
                // 起降地历史记录
                placeHis: [
                    // '上海浦东-广州',
                    // '广州-上海浦西',
                    // '北京-广州',
                    // '广州-南京',
                    // '内蒙古-广州',
                    // '广州-汕头',
                ],
                // 航班号今明2天传数据 0：今天 1：明天
                planeNotm: 0,
                // 起降地今明2天传数据 0：今天 1：明天
                placetm: 0,
                planeNoMsg: '',
                placeMsg: '',
                strMsg: '请输入',
                arrMsg: '请输入',
                strCode: '',
                arrCode: '',
                dateNow: '',
                dateTom: '',
            }
        },
        methods: {

            dataInit() {
                if (sessionStorage.getItem('city')) {
                    var city = JSON.parse(sessionStorage.getItem('city'));
                    var soa = sessionStorage.getItem('soa');
                    this.show.placeSearch = soa;
                    if (this.show.placeSearch === "true") {
                        this.show.placeSearch = true;
                        this.arrMsg = city.name;
                        this.arrCode = city.code;
                    } else {
                        this.show.placeSearch = false;
                        this.strMsg = city.name;
                        this.strCode = city.code;
                    }
                }
                if (sessionStorage.getItem('conShow') == null) {
                    this.show.conShow = true
                } else {
                    var conShow = sessionStorage.getItem('conShow')
                    if (conShow === "true") {
                        this.show.conShow = true;
                    } else {
                        this.show.conShow = false;
                    }
                }

                var d = new Date();
                d.setTime(d.getTime());
                this.dateNow = d.getFullYear().toString() + (d.getMonth() + 1).toString() + d.getDate().toString();
                d.setTime(d.getTime() + 24 * 60 * 60 * 1000);
                this.dateTom = d.getFullYear().toString() + (d.getMonth() + 1).toString() +  d.getDate().toString();
                console.log(this.dateNow, this.dateTom);

            },

            conShowClick(type) {
                if (type == 0) {
                    this.show.conShow = true;
                    sessionStorage.setItem('conShow', true)
                } else {
                    this.show.conShow = false;
                    sessionStorage.setItem('conShow', false)
                }
            },
            planeNoClicktm(type) {
                if (type == 0) {
                    this.show.planeNoToday = true;
                    this.planeNotm = 0;
                } else {
                    this.show.planeNoToday = false;
                    this.planeNotm = 1;
                }
            },
            planeNoClearHis() {
                this.planeNoHis = [];
                localStorage.setItem('planeNoHis', JSON.stringify(this.planeNoHis));
            },
            placeClicktm(type) {
                if (type == 0) {
                    this.show.placeToday = true;
                    this.placetm = 0;
                } else {
                    this.show.placeToday = false;
                    this.placetm = 1;
                }
            },
            planeNoDel(index) {
                this.planeNoHis.splice(index, 1);
                localStorage.setItem('planeNoHis', JSON.stringify(this.planeNoHis));
            },
            exchange() {
                this.show.placeSearch = !this.show.placeSearch;
                sessionStorage.setItem('soa', this.show.placeSearch);
                var msg = "";
                msg = this.arrMsg;
                this.arrMsg = this.strMsg
                this.strMsg = msg
            },
            placeClearHis() {
                this.placeHis = [];
                localStorage.setItem('placeHis', JSON.stringify(this.placeHis));
            },
            placeDel(index) {
                this.placeHis.splice(index, 1);
                localStorage.setItem('placeHis', JSON.stringify(this.placeHis));
            },
            goCity() {
                var a = this.show.placeSearch;
                // console.log(a);
                sessionStorage.setItem('soa', a)
                sessionStorage.setItem('conShow', false);
                // window.location.href = './flightSearchList.html';
                this.$router.push('flightSearchList');
            },
            planeNoHisClick(item) {
                this.planeNoMsg = item;
            },
            planeNoSearch() {
                var val = this.planeNoMsg;
                console.log(val);
                if (val.length <= 0 || val == '') {
                    this.$toast.fail('请填写航班号！');
                    return
                }

                this.planeNoHis.push(val);

                for (var i = 0, len = this.planeNoHis.length; i < len; i++) {
                    for (var j = i + 1; j < len; j++) {
                        if (this.planeNoHis[i] == this.planeNoHis[j]) {
                            this.planeNoHis.splice(j, 1);
                            len--;
                            j--;
                        }
                    }
                }

                localStorage.setItem('planeNoHis', JSON.stringify(this.planeNoHis));
                var searchItem = {};
                searchItem.time = this.planeNotm;
                searchItem.ffId = val;
                searchItem.type = 0;
                console.log(searchItem);
                sessionStorage.setItem('searchItem', JSON.stringify(searchItem));
                sessionStorage.setItem('conShow', true);
                // flightSearch({ffid:val,})
                this.$router.push({name:'flightList'})
            },

            placeSearchIt() {
                var searchItem = {};
                searchItem.time = this.placetm;
                searchItem.apcd = val;
                searchItem.type = 1;
                sessionStorage.removeItem('searchItem');
                // 出发地广州
                if (this.show.placeSearch) {
                    var val = this.arrMsg;
                    if (val == '请输入') {
                        this.$toast.fail('请选择到达地');
                        return;
                    }

                    var his = `广州-${val}`;
                    searchItem.status = "D";
                    // var city = JSON.parse(sessionStorage.getItem('city'));
                    // searchItem.apcd = city.code;
                } else { //到达地广州
                    var val = this.strMsg;
                    if (val == '请输入') {
                        this.$toast.fail('请选择出发地');
                        return;
                    }
                    var his = `${val}-广州`;
                    searchItem.status = "A";
                    // searchItem.apcd = this.strCode;
                }
                if (this.strCode != '') {
                    searchItem.apcd = this.strCode
                } else {
                    var city = JSON.parse(sessionStorage.getItem('city'));
                    searchItem.apcd = city.code;
                }
                // var city = JSON.parse(sessionStorage.getItem("city"));
                // searchItem.apcd = city.code;

                var json = {
                    'name': val,
                    'code': searchItem.apcd
                }
                sessionStorage.setItem('city', JSON.stringify(json));
                this.placeHis.push(json);

                for (var i = 0, len = this.placeHis.length; i < len; i++) {
                    for (var j = i + 1; j < len; j++) {
                        if (this.placeHis[i].name == this.placeHis[j].name) {
                            this.placeHis.splice(j, 1);
                            len--;
                            j--;
                        }
                    }
                }

                localStorage.setItem('placeHis', JSON.stringify(this.placeHis));
                sessionStorage.setItem('searchItem', JSON.stringify(searchItem));
                sessionStorage.setItem('conShow', false);

                this.$router.push('./flightList'); 
            },

            placeHisClick(item) {
                // console.log(123);
                console.log(this.show.placeSearch);
                // console.log(item);
                if (this.show.placeSearch) {
                    this.arrMsg = item.name;
                    this.arrCode = item.code;
                } else {
                    this.strMsg = item.name;
                    this.strCode = item.code;
                }
                sessionStorage.setItem('city',JSON.stringify({"name":item.name,"code":item.code}));
                console.log(this.arrMsg,this.arrCode);
            },

            localStorageHandle() {
                if (localStorage.getItem('placeHis')) {
                    var placeHis = JSON.parse(localStorage.getItem('placeHis'));
                    if (placeHis.length > 8) {
                        var a = placeHis.splice(-8, 8);
                        this.placeHis = a;
                    } else {
                        this.placeHis = placeHis;
                    }


                }
                if (localStorage.getItem('planeNoHis')) {
                    var planeNoHis = JSON.parse(localStorage.getItem('planeNoHis'));
                    if (planeNoHis.length > 8) {
                        var a = planeNoHis.splice(-8, 8);
                        this.planeNoHis = a;
                    } else {
                        this.planeNoHis = planeNoHis;
                    }
                }
            },


        },
        created() {
            this.dataInit();
            this.localStorageHandle();
        }
    }
</script>

<style lang="less" scoped>
    @blue: #24A1EE;
    @bg: #ededed;

    html,
    body {
        width: 100%;
        min-height: 100%;
    }

    body {
        background: @bg;
    }

    [v-cloak] {
        display: none;
    }

    #flightSearch {
        background: @bg;
        min-height: 100%;
        .tab {
            .flight_type {
                width: 100%;
                height: 48px;
                background: @blue;
                display: flex;

                li {
                    flex: 1;
                    position: relative;

                    span {
                        display: block;
                        width: 100%;
                        font-size: 1.4rem;
                        text-align: center;
                        line-height: 42px;
                        margin-top: 6px;
                        color: #fff;
                        font-family: PingFangSC-Medium, PingFang SC;
                    }

                    .bottom-color {
                        position: absolute;
                        width: 64px;
                        height: 2px;
                        background: #fff;
                        border-radius: 1px;
                        bottom: 0;
                        left: 0;
                        right: 0;
                        margin: 0 auto;
                    }
                }
            }
        }

        .con-planeNo {
            width: 100%;

            .flight_ipt {
                width: 90%;
                height: 44px;
                background: #fff;
                margin: 22px auto 0 auto;
                border-radius: 5px;

                .flight_icon_plane {
                    background: url("../assets/flight/flight_icon.png") no-repeat;
                    width: 14px;
                    height: 14px;
                    display: inline-block;
                    background-size: 14px 14px;
                    margin: 15px 10px;
                    vertical-align: top;
                }

                .ipt-planeNo {
                    display: inline-block;
                    width: 80%;
                    height: 40px;
                    margin-top: 2px;
                    border: none;
                    outline: none;
                    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
                }
            }


            .modal-history {
                .flight_history {
                    width: 90%;
                    margin: 10px auto;

                    .history_tip {
                        width: 48px;
                        height: 17px;
                        font-size: 12px;
                        font-family: PingFangSC-Regular, PingFang SC;
                        font-weight: 400;
                        color: rgba(51, 51, 51, 1);
                        line-height: 17px;
                    }

                    .history_clear {
                        width: 55px;
                        height: 17px;
                        font-size: 13px;
                        font-family: PingFangSC-Regular, PingFang SC;
                        font-weight: 400;
                        color: rgba(136, 136, 136, 1);
                        line-height: 17px;
                        display: inline-block;
                    }

                    .history_clear_icon {
                        background: url('../assets/flight/delete_group.png') no-repeat;
                        width: 12px;
                        height: 13px;
                        display: inline-block;
                        background-size: 12px 12px;
                        margin: 0px 4px;
                        background-position-y: 2px;
                    }
                }

                .history_content_list {
                    display: flex;
                    flex-wrap: wrap;
                    width: 90%;
                    margin: 0 auto;
                    font-size: 1.4rem;
                    justify-content: flex-start;

                    .history_content {
                        height: 40px;
                        margin-bottom: 6px;

                        .his {
                            width:max-content;
                            height: 40px;
                            background: #fff;
                            margin: 0 5px 0 0;
                            border-radius: 5px;
                            text-align: center;
                            padding: 0 0 0 5px;
                            span {
                                display: inline-block;
                                line-height: 40px;
                                overflow: hidden;
                                white-space: nowrap;
                                text-overflow: ellipsis;
                            }

                            .history_delete_icon {
                                display: inline-block;
                                background: url('../assets/flight/delete.png') no-repeat;
                                width: 12px;
                                height: 13px;
                                display: inline-block;
                                background-size: 12px 12px;
                                margin: 14px 4px 0 4px;
                                background-position-y: 2px;
                                vertical-align: top;
                            }
                        }

                    }

                }
            }
        }

        .con-place {
            width: 100%;

            .flight_place {
                width: 90%;
                height: 88px;
                border-radius: 5px;
                background: #fff;
                margin: 22px auto 16px auto;
                text-align: center;

                .start,
                .icon-change,
                .arrival {
                    display: inline-block;
                    color: #333333;
                }

                .start,
                .arrival {
                    width: 40%;
                    text-align: center;
                }

                .start {
                    .tit-start {
                        display: inline-block;
                        vertical-align: top;
                        font-size: 1.4rem;
                        margin: 18px 0 6px 0;
                    }

                    .tit-start:before {
                        display: inline-block;
                        content: '';
                        width: 16px;
                        height: 16px;
                        background: url('../assets/flight/take_off.png') no-repeat;
                        background-size: 16px 16px;
                        vertical-align: top;
                        margin: 0 4px;
                    }

                    .name-start {
                        font-size: 1.6rem;
                    }

                    .ipt-start {
                        font-size: 1.6rem;
                        color: #888888;
                        vertical-align: top;
                        width: 100%;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        overflow: hidden;
                    }

                    .ipt-start:after {
                        display: inline-block;
                        width: 12px;
                        height: 16px;
                        content: '';
                        background: url('../assets/flight/arrow.png') no-repeat;
                        background-size: 12px 16px;
                        margin: 4px 2px;
                        vertical-align: top;
                    }
                }

                .icon-change {
                    width: 34px;
                    margin-top: 27px;
                }

                .arrival {
                    .tit-arrival {
                        display: inline-block;
                        vertical-align: top;
                        font-size: 1.4rem;
                        margin: 18px 0 6px 0;
                    }

                    .tit-arrival:before {
                        display: inline-block;
                        content: '';
                        width: 16px;
                        height: 16px;
                        background: url('../assets/flight/flight_descend.png') no-repeat;
                        background-size: 16px 16px;
                        vertical-align: top;
                        margin: 0 4px;
                    }

                    .name-arrival {
                        font-size: 1.6rem;
                    }

                    .ipt-arrival {
                        font-size: 1.6rem;
                        color: #888888;
                        vertical-align: top;
                        width: 100%;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        overflow: hidden;
                    }

                    .ipt-arrival:after {
                        display: inline-block;
                        width: 12px;
                        height: 16px;
                        content: '';
                        background: url('../assets/flight/arrow.png') no-repeat;
                        background-size: 12px 16px;
                        margin: 4px 2px;
                        vertical-align: top;
                    }
                }
            }

            .modal-history {
                .flight_history {
                    width: 90%;
                    margin: 0 auto;

                    .history_tip {
                        width: 48px;
                        height: 17px;
                        font-size: 12px;
                        font-family: PingFangSC-Regular, PingFang SC;
                        font-weight: 400;
                        color: rgba(51, 51, 51, 1);
                        line-height: 17px;
                    }

                    .history_clear {
                        width: 55px;
                        height: 17px;
                        font-size: 13px;
                        font-family: PingFangSC-Regular, PingFang SC;
                        font-weight: 400;
                        color: rgba(136, 136, 136, 1);
                        line-height: 17px;
                        display: inline-block;
                    }

                    .history_clear_icon {
                        background: url('../assets/flight/delete_group.png') no-repeat;
                        width: 12px;
                        height: 13px;
                        display: inline-block;
                        background-size: 12px 12px;
                        margin: 0px 4px;
                        background-position-y: 2px;
                    }
                }

                .history_content_list {
                    display: flex;
                    flex-wrap: wrap;
                    width: 90%;
                    margin: 10px auto;
                    font-size: 1.4rem;

                    .history_content {
                        flex: 1;
                        width: 50%;
                        min-width: 50%;
                        max-width: 50%;
                        height: 40px;
                        margin-bottom: 6px;

                        .his {
                            width: 90%;
                            height: 40px;
                            background: #fff;
                            margin: 0 5%;
                            border-radius: 5px;
                            text-align: center;

                            span {
                                display: inline-block;
                                line-height: 40px;
                                width: 60%;
                                overflow: hidden;
                                white-space: nowrap;
                                text-overflow: ellipsis;
                            }

                            .history_delete_icon {
                                background: url('../assets/flight/delete.png') no-repeat;
                                width: 12px;
                                height: 13px;
                                display: inline-block;
                                background-size: 12px 12px;
                                margin: 14px 6px 0 6px;
                                background-position-y: 2px;
                                vertical-align: top;
                            }
                        }

                    }

                }
            }
        }
    }

    .flight_date {
        width: 90%;
        height: 44px;
        background: #fff;
        margin: 22px auto 0 auto;
        border-radius: 5px;
        font-size: 1.4rem;

        .flight_icon_calendar {
            background: url("../assets/flight/calendar.png") no-repeat;
            width: 14px;
            height: 14px;
            display: inline-block;
            background-size: 14px 14px;
            margin: 15px 10px;
        }

        .flight_today,
        .flight_tomorrow {
            display: inline-block;
            color: #888888;
            line-height: 44px;
            vertical-align: top;
        }

        .flight_today {
            margin-right: 16px;
        }

        .pick {
            color: @blue;
        }
    }

    .flight_search {
        width: 167px;
        height: 44px;
        background: linear-gradient(53deg, rgba(36, 161, 238, 1) 0%, rgba(36, 188, 238, 1) 100%);
        box-shadow: 0px 1px 8px 0px rgba(36, 161, 238, 0.5);
        border-radius: 22px;
        color: #fff;
        border: none;
        display: block;
        margin: 24px auto;
        font-size: 1.6rem;
        outline: none;
        -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
    }
</style>