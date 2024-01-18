<template>
  <div class="bus-list-his">
    <header>
      <div class="tit">
        <div class="tit-bus">
          <span class="tit-bus-name">{{busStatics.rn}}</span>
          <span class="tit-bus-com">{{busStatics.com}}</span>
        </div>
        <div class="tit-st">
          <div class="tit-st-s">{{busStatics.firstStationName}}</div>
          <i class="fa fa-arrow-right"></i>
          <div class="tit-st-e">{{busStatics.lastStationName}}</div>
        </div>
      </div>
    </header>
    <section>
      <div class="tips" v-show="showTips">
        <span>历史时刻表由行讯通提供，仅供参考，请提前候车。</span>
        <van-icon name="cross" class="x" @click="removeTips" />
      </div>
      <van-list class="his-list" v-for="(item,index) in list" :key="item.name">
        <div class="time-frame clearfix" @click="clickDetails(index)" v-if="item.list &&item.list.length>0">
          <div class="time fl">{{item.name}}</div>
          <span class="hot" v-if="item.hot == 1">高峰</span>
          <van-icon name="arrow-down" class="icon fr" v-show="item.showDetails" />
          <van-icon name="arrow" class="icon fr" v-show="!item.showDetails" />
        </div>
        <ul class="time-details" v-show="item.showDetails" v-if="item.list !== undefined  &&  item.list.length>0">
          <li v-for="(ktem,k) in item.timeList" :key="ktem.time">
            <span :class="ktem.pick==1?'that':''">{{ktem.time}}</span>
          </li>
        </ul>
      </van-list>
    </section>
  </div>
</template>

<script>
  import {
    postByDeparturePlan
  } from "@/api/api";
  export default {
    name: "",
    data() {
      return {
        list: [{
            fastigium: 0,
            showDetails: false,
            time: [{
                tr: "0:00",
                pick: 0
              },
              {
                tr: "0:00",
                pick: 0
              },
              {
                tr: "0:00",
                pick: 1
              },
              {
                tr: "0:00",
                pick: 0
              }
            ]
          },
          {
            fastigium: 0,
            showDetails: false,
            time: [{
              tr: "0:00",
              pick: 0
            }]
          },
          {
            fastigium: 0,
            showDetails: false,
            time: [{
              tr: "0:00",
              pick: 0
            }]
          }
        ],
        busInfo: {},
        liList: "",
        isPick: false,
        busStatics: {},
        showTips: true,
      };
    },
    created() {},
    mounted() {
      this.busInfo = JSON.parse(sessionStorage.getItem("busInfo"));
      console.log(this.busInfo);
      this.busStatics = this.$route.params.busStatics
      postByDeparturePlan({
        direction: this.$route.params.busStatics.d,
        routeId: this.busInfo.ri
      }).then(res => {
        if (res.retCode == 0) {
          this.list = res.retData;
          var dateTime = new Date();
          let time = dateTime.getHours() * 60 * 60 + dateTime.getMinutes() * 60;
          for (let i = 0; i < this.list.length; i++) {
            let timeList = [];
            this.$set(this.list[i], "showDetails", false);
            for (let k = 0; k < this.list[i].list.length; k++) {
              timeList.push({
                time: this.list[i].list[k],
                pick: 0
              });
            }
            this.$set(this.list[i], "timeList", timeList);
          }
          for (let j = 0; j < this.list.length; j++) {
            for (let k = 0; k < this.list[j].timeList.length; k++) {
              let myTime = this.list[j].list[k].split(":");
              let validTime = myTime[0] * 60 * 60 + myTime[1] * 60;
              if (validTime - time > 0) {
                if (!this.isPick) {
                  this.$set(this.list[j].timeList[k], "pick", 1);
                  this.isPick = !this.isPick;
                }
                break;
              }
            }
          }
          console.log(this.list);
          // this.busInfo = this.$route.params.busStatics;
        } else {
          Dialog({
            message: res.retMsg
          });
        }
      });
    },
    methods: {
      clickDetails(index) {
        var i = index;
        this.$set(this.list[i], "showDetails", !this.list[i].showDetails);
      },
      removeTips() {
        this.showTips = !this.showTips;
      }
    }
  };
</script>

<style scoped lang="less">
  @blue: #24a1ee;
  @gray: #a1a1a1;
  @line: #e6e6e6;
  @height: 2.88;

  .bus-list-his {
    header {
      width: 100%;
      height: 88px * @height;
      box-sizing: border-box;
      padding: 4px * @height 0;
      background: #f2f2f2;

      .tit {
        width: 100%;
        height: 100%;
        background: #fff;

        .tit-bus {
          width: 92.4%;
          margin: 0 auto 0 auto;
          padding-top: 10px * @height;

          .tit-bus-name {
            font-size: 1.6rem * @height;
            font-weight: 600;
            margin-right: 4px * @height;
          }

          .tit-bus-com {
            font-size: 1.2rem * @height;
            color: #888888;
          }
        }

        .tit-st {
          width: 92.4%;
          margin: 10px * @height auto 0 auto;

          div {
            display: inline-block;
            font-size: 1.2rem * @height;
          }

          .fa {
            display: inline-block;
            font-size: 1.2rem * @height;
            margin: 0 2px * @height;
          }
        }
      }
    }

    section {
      width: 100%;

      .tips {
        background: @blue;
        height: 32px * @height;
        line-height: 32px * @height;
        font-size: 1.2rem * @height;
        color: #fff;
        text-align: center;
        position: relative;

        .x {
          position: absolute;
          right: 2%;
          line-height: 32px * @height;
        }
      }

      .his-list {
        width: 100%;

        .time-frame {
          box-sizing: border-box;
          padding: 0 10px * @height;
          height: 44px * @height;
          border-bottom: 1px solid @line;

          .time {
            display: inline-block;
            font-size: 1.2rem * @height;
            line-height: 44px * @height;
          }

          .hot {
            margin-left: 20px * @height;
            padding: 10px;
            font-size: 1rem * @height;
            line-height: 44px * @height;
            color: #f2f2f2;
            background-color: #f59961;
            border-radius: 5px * @height;
          }

          .icon {
            font-size: 1.2rem * @height;
            line-height: 44px * @height;
          }
        }

        .time-details {
          width: 100%;
          background: #f2f2f2;
          padding: 10px * @height;
          display: flex;
          flex-flow: wrap;
        
          li {
            // flex: 1;
            width: 25%;
            margin: 4px * @height 0;
            text-align: center;
            
            span {
              font-size: 1.2rem * @height;
            }

            .that {
              border: 1px * @height solid #f59961;
              padding: 2px * @height 4px * @height;
              border-radius: 20px * @height;
            }
          }
        }
      }
    }
  }
</style>