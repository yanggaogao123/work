<template>
    <div id="trafficArea">
        <div id="container">

        </div>
        <section>
            <traffic-table v-if="tableData.length>0" :trafficArr="tableData"></traffic-table>
            <img src="../../assets/duanwang.png" class="duanwang-tips" v-else alt="">
        </section>
        <div class="info">

        </div>
        <footer>
            <footer-nav pick="1"></footer-nav>
        </footer>
    </div>
</template>

<script>
    import trafficTable from './trafficComponent/trafficTable.vue';
    import footerNav from './trafficComponent/footerBar.vue'
    import {
        getZoneIndex
    } from '@/api/api.js';
    export default {
        name: 'trafficArea',
        components: {
            trafficTable,
            footerNav
        },
        data() {
            return {
                map: '',
                tableData: [],
            }
        },
        created() {
            this.dataInit();
        },
        mounted() {
            this.mapInit();
        },
        methods: {
            mapInit() {
                let that = this;
                this.map = new AMap.Map('container', {
                    center: [113.280637, 23.125178],
                    zoom: 11
                });
            },
            dataInit() {
                let that = this;
                getZoneIndex().then(res => {
                    console.log(res);
                    if (res.retCode != 0) {
                        that.$notify('接口出错，请稍后再试！');
                        return;
                    }
                    let data = res.retData;
                    that.tableData = data.map(item=>{
                        return{
                            zoneName: item.zoneName,
                            congIndex: item.congIndex,
                            congName: item.congName,
                            roadSpeed: item.zoneSpeed
                        }
                    });
                    console.log(that.tableData);
                    data.forEach(ele => {
                        ele.path = ele.pointList.map(res=>{
                            return new AMap.LngLat(res.lon,res.lat)
                        })
                        let polygon = new AMap.Polygon({
                            path: ele.path,
                            fillColor: ele.levelColor,
                            strokeOpacity: 1,
                            fillOpacity: 0.8,
                            strokeColor: '#2b8cbe',
                            strokeWeight: 1,
                            strokeStyle: 'dashed',
                            strokeDasharray: [5, 5],
                        });
                        polygon.on('click',()=>{
                            console.log('123');
                            that.$dialog.alert({
                                message: `<div>区域名称:${ele.zoneName}</div>
                                    <div>拥堵指数:${ele.congIndex}</div>
                                    <div>拥堵级别:${ele.congName}</div>
                                    <div>平均速度:${ele.zoneSpeed}</div>`,
                                }).then(() => {
                                // on close
                            });
                        })
                        that.map.add(polygon);


                        let maker = new AMap.Marker({
                            map: that.map,
                            content: ele.zoneName,
                            position: [ele.lon, ele.lat],
                            // title: marker.stationName,
                            offset: new AMap.Pixel(-21,-10),
                        });
                    });
                    console.log(data);
                    
                    // let polygon = new AMap.Polygon({
                    //     path: polygonData,
                    //     fillColor: '#ccebc5',
                    //     strokeOpacity: 1,
                    //     fillOpacity: 0.5,
                    //     strokeColor: '#2b8cbe',
                    //     strokeWeight: 1,
                    //     strokeStyle: 'dashed',
                    //     strokeDasharray: [5, 5],
                    // });
                    // that.map.add(polygon);
                })
            },
        }
    }
</script>

<style lang="less" scoped>
    html,
    body {
        width: 100%;
        height: 100%;
    }

    #trafficArea {
        width: 100%;
        height: 100%;
        position: relative;

        #container {
            width: 100%;
            height: 340px;
        }

        section {
            height: calc(100% - 340px);
            .duanwang-tips{
                width: 50%;
                display: block;
                margin: 0 auto;
            }
        }

        footer {
            position: fixed;
            left: 0;
            bottom: 0;
            width: 100%;
        }
    }
    /deep/.amap-marker-content{
        font-size: 12px;
    }
</style>