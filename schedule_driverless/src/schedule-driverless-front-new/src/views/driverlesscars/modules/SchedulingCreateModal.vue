<template>
  <a-modal
    title="主线-关联线混编配车计划配置"
    :visible="visible"
    :width="width"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :confirmLoading="clickFlag"
    :footer="false"
    :destroyOnClose="true"
  >
    <a-spin tip="Loading..." :spinning="spinning">
      <div class="tit">
        <span @click="exDay()"><a-icon type="left-circle" />前一天</span>
        <span class="tit-date">{{ this.runDateStr }}</span>
        <span @click="nextDay()">后一天<a-icon type="right-circle" /></span>
      </div>
      <a-form layout="inline" class="form1">
        <a-form-item label="主线路">
          <a-select
            show-search
            allowClear
            v-model="routeId"
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
        <a-form-item label="模版选择">
          <a-select allowClear v-model="templateId" placeholder="请选择模版" style="width: 290px" @change="seleChange">
            <a-select-option v-for="item in temList" :value="item.templateId">
              {{ item.templateNameFull }}
            </a-select-option>
            <!-- <a-select-option value="2"> 周末 </a-select-option> -->
          </a-select>
        </a-form-item>
        <a-form-item label="客流参考日期">
          <a-date-picker placeholder="请选择日期" moment="YYYY-MM-DD" @change="onDateChange1" style="width: 210px" />
        </a-form-item>
        <a-form-item label="周转参考日期">
          <a-date-picker placeholder="请选择日期" moment="YYYY-MM-DD" @change="onDateChange2" style="width: 210px" />
        </a-form-item>
        <div class="line"></div>
      </a-form>
      <a-form layout="inline">
        <a-form-item label="关联线路">
          <a-select allowClear v-model="supRouteId" placeholder="请选择主线路" style="width: 200px" @change="seleChange">
            <a-select-option v-for="item in supRouteList" :value="item.supportRouteId">
              {{ item.supportRouteName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="模版选择">
          <a-select allowClear v-model="supTemplateId" placeholder="请选择模版" style="width: 276px" @change="seleChange">
            <a-select-option v-for="item in supTemList" :value="item.templateId">
              {{ item.templateNameFull }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="客流参考日期">
          <a-date-picker placeholder="请选择日期" moment="YYYY-MM-DD" @change="onDateChange3" style="width: 210px" />
        </a-form-item>
        <a-form-item label="周转参考日期">
          <a-date-picker placeholder="请选择日期" moment="YYYY-MM-DD" @change="onDateChange4" style="width: 210px" />
        </a-form-item>
      </a-form>
      <div class="set-best">
        <div class="best-tit">
          <span>最优配车</span>
          <span @click="createIt()">最优混编生成</span>
        </div>
        <div class="best-con">
          <div class="con-li">
            <span class="li-left">主线:</span>
            <div class="li-box" style="margin-right: 30px">
              <span class="logo">上行</span>
              <div class="box-area">
                <div class="box-name">
                  总配车<span>{{ createData.mainUpBusNum }}</span>
                </div>
                <div class="box-name">
                  单班<span>{{ createData.mainSingleBusUp }}</span>
                </div>
              </div>
            </div>
            <div class="li-box">
              <span class="logo">下行</span>
              <div class="box-area">
                <div class="box-name">
                  总配车<span>{{ createData.mainDownBusNum }}</span>
                </div>
                <div class="box-name">
                  单班<span>{{ createData.mainSingleBusDown }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="con-li">
            <span class="li-left" style="margin-right: 16px">关联线:</span>
            <div class="li-box" style="margin-right: 30px">
              <span class="logo">上行</span>
              <div class="box-area">
                <div class="box-name">
                  总配车<span>{{ createData.subUpBusNum }}</span>
                </div>
                <div class="box-name">
                  单班<span>{{ createData.subSingleBusUp }}</span>
                </div>
              </div>
            </div>
            <div class="li-box">
              <span class="logo">下行</span>
              <div class="box-area">
                <div class="box-name">
                  总配车<span>{{ createData.subDownBusNum }}</span>
                </div>
                <div class="box-name">
                  单班<span>{{ createData.subSingleBusDown }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="set-want" v-if="setBool">
        <div class="best-tit">
          <span>预设配车设置</span>
          <span @click="setCreateIt()">预设混编生成</span>
        </div>
        <div class="best-con">
          <div class="con-li">
            <span class="li-left">主线:</span>
            <div class="li-box" style="margin-right: 30px">
              <span class="logo">上行</span>
              <div class="box-area">
                <div class="box-name">总配车<a-input-number :min="0" :max="100000" v-model="presetsInfo.mainUpBusNum" style="width: 70px" /></div>
                <div class="box-name">单班<a-input-number :min="0" :max="100000" v-model="presetsInfo.mainSingleBusUp" style="width: 70px" /></div>
                <div class="box-name" v-show="mainShifts.includes(0)"><span>早半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainEarlyHalfBusUp" style="width: 70px" /></div>

                <div class="box-name" v-show="mainShifts.includes(1)"><span>晚半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainLateHalfBusUp" style="width: 70px" /></div>
                <div class="box-name" v-show="mainShifts.includes(2)"><span>中班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainMiddleBusUp" style="width: 70px" /></div>
                <div class="box-name" v-show="mainShifts.includes(3)"><span>双班中停</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainDoubleStopBusUp" style="width: 70px" /></div>
              </div>
            </div>
            <div class="li-box">
              <span class="logo">下行</span>
              <div class="box-area">
                <div class="box-name">总配车<a-input-number :min="0" :max="100000" v-model="presetsInfo.mainDownBusNum" style="width: 70px" /></div>
                <div class="box-name">单班<a-input-number :min="0" :max="100000" v-model="presetsInfo.mainSingleBusDown" style="width: 70px" /></div>
                <div class="box-name" v-show="mainShifts.includes(0)"><span>早半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainEarlyHalfBusDown" style="width: 70px" /></div>

                <div class="box-name" v-show="mainShifts.includes(1)"><span>晚半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainLateHalfBusDown" style="width: 70px" /></div>
                <div class="box-name" v-show="mainShifts.includes(2)"><span>中班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainMiddleBusDown" style="width: 70px" /></div>
                <div class="box-name" v-show="mainShifts.includes(3)">
                  <span>双班中停</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.mainDoubleStopBusDown" style="width: 70px" />
                </div>
              </div>
            </div>
          </div>
          <div class="con-li">
            <span class="li-left" style="margin-right: 16px">关联线:</span>
            <div class="li-box" style="margin-right: 30px">
              <span class="logo">上行</span>
              <div class="box-area">
                <div class="box-name">总配车<a-input-number :min="0" :max="100000" v-model="presetsInfo.subUpBusNum" style="width: 70px" /></div>
                <div class="box-name">单班<a-input-number :min="0" :max="100000" v-model="presetsInfo.subSingleBusUp" style="width: 70px" /></div>
                <div class="box-name" v-show="subShifts.includes(0)"><span>早半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subEarlyHalfBusUp" style="width: 70px" /></div>

                <div class="box-name" v-show="subShifts.includes(1)"><span>晚半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subLateHalfBusUp" style="width: 70px" /></div>
                <div class="box-name" v-show="subShifts.includes(2)"><span>中班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subMiddleBusUp" style="width: 70px" /></div>
                <div class="box-name" v-show="subShifts.includes(3)"><span>双班中停</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subDoubleStopBusUp" style="width: 70px" /></div>
              </div>
            </div>
            <div class="li-box">
              <span class="logo">下行</span>
              <div class="box-area">
                <div class="box-name">总配车<a-input-number :min="0" :max="100000" v-model="presetsInfo.subDownBusNum" style="width: 70px" /></div>
                <div class="box-name">单班<a-input-number :min="0" :max="100000" v-model="presetsInfo.subSingleBusDown" style="width: 70px" /></div>
                <div class="box-name" v-show="subShifts.includes(0)"><span>早半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subEarlyHalfBusDown" style="width: 70px" /></div>

                <div class="box-name" v-show="subShifts.includes(1)"><span>晚半班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subLateHalfBusDown" style="width: 70px" /></div>
                <div class="box-name" v-show="subShifts.includes(2)"><span>中班</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subMiddleBusDown" style="width: 70px" /></div>
                <div class="box-name" v-show="subShifts.includes(3)"><span>双班中停</span><a-input-number :min="0" :max="100000" v-model="presetsInfo.subDoubleStopBusDown" style="width: 70px" /></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </a-modal>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

export default {
  name: 'SchedulingCreateModal',
  data() {
    return {
      url: {
        getRouteList: `${process.env.VUE_APP_BUS_API}/route/getRouteList`,
        getUnionRouteInfo: `${process.env.VUE_APP_BUS_API}/route/getUnionRouteInfo`,
        getJoinTemplateListByRouteId: `${process.env.VUE_APP_BUS_API}/schedule/getJoinTemplateListByRouteId`,
        generateSupportSchedule: `${process.env.VUE_APP_BUS_API}/schedule/generateSupportSchedule`,
        getBusConfig: `${process.env.VUE_APP_BUS_API}/schedule/getBusConfig`,
      },
      spinning: false,
      visible: false,
      clickFlag: false,
      selectType: 'id',
      isGetOption: true,
      confirmLoading: false,
      width: 700,

      routeId: '',
      routeName: '',
      supRouteId: '',
      supRouteName: '',
      runDate: '',
      runDateStr: '',

      allRouteList: [],
      routeList: [],

      supRouteList: [],

      templateId: '',
      supTemplateId: '',
      temList: '',
      supTemList: '',
      //   planType: 2,
      passengerDate: '',
      turnaroundDate: '',
      supportPassengerDate: '',
      supportTurnaroundDate: '',
      createData: {},
      presetsInfo: {},
      mainShifts: [],
      subShifts: [],
      setBool: false,
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

      await axios.post(this.url.getUnionRouteInfo, { routeId: this.routeId }, { params }).then((res) => {
        console.log('关联线路信息', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.supRouteList = res.data.data;
        this.supRouteId = Number(record.supRouteId);
      });

      await axios.get(`${this.url.getJoinTemplateListByRouteId}/${this.routeId}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.temList = res.data.data;
        let day = moment(this.runDate).isoWeekday();
        console.log(day);
        let seleItem = this.temList.filter((item) => {
          return item.applyDayJoin && item.applyDayJoin.includes(day);
        });
        console.log(seleItem);
        this.templateId = seleItem[0].templateId;
      });

      await axios.get(`${this.url.getJoinTemplateListByRouteId}/${this.supRouteId}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.supTemList = res.data.data;
        let day = moment(this.runDate).isoWeekday();
        console.log(day);
        let seleItem = this.supTemList.filter((item) => {
          return item.applyDayJoin && item.applyDayJoin.includes(day);
        });
        console.log(seleItem);
        this.supTemplateId = seleItem[0].templateId;
      });

      //   let data = {
      //     routeId: this.routeId,
      //     supportRouteId: this.supRouteId,
      //     runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
      //     //   planType: 1,
      //     templateId: this.templateId,
      //     supportTemplateId: this.supTemplateId,
      //   };
      //   await axios.post(this.url.getBusConfig, data, { params }).then((res) => {
      //     console.log("查询数据", res);
      //     if (res.data.retCode != 0) {
      //       this.$message.error(res.data.retMsg);
      //       this.setBool = false;
      //       this.createData = {};
      //       return;
      //     }
      //     this.createData = res.data.data.optimalInfo;
      //     this.presetsInfo = res.data.data.presetsInfo;
      //     this.mainShifts = res.data.data.mainShifts;
      //     this.subShifts = res.data.data.subShifts;
      //     this.setBool = true;
      //   });
      await this.getBusConfig();
    },
    nextDay() {
      let params = this.mes;
      this.runDate = moment(this.runDate).add('1', 'days');
      this.runDateStr = moment(this.runDate).format('YYYY-MM-DD dddd');
      //   let data = {
      //     routeId: this.routeId,
      //     supportRouteId: this.supRouteId,
      //     runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
      //     //   planType: 1,
      //     templateId: this.templateId,
      //     supportTemplateId: this.supTemplateId,
      //   };
      //   axios.post(this.url.getBusConfig, data, { params }).then((res) => {
      //     console.log("查询数据", res);
      //     if (res.data.retCode != 0) {
      //       this.$message.error(res.data.retMsg);
      //       this.setBool = false;
      //       this.createData = {};
      //       return;
      //     }
      //     this.createData = res.data.data.optimalInfo;
      //     this.presetsInfo = res.data.data.presetsInfo;
      //     this.mainShifts = res.data.data.mainShifts;
      //     this.subShifts = res.data.data.subShifts;
      //     this.setBool = true;
      //   });
      this.getBusConfig();
    },
    exDay() {
      let params = this.mes;
      this.runDate = moment(this.runDate).add('-1', 'days');
      this.runDateStr = moment(this.runDate).format('YYYY-MM-DD dddd');
      //   let data = {
      //     routeId: this.routeId,
      //     supportRouteId: this.supRouteId,
      //     runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
      //     //   planType: 1,
      //     templateId: this.templateId,
      //     supportTemplateId: this.supTemplateId,
      //   };
      //   axios.post(this.url.getBusConfig, data, { params }).then((res) => {
      //     console.log("查询数据", res);
      //     if (res.data.retCode != 0) {
      //       this.$message.error(res.data.retMsg);
      //       this.setBool = false;
      //       this.createData = {};
      //       return;
      //     }
      //     this.createData = res.data.data.optimalInfo;
      //     this.presetsInfo = res.data.data.presetsInfo;
      //     this.mainShifts = res.data.data.mainShifts;
      //     this.subShifts = res.data.data.subShifts;
      //     this.setBool = true;
      //   });
      this.getBusConfig();
    },
    handleOk() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }
      this.visible = false;
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

      axios.get(`${this.url.getJoinTemplateListByRouteId}/${value}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.temList = res.data.data;
      });
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
    onDateChange(date, dateString) {
      console.log(date, dateString);
      this.runDate = dateString;
    },
    onDateChange1(date, dateString) {
      this.passengerDate = dateString;
    },
    onDateChange2(date, dateString) {
      this.turnaroundDate = dateString;
    },
    onDateChange3(date, dateString) {
      this.supportPassengerDate = dateString;
    },
    onDateChange4(date, dateString) {
      this.supportTurnaroundDate = dateString;
    },

    async createIt() {
      let params = this.mes;
      if (
        !this.routeId ||
        !this.runDate ||
        !this.passengerDate ||
        !this.turnaroundDate ||
        !this.supRouteId ||
        !this.templateId ||
        !this.supTemplateId ||
        !this.supportPassengerDate ||
        !this.supportTurnaroundDate
      ) {
        this.$message.warning('请填写相关选项后生成排班！！！');
        return;
      }
      this.spinning = true;
      let data = {
        routeId: this.routeId,
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
        passengerData: `${this.passengerDate} 00:00:00`,
        turnaroundData: `${this.turnaroundDate} 00:00:00`,
        supportRouteId: this.supRouteId,
        planType: 1,
        templateId: this.templateId,
        supportTemplateId: this.supTemplateId,
        supportPassengerData: `${this.supportPassengerDate} 00:00:00`,
        supportTurnaroundData: `${this.supportTurnaroundDate} 00:00:00`,
      };

      await axios.post(this.url.generateSupportSchedule, data, { params }).then((res) => {
        console.log(res);
        this.spinning = false;
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.$message.success(res.data.retMsg);
        this.setBool = true;
      });

      if (this.setBool) {
        await this.getBusConfig();
      }
    },
    async setCreateIt() {
      let params = this.mes;
      if (
        !this.routeId ||
        !this.runDate ||
        !this.passengerDate ||
        !this.turnaroundDate ||
        !this.supRouteId ||
        !this.templateId ||
        !this.supTemplateId ||
        !this.supportPassengerDate ||
        !this.supportTurnaroundDate
      ) {
        this.$message.warning('请填写相关选项后生成排班！！！');
        return;
      }
      this.spinning = true;
      let data = {
        routeId: this.routeId,
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
        passengerData: `${this.passengerDate} 00:00:00`,
        turnaroundData: `${this.turnaroundDate} 00:00:00`,
        supportRouteId: this.supRouteId,
        planType: 2,
        templateId: this.templateId,
        supportTemplateId: this.supTemplateId,
        supportPassengerData: `${this.supportPassengerDate} 00:00:00`,
        supportTurnaroundData: `${this.supportTurnaroundDate} 00:00:00`,
        busNumberUp: this.presetsInfo.mainUpBusNum,
        busNumberDown: this.presetsInfo.mainDownBusNum,
        singleBusUp: this.presetsInfo.mainSingleBusUp,
        singleBusDwon: this.presetsInfo.mainSingleBusDown,
        earlyHalfBusUp: this.presetsInfo.mainEarlyHalfBusUp,
        earlyHalfBusDown: this.presetsInfo.mainEarlyHalfBusDown,
        lateHalfBusUp: this.presetsInfo.mainLateHalfBusUp,
        lateHalfBusDown: this.presetsInfo.mainLateHalfBusDown,
        middleBusUp: this.presetsInfo.mainMiddleBusUp,
        middleBusDown: this.presetsInfo.mainMiddleBusDown,
        doubleStopBusUp: this.presetsInfo.mainDoubleStopBusUp,
        doubleStopBusDown: this.presetsInfo.mainDoubleStopBusUp,
        supportBusNumberUp: this.presetsInfo.subUpBusNum,
        supportBusNumberDown: this.presetsInfo.subDownBusNum,
        supportSingleBusUp: this.presetsInfo.subSingleBusUp,
        supportSingleBusDwon: this.presetsInfo.subSingleBusDown,
        supportEarlyHalfBusUp: this.presetsInfo.subEarlyHalfBusUp,
        supportEarlyHalfBusDown: this.presetsInfo.subEarlyHalfBusDown,
        supportLateHalfBusUp: this.presetsInfo.subLateHalfBusUp,
        supportLateHalfBusDown: this.presetsInfo.subLateHalfBusDown,
        supportMiddleBusUp: this.presetsInfo.subMiddleBusUp,
        supportMiddleBusDown: this.presetsInfo.subMiddleBusDown,
        supportDoubleStopBusUp: this.presetsInfo.subDoubleStopBusUp,
        supportDoubleStopBusDown: this.presetsInfo.subDoubleStopBusDown,
      };

      await axios.post(this.url.generateSupportSchedule, data, { params }).then((res) => {
        console.log(res);
        this.spinning = false;
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.$message.success(res.data.retMsg);
        this.setBool = true;
      });

      if (this.setBool) {
        await this.getBusConfig();
        // let data1 = {
        //   routeId: this.routeId,
        //   supportRouteId: this.supRouteId,
        //   runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
        //   //   planType: 1,
        //   templateId: this.templateId,
        //   supportTemplateId: this.supTemplateId,
        // };
        // await axios
        //   .post(this.url.getBusConfig, data1, { params })
        //   .then((res) => {
        //     console.log("查询数据", res);
        //     if (res.data.retCode != 0) {
        //       this.$message.error(res.data.retMsg);
        //       return;
        //     }
        //     this.createData = res.data.data.optimalInfo;
        //     if (res.data.data.presetsInfo) {
        //       this.presetsInfo = res.data.data.presetsInfo;
        //     } else {
        //       this.presetsInfo = {
        //         id: null,
        //         routeId: null,
        //         supportRouteId: null,
        //         type: null,
        //         runDate: null,
        //         mainUpBusNum: 0,
        //         mainDownBusNum: 0,
        //         mainSingleBusUp: 0,
        //         mainSingleBusDown: 0,
        //         mainEarlyHalfBusUp: 0,
        //         mainEarlyHalfBusDown: 0,
        //         subUpBusNum: 0,
        //         subDownBusNum: 0,
        //         subSingleBusUp: 0,
        //         subSingleBusDown: 0,
        //         subEarlyHalfBusUp: 0,
        //         subEarlyHalfBusDown: 0,
        //         templateId: null,
        //         supportTemplateId: null,
        //         mainLateHalfBusUp: 0,
        //         mainLateHalfBusDown: 0,
        //         mainMiddleBusUp: 0,
        //         mainMiddleBusDown: 0,
        //         mainDoubleStopBusUp: 0,
        //         mainDoubleStopBusDown: 0,
        //         subLateHalfBusUp: 0,
        //         subLateHalfBusDown: 0,
        //         subMiddleBusUp: 0,
        //         subMiddleBusDown: 0,
        //         subDoubleStopBusUp: 0,
        //         subDoubleStopBusDown: 0,
        //       };
        //     }
        //     this.mainShifts = res.data.data.mainShifts;
        //     this.subShifts = res.data.data.subShifts;
        //   });
      }
    },
    getBusConfig() {
      let params = this.mes;
      let data1 = {
        routeId: this.routeId,
        supportRouteId: this.supRouteId,
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
        //   planType: 1,
        templateId: this.templateId,
        supportTemplateId: this.supTemplateId,
      };
      axios.post(this.url.getBusConfig, data1, { params }).then((res) => {
        console.log('查询数据', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          this.setBool = false;
          this.createData = {};
          this.presetsInfo = {};
          return;
        }
        this.setBool = true;
        this.createData = res.data.data.optimalInfo;
        if (res.data.data.presetsInfo) {
          this.presetsInfo = res.data.data.presetsInfo;
        } else {
          this.presetsInfo = {
            id: null,
            routeId: null,
            supportRouteId: null,
            type: null,
            runDate: null,
            mainUpBusNum: 0,
            mainDownBusNum: 0,
            mainSingleBusUp: 0,
            mainSingleBusDown: 0,
            mainEarlyHalfBusUp: 0,
            mainEarlyHalfBusDown: 0,
            subUpBusNum: 0,
            subDownBusNum: 0,
            subSingleBusUp: 0,
            subSingleBusDown: 0,
            subEarlyHalfBusUp: 0,
            subEarlyHalfBusDown: 0,
            templateId: null,
            supportTemplateId: null,
            mainLateHalfBusUp: 0,
            mainLateHalfBusDown: 0,
            mainMiddleBusUp: 0,
            mainMiddleBusDown: 0,
            mainDoubleStopBusUp: 0,
            mainDoubleStopBusDown: 0,
            subLateHalfBusUp: 0,
            subLateHalfBusDown: 0,
            subMiddleBusUp: 0,
            subMiddleBusDown: 0,
            subDoubleStopBusUp: 0,
            subDoubleStopBusDown: 0,
          };
        }
        console.log('presetsInfo', this.presetsInfo);
        this.mainShifts = res.data.data.mainShifts;
        this.subShifts = res.data.data.subShifts;
      });
    },
  },
};
</script>

<style lang="less" scoped>
.tit {
  display: flex;
  justify-content: space-between;
  span {
    display: inline-block;
    line-height: 40px;
  }
  .tit-date {
    font-size: 20px;
    font-weight: 600;
  }
  span:nth-child(1) {
    cursor: pointer;
  }
  span:nth-child(3) {
    cursor: pointer;
  }
}
.line {
  width: 97%;
  height: 1px;
  margin: 10px 0;
  background: #dadada;
}
.set-best {
  box-sizing: border-box;
  border: 1px solid #dadada;
  border-radius: 10px;
  box-sizing: border-box;
  padding: 10px 20px;
  margin: 10px 0 0 0;
  .best-tit {
    display: flex;
    justify-content: space-between;
    margin: 0 0 10px 0;
    span {
      display: inline-block;
    }
    span:nth-child(1) {
    }
    span:nth-child(2) {
      cursor: pointer;
      padding: 5px 10px;
      border-radius: 5px;
      color: #fff;
      background: #1890ff;
    }
  }
  .best-con {
    // display: flex;
    // justify-content: space-between;
    .con-li {
      //   display: flex;
      width: 100%;
      margin: 0 0 10px 0;

      span {
        display: inline-block;
      }
      .li-left {
        font-size: 14px;
        line-height: 36px;
        margin-right: 30px;
      }
      .li-box {
        display: inline-block;
        position: relative;
        width: 250px;
        height: +36px;
        // line-height: 42px;
        border: 1px solid #dadada;
        border-radius: 5px;
        div,
        span {
          display: inline-block;
        }

        .logo {
          position: absolute;
          top: 0;
          left: 0;
          color: #1890ff;
          background: #dbefff;
          font-size: 12px;
        }
        .box-area {
          margin-left: 30px;
          display: flex;
        }
        .box-name {
          display: flex;
          width: 50%;
          line-height: 36px;
          justify-content: space-between;
          box-sizing: border-box;
          padding: 0 10px;
          font-size: 14px;
          span {
            color: #1890ff;
            font-weight: 600;
          }
        }
      }
    }
  }
}
.set-want {
  box-sizing: border-box;
  border: 1px solid #dadada;
  border-radius: 10px;
  box-sizing: border-box;
  padding: 10px 20px;
  margin: 10px 0 0 0;
  .best-tit {
    display: flex;
    justify-content: space-between;
    margin: 0 0 10px 0;
    span {
      display: inline-block;
    }
    span:nth-child(1) {
    }
    span:nth-child(2) {
      cursor: pointer;
      padding: 5px 10px;
      border-radius: 5px;
      color: #fff;
      background: #2fcba9;
    }
  }
  .best-con {
    .con-li {
      //   display: flex;
      width: 100%;
      margin: 0 0 10px 0;

      span {
        display: inline-block;
      }
      .li-left {
        font-size: 14px;
        line-height: 36px;
        margin-right: 30px;
      }
      .li-box {
        display: inline-block;
        position: relative;
        width: 250px;
        height: 140px;
        overflow-y: auto;
        // line-height: 42px;
        border: 1px solid #dadada;
        border-radius: 5px;
        box-sizing: border-box;
        padding: 5px 0;
        div,
        span {
          display: inline-block;
        }

        .logo {
          position: absolute;
          top: 0;
          left: 0;
          color: #1890ff;
          background: #dbefff;
          font-size: 12px;
        }
        .box-area {
          margin: 0 0 0 30px;
          width: calc(100% - 30px);
          //   display: flex;
        }
        .box-name {
          display: flex;
          width: 100%;
          line-height: 32px;
          justify-content: space-between;
          box-sizing: border-box;
          padding: 0 10px;
          font-size: 14px;
          margin: 0 0 10px 0;
          //   span {
          //     color: #1890ff;
          //     font-weight: 600;
          //   }
        }
      }
    }
  }
}
</style>
