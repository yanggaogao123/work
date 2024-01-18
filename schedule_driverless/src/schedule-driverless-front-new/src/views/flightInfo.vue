<template>
    <div id="flightInfo">
        <div class="flight_header">
            <span>{{flightDetail.airlines}}</span>
            <span>{{flightDetail.ffid}}</span>
        </div>

        <div class="flight_body">
            <div class="flight_body_takeOff">
                <div class="flight_place">
                    <div class="plane_fligt"></div> <span>{{flightDetail.cnnms[0]}}</span>
                    <span>{{flightDetail.fptt?flightDetail.fptt.split(":00")[0]:'-'}}</span>
                </div>
                <div class="flight_time">
                    <span>实际起飞</span>
                    <span>{{flightDetail.frtt?flightDetail.frtt.split(":00")[0]:'-'}}</span>
                </div>
            </div>

            <div class="midWay" v-if="flightDetail.station">
                <div>途经站点</div>
                <div>{{flightDetail.station}}</div>
            </div>

            <div class="flight_body_takeOff">
                <div class="flight_place">
                    <div class="plane_fligt_back"></div><span>{{flightDetail.cnnms[flightDetail.cnnms.length-2]}}</span>
                    <span>{{flightDetail.fplt?flightDetail.fplt.substring(0,flightDetail.fplt.length-3):'-'}}</span>
                </div>
                <div class="flight_time">
                    <span>实际到达</span>
                    <span>{{flightDetail.frlt?flightDetail.frlt.substring(0,flightDetail.frlt.length-3):'-'}}</span>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    export default {
        data() {
            return {
                flightDetail: {
                }
            }
        },
        methods: {


        },
        created() {
            var that = this;
            var flightInfo = sessionStorage.getItem("info");
            that.flightDetail = JSON.parse(flightInfo);
            console.log(that.flightDetail);
        },
    }
</script>

<style lang='less' scoped>
    body {
        width: 100%;
        height: 100%;
        background: rgba(238, 238, 238, 1);
    }

    #flightInfo{
        background: #ededed;
        min-height: 100%;
    }

    .flight_header {
        width: 100%;
        height: 44px;
        background: rgba(255, 255, 255, 1);
        box-shadow: 0px 1px 0px 0px rgba(223, 223, 223, 1);
        span{
            width: 50%;
        }
    }

    span {
        width: 56px;
        height: 20px;
        font-size: 14px;
        font-family: PingFangSC-Regular, PingFang SC;
        font-weight: 400;
        color: rgba(51, 51, 51, 1);
        line-height: 44px;
    }

    .flight_header span:nth-child(1) {
        margin-left: 15px;
    }

    .flight_header span:nth-child(2) {
        float: right;
        margin-right: 14px;
        text-align: right;
    }

    .midWay{
        width: 100%;
        height: 45px;
        background: #fff;
        margin-top: 10px;
        line-height: 45px;
        padding: 0 15px;
        display: flex;
        justify-content: space-between;
    }

    .flight_body_takeOff {
        margin-top: 10px;
        width: 100%;
        height: 89px;
        background: rgba(255, 255, 255, 1);
        box-shadow: 0px 1px 0px 0px rgba(223, 223, 223, 1);
        
    }

    .flight_place {
        border-bottom: 1px solid rgba(238, 238, 238, 1);
        height: 45px;
        // max-width: 50%;
        // overflow:hidden;
        // white-space: nowrap;

    }


    .flight_place span:nth-child(2) {
        font-weight: 600;
        display: inline-block;
        line-height: 45px;
        font-size: 1.4rem;
        height: 45px;
        width: auto;
        max-width: 35%;
        overflow : hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .flight_place span:nth-child(3) {
        float: right;
        margin-right: 15px;
        width: 38%;
        text-align: right;
    }

    .flight_time span:nth-child(1) {
        margin-left: 15px;
    }

    .flight_time span:nth-child(2) {
        float: right;
        margin-right: 14px;
        width: 50%;
        text-align: right;
    }

    .plane_fligt {
        background: url(../assets/flight/plane_flight.png) no-repeat;
        width: 20px;
        height: 11px;
        display: inline-block;
        margin-left: 15px;
        margin-top: 20px;
        background-size: 13px 10px;
        vertical-align: top;
    }


    .plane_fligt_back {
        background: url('../assets/flight/plane_flight_back.png') no-repeat;
        width: 20px;
        height: 11px;
        display: inline-block;
        margin-left: 15px;
        background-size: 13px 10px;
        vertical-align: top;
        margin-top: 20px;
    }
</style>