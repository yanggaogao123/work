<template>
  <div class="map-modules" v-show="visibility">
    <div class="close" @click="closeModule"><img src="../../../assets/driverlesscars/map-btn-close.png" alt=""></div>
    <div class="bg"><img src="../../../assets/driverlesscars/big.png" alt=""></div>
    <div class="left-aside">
      <div class="route-station">
        <div class="container">
          <div class="top">
            <img src="../../../assets/driverlesscars/map-left-bg.png" alt="">
            <div class="text">线路支援计划</div>
          </div>
          <div class="content">
            <div class="row">
              <div class="label">当前线路</div>
              <div class="val">{{ supportPlanData.routeName ? supportPlanData.routeName : '-' }}</div>
            </div>
            <div class="row">
              <div class="l">
                <div class="val">{{ supportPlanData.routeName ? supportPlanData.routeName : '-' }}</div>
              </div>
              <div class="arrow">
                支援
                <div class="arrow-r"></div>
                <div class="arrow-l"></div>
              </div>
              <div class="r">
                <div class="val" style="cursor: pointer;" @click="showSupportBus">{{ supportPlanData.supportRouteName ?
    supportPlanData.supportRouteName : '-' }}</div>
              </div>
            </div>
            <div class="row">
              <div class="l">
                <div class="label">支援开始时间</div>
                <div class="val">{{ supportPlanData.curStartTime ? supportPlanData.curStartTime : '--:--' }}
                </div>
              </div>
              <div class="r">
                <div class="label">支援结束时间</div>
                <div class="val">{{ supportPlanData.curEndTime ? supportPlanData.curEndTime : '--:--' }}</div>
              </div>
            </div>
            <div class="row">
              <div class="label">已支援</div>
              <div class="val">{{ supportPlanData.supported ? supportPlanData.supported : 0 }} <span>躺</span></div>
            </div>
            <div class="row">
              <div class="label">待支援</div>
              <div class="val">{{ supportPlanData.unsupported ? supportPlanData.unsupported : 0 }} <span>躺</span></div>
            </div>
            <!-- <div class="row">
              <div class="label">正在支援</div>
              <div class="val">{{ supportPlanList.length > 0 ? supportPlanList.length : 0 }} <span>躺</span></div>
            </div> -->
          </div>
        </div>
        <div class="container">
          <div class="top">
            <img src="../../../assets/driverlesscars/map-left-bg.png" alt="">
            <div class="text">车辆支援计划</div>
          </div>
          <div class="content">
            <div class="row">
              <div class="label">当前车</div>
              <div class="val">{{ supportPlanData.curBusName }}</div>
            </div>
            <div class="row">
              <div class="label">任务计划</div>
            </div>
            <div class="row">
              <div class="l">
                <div class="val">{{ supportPlanData.supportStartTime ? supportPlanData.supportStartTime : '--:--' }}
                </div>
                <div class="label">开始</div>
              </div>
              <div class="arrow">
                <div class="arrow-r"></div>
              </div>
              <div class="r">
                <div class="val">{{ supportPlanData.supportEndTime ? supportPlanData.supportEndTime : '--:--' }}</div>
                <div class="label">结束</div>
              </div>
            </div>
            <div class="row">
              <div class="label">到达始发站</div>
              <div class="val" style="max-width: 65%;">{{ supportPlanData.firstRouteStaName }}</div>
            </div>
            <!-- <div class="row">
              <div class="label">该车支援总趟次</div>
              <div class="val">- <span>躺</span></div>
            </div> -->
          </div>
        </div>
      </div>
      <div class="route-inf" v-show="showSupportBusList">
        <div class="close" @click="hideSupportBus"><img src="../../../assets/driverlesscars/map-btn-close.png" alt="">
        </div>
        <div class="bg"><img src="../../../assets/driverlesscars/bg-route-inf.png" alt=""></div>
        <div class="content">
          <div class="top">
            <div class="tab active">{{ supportPlanData.supportRouteName ? supportPlanData.supportRouteName : '-' }}
            </div>
            <!-- <div class="tab">456路</div> -->
          </div>
          <div class="table">
            <div class="header">
              <div class="col">开始支援</div>
              <div class="col">结束始支援</div>
              <div class="col">车号</div>
              <div class="col">任务</div>
              <div class="col">方向</div>
              <div class="col"> </div>
            </div>
            <div class="body">
              <div class="row" v-for="item, i in supportPlanList" :key="i">
                <div class="col">{{ item.planTime.split(' ')[1].slice(0, -3) }}</div>
                <div class="col">{{ item.tripEndTime.split(' ')[1].slice(0, -3) }}</div>
                <div class="col">{{ item.busName }}</div>
                <div class="col">{{ item.serviceName }}</div>
                <div class="col">{{ item.direction ? '下行' : '上行' }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div id="map-container">
      <div class="station-name" ref="stationName" v-show="showName">{{ selectStation.stationName }}</div>
      <div class="bus-inf" ref="busInf" v-show="showBusInf">
        <!-- <div class="close" @click="hideBusInf"><img src="../../../assets/driverlesscars/map-btn-close.png" alt=""></div> -->
        <div class="bg"><img src="../../../assets/driverlesscars/tankuang.png" alt=""></div>
        <div class="content">
          <div class="box">

            <div class="l">
              <div class="pic-box">
                <img src="../../../assets/driverlesscars/bus-default.png" alt="">
              </div>
              <div class="row">
                <div class="label">支援任务</div>
                <div class="val">{{ supportPlanData.supportRouteName }}</div>
              </div>
              <div class="row">
                <div class="label">预计到达</div>
                <div class="val">{{ supportPlanData.arriveTime }}</div>
              </div>
            </div>
            <div class="r">
              <div class="row">
                <div class="label">车号</div>
                <div class="val">{{ supportPlanData.curBusName }}</div>
              </div>
              <!-- <div class="row">
              <div class="label">剩余电量</div>
              <div class="val">2 <span>%</span></div>
            </div> -->
              <div class="row">
                <div class="label">SOC</div>
                <div class="val">{{ supportPlanData.soc }} <span>%</span></div>
              </div>
              <div class="row">
                <div class="label">核载人数</div>
                <div class="val">{{ supportPlanData.supportPassengerNum }}<span>人</span></div>
              </div>
              <div class="row">
                <div class="label">驾驶员</div>
                <div class="val">{{ supportPlanData.employeeName }}</div>
              </div>
              <div class="row">
                <div class="label">距离始发站</div>
                <div class="val">{{ supportPlanData.distance }} <span>km</span></div>
              </div>
              <!-- <div class="row">
              <div class="label">距离总站</div>
              <div class="val">5 <span>站</span></div>
            </div> -->
            </div>
          </div>
          <div class="row">
            <div class="label">到达站点</div>
            <div class="val">{{ supportPlanData.firstRouteStaName }}</div>
          </div>
        </div>
      </div>
      <div class="zoom-content">
        <div class="btn" @click="changeMapZoom(1)">+</div>
        <div class="number">{{ mapZoom }}</div>
        <div class="btn" @click="changeMapZoom(0)">-</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import AMapLoader from '@amap/amap-jsapi-loader';
import moment from 'moment';
import icon_bus from '../../../assets/driverlesscars/car.png'
export default {
  components: {},
  props: {
    sendData: Object,
    visibility: Boolean,
  },
  data() {
    return {
      map: null,
      stationMarkers: [],
      busMarkers: [],
      routePolyline: null,
      busInf: null,
      busPositionInf: {},
      showBusInf: false,
      stationInf: null,
      routeList: [],
      mapZoom: 6,
      showName: false,
      selectStation: {
        stationName: ''
      },
      supportPlanData: {},
      supportPlanList: [],// 左侧信息
      timer: null,
      showSupportBusList: false,
      url: {
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
        getGisRoadInfo: `${process.env.VUE_APP_BUS_API}/schedule/getGisRoadInfo`,
        getSchedulePlanDetail: `${process.env.VUE_APP_BUS_API}/schedule/getSchedulePlanDetail`,
      },
    };
  },
  watch: {
    visibility(nv, ov) {
      if (nv) {
        if (this.map) {
          this.map.resize()
          this.getMonitorInfo()
          this.setTimer()
          this.getGisRoadInfo()
          this.getSchedulePlanDetail()
        } else {
          this.$nextTick(() => {
            this.initAMap();
          })
        }
      } else {
        if (this.timer) {
          clearInterval(this.timer)
        }
        this.map.destroy()
        this.map = null
      }
    }
  },
  computed: {
  },
  created() {
  },
  mounted() {
    // this.$nextTick(() => {
    //   this.initAMap();
    // })
  },
  methods: {
    initAMap() {
      console.log('sendData', this.$props.sendData)
      let that = this
      AMapLoader.load({
        key: "aa76ad84f92f661980f710cbe966b7f6", // 申请好的Web端开发者Key，首次调用 load 时必填
        version: "2.0", // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
      })
        .then((AMap) => {
          this.map = new AMap.Map("map-container", {
            // 设置地图容器id
            viewMode: "3D", // 是否为3D地图模式
            zoom: 12, // 初始化地图级别
            // rotation: 0, // 地图平面旋转
            // pitch: 0, // 地图视角
            zooms: [6, 20],
            mapStyle: 'amap://styles/darkblue',
            center: [113.312529, 23.12387] // 初始化地图中心点位置
          });
          this.map.on('zoomend', () => {
            that.mapZoom = that.map.getZoom()
          });
          this.busInf = new AMap.InfoWindow({
            isCustom: true,
            closeWhenClickMap: true,
            content: this.$refs.busInf,
            anchor: 'bottom-center',
            offset: new AMap.Pixel(0, -42)
          })
          // this.stationInf = new AMap.InfoWindow({
          //   isCustom: true,
          //   content: this.$refs.stationName,
          //   anchor: 'bottom-center',
          //   offset: new AMap.Pixel(0, -10)
          // })
          this.$nextTick(() => {
            // this.$refs.searchModel.getRouteTreeOptions()
            // this.loadStationMarker([{ lng: 113.397428, lat: 23.30923 }, { lng: 113.312529, lat: 23.12387 }])
            // this.loadBusMarker([{ lng: 113.312529, lat: 23.12587 }])
            // this.loadRouteLine([[113.397428, 23.30923], [113.312529, 23.12387]])
            // 调用接口，然后渲染
            if (this.visibility) {
              this.getMonitorInfo()
              this.setTimer()
              this.getGisRoadInfo()
              this.getSchedulePlanDetail()
            }
          })
        })
        .catch((e) => {
          console.log(e);
        });
    },
    changeMapZoom(type) {
      if (type == 1) {
        if (this.mapZoom >= 20) return
        this.mapZoom += 1
      } else {
        if (this.mapZoom <= 6) return
        this.mapZoom -= 1
      }
      this.map.setZoom(this.mapZoom)
    },
    loadStationMarker(data) {
      console.log(data)
      if (this.stationMarkers && this.stationMarkers.length > 0) {
        this.map.remove(this.stationMarkers)
        // this.map.clearMap()
        this.stationMarkers = []
      }
      data.forEach((item, index) => {
        let html = `<div class="station"></div>`
        let offset = new AMap.Pixel(-4, -4)
        this.stationMarkers.push(new AMap.Marker({
          position: [item.lng, item.lat],
          offset: offset,
          content: html,
          extData: item
        }))
        // if (!this.showRouteInf) {
        this.stationMarkers[index].on('click', (e) => {
          console.log(e.target)
          this.map.setCenter(e.target.getPosition())
          // this.showInf = true
          // this.infoWindow.open(this.map, e.target.getPosition());
          // 调接口获取站点信息
          this.getStationInfo(e.target.getExtData())
        })
        this.stationMarkers[index].on('mouseover', (e) => {
          // console.log(e.target)
          // this.map.setCenter(e.target.getPosition())
          // this.showInf = true
          // this.infoWindow.open(this.map, e.target.getPosition());
          // 调接口获取站点信息
          // this.getStationInfo(e.target.getExtData())
          this.showStationName(e.target.getExtData())
        })
        this.stationMarkers[index].on('mouseout', (e) => {
          // console.log(e.target)
          // this.map.setCenter(e.target.getPosition())
          // this.showInf = true
          // this.infoWindow.open(this.map, e.target.getPosition());
          // 调接口获取站点信息
          // this.getStationInfo(e.target.getExtData().stationName)
          this.hideStationName()
        })


        // }
      })
      this.map.add(this.stationMarkers)
      this.map.setFitView()
    },
    showStationName(data) {
      console.log(data)
      this.selectStation = data
      this.stationInf = new AMap.Marker({
        position: [data.lng, data.lat],
        offset: new AMap.Pixel(0, -10),
        content: this.$refs.stationName,
        anchor: 'bottom-center',
      })
      this.showName = true
      this.map.add(this.stationInf)
      // this.stationInf.open(this.map, [data.lng, data.lat])
    },
    hideStationName() {
      this.showName = false
      this.map.remove(this.stationInf)
      // this.stationInf.close()
    },
    loadBusMarker(data) {
      console.log(data)
      if (this.busMarkers && this.busMarkers.length > 0) {
        this.map.remove(this.busMarkers)
        // this.map.clearMap()
        this.busMarkers = []
      }
      data.forEach((item, index) => {
        let icon = null
        let offset = null
        icon = new AMap.Icon({
          size: [14, 30],
          image: icon_bus,
          imageSize: [14, 30],
        })
        offset = new AMap.Pixel(-7, -15)

        this.busMarkers.push(new AMap.Marker({
          position: [item.longitude, item.latitude],
          offset: offset,
          angle: item.busDirection ? item.busDirection : 0,
          icon: icon,
          extData: item
        }))
        this.busMarkers[index].on('click', (e) => {
          // console.log(e.target)
          this.busInf.setContent(this.$refs.busInf)
          // this.map.setCenter(e.target.getPosition())
          this.showBusInf = true
          this.busInf.open(this.map, [e.target.getPosition().lng, e.target.getPosition().lat])
        })
      })
      this.map.add(this.busMarkers)
      // this.map.setFitView()
    },
    hideBusInf() {
      this.showBusInf = false
      this.busInf.close()
    },
    loadRouteLine(data) {
      if (this.routePolyline) {
        this.map.remove(this.routePolyline)
        this.routePolyline = null
      }
      console.log(data)
      this.routePolyline = new AMap.Polyline({
        path: data,
        strokeColor: '#16A183',
        strokeWeight: 6,
        strokeOpacity: 1,
        strokeStyle: 'solid',
        // showDir: true,
      })

      this.map.add(this.routePolyline);
      this.map.setFitView();
    },
    closeModule() {
      console.log('hideMapModule')
      if (this.timer) {
        clearInterval(this.timer)
      }
      this.$emit('hideMapModule')
    },
    showSupportBus() {
      this.showSupportBusList = true
    },
    hideSupportBus() {
      this.showSupportBusList = false
    },
    setTimer() {
      if (this.timer) {
        clearInterval(this.timer)
      }
      this.timer = setInterval(() => {
        this.getMonitorInfo()
        this.getSchedulePlanDetail()
      }, 10 * 1000)
    },
    getMonitorInfo() {
      let params = {
        routeId: this.$props.sendData.routeId,
        runDate: `${moment(new Date()).format('YYYY-MM-DD')} 00:00:00`,
      }
      axios.post(this.url.getMonitorInfo, params).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.busPositionInf = res.data.runBus.filter(l => l.busId == this.$props.sendData.busId)

        this.loadBusMarker(this.busPositionInf)
      });
    },
    getGisRoadInfo() {
      let params = {
        routeId: this.$props.sendData.routeId,
        direction: this.$props.sendData.direction
      }
      axios.post(this.url.getGisRoadInfo, params).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.routeList = []
        res.data.data.gis.forEach(l => {
          if (l.longitude && l.latitude) {
            this.routeList.push([l.longitude, l.latitude])
          }
        })
        console.log('线路坐标', this.routeList)
        this.loadRouteLine(this.routeList)
        console.log('线路站点坐标', res.data.data.station)
        let stationList = []
        res.data.data.station.forEach(l => {
          if (l.LONGITUDED && l.LATITUDED) {
            stationList.push({
              lng: l.LONGITUDED,
              lat: l.LATITUDED,
              routeStaId: l.ROUTESTAID,
              stationName: l.ROUTESTATIONNAME,
              stationId: l.STATIONID,
            })
          }
        })
        this.loadStationMarker(stationList)

      });
    },
    getSchedulePlanDetail() {
      let params = {
        routeId: this.$props.sendData.routeId,
        supportRouteId: this.$props.sendData.supportRouteId,
        scheduleId: this.$props.sendData.scheduleId,
      }
      axios.post(this.url.getSchedulePlanDetail, params).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.supportPlanData = res.data.data
        this.supportPlanList = res.data.data.supportPlanList
      });
    },
  },
};
</script>

<style lang='less' scoped>
.map-modules {
  width: 1733px;
  height: 670px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 109;

  >.close {
    width: 46px;
    height: 46px;
    line-height: 46px;
    box-sizing: border-box;
    color: #fff;
    font-size: 28px;
    text-align: center;
    position: absolute;
    top: -10px;
    right: 30px;
    z-index: 1;
    cursor: pointer;

    img {
      width: 100%;
      height: 100%;
    }
  }

  >.bg {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .left-aside {
    width: 280px;
    height: calc(670px - 140px);
    padding: 12px 16px 0 0;
    // background: RGBA(3, 54, 112, .7);
    position: absolute;
    top: 50%;
    left: 46px;
    transform: translateY(-50%);
    z-index: 2;

    .route-station {
      .container {
        margin-bottom: 20px;
        font-family: Source Han Sans CN, Source Han Sans CN !important;

        .top {
          margin-bottom: 6px;
          position: relative;

          img {
            width: 100%;
            height: 22px;
            position: absolute;
            top: 0;
            left: 0;
          }

          .text {
            line-height: 22px;
            margin-left: 24px;
            color: #FBFBFB;
            font-size: 12px;
            font-weight: bold;
            position: relative;
          }
        }

        .content {
          width: 264px;
          // height: 315px;
          padding: 12px 18px;
          box-sizing: border-box;
          background: #0A3979;
          border-radius: 10px;

          .row {
            margin-bottom: 8px;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .label {
              color: #FBFBFB;
              font-size: 12px;
              white-space: nowrap;
            }

            .val {
              color: #2CCCFF;
              font-size: 16px;
              font-weight: bold;

              span {
                font-weight: 400;
                font-size: 12px;
                color: #CFD7E0;
              }
            }

            .l {
              text-align-last: left;
            }

            .r {
              text-align: right;
            }

            .arrow {
              color: #CFD7E0;
              text-align: center;

              .arrow-r,
              .arrow-l {
                width: 42px;
                height: 2px;
                margin-bottom: 2px;
                background: #CFD7E0;
                position: relative;
              }

              .arrow-r::after {
                content: '';
                width: 6px;
                height: 2px;
                background: #CFD7E0;
                position: absolute;
                top: -3px;
                right: 0;
                transform: rotate(45deg);
              }

              .arrow-l::after {
                content: '';
                width: 6px;
                height: 2px;
                background: #CFD7E0;
                position: absolute;
                left: 0;
                bottom: -3px;
                transform: rotate(45deg);
              }

            }
          }

        }
      }
    }

    .route-inf {
      width: 308px;
      height: 444px;
      padding: 40px 20px;
      box-sizing: border-box;
      position: absolute;
      top: 50%;
      right: -346px;
      transform: translateY(-50%);
      z-index: 1;

      .close {
        width: 34px;
        height: 34px;
        position: absolute;
        top: -20px;
        right: 8px;
        z-index: 1;
        cursor: pointer;

        img {
          width: 100%;
          height: 100%;
        }
      }

      .bg {
        width: 308px;
        height: 444px;
        position: absolute;
        top: 0;
        left: 0;

        img {
          width: 100%;
          height: 100%;
        }
      }

      .content {
        width: calc(308px - 40px);
        height: calc(444px - 80px);
        position: relative;
        overflow: hidden;

        /* 宽度和颜色 */
        ::-webkit-scrollbar {
          width: 4px;
          /* 滚动条宽度 */
        }

        ::-webkit-scrollbar-track {
          background: #f1f1f1;
          /* 滚动条轨道背景色 */
        }

        ::-webkit-scrollbar-thumb {
          background: #888;
          /* 滚动条手柄颜色 */
        }

        ::-webkit-scrollbar-thumb:hover {
          background: #555;
          /* 鼠标悬停时手柄颜色 */
        }

        .top {
          .tab {
            width: 50%;
            width: 100%;
            height: 24px;
            line-height: 24px;
            box-sizing: border-box;
            border: 1px solid #5BAEEC;
            color: #9EAEBD;
            font-size: 14px;
            text-align: center;
            display: inline-block;
            cursor: pointer;
          }

          .active {
            background: #449BD7;
            color: #E6F7FF;
          }
        }

        .table {
          width: 100%;
          height: calc(100% - 24px);

          .header,
          .row {
            height: 38px;
            box-sizing: border-box;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .col {
              line-height: 38px;
              color: #9EAEBD;
              font-size: 14px;
              text-align: center;
            }

            .col:nth-child(1) {
              width: 120px;
            }

            .col:nth-child(2) {
              width: 130px;
            }

            .col:nth-child(3) {
              width: 70px;
            }

            .col:nth-child(4) {
              width: 70px;
            }

            .col:nth-child(5) {
              width: 70px;
            }

            .col:nth-child(6) {
              width: 4px;
            }
          }

          .body {
            height: calc(100% - 38px);
            overflow-y: auto;

            .row {
              margin-bottom: 6px;
              background: rgba(36, 97, 113, 0.48);

              .col {
                color: #E6F7FF;
              }
            }

            .row:nth-child(2n) {
              background: rgba(36, 64, 113, 0.48);
            }

          }
        }
      }
    }
  }

  #map-container {
    padding: 0px;
    margin: 0px;
    width: calc(1733px - 40px - 334px);
    height: calc(670px - 140px);
    position: absolute;
    top: 50%;
    right: 46px;
    transform: translateY(-50%);
    z-index: 1;

    /deep/.station {
      width: 8px;
      height: 8px;
      box-sizing: border-box;
      background: #FFFFFF;
      border: 1px solid #7DF6DB;
      border-radius: 50%;
    }

    /deep/.station-name {
      padding: 6px;
      background: #414141;
      border-radius: 2px;
      color: #D7D8D7;
      white-space: nowrap;
      position: relative;
      z-index: 109;
    }

    /deep/.bus-inf {
      width: 308px;
      height: 174px;
      font-family: Source Han Sans CN, Source Han Sans CN !important;
      position: relative;
      z-index: 109;

      .close {
        width: 30px;
        height: 30px;
        line-height: 30px;
        box-sizing: border-box;
        color: #fff;
        font-size: 18px;
        text-align: center;
        position: absolute;
        top: -38px;
        right: 0;
        cursor: pointer;

        img {
          width: 100%;
          height: 100%;
        }
      }


      .bg {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;

        img {
          width: 100%;
          height: 100%;
        }
      }

      .content {
        width: calc(100% - 24px);
        height: calc(100% - 20px);
        // background: #000;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);

        .box {
          display: flex;
          justify-content: space-between;
        }

        .l,
        .r {
          width: 48%;
          height: 100%;
        }

        .r {
          .row {
            margin-bottom: 6px;
          }
        }

        .pic-box {
          margin-bottom: 4px;

          img {
            width: 140px;
            height: 82px;
          }

        }

        .row {
          margin-bottom: 4px;
          display: flex;
          justify-content: space-between;
          align-items: center;

          .label {
            color: #CFD7E0;
            font-size: 12px;
          }

          .val {
            color: #2CCCFF;
            font-size: 14px;
            font-weight: bold;

            span {
              color: #CFD7E0;
              font-size: 12px;
            }
          }
        }
      }
    }

    .zoom-content {
      padding: 0 4px;
      background: #414141;
      color: #D7D8D7;
      text-align: center;
      position: absolute;
      right: 20px;
      bottom: 20px;
      z-index: 1;

      .btn {
        line-height: 36px;
        font-size: 18px;
        font-weight: bold;
        cursor: pointer;
      }

      .number {
        width: 36px;
        line-height: 40px;
        border-top: 1px solid #6D6D6D;
        border-bottom: 1px solid #6D6D6D;
        font-size: 14px;

      }
    }
  }
}
</style>