<template>
  <div id="driverTraining">
    <ul class="list">
      <li v-for="(item, index) in list" @click="go(index)">
        <img :src="item.img" alt="" />
        <div>{{ item.name }}</div>
      </li>
    </ul>

    <!-- <div class="bottom">
      <div class="bottom-tit">
        <div class="line"></div>
        <p>驾培学费第三方存管</p>
      </div>
      <ul class="bottom-list">
        <li v-for="(item, index) in bList" @click="gogo(index)">
          <div class="area">
            <img :src="item.img" alt="" />
          </div>
        </li>
      </ul>
    </div> -->
  </div>
</template>

<script>
export default {
  name: "driverTraining",
  data() {
    return {
      list: [
        {
          img: require("../assets/dt4.png"),
          name: "学车报名",
        },
        {
          img: require("../assets/dt3.png"),
          name: "驾校查询",
        },
        {
          img: require("../assets/dt1.png"),
          name: "教练车查询",
        },
        {
          img: require("../assets/dt2.png"),
          name: "教练员查询",
        },
      ],
      bList: [
        {
          img: require("../assets/dt4-1.png"),
          name: "支付宝“穗学车”渠道",
        },
        {
          img: require("../assets/dt5-1.png"),
          name: "银行“二类卡”渠道",
        },
      ],
    };
  },
  created() {
    document.title = "学车";
  },
  mounted() {},
  methods: {
    go(i) {
      switch (i) {
        case 0:
          let url = `https://render.alipay.com/p/s/i/?scheme=alipays://platformapi/startapp?appId=2021002148608911&thirdPartSchema=gzshb%3A%2F%2Fgzshb.gzonline.gov.cn%2Fapplet&query=thirdPartSchema%3Dgzshb%3A%2F%2Fgzshb.gzonline.gov.cn%2Fapplet`;
          var u = navigator.userAgent,
            app = navigator.appVersion;
          var isAndroid = u.indexOf("Android") > -1; //android终端或者uc浏览器
          var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
          if (isAndroid) {
            AndroidApp.sendHtml(url);
          } else if (isiOS) {
            window.GCI.sendHtml(url);
          }
          break;
        case 1:
          window.location.href =
            "http://dlysgzh.gdcd.gov.cn/jiapei/index.html#/DriverSchool/DriverSchoolList";
          break;
        case 2:
          window.location.href =
            "http://dlysgzh.gdcd.gov.cn/jiapei/index.html#/DriverCoachCar/DriverCoachCarList";
          break;
        case 3:
          window.location.href =
            "http://dlysgzh.gdcd.gov.cn/jiapei/index.html#/DriverCoach/DriverCoachList";
          break;
        default:
          break;
      }
    },
    gogo(i) {
      switch (i) {
        case 0:
          let url = `https://render.alipay.com/p/s/i/?scheme=alipays://platformapi/startapp?appId=2021002148608911&thirdPartSchema=gzshb%3A%2F%2Fgzshb.gzonline.gov.cn%2Fapplet&query=thirdPartSchema%3Dgzshb%3A%2F%2Fgzshb.gzonline.gov.cn%2Fapplet`;
          var u = navigator.userAgent,
            app = navigator.appVersion;
          var isAndroid = u.indexOf("Android") > -1; //android终端或者uc浏览器
          var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
          if (isAndroid) {
            AndroidApp.sendHtml(url);
          } else if (isiOS) {
            window.GCI.sendHtml(url);
          }

          break;
        case 1:
          window.location.href =
            "http://wx.gzjpxh.com/wxnew/#/netsign/introduce";
          break;
        default:
          break;
      }
    },
  },
};
</script>

<style scoped lang="less">
html,
body {
  width: 100%;
  height: 100%;
}
#driverTraining {
  width: 100%;
  height: 100%;
  background: #edeeef;
  .list {
    width: 90.5%;
    padding-top: 30px;
    margin: 0 auto;
    li {
      width: 100%;
      margin: 16px 0 0 0;
      height: 88px;
      background: #fff;
      border-radius: 10px;
      position: relative;
      img {
        display: inline-block;
        width: 64px;
        height: 64px;
        margin: 13px 0 0 7.2%;
      }
      div {
        display: inline-block;
        position: absolute;
        font-size: 1.8rem;
        line-height: 88px;
        left: 32.4%;
        top: 0;
        font-weight: 800;
      }
    }
  }

  .bottom {
    width: 90.5%;
    margin: 0 auto;
    .bottom-tit {
      width: 100%;
      height: 26px;
      position: relative;
      margin: 20px 0 10px 0;
      .line {
        width: 100%;
        height: 1px;
        background: gray;
        position: absolute;
        top: 12px;
      }
      p {
        position: absolute;
        // width: max-content;
        // width: -webkit-max-content; // 兼容
        // width: -moz-max-content; // 兼容
        width: 42%;
        text-align: center;
        font-size: 1.2rem;
        font-weight: 600;
        background: #edeeef;
        line-height: 26px;
        left: 0;
        right: 0;
        margin: 0 auto;
        padding: 0 4px;
      }
    }
    .bottom-list {
      display: flex;
      li {
        width: 50%;
        height: 88px;
        padding: 0 10px;
        .area {
          width: 100%;
          height: 88px;
          background: #fff;
          border-radius: 10px;
          text-align: center;
          img {
            // width: 50px;
            // margin: 6px 0 5px 0;
            width: 100%;
            // height: 100%;
          }
          div {
            font-size: 1.4rem;
            font-weight: 600;
          }
        }
      }
    }
  }
}
</style>
