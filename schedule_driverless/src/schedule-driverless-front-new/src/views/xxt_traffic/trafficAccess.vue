<template>
    <div id="trafficAccess">
        <header>
            <van-search v-model="value" label="道路" placeholder="请输入搜索关键词" background="#FF6701" @search="onSearch" @cancel="onCancel"/>
        </header>
        <section>
            <img v-if="tipsBool" src="../../assets/duanwang.png" class="duanwang-tips" v-else alt="">
            <traffic-table v-if="tableData.length>0" :tType="1" :trafficArr="tableData"></traffic-table>
            
        </section>
        <footer>
            <footer-nav :pick="2"></footer-nav>
        </footer>
    </div>
</template>

<script>
    import trafficTable from './trafficComponent/trafficTable.vue';
    import footerNav from './trafficComponent/footerBar.vue'
    import {getRoadIndex} from '@/api/api.js';
    export default {
        name: 'trafficAccess',
        data() {
            return {
                value:'',
                tableData:[],
                oriTableData:[
                    
                ],
                obj1:{
                    name:1,
                    hh:2,
                    ii:3
                },
                tipsBool: true,
            }
        },
        components:{
            trafficTable,footerNav
        },
        created(){
            this.dataInit();
        },
        watch:{
            value() {
                console.log(this.value);
                this.tableData = []
                this.oriTableData.forEach(item => {
                    if(item.roadName.indexOf(this.value) != -1){
                         this.tableData.push(item);
                    }
                });
                if(this.tableData.length<1){
                    this.$notify('无此搜索结果');
                }
            }
        },
        methods:{
            onSearch(){
                console.log(123);
            },
            onCancel(){

            },
            dataInit(){
                let that = this;
                getRoadIndex().then(res=>{
                    console.log(res);
                    if(res.retCode != 0){
                        that.$notify('接口出错，请稍后再试！');
                        return;
                    }
                    let data = res.retData;
                    that.tableData = data.map(item=>{
                        return {
                            zoneName: `${item.roadName}(${item.dir})`,
                            roadTTI: item.roadTTI,
                            congName: item.congName,
                            congIndex: item.congIndex,
                            roadSpeed: item.roadSpeed,
                            roadName: item.roadName
                        }
                    })
                    that.oriTableData = that.tableData;
                    this.tipsBool = false;
                    // console.log(that.oriTableData)
                })
            },
        }
    }
</script>

<style scoped lang="less">
    html,body{
        width: 100%;
        height: 100%;
    }
    #trafficAccess{
        width: 100%;
        height: 100%;
        position: relative;
        header{
            height: 54px;
        }
        section{
            height: calc(100% - 104px);
            // overflow: scroll;
            .duanwang-tips{
                width: 50%;
                display: block;
                margin: 0 auto;
            }
        }
        footer{
            position: fixed;
            left: 0;
            bottom: 0;
            width: 100%;
        }
    }
</style>