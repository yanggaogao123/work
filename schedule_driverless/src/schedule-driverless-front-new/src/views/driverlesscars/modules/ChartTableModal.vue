<template>
  <div id="main">
    <header>
      <span class="name">班次客流量</span>
      <div>
        <a-radio-group v-model="seeType" button-style="solid">
          <a-radio-button value="a">图表</a-radio-button>
          <a-radio-button value="b">报表</a-radio-button>
        </a-radio-group>
      </div>
    </header>
    <section>
      <div class="con-chart" v-if="seeType == 'a'">
        <div class="up-part">
          <div class="left-part">
            <div id="up-left-chart"></div>
          </div>
          <div class="right-part">
            <div id="up-right-chart"></div>
          </div>
        </div>
        <div class="down-part">
          <div class="left-part">
            <div id="down-left-chart"></div>
          </div>
          <div class="right-part">
            <div id="down-right-chart"></div>
          </div>
        </div>
      </div>
      <div class="con-table" v-else>
        <div class="up-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="mainUpColumns"
              :dataSource="mainUpData"
              :pagination="false"
              :loading="loading"
              @change="handleTableChange"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 400 }"
            >
              <!-- <span slot="action" slot-scope="text, record" v-has="'1015:edit'">
                    <a @click="handleEdit(record)">编辑</a>

                    <a-divider type="vertical" />
                    <a-dropdown>
                        <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                        <a-menu slot="overlay">
                            <a-menu-item>
                                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                                    <a>删除</a>
                                </a-popconfirm>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </span> -->
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="mainDownColumns"
              :dataSource="mainDownData"
              :pagination="false"
              :loading="loading"
              @change="handleTableChange"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div>
        </div>
        <div class="down-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="supUpColumns"
              :dataSource="supUpData"
              :pagination="false"
              :loading="loading"
              @change="handleTableChange"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="supDownColumns"
              :dataSource="supDownData"
              :pagination="false"
              :loading="loading"
              @change="handleTableChange"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import "@/assets/less/TableExpand.less";
// import { JeecgListMixin } from "@/assets/js/JeecgListMixin";
// import { JeecgListMixin } from "@/mixins/JeecgListMixin";
import Vue from "vue";
import axios from "axios";
import "@/assets/less/base.css";
export default {
  name: "ChartTableModal",
  props: ["chartData", "baseData"],
  // mixins: [JeecgListMixin],
  data() {
    return {
      seeType: "a",
      mainUpColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },
        {
          title: `${this.baseData.routeName}(上行) 时段`,
          align: "center",
          dataIndex: "time",
        },
        {
          title: "总班次",
          align: "center",
          dataIndex: "upClasses",
        },
        {
          title: "无人车或支援班次",
          align: "center",
          dataIndex: "upSupportClasses",
        },
        {
          title: "总客流人次",
          align: "center",
          dataIndex: "upPassenger",
        },
        {
          title: "最高车内人数",
          align: "center",
          dataIndex: "upMax",
        },
        {
          title: "班次车流人数",
          align: "center",
          dataIndex: "uploadPeopleClasses",
        },
        {
          title: "平均停站时间",
          align: "center",
          dataIndex: "upStopTime",
        },
        {
          title: "平均单程时间",
          align: "center",
          dataIndex: "upIntersite",
        },
        {
          title: "平均发班间隔",
          align: "center",
          dataIndex: "upInterval",
        },
      ],
      mainDownColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },
        {
          title: `${this.baseData.routeName}(下行) 时段`,
          align: "center",
          dataIndex: "time",
        },
        {
          title: "总班次",
          align: "center",
          dataIndex: "upClasses",
        },
        {
          title: "无人车或支援班次",
          align: "center",
          dataIndex: "upSupportClasses",
        },
        {
          title: "总客流人次",
          align: "center",
          dataIndex: "upPassenger",
        },
        {
          title: "最高车内人数",
          align: "center",
          dataIndex: "upMax",
        },
        {
          title: "班次车流人数",
          align: "center",
          dataIndex: "uploadPeopleClasses",
        },
        {
          title: "平均停站时间",
          align: "center",
          dataIndex: "upStopTime",
        },
        {
          title: "平均单程时间",
          align: "center",
          dataIndex: "upIntersite",
        },
        {
          title: "平均发班间隔",
          align: "center",
          dataIndex: "upInterval",
        },
      ],
      supUpColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },
        {
          title: `${this.baseData.supRouteName}(上行) 时段`,
          align: "center",
          dataIndex: "time",
        },
        {
          title: "总班次",
          align: "center",
          dataIndex: "upClasses",
        },
        {
          title: "无人车或支援班次",
          align: "center",
          dataIndex: "upSupportClasses",
        },
        {
          title: "总客流人次",
          align: "center",
          dataIndex: "upPassenger",
        },
        {
          title: "最高车内人数",
          align: "center",
          dataIndex: "upMax",
        },
        {
          title: "班次车流人数",
          align: "center",
          dataIndex: "uploadPeopleClasses",
        },
        {
          title: "平均停站时间",
          align: "center",
          dataIndex: "upStopTime",
        },
        {
          title: "平均单程时间",
          align: "center",
          dataIndex: "upIntersite",
        },
        {
          title: "平均发班间隔",
          align: "center",
          dataIndex: "upInterval",
        },
      ],
      supDownColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },
        {
          title: `${this.baseData.supRouteName}(下行) 时段`,
          align: "center",
          dataIndex: "time",
        },
        {
          title: "总班次",
          align: "center",
          dataIndex: "upClasses",
        },
        {
          title: "无人车或支援班次",
          align: "center",
          dataIndex: "upSupportClasses",
        },
        {
          title: "总客流人次",
          align: "center",
          dataIndex: "upPassenger",
        },
        {
          title: "最高车内人数",
          align: "center",
          dataIndex: "upMax",
        },
        {
          title: "班次车流人数",
          align: "center",
          dataIndex: "uploadPeopleClasses",
        },
        {
          title: "平均停站时间",
          align: "center",
          dataIndex: "upStopTime",
        },
        {
          title: "平均单程时间",
          align: "center",
          dataIndex: "upIntersite",
        },
        {
          title: "平均发班间隔",
          align: "center",
          dataIndex: "upInterval",
        },
      ],
      mainUpData: [],
      mainDownData: [],
      supUpData: [],
      supDownData: [],
    };
  },
  watch: {
    chartData() {
      setTimeout(() => {
        this.ulChartInit();
        this.urChartInit();
        this.dlChartInit();
        this.drChartInit();
      }, 500);
    },
    seeType() {
      if (this.seeType == "a") {
        setTimeout(() => {
          this.ulChartInit();
          this.urChartInit();
          this.dlChartInit();
          this.drChartInit();
        }, 500);
      } else if (this.seeType == "b") {
        this.tableInit();
      }
    },
  },
  created() {
    this.tableInit();
  },
  mounted() {
    this.ulChartInit();
    this.urChartInit();
    this.dlChartInit();
    this.drChartInit();
  },
  methods: {
    tableInit() {
      this.mainUpData = [];
      let data = this.chartData.mainUpWordMap;
      for (let i in data) {
        if (i < 10) {
          data[i].time = `0${i}:00-0${i}:59`;
        } else {
          data[i].time = `${i}:00-${i}:59`;
        }
        this.mainUpData.push(data[i]);
      }

      this.mainDownData = [];
      let data1 = this.chartData.mainDownWordMap;
      for (let i in data) {
        if (i < 10) {
          data[i].time = `0${i}:00-0${i}:59`;
        } else {
          data[i].time = `${i}:00-${i}:59`;
        }
        this.mainDownData.push(data[i]);
      }

      this.supUpData = [];
      let data2 = this.chartData.subUpWordMap;
      for (let i in data) {
        if (i < 10) {
          data[i].time = `0${i}:00-0${i}:59`;
        } else {
          data[i].time = `${i}:00-${i}:59`;
        }
        this.supUpData.push(data[i]);
      }

      this.supDownData = [];
      let data3 = this.chartData.subDownWordMap;
      for (let i in data) {
        if (i < 10) {
          data[i].time = `0${i}:00-0${i}:59`;
        } else {
          data[i].time = `${i}:00-${i}:59`;
        }
        this.supDownData.push(data[i]);
      }
      //   this.mainDownData = this.chartData.mainDownWordMap;
      //   console.log("mainUpData", this.mainUpData);
      //   let supUpData = this.chartData.mainUpWordMap;
      //   let supDownData = this.chartData.mainUpWordMap;
    },
    ulChartInit() {
      const that = this;
      let chartDom = document.getElementById("up-left-chart");
      let upLeftChart = this.$echarts.init(chartDom);
      let arr = Object.values(this.chartData.mainUploadPeopleNumMap);
      //   console.log("arr", arr);
      let option = {
        title: {
          text: that.baseData.routeName,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            crossStyle: {
              color: "#999",
            },
          },
        },
        // legend: {
        //   data: [
        //     "时段班次核载人数",
        //     "时段客流人数",
        //     "时段最高车内人数",
        //     "时段发车班次数",
        //     "时段班次满载率(%)",
        //     "时段平均间隔",
        //     "时段平均周转时间",
        //   ],
        // },
        xAxis: [
          {
            type: "category",
            data: [
              "00:00",
              "01:00",
              "03:00",
              "04:00",
              "05:00",
              "06:00",
              "07:00",
              "08:00",
              "09:00",
              "10:00",
              "11:00",
              "12:00",
              "13:00",
              "14:00",
              "15:00",
              "16:00",
              "17:00",
              "18:00",
              "19:00",
              "20:00",
              "21:00",
              "22:00",
              "23:00",
            ],
            axisPointer: {
              type: "shadow",
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "上行",
            min: 0,
            max: 2000,
            interval: 200,
            axisLabel: {
              formatter: "{value}",
            },
          },
          {
            type: "value",
            // name: "Temperature",
            //   min: 0,
            //   max: 25,
            //   interval: 5,
            axisLabel: {
              formatter: "{value}",
            },
          },
        ],
        series: [
          {
            name: "时段班次核载人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUploadPeopleNumMap),
          },
          {
            name: "时段客流人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUpPassengerMap),
          },
          {
            name: "时段最高车内人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUpMaxPeopleMap),
          },
          {
            name: "时段发车班次数",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUpClassesMap),
          },
          {
            name: "时段班次满载率(%)",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUpFullPercentMap),
          },
          {
            name: "时段平均间隔",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUpIntervalMap),
          },
          {
            name: "时段平均周转时间",
            type: "line",
            yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainUpIntersiteMap),
          },
        ],
      };
      upLeftChart.setOption(option);
    },
    urChartInit() {
      const that = this;
      let chartDom = document.getElementById("up-right-chart");
      let upRightChart = this.$echarts.init(chartDom);
      let option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            crossStyle: {
              color: "#999",
            },
          },
        },
        legend: {
          data: [
            "时段班次核载人数",
            "时段客流人数",
            "时段最高车内人数",
            "时段发车班次数",
            "时段班次满载率(%)",
            "时段平均间隔",
            "时段平均周转时间",
          ],
          type: "scroll",
        },
        xAxis: [
          {
            type: "category",
            data: [
              "00:00",
              "01:00",
              "03:00",
              "04:00",
              "05:00",
              "06:00",
              "07:00",
              "08:00",
              "09:00",
              "10:00",
              "11:00",
              "12:00",
              "13:00",
              "14:00",
              "15:00",
              "16:00",
              "17:00",
              "18:00",
              "19:00",
              "20:00",
              "21:00",
              "22:00",
              "23:00",
            ],
            axisPointer: {
              type: "shadow",
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "下行",
            min: 0,
            max: 2000,
            interval: 200,
            axisLabel: {
              formatter: "{value}",
            },
          },
          {
            type: "value",
            // name: "Temperature",
            //   min: 0,
            //   max: 25,
            //   interval: 5,
            axisLabel: {
              formatter: "{value}",
            },
          },
        ],
        series: [
          {
            name: "时段班次核载人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownloadPeopleNumMap),
          },
          {
            name: "时段客流人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownPassengerMap),
          },
          {
            name: "时段最高车内人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownMaxPeopleMap),
          },
          {
            name: "时段发车班次数",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownClassesMap),
          },
          {
            name: "时段班次满载率(%)",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownFullPercentMap),
          },
          {
            name: "时段平均间隔",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownIntervalMap),
          },
          {
            name: "时段平均周转时间",
            type: "line",
            yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.mainDownIntersiteMap),
          },
        ],
      };
      upRightChart.setOption(option);
    },
    dlChartInit() {
      const that = this;
      let chartDom = document.getElementById("down-left-chart");
      let downLeftChart = this.$echarts.init(chartDom);
      let option = {
        title: {
          text: that.baseData.supRouteName,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            crossStyle: {
              color: "#999",
            },
          },
        },
        // legend: {
        //   data: [
        //     "时段班次核载人数",
        //     "时段客流人数",
        //     "时段最高车内人数",
        //     "时段发车班次数",
        //     "时段班次满载率(%)",
        //     "时段平均间隔",
        //     "时段平均周转时间",
        //   ],
        // },
        xAxis: [
          {
            type: "category",
            data: [
              "00:00",
              "01:00",
              "03:00",
              "04:00",
              "05:00",
              "06:00",
              "07:00",
              "08:00",
              "09:00",
              "10:00",
              "11:00",
              "12:00",
              "13:00",
              "14:00",
              "15:00",
              "16:00",
              "17:00",
              "18:00",
              "19:00",
              "20:00",
              "21:00",
              "22:00",
              "23:00",
            ],
            axisPointer: {
              type: "shadow",
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "上行",
            min: 0,
            max: 2000,
            interval: 200,
            axisLabel: {
              formatter: "{value}",
            },
          },
          {
            type: "value",
            // name: "Temperature",
            //   min: 0,
            //   max: 25,
            //   interval: 5,
            axisLabel: {
              formatter: "{value}",
            },
          },
        ],
        series: [
          {
            name: "时段班次核载人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUploadPeopleNumMap),
          },
          {
            name: "时段客流人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUpPassengerMap),
          },
          {
            name: "时段最高车内人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUpMaxPeopleMap),
          },
          {
            name: "时段发车班次数",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUpClassesMap),
          },
          {
            name: "时段班次满载率(%)",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUpFullPercentMap),
          },
          {
            name: "时段平均间隔",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUpIntervalMap),
          },
          {
            name: "时段平均周转时间",
            type: "line",
            yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subUpIntersiteMap),
          },
        ],
      };
      downLeftChart.setOption(option);
    },
    drChartInit() {
      const that = this;
      let chartDom = document.getElementById("down-right-chart");
      let downRightChart = this.$echarts.init(chartDom);
      let option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            crossStyle: {
              color: "#999",
            },
          },
        },
        legend: {
          data: [
            "时段班次核载人数",
            "时段客流人数",
            "时段最高车内人数",
            "时段发车班次数",
            "时段班次满载率(%)",
            "时段平均间隔",
            "时段平均周转时间",
          ],
          type: "scroll",
        },
        xAxis: [
          {
            type: "category",
            data: [
              "00:00",
              "01:00",
              "03:00",
              "04:00",
              "05:00",
              "06:00",
              "07:00",
              "08:00",
              "09:00",
              "10:00",
              "11:00",
              "12:00",
              "13:00",
              "14:00",
              "15:00",
              "16:00",
              "17:00",
              "18:00",
              "19:00",
              "20:00",
              "21:00",
              "22:00",
              "23:00",
            ],
            axisPointer: {
              type: "shadow",
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "下行",
            min: 0,
            max: 2000,
            interval: 200,
            axisLabel: {
              formatter: "{value}",
            },
          },
          {
            type: "value",
            // name: "Temperature",
            //   min: 0,
            //   max: 25,
            //   interval: 5,
            axisLabel: {
              formatter: "{value}",
            },
          },
        ],
        series: [
          {
            name: "时段班次核载人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownloadPeopleNumMap),
          },
          {
            name: "时段客流人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownPassengerMap),
          },
          {
            name: "时段最高车内人数",
            type: "bar",
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownMaxPeopleMap),
          },
          {
            name: "时段发车班次数",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownClassesMap),
          },
          {
            name: "时段班次满载率(%)",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownFullPercentMap),
          },
          {
            name: "时段平均间隔",
            type: "line",
            // yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownIntervalMap),
          },
          {
            name: "时段平均周转时间",
            type: "line",
            yAxisIndex: 1,
            tooltip: {
              valueFormatter: function (value) {
                return value;
              },
            },
            data: Object.values(that.chartData.subDownIntersiteMap),
          },
        ],
      };
      downRightChart.setOption(option);
    },
  },
};
</script>

<style lang="less" scoped>
#main {
  width: 100%;
  background: #dbefff;

  header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 70px;
    box-sizing: border-box;
    padding: 0 70px;
    border-top: 1px solid #99bbe8;
    border-bottom: 1px solid #99bbe8;
    .name {
      font-size: 16px;
      font-weight: 600;
    }
  }
  section {
    .con-chart,
    .con-table {
      width: 100%;
      padding: 0 70px;
      box-sizing: border-box;
      .up-part,
      .down-part {
        display: flex;
        height: 384px;
        .left-part,
        .right-part {
          width: 50%;
          height: 100%;
          box-sizing: border-box;
          div {
            width: 100%;
            height: 100%;
          }
        }
      }
      .up-part {
        margin-top: 20px;
      }
    }

    .con-table {
      .up-part,
      .down-part {
        display: flex;
        height: 484px;
        .left-part,
        .right-part {
          width: 50%;
          height: 100%;
          box-sizing: border-box;
          div {
            width: 100%;
            height: 100%;
          }
        }
      }
      .down-part {
        margin-top: 20px;
      }
    }
    .table-blue {
      /deep/.ant-table-thead tr th {
        background: #b8deff !important;
      }
      /deep/.ant-table-hide-scrollbar {
        min-width: 5px !important;
      }
    }
    .table-blue ::-webkit-scrollbar {
      width: 5px;
      //   height: 2px;
      //   background: #ccc;
      //   border-radius: 10px; /*外层轨道*/
    }
    .table-green {
      /deep/.ant-table-thead tr th {
        background: #bce9f0 !important;
      }
      /deep/.ant-table-hide-scrollbar {
        min-width: 5px !important;
      }
    }
    .table-green ::-webkit-scrollbar {
      width: 5px;
      //   height: 2px;
      //   background: #ccc;
      //   border-radius: 10px; /*外层轨道*/
    }
  }
}
</style>
