<template>
  <div class="busListTime" v-cloak>
    <div class="bus-num">
      <p>车牌号：{{plateNumber}}</p>
    </div>
    <ul class="con clearfix" v-if="stationList.d">
      <li v-for="(item, index) in stationList.d.l" :key="item.i">
        <p class="station fl">{{item.n}}</p>
        <p class="time arrived">
          <span class="send" v-if="index == 0">预计发车{{stationList.d.fbt}}</span>
          <span v-else-if="item.ti == -1">已过站</span>
          <span v-else-if="item.ti != -1">{{item.ti}}分钟后到达此站</span>
        </p>
      </li>
    </ul>
  </div>
</template>

<script>
import { postBySubId } from "@/api/api";
import { Dialog } from "vant";
export default {
  name: "busListTime",
  components: {},
  data() {
    return {
      stationList: {},
      bus: {},
      plateNumber: null
    };
  },
  methods: {},
  created() {
    this.bus = JSON.parse(sessionStorage.getItem("busInfo"));
    this.plateNumber = this.$route.params.busStatics.plateNumber;
    postBySubId({
      subId: this.$route.params.busStatics.subId,
      busId: this.$route.params.busStatics.id
    }).then(res => {
      if (res.retCode == 0) {
        this.stationList = res.retData;
      } else {
        Dialog({ message: res.retMsg });
      }
    });
  }
};
</script>

<style scoped lang="less">
@height: 2.88;
.busListTime {
  position: relative;
  width: 100%;
  margin-bottom: 80px * @height;
  .bus-num {
    background-color: #ffffff;
    border: 1px solid #e6e6e6;
    p {
      display: inline-block;
      font-size: 1.6rem * @height;
      line-height: 48px * @height;
    }
    p:before {
      content: "";
      width: 18px * @height;
      height: 22px * @height;
      display: inline-block;
      background: url("~@/assets/ic_bus@2x.png") no-repeat;
      background-size: 18px * @height 22px * @height;
      vertical-align: middle;
      margin: 0 10px * @height 0 14px * @height;
    }
  }
  .con {
    margin-top: 8px * @height;
    li {
      min-height: 50px * @height;
      height: auto;
      width: 100%;
      background-color: #ffffff;
      border-bottom: 1px solid #e6e6e6;
      position: relative;
      .station {
        font-size: 1.6rem * @height;
        margin: 9px * @height 0 0 14px * @height;
        max-width: 56%;
        overflow: hidden;
        height: 2rem * @height;
      }
      .time {
        position: absolute;
        top: 9px * @height;
        left: 63.6%;
        font-size: 1.6rem * @height;
      }
      .send {
        color: #5197e6;
      }
      .arrived {
        color: #a1a1a1;
      }
      .arrive {
        color: #a1a1a1;
      }
      .orange {
        color: #f66f40;
      }
    }
  }
}
</style>