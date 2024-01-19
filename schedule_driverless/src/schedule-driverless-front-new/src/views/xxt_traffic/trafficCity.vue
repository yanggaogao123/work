<template>
  <div id="trafficCity">
    <header>
      <div class="area-sele">
        <span class="name">区域:</span>
        <div style="display:inline-block;">
          <van-dropdown-menu active-color="#FF6701">
            <van-dropdown-item v-model="value1" :options="option1" />
          </van-dropdown-menu>
        </div>
      </div>
      <span class="iconfont icon-bangzhu" @click="gohelp"></span>
    </header>
    <section>
      <traffic-table
        v-if="tableData.length > 0"
        :trafficArr="tableData"
        :bType="2"
      ></traffic-table>
      <img
        v-else
        src="../../assets/duanwang.png"
        class="duanwang-tips"
        v-else
        alt=""
      />
      <div class="charts">
        <div class="tit">交通指数发布时间：{{ chartData.publishTime }}</div>
        <div ref="chartCon" class="charts-con" id="charts-con"></div>
        <!-- <charts v-if="chartData && chartData.today.length>0" :chartData="chartData"></charts> -->
        <div class="chart-tips">
          <ul class="left-part">
            <li>
              <span class="color" style="background:#66910d;"></span>
              <div>畅通[0-2]</div>
            </li>
            <li>
              <span class="color" style="background:#94ba00;"></span>
              <div>基本畅通[2-4]</div>
            </li>
            <li>
              <span class="color" style="background:#ef9900;"></span>
              <div>轻度拥堵[4-6]</div>
            </li>
            <li>
              <span class="color" style="background:#db6c00;"></span>
              <div>中度拥堵[6-8]</div>
            </li>
            <li>
              <span class="color" style="background:#c92f00;"></span>
              <div>严重拥堵[8-10]</div>
            </li>
          </ul>
          <ul class="right-part">
            <li>
              <span class="color" style="background:#fff;"></span>
              <div>今天指数</div>
            </li>
            <li>
              <span class="color" style="background:#CC0000;"></span>
              <div>昨天指数</div>
            </li>
            <li>
              <span class="color" style="background:#00CCCC;"></span>
              <div>上周同期指数</div>
            </li>
          </ul>
        </div>
        <!-- <canvas id="antChart" width="400" height="260"></canvas> -->
      </div>
    </section>
    <footer>
      <footer-nav :pick="0"></footer-nav>
    </footer>
  </div>
</template>

<script>
import trafficTable from "./trafficComponent/trafficTable.vue";
import footerNav from "./trafficComponent/footerBar.vue";
import charts from "./trafficComponent/charts.vue";
import { getIndexByArea } from "@/api/api.js";
export default {
  name: "trafficCity",
  components: {
    trafficTable,
    footerNav,
    charts,
  },
  data() {
    return {
      value1: "6",
      option1: [
        {
          text: "广州市中心区",
          value: "6",
        },
        {
          text: "天河区",
          value: "440106",
        },
        {
          text: "白云区",
          value: "440111",
        },
        {
          text: "荔湾区",
          value: "440103",
        },
        {
          text: "越秀区",
          value: "440104",
        },
        {
          text: "海珠区",
          value: "440105",
        },
      ],
      tableData: [],
      chartData: {},
      myChart: "",
      todayArr: [],
    };
  },
  watch: {
    value1() {
      console.log(this.value1);
      // this.dataInit(this.value1);
      this.go();
    },
    chartData() {
      console.log(this.chartData);
    },
  },
  created() {
    this.dataInit(this.value1);
  },
  mounted() {
    this.go();
    // this.antChartInit();
    // this.newChartInit();
  },
  methods: {
    dataInit(area) {
      let that = this;
      return new Promise((resolve, reject) => {
        getIndexByArea({
          area: area,
          type: 7,
        }).then((res) => {
          console.log(res);
          if (res.retCode != 0) {
            this.$notify("接口出错，请稍后再试！");
            return;
          }
          let data = res.retData;
          let obj = {
            zoneName: data.zoneName,
            congIndex: data.congIndex,
            congName: data.congName,
            roadSpeed: data.roadSpeed,
          };
          that.tableData = [];
          that.tableData.push(obj);
          console.log(that.tableData);

          let todayDate = getDay(0);
          let yesterdayDate = getDay(-1);
          let lastweekDate = getDay(-7);
          console.log(lastweekDate);

          function getDay(day) {
            var today = new Date();
            var targetday_milliseconds =
              today.getTime() + 1000 * 60 * 60 * 24 * day;
            today.setTime(targetday_milliseconds); //注意，这行是关键代码
            var tYear = today.getFullYear();
            var tMonth = today.getMonth();
            var tDate = today.getDate();
            tMonth = doHandleMonth(tMonth + 1);
            tDate = doHandleMonth(tDate);
            return tYear + "/" + tMonth + "/" + tDate;
          }

          function doHandleMonth(month) {
            var m = month;
            if (month.toString().length == 1) {
              m = "0" + month;
            }
            return m;
          }

          // let today = data.today.map((item) => {
          //     let arr = [new Date(`${todayDate} ${item.time}`).getTime(), item.value]
          //     let arr1 = [...arr]
          //     return arr1
          // })
          // let yesterday = data.yesterday.map((item) => {
          //     let arr = [new Date(`${todayDate} ${item.time}`).getTime(), item.value]
          //     return arr
          // })
          // let lastweek = data.lastweek.map((item) => {
          //     let arr = [new Date(`${todayDate} ${item.time}`).getTime(), item.value]
          //     return arr
          // })
          let today = [];
          data.today.forEach((ele) => {
            // let arr = [new Date(`${todayDate} ${ele.time}`).getTime(), ele.value];
            let arr = [
              new Date(`${todayDate} ${ele.time}`).getTime(),
              ele.value,
            ];
            // let arr1 = [...arr]
            today.push(arr);
          });
          // 将原始数据转化为对象数组
          const dataObjectsToday = today.map(([time, value]) => ({
            time,
            value,
          }));
          // 按时间轴排序
          const sortedDataToday = dataObjectsToday.sort(
            (a, b) => a.time - b.time
          );
          const rawDataToday = sortedDataToday.map(({ time, value }) => [
            time,
            value,
          ]);

          let yesterday = [];
          data.yesterday.forEach((ele) => {
            yesterday.push([
              new Date(`${todayDate} ${ele.time}`).getTime(),
              ele.value,
            ]);
          });
          // 将原始数据转化为对象数组
          const dataObjectsYes = yesterday.map(([time, value]) => ({
            time,
            value,
          }));
          // 按时间轴排序
          const sortedDataYes = dataObjectsYes.sort((a, b) => a.time - b.time);
          const rawDataYes = sortedDataYes.map(({ time, value }) => [
            time,
            value,
          ]);

          let lastweek = [];
          data.lastweek.forEach((ele) => {
            lastweek.push([
              new Date(`${todayDate} ${ele.time}`).getTime(),
              ele.value,
            ]);
          });
          // 将原始数据转化为对象数组
          const dataObjectsLastweek = lastweek.map(([time, value]) => ({
            time,
            value,
          }));
          // 按时间轴排序
          const sortedDataLastweek = dataObjectsLastweek.sort(
            (a, b) => a.time - b.time
          );
          const rawDataLastweek = sortedDataLastweek.map(({ time, value }) => [
            time,
            value,
          ]);
          that.chartData = {
            publishTime: data.publishTime,
            today: rawDataToday,
            yesterday: rawDataYes,
            lastweek: rawDataLastweek,
          };
          console.log("chartData", that.chartData);
          resolve(that.chartData);
        });
      });
    },
    chartInit() {
      let that = this;
      return new Promise((resolve, reject) => {
        if (this.chartData.today.length < 1) {
          return;
        }
        // 基于准备好的dom，初始化echarts实例
        var myChart = this.$echarts.init(this.$refs.chartCon);
        // 绘制图表
        var option;
        // console.log(that.todayArr);

        // let arr1 = [];
        // for (var i = 0; i < that.todayArr.length; i++) {
        //     arr1[i] = [...that.todayArr[i]];
        // }
        // console.log('arr1', arr1)

        option = {
          tooltip: {
            trigger: "axis",
          },
          grid: {
            x: 0,
            y: 0,
            x2: 0,
            y2: 20,
          },
          xAxis: {
            type: "time",
            // type: 'category',
            splitNumber: 24,
            minInterval: 3600 * 3 * 1000,
            axisLabel: {
              interval: 4,
              formatter: `{HH}`,
            },
            splitLine: {
              show: false,
            },
          },
          yAxis: {
            type: "value",
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
                color: ["#66910d", "#94ba00", "#ef9900", "#db6c00", "#c92f00"],
              },
            },
          },
          series: [
            {
              name: "今天指数",
              type: "line",
              symbol: "none",
              // smooth: true,
              lineStyle: {
                color: "#fff",
              },
              itemStyle: {
                display: "none",
                color: "#fff",
              },
              data: that.chartData.today,
            },
            {
              name: "昨天指数",
              type: "line",
              symbol: "none",
              lineStyle: {
                color: "#CC0000",
              },
              itemStyle: {
                display: "none",
                color: "#CC0000",
              },
              data: that.chartData.yesterday,
            },
            {
              name: "上周同期指数",
              type: "line",
              symbol: "none",
              lineStyle: {
                color: "#00CCCC",
              },
              itemStyle: {
                display: "none",
                color: "#00CCCC",
              },
              data: that.chartData.lastweek,
            },
          ],
        };
        // that.myChart.setOption(option);
        myChart.setOption(option, true);
        resolve();
      });
    },
    dataResolve() {
      let that = this;
      return new Promise((resolve, reject) => {
        that.todayArr = [];
        for (let i = 0; i < that.chartData.today.length; i++) {
          that.todayArr[i] = [...that.chartData.today[i]];
        }
        console.log(that.todayArr);
        resolve();
      });
    },
    async go() {
      let that = this;
      await this.dataInit(this.value1);
      await this.chartInit();
    },
    newChartInit() {
      var myChart = this.$echarts.init(this.$refs.chartCon);
      var option;
      let base = +new Date(1988, 9, 3);
      let oneDay = 24 * 3600 * 1000;
      let data = [[base, Math.random() * 300]];
      for (let i = 1; i < 20000; i++) {
        let now = new Date((base += oneDay));
        data.push([
          +now,
          Math.round((Math.random() - 0.5) * 20 + data[i - 1][1]),
        ]);
      }
      console.log(data);
      option = {
        tooltip: {
          trigger: "axis",
          position: function(pt) {
            return [pt[0], "10%"];
          },
        },
        title: {
          left: "center",
          text: "Large Ara Chart",
        },
        toolbox: {
          feature: {
            dataZoom: {
              yAxisIndex: "none",
            },
            restore: {},
            saveAsImage: {},
          },
        },
        xAxis: {
          type: "time",
          boundaryGap: false,
        },
        yAxis: {
          type: "value",
          boundaryGap: [0, "100%"],
        },
        dataZoom: [
          {
            type: "inside",
            start: 0,
            end: 20,
          },
          {
            start: 0,
            end: 20,
          },
        ],
        series: [
          {
            name: "Fake Data",
            type: "line",
            smooth: true,
            symbol: "none",
            areaStyle: {},
            data: data,
          },
        ],
      };

      myChart.setOption(option);
    },
    antChartInit() {
      // F2 对数据源格式的要求，仅仅是 JSON 数组，数组的每个元素是一个标准 JSON 对象。
      const data = [
        {
          genre: "Sports",
          sold: 275,
        },
        {
          genre: "Strategy",
          sold: 115,
        },
        {
          genre: "Action",
          sold: 120,
        },
        {
          genre: "Shooter",
          sold: 350,
        },
        {
          genre: "Other",
          sold: 150,
        },
      ];

      // Step 1: 创建 Chart 对象
      console.log(this.$F2);
      const antChart = new this.$F2.Chart({
        id: "antChart",
        pixelRatio: window.devicePixelRatio, // 指定分辨率
      });

      // Step 2: 载入数据源
      antChart.source(data);

      // Step 3：创建图形语法，绘制柱状图，由 genre 和 sold 两个属性决定图形位置，genre 映射至 x 轴，sold 映射至 y 轴
      antChart
        .interval()
        .position("genre*sold")
        .color("genre");

      // Step 4: 渲染图表
      antChart.render();
    },
    gohelp() {
      window.location.href =
        "http://nxxt.gzyyjt.net:8008/xxt_app/traffi/congestion?xxt_title=";
    },
    changeArea(data) {
      console.log(data);
    },
  },
};
</script>

<style lang="less" scoped>
@import "../../assets/font-traffic/iconfont.css";

html,
body {
  width: 100%;
  background: #eeeeee;
}

#trafficCity {
  width: 100%;
  height: 100%;
  background: #eeeeee;

  header {
    width: 100%;
    height: 40px;
    background: #ffffff;
    display: flex;
    justify-content: space-between;

    .area-sele {
      display: inline-block;
      margin-left: 4%;

      .name {
        font-size: 1.5rem;
      }

      .van-dropdown-menu {
        width: 100%;
        height: 40px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }

    .iconfont {
      font-size: 2rem;
      line-height: 40px;
      margin-right: 4%;
    }
  }

  section {
    width: 100%;
    background: #fff;
    .duanwang-tips {
      width: 50%;
      display: block;
      margin: 0 auto;
    }
    .table {
      width: 100%;

      ul {
        display: flex;
        width: 100;
        height: 42px;
        justify-content: center;
        align-items: center;
        box-sizing: border-box;
        border-top: 1px solid #ccc;
        background: #f8f8f8;

        li {
          width: 25%;
          text-align: center;
          font-size: 1.4rem;
        }
      }
    }

    .charts {
      .tit {
        text-align: center;
        font-size: 1.4rem;
        line-height: 28px;
      }

      .charts-con {
        width: 100%;
        height: 200px;
      }

      .chart-tips {
        display: flex;
        width: 100%;
        margin-top: 20px;

        ul {
          width: 50%;
          padding-left: 10%;
        }

        .left-part {
          li {
            height: 24px;
            margin-bottom: 20px;

            .color {
              display: inline-block;
              width: 25%;
              height: 16px;
              border-radius: 4px;
              margin: 4px 6px 0 0;
            }

            div {
              display: inline-block;
              line-height: 24px;
              vertical-align: top;
            }
          }
        }

        .right-part {
          li {
            height: 40px;
            margin-bottom: 30px;
          }

          .color {
            display: inline-block;
            width: 28px;
            height: 28px;
            border-radius: 4px;
            margin: 6px 6px 0 0;
            box-sizing: border-box;
            border: 1px solid #ccc;
          }

          div {
            display: inline-block;
            line-height: 40px;
            vertical-align: top;
          }
        }
      }
    }
  }

  footer {
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;

    .footer-nav {
      width: 100%;
      height: 50px;
      box-sizing: border-box;
      background: #fff;
      border-top: 1px solid #ccc;
      display: flex;
      margin: 0;

      li {
        flex: 1;
        text-align: center;

        .iconfont {
          font-size: 18px;
        }

        p {
          margin: 0;
          font-size: 1.4rem;
        }
      }

      .pick {
        color: #ff6701;
      }
    }
  }
}
</style>
