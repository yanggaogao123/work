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
  props: ["chartData", "title"],
  components: {
    ChartSection,
  },
  // mixins: [JeecgListMixin],
  data() {
    return {
      myChart1: null,
    };
  },

  created() {},
  mounted() {
    if (!this.$refs.chartRef1 || !this.$refs.chartRef2) return;
    const chartDom1 = this.$refs.chartRef1;
    const chartDom2 = this.$refs.chartRef2;
    this.myChart1 = echarts.init(chartDom1);
    this.myChart2 = echarts.init(chartDom2);
    this.myChart1.setOption(
      this.getOptions([
        [
          {
            label: "海印桥",
            value: 36,
          },
          {
            label: "星之光大道",
            value: 50,
          },
          {
            label: "白云路东",
            value: 62,
          },
          {
            label: "越秀南路",
            value: 32,
          },
          {
            label: "中山图书馆",
            value: 40,
          },
        ],
        [
          {
            label: "海印桥",
            value: 4,
          },
          {
            label: "星之光大道",
            value: 7,
          },
          {
            label: "白云路东",
            value: 12,
          },
          {
            label: "越秀南路",
            value: 7,
          },
          {
            label: "中山图书馆",
            value: 9,
          },
        ],
        [
          {
            label: "海印桥",
            value: 54,
          },
          {
            label: "星之光大道",
            value: 35,
          },
          {
            label: "白云路东",
            value: 24,
          },
          {
            label: "越秀南路",
            value: 55,
          },
          {
            label: "中山图书馆",
            value: 67,
          },
        ],
      ])
    );

    this.myChart2.setOption(
      this.getOptions([
        [
          {
            label: "海印桥",
            value: 36,
          },
          {
            label: "星之光大道",
            value: 50,
          },
          {
            label: "白云路东",
            value: 62,
          },
          {
            label: "越秀南路",
            value: 32,
          },
          {
            label: "中山图书馆",
            value: 40,
          },
        ],
        [
          {
            label: "海印桥",
            value: 4,
          },
          {
            label: "星之光大道",
            value: 7,
          },
          {
            label: "白云路东",
            value: 12,
          },
          {
            label: "越秀南路",
            value: 7,
          },
          {
            label: "中山图书馆",
            value: 9,
          },
        ],
        [
          {
            label: "海印桥",
            value: 54,
          },
          {
            label: "星之光大道",
            value: 35,
          },
          {
            label: "白云路东",
            value: 24,
          },
          {
            label: "越秀南路",
            value: 55,
          },
          {
            label: "中山图书馆",
            value: 67,
          },
        ],
      ])
    );
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
          options: posList.reduce(function(map, pos) {
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
        onChange: function() {
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
          top: '12px',
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
                { offset: 0, color: "#2885ff" },
                { offset: 0.5, color: "#2065ce" },
                { offset: 1, color: "#164a9f" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            label: labelOption,
            emphasis: {
              focus: "series",
            },
            data: datas[0] || [],
            barWidth: "8px",
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
                { offset: 0, color: "#08d8dd" },
                { offset: 0.5, color: "#089db3" },
                { offset: 1, color: "#097396" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            data: datas[1] || [],
            barWidth: "8px",
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
                { offset: 0, color: "#bfe209" },
                { offset: 0.5, color: "#8b9724" },
                { offset: 1, color: "#5e5b3f" },
              ]),
              borderRadius: [20, 20, 0, 0],
            },
            data: datas[2] || [],
            barWidth: "8px",
          },
        ],
      };

      return option;
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
