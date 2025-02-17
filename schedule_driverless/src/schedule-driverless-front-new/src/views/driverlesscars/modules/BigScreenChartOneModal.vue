<template>
  <ChartSection :title="title">
    <div ref="chartRef" style="width: 100%; height: 100%"></div>
  </ChartSection>
</template>

<script>
import Vue from "vue";
import axios from "axios";
import * as echarts from "echarts/core";
import {
  TooltipComponent,
  GridComponent,
  LegendComponent,
} from "echarts/components";
import { BarChart, LineChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";

import "@/assets/less/base.css";
import ChartSection from "../components/ChartSection.vue";

// import mockResponse from "./mock.js";

echarts.use([
  TooltipComponent,
  GridComponent,
  LegendComponent,
  BarChart,
  CanvasRenderer,
  LineChart,
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
      myChart: null,
      mockMode: false,
      chartDatas: [[], [], [], [], [], []],
      labelData: [],
      url: `${process.env.VUE_APP_BUS_API}/dataService/list`,
    };
  },
  async mounted() {
    if (!this.$refs.chartRef) return;
    const chartDom = this.$refs.chartRef;
    this.myChart = echarts.init(chartDom);
    if (this.routeId || this.mockMode) {
      await this.getChartData();
    }
    // this.myChart.setOption(this.getOptions());
  },
  methods: {
    getOptions() {
      const seriesName = [
        "上行高断面客流",
        "下行断面客流",
        "上行总客流",
        "下行总客流",
        "上行班次数",
        "下行班次数",
      ];
      return {
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
          valueFormatter: (value) => {
            if (typeof value === "number") {
              return Math.abs(value);
            }
            return value;
          },
        },
        legend: {
          data: seriesName,
          icon: "rect",
          itemWidth: 8,
          top: "12px",
          itemHeight: 8,
          textStyle: {
            color: "#fff",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          top: "36px",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            splitLine: {
              show: true,
              lineStyle: {
                color: "#3d4b64",
                type: "dashed",
                width: 1,
              },
            },
            axisTick: {
              alignWithLabel: true,
            },
            axisLabel: {
              color: "#c6d9e7",
            },
            nameLocation: "center",
            data: this.labelData,
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "单位/人次",
            axisTick: {
              show: true,
            },
            splitLine: {
              show: false,
              lineStyle: {
                color: "#3d4b64",
                type: "dashed",
                width: 1,
              },
            },
            axisLabel: {
              color: "#c6d9e7",
              formatter: (value) => {
                if (typeof value === "number") {
                  return Math.abs(value);
                }
                return value;
              },
            },
          },
          {
            type: "value",
            name: "单位/班次",
            axisTick: {
              show: true,
            },
            axisLabel: {
              color: "#c6d9e7",
              formatter: (value) => {
                if (typeof value === "number") {
                  return Math.abs(value);
                }
                return value;
              },
            },
            splitLine: {
              show: false,
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
            stack: "Up",
            label: {
              show: false,
              position: "top",
            },
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#334DF6" },
                // { offset: 0.5, color: "#2453ba" },
                { offset: 1, color: "rgba(51,77,246,0.2)" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            barGap: 0.8,
            barWidth: "5px",
            emphasis: {
              focus: "series",
            },
            data: this.chartDatas[0] || [],
          },
          {
            name: seriesName[1],
            type: "bar",
            stack: "Up",
            label: {
              show: false,
              position: "bottom",
              formatter: ({ value }) => {
                if (typeof value === "number") {
                  return Math.abs(value);
                }
                return value;
              },
            },
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#11BB881A" },
                // { offset: 0.5, color: "#00967b" },
                { offset: 1, color: "#11BB88FF" },
              ]),
              borderRadius: [0, 0, 20, 20],
            },
            barGap: 0.8,
            barWidth: "5px",
            emphasis: {
              focus: "series",
            },
            data: this.chartDatas[1] || [],
          },
          {
            name: seriesName[2],
            type: "bar",
            stack: "Down",
            label: {
              show: false,
              position: "top",
              color: "#fff",
            },
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#3387F6FF" },
                // { offset: 0.5, color: "#1b88be" },
                { offset: 1, color: "#3387F61A" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            barGap: 0.8,
            barWidth: "5px",
            emphasis: {
              focus: "series",
            },
            data: this.chartDatas[2] || [],
          },
          {
            name: seriesName[3],
            type: "bar",
            stack: "Down",
            label: {
              show: false,
              position: "bottom",
              color: "#fff",
              formatter: ({ value }) => {
                if (typeof value === "number") {
                  return Math.abs(value);
                }
                return value;
              },
            },
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "#47F6331A" },
                // { offset: 0.5, color: "#1b88be" },
                { offset: 1, color: "#47F633FF" },
              ]),
              borderRadius: [0, 0, 20, 20],
            },
            barGap: 0.8,
            barWidth: "5px",
            emphasis: {
              focus: "series",
            },
            data: this.chartDatas[3] || [],
          },
          {
            type: "line",
            // stack: "BC",
            name: seriesName[4],
            smooth: false,
            lineStyle: {
              color: "#393beb",
              width: 2,
            },
            label: {
              show: true,
              position: "bottom",
              color: "#fff",
            },
            showSymbol: true,
            symbol: "circle",
            symbolSize: 6,
            emphasis: {
              focus: "series",
            },
            yAxisIndex: 1,
            data: this.chartDatas[4] || [],
          },
          {
            type: "line",
            // stack: "BC",
            name: seriesName[5],
            smooth: false,
            lineStyle: {
              color: "#63b726",
              width: 2,
            },
            label: {
              show: true,
              position: "bottom",
              color: "#fff",
              formatter: ({ value }) => {
                if (typeof value === "number") {
                  return Math.abs(value);
                }
                return value;
              },
            },
            showSymbol: true,
            symbol: "circle",
            symbolSize: 6,
            emphasis: {
              focus: "series",
            },
            yAxisIndex: 1,
            data: this.chartDatas[5] || [],
          },
        ],
      };
    },
    async getChartData() {
      const getNumberValue = (value, negative = false) => {
        let target = 0;
        if (typeof value === "string") {
          target = isNaN(Number(value)) ? 0 : Number(value);
        }

        return negative ? -target : target;
      };
      this.labelData = [];
      this.chartDatas = [[], [], [], [], [], []];
      this.myChart.setOption(this.getOptions());

      try {
        const mockResponse = await axios.post(
          this.url,
          {
            appName: "",
            businessID: "001100",
            page: "1",
            pageSize: "1000",
            data: {
              routeId:
                typeof this.routeId === "number"
                  ? this.routeId.toString()
                  : this.routeId || "101",
            },
          },
          {
            headers: {
              "Content-Type": "application/json;charset=UTF-8",
            },
          }
        );

        const response = JSON.parse(mockResponse.data.data);
        const listData =
          response && response.retData && response.retData.list
            ? response.retData.list
            : [];

        const sortResponse = listData.sort((a, b) => {
          const aHour = a.fragment.split(":")[0] || 0;
          const bHour = b.fragment.split(":")[0] || 0;
          return aHour - bHour;
        });

        this.chartDatas = [
          sortResponse.map((item) =>
            getNumberValue(item.up_fragment_passengers)
          ),
          sortResponse.map((item) =>
            getNumberValue(item.down_fragment_passengers, true)
          ),
          sortResponse.map((item) => {
            return getNumberValue(item.up_all_passengers);
          }),
          sortResponse.map((item) => {
            return getNumberValue(item.down_all_passengers, true);
          }),
          sortResponse.map((item) => {
            return getNumberValue(item.up_trips);
          }),
          sortResponse.map((item) => {
            return getNumberValue(item.down_trips, true);
          }),
        ];

        this.labelData = sortResponse.map((item) => item.fragment);
        this.myChart.setOption(this.getOptions());
      } catch (error) {
        console.error(error);
      }
    },
  },
  watch: {
    routeId(newValue) {
      if (newValue) {
        this.getChartData();
      }
    },
  },
};
</script>

<style lang="less" scoped></style>
