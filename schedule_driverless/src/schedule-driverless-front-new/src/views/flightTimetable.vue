<template>
    <div id="flightTimetable">
        <div id="wrapper">
            <div class="tit">
                <img src="../assets/flight/logo-rule.png" @click="goRule" alt="">
            </div>
            <header>
                <ul class="btn-select">
                    <li class="btn-go" :class="{ 'pick-it': pickBool }" v-on:click="tab1">
                        <p>到达</p>
                        <div class="gd-co"></div>
                    </li>
                    <li class="btn-arrival" :class="{ 'pick-it': !pickBool }" v-on:click="tab2">
                        <p>出发</p>
                        <div class="gd-co"></div>
                    </li>
                </ul>

                <!-- <div class="select">
          <div class="select-main" v-on:click="select">
            <span>{{ seleName }}</span>
            <i
              class="fa fa-caret-down"
              :class="seleBool ? 'fa-caret-down' : 'fa-caret-up'"
            ></i>
             <i class="fa fa-caret-up"></i>
          </div>
        <ul class="select-list" v-show="!seleBool">
            <li
              v-for="(item, index) in list"
              :val="item.name"
              @click="selectClick(item.name)"
            >
              {{ item.name }}
            </li>
          </ul> 
        </div> -->
            </header>
            <section>
                <ul class="name">
                    <li>航空公司</li>
                    <li>航班号</li>
                    <li v-show="!pickBool">入港站</li>
                    <li v-show="pickBool">出港站</li>
                    <li v-show="!pickBool">计划出港</li>
                    <li v-show="pickBool">计划入港</li>
                    <li v-show="!pickBool">实际出港</li>
                    <li v-show="pickBool">实际入港</li>
                    <li>当前状态</li>
                    <li>状态</li>
                </ul>
                <div class="mescroll" id="timeTable">
                    <div id="timeScroll">
                        <ul v-for="(item, i) in dataList">
                            <li v-if="item.airlines" class="li-company">
                                 <marquee v-if="item.airlines.length > 3" scrollamount="3">{{item.airlines}}</marquee>
                                <template v-else>
                                    {{ item.airlines }}
                                </template>
                            </li>
                            <li>
                                <span scrollamount="3">{{ item.ffid.split('(')[0] }}</span>
                                <div>{{item.ffid.split('(')[1].split(')')[0]}}</div>
                            </li>
                            <li>
                                <marquee v-if="item.cnnms.length > 3" scrollamount="3">{{item.cnnms}}</marquee>
                                <p v-else>{{ item.cnnms }}</p>
                            </li>
                            <li v-show="!pickBool">
                                <p v-if="item.fptt != null && item.fptt.length > 4">
                                    {{ item.fptt.split(" ")[1].split(":")[0] }}:{{ item.fptt.split(" ")[1].split(":")[1] }}
                                </p>
                                <p v-else>{{ item.fptt }}</p>
                            </li>
                            <li v-show="pickBool">
                                <p v-if="item.fplt != null && item.fplt.length > 4">
                                    {{ item.fplt.split(" ")[1].split(":")[0] }}:{{ item.fplt.split(" ")[1].split(":")[1] }}
                                </p>
                                <p v-else>{{ item.fplt }}</p>
                            </li>
                            <li v-show="!pickBool">
                                <p v-if="item.frtt != null && item.frtt.length > 4">
                                    {{ item.frtt.split(" ")[item.frtt.split(" ").length-1].split(":")[0] }}:{{item.frtt.split(" ")[item.frtt.split(" ").length-1].split(":")[1]}}
                                </p>
                                <p v-else>{{ item.frtt }}</p>
                            </li>
                            <li v-show="pickBool">
                                <p v-if="item.frlt != null && item.frlt.length > 4">
                                    {{ item.frlt.split(" ")[item.frlt.split(" ").length-1].split(":")[0] }}:{{ item.frlt.split(" ")[item.frlt.split(" ").length-1].split(":")[1] }}
                                </p>
                                <p v-else>{{ item.frlt }}</p>
                            </li>
                            <li class="stat">{{ item.stat }}</li>
                            <li>{{ item.isdelay }}</li>
                        </ul>
                    </div>

                    <!-- <div id="timeScroll">
                        <ul>
                            <li>CZ3999</li>
                            <li v-show="!pickBool">15:00</li>
                            <li v-show="pickBool">15:00</li>
                            <li v-show="!pickBool">北京市</li>
                            <li v-show="pickBool">北京市</li>
                            <li v-show="!pickBool">CDEF</li>
                            <li v-show="pickBool">CDEF</li>
                            <li v-show="!pickBool">15:00</li>
                            <li v-show="pickBool">9:00-9:00</li>
                            <li>ok</li>
                        </ul>
                       
                    </div> -->
                </div>

                <p class="tips" v-show="!dataList.length > 0">暂无数据</p>
            </section>
            <div id="tips">*航班状态信息仅供参考，具体请以实际为准</div>
            <footer>
                <div class="btn-search" @click="flightsSearch()">
                    <i class="fa fa-plane"></i>
                    <span>航班查询</span>
                </div>
            </footer>
        </div>
    </div>
</template>

<script>
    import "@/assets/font-awesome/css/font-awesome.min.css";
    import {
        flightTimeTable,
        flightSearch
    } from "@/api/api";
    export default {
        name: "flightTimetable",
        data() {
            return {
                pickBool: true,
                seleBool: true,
                seleName: "T1",
                //   list: [{ name: "T1" }, { name: "T2" }],
                dataList: [],
                dateNow: '',
                dateTom: '',
            };
        },
        beforeCreate() {
            document.title = '航班';
        },
        created() {
            this.dataInit();

        },
        mounted() {},
        methods: {
            goRule() {
                window.location.href = 'https://news.sina.cn/project/fy2020/covid_19_trip.d.html?ua'
            },
            dataInit() {
                let that = this;
                var d = new Date();
                d.setTime(d.getTime());
                this.dateNow = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
                d.setTime(d.getTime() + 24 * 60 * 60 * 1000);
                this.dateTom = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
                console.log(this.dateNow, this.dateTom);

                this.getData();
            },
            tab1() {
                this.pickBool = true;
                this.getData();
                this.dataList = [];
            },
            tab2() {
                this.pickBool = false;
                this.getData();
                this.dataList = [];
            },
            select() {
                this.seleBool = !this.seleBool;
            },
            selectClick(item) {
                this.seleName = item;
                this.getData();
                this.seleBool = true;
            },
            getData() {
                if (this.pickBool == true) {
                    var type = "A";
                } else {
                    var type = "D";
                }
                let data = {
                    stype: type,
                };
                flightTimeTable(data).then((res) => {
                    console.log(res);
                    let dataList = res.retData;
                    dataList.forEach((item, index, arr) => {
                        let ffid = item.ffid.split("-");
                        item.ffid = ffid[0] + "-" + ffid[1] + "(" + item.fatt + ":" + item.tmcd + ")";
                        let cnnms = item.cnnms.split(",");
                        if (type == "A") {
                            item.cnnms = cnnms[0];
                        } else {
                            item.cnnms = cnnms[1];
                        }
                        arr[index] = item;
                    });
                    this.dataList = dataList;
                });
            },
            flightsSearch() {
                this.$router.push('./flightSearch');
            }
        },
    };
</script>

<style scoped lang="less">
    #flightTimetable {
        width: 100%;
        height: 100%;
        background: #eeeeee;
        position: relative;
    }

    .tit {
        width: 100%;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 3;

        img {
            width: 100%;
        }
    }

    header {
        width: 100%;
        height: 44px;
        background: #fff;
        position: fixed;
        top: 34px;
        left: 0;
        z-index: 2;
    }

    header .btn-select {
        width: 50%;
        height: 44px;
        display: flex;
        margin: 0 auto;
        text-align: center;
        position: relative;
    }

    header .btn-select li {
        flex: 1;
        line-height: 44px;
        position: relative;
    }

    header .btn-select li p {
        font-size: 1.4rem;
        color: #888888;
    }

    header .btn-select li .gd-co {
        position: absolute;
        width: 60%;
        height: 3px;
        /* background: #FD7B2D; */
        bottom: 0;
        left: 0;
        right: 0;
        margin: 0 auto;
        border-radius: 5px;
    }

    header .btn-select .pick-it p {
        color: #fd7b2d;
    }

    header .btn-select .pick-it .gd-co {
        background: #fd7b2d;
    }

    header .select {
        position: absolute;
        width: 10%;
        /* height: 44px; */
        top: 0;
        right: 0;
    }

    header .select .select-main {
        width: 100%;
        height: 44px;
    }

    header .select .select-main span {
        display: inline-block;
        line-height: 44px;
    }

    header .select .select-main .fa {
        font-size: 1.6rem;
        margin: 0 2px;
        line-height: 44px;
    }

    header .select .select-list {
        /* display: none; */
        width: 100%;
        position: absolute;
        right: 0;
        bottom: -80px;
        background: #fff;
        z-index: 999;
    }

    header .select .select-list li {
        width: 100%;
        height: 40px;
        line-height: 40px;
        text-align: center;
    }

    section {
        width: 100%;
        padding-top: 84px;
        height: calc(100% - 84px);
        position: relative;
        font-size: 1.3rem;
        
    }

    section .name {
        width: 100%;
        height: 32px;
        display: flex;
        background: #ededed;
        z-index: 1;
        position: fixed;
        top: 78px;
        left: 0;
    }

    section ul{
        margin-bottom: 0;
    }

    section .name li {
        flex: 1;
        text-align: center;
        line-height: 32px;
        white-space: nowrap;
        overflow-x: scroll;
        &::-webkit-scrollbar {display:none}
    }

    section .mescroll {
        width: 100%;
        overflow: scroll;
        padding: 30px 0 80px 0;
    }

    section .mescroll #timeScroll {}

    section .mescroll ul {
        width: 100%;
        height: 48px;
        display: flex;
        background: blue;
    }

    section .mescroll ul li {
        flex: 1;
        text-align: center;
        line-height: 48px;
        // overflow-y: scroll;
        height: 48px;
    }

    // section .mescroll ul li:nth-child(1) {
    //     line-height: 24px;
    //     height: 48px;
    //     overflow: scroll;
    // }

    // .li-company{
    //     height: 48px;
    //     line-height: 24px;
    //     overflow-y: scroll;  
    // }

    .stat{
         height: 48px;
        line-height: 48px;
        overflow-y: scroll;
    }

    section .mescroll ul li:nth-child(2) {
        height: 48px;
        overflow: scroll;
        span {
            display: block;
            line-height: 24px;
            font-size: 1.2rem;
        }

        div {
            display: block;
            line-height: 24px;
            height: 24px;
        }
    }


    section .mescroll ul li:nth-child(5) span {
        display: block;
        line-height: 16px;
    }

    section .mescroll ul li:nth-child(5) span:nth-child(1) {
        margin-top: 8px;
    }

    section .mescroll ul:nth-child(2n-1) {
        background: #fff;
    }

    section .mescroll ul:nth-child(2n) {
        background: #dfdfdf;
    }

    section .tips {
        font-size: 1.4rem;
        text-align: center;
        color: gray;
    }

    #tips {
        position: fixed;
        width: 100%;
        height: 28px;
        line-height: 28px;
        background: #eeeeee;
        text-align: center;
        color: red;
        bottom: 0px;
        left: 0;
        z-index: 2;
    }

    footer {
        position: fixed;
        width: 200px;
        height: 40px;
        bottom: 45px;
        right: 0;
        left: 0;
        margin: 0 auto;
    }

    footer .btn-search {
        position: absolute;
        width: 100%;
        height: 100%;
        background: linear-gradient(53deg, rgba(36, 161, 238, 1) 0%, rgba(36, 188, 238, 1) 100%);
        border-radius: 20px;
        text-align: center;
        color: #fff;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        margin: auto;
    }

    footer .btn-search .fa {
        display: inline-block;
        font-size: 1.4rem;
        line-height: 40px;
        margin: 0 2px;
    }

    footer .btn-search span {
        display: inline-block;
        line-height: 40px;
    }
</style>