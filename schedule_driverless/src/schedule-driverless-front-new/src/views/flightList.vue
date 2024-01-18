<template>
    <div id="flightList">
        <!-- <ul>
            <template v-for="itemName in flightInfoList">
                <li @click="queryFlightInfo(itemName)">
                    <div class="info_top">
                        <span>{{itemName.flightCompany}}</span><span>{{itemName.airPortCode}}</span></div>
                    <div class="info_content">
                        <div class="info_takeOff">
                            <span>{{itemName.startPlace}}</span><span>{{itemName.takeOffTime}}</span></div>
                        <div class="flight_icon_plane"></div>
                        <div class="info_array">
                            <span>{{itemName.endPlace}}</span><span>{{itemName.arrivalTime}}</span></div>
                    </div>
                </li>
            </template>
        </ul> -->

        <ul>
            <li v-for="(item,i) in dataList" @click="goInfo(item)">
                <div class="info_top"><span>{{item.airlines}}</span><span>{{item.ffid}}</span></div>
                <div class="info_content">
                    <div class="info_takeOff"><span>{{item.cnnms[0]}}</span><span>{{item.fptt.split(' ')[1].substring(0,item.fptt.split(' ')[1].length-3)}}</span></div>
                    <div class="flight_icon_plane"></div>
                    <div class="info_array"><span>{{item.cnnms[item.cnnms.length-2]}}</span><span>{{item.fplt.split(' ')[1].substring(0,item.fplt.split(' ')[1].length-3)}}</span></div>
                    <div class="midway" v-if="item.station">途经站点：{{item.station}}</div>
                </div>
            </li>
        </ul>
    </div>
</template>

<script>
import {flightSearch} from "@/api/api";
    export default {
        data() {
            return {
                dataList: {},
                dateNow:'',
                dateTom:'',
            }
        },
        methods: {
            queryFlightInfo: function(itemName) {
                sessionStorage.setItem("item", JSON.stringify(itemName))
                window.location.href = "flightInfo.html"
            }

        },
        created() {
            this.dataInit();
        },
        methods: {
            dataInit() {
                let that = this;

                var d = new Date();
                d.setTime(d.getTime());
                let yyyy = d.getFullYear();
                let MM = (d.getMonth() + 1) < 10 ? '0' + (d.getMonth() + 1) : d.getMonth() + 1;
                let dd = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
                this.dateNow = yyyy.toString() + MM.toString() + dd.toString();
                // this.dateNow = d.getFullYear().toString() + (d.getMonth() + 1).toString() + d.getDate().toString();
                d.setTime(d.getTime() + 24 * 60 * 60 * 1000);
                // this.dateTom = d.getFullYear().toString() + (d.getMonth() + 1).toString() +  d.getDate().toString();
                let tyyyy = d.getFullYear();
                let tMM = (d.getMonth() + 1) < 10 ? '0' + (d.getMonth() + 1) : d.getMonth() + 1;
                let tdd = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
                this.dateTom = tyyyy.toString() + tMM.toString() + tdd.toString();
                console.log(this.dateNow,this.dateTom);

                let data = JSON.parse(sessionStorage.getItem('searchItem'));
                console.log(data);
                var time = '';
                if (data.time == 0) {
                    time = that.dateNow;
                } else {
                    time = that.dateTom;
                }
                let params = {};
                if (data.type == 0) {
                    params = {
                        ffid: data.ffId,
                        date: time
                    }
                } else if (data.type == 1) {
                    params = {
                        date: time,
                        stype: data.status,
                        place: data.apcd
                    }
                }

                console.log(params);
                flightSearch(params).then(res => {
                    console.log(res);
                    if (res.retCode != 0) {
                        that.$toast.fail('接口报错请重试');
                        return
                    }
                    if (res.retData.length < 1) {
                        that.$toast.fail('查无此数据');
                        return
                    }
                    let dataList = res.retData;
                    // sessionStorage.removeItem('searchItem');

                    dataList.forEach((item, index, arr) => {
                        let ffid = item.ffid.split("-");
                        item.ffid = ffid[0] + "-" + ffid[1] + "(" + item.fatt + ":" + item.tmcd + ")";
                        item.cnnms = item.cnnms.split(',');
                        let station = [...new Set(item.cnnms)];
                        station.pop();
                        station.pop();
                        station.shift();
                        if(station.length>0){
                            item.station = station.join('->');
                        }
                    });
                    console.log(dataList);
                    that.dataList = dataList;
                    console.log(that.dataList);
                })
            },
            goInfo(item){
                sessionStorage.setItem('info',JSON.stringify(item));
                
                this.$router.push({'name':'flightInfo'})
            }
        }
    }
</script>

<style lang='less' scoped>
    body {
        width: 100%;
        height: 100%;
        background: rgba(238, 238, 238, 1);
    }

    #flightList{
        background: #ededed;
        min-height: 100%;
        padding: 10px 0;
    }

    ul li {
        list-style-type: none;
        width: 90%;
        background: rgba(255, 255, 255, 1);
        box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
        margin: 2% auto;
    }

    ul li div {
        display: inline-block;
    }

    .info_top {
        width: 20%;
        margin: 14px 0 14px 14px;
        font-size: 12px;
        font-family: PingFangSC-Medium, PingFang SC;
        font-weight: 500;
        color: rgba(51, 51, 51, 1);
        line-height: 17px;
        border-right: 1px solid #EEEEEE;
        padding-right: 10px;
    }

    .info_takeOff {
        width: 30%;
        margin: 14px;
        font-size: 12px;
        font-family: PingFangSC-Medium, PingFang SC;
        font-weight: 500;
        color: rgba(51, 51, 51, 1);
        line-height: 17px;
        text-align: center;
        span:nth-child(1){
            font-size: 14px;
            font-weight: 600;
        }
    }

    .info_array {
        display: inline-block;
        width: 30%;
        font-size: 12px;
        font-family: PingFangSC-Medium, PingFang SC;
        color: rgba(51, 51, 51, 1);
        line-height: 17px;
        text-align: center;
        margin: 14px;
        /* display: inline; */
        span:nth-child(1){
            font-size: 14px;
            font-weight: 600;
        }
    }

    .info_takeOff span,
    .info_array span {
        display: inline-block;
        width: 100%;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
    }

    .flight_icon_plane {
        background: url("../assets/flight/plane.png") no-repeat;
        width: 20px;
        height: 20px;
        background-size: 20px 8px;
    }

    .info_content {
        margin: 14px auto;
        width: 74%;
        vertical-align: top;
    }

    .midway{
        display: block;
        padding: 0 4px;
        font-size: 12px;
    }

    span {
        display: block;
    }
</style>