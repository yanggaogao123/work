<template>
  <div id="page">
    <div class="container" id="container">
      <div class="content">
        <!-- 头部 -->
        <header class="header">
          <img class="flash-left" src="@/assets/driverlesscars/falsh.gif" alt="" />
          <img class="flash-right" src="@/assets/driverlesscars/falsh.gif" alt="" />
          <div class="header-con">
            <div class="header-left">
              <div class="search-box">
                <img src="@/assets/driverlesscars/bs-top-left.png" alt="" />
                <div class="search-con">
                  <input type="text" class="ipt" @keyup="searchIpt" v-model="searchName" />
                  <a-icon type="up" @click="showSearchList" v-show="!searchListBool" />
                  <a-icon type="down" @click="showSearchList" v-show="searchListBool" />
                  <ul class="ipt-list" v-show="searchListBool">
                    <li @click="clickList(item)" :key="i" v-for="(item, i) in allRouteList">{{ item.routeName }}-{{
                    item.supportRouteName }}</li>
                  </ul>
                </div>
              </div>
              <div class="weather">{{ moment().format('YYYY-MM-DD') }}</div>
            </div>
            <div class="header-middle">公交混编自动驾驶高效调度平台</div>
            <div class="header-right">
              <img src="@/assets/driverlesscars/bs-top-right.png" alt="" />

              <div class="header-right-con">
                <ul class="bus-list" id="movingDiv">
                  <li v-for="(item, i) in supList" :key="i" @click="showMap(item)">
                    {{ item.status == 1 ? '无人车' : '支援车' }}
                    {{ item.busName }}
                    {{ moment(item.planTime).format('HH:mm') }}
                  </li>
                  <!-- <li>支援车 D102 7:10</li>
                  <li>支援车 D102 7:10</li>
                  <li>支援车 D102 7:10</li> -->
                </ul>
              </div>
            </div>
          </div>
        </header>
        <section class="section">
          <div class="section-con">
            <!-- 图表 -->
            <div class="chart-box">
              <div class="chart-left">
                <!-- BigScreenChartOneModal -->
                <big-screen-chart-one-modal title="101路日均各时段班次客运量"></big-screen-chart-one-modal>
              </div>
              <div class="chart-right">
                <big-screen-chart-two-modal title="101路日均各时段班次客运量"></big-screen-chart-two-modal>
              </div>
            </div>
            <!-- 简图 -->
            <div class="car-box">
              <!-- <big-screen-car-one-modal></big-screen-car-one-modal> -->
              <big-screen-car-two-modal :sendData="sendData"></big-screen-car-two-modal>
            </div>
            <!-- 图表 -->
            <div class="chart-box">
              <div class="chart-left">
                <!-- BigScreenChartOneModal -->
                <big-screen-chart-one-modal routeId="106" title="106路日均各时段班次客运量"></big-screen-chart-one-modal>
              </div>
              <div class="chart-right">
                <big-screen-chart-two-modal routeId="106" title="106路日均各时段班次客运量"></big-screen-chart-two-modal>
              </div>
            </div>
          </div>
        </section>
        <big-screen-map :sendData="sendData" :visibility="showMapModule" @hideMapModule="hideMap"></big-screen-map>
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
import BigScreenCarOneModal from './modules/BigScreenCarOneModal.vue';
import BigScreenCarTwoModal from './modules/BigScreenCarTwoModal.vue';
import BigScreenMap from './modules/BigScreenMap.vue';

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
  components: {
    BigScreenChartOneModal,
    BigScreenChartTwoModal,
    BigScreenCarOneModal,
    BigScreenCarTwoModal,
    BigScreenMap,
  },
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

        // 模糊搜索线路组接口
        getByRouteNameKey: `${process.env.VUE_APP_BUS_API}/route/getByRouteNameKey`,
        // 未来一小时支援车
        getOneHourSupportPlan: `${process.env.VUE_APP_BUS_API}/schedule/getOneHourSupportPlan`,
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

      // 搜索框数据
      searchName: '',
      searchObj: {},
      searchListBool: false,

      // 滚动框数据
      supList: [],
      movingTimer: '',

      // 是否展示map
      showMapModule: false,
    };
  },
  created() {
    this.getData();
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
      axios.post(`${this.url.getByRouteNameKey}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.allRouteList = res.data.data;
      });
    },

    // 搜索栏
    showSearchList() {
      this.searchListBool = !this.searchListBool;
    },
    async clickList(item) {
      this.searchObj = item;
      this.searchName = `${item.routeName}-${item.supportRouteName}`;
      this.routeId = item.routeId;
      this.routeName = item.routeName;
      this.supRouteId = item.supportRouteId;
      this.supRouteName = item.supportRouteName;
      this.carType = item.type;
      this.searchListBool = false;

      let params = this.mes;
      await axios.post(`${this.url.getOneHourSupportPlan}`, { routeId: this.routeId, supportRouteId: this.supRouteId }, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.supList = res.data.data;

        clearInterval(this.movingTimer);

        // 获取需要移动的div元素
        const movingDiv = document.getElementById('movingDiv');

        // 定义初始位置和每次移动的距离
        let currentPosition = 0;
        const moveDistance = 50;

        // 定义向上移动的函数
        function moveUp() {
          // 向上移动指定距离
          currentPosition -= moveDistance;
          movingDiv.style.top = currentPosition + 'px';

          // 当到达底部时，自动返回到最顶部
          if (currentPosition <= -movingDiv.offsetHeight) {
            currentPosition = 0;
            movingDiv.style.top = currentPosition + 'px';
          }
        }

        // 定时触发向上移动函数
        this.movingTimer = setInterval(moveUp, 2000); // 每秒触发一次向上移动函数
      });

      await this.getRuningScheduleConfig();
    },
    searchIpt() {
      axios
        .post(`${this.url.getByRouteNameKey}`, {
          routeNameKey: this.searchName,
        })
        .then((res) => {
          console.log(res);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          this.searchListBool = true;
          this.allRouteList = res.data.data;
        });
    },

    /****************************/

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
          carType: this.carType,
        };
        if ([0, 1, 2, 5].includes(this.carType)) {
          this.carBool = 'b';
        } else if ([3, 4, 6, 7].includes(this.carType)) {
          this.carBool = 'c';
        }
      });
    },
    showMap(selectData) {
      this.showMapModule = true
      this.sendData.busId = selectData.busId ? selectData.selectData : 3010461
    },
    hideMap() {
      this.showMapModule = false
      console.log(this.showMapModule)
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
  overflow: hidden;
  /* 防止页面滚动 */
  position: relative;
  background: #000;
}

#page {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
  /* 防止页面滚动 */
  position: relative;
  background: #000;
}

.container {
  position: absolute;
  width: 1920px;
  /* 固定宽度为1920px */
  height: 1080px;
  /* 固定高度为1080px */
  display: flex;
  align-items: center;
  /* 垂直居中 */
  justify-content: center;
  /* 水平居中 */
  transform-origin: top left;
  /* 缩放原点为左上角 */
  transform: scale(1);
  /* 初始缩放为1 */
  transition: transform 0.3s ease;
  /* 添加过渡效果 */
}

.content {
  width: 100%;
  height: 100%;
  background: #091a4f;
  position: relative;

  .header {
    // position: absolute;
    width: 100%;
    height: 120px;
    position: relative;
    display: flex;

    .flash-left,
    .flash-right {
      width: 50%;
      height: 100%;
    }

    .flash-right {
      transform: scaleX(-1);
    }

    .header-con {
      position: absolute;
      width: 100%;
      height: 90px;
      top: 0;
      left: 0;
      display: flex;
      justify-content: space-between;

      .header-left {
        .search-box {
          position: relative;
          display: inline-block;
          margin: 0 0 0 50px;

          img {
            width: 360px;
            height: 90px;
          }

          .search-con {
            position: absolute;
            top: 30px;
            left: 100px;

            .ipt {
              width: 180px;
              height: 36px;
              border: none;
              background-color: transparent;
              color: #fff;
              line-height: 36px;
              font-size: 24px;
            }

            i {
              font-size: 20px;
              color: #fff;
            }

            .ipt-list {
              position: absolute;
              top: 42px;
              left: 0px;
              z-index: 2;
              color: rgb(230, 247, 255);
              font-size: 20px;
              height: 160px;
              overflow: auto;

              li {
                width: 249px;
                height: 48px;
                border-radius: 0px;
                border: none;
                background: rgba(9, 25, 79, 0.8);
                cursor: pointer;
                line-height: 48px;
                margin-bottom: 4px;

                &:hover {
                  background: #005873;
                }
              }

              &::-webkit-scrollbar {
                display: none;
                /* Chrome Safari */
              }
            }
          }
        }

        .weather {
          display: inline-block;
          font-size: 18px;
          color: rgb(189, 222, 239);
          letter-spacing: 2px;
          font-weight: normal;
          line-height: 100px;
          vertical-align: top;
          margin-left: 20px;
        }
      }

      .header-middle {
        width: 520px;
        position: absolute;
        color: #fff;
        font-size: 32px;
        font-weight: 600;
        line-height: 80px;
        letter-spacing: 5px;
        left: 0;
        right: 0;
        top: 0;
        margin: 0 auto;
      }

      .header-right {
        position: relative;
        margin-right: 50px;

        img {
          width: 300px;
          height: 90px;
        }

        .header-right-con {
          position: absolute;
          top: 24px;
          left: 114px;
          width: 200px;
          height: 50px;
          overflow: scroll;

          &::-webkit-scrollbar {
            display: none;
            /* Chrome Safari */
          }
        }

        .bus-list {
          position: absolute;
          color: #fff;
          top: 0;
          left: 0;
          line-height: 50px;
          font-size: 20px;
          // transition: top 0.5s ease-in-out;
          cursor: pointer;
        }
      }

      // background: red;
    }
  }

  .section {
    position: absolute;
    width: 100%;
    height: calc(100% - 100px);
    top: 92px;

    .section-con {
      padding: 0 15px;

      .chart-box {
        width: 100%;
        height: 240px;
        // background: blue;
        display: flex;
        justify-content: space-between;

        .chart-left,
        .chart-right {
          width: 50%;
          // background: green;
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
        // background: red;
      }
    }
  }
}
</style>
