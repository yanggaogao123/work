<template>
    <!-- <div id="mainer"> -->
    <div ref="chartCon" class="charts-con" id="charts-con">

    </div>
    <!-- </div> -->
</template>

<script>
    export default {
        name: 'charts',
        props: ['chartData'],
        data() {
            return {
                myChart: '',
            }
        },
        mounted() {

        },
        watch: {
            chartData() {
                console.log(this.chartData);
                this.chartInit();
            }
        },
        methods: {
            chartInit() {
                let that = this;
                if (this.chartData.today.length < 1) {
                    return
                }
                // 基于准备好的dom，初始化echarts实例
                var myChart = this.$echarts.init(this.$refs.chartCon);
                // 绘制图表
                var option;
                // console.log(that.todayArr);

                let arr1 = [];
                for (var i = 0; i < this.chartData.today.length; i++) {
                    arr1[i] = [...this.chartData.today[i]];
                }


                arr1 = JSON.parse(JSON.stringify(arr1));
                console.log('arr1', arr1)

                option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    grid: {
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 20
                    },
                    xAxis: {
                        type: 'time',
                        // type: 'category',
                        splitNumber: 24,
                        minInterval: 3600 * 3 * 1000,
                        axisLabel: {
                            interval: 4,
                            formatter: `{HH}`
                        },
                        splitLine: {
                            show: false
                        },
                    },
                    yAxis: {
                        type: 'value',
                        max: 10,
                        min: 0,
                        // axisLabel: {
                        //     formatter: function() {
                        //         return "";
                        //     }
                        // },
                        axisLine: {
                            show: false,
                        },
                        splitArea: {
                            show: true,
                            areaStyle: {
                                color: [
                                    '#66910d',
                                    '#94ba00',
                                    '#ef9900',
                                    '#db6c00',
                                    '#c92f00',
                                ]
                            }
                        }
                    },
                    series: [{
                            name: '今天指数',
                            type: 'line',
                            symbol: 'none',
                            // smooth: true,
                            lineStyle: {
                                color: '#fff'
                            },
                            itemStyle: {
                                display: 'none',
                                color: '#fff'
                            },
                            data: arr1

                        },
                        {
                            name: '昨天指数',
                            type: 'line',
                            symbol: 'none',
                            lineStyle: {
                                color: '#CC0000'
                            },
                            itemStyle: {
                                display: 'none',
                                color: '#CC0000'
                            },
                            data: that.chartData.yesterday
                        }, {
                            name: '上周同期指数',
                            type: 'line',
                            symbol: 'none',
                            lineStyle: {
                                color: '#00CCCC'
                            },
                            itemStyle: {
                                display: 'none',
                                color: '#00CCCC'
                            },
                            data: that.chartData.lastweek
                        }
                    ]
                }
                myChart.setOption(option);

            },
        }
    }
</script>

<style>

</style>