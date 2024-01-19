<template>
    <div id="flightSearchList">
        <header>
            <nav>
                <div class="tab-ch" @click="tabClick(0)" :class="{'pick':tabshow}">国内</div>
                <div class="tab-in" @click="tabClick(1)" :class="{'pick':!tabshow}">港澳台/国际</div>
            </nav>
            <div class="hidden-modal"></div>
            <div class="ipt-city clearfix">
                <input type="text" placeholder="请输入城市名称" @keyup="importSth(cityMsg)" class="ipt fl" v-model="cityMsg">
                <img src="../assets/flight/search.png" class="icon-search fr" alt="">
            </div>
            <ul class="search-list" v-show="tabList">
                <li v-for="(item,index) in searchData" @click="ListClick(item.name,item.code)">{{item.name}}</li>

            </ul>
            <div class="bg" @click="hideList" v-show="tabList"></div>
            <div class="modal-history">
                <div class="flight_history clearfix">
                    <div class="history_tip fl">搜索历史</div>
                    <div class="fr btn-clear">
                        <div class="history_clear_icon"></div>
                        <div class="history_clear" @click="cityClearHis">清除历史</div>
                    </div>
                </div>
                <ul class="history_content_list">
                    <li class="history_content" v-for="(item,index) in cityHis">
                        <div class="his">
                            <span @click="hisClick(item.name,item.code)">{{item.name}}</span>
                            <div class="history_delete_icon" @click="cityDel(index)"></div>
                        </div>
                    </li>
                </ul>
            </div>
        </header>

        <div class="city" @scroll="sc">
            <div class="city-wrapper city-wrapper-hook">
                <div class="scroller-hook">
                    <div class="cities cities-hook">
                        <template v-for="itemName in cityData">
                            <div class="title" :id="itemName.name">{{itemName.name}}</div>
                            <ul>
                                <li class="item" v-for="(item, index) in itemName.cities" :data-id="item.code"><span class="border-1px name" @click="cityClick(item.airPort,item.code)">{{item.airPort}}</span></li>
                            </ul>
                        </template>
                    </div>
                </div>

            </div>
            <div class="shortcut shortcut-hook">
                <ul>
                    <li v-for="item in cityData" @touchstart="ts(item.name.substr(0,1))" :data-anchor="item.name.substr(0,1)" class="item">
                        {{item.name.substr(0,1)}}</li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
    import {
        getAirPortCode
    } from '@/api/api';
    export default {
        data() {
            return {
                query: {
                    code: "0" //0：请求中国大陆的机场，1为请求国外机场
                },
                chineseAirPort: null,
                foreignAirPort: null,
                airPort: null,
                // scroll: null,
                tabshow: true,
                tabList: false,
                // 起降地历史记录
                cityHis: [

                ],
                // 
                scroll: null,
                anchorMap: {},
                shortcutList: [],
                cityData: '',
                cityMsg: '',
                searchData: [

                ]
            }
        },
        methods: {
            dataInit() {
                if (localStorage.getItem('cityHis')) {
                    var cityHis = JSON.parse(localStorage.getItem('cityHis'));
                    if (cityHis.length > 6) {
                        var a = cityHis.splice(-6, 6);
                        this.cityHis = a;
                    } else {
                        this.cityHis = cityHis;
                    }
                }
            },

            handleSearch: function() {
                if (this.query.code == "1") {
                    this.cityData = this.foreignAirPort
                } else {
                    this.cityData = this.chineseAirPort
                }
            },
            handleAirPortSort: function(AirPortList) {
                if (!String.prototype.localeCompare)
                    return null;

                var letters = "ABCDEFGHJKLMNOPQRSTWXYZ".split('');
                var zh = "阿八嚓哒妸发旮哈讥咔垃痳拏噢妑七呥扨它穵夕丫帀".split('');

                var segs = [];
                var curr;
                var y = 0;
                letters.forEach(function(item, i) {
                    curr = {
                        name: item,
                        cities: []
                    };
                    AirPortList.forEach(function(item2) {
                        if ((!zh[i] || zh[i].localeCompare(item2.airPort) <= 0)) {
                            if (i < zh.length) {
                                if (item2.airPort.localeCompare(zh[i + 1]) == -1) {
                                    curr.cities.push(item2);
                                }
                            } else {
                                if (item2.airPort.localeCompare(zh[i]) == -1) {
                                    curr.cities.push(item2);
                                }
                            }
                        }
                    });
                    curr.scrollName = '#' + curr.name;
                    if (curr.cities.length) {
                        segs.push(curr);
                        curr.cities.sort(function(a, b) {
                            return a.airPort.localeCompare(b.airPort);
                        });
                    } else {
                        segs.push(curr);
                    }
                });
                return segs;
            },


            tabClick: function(type) {
                switch (type) {
                    case 0:
                        this.tabshow = true;
                        this.query.code = 0
                        this.handleSearch();
                        break;
                    case 1:
                        this.tabshow = false;
                        this.query.code = 1
                        this.handleSearch();
                        break;
                    default:
                        break;
                }
            },
            cityClearHis: function() {
                this.cityHis = [];
                localStorage.setItem('cityHis', JSON.stringify(this.cityHis));
            },
            cityDel(index) {
                this.cityHis.splice(index, 1);
                localStorage.setItem('cityHis', JSON.stringify(this.cityHis));
            },
            hisClick(name, code) {
                var city = {
                    'name': name,
                    'code': code
                }
                sessionStorage.setItem('city', JSON.stringify(city));
                // window.location.href = "./flightSearch.html";
                this.$router.push('flightSearch');
            },

            ts: function(data) {
                console.log(data);
                var a = document.querySelector('#' + data);
                var s = a.offsetTop - 240;
                document.querySelector(".city").scrollTop = s
            },

            cityClick(data1, data2) {
                console.log(data1, data2);
                var city = {
                    'name': data1,
                    'code': data2
                }

                this.cityHis.push(city);

                for (var i = 0, len = this.cityHis.length; i < len; i++) {
                    for (var j = i + 1; j < len; j++) {
                        if (this.cityHis[i].name == this.cityHis[j].name) {
                            this.cityHis.splice(j, 1);
                            len--;
                            j--;
                        }
                    }
                }

                localStorage.setItem('cityHis', JSON.stringify(this.cityHis));

                sessionStorage.setItem('city', JSON.stringify(city));
                // window.location.href = "./flightSearch.html";
                this.$router.push('flightSearch');
            },

            sc() {
                var a = document.querySelector('.city').scrollTop;
                // console.log(a);
                // console.log(document.querySelector("#B").offsetTop);
            },

            importSth(cityMsg) {
                this.tabList = true;
                var data = this.cityData;
                var arr = [];
                for (var i = 0; i < data.length; i++) {
                    if (data[i].cities.length > 0) {
                        for (var k = 0; k < data[i].cities.length; k++) {
                            if (data[i].cities[k].airPort.indexOf(cityMsg) != -1) {
                                var a = {
                                    'name': data[i].cities[k].airPort,
                                    'code': data[i].cities[k].code
                                }
                                arr.push(a);
                            }
                        }
                    }
                }
                this.searchData = arr;
            },
            ListClick(name, code) {
                var city = {
                    'name': name,
                    'code': code
                }
                this.cityHis.push(city);

                for (var i = 0, len = this.cityHis.length; i < len; i++) {
                    for (var j = i + 1; j < len; j++) {
                        if (this.cityHis[i].name == this.cityHis[j].name) {
                            this.cityHis.splice(j, 1);
                            len--;
                            j--;
                        }
                    }
                }

                localStorage.setItem('cityHis', JSON.stringify(this.cityHis));

                sessionStorage.setItem('city', JSON.stringify(city));
                // window.location.href = "./flightSearch.html";
                this.$router.push('flightSearch');
            },

            hideList() {
                this.tabList = false;
            }

        },
        created: function() {
            // this.cityData = cityData;
            this.dataInit();

            getAirPortCode({
                'code': "0"
            }).then(res => {
                if (res.retCode == 0) {
                    this.chineseAirPort = this.handleAirPortSort(res.retData);
                    console.log(this.chineseAirPort);
                    this.cityData = this.chineseAirPort;
                    console.log(this.cityData)
                }
            })

            getAirPortCode({
                'code': "1"
            }).then(res => {
                if (res.retCode == 0) {
                    if (res.retCode == 0) {
                        this.foreignAirPort = this.handleAirPortSort(res.retData);
                    }
                }
            })
        }
    }
</script>

<style lang="less" scoped>
    html,
    body {
        width: 100%;
        height: 100%;
        overflow: hidden;
    }

    body {
        height: 100%;
    }

    #flightSearchList {
        width: 100%;
        height: 100%;

        position: relative;
    }

    #flightSearchList header {
        width: 100%;
        height: 240px;
        background: #fff;
    }

    header nav {
        width: 200px;
        height: 32px;
        border-radius: 5px;
        box-sizing: border-box;
        display: flex;
        border: 1px solid #24A1EE;
        margin: 12px auto 16px auto;
        position: relative;
    }

    header nav div {
        display: inline-block;
        flex: 1;
        width: 50%;
        font-size: 1.4rem;
        text-align: center;
        line-height: 32px;
        color: #24A1EE;
    }

    header nav .pick {
        color: #fff;
        background: #24A1EE;
    }

    header .hidden-modal {
        width: 90%;
        height: 44px;
        border-radius: 5px;
        background: #F4F4F4;
        margin: 0 auto;
        visibility: hidden;
    }

    header .ipt-city {
        width: 90%;
        height: 44px;
        border-radius: 5px;
        background: #F4F4F4;
        top: 48px;
        left: 0;
        right: 0;
        margin: 0 auto;
        position: absolute;
        z-index: 3;
    }

    header .ipt-city .ipt {
        width: 80%;
        height: 40px;
        font-size: 1.4rem;
        display: inline-block;
        margin: 2px 0 0 4%;
        vertical-align: top;
        background: #F4F4F4;
        outline: none;
        border: none;
        -webkit-tap-highlight-color: rgba(0, 0, 0, 0);

    }

    header .ipt-city .icon-search {
        display: inline-block;
        vertical-align: top;
        margin: 14px 4% 0 0;
        width: 18px;
        height: 18px;

    }

    header .search-list {
        width: 90%;
        height: 120px;
        position: absolute;
        top: 92px;
        left: 0;
        right: 0;
        margin: 0 auto;
        border-radius: 5px;
        overflow: scroll;
        z-index: 3;
    }

    header .search-list li {
        width: 100%;
        height: 30px;
        box-sizing: border-box;
        border-bottom: 1px solid #E4E4E4;
        background: #fff;
        line-height: 25px;
        padding-left: 20px;

    }

    header .bg {
        position: fixed;
        width: 100%;
        height: 100%;
        background: black;
        opacity: 0.4;
        top: 0;
        left: 0;
        z-index: 1;
    }

    .modal-history .flight_history {
        width: 90%;
        margin: 10px auto;
    }

    .modal-history .flight_history .history_tip {
        width: 48px;
        height: 17px;
        font-size: 12px;
        font-family: PingFangSC-Regular, PingFang SC;
        font-weight: 400;
        color: #333333;
        line-height: 17px;
    }

    .modal-history .flight_history .history_clear {
        width: 55px;
        height: 17px;
        font-size: 13px;
        font-family: PingFangSC-Regular, PingFang SC;
        font-weight: 400;
        color: #888888;
        line-height: 17px;
        display: inline-block;
    }

    .modal-history .flight_history .history_clear_icon {
        background: url('../assets/flight/delete_group.png') no-repeat;
        width: 12px;
        height: 13px;
        display: inline-block;
        background-size: 12px 12px;
        margin: 0px 4px;
        background-position-y: 2px;
    }

    .modal-history .history_content_list {
        display: flex;
        flex-wrap: wrap;
        width: 90%;
        margin: 0 auto;
        font-size: 1.4rem;
    }

    .modal-history .history_content_list .history_content {
        flex: 1;
        width: 32%;
        height: 40px;
        min-width: 32%;
        max-width: 32%;
        margin-bottom: 12px;
    }

    .modal-history .history_content_list .history_content .his {
        width: 86%;
        height: 40px;
        background: #fff;
        margin: -1px 1%;
        border-radius: 5px;
        text-align: center;
        border: 1px solid #E4E4E4;
        box-sizing: border-box;
    }

    .modal-history .history_content_list .history_content .his span {
        width: 68%;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        display: inline-block;
        line-height: 40px;
        vertical-align: middle;
    }

    .modal-history .history_content_list .history_content .his .history_delete_icon {
        background: url(../assets/flight/delete.png) no-repeat;
        width: 12px;
        height: 13px;
        display: inline-block;
        background-size: 12px 12px;
        margin: 12px 4px 0px 4px;
        background-position-y: 2px;
        vertical-align: top;
    }



    .city {
        display: block;
        height: calc(100% - 240px);
        overflow: scroll;
        position: relative;
        /* overflow: hidden; */
    }

    .city .city-wrapper {
        width: 100%;
        z-index: 1;
        overflow: scroll;

    }

    .city .city-wrapper::-webkit-scrollbar {
        display: none;
    }

    .city .city-wrapper .cities .title {
        height: 28px;
        padding-left: 16px;
        line-height: 28px;
        background-color: #f5f5f5;
        font-family: Helvetica;
        font-size: 1.2rem;
        color: #878787;
    }

    .city .city-wrapper .cities .item {
        height: 44px;
        padding: 0 16px;
        line-height: 44px;
        font-size: 1.6rem;
    }

    .city .city-wrapper .cities .item .name {
        display: block;
        position: relative;
    }

    .city .city-wrapper .cities .item .name:before,
    .city .city-wrapper .cities .item .name:after {
        display: block;
        position: absolute;
        border-top: 1px solid #e5e5e5;
        left: 0;
        width: 100%;
        content: ' ';
    }

    .city .city-wrapper .cities .item .name:before {
        display: none;
        top: 0;
    }

    .city .city-wrapper .cities .item .name:after {
        display: block;
        bottom: 0;
    }

    .city .city-wrapper .cities .item:active {
        background-color: #f0f0f0;
    }

    .city .city-wrapper .cities .item:last-child .name:after {
        display: none;
    }

    .shortcut {
        position: fixed;
        z-index: 30;
        width: 40px;
        right: 0;
        top: 240px;
        /* bottom: 0; */
        /* margin : auto 0; */
        font-family: Helvetica;
    }

    .shortcut .item {
        height: 12px;
        padding-top: 4px;
        padding-left: 24px;
        text-align: center;
        font-size: 1.2rem;
        color: #24A1EE;
    }

    @media only screen and (max-height: 600px) {
        .shortcut .item {
            padding-top: 3px;
        }
    }
</style>