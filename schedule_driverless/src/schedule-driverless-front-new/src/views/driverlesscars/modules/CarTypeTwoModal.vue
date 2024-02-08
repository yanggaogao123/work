<template>
  <section id="section">
    <!-- <div class="con-head">
      <div>
        排班计划（当前为 <span>预设计划</span>，最后一次生成计划时间为{{
          centerData.titleMap.lastPlanTime
        }}）
      </div>
      <div>
        总（趟次:{{ centerData.titleMap.totalClasses }} /援10 派车:43/援10
        里程{{ centerData.titleMap.totalRunMileage }}km 工时{{
          centerData.titleMap.totalDuration
        }}h ）
      </div>
      <div class="time-choice">
        <img
          v-if="btnShow"
          @click="playIt()"
          src="@/assets/driverlesscars/play.png"
          alt=""
        />
        <img
          v-else
          @click="stopIt()"
          src="@/assets/driverlesscars/stop.png"
          alt=""
        />
        <a-time-picker v-model="time" format="HH:mm:ss" @change="timeChange" />
      </div>
      <div class="progress-bar">
        <span>播放速度</span>
        <div class="bar">
          <ul class="round">
            <li v-for="(item, index) in speedList" @click="speedChoice(index)">
              <img
                v-show="speed == index"
                src="@/assets/driverlesscars/round.png"
              />
            </li>
          </ul>
          <ul class="num">
            <li>10倍</li>
            <li>20倍</li>
            <li>40倍</li>
          </ul>
        </div>
      </div>
    </div> -->
    <div class="car-content">
      <div class="out-car">
        <div class="content">
          <!-- 左侧总站公交 -->
          <div class="left-z-car">
            <div class="name">{{ routeName }}</div>
            <div
              class="bus"
              style="margin: 0 0 10px 10px"
              v-for="item in downFinalCars"
            >
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
          <div class="car-box">
            <!-- 上行公交 -->
            <div class="top-car">
              <div class="top-bg"></div>
              <div class="line-car line-car-top">
                <div
                  class="line"
                  v-for="(item, i) in arrUp"
                  :style="{ flex: item.stationDistance }"
                  :routeStationId="item.routeStationId"
                  :nextRouteStationId="item.nextRouteStationId"
                  :inShow="item.inShow"
                  :outShow="item.outShow"
                >
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <!-- <div
                    class="bus"
                    v-show="item.cars"
                    :class="{ stationRun: item.cars && !item.cars.haha }"
                  > -->
                  <div class="bus" v-show="item.inShow">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                        #ff0000 0% 0%,
                                        #ff0000 0% ${
                                          item.cars
                                            ? item.cars.fullLoadRatio
                                            : 0
                                        }%,
                                        #b6b6b6 ${
                                          item.cars
                                            ? item.cars.fullLoadRatio
                                            : 0
                                        }% 100%,
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
                                            #F47A55 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #E2CE29 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #5CC065 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{
                          item.cars
                            ? item.cars.busName
                              ? item.cars.busName
                              : item.cars.busNameFull
                            : ""
                        }}
                      </div>
                    </div>
                  </div>
                  <div class="bus-middle" v-show="item.outShow">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                        #ff0000 0% 0%,
                                        #ff0000 0% ${
                                          item.cars
                                            ? item.cars.fullLoadRatio
                                            : 0
                                        }%,
                                        #b6b6b6 ${
                                          item.cars
                                            ? item.cars.fullLoadRatio
                                            : 0
                                        }% 100%,
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
                                            #F47A55 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #E2CE29 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #5CC065 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{
                          item.cars
                            ? item.cars.busName
                              ? item.cars.busName
                              : item.cars.busNameFull
                            : ""
                        }}
                      </div>
                    </div>
                  </div>
                  <img
                    class="logo"
                    src="@/assets/driverlesscars/goRight.png"
                    alt=""
                  />
                </div>
              </div>
            </div>
            <!-- <div class="center-car">
                            <p>
                                <span>上行运行时间: {{ centerData.upTime }}</span>
                                <span>920路</span>
                                <span>下行运行时间: {{ centerData.downTime }}</span>
                            </p>
                            <p>
                                <span>总配车数: {{ centerData.allCars }}</span
                                ><span>单班车数: {{ centerData.singleCars }}</span
                                ><span>双班车数: {{ centerData.doubleCars }}</span>
                            </p>
                            <p>
                                <span>计划支援总班次数：3</span><span>支援开始时间：17 : 00</span
                                ><span>支援结束时间：17 : 00</span>
                            </p>
                        </div> -->
            <!-- 下行公交 -->
            <div class="bottom-car">
              <div class="bottom-bg"></div>
              <div class="line-car line-car-bottom">
                <div
                  class="line"
                  v-for="(item, index) in arrDown"
                  :style="{ flex: item.stationDistance }"
                  :routeStationId="item.routeStationId"
                  :nextRouteStationId="item.nextRouteStationId"
                >
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div class="bus" v-show="item.inShow">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #F47A55 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #E2CE29 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #5CC065 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{
                          item.cars
                            ? item.cars.busName
                              ? item.cars.busName
                              : item.cars.busNameFull
                            : ""
                        }}
                      </div>
                    </div>
                  </div>
                  <div class="bus-middle" v-show="item.outShow">
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #F47A55 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #E2CE29 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #5CC065 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{
                          item.cars
                            ? item.cars.busName
                              ? item.cars.busName
                              : item.cars.busNameFull
                            : ""
                        }}
                      </div>
                    </div>
                  </div>

                  <img
                    class="logo"
                    src="@/assets/driverlesscars/goLeft.png"
                    alt=""
                  />
                </div>
              </div>
            </div>
            <!-- 总站标识 -->
            <div class="top-st" v-if="[1, 5].includes(carType)">
              {{ arrUp[0].routeStationName }}
            </div>
            <div class="bottom-st" v-if="[0, 5].includes(carType)">
              {{ arrUp[arrUp.length - 1].routeStationName }}
            </div>
            <!-- <div class="bottom-st"></div> -->
          </div>

          <!-- 右侧总站公交 -->
          <div class="right-z-car">
            <div class="name">{{ routeName }}</div>
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
      <div class="in-car" :class="`type${carType}`">
        <div class="content">
          <!-- 左侧总站公交 -->
          <div class="left-z-car">
            <div class="name">{{ supRouteName }}</div>
            <div
              class="bus"
              style="margin: 0 0 10px 10px"
              v-for="item in downFinalCarsTwo"
            >
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
          <div class="car-box">
            <!-- 上行公交 -->
            <div class="top-car">
              <div class="top-bg"></div>
              <div class="line-car line-car-top">
                <div
                  class="line"
                  v-for="item in arrUpTwo"
                  :style="{ flex: item.stationDistance }"
                  :routeStationId="item.routeStationId"
                >
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div
                    class="bus"
                    v-show="item.cars"
                    :class="{ stationRun: item.cars && !item.cars.haha }"
                  >
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #F47A55 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #E2CE29 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #5CC065 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
                                            #b6b6b6 100% 100%
                                        )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{
                          item.cars
                            ? item.cars.busName
                              ? item.cars.busName
                              : item.cars.busNameFull
                            : ""
                        }}
                      </div>
                    </div>
                  </div>
                  <img
                    class="logo"
                    src="@/assets/driverlesscars/goRight.png"
                    alt=""
                  />
                </div>
              </div>
            </div>
            <div class="center-car">
              <div class="cen-left">
                <span>{{ routeName }}</span>
                <span>{{ supRouteName }}</span>
              </div>
              <div class="cen-right">
                <ul>
                  <li>上行运行时间</li>
                  <li>下行运行时间</li>
                  <li>总配车数</li>
                  <li>单班车数</li>
                  <li>双班车数</li>
                  <li>计划支援总班次数</li>
                  <li>支援开始时间</li>
                  <li>支援结束时间</li>
                </ul>
                <ul class="cen-car1">
                  <li>
                    {{
                      `${centerData.mainMap.upBeginTime}-${centerData.mainMap.upEndTime}`
                    }}
                  </li>
                  <li>
                    {{
                      `${centerData.mainMap.downBeginTime}-${centerData.mainMap.downEndTime}`
                    }}
                  </li>
                  <li>{{ centerData.mainMap.totalBusNum }}</li>
                  <li>{{ centerData.mainMap.singleBusNum }}</li>
                  <li>{{ centerData.mainMap.doubleBusNum }}</li>
                  <li>{{ centerData.mainMap.totalSupportClasses }}</li>
                  <li>{{ centerData.mainMap.supportBeginTime }}</li>
                  <li>{{ centerData.mainMap.supportEndTime }}</li>
                </ul>
                <ul class="cen-car2">
                  <li>
                    {{
                      `${centerData.subMap.upBeginTime}-${centerData.subMap.upEndTime}`
                    }}
                  </li>
                  <li>
                    {{
                      `${centerData.subMap.downBeginTime}-${centerData.subMap.downEndTime}`
                    }}
                  </li>
                  <li>{{ centerData.subMap.totalBusNum }}</li>
                  <li>{{ centerData.subMap.singleBusNum }}</li>
                  <li>{{ centerData.subMap.doubleBusNum }}</li>
                  <li>{{ centerData.subMap.totalSupportClasses }}</li>
                  <li>{{ centerData.subMap.supportBeginTime }}</li>
                  <li>{{ centerData.subMap.supportEndTime }}</li>
                </ul>
              </div>
            </div>
            <!-- 下行公交 -->
            <div class="bottom-car">
              <div class="bottom-bg"></div>
              <div class="line-car line-car-bottom">
                <div
                  class="line"
                  v-for="(item, index) in arrDownTwo"
                  :style="{ flex: item.stationDistance }"
                  :routeStationId="item.routeStationId"
                >
                  <div class="model">
                    <span>{{ item.routeStationName }}</span>
                  </div>
                  <div
                    class="bus mts"
                    v-show="item.cars"
                    :class="{ mts: item.cars && !item.cars.haha }"
                  >
                    <div class="bus-container">
                      <div
                        v-if="item.cars && item.cars.fullLoadRatio >= 90"
                        class="bus-bar"
                        :style="{
                          background: `conic-gradient(
                                            #ff0000 0% 0%,
                                            #ff0000 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                            #F47A55 0% ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }%,
                                            #b6b6b6 ${
                                              item.cars
                                                ? item.cars.fullLoadRatio
                                                : 0
                                            }% 100%,
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
                                          #E2CE29 0% ${
                                            item.cars
                                              ? item.cars.fullLoadRatio
                                              : 0
                                          }%,
                                          #b6b6b6 ${
                                            item.cars
                                              ? item.cars.fullLoadRatio
                                              : 0
                                          }% 100%,
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
                                          #5CC065 0% ${
                                            item.cars
                                              ? item.cars.fullLoadRatio
                                              : 0
                                          }%,
                                          #b6b6b6 ${
                                            item.cars
                                              ? item.cars.fullLoadRatio
                                              : 0
                                          }% 100%,
                                          #b6b6b6 100% 100%
                                      )`,
                        }"
                      ></div>
                      <div class="bus-text">
                        {{
                          item.cars
                            ? item.cars.busName
                              ? item.cars.busName
                              : item.cars.busNameFull
                            : ""
                        }}
                      </div>
                    </div>
                  </div>

                  <img
                    class="logo"
                    src="@/assets/driverlesscars/goLeft.png"
                    alt=""
                  />
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
            <div class="name">{{ supRouteName }}</div>
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
import Vue from "vue";
import axios from "axios";
import "@/assets/less/base.css";
import {
  getAction,
  postAction,
  getActionNew,
  postActionNew,
} from "@/api/manage";
// import { mixinDevice } from '@/utils/mixin';
import moment from "moment";
import busData from "./busData.json";
// import stationOne from './stationOne.json';
// import stationTwo from './stationTwo.json';
export default {
  name: "CarTypeOneModal",
  props: ["sendData"],
  data() {
    return {
      url: {
        adrealInfo: `${process.env.VUE_APP_BUS_API}/simulation/adrealInfo`,
        getListByRouteId: `${process.env.VUE_APP_BUS_API}/routeStation/getListByRouteId`,
        getRuningScheduleConfig: `${process.env.VUE_APP_BUS_API}/schedule/getRuningScheduleConfig`,
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
      },
      mes: "",
      // 0:共首站，1:共末站，2:共首末站，3:首站为支援线路末站，4:末站为支援线路首站，5:首末站相邻
      carType: 2,
      routeId: "",
      routeName: "",
      supRouteId: "",
      supRouteName: "",
      runDate: "",
      time: "",
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
      centerData: {
        titleMap: {},
        mainMap: {},
        subMap: {},
      },
      // 当前时间段的车辆数据
      needArr: [],
      needBusCodeArr: [],
      // 没有运行的车辆
      noNeedArr: [],
      // 进出站定时器
      timer: "",
      // 时间定时器
      timeCounter: "",
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
      centerDataTwo: {
        titleMap: null,
        mainMap: null,
        subMap: null,
      },
      // 当前时间段的车辆数据
      needArrTwo: [],
      needBusCodeArrTwo: [],
      // 没有运行的车辆
      noNeedArrTwo: [],
      // 进出站定时器
      timerTwo: "",
      // 时间定时器
      timeCounterTwo: "",
      // 开始停止按钮
      btnShowTwo: true,
      // 下行总站停靠车辆
      downFinalCarsTwo: [],
      // 上行总站停靠车辆
      upFinalCarsTwo: [],

      /*****************************/

      // 数据储存
      // 主线
      runBusInfo: [],
      MonitorInfo: [],
      supportInfo: [],
      // 关联线
      runBusInfoTwo: [],
      MonitorInfoTwo: [],
      supportInfoTwo: [],
    };
  },
  watch: {
    sendData() {
      console.log("sendData", this.sendData);
      this.centerData = this.sendData.centerData;
      this.runDate = this.sendData.runDate;
      this.time = moment(this.sendData.time, "HH:mm:ss");
      this.timeStr = this.sendData.time;
      this.routeId = this.sendData.routeId;
      this.routeName = this.sendData.routeName;
      this.supRouteId = this.sendData.supRouteId;
      this.supRouteName = this.sendData.supRouteName;
      this.carType = this.sendData.busRunData.data.simulationType;
      console.log(this.carType);
      this.getListByRouteId();
      this.getListByRouteId2();
      this.getMonitorInfo();
    },
  },
  created() {
    const queryString = window.location.search;
    const searchParams = new URLSearchParams(queryString);
    const paramString = searchParams.get("paramString");
    console.log(paramString);
    this.mes = new URLSearchParams();
    this.mes.append("paramString", paramString);
    console.log(this.mes.toString());
  },
  mounted() {
    // this.adrealInfo();
    // this.adrealInfo2();
  },
  methods: {
    /****************************/
    //查询线路站点
    getListByRouteId() {
      let that = this;
      let params = this.res;
      axios
        .get(`${this.url.getListByRouteId}/${this.routeId}`, "", { params })
        .then((res) => {
          console.log("stationOne", res.data.data);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          var allArr = res.data.data;
          // 上行
          var arr1 = [];
          for (var i = 0; i < allArr.length; i++) {
            if (
              allArr[i].stationMark == "0" ||
              allArr[i].stationMark == "1" ||
              allArr[i].stationMark == "2"
            ) {
              arr1.push(allArr[i]);
            }
          }

          arr1[0].stationDistance = 0;

          for (let i = 0; i < arr1.length - 1; i++) {
            // 将后一组的 routeStationId 存储到前一组的 nextRouteStationId 属性中
            arr1[i].nextRouteStationId = arr1[i + 1].routeStationId;
          }
          // arr1[arr1.length - 1].stationDistance = 0;
          // console.log(arr1[1]);
          that.arrUp = arr1;
          that.upStation = arr1[arr1.length - 1].routeStationId;
          // 下行
          var arr2 = [];
          for (var i = 0; i < allArr.length; i++) {
            if (
              allArr[i].stationMark == "3" ||
              allArr[i].stationMark == "4" ||
              allArr[i].stationMark == "5"
            ) {
              arr2.push(allArr[i]);
            }
          }
          arr2.reverse();
          console.log("arr2", arr2);
          for (let i = 0; i < arr2.length - 1; i++) {
            // 将后一组的 routeStationId 存储到前一组的 nextRouteStationId 属性中
            arr2[i].nextRouteStationId = arr2[i + 1].routeStationId;
          }
          // console.log("ARR2", arr2);
          that.arrDown = arr2;
          that.downStation = arr2[0].routeStationId;
        });
    },
    getListByRouteId2() {
      let that = this;
      let params = this.res;
      axios
        .get(`${this.url.getListByRouteId}/${this.supRouteId}`, "", { params })
        .then((res) => {
          console.log("stationTwo", res.data.data);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          var allArr = res.data.data;
          // 上行
          var arr1 = [];
          for (var i = 0; i < allArr.length; i++) {
            if (
              allArr[i].stationMark == "0" ||
              allArr[i].stationMark == "1" ||
              allArr[i].stationMark == "2"
            ) {
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
            if (
              allArr[i].stationMark == "3" ||
              allArr[i].stationMark == "4" ||
              allArr[i].stationMark == "5"
            ) {
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

    getMonitorInfo() {
      let params = this.mes;
      let data = {
        routeId: this.routeId,
        runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
      };
      axios.post(`${this.url.getMonitorInfo}`, data, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.runBusInfo = res.data.runBus;
        this.arrUp.forEach((item) => {
          // 进站
          const matchRunBusInfo = this.runBusInfo.find((runItem) => {
            return (
              runItem.routeStationId == item.routeStationId &&
              runItem.adFlag == "0"
            );
          });
          if (matchRunBusInfo) {
            item.runBusInfo = matchRunBusInfo;
            // item.inShow = true;
            Vue.set(item, "inShow", true);
          } else {
            // item.inShow = false;
            Vue.set(item, "inShow", false);
          }

          // 出站
          const matchRunBusInfoOut = this.runBusInfo.find((runItem) => {
            return (
              runItem.routeStationId == item.nextRouteStationId &&
              runItem.adFlag == "1"
            );
          });
          if (matchRunBusInfoOut) {
            item.runBusInfo = matchRunBusInfoOut;
            Vue.set(item, "outShow", true);
          } else {
            Vue.set(item, "outShow", false);
          }
        });
        console.log("arrUp", JSON.parse(JSON.stringify(this.arrUp)));
        this.arrDown.forEach((item) => {
          // 进站
          const matchRunBusInfo = this.runBusInfo.find((runItem) => {
            return (
              runItem.routeStationId == item.routeStationId &&
              runItem.adFlag == "0"
            );
          });
          if (matchRunBusInfo) {
            item.runBusInfo = matchRunBusInfo;
            Vue.set(item, "inShow", true);
          } else {
            Vue.set(item, "inShow", false);
          }

          // 出站
          const matchRunBusInfoOut = this.runBusInfo.find((runItem) => {
            return (
              runItem.routeStationId == item.nextRouteStationId &&
              runItem.adFlag == "1"
            );
          });
          if (matchRunBusInfoOut) {
            item.runBusInfo = matchRunBusInfoOut;
            Vue.set(item, "outShow", true);
          } else {
            Vue.set(item, "outShow", false);
          }
        });
        console.log("arrDown", JSON.parse(JSON.stringify(this.arrDown)));
        this.MonitorInfo = res.data.info;
        this.supportInfo = res.data.supportInfo;
        console.log("runBusInfo", this.runBusInfo);
      });
    },
    getMonitorInfo2() {},
  },
};
</script>

<style lang="less" scoped>
section {
  background: #dbefff;
  .con-head {
    // margin: 12px 0 0 0;
    padding: 12px;
    div {
      display: inline-block;
      font-size: 14px;
      margin-right: 12px;
    }
    div:nth-child(1) {
      span {
        display: inline-block;
        font-size: 16px;
        font-weight: 500;
      }
    }
    div:nth-child(2) {
      display: inline-block;
      font-size: 16px;
      font-weight: 500;
    }
    .time-choice {
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
    // display: flex;
    // flex-wrap: nowrap;
    position: relative;
    .out-car {
      width: 100%;
      height: 660px;

      .content {
        // overflow: scroll;
        width: 100%;
        height: 100%;
        position: relative;
        display: flex;
        align-items: center;
        left: 0;
        right: 0;
        margin: 0 auto;

        .car-box {
          flex: 1;
          height: 560px;
          margin: 0 190px;
          position: relative;

          .top-car {
            position: relative;
            width: 100%;
            height: 50%;

            .top-bg {
              position: absolute;
              width: 100%;
              height: 100%;
              border-top-left-radius: 100px;
              border-top-right-radius: 100px;
              overflow: hidden;
              border: 6px solid #2796fd;
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
              border-bottom-left-radius: 100px;
              border-bottom-right-radius: 100px;
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
                  font-size: 12px;
                }
              }

              .model:after {
                content: " ";
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
                  background: conic-gradient(
                    #ff0000 0% 0%,
                    #ff0000 0% 50%,
                    #b6b6b6 50% 100%,
                    #b6b6b6 100% 100%
                  );
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(
                    0 0 0 0 round 20px
                  ); /* 设置进度条四个角的弧度，与容器相同 */
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
                right: calc(50% - 14px);

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
                  background: conic-gradient(
                    #ff0000 0% 0%,
                    #ff0000 0% 50%,
                    #b6b6b6 50% 100%,
                    #b6b6b6 100% 100%
                  );
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(
                    0 0 0 0 round 20px
                  ); /* 设置进度条四个角的弧度，与容器相同 */
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
            margin-top: 120px;

            .line {
              .model {
                float: left;
                position: relative;
                > span {
                  position: absolute;
                  margin-bottom: 15px;
                  font-size: 12px;
                  left: 0;
                  bottom: 0;
                }
              }

              .model:after {
                content: " ";
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
                  background: conic-gradient(
                    #ff0000 0% 0%,
                    #ff0000 0% 50%,
                    #b6b6b6 50% 100%,
                    #b6b6b6 100% 100%
                  );
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(
                    0 0 0 0 round 20px
                  ); /* 设置进度条四个角的弧度，与容器相同 */
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
                bottom: -20px;
                left: calc(50% - 16px);

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
                  background: conic-gradient(
                    #ff0000 0% 0%,
                    #ff0000 0% 50%,
                    #b6b6b6 50% 100%,
                    #b6b6b6 100% 100%
                  );
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(
                    0 0 0 0 round 20px
                  ); /* 设置进度条四个角的弧度，与容器相同 */
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
            content: "";
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
            content: "";
            width: 18px;
            height: 18px;
            background: #fff;
            border: 2px solid #2680eb;
            border-radius: 10px;
            position: absolute;
            right: -18px;
            top: 50%;
            transform: translate(0, -50%);
          }
        }

        .left-z-car {
          position: absolute;
          left: 50px;
          top: 20px;
          width: 50px;
          // text-align: center;

          .name {
            font-size: 16px;
            line-height: 28px;
            width: 50px;
            text-align: center;
            margin: 0 0 0 10px;
            color: #2680eb;
          }
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
              background: conic-gradient(
                #ff0000 0% 0%,
                #ff0000 0% 50%,
                #b6b6b6 50% 100%,
                #b6b6b6 100% 100%
              );
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(
                0 0 0 0 round 20px
              ); /* 设置进度条四个角的弧度，与容器相同 */
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
          right: 50px;
          top: 20px;

          .name {
            font-size: 16px;
            line-height: 28px;
            width: 50px;
            text-align: center;
            // margin: 0 0 0 10px;
            color: #2680eb;
          }
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
              background: conic-gradient(
                #ff0000 0% 0%,
                #ff0000 0% 50%,
                #b6b6b6 50% 100%,
                #b6b6b6 100% 100%
              );
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(
                0 0 0 0 round 20px
              ); /* 设置进度条四个角的弧度，与容器相同 */
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
    .in-car {
      position: absolute;
      width: 100%;
      height: 330px;
      top: 50%;
      left: 0;
      transform: translate(0, -50%);

      .content {
        // overflow: scroll;
        width: 100%;
        height: 100%;
        position: relative;
        display: flex;
        align-items: center;
        left: 0;
        right: 0;
        margin: 0 auto;

        .car-box {
          flex: 1;
          height: 260px;
          margin: 0px 190px;
          position: relative;

          .top-car {
            position: relative;
            width: 100%;
            height: 50%;

            .top-bg {
              position: absolute;
              width: 100%;
              height: 100%;
              border-top-left-radius: 100px;
              border-top-right-radius: 100px;
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
              border-bottom-left-radius: 100px;
              border-bottom-right-radius: 100px;
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
                position: relative;
                font-size: 12px;

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
                  font-size: 12px;
                }
              }

              .model:after {
                content: " ";
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
                  background: conic-gradient(
                    #ff0000 0% 0%,
                    #ff0000 0% 50%,
                    #b6b6b6 50% 100%,
                    #b6b6b6 100% 100%
                  );
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(
                    0 0 0 0 round 20px
                  ); /* 设置进度条四个角的弧度，与容器相同 */
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
            margin-top: -30px;

            .line {
              .model {
                float: left;
                position: relative;
                > span {
                  position: absolute;
                  margin-bottom: 15px;
                  left: 0;
                  bottom: 0;
                  font-size: 12px;
                }
              }

              .model:after {
                content: " ";
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
                  background: conic-gradient(
                    #ff0000 0% 0%,
                    #ff0000 0% 50%,
                    #b6b6b6 50% 100%,
                    #b6b6b6 100% 100%
                  );
                  border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
                  clip-path: inset(
                    0 0 0 0 round 20px
                  ); /* 设置进度条四个角的弧度，与容器相同 */
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
            width: 85%;
            height: 110px;
            position: absolute;
            box-sizing: border-box;
            padding: 5px 0;
            left: 0;
            right: 0;
            top: 50%;
            margin: 0 auto;
            transform: translate(0, -50%);
            display: flex;
            .cen-left {
              margin: 33px 10px 0 10px;
              span {
                display: block;
                width: 67px;
                height: 30px;
                background: #1890ff;
                border-radius: 5px 5px 5px 5px;
                opacity: 1;
                color: #fff;
                font-size: 20px;
                line-height: 30px;
                text-align: center;
              }
              span:nth-child(2) {
                margin-top: 10px;
                background: #1bb291;
              }
            }
            .cen-right {
              width: calc(100% - 77px);
              ul {
                display: flex;
                width: 100%;
                box-sizing: border-box;
                padding: 0 20px;
                justify-content: space-between;
                text-align: center;
                margin-bottom: 10px;
                li {
                  width: 12.5%;
                }
              }
              .cen-car1,
              .cen-car2 {
                height: 34px;
                line-height: 34px;
                font-size: 18px;
                border-radius: 17px;
                // font-weight: 800;
              }
              .cen-car1 {
                background: #c4e4ff;
              }
              .cen-car2 {
                background: #c8eff0;
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
            content: "";
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
          }
          .bottom-st:after {
            content: "";
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

        .left-z-car {
          position: absolute;
          left: 50px;
          top: 70%;
          // transform: translate(0, -50%);
          width: 50px;
          // text-align: center;
          .name {
            font-size: 16px;
            line-height: 28px;
            width: 50px;
            text-align: center;
            margin: 0 0 0 10px;
            color: #1bb291;
          }
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
              background: conic-gradient(
                #ff0000 0% 0%,
                #ff0000 0% 50%,
                #b6b6b6 50% 100%,
                #b6b6b6 100% 100%
              );
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(
                0 0 0 0 round 20px
              ); /* 设置进度条四个角的弧度，与容器相同 */
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
          right: 50px;
          top: 70%;
          // transform: translate(0, -50%);
          .name {
            font-size: 16px;
            line-height: 28px;
            width: 50px;
            text-align: center;
            // margin: 0 0 0 10px;
            color: #1bb291;
          }
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
              background: conic-gradient(
                #ff0000 0% 0%,
                #ff0000 0% 50%,
                #b6b6b6 50% 100%,
                #b6b6b6 100% 100%
              );
              border-radius: 20px; /* 设置进度条四个角的弧度，与容器相同 */
              clip-path: inset(
                0 0 0 0 round 20px
              ); /* 设置进度条四个角的弧度，与容器相同 */
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
    .type0 {
      .content {
        .car-box {
          margin: 0 calc(190px + 5%) 0 190px;
        }
      }
    }
    .type1 {
      .content {
        .car-box {
          margin: 0 190px 0 calc(190px + 5%);
        }
      }
    }
    .type2 {
      .content {
        .car-box {
          margin: 0 190px;
        }
      }
    }
    .type5 {
      .content {
        .car-box {
          margin: 0 calc(190px + 5%);
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
