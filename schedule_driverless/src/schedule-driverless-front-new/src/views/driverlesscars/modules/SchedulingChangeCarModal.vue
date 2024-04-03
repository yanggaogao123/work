<template>
  <a-modal title="挂车" :visible="visible" :width="width" switchFullscreen @ok="handleOk" @cancel="handleCancel" cancelText="关闭" :confirmLoading="clickFlag" :destroyOnClose="true">
    <a-spin tip="Loading..." :spinning="spinning">
      <!-- <div class="tit">
        <span @click="exDay()"><a-icon type="left-circle" />前一天</span>

        <span @click="nextDay()">后一天<a-icon type="right-circle" /></span>
      </div> -->
      <div class="content">
        <div class="part-left">
          <div class="tit">
            <span class="tit-name">{{ this.routeName }}</span>
            <span class="tit-date">{{ this.runDateStr }}</span>
          </div>
          <div class="box-table">
            <ul class="table-tit">
              <li>车号</li>
              <li>昨天车名</li>
              <li>中途出场任务</li>
              <li>中途出场时间</li>
              <li>首轮排班任务</li>
              <li>首轮排班时间</li>
              <li>首轮发班任务</li>
              <li>首轮发班时间</li>
              <li>车辆名称</li>
            </ul>
            <div class="table-con">
              <ul
                v-for="(item, i) in tableData"
                :class="{
                  tableUp: item.startDirection == 0,
                  tableDown: item.startDirection == 1,
                  'line-red': i < tableData.length - 1 && tableData[i + 1].startDirection == 1 && item.startDirection == 0,
                }"
              >
                <li>{{ item.startDirection == 0 ? '上行' : '下行' }}{{ item.startOrderNumber }}号车</li>
                <li>{{ item.oBusName }}</li>
                <li @click="showTaskModal(item, i, 0)">{{ item.dyMidwayShortStation ? item.dyMidwayShortStation.midWayTaskName : '' }}</li>
                <li>
                  <a-time-picker
                    :value="item.dyMidwayShortStation ? (item.dyMidwayShortStation.midWayPlanTime ? moment(item.dyMidwayShortStation.midWayPlanTime) : undefined) : undefined"
                    :data-index="i"
                    format="HH:mm"
                    @change="onDateChange(i, $event)"
                    style="width: 110px"
                  />
                </li>
                <li>{{ item.serviceName }}</li>
                <li>{{ moment(item.planTime).format('HH:mm') }}</li>
                <li @click="showTaskModal(item, i, 1)">{{ item.firstRoundTaskName }}</li>
                <li>
                  <a-time-picker
                    :value="item.firstRoundPlanTime ? moment(item.firstRoundPlanTime) : undefined"
                    format="HH:mm"
                    :data-index="i"
                    @change="onDateChange1(i, $event)"
                    style="width: 110px"
                  />
                </li>
                <li>
                  <div class="bus-tag" v-if="item.busName">
                    <div class="list-left">
                      <a-tag closable @close="closeLog(i, $event)">
                        {{ item.busName }}
                      </a-tag>
                    </div>
                    <div class="list-right">
                      <span @click="goUp(i)"><img src="@/assets/driverlesscars/up.png" alt="" /></span>
                      <span @click="goDown(i)"><img src="@/assets/driverlesscars/down.png" alt="" /></span>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div class="part-right">
          <a-select show-search placeholder="请选择车辆" option-filter-prop="children" style="width: 200px" :filter-option="filterOption" @change="handleChange">
            <a-select-option v-for="(item, i) in busList" :value="item.busId"> {{ item.busName }} </a-select-option>
          </a-select>
          <ul class="bus-list">
            <li v-for="(item, i) in useBusList">
              <div class="list-left">{{ item.busName }}</div>
              <div class="list-right">
                <span @click="joinUp(item, i)">+</span>
                <span @click="joinDown(item, i)">+</span>
              </div>
            </li>
          </ul>
        </div>
        <sub-line-modal ref="SubLineModal" @receive="receiveMsg"></sub-line-modal>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import moment from 'moment';
import '@/assets/less/base.css';
import '@/assets/less/TableExpand.less';
import 'moment/locale/zh-cn';
import SubLineModal from './SubLineModal.vue';

moment.locale('zh-cn');

export default {
  name: 'SchedulingChangeCarModal',
  components: {
    SubLineModal,
  },
  data() {
    return {
      url: {
        getRouteList: `${process.env.VUE_APP_BUS_API}/route/getRouteList`,
        getUnionRouteInfo: `${process.env.VUE_APP_BUS_API}/route/getUnionRouteInfo`,
        getJoinTemplateListByRouteId: `${process.env.VUE_APP_BUS_API}/schedule/getJoinTemplateListByRouteId`,
        generateSupportSchedule: `${process.env.VUE_APP_BUS_API}/schedule/generateSupportSchedule`,
        getBusConfig: `${process.env.VUE_APP_BUS_API}/schedule/getBusConfig`,

        mountCarPlan: `${process.env.VUE_APP_BUS_API}/schedule/mountCarPlan`,
        recentRunBus: `${process.env.VUE_APP_BUS_API}/schedule/recentRunBus`,
        dispatchTask: `${process.env.VUE_APP_BUS_API}/schedule/dispatchTask`,
        saveMountCar: `${process.env.VUE_APP_BUS_API}/schedule/saveMountCar`,
      },
      spinning: false,
      visible: false,
      clickFlag: false,
      selectType: 'id',
      isGetOption: true,
      confirmLoading: false,
      width: 1640,

      routeId: '',
      routeName: '',
      supRouteId: '',
      supRouteName: '',
      runDate: '',
      runDateStr: '',
      midWayPlanTime: '',
      planTime: '',

      allRouteList: [],
      routeList: [],

      supRouteList: [],
      columns: [
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

        // {
        //   title: `${this.baseData.routeName}(下行) 时段`,
        //   align: "center",
        //   dataIndex: "time",
        // },
        {
          title: '车号',
          align: 'center',
          // slots: { title: 'customTitle' },
          dataIndex: 'startOrderNumber',
          scopedSlots: { customRender: 'busNum' },
          // width: 70,
        },
        {
          title: '昨天车名',
          align: 'center',
          dataIndex: 'oBusName',
          // width: 70,
        },
        {
          title: '中途出场任务',
          align: 'center',
          dataIndex: 'dyMidwayShortStation.midWayTaskName',
          scopedSlots: { customRender: 'midWayTaskName' },
          // width: 70,
        },
        {
          title: '中途出场时间',
          align: 'center',
          dataIndex: 'dyMidwayShortStation.midWayPlanTime',
          // width: 70,
          scopedSlots: { customRender: 'midWayPlanTime' },
        },
        {
          title: '首轮排班任务',
          align: 'center',
          dataIndex: 'serviceName',
          // width: 70,
        },
        {
          title: '首轮排班时间',
          align: 'center',
          dataIndex: 'planTime',
          // width: 70,
        },
        {
          title: '首轮发班任务',
          align: 'center',
          dataIndex: 'firstRoundTaskName',
          scopedSlots: { customRender: 'firstRoundTaskName' },
          // width: "40%",
        },
        {
          title: '首轮发班时间',
          align: 'center',
          dataIndex: 'firstRoundPlanTime',
          scopedSlots: { customRender: 'firstRoundPlanTime' },
          // width: "40%",
        },
        {
          title: '车辆名称',
          align: 'center',
          dataIndex: 'busName',
          scopedSlots: { customRender: 'busName' },
          // width: "40%",
        },
      ],
      tableData: [],
      busList: [],
      useBusList: [],
    };
  },
  filters: {
    ensureZero: function (value) {
      return value || value == 0 ? value : 0;
    },
  },
  created() {
    this.getData();
    // this.testDate();
  },
  methods: {
    moment,
    async edit(record) {
      let params = this.mes;
      console.log(record);
      this.visible = true;
      this.runDate = record.runDate;
      this.runDateStr = moment(record.runDate).format('YYYY-MM-DD dddd');
      this.routeName = record.routeName;
      this.routeList = [];
      this.routeList = this.allRouteList.filter((route) => route.routeName.includes(this.routeName));
      this.routeId = Number(record.routeId);

      await axios.get(`${this.url.mountCarPlan}/${this.routeId}/2/${moment(this.runDate).subtract(1, 'days').format('YYYY-MM-DD')}/${moment(this.runDate).format('YYYY-MM-DD')}`).then((res) => {
        console.log('2.12.查询挂车计划数据接口', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.tableData = res.data.data;
        this.tableData.forEach((item) => {
          item.routeId = this.routeId;
          item.runDate = this.runDate;
          item.planDate = this.runDate;
        });

        console.log('tableData', this.tableData);
      });

      await axios.get(`${this.url.recentRunBus}/${this.routeId}/${moment(this.runDate).subtract(7, 'days').format('YYYY-MM-DD')}`).then((res) => {
        console.log('2.13.挂车-获取线路的车辆数据接口', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.busList = res.data.data;

        this.useBusList = JSON.parse(JSON.stringify(res.data.data));

        for (let i = 0; i < this.useBusList.length; i++) {
          // 在数组A中查找是否存在与当前数组B中对象相同id的对象
          let foundIndex = this.tableData.findIndex((itemA) => itemA.busId === this.useBusList[i].busId);

          // 如果在数组A中找到了相同id的对象，则从数组B中删除该对象
          if (foundIndex !== -1) {
            this.useBusList.splice(i, 1);
            i--; // 删除后数组长度减1，因此需要将索引减1
          }
        }
      });
    },
    handleOk() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }

      axios.post(this.url.saveMountCar, this.tableData).then((res) => {
        console.log('2.16.挂车-保存挂车数据接口', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.$message.success('挂车成功');
        this.visible = false;
      });

      // this.visible = false;
    },
    handleCancel() {
      this.visible = false;
    },
    getData() {
      const queryString = window.location.search;
      const searchParams = new URLSearchParams(queryString);
      const paramString = searchParams.get('paramString');
      const params = new URLSearchParams();
      params.append('paramString', paramString);
      this.mes = params;
      console.log(params.toString());
      //查询主线路list
      axios.get(`${this.url.getRouteList}?${params}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.allRouteList = res.data.data;
      });
      // 查询生成数据
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
      // let params = this.mes;
      console.log(value, option);
      this.useBusList = this.useBusList.sort((a, b) => {
        if (a.busId === value) {
          return -1; // 如果a匹配到了目标busId，则将a排到前面
        } else if (b.busId === value) {
          return 1; // 如果b匹配到了目标busId，则将b排到前面
        } else {
          return 0; // 如果都不匹配目标busId，则保持原来的顺序
        }
      });

      console.log(this.useBusList);
    },
    seleChange(value) {
      let params = this.mes;
      // console.log(value);
      // console.log(this.supRouteList);
      let arr = this.supRouteList.filter((route) => route.supportRouteId == value);
      //   this.supRouteId = value;
      this.supRouteName = arr[0].supportRouteName;
      axios.get(`${this.url.getJoinTemplateListByRouteId}/${value}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.supTemList = res.data.data;
      });
    },
    onDateChange(index, event) {
      console.log(index, event);
      if (event) {
        let time = `${this.runDate} ${moment(event._d).format('HH:mm:ss')}`;
        console.log('time', time);
        if (this.tableData[index].dyMidwayShortStation) {
          Vue.set(this.tableData[index].dyMidwayShortStation, 'midWayPlanTime', time);
        } else {
          this.tableData[index].dyMidwayShortStation = {
            runDate: `${this.runDate} 00:00:00`,
            routeId: this.routeId,
            busId: this.tableData[index].busId,
            busName: this.tableData[index].busName,
            // type: '0',
            midWayTaskName: this.tableData[index].dyMidwayShortStation ? this.tableData[index].dyMidwayShortStation.midWayTaskName : '',
            taskId: this.tableData[index].dyMidwayShortStation ? this.tableData[index].dyMidwayShortStation.taskId : '',
            midWayPlanTime: '',
            startOrderNumber: this.tableData[index].startOrderNumber,
            startDirection: this.tableData[index].startDirection,
          };
          Vue.set(this.tableData[index].dyMidwayShortStation, 'midWayPlanTime', time);
        }
      } else {
        Vue.set(this.tableData[index].dyMidwayShortStation, 'midWayPlanTime', undefined);
      }

      console.log(this.tableData);
    },
    onDateChange1(index, event) {
      if (event) {
        let time = `${this.runDate} ${moment(event._d).format('HH:mm')}`;
        console.log('time', time);
        Vue.set(this.tableData[index], 'firstRoundPlanTime', time);
      } else {
        Vue.set(this.tableData[index], 'firstRoundPlanTime', undefined);
      }
    },
    showTaskModal(item, index, taskType) {
      console.log('123123123');
      this.$refs.SubLineModal.edit({
        runDate: this.runDate,
        routeId: this.routeId,
        supRouteId: this.supRouteId,
        routeName: this.routeName,
        supRouteName: this.supRouteName,
        startDirection: item.startDirection,
        taskType: taskType,
        index: index,
      });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    receiveMsg(msg) {
      console.log('接受到的信息', msg);
      let index = msg.index;
      if (msg.taskType == 0) {
        if (this.tableData[index].dyMidwayShortStation) {
          Vue.set(this.tableData[index].dyMidwayShortStation, 'midWayTaskName', msg.serviceName);
          Vue.set(this.tableData[index].dyMidwayShortStation, 'taskId', msg.routeSubId);
        } else {
          this.tableData[index].dyMidwayShortStation = {
            runDate: `${this.runDate} 00:00:00`,
            routeId: this.routeId,
            busId: this.tableData[index].busId,
            busName: this.tableData[index].busName,
            // type: '0',
            midWayTaskName: this.tableData[index].dyMidwayShortStation ? this.tableData[index].dyMidwayShortStation.midWayTaskName : '',
            taskId: this.tableData[index].dyMidwayShortStation ? this.tableData[index].dyMidwayShortStation.taskId : '',
            midWayPlanTime: this.tableData[index].dyMidwayShortStation ? this.tableData[index].dyMidwayShortStation.midWayPlanTime : '',
            startOrderNumber: this.tableData[index].startOrderNumber,
            startDirection: this.tableData[index].startDirection,
          };
          Vue.set(this.tableData[index].dyMidwayShortStation, 'midWayTaskName', msg.serviceName);
          Vue.set(this.tableData[index].dyMidwayShortStation, 'taskId', msg.routeSubId);
        }
      } else if (msg.taskType == 1) {
        this.tableData[index].firstRoundTaskId = msg.routeSubId;
        this.tableData[index].firstRoundTaskName = msg.serviceName;
      }
      console.log(this.tableData);
    },
    closeLog(i, event) {
      console.log(i, event);
      this.useBusList.push({
        busId: this.tableData[i].busId,
        busName: this.tableData[i].busName,
      });
      this.tableData[i].busId = '';
      this.tableData[i].busName = '';
    },
    goUp(i) {
      if (i - 1 >= 0) {
        let a = this.tableData[i - 1].busId;
        let b = this.tableData[i - 1].busName;
        this.tableData[i - 1].busId = this.tableData[i].busId;
        this.tableData[i - 1].busName = this.tableData[i].busName;
        this.tableData[i].busId = a;
        this.tableData[i].busName = b;
      } else if (i - 1 < 0) {
        let a = this.tableData[this.tableData.length - 1].busId;
        let b = this.tableData[this.tableData.length - 1].busName;
        this.tableData[this.tableData.length - 1].busId = this.tableData[i].busId;
        this.tableData[this.tableData.length - 1].busName = this.tableData[i].busName;
        this.tableData[i].busId = a;
        this.tableData[i].busName = b;
      }
    },
    goDown(i) {
      if (i + 1 < this.tableData.length) {
        let a = this.tableData[i + 1].busId;
        let b = this.tableData[i + 1].busName;
        this.tableData[i + 1].busId = this.tableData[i].busId;
        this.tableData[i + 1].busName = this.tableData[i].busName;
        this.tableData[i].busId = a;
        this.tableData[i].busName = b;
      } else if (i + 1 >= this.tableData.length) {
        let a = this.tableData[0].busId;
        let b = this.tableData[0].busName;
        this.tableData[0].busId = this.tableData[i].busId;
        this.tableData[0].busName = this.tableData[i].busName;
        this.tableData[i].busId = a;
        this.tableData[i].busName = b;
      }
    },
    joinUp(item, i) {
      for (let i = 0; i < this.tableData.length; i++) {
        if (this.tableData[i].startDirection == '0' && !this.tableData[i].busName) {
          this.tableData[i].busName = item.busName;
          this.tableData[i].busId = item.busId;
          break;
        }
      }
      this.useBusList = this.useBusList.filter((ktem) => ktem.busId !== item.busId);
    },
    joinDown(item, index) {
      for (let i = 0; i < this.tableData.length; i++) {
        if (this.tableData[i].startDirection == '1' && !this.tableData[i].busName) {
          this.tableData[i].busName = item.busName;
          this.tableData[i].busId = item.busId;
          break;
        }
      }
      this.useBusList = this.useBusList.filter((ktem) => ktem.busId !== item.busId);
    },
  },
};
</script>

<style lang="less" scoped>
.content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  .part-left {
    width: 80%;
    .tit {
      font-weight: 400;
      font-size: 18px;
      color: #262626;
      margin-bottom: 20px;
      span {
        display: inline-block;
        margin-right: 20px;
      }
    }
    .box-table {
      width: 100%;
      border: 1px solid #99bbe8;
      box-sizing: border-box;
      .table-tit {
        width: 100%;
        height: 42px;
        display: flex;
        background: #eff6fc;
        li {
          flex: 1;
          text-align: center;
          height: 42px;
          line-height: 42px;
          font-size: 16px;
          font-weight: 600;
          border-right: 1px solid #99bbe8;
          border-bottom: 1px solid #99bbe8;
          &:nth-last-child(1) {
            border-right: none;
          }
        }
      }
      .table-con {
        width: 100%;
        ul {
          display: flex;
          height: 42px;
          &:nth-child(2n-1) {
            background: #ffffff;
          }
          &:nth-child(2n) {
            background: #eff6fc;
          }
          li {
            flex: 1;
            text-align: center;
            height: 42px;
            line-height: 42px;
            font-size: 14px;
            border-right: 1px solid #99bbe8;
            border-bottom: 1px solid #99bbe8;
            &:nth-last-child(1) {
              border-right: none;
            }
            .bus-tag {
              box-sizing: border-box;
              width: 120px;
              height: 34px;
              display: flex;
              border: 1px solid #1890ff;
              border-radius: 5px;
              // margin-top: 5px;
              margin: 3px auto 0 auto;
              .list-left {
                width: calc(100% - 26px);
                text-align: center;
                line-height: 34px;
                font-size: 16px;
                font-weight: 600;
              }
              .list-right {
                width: 26px;
                height: 34px;
                box-sizing: border-box;
                border-left: 1px solid #1890ff;
                span {
                  cursor: pointer;
                  display: block;
                  height: 17px;
                  text-align: center;
                  box-sizing: border-box;
                  font-size: 16px;
                  line-height: 12px;
                  color: #1890ff;
                  font-weight: 600;
                  img {
                    width: 10px;
                    display: block;
                    margin: 1px auto;
                  }
                  &:nth-child(1) {
                    border-bottom: 1px solid #1890ff;
                  }
                }
              }
            }
          }
        }
        .line-red {
          li {
            border-bottom: 1px solid red;
          }
        }
        .tableDown {
        }
      }
    }
  }
  .part-right {
    width: 20%;
    padding: 0 20px;

    .bus-list {
      margin-left: 40px;
      li {
        box-sizing: border-box;
        width: 120px;
        height: 34px;
        display: flex;
        border: 1px solid #1890ff;
        border-radius: 5px;
        margin-top: 5px;
        .list-left {
          width: calc(100% - 26px);
          text-align: center;
          line-height: 34px;
          font-size: 16px;
          font-weight: 600;
        }
        .list-right {
          width: 26px;
          height: 34px;
          box-sizing: border-box;
          border-left: 1px solid #1890ff;
          span {
            cursor: pointer;
            display: block;
            height: 17px;
            text-align: center;
            box-sizing: border-box;
            font-size: 16px;
            line-height: 12px;
            color: #1890ff;
            font-weight: 600;
            &:nth-child(1) {
              border-bottom: 1px solid #1890ff;
            }
          }
        }
      }
    }
  }
}
</style>
