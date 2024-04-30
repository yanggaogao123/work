<template>
  <ChartSection :title="title">
    <div class="muti-chart-section__main">
      <div style="width: 50%; height: 100%; position: relative">
        <header class="chart-header">上行满载率 %</header>
        <div ref="chartRef1" style="height: 100%; width: 100%"></div>
      </div>
      <div style="width: 50%; height: 100%; position: relative">
        <header class="chart-header">下行满载率 %</header>
        <div ref="chartRef2" style="height: 100%; width: 100%"></div>
      </div>
    </div>
  </ChartSection>
</template>

<script>
import Vue from "vue";
import axios from "axios";
import * as echarts from "echarts/core";
import {
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
} from "echarts/components";
import { BarChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";
import "@/assets/less/base.css";
import ChartSection from "../components/ChartSection.vue";

// import { dayFlowResponse } from "./mock";

echarts.use([
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  BarChart,
  CanvasRenderer,
]);

export default {
  name: "BigScreenChartOneModal",
  props: ["title", "routeId"],
  components: {
    ChartSection,
  },
  // mixins: [JeecgListMixin],
  data() {
    return {
      myChart1: null,
      chartData1: [[], [], []],
      chartData2: [[], [], []],
      mockMode: false,
      url: `${process.env.VUE_APP_BUS_API}/schedule-driverless/dataService/list`,
    };
  },

  created() {},
  async mounted() {
    if (!this.$refs.chartRef1 || !this.$refs.chartRef2) return;
    const chartDom1 = this.$refs.chartRef1;
    const chartDom2 = this.$refs.chartRef2;
    this.myChart1 = echarts.init(chartDom1);
    this.myChart2 = echarts.init(chartDom2);
    if (this.routeId || this.mockMode) {
      await this.getChartDatas();
    }
  },
  methods: {
    getOptions(datas = [[], [], []]) {
      if (!(datas instanceof Array)) return;

      const posList = [
        "left",
        "right",
        "top",
        "bottom",
        "inside",
        "insideTop",
        "insideLeft",
        "insideRight",
        "insideBottom",
        "insideTopLeft",
        "insideTopRight",
        "insideBottomLeft",
        "insideBottomRight",
      ];
      let app = {};
      app.configParameters = {
        rotate: {
          min: -90,
          max: 90,
        },
        align: {
          options: {
            left: "left",
            center: "center",
            right: "right",
          },
        },
        verticalAlign: {
          options: {
            top: "top",
            middle: "middle",
            bottom: "bottom",
          },
        },
        position: {
          options: posList.reduce(function (map, pos) {
            map[pos] = pos;
            return map;
          }, {}),
        },
        distance: {
          min: 0,
          max: 100,
        },
      };
      app.config = {
        rotate: 90,
        align: "left",
        verticalAlign: "middle",
        position: "insideBottom",
        distance: 15,
        onChange: function () {
          const labelOption = {
            rotate: app.config.rotate,
            align: app.config.align,
            verticalAlign: app.config.verticalAlign,
            position: app.config.position,
            distance: app.config.distance,
          };
          mutiBarChart.value.setOption({
            series: [
              {
                label: labelOption,
              },
              {
                label: labelOption,
              },
              {
                label: labelOption,
              },
            ],
          });
        },
      };
      const seriesName = ["早高峰", "平峰", "晚高峰"];
      const labelOption = {
        show: false,
        position: app.config.position,
        distance: app.config.distance,
        align: app.config.align,
        verticalAlign: app.config.verticalAlign,
        rotate: app.config.rotate,
        formatter: "{c}  {name|{a}}",
        fontSize: 16,
        rich: {
          name: {},
        },
      };
      const option = {
        grid: {
          left: "3%",
          right: "4%",
          top: "36px",
          bottom: "1%",
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
            label: {
              backgroundColor: "rgba(6, 26, 60, 0.9)",
            },
          },
          borderWidth: 0,
          backgroundColor: "rgba(6, 26, 60, 0.9)",
          textStyle: {
            color: "#fff",
          },
          valueFormatter: (value) => value,
        },
        legend: {
          data: seriesName || [],
          icon: "rect",
          itemWidth: 8,
          top: "12px",
          itemHeight: 8,
          textStyle: {
            color: "#fff",
          },
        },
        toolbox: {
          show: false,
          orient: "vertical",
          left: "right",
          top: "center",
          feature: {
            // mark: { show: false },
            // dataView: { show: false, readOnly: false },
            magicType: {
              show: true,
              type: ["line", "bar"],
              iconStyle: {
                color: "#fff",
              },
              title: {
                line: "切换为折线图",
                bar: "切换为柱状图",
              },
            },
            // restore: { show: false },
            // saveAsImage: { show: false }
          },
        },
        xAxis: [
          {
            type: "category",
            axisTick: {
              alignWithLabel: true,
            },
            axisLabel: {
              color: "#c6d9e7",
              interval: 0,
              overflow: "truncate",
            },
            data: datas[1].map((item) => item.label),
          },
        ],
        yAxis: [
          {
            type: "value",
            axisLabel: {
              color: "#93a8c3",
            },
            splitLine: {
              show: true,
              lineStyle: {
                color: "#3d4b64",
                type: "dashed",
                width: 1,
              },
            },
          },
        ],
        series: [
          {
            name: seriesName[0],
            type: "bar",
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#334DF6" },
                // { offset: 0.5, color: "#2065ce" },
                { offset: 1, color: "rgba(51,77,246,0.2)" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            label: labelOption,
            emphasis: {
              focus: "series",
            },
            data: datas[0] || [],
            barWidth: "5px",
          },
          {
            name: seriesName[1],
            type: "bar",
            barGap: 0.8,
            label: labelOption,
            emphasis: {
              focus: "series",
            },
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#4DF6C4" },
                // { offset: 0.5, color: "#089db3" },
                { offset: 1, color: "rgba(77,246,196,0.2)" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            data: datas[1] || [],
            barWidth: "5px",
          },
          {
            name: seriesName[2],
            type: "bar",
            label: labelOption,
            emphasis: {
              focus: "series",
            },
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#53F633" },
                // { offset: 0.5, color: "#8b9724" },
                { offset: 1, color: "rgba(83,246,51,0.2)" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            data: datas[2] || [],
            barWidth: "5px",
          },
        ],
      };

      return option;
    },
    async getChartDatas() {
      try {
        const dayFlowResponse = await axios.post(this.url, {
          appName: "",
          businessID: "001101",
          page: "1",
          pageSize: "1000",
          data: {
            routeId: this.routeId || "101",
          },
        });

        const upChartData = dayFlowResponse.retData.list
          .filter((item) => item.direction === "0")
          .sort((a, b) => a.station_order - b.station_order);
        const downChartData = dayFlowResponse.retData.list
          .filter((item) => item.direction === "1")
          .sort((a, b) => a.station_order - b.station_order);
        // const upChartData = dayFlowResponse.retData.list
        //   .filter((item) => item.direction === "0")
        //   .sort((a, b) => a.station_order - b.station_order);
        // const downChartData = dayFlowResponse.retData.list
        //   .filter((item) => item.direction === "1")
        //   .sort((a, b) => a.station_order - b.station_order);

        const getNumberValue = (value, negative = false) => {
          let target = 0;
          if (typeof value === "string") {
            target = isNaN(Number(value)) ? 0 : Number(value);
          }

          return ((negative ? -target : target) * 100).toFixed(2);
        };

        this.chartData1 = [
          upChartData.map((item) => {
            return {
              label: item.station_name,
              value: getNumberValue(item.mor_load_factor),
            };
          }),
          upChartData.map((item) => {
            return {
              label: item.station_name,
              value: getNumberValue(item.flat_load_factor),
            };
          }),
          upChartData.map((item) => {
            return {
              label: item.station_name,
              value: getNumberValue(item.night_load_factor),
            };
          }),
        ];
        this.chartData2 = [
          downChartData.map((item) => {
            return {
              label: item.station_name,
              value: getNumberValue(item.mor_load_factor),
            };
          }),
          downChartData.map((item) => {
            return {
              label: item.station_name,
              value: getNumberValue(item.flat_load_factor),
            };
          }),
          downChartData.map((item) => {
            return {
              label: item.station_name,
              value: getNumberValue(item.night_load_factor),
            };
          }),
        ];
        this.myChart1.setOption(this.getOptions(this.chartData1));
        this.myChart2.setOption(this.getOptions(this.chartData2));
      } catch (error) {
        console.error(error);
        this.myChart1.setOption(this.getOptions([[], [], []]));
        this.myChart2.setOption(this.getOptions([[], [], []]));
      }
    },
  },
  watch: {
    routeId(newValue) {
      if (newValue) {
        this.getChartDatas();
      }
    },
  },
};
</script>

<style scoped>
.chart-header {
  color: #c6d9e7;
  position: absolute;
  left: 8px;
  top: 4px;
}

.muti-chart-section__main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
