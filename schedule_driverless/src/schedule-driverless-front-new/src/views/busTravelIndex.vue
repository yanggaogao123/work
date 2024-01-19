<template>
  <div class="busTravelIndex">
    <div id="container" :class="show.stShow==true?'':'details'"></div>
    <div id="station">
      <div class="st-brief" v-show="show.stShow">
        <div class="st-info">
          <div class="st-tips" @click="stControl">
            <van-icon name="arrow-up" />
            <span>上拉查看更多线路</span>
          </div>
          <div class="st-tit">{{stationName}}</div>
          <div class="st-through">途经{{runBusLength}}条线路</div>
          <van-icon name="replay" class="btn-refresh" @click="loadData" />
          <div class="st-dis">{{distance}}</div>
        </div>
        <ul class="line-info">
          <li v-for="(item,index) in busList.l" :key="item.rsi">
            <div class="line-div" @click="goBusList(item)">
              <div class="line-div">
                <div class="bus-name">{{item.rn}}</div>
                <div class="bus-go">开往：{{item.dn.split("-")[1]}}</div>
                <div class="bus-time">
                  <span v-if="item.forecast.time!=-1">{{item.forecast.time}}分</span>
                  <span v-if="item.forecast.time==-1">---</span>
                </div>
                <div class="bus-dis" v-if="item.forecast.c!=-1">
                  距离该站
                  <span>{{item.forecast.c}}</span>站
                </div>
                <div class="bus-dis" v-if="item.forecast.c==-1">尚未发车</div>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div class="st-particular" v-show="!show.stShow">
        <div class="st-info">
          <div class="st-tips" @click="stControl">
            <van-icon name="arrow-down" />
            <span>下滑查看地图</span>
          </div>
          <div class="st-tit">{{stationName}}</div>
          <div class="st-through">途经{{runBusLength}}条线路</div>
          <van-icon name="replay" class="btn-refresh" />
          <div class="st-dis">{{distance}}</div>
        </div>
        <ul class="line-info">
          <li v-for="(item,index) in busList.l" :key="item.rsi">
            <div class="line-div" @click="goBusList(item)">
              <div class="line-div">
                <div class="bus-name">{{item.rn}}</div>
                <div class="bus-go">开往：{{item.dn.split("-")[1]}}</div>
                <div class="bus-time">
                  <span v-if="item.forecast.time!=-1">{{item.forecast.time}}分</span>
                  <span v-if="item.forecast.time==-1">---</span>
                </div>
                <div class="bus-dis" v-if="item.forecast.c!=-1">
                  距离该站
                  <span>{{item.forecast.c}}</span>站
                </div>
                <div class="bus-dis" v-if="item.forecast.c==-1">尚未发车</div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { postByCoord, postByStationNameId, postByForecastBus } from "@/api/api";
import locLogo from "@/assets/loc.png";
import stLogo from "@/assets/bus_big.png";
export default {
  name: "busTravelIndex",
  components: {},
  data() {
    return {
      map: "",
      isLoad: false,
      stationList: [],
      busList: [],
      forecastList: [],
      show: {
        stShow: true
      },
      localLng: 113.270758,
      localLat: 23.135589,
      stationInfo: {}
    };
  },
  created() {},
  mounted() {
    this.mapInit();
  },
  computed: {
    distance() {
      if (this.stationInfo) {
        return (
          "距离您" +
          this.mapDistance(
            this.stationInfo.longitude,
            this.stationInfo.latitude
          ) +
          "m"
        );
      } else {
        return;
      }
    },
    stationName() {
      if (!this.stationInfo) {
        return "暂无线路线路";
      } else {
        return this.stationInfo.n;
      }
    },
    runBusLength() {
      if (this.busList.l) {
        return this.busList.l.length;
      }
    }
  },
  methods: {
    mapInit() {
      let map = this.map;
      let that = this;
      map = new AMap.Map("container", {
        zoom: 17, //级别
        center: [113.270758, 23.135589], //中心点坐标
        mapStyle: "amap://styles/macaron", //设置地图的显示样式
        viewMode: "3D" //使用3D视图
      });

      //生成定位
      map.plugin("AMap.Geolocation", function() {
        let geolocation = new AMap.Geolocation({
          enableHighAccuracy: true, //是否使用高精度定位，默认:true
          timeout: 2000, //超过10秒后停止定位，默认：无穷大
          showButton: true,
          // buttonOffset: new AMap.Pixel(10, 120), //定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
          showMarker: true,
          showCircle: false,
          buttonPosition: "LB",
          panToLocation: true,
          zoomToAccuracy: true
        });
        map.addControl(geolocation);
        geolocation.getCurrentPosition();
        AMap.event.addListener(geolocation, "complete", onComplete); //返回定位信息
        AMap.event.addListener(geolocation, "error", onError); //返回定位出错信息
      });

      // 移动拖拽事件
      AMapUI.loadUI(["misc/PositionPicker"], function(PositionPicker) {
        var positionPicker = new PositionPicker({
          mode: "dragMap",
          map: map,
          iconStyle: {
            //自定义外观
            url: locLogo,
            ancher: [24, 40],
            size: [48, 74]
          }
        });
        var bool = true;
        positionPicker.on("success", function(positionResult) {
          console.log(positionResult.position);
          let x = positionResult.position.lng;
          let y = positionResult.position.lat;
          that.localLng = x;
          that.localLat = y;
          let my_loc = positionResult.address;
          that.loadData(); //加载附近站台数据
          // maker();
        });
        var onModeChange = function(e) {
          positionPicker.setMode(e.target.value);
        };
        positionPicker.start();
        map.panBy(0, 1);
      });

      //解析定位结果
      function onComplete(data) {
        //  var str=['定位成功'];
        console.log(data);
        let x = data.position.lng;
        let y = data.position.lat;
        let my_loc = data.formattedAddress;
        console.log(my_loc);
        console.log(x);
        console.log(y);

        map.panTo([x, y]);
      }
      //解析定位错误信息
      function onError(data) {
        let my_loc = null;
        map.panTo([113.270758, 23.135589]);
      }

      this.map = map;
    },
    addMarker(arr) {
      console.log(arr);
      // 创建一个 Icon
      var stIcon = new AMap.Icon({
        // 图标尺寸
        size: new AMap.Size(60, 60),
        // 图标的取图地址
        image: stLogo,
        // 图标所用图片大小
        imageSize: new AMap.Size(60, 60)
      });

      let markerList = [];
      console.log(arr);
      for (var i = 0; i < arr.length; i++) {
        var marker = new AMap.Marker({
          map: this.map,
          icon: stIcon,
          offset: new AMap.Pixel(-15, -15),
          position: new AMap.LngLat(arr[i].longitude, arr[i].latitude)
        });
        if(i==0){
          marker.setLabel({
            offset: new AMap.Pixel(0, 0), //设置文本标注偏移量
            content: "<div class='info-this'>" + arr[i].n + "</div>", //设置文本标注内容
            direction: "bottom", //设置文本标注方位,
          });
        }else{
          marker.setLabel({
            offset: new AMap.Pixel(0, 0), //设置文本标注偏移量
            content: "<div class='info'>" + arr[i].n + "</div>", //设置文本标注内容
            direction: "bottom" //设置文本标注方位
          });
        }
        
        markerList.push(marker);
      }
      // AMap.event.addListener(markerList);
      this.map.add(markerList);
    },
    mapDistance(longitude, latitude) {
      var radLat1 = this.getRad(this.localLat);
      var radLat2 = this.getRad(latitude);

      var a = radLat1 - radLat2;
      var b = this.getRad(this.localLng) - this.getRad(longitude);

      var s =
        2 *
        Math.asin(
          Math.sqrt(
            Math.pow(Math.sin(a / 2), 2) +
              Math.cos(radLat1) *
                Math.cos(radLat2) *
                Math.pow(Math.sin(b / 2), 2)
          )
        );
      s = s * 6378137.0;
      return Math.round(s);
    },
    stControl() {
      this.show.stShow = !this.show.stShow;
    },
    loadData() {
      var param = {
        longitude: this.localLng,
        latitude: this.localLat
      };
      console.log(param);
      let that = this;
      postByCoord(param).then(res => {
        if (res.retCode == 0) {
          this.stationList = res.retData;
          console.log(this.stationList);
          console.log(this);
          this.addMarker(this.stationList);
          this.stationInfo = this.stationList[0];
          var that = this;
          this.$axios
            .all([
              this.loadBusList(this.stationList[0].i),
              this.loadForecast(this.stationList[0].i)
            ])
            .then(
              that.$axios.spread(function(allBus, allForcast) {
                console.log("所有请求完成");
                console.log("请求1结果", allBus);
                console.log("请求2结果", allForcast);
                that.busList = allBus.retData;
                that.forecastList = allForcast.retData;
                that.handLoadBusData(allBus.retData, allForcast.retData);
              })
            );
        }
      });
    },
    getRad(d) {
      return (d * Math.PI) / 180.0;
    },
    handLoadBusData(allBus, allForcast) {
      console.log(allBus);
      for (var i = 0; i < allBus.l.length; i++) {
        for (var j = 0; j < allForcast.length; j++) {
          if (allBus.l[i].rsi == allForcast[j].i) {
            this.busList.l[i].forecast = allForcast[j];
            break;
          }
        }
      }
    },
    loadBusList(stationNameId) {
      var param = {
        stationNameId: stationNameId
      };

      return postByStationNameId(param);
    },
    loadForecast(stationNameId) {
      var param = {
        stationNameId: stationNameId
      };
      return postByForecastBus(param);
    },
    goBusList(item) {
      this.$router.push({
        name: "busList"
      });
      sessionStorage.setItem("busInfo", JSON.stringify(item));
      sessionStorage.setItem(
        "localtion",
        JSON.stringify({
          localLng: this.localLng,
          localLat: this.localLat
        })
      );
      sessionStorage.setItem("busStation", JSON.stringify(this.stationName));
    }
  }
};
</script>

<style lang="less">
@gray: #dfdfdf;
@height: 2.88;
@blue: #24a1ee;



.busTravelIndex {
  width: 100%;
  height: 100%;
  position: relative;

  #container {
    width: 100%;
    height: calc(100% - 248px * @height);
  }
  .amap-marker-label{
    // background : @blue;
    color: #fff;
    border:none;
    .info {
      background : #fff;
      color: @blue;
      // width: 44px * @height;
      // height: 14px * @height;
      // font-size: 1.2rem * @height;
    }
    .info-this{
      background : @blue;
      color: #fff;
    }
  }
  

  #container.details {
    width: 100%;
    height: calc(100% - 428px * @height);
  }

  #station {
    position: fixed;
    width: 100%;
    left: 0;
    bottom: 0;

    .st-brief {
      width: 100%;
      height: 248px * @height;
      background: #fff;

      .st-info {
        width: 100%;
        height: 68px * @height;
        position: relative;
        box-sizing: border-box;
        border-bottom: 1px solid @gray;

        div {
          position: absolute;
        }

        .st-tips {
          width: 100%;
          text-align: center;
          font-size: 1rem * @height;
          color: #22d288;
        }

        .st-tit {
          font-size: 1.6rem * @height;
          top: 14px * @height;
          left: 10px * @height;
          font-weight: 600;
        }

        .st-through {
          font-size: 1.2rem * @height;
          color: #888888;
          bottom: 10px * @height;
          left: 10px * @height;
        }

        .btn-refresh {
          position: absolute;
          top: 10px;
          right: 10px;
          font-size: 1.8rem * @height;
        }

        .st-dis {
          font-size: 1.2rem * @height;
          color: #888888;
          bottom: 10px * @height;
          right: 10px * @height;
        }
      }

      .line-info {
        height: 180px;

        li {
          height: 60px * @height;
          box-sizing: border-box;
          border-bottom: 1px solid @gray;

          .line-div {
            width: 100%;
            height: 60px * @height;
            position: relative;

            div {
              position: absolute;
            }

            .bus-name {
              top: 8px * @height;
              left: 10px * @height;
              font-size: 1.4rem * @height;
              font-weight: 600;
            }

            .bus-go {
              bottom: 8px * @height;
              left: 10px * @height;
              font-size: 1.2rem * @height;
              color: #888888;
            }

            .bus-time {
              top: 8px * @height;
              right: 10px * @height;
              font-size: 1.2rem * @height;
              color: #4287ff;

              span {
                display: inline-block;
                font-size: 1.6rem * @height;
              }
            }

            .bus-dis {
              bottom: 8px * @height;
              right: 10px * @height;
              font-size: 1.2rem * @height;
              color: #888888;

              span {
                display: inline-block;
                color: #e54b4b;
              }
            }
          }
        }
      }
    }

    .st-particular {
      width: 100%;
      height: 428px * @height;
      background: #fff;

      .st-info {
        width: 100%;
        height: 68px * @height;
        position: relative;
        box-sizing: border-box;
        border-bottom: 1px solid @gray;

        div {
          position: absolute;
        }

        .st-tips {
          width: 100%;
          text-align: center;
          font-size: 1rem * @height;
          color: #22d288;
        }

        .st-tit {
          font-size: 1.6rem * @height;
          top: 14px * @height;
          left: 10px * @height;
          font-weight: 600;
        }

        .st-through {
          font-size: 1.2rem * @height;
          color: #888888;
          bottom: 10px * @height;
          left: 10px * @height;
        }

        .btn-refresh {
          position: absolute;
          top: 10px * @height;
          right: 10px * @height;
          font-size: 1.8rem * @height;
        }

        .st-dis {
          font-size: 1.2rem * @height;
          color: #888888;
          bottom: 10px * @height;
          right: 10px * @height;
        }
      }

      .line-info {
        height: 180px * @height * 2;
        overflow: scroll;

        li {
          height: 60px * @height;
          box-sizing: border-box;
          border-bottom: 1px solid @gray;

          .line-div {
            width: 100%;
            height: 60px * @height;
            position: relative;

            div {
              position: absolute;
            }

            .bus-name {
              top: 8px * @height;
              left: 10px * @height;
              font-size: 1.4rem * @height;
              font-weight: 600;
            }

            .bus-go {
              bottom: 8px * @height;
              left: 10px * @height;
              font-size: 1.2rem * @height;
              color: #888888;
            }

            .bus-time {
              top: 8px * @height;
              right: 10px * @height;
              font-size: 1.2rem * @height;
              color: #4287ff;

              span {
                display: inline-block;
                font-size: 1.6rem * @height;
              }
            }

            .bus-dis {
              bottom: 8px * @height;
              right: 10px * @height;
              font-size: 1.2rem * @height;
              color: #888888;

              span {
                display: inline-block;
                color: #e54b4b;
              }
            }
          }
        }
      }
    }
  }
}
</style>