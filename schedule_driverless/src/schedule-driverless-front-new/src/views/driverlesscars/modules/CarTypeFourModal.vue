<template>
  <section id="section">
    <div class="con-head">
      <div class="con-info" style="display: inline-block">
        <div style="display: inline-block">
          排班计划（当前为 <span>{{ this.planType == 1 ? '最优排班' : '预设排班' }}</span
          >，最后一次生成计划时间为
          {{ centerData.titleMap.lastPlanTime }}
          ）
        </div>
        <div style="display: inline-block">主线班次:{{ centerData.titleMap.totalClasses }} /被支援班次{{ centerData.titleMap.totalSupportClasses }}</div>
        <div style="display: inline-block">关联线班次:{{ centerData.titleMap.subTotalClasses }}/被支援班次{{ centerData.titleMap.subTotalSupportClasses }}</div>
      </div>
      <div class="time-choice">
        <img v-if="btnShow" @click="playIt()" src="@/assets/driverlesscars/play.png" alt="" />
        <img v-else @click="stopIt()" src="@/assets/driverlesscars/stop.png" alt="" />
        <a-time-picker v-model="time" format="HH:mm:ss" @change="timeChange" />
      </div>
      <div class="progress-bar">
        <span>播放速度</span>
        <div class="bar">
          <ul class="round">
            <li v-for="(item, index) in speedList" @click="speedChoice(index)">
              <img v-show="speed == index" src="@/assets/driverlesscars/round.png" />
            </li>
          </ul>
          <ul class="num">
            <li>10倍</li>
            <li>20倍</li>
            <li>40倍</li>
          </ul>
        </div>
      </div>
    </div>
    <div class="car-content">
      <div class="left-car">
        <div class="content">
          <!-- 左侧总站公交 -->
          <div class="left-z-car">
            <div class="bus" style="margin: 0 0 10px 10px" v-for="item in downFinalCars">
              <div class="bus-container">
                <div
                  class="bus-bar"
                  :style="{
                    background: `conic-gradient(
                                    #5CC065 0% 0%,
                                    #5CC065 0% 0%,
                                    #b6b6b6 0% 100%,
                                    #b6b6b6 100% 100%
                                )`,
                  }"
                ></div>
                <div class="bus-text">
                  {{ item.busName ? item.busName : item.busNameFull }}
                </div>
              </div>
            </div>
          </div>

          <!-- 公交线路图 -->
          <div class="car-box" :class="`type${carType}`">
            <!-- 上行公交 -->
            <div class="top-car">
              <div class="top-bg"></div>
              <div class="line-car line-car-top">
                <div class="line" v-for="item in arrUp" :style="{ flex: item.stationDistance }" :routeStationId="item.routeStationId">
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div class="bus" v-show="item.cars" :class="{ stationRun: item.cars && !item.cars.haha }">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>

                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 80"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #F47A55 0% 0%,
                                                                #F47A55 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 60"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #E2CE29 0% 0%,
                                                                #E2CE29 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 0"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #5CC065 0% 0%,
                                                                #5CC065 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{ item.cars ? (item.cars.busName ? item.cars.busName : item.cars.busNameFull) : '' }}
                      </div>
                    </div>
                  </div>
                  <img class="logo" src="@/assets/driverlesscars/goRight.png" alt="" />
                </div>
              </div>
            </div>
            <div class="center-car">
              <p>
                <span>上行运行时间: {{ `${centerData.mainMap.upBeginTime}-${centerData.mainMap.upEndTime}` }}</span>
                <span class="route-name"
                  ><div>{{ routeName }}</div></span
                >
                <span>下行运行时间: {{ `${centerData.mainMap.downBeginTime}-${centerData.mainMap.downEndTime}` }}</span>
              </p>
              <p>
                <span>总配车数: {{ centerData.mainMap.totalBusNum }}</span
                ><span>单班车数: {{ centerData.mainMap.singleBusNum }}</span
                ><span>双班车数: {{ centerData.mainMap.doubleBusNum }}</span>
              </p>
              <p>
                <span>计划支援总班次数:{{ centerData.mainMap.totalSupportClasses }}</span
                ><span>支援开始时间:{{ centerData.mainMap.supportBeginTime }}</span
                ><span>支援结束时间:{{ centerData.mainMap.supportEndTime }}</span>
              </p>
            </div>
            <!-- 下行公交 -->
            <div class="bottom-car">
              <div class="bottom-bg"></div>
              <div class="line-car line-car-bottom">
                <div class="line" v-for="(item, index) in arrDown" :style="{ flex: item.stationDistance }" :routeStationId="item.routeStationId">
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div class="bus mts" v-show="item.cars" :class="{ mts: item.cars && !item.cars.haha }">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                              #ff0000 0% 0%,
                                                              #ff0000 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                              #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                              #b6b6b6 100% 100%
                                                          )`,
                        }"
                      ></div>

                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 80"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #F47A55 0% 0%,
                                                                #F47A55 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 60"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #E2CE29 0% 0%,
                                                                #E2CE29 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 0"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #5CC065 0% 0%,
                                                                #5CC065 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{ item.cars ? (item.cars.busName ? item.cars.busName : item.cars.busNameFull) : '' }}
                      </div>
                    </div>
                  </div>

                  <img class="logo" src="@/assets/driverlesscars/goLeft.png" alt="" />
                </div>
              </div>
            </div>
            <!-- 总站标识 -->
            <div class="top-st">{{ arrUp[0].routeStationName }}</div>
            <div class="bottom-st" v-show="[6, 7].includes(carType)">{{ arrUp[arrUp.length - 1].routeStationName }}</div>
          </div>

          <!-- 右侧总站公交 -->
          <div class="right-z-car">
            <div class="bus" v-for="item in upFinalCars">
              <div class="bus-container">
                <div
                  class="bus-bar"
                  :style="{
                    background: `conic-gradient(
                                    #5CC065 0% 0%,
                                    #5CC065 0% 0%,
                                    #b6b6b6 0% 100%,
                                    #b6b6b6 100% 100%
                                )`,
                  }"
                ></div>
                <div class="bus-text">
                  {{ item.busName ? item.busName : item.busNameFull }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-car">
        <div class="content">
          <!-- 左侧总站公交 -->
          <div class="left-z-car">
            <div class="bus" style="margin: 0 0 10px 10px" v-for="item in downFinalCarsTwo">
              <div class="bus-container">
                <div
                  class="bus-bar"
                  :style="{
                    background: `conic-gradient(
                                    #5CC065 0% 0%,
                                    #5CC065 0% 0%,
                                    #b6b6b6 0% 100%,
                                    #b6b6b6 100% 100%
                                )`,
                  }"
                ></div>
                <div class="bus-text">
                  {{ item.busName ? item.busName : item.busNameFull }}
                </div>
              </div>
            </div>
          </div>

          <!-- 公交线路图 -->
          <div class="car-box" :class="`type${carType}`">
            <!-- 上行公交 -->
            <div class="top-car">
              <div class="top-bg"></div>
              <div class="line-car line-car-top">
                <div class="line" v-for="item in arrUpTwo" :style="{ flex: item.stationDistance }" :routeStationId="item.routeStationId">
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div class="bus" v-show="item.cars" :class="{ stationRun: item.cars && !item.cars.haha }">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>

                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 80"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #F47A55 0% 0%,
                                            #F47A55 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 60"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #E2CE29 0% 0%,
                                            #E2CE29 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 0"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #5CC065 0% 0%,
                                            #5CC065 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{ item.cars ? (item.cars.busName ? item.cars.busName : item.cars.busNameFull) : '' }}
                      </div>
                    </div>
                  </div>
                  <img class="logo" src="@/assets/driverlesscars/goRight.png" alt="" />
                </div>
              </div>
            </div>
            <div class="center-car">
              <p>
                <span>上行运行时间: {{ `${centerData.subMap.upBeginTime}-${centerData.subMap.upEndTime}` }}</span>
                <span class="route-name"
                  ><div>{{ supRouteName }}</div></span
                >
                <span>下行运行时间: {{ `${centerData.subMap.downBeginTime}-${centerData.subMap.downEndTime}` }}</span>
              </p>
              <p>
                <span>总配车数: {{ centerData.subMap.totalBusNum }}</span
                ><span>单班车数: {{ centerData.subMap.singleBusNum }}</span
                ><span>双班车数: {{ centerData.subMap.doubleBusNum }}</span>
              </p>
              <p>
                <span>计划支援总班次数:{{ centerData.subMap.totalSupportClasses }}</span
                ><span>支援开始时间:{{ centerData.subMap.supportBeginTime }}</span
                ><span>支援结束时间:{{ centerData.subMap.supportEndTime }}</span>
              </p>
            </div>
            <!-- 下行公交 -->
            <div class="bottom-car">
              <div class="bottom-bg"></div>
              <div class="line-car line-car-bottom">
                <div class="line" v-for="(item, index) in arrDownTwo" :style="{ flex: item.stationDistance }" :routeStationId="item.routeStationId">
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div class="bus mts" v-show="item.cars" :class="{ mts: item.cars && !item.cars.haha }">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>

                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 80"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                                                #F47A55 0% 0%,
                                                                #F47A55 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                                                #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                                                #b6b6b6 100% 100%
                                                            )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 60"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #E2CE29 0% 0%,
                                            #E2CE29 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div
                        v-else-if="item.cars && item.cars.fullLoadRatio >= 0"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #5CC065 0% 0%,
                                            #5CC065 0% ${item.cars ? item.cars.fullLoadRatio : 0}%,
                                            #b6b6b6 ${item.cars ? item.cars.fullLoadRatio : 0}% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{ item.cars ? (item.cars.busName ? item.cars.busName : item.cars.busNameFull) : '' }}
                      </div>
                    </div>
                  </div>

                  <img class="logo" src="@/assets/driverlesscars/goLeft.png" alt="" />
                </div>
              </div>
            </div>
            <!-- 总站标识 -->
            <div class="top-st">{{ arrUpTwo[0].routeStationName }}</div>
            <div class="bottom-st">
              {{ arrUpTwo[arrUpTwo.length - 1].routeStationName }}
            </div>
          </div>

          <!-- 右侧总站公交 -->
          <div class="right-z-car">
            <div class="bus" v-for="item in upFinalCarsTwo">
              <div class="bus-container">
                <div
                  class="bus-bar"
                  :style="{
                    background: `conic-gradient(
                                    #5CC065 0% 0%,
                                    #5CC065 0% 0%,
                                    #b6b6b6 0% 100%,
                                    #b6b6b6 100% 100%
                                )`,
                  }"
                ></div>
                <div class="bus-text">
                  {{ item.busName ? item.busName : item.busNameFull }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import '@/assets/less/base.css';
import { getAction, postAction, getActionNew, postActionNew } from '@/api/manage';
// import { mixinDevice } from '@/utils/mixin';
import moment from 'moment';
import busData from './busData.json';
// import stationOne from './stationOne.json';
// import stationTwo from './stationTwo.json';
export default {
  name: 'CarTypeFourModal',
  props: ['sendData'],
  data() {
    return {
      url: {
        adrealInfo: `${process.env.VUE_APP_BUS_API}/simulation/adrealInfo`,
        getListByRouteId: `${process.env.VUE_APP_BUS_API}/routeStation/getListByRouteId`,
        getRuningScheduleConfig: `${process.env.VUE_APP_BUS_API}/schedule/getRuningScheduleConfig`,
      },
      mes: '',
      planType: '',
      // 0:共首站，1:共末站，2:共首末站，3:首站为支援线路末站，4:末站为支援线路首站，5:首末站相邻
      carType: null,
      routeId: '',
      routeName: '',
      supRouteId: '',
      supRouteName: '',
      runDate: '',
      time: '',
      playBool: true,
      // 速度选择
      speedList: [1, 2, 3],
      speed: 1,
      speedSecond: 500,
      timeCount: 50,
      /************************/
      // 主线路图
      arrUp: [],
      upStation: [],
      arrDown: [],
      downStation: [],
      centerData: '',
      // 当前时间段的车辆数据
      needArr: [],
      needBusCodeArr: [],
      // 没有运行的车辆
      noNeedArr: [],
      // 进出站定时器
      timer: '',
      // 时间定时器
      timeCounter: '',
      // 开始停止按钮
      btnShow: true,
      // 下行总站停靠车辆
      downFinalCars: [],
      // 上行总站停靠车辆
      upFinalCars: [],

      // 副线路图
      arrUpTwo: [],
      upStationTwo: [],
      arrDownTwo: [],
      downStationTwo: [],
      centerDataTwo: '',
      // 当前时间段的车辆数据
      needArrTwo: [],
      needBusCodeArrTwo: [],
      // 没有运行的车辆
      noNeedArrTwo: [],
      // 进出站定时器
      timerTwo: '',
      // 时间定时器
      timeCounterTwo: '',
      // 开始停止按钮
      btnShowTwo: true,
      // 下行总站停靠车辆
      downFinalCarsTwo: [],
      // 上行总站停靠车辆
      upFinalCarsTwo: [],

      /*****************************/
    };
  },
  watch: {
    sendData() {
      console.log('sendData', this.sendData);
      this.centerData = this.sendData.centerData;
      this.runDate = this.sendData.runDate;
      this.time = moment(this.sendData.time, 'HH:mm:ss');
      this.timeStr = this.sendData.time;
      this.routeId = this.sendData.routeId;
      this.routeName = this.sendData.routeName;
      this.supRouteId = this.sendData.supRouteId;
      this.supRouteName = this.sendData.supRouteName;
      this.carType = this.sendData.busRunData.data.simulationType;
      this.planType = this.sendData.planType;
      console.log(this.carType);
      this.getListByRouteId();
      this.getListByRouteId2();
    },
  },
  created() {
    const queryString = window.location.search;
    const searchParams = new URLSearchParams(queryString);
    const paramString = searchParams.get('paramString');
    console.log(paramString);
    this.mes = new URLSearchParams();
    this.mes.append('paramString', paramString);
    console.log(this.mes.toString());
  },
  mounted() {
    // this.adrealInfo();
    // this.adrealInfo2();
  },
  methods: {
    timeChange(value) {
      console.log('timeChange', value);
      this.timeStr = moment(value).format('HH:mm:ss');
    },
    // 速度选择
    speedChoice: function (index) {
      if (this.btnShow == false) {
        alert('请停止播放再选择速度');
        return;
      }
      this.speed = index;
      if (index == 0) {
        this.speedSecond = 1000;
        this.timeCound = 100;
      } else if (index == 1) {
        this.speedSecond = 500;
        this.timeCound = 50;
      } else if (index == 2) {
        this.speedSecond = 250;
        this.timeCound = 25;
      }
    },
    playIt() {
      let that = this;
      let params = that.mes;
      let send = {
        routeId: that.routeId,
        runDate: `${that.runDate} ${that.timeStr}`,
        supportRouteId: that.supRouteId,
        planType: this.planType,
      };
      axios.post(that.url.adrealInfo, send, { params }).then((res) => {
        console.log('进来请求参数', res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        that.adrealInfo(res.data.data.mainList);
        that.adrealInfo2(res.data.data.subList);
      });
      // this.adrealInfo();
      // this.adrealInfo2();
      this.btnShow = !this.btnShow;
    },
    stopIt() {
      this.btnShow = !this.btnShow;
      this.timeStr = moment(this.time).format('HH:mm:ss');
      window.clearInterval(this.timer);
      window.clearInterval(this.timeCounter);
      window.clearInterval(this.timerTwo);
      window.clearInterval(this.timeCounterTwo);
    },
    /****************************/
    //查询线路站点
    getListByRouteId() {
      let that = this;
      let params = this.res;
      axios.get(`${this.url.getListByRouteId}/${this.routeId}`, '', { params }).then((res) => {
        console.log('stationOne', res.data.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        var allArr = res.data.data;
        // 上行
        var arr1 = [];
        for (var i = 0; i < allArr.length; i++) {
          if (allArr[i].stationMark == '0' || allArr[i].stationMark == '1' || allArr[i].stationMark == '2') {
            arr1.push(allArr[i]);
          }
        }

        arr1[0].stationDistance = 0;
        // arr1[arr1.length - 1].stationDistance = 0;
        // console.log(arr1[1]);
        that.arrUp = arr1;
        that.upStation = arr1[arr1.length - 1].routeStationId;
        // 下行
        var arr2 = [];
        for (var i = 0; i < allArr.length; i++) {
          if (allArr[i].stationMark == '3' || allArr[i].stationMark == '4' || allArr[i].stationMark == '5') {
            arr2.push(allArr[i]);
          }
        }
        arr2.reverse();
        that.arrDown = arr2;
        that.downStation = arr2[0].routeStationId;
      });
    },
    getListByRouteId2() {
      let that = this;
      let params = this.res;
      axios.get(`${this.url.getListByRouteId}/${this.supRouteId}`, '', { params }).then((res) => {
        console.log('stationTwo', res.data.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        var allArr = res.data.data;
        // 上行
        var arr1 = [];
        for (var i = 0; i < allArr.length; i++) {
          if (allArr[i].stationMark == '0' || allArr[i].stationMark == '1' || allArr[i].stationMark == '2') {
            arr1.push(allArr[i]);
          }
        }

        arr1[0].stationDistance = 0;
        // arr1[arr1.length - 1].stationDistance = 0;
        // console.log(arr1[1]);
        that.arrUpTwo = arr1;
        that.upStationTwo = arr1[arr1.length - 1].routeStationId;
        // 下行
        var arr2 = [];
        for (var i = 0; i < allArr.length; i++) {
          if (allArr[i].stationMark == '3' || allArr[i].stationMark == '4' || allArr[i].stationMark == '5') {
            arr2.push(allArr[i]);
          }
        }
        arr2.reverse();

        // arr2[0].stationDistance = 0;
        // arr2[arr2.length - 1].stationDistance = 0;
        // console.log(arr2);
        that.arrDownTwo = arr2;
        that.downStationTwo = arr2[0].routeStationId;
      });
    },
    //进出站详情
    adrealInfo(val) {
      let that = this;
      //console.log(data);

      // axios.post(this.url.adrealInfo)

      // let data = this.sendData.busRunData;
      // let data = val;
      // console.log("busData", data);

      // let arrData = data.data.mainList;
      let arrData = val;
      let runDate = `${this.runDate} ${this.timeStr}`;
      let firstDate = `${this.runDate} ${this.timeStr}`;
      // let runDate = "2023-12-18 06:00:00";
      // let firstDate = "2023-12-18 06:00:00";
      let oneHourLater = new Date(new Date(firstDate).getTime() + 3300 * 1000);
      console.log('runDate', runDate);
      that.needArr = [];
      that.needBusCodeArr = [];
      // 过去5min区间
      let subDate = new Date(new Date(runDate).getTime() - 300 * 1000);
      // 提前打点的车辆
      for (let i = 0; i < arrData.length; i++) {
        let item = arrData[i];
        if (new Date(arrData[i].adTime) <= new Date(runDate) && new Date(arrData[i].adTime) >= subDate && arrData[i].adFlag == 1) {
          // 去重
          if (that.needBusCodeArr.indexOf(item.busCode) > -1) {
            that.needArr.forEach((ytem, y) => {
              // that.needArr.forEach((ktem, k) => {
              //     if (ytem.busCode == ktem.busCode && new Date(ytem.adTime) < new Date(ktem.adTime)){
              //         that.needArr.splice(y, 1);
              //     }
              // })
              if (item.busCode == ytem.busCode) {
                that.needArr.splice(y, 1);
              }
            });
          } else {
            that.needBusCodeArr.push(arrData[i].busCode);
          }
          that.needArr.push(arrData[i]);
        }
      }
      //console.log(that.needArr);
      // 复制一个新的数组，不影响原数组
      let hash = arrData.slice();
      let cutHour;
      that.timeCounter = setInterval(function () {
        let addDate = new Date(new Date(runDate).getTime() + 1 * 1000);
        console.log(moment(addDate).format('YYYY-MM-DD'));
        console.log(that.areDatesEqual(moment(addDate).format('YYYY-MM-DD'), moment(runDate).format('YYYY-MM-DD')));
        if (!that.areDatesEqual(moment(addDate).format('YYYY-MM-DD'), moment(runDate).format('YYYY-MM-DD'))) {
          clearInterval(this.timeCounter);
          clearInterval(this.timer);
          clearInterval(this.timeCounterTwo);
          clearInterval(this.timerTwo);
        }
        that.time = moment(addDate, 'hh:mm:ss');
        runDate = addDate;
        // 判断，55min后继续请求接口数据拼在arrData上面
        if (runDate > oneHourLater) {
          console.log('runDate', runDate, moment(runDate).format('YYYY-MM-DD HH:mm:ss'));
          let params = that.mes;
          let send = {
            routeId: that.routeId,
            runDate: moment(runDate).format('YYYY-MM-DD HH:mm:ss'),
            supportRouteId: that.supRouteId,
            planType: that.planType,
          };
          axios.post(that.url.adrealInfo, send, { params }).then((res) => {
            console.log(res);
            if (res.data.data != null) {
              for (let i = 0; i < res.data.data.mainList.length; i++) {
                arrData.push(res.data.data.mainList[i]);
              }
              // arrData.concat(data1.data);
              //console.log(arrData);
            }
          });
          cutHour = new Date(new Date(oneHourLater).getTime() + 900 * 1000);
          console.log(cutHour);
          oneHourLater = new Date(new Date(oneHourLater).getTime() + 3600 * 1000);
        }
        // 超过15min把旧数据删除了
        if (runDate > cutHour) {
          for (let i = 0; i < arrData.length; i++) {
            if (new Date(new Date(arrData[i].adTime).getTime()) < cutHour) {
              arrData.splice(i, 1);
            }
          }
          cutHour = new Date(new Date(oneHourLater).getTime() + 900 * 1000);
          //console.log(arrData);
        }
      }, that.timeCount);

      that.timer = setInterval(function () {
        that.noNeedArr = [];
        // let hash = [];

        // 时间区间
        let addDate = new Date(new Date(runDate).getTime() + 10 * 1000 * (1000 / that.speedSecond));
        let runDateThis = new Date(new Date(runDate).getTime() - (that.speedSecond * 1000) / that.timeCount);
        //时间跑超过1天处理zyj
        if (moment(addDate, 'yyyy-MM-dd') != that.date) {
          that.date = moment(addDate, 'yyyy-MM-dd');
        }

        // 去除重复选项
        for (let i = 0; i < hash.length; i++) {
          for (let j = i + 1; j < hash.length; j++) {
            if (hash[i].busCode === hash[j].busCode) {
              hash.splice(j, 1);
              j--;
            }
          }
        }
        // console.log(hash);
        // 辨别是否是新加数据，如果是旧数据，则添加一个标志，拿来辨别是否运行动画
        for (let x = 0; x < that.needArr.length; x++) {
          // let haha = 1;
          that.needArr[x].haha = 1;
        }

        arrData.forEach((item, i) => {
          if (new Date(item.adTime) < addDate && new Date(item.adTime) >= new Date(runDateThis) && item.adFlag == 1) {
            if (that.needBusCodeArr.indexOf(item.busCode) > -1) {
              that.needArr.forEach((ktem, k) => {
                if (item.busCode == ktem.busCode) {
                  that.needArr.splice(k, 1);
                }

                if (hash.length == 0 && item.serviceType == -32) {
                  hash.push(item);
                } else {
                  hash.forEach((jtem, j) => {
                    if (ktem.serviceType == -32) {
                      //单班中停 zyj
                      if (hash.indexOf(ktem) == -1) {
                        hash.push(ktem);
                      }
                    } else {
                      if (ktem.busCode == jtem.busCode) {
                        hash.splice(j, 1);
                      }
                    }
                  });
                }
              });
            } else {
              that.needBusCodeArr.push(item.busCode);
            }
            that.needArr.push(item);
          }
        });
        that.noNeedArr = hash;

        // 总站停靠车辆
        that.upFinalCars = [];
        that.downFinalCars = [];
        for (let i = 0; i < that.needArr.length; i++) {
          if (that.needArr[i].routeStationId == that.upStation) {
            that.upFinalCars.push(that.needArr[i]);
          }

          if (that.needArr[i].routeStationId == that.downStation) {
            that.downFinalCars.push(that.needArr[i]);
          }
        }

        // console.log(that.needArr);

        // 上行车辆数据
        for (let k = 0; k < that.arrUp.length; k++) {
          if (that.arrUp[k].cars) {
            delete that.arrUp[k].cars;
          }
          for (let i = 0; i < that.needArr.length; i++) {
            if (that.needArr[i].routeStationId == that.arrUp[k].routeStationId) {
              // that.arrUp[k] = Object.assign(that.arrUp[k],that.needArr[i]);
              //debugger
              that.arrUp[k].cars = that.needArr[i];
              Vue.set(that.arrUp, k, that.arrUp[k]);
            }
          }
        }
        if (that.arrUp[that.arrUp.length - 1].cars) {
          delete that.arrUp[that.arrUp.length - 1].cars;
        }

        // console.log(that.arrUp);

        // 下行车辆数据
        for (let k = 0; k < that.arrDown.length; k++) {
          if (that.arrDown[k].cars) {
            delete that.arrDown[k].cars;
          }
          for (let i = 0; i < that.needArr.length; i++) {
            if (that.needArr[i].routeStationId == that.arrDown[k].routeStationId) {
              // that.arrDown[k] = Object.assign(that.arrDown[k], that.needArr[i]);
              that.arrDown[k].cars = that.needArr[i];
              Vue.set(that.arrDown, k, that.arrDown[k]);
            }
          }
        }
        if (that.arrDown[0].cars) {
          delete that.arrDown[0].cars;
        }

        // console.log(that.arrDown);

        runDate = addDate;
        //console.log("runDate"+runDate)
      }, that.speedSecond);
    },
    adrealInfo2(val) {
      let that = this;
      //console.log(data);
      // let data = busData;
      // let data = this.sendData.busRunData;
      // let data = val;
      // console.log("busData", data);

      // var arrData = data.data.subList;
      var arrData = val;
      let runDate = `${this.runDate} ${this.timeStr}`;
      let firstDate = `${this.runDate} ${this.timeStr}`;
      console.log(runDate);
      // var runDate = "2023-12-18 06:00:00";
      // var firstDate = "2023-12-18 06:00:00";
      var oneHourLater = new Date(new Date(firstDate).getTime() + 3300 * 1000);
      console.log('runDate', runDate);
      that.needArrTwo = [];
      that.needBusCodeArrTwo = [];
      // 过去5min区间
      var subDate = new Date(new Date(runDate).getTime() - 300 * 1000);
      // 提前打点的车辆
      for (var i = 0; i < arrData.length; i++) {
        var item = arrData[i];
        if (new Date(arrData[i].adTime) <= new Date(runDate) && new Date(arrData[i].adTime) >= subDate && arrData[i].adFlag == 1) {
          // 去重
          if (that.needBusCodeArrTwo.indexOf(item.busCode) > -1) {
            that.needArrTwo.forEach((ytem, y) => {
              // that.needArrTwo.forEach((ktem, k) => {
              //     if (ytem.busCode == ktem.busCode && new Date(ytem.adTime) < new Date(ktem.adTime)){
              //         that.needArrTwo.splice(y, 1);
              //     }
              // })
              if (item.busCode == ytem.busCode) {
                that.needArrTwo.splice(y, 1);
              }
            });
          } else {
            that.needBusCodeArrTwo.push(arrData[i].busCode);
          }
          that.needArrTwo.push(arrData[i]);
        }
      }
      //console.log(that.needArrTwo);
      // 复制一个新的数组，不影响原数组
      var hash = arrData.slice();
      var cutHour;
      that.timeCounterTwo = setInterval(function () {
        var addDate = new Date(new Date(runDate).getTime() + 1 * 1000);
        that.time = moment(addDate, 'hh:mm:ss');
        runDate = addDate;

        // 判断，55min后继续请求接口数据拼在arrData上面
        if (runDate > oneHourLater) {
          let params = that.mes;
          let send = {
            routeId: that.routeId,
            supportRouteId: that.supRouteId,
            runDate: moment(runDate).format('YYYY-MM-DD HH:mm:ss'),
            planType: that.planType,
          };
          axios.post(that.url.adrealInfo, send, { params }).then((res) => {
            console.log(res);
            if (res.data.data != null) {
              for (let i = 0; i < res.data.data.subList.length; i++) {
                arrData.push(res.data.data.subList[i]);
              }
              // arrData.concat(data1.data);
              //console.log(arrData);
            }
          });

          cutHour = new Date(new Date(oneHourLater).getTime() + 900 * 1000);
          console.log(cutHour);
          oneHourLater = new Date(new Date(oneHourLater).getTime() + 3600 * 1000);
        }
        // 超过15min把旧数据删除了
        if (runDate > cutHour) {
          for (var i = 0; i < arrData.length; i++) {
            if (new Date(new Date(arrData[i].adTime).getTime()) < cutHour) {
              arrData.splice(i, 1);
            }
          }
          cutHour = new Date(new Date(oneHourLater).getTime() + 900 * 1000);
          //console.log(arrData);
        }
      }, that.timeCount);

      that.timerTwo = setInterval(function () {
        that.noNeedArrTwo = [];
        // var hash = [];

        // 时间区间
        var addDate = new Date(new Date(runDate).getTime() + 10 * 1000 * (1000 / that.speedSecond));
        var runDateThis = new Date(new Date(runDate).getTime() - (that.speedSecond * 1000) / that.timeCount);
        //时间跑超过1天处理zyj
        if (moment(addDate, 'yyyy-MM-dd') != that.date) {
          that.date = moment(addDate, 'yyyy-MM-dd');
        }

        // 去除重复选项
        for (var i = 0; i < hash.length; i++) {
          for (var j = i + 1; j < hash.length; j++) {
            if (hash[i].busCode === hash[j].busCode) {
              hash.splice(j, 1);
              j--;
            }
          }
        }
        // console.log(hash);
        // 辨别是否是新加数据，如果是旧数据，则添加一个标志，拿来辨别是否运行动画
        for (var x = 0; x < that.needArrTwo.length; x++) {
          // var haha = 1;
          that.needArrTwo[x].haha = 1;
        }

        arrData.forEach((item, i) => {
          if (new Date(item.adTime) < addDate && new Date(item.adTime) >= new Date(runDateThis) && item.adFlag == 1) {
            if (that.needBusCodeArrTwo.indexOf(item.busCode) > -1) {
              that.needArrTwo.forEach((ktem, k) => {
                if (item.busCode == ktem.busCode) {
                  that.needArrTwo.splice(k, 1);
                }

                if (hash.length == 0 && item.serviceType == -32) {
                  hash.push(item);
                } else {
                  hash.forEach((jtem, j) => {
                    if (ktem.serviceType == -32) {
                      //单班中停 zyj
                      if (hash.indexOf(ktem) == -1) {
                        hash.push(ktem);
                      }
                    } else {
                      if (ktem.busCode == jtem.busCode) {
                        hash.splice(j, 1);
                      }
                    }
                  });
                }
              });
            } else {
              that.needBusCodeArrTwo.push(item.busCode);
            }
            that.needArrTwo.push(item);
          }
        });
        // console.log(that.needArrTwo);
        that.noNeedArrTwo = hash;

        // 总站停靠车辆
        that.upFinalCarsTwo = [];
        that.downFinalCarsTwo = [];
        for (var i = 0; i < that.needArrTwo.length; i++) {
          if (that.needArrTwo[i].routeStationId == that.upStationTwo) {
            that.upFinalCarsTwo.push(that.needArrTwo[i]);
          }

          if (that.needArrTwo[i].routeStationId == that.downStationTwo) {
            that.downFinalCarsTwo.push(that.needArrTwo[i]);
          }
        }

        // console.log(that.needArrTwo);

        // 上行车辆数据
        for (var k = 0; k < that.arrUpTwo.length; k++) {
          if (that.arrUpTwo[k].cars) {
            delete that.arrUpTwo[k].cars;
          }
          for (var i = 0; i < that.needArrTwo.length; i++) {
            if (that.needArrTwo[i].routeStationId == that.arrUpTwo[k].routeStationId) {
              // that.arrUpTwo[k] = Object.assign(that.arrUpTwo[k],that.needArrTwo[i]);
              //debugger
              that.arrUpTwo[k].cars = that.needArrTwo[i];
              // console.log(that.arrUpTwo[k]);
              Vue.set(that.arrUpTwo, k, that.arrUpTwo[k]);
            }
          }
        }
        if (that.arrUpTwo[that.arrUpTwo.length - 1].cars) {
          delete that.arrUpTwo[that.arrUpTwo.length - 1].cars;
        }

        // console.log(that.arrUpTwo);

        // 下行车辆数据
        for (var k = 0; k < that.arrDownTwo.length; k++) {
          if (that.arrDownTwo[k].cars) {
            delete that.arrDownTwo[k].cars;
          }
          for (var i = 0; i < that.needArrTwo.length; i++) {
            if (that.needArrTwo[i].routeStationId == that.arrDownTwo[k].routeStationId) {
              // that.arrDownTwo[k] = Object.assign(that.arrDownTwo[k], that.needArrTwo[i]);
              that.arrDownTwo[k].cars = that.needArrTwo[i];
              Vue.set(that.arrDownTwo, k, that.arrDownTwo[k]);
            }
          }
        }
        if (that.arrDownTwo[0].cars) {
          delete that.arrDownTwo[0].cars;
        }

        // console.log(that.arrDownTwo);

        runDate = addDate;
        //console.log("runDate"+runDate)
      }, that.speedSecond);
    },
    areDatesEqual(dateStr1, dateStr2) {
      const date1 = new Date(dateStr1);
      const date2 = new Date(dateStr2);
      return date1.getTime() === date2.getTime();
    },
  },
};
</script>

<style lang="less" scoped>
section {
  .con-head {
    // margin: 12px 0 0 0;
    padding: 12px;
    .con-info {
      display: inline-block;
      margin-right: 30px;
      div {
        display: inline-block;
        font-size: 14px;
        font-weight: 600;
        margin-right: 12px;
      }
      div:nth-child(1) {
        span {
          display: inline-block;
          font-size: 16px;
          font-weight: 500;
        }
      }
    }

    .time-choice {
      display: inline-block;
      margin-right: 20px;
      img {
        display: inline-block;
        width: 24px;
        margin-right: 5px;
        cursor: pointer;
      }
    }
    .progress-bar {
      display: inline-block;
      position: relative;
      height: 34px;
      span {
        display: inline-block;
        font-size: 14px;
        line-height: 34px;
        margin-right: 5px;
      }
      .bar {
        display: inline-block;
        width: 180px;
        height: 2px;
        background: #8ba0bf;
        vertical-align: top;
        margin: 12px 0 0 0;
        position: relative;
        .round {
          position: absolute;
          top: -8px;
          width: 100%;
          li {
            display: inline-block;
            width: 18px;
            height: 18px;
            border-radius: 18px;
            background: #bfd9f3;
            position: absolute;
            cursor: pointer;
            img {
              position: absolute;
              width: 23px;
              top: -1px;
              left: -2px;
            }
          }
          li:nth-child(1) {
            top: 0;
            left: 0;
          }
          li:nth-child(2) {
            top: 0;
            left: 80px;
          }
          li:nth-child(3) {
            top: 0;
            right: 0;
          }
        }
        .num {
          position: absolute;
          top: 8px;
          width: 104%;
          font-size: 12px;
          li {
            display: inline-block;
          }
          li:nth-child(2) {
            margin: 0 52px;
          }
        }
      }
    }
  }
  .car-content {
    display: flex;
    flex-wrap: nowrap;
    .left-car {
      width: 50%;
      height: 560px;

      .content {
        // overflow: scroll;
        width: 100%;
        height: 560px;
        position: relative;
        display: flex;
        align-items: center;
        left: 0;
        right: 0;
        margin: 0 auto;

        .car-box {
          flex: 1;
          height: 400px;
          margin: 0 0 0 60px;
          position: relative;

          .top-car {
            position: relative;
            width: 100%;
            height: 50%;

            .top-bg {
              position: absolute;
              width: 100%;
              height: 100%;
              border-top-left-radius: 50px;
              border-top-right-radius: 50px;
              overflow: hidden;
              border: 6px solid #2796fd;
              border-bottom: none;
              // box-shadow: inset 0 0 0 2px #2680eb, /* 内边框，宽度为2px */ 0 0 0 2px #2680eb; /* 外边框，宽度为2px */
            }
          }

          .bottom-car {
            position: relative;
            display: grid;
            width: 100%;
            height: 50%;

            .bottom-bg {
              position: absolute;
              width: 100%;
              height: 100%;
              border-bottom-left-radius: 50px;
              border-bottom-right-radius: 50px;
              overflow: hidden;
              border: 6px solid #2796fd;
              border-top: none;
            }
          }

          .line-car {
            height: 168px;
            width: 85%;
            margin: 0 auto;
            display: flex;
            position: relative;

            .line {
              flex: 1;
              position: relative;

              .model {
                width: 20px;
                height: 162px;
                line-height: 21px;
                font-size: 14px;
                position: relative;

                > span {
                  width: 100%;
                  // text-align: center;
                  display: -webkit-box;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 7;
                  line-clamp: 7;
                  -webkit-box-orient: vertical;
                  writing-mode: vertical-rl;
                  // text-orientation: upright; /* 保持文字正立，可选 */
                }
              }
            }
          }

          .line-car-top {
            margin-top: 0;

            .line {
              .model {
                float: right;

                > span {
                  margin-top: 15px;
                }
              }

              .model:after {
                content: ' ';
                position: absolute;
                left: 0;
                top: -2px;
                right: 0;
                width: 10px;
                height: 10px;
                border-radius: 5px;
                border: 2px solid #2680eb;
                background: white;
                margin: 0 auto;
              }

              .bus {
                // display: none;
                position: absolute;
                top: -26px;
                right: calc(0% - 14px);

                .bus-container {
                  position: relative;
                  width: 50px;
                  height: 26px;
                  background: #ffffff;
                  border-radius: 15px; /* 设置长方形四个角的弧度 */
                  overflow: hidden;
                }

                .bus-bar {
                  position: relative;
                  width: 100%;
                  height: 100%;
                  background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                }

                .bus-text {
                  position: absolute;
                  width: 40px;
                  height: 16px;
                  border-radius: 10px;
                  font-size: 13px;
                  line-height: 16px;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  text-align: center;
                  color: #262626; /* 进度条颜色 */
                  background: #ffffff;
                }
              }

              .bus-middle {
                // display: none;
                position: absolute;
                top: -26px;
                right: 40%;

                .bus-info {
                  position: absolute;
                  width: 40px;
                  height: 20px;

                  img {
                    display: block;
                    width: 100%;
                    height: 100%;
                  }

                  p {
                    position: absolute;
                    font-size: 1.2rem;
                    line-height: 1.4rem;
                    text-align: center;
                    color: #fff;
                    top: 1px;
                    left: 0;
                    right: 0;
                    margin: 0 auto;
                  }
                }

                .bus-congestion {
                  position: absolute;
                  width: 40px;
                  top: -32px;
                  text-align: center;
                  .congestion-info {
                    position: relative;
                    display: inline-block;
                    width: 40px;
                    height: 5px;
                    border-radius: 5px;
                    background: #d6d6d6;
                    overflow: hidden;

                    div {
                      position: absolute;
                      //  width: 80%;
                      height: 5px;
                      border-radius: 5px;
                      top: 0;
                      left: 0;
                      background: red;
                    }
                  }

                  span {
                    display: inline-block;
                    font-size: 1.2rem;
                  }
                }
              }
              .logo {
                position: absolute;
                top: 0;
                left: 0;
                width: 6px;
              }
            }

            > div:first-child {
              flex: none;
            }
          }

          .line-car-bottom {
            margin-top: 40px;

            .line {
              .model {
                float: left;
                position: relative;
                > span {
                  position: absolute;
                  margin-bottom: 15px;
                  left: 0;
                  bottom: 0;
                }
              }

              .model:after {
                content: ' ';
                position: absolute;
                left: 0;
                bottom: 0px;
                right: 0;
                width: 10px;
                height: 10px;
                border-radius: 5px;
                border: 2px solid #2680eb;
                background: white;
                margin: 0 auto;
              }

              .bus {
                // display: none;
                position: absolute;
                bottom: -20px;
                left: calc(0% - 16px);

                .bus-container {
                  position: relative;
                  width: 50px;
                  height: 26px;
                  background: #ffffff;
                  border-radius: 15px; /* 设置长方形四个角的弧度 */
                  overflow: hidden;
                }

                .bus-bar {
                  position: relative;
                  width: 100%;
                  height: 100%;
                  background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                }

                .bus-text {
                  position: absolute;
                  width: 40px;
                  height: 16px;
                  border-radius: 10px;
                  font-size: 13px;
                  line-height: 16px;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  text-align: center;
                  color: #262626; /* 进度条颜色 */
                  background: #ffffff;
                }
              }

              .bus-middle {
                // display: none;
                position: absolute;
                bottom: -10px;
                left: -75%;

                .bus-info {
                  position: absolute;
                  width: 40px;
                  height: 20px;

                  img {
                    display: block;
                    width: 100%;
                    height: 100%;
                  }

                  p {
                    position: absolute;
                    font-size: 1.2rem;
                    line-height: 1.4rem;
                    text-align: center;
                    color: #fff;
                    top: 1px;
                    left: 0;
                    right: 0;
                    margin: 0 auto;
                  }
                }

                .bus-congestion {
                  position: absolute;
                  width: 40px;
                  top: 20px;
                  text-align: center;
                  .congestion-info {
                    position: relative;
                    display: inline-block;
                    width: 40px;
                    height: 5px;
                    border-radius: 5px;
                    background: #d6d6d6;
                    overflow: hidden;

                    div {
                      position: absolute;
                      // width: 80%;
                      height: 5px;
                      border-radius: 5px;
                      top: 0;
                      left: 0;
                      background: red;
                    }
                  }

                  span {
                    display: inline-block;
                    font-size: 1.2rem;
                  }
                }
              }

              .logo {
                position: absolute;
                bottom: 8px;
                right: 0;
                width: 6px;
              }
            }

            > div:last-child {
              flex: none;
            }
          }

          .center-car {
            z-index: 99;
            background: #b8deff;
            height: 100px;
            width: 76%;
            position: absolute;
            // border: 1px #3789fe dashed;
            border-radius: 15px;
            box-sizing: border-box;
            padding: 5px 0;
            overflow: hidden;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
            margin: auto;

            > p {
              display: flex;
              width: 100%;
              line-height: 30px;

              > span {
                font-size: 15px;
                line-height: 30px;
                text-align: center;
                color: #444444;
                flex: 1;
              }
            }
            .route-name {
              div {
                width: 66px;
                height: 30px;
                font-size: 20px;
                line-height: 30px;
                color: #fff;
                background: #1890ff;
                border-radius: 5px 5px 5px 5px;
                margin: 0 auto;
              }
            }
          }

          .top-st {
            width: 18px;
            top: 50%;
            left: 12px;
            position: absolute;
            font-weight: 600;
            transform: translate(0, -50%);
            -webkit-box-orient: vertical;
            writing-mode: vertical-rl;
          }
          .top-st:after {
            content: '';
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2680eb;
            border-radius: 10px;
            position: absolute;
            left: -18px;
            top: 50%;
            transform: translate(0, -50%);
          }

          .bottom-st {
            width: 18px;
            top: 50%;
            right: 12px;
            position: absolute;
            font-weight: 600;
            transform: translate(0, -50%);
          }
          .bottom-st:after {
            content: '';
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2680eb;
            border-radius: 10px;
            position: absolute;
            right: -18px;
            top: 50%;
            transform: translate(0, -50%);
            -webkit-box-orient: vertical;
            writing-mode: vertical-rl;
          }
        }
        .type3 {
        }
        .type4 {
        }
        .type6 {
          margin: 0 10px 0 60px;
        }
        .type7 {
          margin: 0 10px 0 60px;
        }
        .left-z-car {
          position: absolute;
          left: -6px;
          top: 50%;
          transform: translate(0, -50%);
          width: 50px;
          // text-align: center;

          .bus {
            display: block;
            position: relative;
            height: 20px;
            margin: 0 0 10px 0;

            .bus-container {
              position: relative;
              width: 50px;
              height: 26px;
              background: #ffffff;
              border-radius: 15px; /* 设置长方形四个角的弧度 */
              overflow: hidden;
            }

            .bus-bar {
              position: relative;
              width: 100%;
              height: 100%;
              background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
            }

            .bus-text {
              position: absolute;
              width: 40px;
              height: 16px;
              border-radius: 10px;
              font-size: 13px;
              line-height: 16px;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              text-align: center;
              color: #262626; /* 进度条颜色 */
              background: #ffffff;
            }
          }
        }

        .right-z-car {
          position: absolute;
          right: 14px;
          top: 50%;
          transform: translate(0, -50%);
          .bus {
            display: block;
            position: relative;
            height: 20px;
            margin: 0 0 10px 0;

            .bus-container {
              position: relative;
              width: 50px;
              height: 26px;
              background: #ffffff;
              border-radius: 15px; /* 设置长方形四个角的弧度 */
              overflow: hidden;
            }

            .bus-bar {
              position: relative;
              width: 100%;
              height: 100%;
              background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
            }

            .bus-text {
              position: absolute;
              width: 40px;
              height: 16px;
              border-radius: 10px;
              font-size: 13px;
              line-height: 16px;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              text-align: center;
              color: #262626; /* 进度条颜色 */
              background: #ffffff;
            }
          }
        }
      }
    }
    .right-car {
      width: 50%;
      height: 560px;

      .content {
        // overflow: scroll;
        width: 100%;
        height: 560px;
        position: relative;
        display: flex;
        align-items: center;
        left: 0;
        right: 0;
        margin: 0 auto;

        .car-box {
          flex: 1;
          height: 400px;
          margin: 0px 60px 0 0;
          position: relative;

          .top-car {
            position: relative;
            width: 100%;
            height: 50%;

            .top-bg {
              position: absolute;
              width: 100%;
              height: 100%;
              border-top-left-radius: 50px;
              border-top-right-radius: 50px;
              overflow: hidden;
              border: 6px solid #2fcba9;
              border-bottom: none;
            }
          }

          .bottom-car {
            position: relative;
            display: grid;
            width: 100%;
            height: 50%;

            .bottom-bg {
              position: absolute;
              width: 100%;
              height: 100%;
              border-bottom-left-radius: 50px;
              border-bottom-right-radius: 50px;
              overflow: hidden;
              border: 6px solid #2fcba9;
              border-top: none;
            }
          }

          .line-car {
            height: 168px;
            width: 85%;
            margin: 0 auto;
            display: flex;
            position: relative;

            .line {
              flex: 1;
              position: relative;

              .model {
                width: 20px;
                height: 162px;
                line-height: 21px;
                font-size: 14px;
                position: relative;

                > span {
                  width: 100%;
                  // text-align: center;
                  display: -webkit-box;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 7;
                  line-clamp: 7;
                  -webkit-box-orient: vertical;
                  writing-mode: vertical-rl;
                  // text-orientation: upright; /* 保持文字正立，可选 */
                }
              }
            }
          }

          .line-car-top {
            margin-top: 0;

            .line {
              .model {
                float: right;

                > span {
                  margin-top: 15px;
                }
              }

              .model:after {
                content: ' ';
                position: absolute;
                left: 0;
                top: -2px;
                right: 0;
                width: 10px;
                height: 10px;
                border-radius: 5px;
                border: 2px solid #2fcba9;
                background: white;
                margin: 0 auto;
              }

              .bus {
                // display: none;
                position: absolute;
                top: -26px;
                right: calc(0% - 14px);

                .bus-container {
                  position: relative;
                  width: 50px;
                  height: 26px;
                  background: #ffffff;
                  border-radius: 15px; /* 设置长方形四个角的弧度 */
                  overflow: hidden;
                }

                .bus-bar {
                  position: relative;
                  width: 100%;
                  height: 100%;
                  background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                }

                .bus-text {
                  position: absolute;
                  width: 40px;
                  height: 16px;
                  border-radius: 10px;
                  font-size: 13px;
                  line-height: 16px;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  text-align: center;
                  color: #262626; /* 进度条颜色 */
                  background: #ffffff;
                }
              }

              .bus-middle {
                // display: none;
                position: absolute;
                top: -26px;
                right: 40%;

                .bus-info {
                  position: absolute;
                  width: 40px;
                  height: 20px;

                  img {
                    display: block;
                    width: 100%;
                    height: 100%;
                  }

                  p {
                    position: absolute;
                    font-size: 1.2rem;
                    line-height: 1.4rem;
                    text-align: center;
                    color: #fff;
                    top: 1px;
                    left: 0;
                    right: 0;
                    margin: 0 auto;
                  }
                }

                .bus-congestion {
                  position: absolute;
                  width: 40px;
                  top: -32px;
                  text-align: center;
                  .congestion-info {
                    position: relative;
                    display: inline-block;
                    width: 40px;
                    height: 5px;
                    border-radius: 5px;
                    background: #d6d6d6;
                    overflow: hidden;

                    div {
                      position: absolute;
                      //  width: 80%;
                      height: 5px;
                      border-radius: 5px;
                      top: 0;
                      left: 0;
                      background: red;
                    }
                  }

                  span {
                    display: inline-block;
                    font-size: 1.2rem;
                  }
                }
              }
              .logo {
                position: absolute;
                top: 0;
                left: 0;
                width: 6px;
              }
            }

            > div:first-child {
              flex: none;
            }
          }

          .line-car-bottom {
            margin-top: 40px;

            .line {
              .model {
                float: left;
                position: relative;
                > span {
                  position: absolute;
                  margin-bottom: 15px;
                  left: 0;
                  bottom: 0;
                }
              }

              .model:after {
                content: ' ';
                position: absolute;
                left: 0;
                bottom: 0px;
                right: 0;
                width: 10px;
                height: 10px;
                border-radius: 5px;
                border: 2px solid #2fcba9;
                background: white;
                margin: 0 auto;
              }

              .bus {
                // display: none;
                position: absolute;
                bottom: -20px;
                left: calc(0% - 16px);

                .bus-container {
                  position: relative;
                  width: 50px;
                  height: 26px;
                  background: #ffffff;
                  border-radius: 15px; /* 设置长方形四个角的弧度 */
                  overflow: hidden;
                }

                .bus-bar {
                  position: relative;
                  width: 100%;
                  height: 100%;
                  background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
                }

                .bus-text {
                  position: absolute;
                  width: 40px;
                  height: 16px;
                  border-radius: 10px;
                  font-size: 13px;
                  line-height: 16px;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  text-align: center;
                  color: #262626; /* 进度条颜色 */
                  background: #ffffff;
                }
              }

              .bus-middle {
                // display: none;
                position: absolute;
                bottom: -10px;
                left: -75%;

                .bus-info {
                  position: absolute;
                  width: 40px;
                  height: 20px;

                  img {
                    display: block;
                    width: 100%;
                    height: 100%;
                  }

                  p {
                    position: absolute;
                    font-size: 1.2rem;
                    line-height: 1.4rem;
                    text-align: center;
                    color: #fff;
                    top: 1px;
                    left: 0;
                    right: 0;
                    margin: 0 auto;
                  }
                }

                .bus-congestion {
                  position: absolute;
                  width: 40px;
                  top: 20px;
                  text-align: center;
                  .congestion-info {
                    position: relative;
                    display: inline-block;
                    width: 40px;
                    height: 5px;
                    border-radius: 5px;
                    background: #d6d6d6;
                    overflow: hidden;

                    div {
                      position: absolute;
                      // width: 80%;
                      height: 5px;
                      border-radius: 5px;
                      top: 0;
                      left: 0;
                      background: red;
                    }
                  }

                  span {
                    display: inline-block;
                    font-size: 1.2rem;
                  }
                }
              }

              .logo {
                position: absolute;
                bottom: 8px;
                right: 0;
                width: 6px;
              }
            }

            > div:last-child {
              flex: none;
            }
          }

          .center-car {
            z-index: 99;
            background: #bce9f0;
            height: 100px;
            width: 76%;
            position: absolute;
            // border: 1px #3789fe dashed;
            border-radius: 15px;
            box-sizing: border-box;
            padding: 5px 0;
            overflow: hidden;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
            margin: auto;

            > p {
              display: flex;
              width: 100%;
              line-height: 30px;

              > span {
                font-size: 15px;
                line-height: 30px;
                text-align: center;
                color: #444444;
                flex: 1;
              }
            }
            .route-name {
              div {
                width: 66px;
                height: 30px;
                font-size: 20px;
                line-height: 30px;
                color: #fff;
                background: #1bb291;
                border-radius: 5px 5px 5px 5px;
                margin: 0 auto;
              }
            }
          }

          .top-st {
            width: 18px;
            top: 50%;
            left: 12px;
            position: absolute;
            font-weight: 600;
            transform: translate(0, -50%);
            -webkit-box-orient: vertical;
            writing-mode: vertical-rl;
          }
          .top-st:after {
            content: '';
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2fcba9;
            border-radius: 10px;
            position: absolute;
            left: -18px;
            top: 50%;
            transform: translate(0, -50%);
          }

          .bottom-st {
            width: 18px;
            top: 50%;
            right: 12px;
            position: absolute;
            font-weight: 600;
            transform: translate(0, -50%);
            -webkit-box-orient: vertical;
            writing-mode: vertical-rl;
          }
          .bottom-st:after {
            content: '';
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2fcba9;
            border-radius: 10px;
            position: absolute;
            right: -18px;
            top: 50%;
            transform: translate(0, -50%);
          }
        }
        .type3 {
          .top-st:after {
            content: '';
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2fcba9;
            border-radius: 10px;
            position: absolute;
            left: -21px;
            top: 50%;
            transform: translate(0, -50%);
          }
        }
        .type4 {
          .top-st:after {
            content: '';
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2fcba9;
            border-radius: 10px;
            position: absolute;
            left: -21px;
            top: 50%;
            transform: translate(0, -50%);
          }
        }
        .type6 {
          margin: 0px 60px 0 10px;
        }
        .type7 {
          margin: 0px 60px 0 10px;
        }

        .left-z-car {
          position: absolute;
          left: 16px;
          top: 50%;
          transform: translate(0, -50%);
          width: 50px;
          // text-align: center;

          .bus {
            display: block;
            position: relative;
            height: 20px;
            margin: 0 0 10px 0;

            .bus-container {
              position: relative;
              width: 50px;
              height: 26px;
              background: #ffffff;
              border-radius: 15px; /* 设置长方形四个角的弧度 */
              overflow: hidden;
            }

            .bus-bar {
              position: relative;
              width: 100%;
              height: 100%;
              background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
            }

            .bus-text {
              position: absolute;
              width: 40px;
              height: 16px;
              border-radius: 10px;
              font-size: 13px;
              line-height: 16px;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              text-align: center;
              color: #262626; /* 进度条颜色 */
              background: #ffffff;
            }
          }
        }

        .right-z-car {
          position: absolute;
          right: 3px;
          top: 50%;
          transform: translate(0, -50%);
          .bus {
            display: block;
            position: relative;
            height: 20px;
            margin: 0 0 10px 0;

            .bus-container {
              position: relative;
              width: 50px;
              height: 26px;
              background: #ffffff;
              border-radius: 15px; /* 设置长方形四个角的弧度 */
              overflow: hidden;
            }

            .bus-bar {
              position: relative;
              width: 100%;
              height: 100%;
              background: conic-gradient(#ff0000 0% 0%, #ff0000 0% 50%, #b6b6b6 50% 100%, #b6b6b6 100% 100%);
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(0 0 0 0 round 20px); /* 设置进度条四个角的弧度，与容器相同 */
            }

            .bus-text {
              position: absolute;
              width: 40px;
              height: 16px;
              border-radius: 10px;
              font-size: 13px;
              line-height: 16px;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              text-align: center;
              color: #262626; /* 进度条颜色 */
              background: #ffffff;
            }
          }
        }
      }
    }

    .content::-webkit-scrollbar {
      display: none;
    }

    .middleRun {
      animation: stationToMiddle 1s;
    }
    .stationRun {
      animation: middleToStation 1s;
    }

    @keyframes stationToMiddle {
      from {
        right: 31px;
      }
      to {
        right: 40%;
      }
    }

    @keyframes middleToStation {
      from {
        right: calc(100% - 14px);
      }

      to {
        right: calc(0% - 14px);
      }
    }

    .stm {
      animation: downStationToMiddle 1s;
    }
    .mts {
      animation: downMiddleToStation 1s;
    }

    @keyframes downStationToMiddle {
      from {
        left: -10%;
      }

      to {
        left: -75%;
      }
    }

    @keyframes downMiddleToStation {
      from {
        left: calc(100% - 16px);
      }

      to {
        left: calc(0% - 16px);
      }
    }
  }
}
</style>
