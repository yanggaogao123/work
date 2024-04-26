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
import { BarChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";

import "@/assets/less/base.css";
import ChartSection from "../components/ChartSection.vue";

echarts.use([
  TooltipComponent,
  GridComponent,
  LegendComponent,
  BarChart,
  CanvasRenderer,
]);

export default {
  name: "BigScreenChartOneModal",
  props: ["chartData", "title"],
  components: {
    ChartSection,
  },
  // mixins: [JeecgListMixin],
  data() {
    return {
      myChart: null,
    };
  },

  created() {},
  mounted() {
    if (!this.$refs.chartRef) return;
    const chartDom = this.$refs.chartRef;
    this.myChart = echarts.init(chartDom);
    this.myChart.setOption(this.getOptions());
  },
  methods: {
    getOptions() {
      return {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          data: ["Profit", "Expenses", "Income"],
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            axisTick: {
              show: false,
            },
            data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        series: [
          {
            name: "Profit",
            type: "bar",
            label: {
              show: true,
              position: "inside",
            },
            emphasis: {
              focus: "series",
            },
            data: [200, 170, 240, 244, 200, 220, 210],
          },
          {
            name: "Income",
            type: "bar",
            stack: "Total",
            label: {
              show: true,
            },
            emphasis: {
              focus: "series",
            },
            data: [320, 302, 341, 374, 390, 450, 420],
          },
          {
            name: "Expenses",
            type: "bar",
            stack: "Total",
            label: {
              show: true,
              position: "left",
            },
            emphasis: {
              focus: "series",
            },
            data: [-120, -132, -101, -134, -190, -230, -210],
          },
        ],
      };
    },
  },
};
</script>

<style lang="less" scoped></style>
