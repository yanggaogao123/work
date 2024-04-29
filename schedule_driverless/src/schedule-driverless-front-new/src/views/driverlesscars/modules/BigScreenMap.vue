<template>
  <div class="map-modules" v-if="false">
    <div class="close">X</div>
    <div class="bg"><img src="../../../assets/driverlesscars/big.png" alt=""></div>
    <div class="left-aside">
      <div class="route-station">

      </div>
    </div>
    <div id="map-container">
      <div class="station-name" ref="stationName" v-show="showName">{{ selectStation.stationName }}</div>
      <div class="bus-inf" ref="busInf" v-show="showBusInf">
        <div class="close" @click="hideBusInf">X</div>
        <div class="bg"><img src="../../../assets/driverlesscars/tankuang.png" alt=""></div>
        <div class="content"></div>
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
import AMapLoader from '@amap/amap-jsapi-loader';
import icon_bus from '../../../assets/driverlesscars/car.png'
export default {
  components: {},
  props: {},
  data() {
    return {
      map: null,
      stationMarkers: [],
      busMarkers: [],
      routePolyline: null,
      busInf: null,
      showBusInf: false,
      stationInf: null,
      mapZoom: 6,
      showName: false,
      selectStation: {
        stationName: '珠江新城站'
      }
    };
  },
  watch: {
  },
  computed: {
  },
  created() {
  },
  mounted() {
    this.initAMap();
  },
  methods: {
    initAMap() {
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
            this.loadStationMarker([{ lng: 113.397428, lat: 23.30923 }, { lng: 113.312529, lat: 23.12387 }])
            this.loadBusMarker([{ lng: 113.312529, lat: 23.12587 }])
            this.loadRouteLine([[113.397428, 23.30923], [113.312529, 23.12387]])

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
      data.stationName = '大沙头码头站'
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
          position: [item.lng, item.lat],
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
  },
};
</script>

<style lang='less' scoped>
.map-modules {
  width: 1733px;
  height: 800px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 109;

  .close {
    width: 46px;
    height: 46px;
    line-height: 46px;
    box-sizing: border-box;
    border: 2px solid #fff;
    border-radius: 50%;
    color: #fff;
    font-size: 28px;
    text-align: center;
    position: absolute;
    top: -10px;
    right: 30px;
    z-index: 1;
    cursor: pointer;
  }

  .close:hover {
    color: lightcoral;
    border: 2px solid lightcoral;
  }

  .bg {
    width: 1733px;
    height: 800px;
    position: absolute;
    top: 0;
    left: 0;

    img {
      width: 1733px;
      height: 800px;
    }
  }

  .left-aside {
    width: 350px;
    height: calc(800px - 140px);
    background: #000;
    position: absolute;
    top: 50%;
    left: 46px;
    transform: translateY(-50%);
    z-index: 1;
  }

  #map-container {
    padding: 0px;
    margin: 0px;
    width: calc(1733px - 40px - 350px);
    height: calc(800px - 140px);
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
      color: #6D6D6D;
      white-space: nowrap;
      position: relative;
      z-index: 109;
    }

    /deep/.bus-inf {
      width: 594px;
      height: 364px;
      position: relative;
      z-index: 109;

      .close {
        width: 46px;
        height: 46px;
        line-height: 46px;
        box-sizing: border-box;
        border: 2px solid #fff;
        border-radius: 50%;
        color: #fff;
        font-size: 28px;
        text-align: center;
        position: absolute;
        top: -56px;
        right: 0;
        cursor: pointer;
      }

      .close:hover {
        color: lightcoral;
        border: 2px solid lightcoral;
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
        width: 90%;
        height: 90%;
        background: #000;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
    }

    .zoom-content {
      padding: 0 4px;
      background: #414141;
      color: #D7D8D7;
      text-align: center;
      position: absolute;
      right: 60px;
      bottom: 80px;
      z-index: 1;

      .btn {
        line-height: 36px;
        font-size: 24px;
        font-weight: bold;
        cursor: pointer;
      }

      .number {
        width: 44px;
        line-height: 40px;
        border-top: 1px solid #6D6D6D;
        border-bottom: 1px solid #6D6D6D;
        font-size: 18px;

      }
    }
  }
}
</style>