<template>
  <!-- 仿真图 -->
  <a-card :bordered="false">
    <div id="main">
      <header>
        <a-form layout="inline">
          <a-form-item label="主线路">
            <a-select
              show-search
              allowClear
              :value="routeId"
              placeholder="请选择主线路"
              :default-active-first-option="false"
              :show-arrow="false"
              :filter-option="false"
              :not-found-content="null"
              style="width: 200px"
              @search="handleSearch"
              @change="handleChange"
            >
              <a-select-option v-for="item in routeList" :value="item.routeId">
                {{ item.routeName }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="关联线路">
            <a-select
              allowClear
              v-model="supRouteId"
              placeholder="请选择主线路"
              style="width: 200px"
              @change="seleChange"
            >
              <a-select-option
                v-for="item in supRouteList"
                :value="item.supportRouteId"
              >
                {{ item.supportRouteName }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="日期">
            <a-date-picker
              placeholder="请选择日期"
              moment="YYYY-MM-DD"
              v-model="runDate"
              @change="onDateChange"
            />
          </a-form-item>

          <a-form-item label="排班">
            <a-select
              placeholder="请选择排班方案"
              style="width: 160px"
              v-model="planType"
            >
              <a-select-option :value="1">最优排班</a-select-option>
              <a-select-option :value="2">预设排班</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="searchIt" icon="search"
              >查询</a-button
            >
          </a-form-item>

          <a-form-item style="float: right">
            <a-radio-group v-model="pageValue" @change="onChange">
              <a-radio-button value="a">计划表</a-radio-button>
              <a-radio-button value="b">车位图</a-radio-button>
              <a-radio-button value="c">排班仿真</a-radio-button>
            </a-radio-group>
          </a-form-item>
        </a-form>
      </header>

      <car-type-one-modal
        v-show="carBool == 'b'"
        :sendData="sendData"
      ></car-type-one-modal>
      <car-type-four-modal
        v-show="carBool == 'c'"
        :sendData="sendData"
      ></car-type-four-modal>
      <chart-table-modal
        v-if="chartBool"
        :chartData="chartData"
        :baseData="baseData"
      ></chart-table-modal>
    </div>
  </a-card>
</template>

<script>
import Vue from "vue";
import axios from "axios";
import "@/assets/less/base.css";
import moment from "moment";
import CarTypeFourModal from "./modules/CarTypeFourModal.vue";
import CarTypeOneModal from "./modules/CarTypeOneModal.vue";
import ChartTableModal from "./modules/ChartTableModal.vue";

export default {
  name: "SchedulingSimulation",
  components: {
    CarTypeOneModal,
    CarTypeFourModal,
    ChartTableModal,
  },
  data() {
    return {
      url: {
        adrealInfo: `${process.env.VUE_APP_BUS_API}/simulation/adrealInfo`,
        getRouteList: `${process.env.VUE_APP_BUS_API}/route/getRouteList`,
        getUnionRouteInfo: `${process.env.VUE_APP_BUS_API}/route/getUnionRouteInfo`,
        getMinPlanTime: `${process.env.VUE_APP_BUS_API}/simulation/getMinPlanTime`,
        getRouteUpDownInfo: `${process.env.VUE_APP_BUS_API}/route/getRouteUpDownInfo`,
        busConfigure: `${process.env.VUE_APP_BUS_API}/schedule/busConfigure`,
        getScheduleCountResult: `${process.env.VUE_APP_BUS_API}/scheduleCount/getScheduleCountResult`,
        getRuningScheduleConfig: `${process.env.VUE_APP_BUS_API}/schedule/getRuningScheduleConfig`,
      },
      paramString: "",
      id: "",
      mes: "",
      allRouteList: [],
      routeList: [],
      supRouteList: [],
      // routeId: "194",
      // supRouteId: "420",
      // routeName: "36路",
      // supRouteName: "76A路",
      // runDate: "2023-12-18",
      routeId: "",
      supRouteId: "",
      routeName: "",
      supRouteName: "",
      runDate: moment().add("1", "days"),
      sendData: {},
      centerData: "",
      chartData: "",
      baseData: {},
      chartBool: false,
      carBool: "a",

      planType: 2,
      pageValue: "c",
      time: "",
      playBool: true,
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
      const paramString = searchParams.get("paramString");
      const params = new URLSearchParams();
      params.append("paramString", paramString);
      this.mes = params;
      console.log(params.toString());
      axios
        .get(`${this.url.getRouteList}?${params}`, {}, { params })
        .then((res) => {
          console.log(res);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          this.allRouteList = res.data.data;
        });
    },
    handleSearch(value) {
      console.log(value);
      if (value == "") {
        this.routeList = [];
        return;
      }
      this.routeList = [];
      this.routeList = this.allRouteList.filter((route) =>
        route.routeName.includes(value)
      );
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
      axios
        .post(this.url.getUnionRouteInfo, { routeId: value }, { params })
        .then((res) => {
          console.log("关联线路信息", res);
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
      let arr = this.supRouteList.filter(
        (route) => route.supportRouteId == value
      );
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
        this.$message.error("请选择相关选项再查询");
        return;
      }
      // await this.getMinPlanTime();
      // this.getRouteUpDownInfo();
      // this.getBusConfigure();
      await this.getRuningScheduleConfig();
      await this.getScheduleCountResult();
      await this.getAdrealInfo();
    },
    // 车子仿真数据
    getAdrealInfo() {
      let send = {
        routeId: this.routeId,
        runDate: `${this.runDate} ${this.time}`,
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
        if ([0, 1, 2].includes(res.data.data.simulationType)) {
          this.carBool = "b";
        } else if ([3, 4, 4].includes(res.data.data.simulationType)) {
          this.carBool = "c";
        }
        console.log("carBool", this.carBool);
      });
    },
    // 最早发班时间
    getMinPlanTime() {
      let send = {
        routeId: this.routeId,
        planDate: moment(this.runDate).format("YYYY-MM-DD"),
      };
      let params = this.mes;
      axios.post(this.url.getMinPlanTime, send, params).then((res) => {
        console.log("最早发班时间", res.data);
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
        console.log("获取上下行站点和首末班时间", res.data);
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
        console.log("查询线路配车", res.data);
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
        runDate: `${this.runDate} 00:00:00`,
        planType: 2,
      };
      let params = this.mes;
      axios.post(this.url.getScheduleCountResult, send, params).then((res) => {
        console.log("统计报表", res.data);
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
        runDate: this.runDate,
      };
      let params = this.mes;
      axios.post(this.url.getRuningScheduleConfig, send, params).then((res) => {
        console.log("车辆配置信息", res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.centerData = res.data.data;
      });
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
}
</style>
