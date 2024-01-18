<template>
    <div class="busListSelectH5">
        <ul class="bus-list">
            <li class="clearfix" v-for="(item,index) in busList" @click="go(item)">
                <div class="left fl">
                    <img v-if="item.serviceType == 1 || item.serviceType == 8" src="../assets/busn.png" alt="">
                    <img v-else-if="item.serviceType == 3 || item.serviceType == 7 || item.serviceType == 9" src="../assets/busf.png" alt="">
                    <img v-else src="../assets/buss.png" alt="">
                    <span>车牌号：{{item.plateNumber}}</span>
                </div>
                <div class="right fr">
                    <span v-if="item.serviceType == 1 || item.serviceType == 8">车辆：普通</span>
                    <span v-else-if="item.serviceType == 3 || item.serviceType == 7 || item.serviceType == 9">车辆：快线</span>
                    <span v-else>车辆：短线</span>
                </div>
            </li>
            
        </ul>
    </div>
</template>

<script>
    export default {
        name: 'busListSelectH5',
        data() {
            return {
                busList : [],

            }
        },
        created() {
            this.dataInit();
        },
        mounted() {

        },
        methods: {
            dataInit(){
                this.busList = JSON.parse(sessionStorage.getItem("busList"));
                console.log(this.busList);
            },
            go(data){
                this.$router.push({
                    name: "busListTimeH5",
                    params: {
                    busStatics: {
                        id: data.id,
                        subId: data.subId,
                        plateNumber: data.plateNumber
                    }
                    }
                });   
            }
        }
    }
</script>

<style scoped lang="less">
    @background: #dfdfdf;
    @blue: #24a1ee;
    @gray: #a1a1a1;
    @line: #dfdfdf;
    .busListSelectH5{
        width: 100%;
        .bus-list{
            width: 92.6%;
            margin:  0 auto;
            li{
                height: 40px;
                box-sizing: border-box;
                border-bottom: 1px solid @line;
                .left{
                    img{
                        display: inline-block;
                        width: 20px;
                        vertical-align: top;
                        padding-top: 10px;   
                    }
                    span{
                        display: inline-block;
                        font-size: 1.4rem;
                        line-height: 40px;
                        margin-left: 6px;
                    }
                }
                .right{
                    span{
                        display: inline-block;
                        font-size: 1.4rem;
                        line-height: 40px;
                        margin-right: 6px;
                    }
                }
            }
        }
    }
</style>