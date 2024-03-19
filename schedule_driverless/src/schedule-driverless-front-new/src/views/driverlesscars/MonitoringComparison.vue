<template>
  <a-card :bordered="false">
    <div id="main">
      <header>
        <a-form layout="inline">
          <a-form-item>
            <div class="select-button" @click="showModal">
              <img src="@/assets/driverlesscars/lineIcon.png" alt="" />
              线路选择
            </div>
          </a-form-item>
          <a-form-item>
            <ul>
              <li class="selected" v-if="item.routeName" v-for="(item, i) in busList">
                <span>{{ `${item.routeName}/${item.supRouteName}` }}</span>
                <a-icon type="close" />
              </li>
            </ul>
          </a-form-item>

          <a-form-item style="float: right">
            <div class="right-button">
              <a-button>参数设置</a-button>
              <a-button>首轮</a-button>
              <a-button icon="redo">刷新</a-button>
              <!-- <a-button icon="play-circle">播放</a-button>
              <a-button icon="pause">暂停</a-button> -->
            </div>
          </a-form-item>
        </a-form>
      </header>

      <a-modal title="选择线路组" :visible="visible" :width="width" @ok="handleOk" @cancel="handleCancel">
        <div class="line-list-con">
          <!-- <a-button type="primary" @click="addLine">添加线路组</a-button> -->
          <ul>
            <li v-for="(item, i) in selectList">
              <div class="select-box">
                <div class="mask" v-show="i == 1 && showOne"></div>
                <div class="mask" v-show="i == 2 && showTwo"></div>
                <span>组{{ i + 1 }}</span>
                <div class="name">
                  <span>*</span>
                  关联线路:
                </div>
                <line-select-list-modal :number="i" @showOne="showMsgOne" @showTwo="showMsgTwo" @lineInfo="reciveMsg"></line-select-list-modal>
              </div>
              <!-- <div class="delete-it" @click="deleteIt(i)">删除</div> -->
            </li>
          </ul>
        </div>
      </a-modal>
      <template v-for="(item, i) in busList">
        <car-type-five-modal v-show="item.carBool == 'b'" :sendData="item.sendData"></car-type-five-modal>
        <car-type-six-modal v-show="item.carBool == 'c'" :sendData="item.sendData"></car-type-six-modal>
      </template>
    </div>
  </a-card>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import '@/assets/less/base.css';
import moment from 'moment';
import CarTypeFiveModal from './modules/CarTypeFiveModal.vue';
import CarTypeSixModal from './modules/CarTypeSixModal.vue';
import lineSelectListModal from './modules/lineSelectListModal.vue';
export default {
  name: 'MonitoringComparison',
  components: {
    CarTypeFiveModal,
    CarTypeSixModal,
    lineSelectListModal,
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
        getRouteUpDownInfo: `${process.env.VUE_APP_BUS_API}/route/getRouteUpDownInfo`,
        busConfigure: `${process.env.VUE_APP_BUS_API}/schedule/busConfigure`,
        getScheduleCountResult: `${process.env.VUE_APP_BUS_API}/scheduleCount/getScheduleCountResult`,
        getRuningScheduleConfig: `${process.env.VUE_APP_BUS_API}/schedule/getRuningScheduleConfig`,
        runBus: `${process.env.VUE_APP_BUS_API}/schedule/runBus`,
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
      },
      pageValue: 'a',
      mes: '',
      routeId: '194',
      supRouteId: '420',
      routeName: '36路',
      supRouteName: '76A路',
      runDate: '2024-02-05',
      // routeId: "",
      // supRouteId: "",
      // routeName: "",
      // supRouteName: "",
      // runDate: moment(),
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
      pageValue: 'c',
      time: '',
      playBool: true,

      visible: true,
      width: 570,
      lineList: 1,
      selectList: [{}, {}, {}],
      showOne: true,
      showTwo: true,

      busList: [],
    };
  },
  created() {
    this.getData();
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
      await this.getMinPlanTime();
      // this.getRouteUpDownInfo();
      // this.getBusConfigure();
      await this.getRuningScheduleConfig();
      // await this.getScheduleCountResult();
      await this.getAdrealInfo();
    },
    // 车子仿真数据
    getAdrealInfo() {
      let send = {
        routeId: this.routeId,
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} ${this.time}`,
        supportRouteId: this.supRouteId,
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
    getRouteUpDownInfo() {
      let send = { routeId: this.routeId, supportRouteId: this.supRouteId };
      let params = this.mes;
      axios.post(this.url.getRouteUpDownInfo, send, params).then((res) => {
        console.log('获取上下行站点和首末班时间', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
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
    getRuningScheduleConfig(item, i) {
      let send = {
        routeId: item.routeId,
        supportRouteId: item.supRouteId,
        // runDate: "2024-01-10",
        runDate: moment(this.runDate).format('YYYY-MM-DD'),
      };
      let params = this.mes;
      axios.post(this.url.getRuningScheduleConfig, send, params).then((res) => {
        console.log('车辆配置信息', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          this.busList[i].centerData = {
            titleMap: {},
            mainMap: {},
            subMap: {},
          };
          // return;
        } else {
          this.busList[i].centerData = res.data.data;
        }

        this.busList[i].sendData = {
          routeId: item.routeId,
          routeName: item.routeName,
          supRouteName: item.supRouteName,
          supRouteId: item.supRouteId,
          runDate: this.runDate,
          centerData: item.centerData,
        };
        if ([0, 1, 2, 5].includes(item.type)) {
          this.busList[i].carBool = 'b';
        } else if ([3, 4, 6, 7].includes(item.type)) {
          this.busList[i].carBool = 'c';
        }
        Vue.set(this.busList, i, this.busList[i]);
        // console.log(this.busList);
      });
    },

    // modal操作方法
    sendNumber(i) {},
    showModal() {
      this.visible = true;
    },
    handleOk() {
      this.visible = false;
      this.busList = this.selectList;

      this.busList.forEach((item, i) => {
        if (item.routeId) {
          this.getRuningScheduleConfig(item, i);
        }
      });
      console.log('选择后的公交列表', this.busList);
    },
    handleCancel() {
      this.visible = false;
    },
    addLine() {
      if (this.selectList.length >= 3) {
        return;
      }
      this.selectList.push({});
    },
    deleteIt(i) {
      console.log('iiiii', i);
      console.log(this.selectList);
      this.selectList.splice(i, 1);
      console.log(this.selectList);
    },
    reciveMsg(msg) {
      // console.log(msg);
      this.selectList[msg.number] = msg;
      console.log('selectList', this.selectList);
    },
    showMsgOne(msg) {
      this.showOne = msg;
    },
    showMsgTwo(msg) {
      this.showTwo = msg;
    },
  },
};
</script>

<style lang="less" scoped>
.ant-card {
  //   display: flex;
  min-height: 100%;
  background: #dbefff;

  /deep/.ant-card-body {
    // min-height: 100% !important;
    // height: 100% !important;
    flex: 1;
    padding: 0;
  }
}
#main {
  min-width: 1366px;
  height: 100%;
  overflow-x: auto;
  background: #eaf6ff;
}
header {
  height: 66px;
  background: #bfd9f3;
  .ant-form {
    padding: 12px;
  }
  .select-button {
    width: 128px;
    height: 37px;
    background: #1890ff;
    box-shadow: 0px 3px 4px 1px rgba(17, 114, 204, 0.4);
    border-radius: 4px 4px 4px 4px;
    font-size: 18px;
    line-height: 37px;
    text-align: center;
    color: #fff;
    cursor: pointer;
    img {
      display: inline-block;
      width: 24px;
    }
  }
  .selected {
    display: inline-block;
    // width: 217px;
    height: 30px;
    line-height: 30px;
    font-size: 16px;
    background: #dbefff;
    border-radius: 2px 2px 2px 2px;
    border: 1px solid #99bbe8;
    padding: 0 4px;
    margin: 0 6px 0 0;
    span {
      display: inline-block;
      margin: 0 10px;
    }
    i {
      cursor: pointer;
    }
  }
  .right-button {
    button {
      margin: 0 6px 0 0;
      background: transparent;
      border: 1px solid #99bbe8;
    }
  }
}
/deep/.ant-modal-header {
  background: #dbefff !important;
}
/deep/.ant-modal-content {
  background: #dbefff !important;
}
.line-list-con {
  ul {
    li {
      display: flex;
      // justify-content: space-between;
      // width: 509px;
      line-height: 60px;
      height: 60px;
      margin-top: 20px;
      .select-box {
        border: 1px solid #99bbe8;
        width: 100%;
        // width: 510px;
        padding: 0 15px;
        font-size: 16px;
        box-sizing: border-box;
        margin-right: 10px;
        position: relative;
        .mask {
          position: absolute;
          width: 100%;
          height: 100%;
          background: #000;
          opacity: 0.3;
          top: 0;
          left: 0;
          z-index: 999;
        }
        span {
          display: inline-block;
          margin-right: 5px;
        }
        .name {
          display: inline-block;
          margin-right: 5px;
          span {
            color: red;
            margin: 0;
          }
        }
        .sele-list {
          display: inline-block;
        }
      }
      .delete-it {
        font-size: 16px;
        color: #1890ff;
        cursor: pointer;
      }
    }
  }
}
</style>
