<template>
  <div class="busStationInfoH5">
    <header>
      <div class="header-img">
        <!-- <p>{{stationInfo.n}}</p> -->
        <img src="../assets/header.png" alt />
      </div>
      <div class="header-search">
        <input type="text" v-model="msg" placeholder="搜索本站台具体线路" />
      </div>
    </header>
    <section>
      <ul>
        <li class="rt" v-for="(item,index) in busList" :key="item.rsi" v-show="item.rn.indexOf(msg)!=-1">
          <div class="left" :class="item.hasChange?'':'leftSpecail'">
            <i class="iconfont icon-busd"></i>
          </div>
          <div class="middle" @click="goBusList(item)">
            <div class="st-tit">{{item.rn}}</div>
            <div class="st-go" v-if="!item.direction">开往：{{item.dn.split("-")[1]}}</div>
            <div class="st-go" v-if="item.direction">开往：{{item.ddn.split("-")[1]}}</div>
            <div class="st-time" v-if="!item.direction">
              <span v-if="item.forecast.time!=-1">{{item.forecast.time}}分</span>
              <span v-if="item.forecast.time==-1">---</span>
            </div>

            <div class="st-time" v-if="item.direction">
              <span v-if="item.dforecast.time!=-1">{{item.dforecast.time}}分</span>
              <span v-if="item.dforecast.time==-1">---</span>
            </div>
            <div v-if="!item.direction">
              <div class="st-dis" v-if="item.forecast.c!=-1">
                距离该站
                <span v-if="item.forecast.c!=-1">{{item.forecast.c}}</span>站
              </div>
              <div class="st-dis" v-if="item.forecast.c==-1">尚未发车</div>
            </div>
            <div v-if="item.direction ">
              <div class="st-dis" v-if="item.dforecast.c!=-1">
                距离该站
                <span v-if="item.dforecast.c!=-1">{{item.dforecast.c}}</span>站
              </div>
              <div class="st-dis" v-if="item.dforecast.c==-1">尚未发车</div>
            </div>
          </div>
          <div class="right" v-if="item.hasChange" @click="handleChange(index)">
            <img src="../assets/turn.png" alt />
          </div>
        </li>

        <!--  <li class="nt">
          <div class="left">
            <i class="iconfont icon-busd"></i>
          </div>
          <div class="right">
            <div class="st-tit">325线</div>
            <div class="st-go">开往：广汕路（万龙路）总站</div>
            <div class="st-time">
              <span>5</span>
              分
            </div>
            <div class="st-dis">
              距离该站
              <span>2</span>
              站
            </div>
          </div>
        </li>
        <li class="rt">
          <div class="left">
            <i class="iconfont icon-busd"></i>
          </div>
          <div class="middle">
            <div class="st-tit">325线</div>
            <div class="st-go">开往：广汕路（万龙路）总站</div>
            <div class="st-time">
              <span>5</span>
              分
            </div>
            <div class="st-dis">
              距离该站
              <span>2</span>
              站
            </div>
          </div>
          <div class="right">
            <img src="../assets/turn.png" alt />
          </div>
        </li>-->
      </ul>
    </section>
  </div>
</template>

<script>
import { postByStationNameId, postByForecastBus } from "@/api/api";
import { Dialog } from "vant";
export default {
  name: "busStationInfoH5",
  data() {
    return {
      stationInfo: {},
      busList: [],
      direction: 0,
      msg:'',
      searchList:[],
    };
  },
  created() {
    this.initStationInfo();
  },
  mounted(){
    document.title = this.stationInfo.n
  },

  methods: {
    initStationInfo() {
      this.stationInfo = JSON.parse(sessionStorage.getItem("stationInfo"));
      console.log(this.stationInfo);
      postByStationNameId({
        stationNameId: this.stationInfo.i
      }).then(res => {
        if (res.retCode == 0) {
          let busResult = res.retData.l;
          this.loadForcast(busResult);
          console.log(busResult);
        } else {
          Dialog({ message: res.retMsg });
        }
      });
    },
    handleData(busResult) {
      for (let i = 0; i < busResult.length; i++) {
        let hasAdd = false;
        for (var j = 0; j < this.busList.length; j++) {
          if (this.busList[j].ri != busResult[i].ri) {
            hasAdd = true;
          } else {
            hasAdd = false;
          }
        }
        if (this.busList.length == 0) {
          this.busList.push(busResult[i]);
        } else if (hasAdd) {
          this.busList.push(busResult[i]);
        }
      }
      console.log(busResult);
      for (let i = 0; i < busResult.length; i++) {
        for (var j = 0; j < this.busList.length; j++) {
          if (this.busList[j].ri == busResult[i].ri) {
            if (this.busList[j].rsi != busResult[i].rsi) {
              this.$set(this.busList[j], "hasChange", true);
              this.$set(this.busList[j], "direction", false);
              this.$set(this.busList[j], "ddn", busResult[i].dn);
              this.$set(this.busList[j], "drsi", busResult[i].rsi);
              this.$set(this.busList[j], "dforecast", busResult[i].forecast);
            }
          }
        }
      }
      console.log(this.busList);
    },
    loadForcast(busResult) {
      postByForecastBus({
        stationNameId: this.stationInfo.i
      }).then(res => {
        if (res.retCode == 0) {
          for (var i = 0; i < busResult.length; i++) {
            for (var j = 0; j < res.retData.length; j++) {
              if (busResult[i].rsi == res.retData[j].i) {
                this.$set(busResult[i], "forecast", res.retData[j]);
                break;
              }
            }
          }
          this.handleData(busResult);
        } else {
          Dialog({ message: res.retMsg });
        }
      });
    },
    handleChange(index) {
      this.$set(
        this.busList[index],
        "direction",
        !this.busList[index].direction
      );

      console.log(this.busList[index].direction);
    },
    goBusList(item) {
      let busInfo = {
        ri: item.ri,
        rn: item.rn,
        rsi: item.rsi
      };

      if (!item.direction) {
        busInfo.d = item.d;
        busInfo.dn = item.dn;
      } else {
        busInfo.d = item.d == 0 ? 1 : 0;
        busInfo.dn = item.ddn;
      }
      sessionStorage.setItem("busInfo", JSON.stringify(busInfo));
      this.$router.push({
        name: "busListH5"
      });
    }
  }
};
</script>

<style scoped lang="less">
@import "../assets/font/iconfont.css";
@background: #dfdfdf;
@blue: #24a1ee;
@gray: #a1a1a1;
@line: #dfdfdf;

.busStationInfoH5 {
  width: 100%;
  height: 100%;

  header {
    .header-img {
      width: 100%;
      height: 150px;
      position: relative;

      p {
        position: absolute;
        width: 100%;
        text-align: center;
        font-size: 1.4rem;
        font-weight: 600;
        top: 20px;
      }

      img {
        display: block;
        width: 100%;
        height: 100%;
        // background: red;
      }
    }

    .header-search {
      width: 100%;
      height: 50px;
      border-bottom: 1px solid @line;
      box-sizing: border-box;
      padding: 10px 0 0 0;

      input {
        display: block;
        width: 92.4%;
        height: 30px;
        margin: 0 auto;
        border: none;
        font-size: 1.4rem;
        background: @background;
        border-radius: 15px;
        box-sizing: border-box;
        padding: 0 10px;
      }
    }
  }

  section {
    height: calc(100% - 200px);

    ul {
      height: 100%;
      overflow: scroll;

      .nt {
        width: 100%;
        height: 60px;
        background: #fff;
        margin: 10px auto;
        border-radius: 5px;
        display: flex;
        .left {
          width: 14%;
          flex: 1.4;
          vertical-align: top;
          text-align: center;

          i {
            display: block;
            font-size: 1.6rem;
            line-height: 60px;
            color: @blue;
          }
        }

        .right {
          width: 86%;
          flex: 8.6;
          position: relative;

          .st-tit {
            position: absolute;
            top: 10px;
            font-size: 1.4rem;
            font-weight: 600;
          }

          .st-go {
            position: absolute;
            bottom: 10px;
            font-size: 1.2rem;
            color: @gray;
            width: 60%;
            height: 18px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }

          .st-time {
            position: absolute;
            top: 10px;
            right: 10px;
            font-size: 1.2rem;
            span {
              font-size: 1.4rem;
              color: @blue;
            }
          }

          .st-dis {
            position: absolute;
            bottom: 10px;
            right: 10px;

            span {
              color: #f55d23;
            }
          }
        }
      }
      .rt {
        width: 100%;
        height: 60px;
        background: #fff;
        margin: 10px auto;
        border-radius: 5px;
        display: flex;
        .left {
          flex: 1.4;
          vertical-align: top;
          text-align: center;

          i {
            display: block;
            font-size: 1.6rem;
            line-height: 60px;
            color: @blue;
          }
        }
        .leftSpecail{
          flex: 1.2;
        }
        .middle {
          flex: 8.6;
          position: relative;

          .st-tit {
            position: absolute;
            top: 10px;
            font-size: 1.4rem;
            font-weight: 600;
          }

          .st-go {
            position: absolute;
            bottom: 10px;
            font-size: 1.2rem;
            color: @gray;
            width: 60%;
            height: 18px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }

          .st-time {
            position: absolute;
            top: 10px;
            right: 10px;
            font-size: 1.2rem;
            span {
              font-size: 1.4rem;
              color: @blue;
            }
          }

          .st-dis {
            position: absolute;
            bottom: 10px;
            right: 10px;

            span {
              color: #f55d23;
            }
          }
        }
        .right {
          flex: 1.4;
          height: 40px;
          box-sizing: border-box;
          margin-top: 10px;
          vertical-align: top;
          text-align: center;
          position: relative;
          border-left: 1px solid @line;
          img {
            display: block;
            color: @blue;
            width: 50%;
            height: 36%;
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
            margin: auto;
          }
        }
      }
    }
  }
}
</style>