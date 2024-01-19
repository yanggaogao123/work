<template>
    <div class="busPlanningSearchH5">
        <div id="container" hidden></div>
        <header>
            <div class="ipt-search">
                <div class="left">
                    <div class="logo-loc"></div>
                    <div class="logo-line"></div>
                    <div class="logo-go"></div>
                </div>
                <div class="middle">
                    <input type="text" autocomplete="off" id="start-input" @focus="changeInput(0)" @keyup="mapSearch" class="start-input" placeholder="您要从哪里出发" v-model="msg.stMsg">
                    <input type="text" autocomplete="off" id="end-input" @focus="changeInput(1)" @keyup="mapSearch" class="end-input" placeholder="您要去哪里" v-model="msg.endMsg">
                </div>
                <div class="right">
                    <div class="btn-return" @click="changeType">
                        <img src="../assets/arrow.png" alt="">
                    </div>
                </div>
            </div>
            <button class="btn-search" @click="searchIt">
                查询
            </button>
            <ul class="list-search" v-show='show.slShow'>
                <li v-for="(item,index) in searchList" @click="listClick(item)">
                    {{item.name}}
                </li>
            </ul>
            <div class="bg" @click="bgClick" v-show='show.slShow'></div>
        </header>
        <section>
            <div class="tit">历史记录</div>
            <ul class="his-list">
                <li v-for="(item,index) in locHis" @click="hisClick(item)">
                    <van-swipe-cell>
                        <div class="content">
                            <div class="left">
                                <div class="logo-loc"></div>
                                <div class="logo-line"></div>
                                <div class="logo-go"></div>
                            </div>
                            <div class="right">
                                <div class="text-start">{{typeof item.stLoc == 'string'?item.stLoc:'我的位置'}}</div>
                                <div class="text-end">{{typeof item.endLoc == 'string'?item.endLoc:'我的位置'}}</div>
                                <i class="iconfont icon-more1"></i>
                            </div>
                        </div>
                        <template slot="right">
                            <van-button square type="danger" text="删除" />
                        </template>
                    </van-swipe-cell>
                </li>
                
                <p @click="clearHis">清空搜索历史</p>
            </ul>
        </section>
    </div>
</template>

<script>
import {warningOk} from '@/utils/warning';
import {haha} from '@/utils/warning';
import {warning} from '@/utils/warning';
    export default {
        name: "busPlanningSearchH5",
        data() {
            return {
                show: {
                    slShow: false,
                },
                msg: {
                    stMsg: '我的位置',
                    endMsg: ''
                },
                myLoc: {
                    lng:"",
                    lat:""
                },
                realAddress:'',
                map:"",
                searchList:[],
                seInput: 0,
                locHis:[],
            }
        },
        created() {
            this.dataInit();
            this.mapInit();
            this.mapSearch();
        },
        mounted() {

        },
        methods: {
            dataInit(){
                let loc = JSON.parse(sessionStorage.getItem("localtion"));
                this.myLoc.lng = loc.localLng;
                this.myLoc.lat = loc.localLat;
                console.log(this.myLoc);
                let locHis = JSON.parse(localStorage.getItem("locHis"));
                console.log(locHis);
                this.locHis = locHis;
            },
            mapInit() {
                let that = this;
                this.map = new AMap.Map("container", {
                    resizeEnable: true
                });
                if(this.msg.stMsg == '我的位置' || this.msg.endMsg == '我的位置'){
                    var lnglat = [this.myLoc.lng,this.myLoc.lat];
                    that.map.plugin('AMap.Geocoder', function() {
                        var geocoder = new AMap.Geocoder({
                            city: '广州'
                        })
                        geocoder.getAddress(lnglat, function(status, result) {
                            if (status === 'complete' && result.info === 'OK') {
                                // result为对应的地理位置详细信息
                                console.log(result);
                                that.realAddress = result.regeocode.formattedAddress;
                                console.log(that.realAddress);
                            }
                        })
                    })
                }
            },
            mapSearch(){
                let that = this;
                let keywords;
               
                if(that.seInput == 0){
                    keywords = that.msg.stMsg;
                }else if(this.seInput == 1){
                    keywords = that.msg.endMsg;
                }
                
                this.map.plugin('AMap.Autocomplete', function(){
                    // 实例化Autocomplete
                    var autoOptions = {
                    city: '广州'
                    }
                    var autoComplete = new AMap.Autocomplete(autoOptions);
                    autoComplete.search(keywords, function(status, result) {
                    // 搜索成功时，result即是对应的匹配数据
                        console.log(result);
                        if(result.info == "OK"){
                            let arr = []
                            for(var i=0;i<result.tips.length;i++){
                                if(result.tips[i].id != ''){
                                    arr.push(result.tips[i]);
                                }
                            }
                            console.log(arr);
                            that.searchList = arr;
                            that.show.slShow = true;
                        }
                    })
                })
            },
            changeInput(data){
                this.seInput = data;
                console.log(this.seInput);
            },
            listClick(data){
                if(this.seInput == 0){
                    this.msg.stMsg = data.name;
                }else if(this.seInput == 1){
                    this.msg.endMsg = data.name;
                }
                this.show.slShow = false;
            },
            bgClick(){
                this.show.slShow = false;
            },
            changeType(){
                let a = this.msg.stMsg;
                this.msg.stMsg = this.msg.endMsg;
                this.msg.endMsg = a; 
            },
            searchIt(){
                let that = this;
                if(this.msg.stMsg == '' || this.msg.endMsg == ''){
                    warning.warningDanger('请填写出发地与到达地');
                    return;
                }
                if(this.msg.stMsg == '我的位置'){
                    var stLoc = this.realAddress;
                    var endLoc = this.msg.endMsg;
                    var loc={
                        stLoc: stLoc,
                        endLoc: endLoc
                    }
                    console.log(loc);
                }else if(this.msg.endMsg == '我的位置'){
                    var stLoc = this.msg.stMsg;
                    var endLoc = this.realAddress;
                    var loc={
                        stLoc: stLoc,
                        endLoc: endLoc
                    }
                }else{
                    var stLoc = this.msg.stMsg;
                    var endLoc = this.msg.endMsg;
                    var loc={
                        stLoc: stLoc,
                        endLoc: endLoc
                    }
                }
                sessionStorage.setItem("loc",JSON.stringify({stLoc:that.msg.stMsg,endLoc:that.msg.endMsg}));
                
                if(localStorage.getItem('locHis')){
                    var arr = JSON.parse(localStorage.getItem('locHis'));
                    console.log(arr);
                    let a = 0;
                    arr.unshift({stLoc:that.msg.stMsg,endLoc:that.msg.endMsg});
                    for(let i = 0; i < arr.length; i++) {
                        for(let j = i + 1; j < arr.length ; j++) {
                            if(JSON.stringify(arr[i].stLoc) == JSON.stringify(arr[j].stLoc) && JSON.stringify(arr[i].endLoc) == JSON.stringify(arr[j].endLoc)) {
                                arr.splice(j, 1);
                                j--;
                            }
                        }
                    }
                    console.log(arr);
                    localStorage.setItem('locHis',JSON.stringify(arr));
                }else{
                    var arr = [];
                    arr.push({stLoc:that.msg.stMsg,endLoc:that.msg.endMsg});
                    localStorage.setItem('locHis',JSON.stringify(arr));
                }   


                this.$router.push({
                    name: 'busPlanningShowH5'
                })
            },
            hisClick(item){
                console.log(item);
                sessionStorage.setItem("loc",JSON.stringify(item));
                this.$router.push({
                    name: 'busPlanningShowH5'
                })
            },
            clearHis(){
                this.locHis = [];
                let arr = [];
                localStorage.setItem('locHis',JSON.stringify(arr));
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

    .busPlanningSearchH5 {
        width: 100%;
        height: 100%;
        overflow: hidden;
        
        // background: @bg;
        header {
            width: 100%;
            height: 132px;
            box-sizing: border-box;
            padding-top: 92px;
            position: relative;

            // background: #fff
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

            .btn-search {
                display: block;
                width: 94.6%;
                height: 40px;
                box-sizing: border-box;
                border: 1px solid @blue;
                border-radius: 20px;
                text-align: center;
                color: @blue;
                background: #fff;
                margin: 0 auto;
                font-size: 1.4rem;
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
            height: calc(100% - 132px);

            // overflow-y: scroll;
            .tit {
                width: 94.6%;
                color: @gray;
                margin: 20px auto;

            }

            .his-list {
                height: calc(100% - 78px);
                overflow-y: scroll;

                li {
                    .content {
                        display: flex;
                        width: 100%;
                        height: 48px;

                        // background: red;
                        .left {
                            flex: 1;
                            position: relative;

                            .logo-loc,
                            .logo-go {
                                position: absolute;
                                width: 5px;
                                height: 5px;
                                border-radius: 3px;
                                background: @gray;
                            }

                            .logo-loc {
                                right: 0;
                                left: 0;
                                top: 8px;
                                margin: 0 auto;
                            }

                            .logo-go {
                                right: 0;
                                left: 0;
                                bottom: 8px;
                                margin: 0 auto;
                            }

                            .logo-line {
                                position: absolute;
                                width: 1px;
                                height: 20px;
                                background: @gray;
                                left: 0;
                                right: 0;
                                margin: 0 auto;
                                top: 14px;
                                box-sizing: border-box;
                                // padding-left: 1px;
                            }
                        }

                        .right {
                            flex: 9;
                            position: relative;

                            div {
                                display: block;
                                line-height: 24px;
                            }

                            .iconfont {
                                position: absolute;
                                right: 10px;
                                top: 0;
                                line-height: 48px;
                                font-size: 2rem;
                            }
                        }
                    }

                    .van-button {
                        height: 48px;
                    }
                }

                p {
                    width: 100%;
                    height: 48px;
                    line-height: 48px;
                    font-weight: 600;
                    font-size: 1.4rem;
                    text-align: center;
                }
            }
        }
    }
</style>