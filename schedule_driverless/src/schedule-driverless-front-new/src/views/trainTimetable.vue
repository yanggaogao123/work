<template>
    <div id="flightTimetable">
        <div id="wrapper">
            <div class="title">
                <img src="../assets/flight/logo-rule.png" @click="goRule" alt="">
            </div>
            <header>
                <p class="tit">候乘信息</p>

                <div class="select">
                    <div class="select-main" v-on:click="select">
                        <span>{{seleName}}</span>
                        <i class="fa fa-caret-down" :class="seleBool?'fa-caret-down':'fa-caret-up'"></i>
                    </div>
                    <ul class="select-list" v-show="!seleBool">
                        <li v-for="(item,index) in list" :val="item.name" @click="selectClick(item.name)">{{item.name}}</li>
                    </ul>
                </div>
            </header>
            <section>
                <ul class="name">
                    <li>车次</li>
                    <li>终到站</li>
                    <li>开点</li>
                    <li>检票口</li>
                    <li>状态</li>
                </ul>
                <div class="mescroll" id="timeTable">
                    <div id="timeScroll">
                        <ul v-for="(item,i) in dataList">
                            <li>{{item.trainno}}</li>
                            <li>
                                <!-- {{item.zdstation}} -->
                                <marquee v-if="item.zdstation!=null&&item.zdstation.length>5" scrollamount='3'>{{item.zdstation}}</marquee>
                                <p v-else>{{item.zdstation!=null?item.zdstation:'--'}}</p>
                            </li>
                            <li>{{item.schedule_time}}</li>
                            <li>
                                <marquee v-if="item.platform!=null&&item.platform.length>12" scrollamount='3'>{{item.platform}}</marquee>
                                <p v-else>{{item.platform!=null?item.platform:'--'}}</p>
                            </li>
                            <li>
                                <marquee v-if="item.status.length>4" scrollamount='3'>{{item.status}}</marquee>
                                <p v-else>{{item.status}}</p>
                            </li>
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

                <p class="tips" v-show="!dataList.length>0">暂无数据</p>
            </section>
            <div id="tips">*列车状态信息仅供参考，具体请以实际为准</div>
            <footer>
                <div class="btn-search" @click="flightsSearch()">
                    <i class="fa fa-train"></i>
                    <span>车票查询</span>
                </div>
            </footer>
        </div>
    </div>
</template>

<script>
    import "@/assets/font-awesome/css/font-awesome.min.css";
    import { trainTimeTable } from "@/api/api";
    export default {
        name:"trainTimetable",
        data(){
            return{
                pickBool:true,
                seleBool:true,
                seleName: '广州南站',
                list:[
                    {name:'广州南站'},
                    {name:'广州站'},
                    {name:'广州东站'},
                ],
                dataList:[],
            }
        },
        
        created(){
            document.title = '铁路';
            this.getData();
        },
        mounted(){

        },
        methods:{
            goRule() {
                window.location.href = 'https://news.sina.cn/project/fy2020/covid_19_trip.d.html?ua'
            },
            tab1(){
                this.pickBool = true;
                this.getData();
                this.dataList = [];
            },
            tab2(){
                this.pickBool = false;
                this.getData();
                this.dataList = [];
            },
            select(){
                this.seleBool = !this.seleBool;
            },
            selectClick(item){
                this.seleName = item;
                this.seleBool = true;
                this.getData();
            },
            getData(){
                
                let data={
                    stationName: this.seleName
                }
                trainTimeTable(data).then(res=>{
                    console.log(res)
                    let dataList = res.retData;
                    this.dataList = dataList;

                })
            },
            flightsSearch(){
                window.location.href = 'https://mobile.12306.cn/weixin/wxcore/init';
            }
        }
    }
</script>

<style scoped lang="less">
    

    #flightTimetable{
    width: 100%;
    height: 100%;
    background: #EEEEEE;
    position: relative;
}

.title{
        width: 100%;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 3;
        img{
            width: 100%;
        }
    }

header{
    width: 100%;
    height: 44px;
    background: #fff;
    position: fixed;
    top: 40px;
    left: 0;
    z-index: 2;
    .tit{
        font-size: 1.6rem;
        width: 50%;
        line-height: 44px;
        margin: 0 auto;
        text-align: center;
        font-weight: 600;
    }
}



header .select{
    position: absolute;
    width: 20%;
    /* height: 44px; */
    top: 0;
    right: 0;
}
header .select .select-main{
    width: 100%;
    height: 44px;
}
header .select .select-main span{
    display: inline-block;
    line-height: 44px;
}
header .select .select-main .fa{
    font-size: 1.6rem;
    margin: 0 2px;
    line-height: 44px;
}
header .select .select-list{
    width: 100%;
    position: absolute;
    right: 0;
    bottom: -120px;
    background: #fff;
    z-index: 999;
}
header .select .select-list li{
    width: 100%;
    height: 40px;
    line-height: 40px;
    text-align: center;
    z-index: 999;
}

section{
    width: 100%;
    padding-top: 84px;
    height: calc(100% - 84px);
    position: relative;
    font-size: 1.4rem;
    // margin-top: 44px;
    /*position: absolute;*/
    /*top: 44px;*/
    /*left: 0;*/
    /*overflow: scroll;*/
}
section .name{
    position: fixed;
    width: 100%;
    height: 32px;
    display: flex;
    background: #EEEEEE;
    z-index: 1;
    top: 84px;
    left: 0;
}
section .name li{
    flex: 1;
    text-align: center;
    line-height: 32px;
}
section .mescroll{
    width: 100%;
    // max-height: calc(100% - 60px);
    overflow: scroll;
    padding: 32px 0 80px 0;
    /*position: absolute;*/
    /*top: 76px;*/
    /*bottom: 60px;*/
}
section .mescroll #timeScroll{
	
}
section .mescroll ul{
    width: 100%;
    height: 48px;
    display: flex;
    background: blue;
}
section .mescroll ul li{
    flex: 1;
    text-align: center;
    line-height: 48px;
}
section .mescroll ul li p{
    width: 100%;
    height: 48px;
    overflow: scroll-x;
    white-space:nowrap;
}
section .mescroll ul li:nth-child(5) span{
    display: block;
    line-height: 16px;
}
section .mescroll ul li:nth-child(5) span:nth-child(1){
    margin-top: 8px;
}
section .mescroll ul:nth-child(2n-1) {
    background: #fff;
}
section .mescroll ul:nth-child(2n){
    background: #DFDFDF;
}
section .tips{
    font-size: 1.4rem;
    text-align: center;
    color: gray;
    // margin: 0 ;
}

#tips{
    position: fixed;
    width: 100%;
    height: 28px;
    line-height: 28px;
    background: #eeeeee;
    text-align: center;
    color: red;
    bottom: 60px;
    left: 0;
    z-index: 2;
    font-size: 1.4rem;
}

footer{
    position: fixed;
    width: 100%;
    height: 60px;
    background: #fff;
    bottom: 0;
}
footer .btn-search{
    position: absolute;
    width: 92%;
    height: 40px;
    background: #FD7B2D;
    border-radius: 20px;
    text-align: center;
    color: #fff;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    margin: auto;
    font-size: 1.6rem;
}
footer .btn-search .fa{
    display: inline-block;
    font-size: 1.4rem;
    line-height: 40px;
    margin: 0 2px;
}
footer .btn-search span{
    display: inline-block;
    line-height: 40px;
}
</style>