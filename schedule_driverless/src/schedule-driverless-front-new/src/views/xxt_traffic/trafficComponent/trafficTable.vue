<template>
    <div class="table" id="trafficTable">
        <ul class="tit">
            <template v-if="tableType==0">
                <li>区域名称</li>
                <li>交通指数</li>
                <li>拥堵级别</li>
                <li>平均速度 <br> (km/h)</li>
            </template>
            <template v-else>
                <li>通道名称</li>
                <li>行程时间比</li>
                <li>拥堵级别</li>
                <li>平均速度 <br> (km/h)</li>
            </template>
        </ul>
        <div class="con" :class="bottomType == 1?'bo':''">
            <ul v-for="(item,i) in tableArr">
                <li><div>
                    
                    {{item.zoneName}}
                    
                </div></li>
                <li v-if="tableType==0">
                    <span v-if="Number(item.congIndex)<=2" style="color:#66910d">{{item.congIndex}}</span>
                    <span v-else-if="2<Number(item.congIndex)<=4" style="color:#94ba00">{{item.congIndex}}</span>
                    <span v-else-if="4<Number(item.congIndex)<=6" style="color:#ef9900">{{item.congIndex}}</span>
                    <span v-else-if="6<Number(item.congIndex)<=8" style="color:#db6c00">{{item.congIndex}}</span>
                    <span v-else-if="8<Number(item.congIndex)<=10" style="color:#c92f00">{{item.congIndex}}</span>
                </li>
                <li v-else>{{item.roadTTI}}</li>
                <li>
                    <span v-if="Number(item.congIndex)<=2" style="color:#66910d">{{item.congName}}</span>
                    <span v-else-if="2<Number(item.congIndex)<=4" style="color:#94ba00">{{item.congName}}</span>
                    <span v-else-if="4<Number(item.congIndex)<=6" style="color:#ef9900">{{item.congName}}</span>
                    <span v-else-if="6<Number(item.congIndex)<=8" style="color:#db6c00">{{item.congName}}</span>
                    <span v-else-if="8<Number(item.congIndex)<=10" style="color:#c92f00">{{item.congName}}</span>
                </li>
                <li>{{item.roadSpeed}}</li>
            </ul>
            <p v-if="bottomType == 1">--到底啦--</p>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'trafficTable',
        data() {
            return {
                tableType: 0,
                bottomType: 1,
                tableArr: [

                ],
            }
        },
        props: ['trafficArr', 'tType', 'bType'],
        created() {
            this.dataInit();
        },
        watch: {
            trafficArr() {
                this.tableArr = this.trafficArr;
            },
            tType() {
                this.tableType = this.tType;
            },
            bType() {
                this.bottomType = this.bType;
            }
        },
        methods: {
            dataInit() {
                console.log(this.bType);
                this.tableArr = this.trafficArr;
                // console.log(this.tType);
                if (this.tType) {
                    this.tableType = this.tType;
                }
                if (this.bType) {
                    this.bottomType = this.bType;
                }
                // console.log(this.tableArr);
            },
        }
    }
</script>

<style lang="less" scoped>
    .table {
        width: 100%;
        height: 100%;

        ul {
            display: flex;
            width: 100;
            height: 42px;
            justify-content: center;
            align-items: center;
            box-sizing: border-box;
            border-top: 1px solid #ccc;
            background: #F8F8F8;
            margin: 0;

            li {
                width: 25%;
                text-align: center;
                font-size: 1.4rem;
                // height: 42px;
                // overflow:  scroll;
            }
        }

        .con {
            height: calc(100% - 42px);
            overflow: scroll;

            ul {
                li:nth-child(1) {
                    height: 42px;
                    overflow-y: scroll;
                    position: relative;
                    div{
                        display: flex;
                        min-height: 42px;
                        max-height: 80px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }
                }
                
            }

            p {
                font-size: 1.2rem;
                line-height: 20px;
                text-align: center;
            }
        }

        .bo {
            padding-bottom: 50px;
        }
    }
</style>