<template>
  <a-card :bordered="false">
    <div id="main">
      <a-spin tip="Loading..." :spinning="spinning">
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
              <a-select allowClear v-model="supRouteId" placeholder="请选择主线路" style="width: 200px" @change="seleChange">
                <a-select-option v-for="item in supRouteList" :value="item.supportRouteId">
                  {{ item.supportRouteName }}
                </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="日期">
              <a-date-picker placeholder="请选择日期" moment="YYYY-MM-DD" v-model="runDate" @change="onDateChange" />
            </a-form-item>

            <a-form-item label="排班">
              <a-select placeholder="请选择排班方案" style="width: 160px" v-model="planType">
                <a-select-option :value="1">最优排班</a-select-option>
                <a-select-option :value="2">预设排班</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item>
              <a-button type="primary" @click="searchIt" icon="search">查询</a-button>
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
        <section>
          <div class="con-head">
            <div class="con-info">
              <div style="display: inline-block">
                排班计划（当前为 <span>{{ this.planType == 1 ? '最优排班' : '预设排班' }}</span
                >，最后一次生成计划时间为
                {{ centerData.titleMap.lastPlanTime }}
                ）
              </div>
              <div style="display: inline-block">主线班次:{{ centerData.titleMap.totalClasses }} /被支援班次{{ centerData.titleMap.totalSupportClasses }}</div>
              <div style="display: inline-block">关联线班次:{{ centerData.titleMap.subTotalClasses }}/被支援班次{{ centerData.titleMap.subTotalSupportClasses }}</div>
            </div>

            <div class="btn-area">
              <a-button @click="createIt">生成排班</a-button>
              <a-button @click="changeCar">挂车</a-button>
              <a-button>参数设置</a-button>
              <a-button>同步计划</a-button>
              <a-button>导出横竖表</a-button>
            </div>
          </div>
          <div class="con-table" v-if="tableBool">
            <div class="tit-num">
              <div class="tit-left">循次</div>
              <div class="tit-right">
                <ul>
                  <li>趟次数</li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    <!-- {{ i % 2 == 0 ? 1 : 2 }} -->
                    {{ item.busClasses }}
                  </li>
                </ul>
                <ul class="tit-station">
                  <li>始发站点</li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    {{ item.routeStationName }}
                  </li>
                  <!-- <li v-for="(item, i) in 30">始发站点</li> -->
                </ul>
                <ul>
                  <li>
                    <div>原班次/{{ centerData.titleMap.totalClasses }}</div>
                    <div>支援班次/{{ centerData.titleMap.totalSupportClasses }}</div>
                  </li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    <div>原班次</div>
                    <div>支援班次</div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="tit-order">
              <div class="tit-left">车序</div>
              <div class="tit-right">
                <ul>
                  <li>全天时段车次</li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    <div>{{ item.classes }}</div>
                    <div>{{ item.supportClasses }}</div>
                  </li>
                </ul>
                <ul>
                  <li>营运起止时段</li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">{{ item.beginTime }}-{{ item.endTime }}</li>
                </ul>
                <ul>
                  <li>上/下行平均间隔</li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    <div>{{ item.upInterval }}</div>
                    <div>{{ item.downInterval }}</div>
                  </li>
                </ul>
                <ul>
                  <li>
                    <div>停站率</div>
                    <div>满载率</div>
                  </li>

                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    <div>{{ item.parkPercent }}</div>
                    <div>{{ item.fullPercent }}</div>
                  </li>
                </ul>
                <ul>
                  <li>
                    <span class="three">
                      <div>车辆</div>
                      <div>行车时间</div>
                      <div>营运时间</div>
                    </span>
                  </li>
                  <li v-for="(item, i) in tableData.firstRouteStaList">
                    <div></div>
                    <div></div>
                    <div></div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="sec-con">
              <ul v-for="(item, i) in tableData.scheduleBusList">
                <li :class="item.firstDirection == '0' ? 'blue' : 'green'">
                  {{ item.firstBusNumber }}
                </li>
                <li>
                  <span class="three">
                    <div :title="item.busNameFull">{{ item.busNameFull }}</div>
                    <div :title="item.totalTripTime">
                      {{ item.totalTripTime }}
                    </div>
                    <div :title="item.totalRunTime">{{ item.totalRunTime }}</div>
                  </span>
                </li>
                <li v-for="ktem in item.scheduleList">
                  <template v-if="ktem.tripBeginTime">
                    {{ moment(ktem.tripBeginTime).format('HH:mm') }}~
                    {{ moment(ktem.tripEndTime).format('HH:mm') }}
                  </template>
                  <template v-else></template>
                </li>
              </ul>
            </div>
          </div>
        </section>
        <scheduling-create-modal ref="SchedulingCreateModal"></scheduling-create-modal>
        <scheduling-change-car-modal ref="SchedulingChangeCarModal"></scheduling-change-car-modal>
      </a-spin>
    </div>
  </a-card>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import '@/assets/less/base.css';
import moment from 'moment';
import SchedulingCreateModal from './modules/SchedulingCreateModal.vue';
import SchedulingChangeCarModal from './modules/SchedulingChangeCarModal.vue';
export default {
  name: 'SchedulingSearch',
  components: {
    SchedulingCreateModal,
    SchedulingChangeCarModal,
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
      },
      spinning: false,
      pageValue: 'a',
      mes: '',
      //   routeId: "194",
      //   supRouteId: "420",
      //   routeName: "36路",
      //   supRouteName: "76A路",
      //   runDate: "2023-12-18",
      routeId: '',
      supRouteId: '',
      routeName: '',
      supRouteName: '',
      runDate: moment(),
      allRouteList: [],
      routeList: [],
      supRouteList: [],
      tableData: { firstRouteStaList: [], scheduleBusList: [] },
      planType: 1,
      tableBool: false,
      centerData: { mainMap: {}, subMap: {}, titleMap: {} },
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
      //   this.getMinPlanTime();
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
    },
    searchIt() {
      if (!this.routeId || !this.supRouteId || !this.runDate) {
        this.$message.error('请选择相关选项再查询');
        return;
      }
      this.spinning = true;
      const queryString = window.location.search;
      const searchParams = new URLSearchParams(queryString);
      const paramString = searchParams.get('paramString');
      const params = new URLSearchParams();
      params.append('paramString', paramString);
      this.mes = params;
      console.log(params.toString());

      this.getRuningScheduleConfig();
      axios
        .post(
          this.url.getScheduleBySort,
          {
            // routeId: "194",
            // supportRouteId: "420",
            // runDate: "2024-01-17 00:00:00",
            // planType: 2,
            routeId: this.routeId,
            supportRouteId: this.supRouteId,
            runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
            planType: this.planType,
          },
          { params }
        )
        .then((res) => {
          console.log(res);
          this.spinning = false;
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          if (res.data.data.scheduleBusList.length > 0) {
            this.tableBool = true;
          } else {
            this.tableBool = false;
          }
          this.tableData = res.data.data;
          //   this.allRouteList = res.data.data;
        });
    },
    createIt() {
      if (!this.routeId || !this.supRouteId || !this.runDate) {
        this.$message.error('请先查询排班后再点击生成按钮');
        return;
      }
      this.$refs.SchedulingCreateModal.edit({
        runDate: this.runDate,
        routeId: this.routeId,
        supRouteId: this.supRouteId,
        routeName: this.routeName,
        supRouteName: this.supRouteName,
      });
    },
    changeCar() {
      console.log(123);
      this.$refs.SchedulingChangeCarModal.edit({
        runDate: this.runDate,
        routeId: this.routeId,
        supRouteId: this.supRouteId,
        routeName: this.routeName,
        supRouteName: this.supRouteName,
      });
    },
    // 获取排班时间等数据
    getRuningScheduleConfig() {
      let send = {
        routeId: this.routeId,
        supportRouteId: this.supRouteId,
        // runDate: "2024-01-10",
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
      };
      let params = this.mes;
      axios.post(this.url.getRuningScheduleConfig, send, params).then((res) => {
        console.log('车辆配置信息', res.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.centerData = res.data.data;
        console.log(this.centerData);
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
section {
  background: #dbefff;
  .con-head {
    // margin: 12px 0 0 0;
    display: flex;
    justify-content: space-between;
    padding: 12px;
    .head-info {
      display: inline-block;
      font-size: 14px;
      margin-right: 12px;
      line-height: 32px;
    }
    div {
      display: inline-block;
      font-size: 14px;
      font-weight: 600;
      margin-right: 10px;
    }
    div:nth-child(1) {
      font-weight: 500;
      span {
        display: inline-block;
        font-size: 16px;
        font-weight: 500;
      }
    }

    .ant-btn {
      margin-right: 10px;
    }
  }
  .con-table {
    box-sizing: border-box;
    max-width: 100%;
    overflow-x: scroll;
    box-sizing: border-box;
    border: 1px solid #dadada;
    background: #e8f3fd;
    .tit-num,
    .tit-order {
      display: flex;
      flex-wrap: nowrap;
      white-space: nowrap;
      div {
        display: inline-block;
      }
      .tit-left {
        flex: 0 0 auto;
        width: 30px;
        height: 138px;
        writing-mode: vertical-rl; /* 垂直排列，从右到左 */
        text-orientation: upright; /* 文字方向正常，从上到下 */
        white-space: nowrap; /* 防止文字换行 */
        text-align: center;
        border-right: 1px solid #dadada;
        border-bottom: 1px solid #dadada;
        box-sizing: border-box;
        padding-right: 5px;
      }
      .tit-right {
        ul {
          border-bottom: 1px solid #dadada;
          li {
            display: inline-block;
            border-right: 1px solid #dadada;
            // border-bottom: 1px solid #dadada;
            width: 170px;
            height: 38px;
            line-height: 38px;
            text-align: center;
            font-size: 14px;
            vertical-align: top;
            div {
              width: 50%;
              height: 100%;
              vertical-align: top;
              border-right: 1px solid #dadada;
            }
            div:nth-last-child(1) {
              border: none;
            }
            .three {
              display: flex;
              div {
                flex: 1;
              }
            }
          }
          li:nth-child(2n-1) {
            background: #b6ede8;
          }
          li:nth-child(2n) {
            background: #c5e3ff;
          }
          li:nth-child(1) {
            width: 210px;
            font-size: 16px;
            background: #e8f3fd;
          }
        }
        .tit-station {
          height: 60px;
          line-height: 60px;
          li {
            height: 60px;
            vertical-align: top;
            line-height: 60px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            border-bottom: 1px solid #dadada;
          }
        }
      }
    }
    .tit-order {
      .tit-left {
        height: 195px;
      }
    }
    .sec-con {
      div {
        display: inline-block;
      }
      ul {
        display: flex;
        flex-wrap: nowrap;
        white-space: nowrap;

        li {
          display: inline-block;
          border-right: 1px solid #dadada;
          border-bottom: 1px solid #dadada;
          width: 170px;
          height: 38px;
          line-height: 38px;
          text-align: center;
          font-size: 14px;
          vertical-align: top;
          flex-grow: 0;
          flex-shrink: 0;
          div {
            width: 50%;
            height: 100%;
            vertical-align: top;
            border-right: 1px solid #dadada;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }
          div:nth-last-child(1) {
            border: none;
          }
          .three {
            display: flex;
            div {
              flex: 1;
            }
          }
        }
        li:nth-child(2n) {
          background: #b6ede8;
        }
        li:nth-child(2n-1) {
          background: #c5e3ff;
        }
        li:nth-child(1) {
          width: 30px;
        }
        li:nth-child(2) {
          width: 210px;
          background: #e8f3fd;
        }
        .green {
          background: #b6ede8 !important;
        }
        .blue {
          background: #c5e3ff !important;
        }
      }
    }
  }
}
</style>
