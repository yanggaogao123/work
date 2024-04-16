<template>
  <div id="page">
    <div class="container" id="container">
      <div class="content">
        <!-- 头部 -->
        <header class="header"></header>
        <section class="section">
          <div class="section-con">
            <!-- 图表 -->
            <div class="chart-box">
              <div class="chart-left">
                <!-- BigScreenChartOneModal -->
                <big-screen-chart-one-modal :chartData="chartData"></big-screen-chart-one-modal>
              </div>
              <div class="chart-right">
                <big-screen-chart-two-modal :chartData="chartData"></big-screen-chart-two-modal>
              </div>
            </div>
            <!-- 简图 -->
            <div class="car-box"></div>
            <!-- 图表 -->
            <div class="chart-box">
              <div class="chart-left">
                <!-- BigScreenChartOneModal -->
                <big-screen-chart-one-modal :chartData="chartData"></big-screen-chart-one-modal>
              </div>
              <div class="chart-right">
                <big-screen-chart-two-modal :chartData="chartData"></big-screen-chart-two-modal>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import '@/assets/less/base.css';
import moment from 'moment';
import BigScreenChartOneModal from './modules/BigScreenChartOneModal.vue';
import BigScreenChartTwoModal from './modules/BigScreenChartTwoModal.vue';

// 获取容器元素
// var container = document.querySelector('.container');

// 更新缩放比例
function updateScale() {
  var windowWidth = window.innerWidth;
  var windowHeight = window.innerHeight;
  var scaleX = windowWidth / 1920; // 计算宽度比例
  var scaleY = windowHeight / 1080; // 计算高度比例
  var scale = Math.min(scaleX, scaleY); // 取较小的比例
  container.style.transform = 'scale(' + scale + ')'; // 应用缩放

  // 调整内容居中显示
  container.style.transformOrigin = 'top left'; // 设置变换原点
  container.style.left = (windowWidth - 1920 * scale) / 2 + 'px'; // 水平居中
  container.style.top = (windowHeight - 1080 * scale) / 2 + 'px'; // 垂直居中
}

// 页面加载时和窗口大小变化时更新缩放比例
// window.addEventListener('load', updateScale);
// window.addEventListener('resize', updateScale);

export default {
  name: 'BigScreen',
  components: { BigScreenChartOneModal, BigScreenChartTwoModal },
  data() {
    return {
      url: {
        generateSupportSchedule: `${process.env.VUE_APP_BUS_API}/schedule/generateSupportSchedule`,
        getScheduleBySort: `${process.env.VUE_APP_BUS_API}/schedule/getScheduleBySort`,
        adrealInfo: `${process.env.VUE_APP_BUS_API}/simulation/adrealInfo`,
        getRouteList: `${process.env.VUE_APP_BUS_API}/route/getRouteList`,
        getUnionRouteInfo: `${process.env.VUE_APP_BUS_API}/route/getUnionRouteInfo`,
        getMinPlanTime: `${process.env.VUE_APP_BUS_API}/simulation/getMinPlanTime`,
        busConfigure: `${process.env.VUE_APP_BUS_API}/schedule/busConfigure`,
        getScheduleCountResult: `${process.env.VUE_APP_BUS_API}/scheduleCount/getScheduleCountResult`,
        getRuningScheduleConfig: `${process.env.VUE_APP_BUS_API}/schedule/getRuningScheduleConfig`,
        runBus: `${process.env.VUE_APP_BUS_API}/schedule/runBus`,
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
      },
      pageValue: 'a',
      mes: '',
      // routeId: '194',
      // supRouteId: '420',
      // routeName: '36路',
      // supRouteName: '76A路',
      // runDate: '2024-02-05',
      routeId: '',
      supRouteId: '',
      routeName: '',
      supRouteName: '',
      runDate: moment(),
      allRouteList: [],
      routeList: [],
      supRouteList: [],
      tableData: { firstRouteStaList: [], scheduleBusList: [] },
      planType: '1',
      tableBool: false,
      centerData: { mainMap: {}, subMap: {}, titleMap: {} },
      sendData: {},
      centerData: '',
      chartData: '',
      baseData: {},
      chartBool: false,
      carBool: 'a',

      planType: 2,
      pageValue: '',
      time: '',
      playBool: true,
    };
  },
  created() {
    // this.getData();
  },
  mounted() {
    // 获取容器元素
    var container = document.querySelector('.container');
    // 页面加载时和窗口大小变化时更新缩放比例
    window.addEventListener('load', updateScale);
    window.addEventListener('resize', updateScale);
  },
  methods: {
    moment,
    onChange(e) {
      console.log(`checked = ${e.target.value}`);
    },
    getData() {
      const queryString = window.location.search;
      const searchParams = new URLSearchParams(queryString);
      const paramString = searchParams.get('paramString');
      const params = new URLSearchParams();
      params.append('paramString', paramString);
      this.mes = params;
      console.log(params.toString());
      axios.get(`${this.url.getRouteList}?${params}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.allRouteList = res.data.data;
      });

      axios
        .post(
          `${this.url.getMonitorInfo}`,
          {
            routeId: this.routeId,
            runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
          },
          { params }
        )
        .then((res) => {
          console.log(res);
        });

      axios
        .post(
          `${this.url.getMonitorInfo}`,
          {
            routeId: this.supRouteId,
            runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
          },
          { params }
        )
        .then((res) => {
          console.log(res);
        });
    },
    handleSearch(value) {
      console.log(value);
      if (value == '') {
        this.routeList = [];
        return;
      }
      this.routeList = [];
      this.routeList = this.allRouteList.filter((route) => route.routeName.includes(value));
      console.log(this.routeList);
    },
    handleChange(value, option) {
      let params = this.mes;
      // console.log(value, option);
      // console.log(this.routeList);
      let arr = this.routeList.filter((route) => route.routeId == value);
      this.routeId = value;
      this.routeName = arr[0].routeName;
      this.getMinPlanTime();
      axios.post(this.url.getUnionRouteInfo, { routeId: value }, { params }).then((res) => {
        console.log('关联线路信息', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.supRouteList = res.data.data;
      });
    },
    seleChange(value) {
      // console.log(value);
      // console.log(this.supRouteList);
      let arr = this.supRouteList.filter((route) => route.supportRouteId == value);
      this.supRouteId = value;
      this.supRouteName = arr[0].supportRouteName;
      this.carType = arr[0].type;
    },
    onDateChange(date, dateString) {
      console.log(date, dateString);
      this.runDate = dateString;
      this.getMinPlanTime();
    },
    async searchIt() {
      if (!this.routeId || !this.supRouteId || !this.runDate) {
        this.$message.error('请选择相关选项再查询');
        return;
      }
      this.tableBool = true;
      // await this.getMinPlanTime();
      await this.getRuningScheduleConfig();
      // await this.getAdrealInfo();
      // this.sendData = {
      //   routeId: this.routeId,
      //   routeName: this.routeName,
      //   supRouteName: this.supRouteName,
      //   supRouteId: this.supRouteId,
      //   runDate: this.runDate,

      //   // time: this.time,
      //   // busRunData: res.data,
      //   centerData: this.centerData,
      // };
    },
    // 车子仿真数据
    getAdrealInfo() {
      let send = {
        routeId: this.routeId,
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} ${this.time}`,
        supportRouteId: this.supRouteId,
        planType: '1',
      };
      let params = this.mes;
      axios.post(this.url.adrealInfo, send, params).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.sendData = {
          routeId: this.routeId,
          routeName: this.routeName,
          supRouteName: this.supRouteName,
          supRouteId: this.supRouteId,
          runDate: this.runDate,
          time: this.time,
          supRouteId: this.supRouteId,
          busRunData: res.data,
          centerData: this.centerData,
        };
        if ([0, 1, 2, 5].includes(res.data.data.simulationType)) {
          this.carBool = 'b';
        } else if ([3, 4, 6, 7].includes(res.data.data.simulationType)) {
          this.carBool = 'c';
        }
        console.log('carBool', this.carBool);
      });
    },
    // 最早发班时间
    getMinPlanTime() {
      let send = {
        routeId: this.routeId,
        planDate: moment(this.runDate).format('YYYY-MM-DD'),
      };
      let params = this.mes;
      axios.post(this.url.getMinPlanTime, send, params).then((res) => {
        console.log('最早发班时间', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.time = res.data.minPlanTime;
      });
    },

    getBusConfigure() {
      let send = { routeId: this.routeId, supportRouteId: this.supRouteId };
      let params = this.mes;
      axios.post(this.url.busConfigure, send, params).then((res) => {
        console.log('查询线路配车', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
      });
    },
    // 获取报表&图表数据
    getScheduleCountResult() {
      let send = {
        routeId: this.routeId,
        supportRouteId: this.supRouteId,
        // runDate: "2024-01-09 00:00:00",
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
        planType: 2,
      };
      let params = this.mes;
      axios.post(this.url.getScheduleCountResult, send, params).then((res) => {
        console.log('统计报表', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.chartData = res.data.data;
        this.baseData = {
          routeId: this.routeId,
          supRouteId: this.supRouteId,
          routeName: this.routeName,
          supRouteName: this.supRouteName,
        };
        this.chartBool = true;
      });
    },
    // 获取中间区域数据
    getRuningScheduleConfig() {
      let send = {
        routeId: this.routeId,
        supportRouteId: this.supRouteId,
        // runDate: "2024-01-10",
        runDate: moment(this.runDate).format('YYYY-MM-DD'),
      };
      let params = this.mes;
      axios.post(this.url.getRuningScheduleConfig, send, params).then((res) => {
        console.log('车辆配置信息', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          this.centerData = {
            titleMap: {},
            mainMap: {},
            subMap: {},
          };
          // return;
        } else {
          this.centerData = res.data.data;
        }

        this.sendData = {
          routeId: this.routeId,
          routeName: this.routeName,
          supRouteName: this.supRouteName,
          supRouteId: this.supRouteId,
          runDate: this.runDate,
          centerData: this.centerData,
        };
        if ([0, 1, 2, 5].includes(this.carType)) {
          this.carBool = 'b';
        } else if ([3, 4, 6, 7].includes(this.carType)) {
          this.carBool = 'c';
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
body,
html {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden; /* 防止页面滚动 */
  position: relative;
  background: #000;
}
#page {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden; /* 防止页面滚动 */
  position: relative;
  background: #000;
}

.container {
  position: absolute;
  width: 1920px; /* 固定宽度为1920px */
  height: 1080px; /* 固定高度为1080px */
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  transform-origin: top left; /* 缩放原点为左上角 */
  transform: scale(1); /* 初始缩放为1 */
  transition: transform 0.3s ease; /* 添加过渡效果 */
}

.content {
  width: 100%;
  height: 100%;
  background: #0b1340;
  position: relative;
  .header {
    // position: absolute;
    width: 100%;
    height: 90px;
    background: url('../../assets/driverlesscars/tit.png') no-repeat 100% 100%;
    background-size: 100% 100%;
  }
  .section {
    width: 100%;
    height: calc(100% - 90px);
    .section-con {
      padding: 0 15px;
      .chart-box {
        width: 100%;
        height: 240px;
        background: blue;
        display: flex;
        justify-content: space-between;
        .chart-left,
        .chart-right {
          width: 50%;
          background: green;
        }
        .chart-left {
          margin-right: 10px;
        }
        .chart-right {
          margin-left: 10px;
        }
      }
      .car-box {
        width: 100%;
        height: 500px;
        background: red;
      }
    }
  }
}
</style>
