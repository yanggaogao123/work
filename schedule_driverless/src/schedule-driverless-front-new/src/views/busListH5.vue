<template>
  <div class="bus-list">
    <van-overlay class="van-bg" z-index="9999" :show="show.bgShow" duration="0.3" @click="show.bgShow = false">
      <img src="../assets/load.gif" alt />
    </van-overlay>
    <nav class="nav" v-if="firstStationName">
      <div class="nav-left">
        <div class="bus-line">
          {{firstStationName}}
          <i class="fa fa-arrow-right"></i>
          {{lastStationName}}
        </div>
        <div class="bus-inform">
          <span>首 {{staticsData.ft}}</span>
          <span>末 {{staticsData.lt}}</span>
        </div>
      </div>
    </nav>
    <section id="mainContent">
      <div class="go-his">
        <van-icon class="logo-time" name="underway-o" />
        <span @click="goBusListHis">发车时刻表</span>
      </div>
      <ul class="content" v-if="realData.rs[0].bus">
        <span class="start">起</span>
        <li class="clearfix" v-for="(item,index) in realData.rs" :key="item.sni"
          :class="item.rsn == thisSN?item.rsi == routeStationId?'place':'':''">
          <div class="bg"></div>
          <template v-if="item.bus">
            <div class="bus" v-if="item.bus.bl.length>0" @click="goBusBlListTime(item)">
              <span class="number">{{item.bus.bl.length>1?item.bus.bl.length+'辆':''}}</span>
              <template>
                <span class="img" v-if="item.bus.bl[0].serviceType == 1 || item.bus.bl[0].serviceType == 8"></span>
                <span class="img-fast"
                  v-if="item.bus.bl[0].serviceType == 3 || item.bus.bl[0].serviceType == 7 || item.bus.bl[0].serviceType == 9"></span>
                <span class="img-short"
                  v-if="item.bus.bl[0].serviceType == 2 || item.bus.bl[0].serviceType == 4 || item.bus.bl[0].serviceType == 5 || item.bus.bl[0].serviceType == 6 || item.bus.bl[0].serviceType == 10 || item.bus.bl[0].serviceType == 11"></span>
              </template>
            </div>
            <div class="bus-middle" v-if="item.bus.bbl.length>0" @click="goBusBblListTime(item)">
              <span class="number"></span>
              <template>
                <span class="img" v-if="item.bus.bbl[0].serviceType == 1 || item.bus.bbl[0].serviceType == 8"></span>
                <span class="img-fast"
                  v-if="item.bus.bbl[0].serviceType == 3 || item.bus.bbl[0].serviceType == 7 || item.bus.bbl[0].serviceType == 9"></span>
                <span class="img-short"
                  v-if="item.bus.bbl[0].serviceType == 2 || item.bus.bbl[0].serviceType == 4 || item.bus.bbl[0].serviceType == 5 || item.bus.bbl[0].serviceType == 6 || item.bus.bbl[0].serviceType == 10 || item.bus.bbl[0].serviceType == 11"></span>
              </template>
            </div>
          </template>

          <template>
            <div class="fl" :class="index==0?'line-spec':'line'" v-if="item.block == 0 || item.block == 1">
              <div></div>
            </div>
            <div class="yellow fl" :class="index==0?'line-spec':'line'" v-else-if="item.block == 2 || item.block == 3">
              <div></div>
            </div>
            <div class="red fl" :class="index==0?'line-spec':'line'" v-else>
              <div></div>
            </div>
          </template>
          <div class="station fl" @click="changeRouteStation(item.rsi,item.rsn)">
            <span class="title">{{index+1}}</span>
            <span class="name">{{item.rsn}}</span>
            <span class="subway" v-for="(ktem,kndex) in item.swi" :style="{background:ktem.color}">
              <img src="../assets/y.png" alt />
              <span>{{ktem.name.split('号线')[0]}}</span>
            </span>
            <span class="b" v-if="item.brt == 1">B</span>
          </div>
          <div class="location fl">
            <div class="bus-info">
              <span class="title">{{index+1}}</span>
              <span class="name">{{item.rsn}}</span>
              <span class="subway" v-for="(ktem,kndex) in item.swi" :style="{background:ktem.color}">
                <img src="../assets/y.png" alt />
                <span>{{ktem.name.split('号线')[0]}}</span>
              </span>
              <span class="b" v-if="item.brt == 1">B</span>
            </div>

            <p class="tips">距离您最近的三趟车</p>
            <template v-for="(ytem,yndex) in item.time">
              <div class="bus-time bus-arrival" v-if="ytem.count == 0">
                <p class="state">已进站</p>
                <p class="plate-number">{{ytem.plateNumber}}</p>
              </div>
              <div class="bus-time bus-coming" v-if="ytem.count > 0">
                <p class="state">
                  {{ytem.time}}
                  <span>分</span>
                  &nbsp;{{ytem.count}}
                  <span>站</span>
                </p>
                <p class="plate-number">{{ytem.plateNumber}}</p>
              </div>
              <div class="bus-time bus-notsend" v-if="ytem.count == -1 ">
                <p class="state" v-if="ytem.fbt == '' &&ytem.count == -1">尚未发车</p>
                <p class="state" v-if="ytem.fbt == -2 &&ytem.count == -1">待发车</p>
                <p class="state" v-if="ytem.fbt != -2&&ytem.fbt != '' &&ytem.count == -1">
                  <span>预计</span>
                  {{ytem.fbt}}
                  <span>发车</span>
                </p>
                <p class="plate-number" v-if="(ytem.fbt == -2||ytem.fbt == '' )&&ytem.count == -1">无车辆信息</p>
                <p class="plate-number" v-if="ytem.fbt != -2&&ytem.fbt != '' &&ytem.count == -1">仅供参考</p>
              </div>
            </template>
          </div>
        </li>
        <span class="end">终</span>
      </ul>
    </section>
    <footer>
      <ul>
        <li @click="turnAround">
          <img src="../assets/return.png" alt />
          <span>反向</span>
        </li>
        <li @click="reFlash">
          <img src="../assets/refresh.png" alt />
          <span>刷新</span>
        </li>
      </ul>
    </footer>
  </div>
</template>

<script>
  import {
    postStatics,
    postDynamic,
    postByNearStation
  } from "@/api/api";
  import {
    Dialog
  } from "vant";
  export default {
    name: "busList",
    data() {
      return {
        routeId: "",
        direction: "direction",
        routeStationId: "routeStationId",
        type: "type",
        latitude: "latitude",
        longitude: "longitude",
        stationName: {},
        thisSN: null,
        thisRsi: null,
        scrollTop: 0,
        routeStationName: null,
        firstStationName: null,
        lastStationName: null,
        show: {
          bgShow: false
        },
        realData: {
          com: null,
          ft: null,
          lt: null,
          price: null,
          ri: null,
          rn: null,
          rs: [{}]
        },
        staticsData: {
          com: null,
          ft: null,
          lt: null,
          price: null,
          ri: null,
          rn: null,
          rs: [{}]
        },
        myScroll: null,
        dynamicData: null,
        busInfo: {},
        localtion: {}
      };
    },
    created() {
      //从sessionStorage中拿出数据
      this.initData();
      // 加载静态数据，动态数据
    },
    mounted() {
      
    },
    methods: {
      initData() {
        let that = this;

        let path = window.location.href.split("?");
        console.log(path);
        if (path.length>1 && path[1].length > 0) {
          this.busInfo.ri = this.getQueryVariable("routeId");
          this.busInfo.d = this.getQueryVariable("direction");
          this.busInfo.rsi = this.getQueryVariable("routeStationId");
          that.loadStaticsDatas();
          that.loadDynamicDatas();
        }

        if (sessionStorage.getItem("busInfo") && sessionStorage.getItem("localtion")) {
          this.busInfo = JSON.parse(sessionStorage.getItem("busInfo"));
          this.localtion = JSON.parse(sessionStorage.getItem("localtion"));
          //根据附近经纬度获取最近站点
          this.initNearStation();
          var timer = setTimeout(function () {
            that.loadStaticsDatas();
            that.loadDynamicDatas();
          }, 500);
        }

      },
      initNearStation() {
        postByNearStation({
          routeId: this.busInfo.ri,
          direction: this.busInfo.d + "",
          longitude: this.localtion.localLng,
          latitude: this.localtion.localLat
        }).then(res => {
          if (res.retCode == 0) {
            console.log(res);
            console.log(this.busInfo);
            this.thisSN = res.retData.n;
            this.busInfo.rsi = res.retData.i;
          }
        });
      },

      loadStaticsDatas: function () {
        // 加载基础数据
        postStatics({
          routeId: this.busInfo.ri,
          direction: this.busInfo.d
        }).then(res => {
          console.log("基础数据：");
          console.log(JSON.parse(JSON.stringify(res)));
          if (res.retCode == 0) {
            if (res.retData.rs.length < 1) {
              if (this.busInfo.d == "0") {
                this.busInfo.d = "1";
              } else if (this.busInfo.d == "1") {
                this.busInfo.d = "0";
              }
              Dialog({
                message: "暂无相关数据"
              });
              return;
            }
            this.staticsData = res.retData;
            for (var i = 0; i < res.retData.rs.length; i++) {
              if (res.retData.rs[i].rsn == this.thisSN) {
                console.log(res.retData.rs[i]);
                this.routeStationId = res.retData.rs[i].rsi;
              }
            }
            // this.realData = res.retData;
            // console.log(this.realData);
            // 判断是否可以匹配到线路站点ID
            var len = res.retData.rs.length;
            console.log(this.localtion);
          } else {
            Dialog({
              message: res.retMsg
            });
          }
        });
      },
      loadDynamicDatas: function () {
        let that = this;
        postDynamic({
          routeId: this.busInfo.ri,
          routeStationId: this.busInfo.rsi,
          direction: this.busInfo.d
        }).then(res => {
          console.log(res);
          if (res.retCode == 0) {
            // 关闭遮罩
            if (res.retData.block.length < 1) {
              this.show.bgShow = false;
              return;
            }
            this.show.bgShow = false;
            this.dynamicData = res.retData;
            console.log(this.dynamicData);
            this.realData = this.staticsData;
            console.log(that.realData);
            for (var i = 0; i < that.realData.rs.length; i++) {
              that.$set(that.realData.rs[i], "block", this.dynamicData.block[i]);
              that.$set(that.realData.rs[i], "bus", this.dynamicData.bus[i]);
              that.$set(that.realData.rs[i], "time", this.dynamicData.time);
            }
            console.log(that.realData);
            // 配置title
            document.title = that.realData.rn;
            this.firstStationName = that.realData.rs[0].rsn;
            this.lastStationName =
              that.realData.rs[that.realData.rs.length - 1].rsn;
          } else {
            Dialog({
              message: res.retMsg
            });
          }
        });
      },
      // 点击定位站点
      changeRouteStation: function (rsi, rsn) {
        console.log(123);
        localStorage.setItem("rsi", rsi);
        this.routeStationId = rsi;
        // this.busInfo.rsi = rsi;
        this.$set(this.busInfo, "rsi", rsi);
        this.thisSN = rsn;
        this.loadDynamicDatas();
      },
      // 点击车辆
      clickBus: function (buses) {
        localStorage.setItem("startStationName", this.staticsData.rs[0].rsn);
        localStorage.setItem(
          "endStationName",
          this.staticsData.rs[this.staticsData.rs.length - 1].rsn
        );
        if (buses.length > 1) {
          // 获取出发站点，以及终点站
          localStorage.setItem("buses", JSON.stringify(buses));
          window.location.href = "bus_list.html";
        } else {
          localStorage.setItem("bus", JSON.stringify(buses[0]));
          window.location.href = "bus_list_station.html";
        }
      },
      // 用户点击反向操作
      turnAround: function () {
        //判断首站与末站是否为统一个站，若为同一个，则为环线
        if (this.firstStationName == this.lastStationName) {
          Dialog({
            message: "暂无相关数据"
          });
          return;
        }
        this.show.bgShow = true;
        if (this.busInfo.d == "0") {
          this.busInfo.d = "1";
        } else if (this.busInfo.d == "1") {
          this.busInfo.d = "0";
        }
        // 重新加载数据
        this.loadStaticsDatas();
        this.loadDynamicDatas();
      },
      reFlash: function () {
        this.show.bgShow = true;
        // 重新加载数据
        // this.loadStaticsDatas();
        this.loadDynamicDatas();
      },
      goBusBlListTime(item) {
        console.log(item);
        if (item.bus.bl.length > 1) {
          sessionStorage.setItem("busList", JSON.stringify(item.bus.bl));
          this.$router.push({
            name: "busListSelectH5"
          });
        } else {
          this.$router.push({
            name: "busListTimeH5",
            params: {
              busStatics: {
                id: item.bus.bl[0].id,
                subId: item.bus.bl[0].subId,
                plateNumber: item.bus.bl[0].plateNumber
              }
            }
          });
        }
      },
      goBusBblListTime(item) {
        if (item.bus.bbl.length > 1) {
          sessionStorage.setItem("busList", JSON.stringify(item.bus.bbl));
          this.$router.push({
            name: "busListSelectH5"
          });
        } else {
          this.$router.push({
            name: "busListTimeH5",
            params: {
              busStatics: {
                id: item.bus.bbl[0].id,
                subId: item.bus.bbl[0].subId,
                plateNumber: item.bus.bbl[0].plateNumber
              }
            }
          });
        }
      },
      goBusListHis() {
        this.$router.push({
          name: "busListHisH5",
          params: {
            busStatics: {
              rn: this.staticsData.rn,
              com: this.staticsData.com,
              firstStationName: this.firstStationName,
              lastStationName: this.lastStationName,
              d: this.busInfo.d
            }
          }
        });
      },
      // 获取URL后面的参数
      getQueryVariable(variable) {
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i = 0; i < vars.length; i++) {
          var pair = vars[i].split("=");
          if (pair[0] == variable) {
            return pair[1];
          }
        }
        return (false);
      }
    }
  };
</script>

<style scoped lang="less">
  @background: #dfdfdf;
  @blue: #24a1ee;
  @gray: #a1a1a1;
  @line: #dfdfdf;
  @height: 2.88;

  .bus-list {
    width: 100%;
    height: 100%;
    position: relative;
    font-size: 1.2rem;

    .van-bg {

      // position:relative;
      img {
        position: absolute;
        width: 20px;
        height: 20px;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        margin: auto;
      }
    }

    .nav {
      background: #fff;
      position: fixed;
      width: 100%;
      top: 0;
      z-index: 999;
      height: 72px;
      box-sizing: border-box;
      border-bottom: 1px solid @line;

      .nav-left {
        display: inline-block;
        width: 75%;
        margin: 20px 0 5px 12px;

        .bus-line {
          width: 100%;
          font-size: 1.4rem;
          height: 22px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;

          span {
            display: inline-block;
          }

          span:nth-child(3) {
            overflow: hidden;
            height: 22px;
          }
        }

        .bus-inform {
          margin-top: 5px;

          span {
            font-size: 1.1rem;
            color: #a1a1a1;
          }

          span:nth-child(2) {
            margin: 0 10px;
          }
        }
      }
    }

    #mainContent {
      padding-top: 72px;
      padding-bottom: 48px;
      width: 100%;
      overflow-x: hidden;
      position: relative;

      .go-his {
        position: fixed;
        background: @blue;
        top: 100px;
        right: -12px;
        z-index: 1000;
        padding: 0 15px 0 10px;
        font-size: 1.2rem;
        color: #fff;
        border-radius: 30px;

        .logo-time {
          display: inline-block;
          font-size: 1.2rem;
          line-height: 26px;
          vertical-align: middle;
        }

        span {
          display: inline-block;
        }
      }

      .content {
        margin: 14px 0 0 16%;

        .start {
          display: inline-block;
          width: 22px;
          height: 22px;
          font-size: 1.2rem;
          background: @blue;
          color: #fff;
          border-radius: 50px;
          box-sizing: border-box;
          padding: 2px 0 0 5px;
        }

        .end {
          display: inline-block;
          width: 22px;
          height: 22px;
          font-size: 1.2rem;
          background: @blue;
          color: #fff;
          border-radius: 50px;
          box-sizing: border-box;
          padding: 2px 0 0 5px;
        }

        li {
          position: relative;
          height: 46px;

          .bg {
            display: none;
          }

          .bus {
            position: absolute;
            display: block;
            right: 102%;
            width: 50px;
            top: -10%;

            .number {
              display: inline-block;
              font-size: 1.2rem;
              width: 20px;
              vertical-align: top;
              padding-top: 3px;
              margin-right: 2px;
              color: #0099cc;
            }

            .img {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busn.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-fast {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busf.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-short {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/buss.png") no-repeat;
              background-size: 22px 22px;
            }
          }

          .bus-middle {
            position: absolute;
            display: block;
            right: 102%;
            width: 50px;
            top: 30%;

            .number {
              display: inline-block;
              font-size: 1.2rem;
              width: 20px;
              vertical-align: top;
              padding-top: 3px;
              margin-right: 2px;
              color: #0099cc;
            }

            .img {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busn.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-fast {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busf.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-short {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/buss.png") no-repeat;
              background-size: 22px 22px;
            }
          }

          .line-spec {
            display: inline-block;
            width: 5px;
            height: 100%;
            background: #49b993;
            text-align: center;
            margin-left: 7px;

            div {
              width: 1px;
              border-left: 1px dashed #fff;
              height: 100%;
              //                  background: #fff;
              margin: 0 auto;
            }
          }

          .line {
            display: inline-block;
            width: 5px;
            height: 100%;
            background: #49b993;
            text-align: center;
            margin-left: 7px;

            //              margin-top:-5%;
            div {
              width: 1px;
              border-left: 1px dashed #fff;
              height: 100%;
              //                  background: #fff;
              margin: 0 auto;
            }
          }

          .yellow {
            background: #dace4a;
          }

          .red {
            background: #d15264;
          }

          .line:before {
            content: "";
            display: inline-block;
            width: 5px;
            height: 5px;
            background: #ffffff;
            border-radius: 50px;
            position: absolute;
            top: -2px;
            left: 7px;
          }

          .station {
            display: inline-block;
            position: absolute;
            width: 86%;
            top: -8px;
            border-bottom: 1px solid #e6e6e6;
            margin-left: 10px;
            padding-bottom: 14px;

            span {
              display: inline-block;
            }

            .b {
              font-size: 1rem;
              font-weight: bold;
              color: #ff954a;
              margin-left: 5px;
              vertical-align: middle;
              margin-top: 2px;
            }

            .subway {
              display: inline-block;
              padding: 0 4px;
              font-size: 1rem;
              line-height: 14px;
              color: #fff;
              border-radius: 3px;
              text-align: center;
              margin-left: 8px;
              vertical-align: middle;

              img {
                display: inline-block;
                width: 8px;
              }
            }

            .bus-info {
              width: 100%;
              overflow: hidden;

              .title {
                display: inline-block;
                width: 20px;
                font-size: 0.8rem;
                color: #a1a1a1;
                text-align: center;
                vertical-align: middle;
                margin-top: 5px;
              }

              .name {
                font-size: 1.5rem;
                max-width: 80%;
                overflow-x: hidden;
                height: 1.5rem;
                vertical-align: middle;
              }

              .b {
                font-size: 1rem;
                font-weight: bold;
                color: #ff954a;
                margin-left: 5px;
                vertical-align: middle;
                margin-top: 2px;
              }

              .subway {
                display: inline-block;
                padding: 0 4px;
                font-size: 1rem;
                line-height: 14px;
                color: #fff;
                border-radius: 3px;
                text-align: center;
                margin-left: 8px;
                vertical-align: middle;

                img {
                  display: inline-block;
                  width: 8px;
                }
              }

              .subway-line1 {
                background: #f9e001;
              }

              .subway-line2 {
                background: #0067cc;
              }

              .subway-line3 {
                background: #ea6632;
              }

              .subway-line4 {
                background: #009801;
              }

              .subway-line5 {
                background: #ff0000;
              }

              .subway-line6 {
                background: #8c1f5d;
              }

              .subway-line7 {
                background: #3fa37e;
              }

              .subway-line8 {
                background: #00a2cc;
              }

              .subway-line-gf {
                background: #cae987;
              }

              .subway-line-apm {
                background: #00a2cb;
              }
            }

            .hidden {
              color: red;
            }
          }

          .location {
            display: none;
          }
        }

        li:nth-child(2) {
          .line:before {
            content: "";
            display: inline-block;
            width: 5px;
            height: 5px;
            background: #ffffff;
            border-radius: 50px;
            position: absolute;
            top: -2px;
            left: 7px;
          }
        }

        .place {
          height: 120px;

          .bg {
            display: block;
            position: absolute;
            background: #dcf1fc;
            width: 97%;
            height: 100%;
            z-index: -1;
            right: 0;
            top: -16px;
          }

          .bus {
            position: absolute;
            right: 102%;

            .number {
              color: @blue;
            }

            .img {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busn.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-fast {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busf.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-short {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/buss.png") no-repeat;
              background-size: 22px 22px;
            }
          }

          .bus-middle {
            position: absolute;
            right: 102%;

            .number {
              color: #56be4d;
            }

            .img {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busn.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-fast {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/busf.png") no-repeat;
              background-size: 22px 22px;
            }

            .img-short {
              display: inline-block;
              width: 22px;
              height: 22px;
              background: url("../assets/buss.png") no-repeat;
              background-size: 22px 22px;
            }
          }

          .line:before {
            content: "";
            display: inline-block;
            width: 20px;
            height: 20px;
            background: url(../assets/dot.png) no-repeat;
            background-size: 20px 20px;
            position: absolute;
            top: -8px;
            left: 0;
          }

          .station {
            display: none;
          }

          .location {
            display: inline-block;
            position: absolute;
            width: 86%;
            top: -8px;
            border-bottom: 1px solid #e6e6e6;
            margin-left: 10px;
            padding-bottom: 2px;

            .bell {
              display: inline-block;
              position: absolute;
              width: 22px;
              height: 22px;
              // background: url(../img/bell.png) no-repeat;
              background-size: 22px 22px;
              top: 2px;
              right: 2px;
            }

            .bus-info {
              width: 100%;
              height: 24px;
              overflow-x: auto;
              white-space: nowrap;

              .title {
                display: inline-block;
                width: 20px;
                font-size: 0.8rem;
                color: #56be4d;
                text-align: center;
                vertical-align: middle;
                padding-top: 3px;
              }

              .name {
                font-size: 1.5rem;
                max-width: 80%;
                overflow-x: hidden;
                height: 1.5rem;
                vertical-align: middle;
              }

              .b {
                font-size: 1rem;
                font-weight: bold;
                color: #ff954a;
                margin-left: 5px;
                vertical-align: middle;
              }

              .subway {
                display: inline-block;
                padding: 0 4px;
                line-height: 14px;
                color: #fff;
                border-radius: 3px;
                text-align: center;
                margin-left: 8px;

                img {
                  display: inline-block;
                  width: 12px;
                  margin-top: 2px;
                }

                span {
                  font-size: 1.2rem;
                  line-height: 14px;
                }
              }

              .subway-line1 {
                background: #f9e348;
              }

              .subway-line2 {
                background: #2778cf;
              }

              .subway-line3 {
                background: #ea774f;
              }

              .subway-line4 {
                background: #2ca32f;
              }

              .subway-line5 {
                background: #fc2632;
              }

              .subway-line6 {
                background: #96396d;
              }

              .subway-line7 {
                background: #57ab8c;
              }

              .subway-line8 {
                background: #30abcf;
              }

              .subway-line9 {
                background: #a4cd6e;
              }

              .subway-line13 {
                background: #b0bf5d;
              }

              .subway-line14 {
                background: #7f3139;
              }

              .subway-line21 {
                background: #38488d;
              }

              .subway-line-gf {
                background: #cfe998;
              }

              .subway-line-apm {
                background: #30abcf;
              }
            }

            .tips {
              color: #a1a1a1;
              font-size: 0.8rem;
              margin-left: 10px;
            }

            .bus-time {
              display: inline-block;
              width: 30%;
              height: 58px;
              border-radius: 5px;
              margin: 6px 0 0 2%;
              position: relative;

              p {
                position: absolute;
                display: block;
                width: 100%;
                text-align: center;
                font-size: 1.2rem;
                color: #ffffff;
                margin: 0 auto;
              }

              .state {
                top: 2px;

                span {
                  font-size: 0.8rem;
                }
              }

              .plate-number {
                font-size: 1rem;
                bottom: 2px;
              }
            }

            .bus-arrival {
              background: #0099cc;
            }

            .bus-coming {
              background: #5ebedd;
            }

            .bus-notsend {
              background: #91daf2;
            }
          }
        }
      }
    }

    footer {
      position: fixed;
      width: 100%;
      height: 44px;
      box-sizing: border-box;
      border-top: 1px solid @line;
      left: 0;
      bottom: 0;
      background: #fff;

      ul {
        width: 100%;
        height: 100%;
        display: flex;

        li {
          flex: 1;
          width: 100%;
          text-align: center;

          img {
            display: block;
            width: 14px;
            height: 14px;
            margin: 6px auto 2px auto;
          }

          span {
            display: block;
          }
        }
      }
    }
  }
</style>