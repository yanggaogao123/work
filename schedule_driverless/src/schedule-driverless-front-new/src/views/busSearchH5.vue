<template>
  <div class="busSearchH5">
    <header>
      <div class="ipt-search">
        <input type="text" v-model="searchName" @input="changeInput" />
        <i class="iconfont icon-close" @click="clearInput"></i>
      </div>
      <button class="btn-search" @click="backToBusIndex">取消</button>
    </header>
    <section>
      <ul>
        <template>
          <li
            v-for="(item,index) in searchInfoList.bus.route"
            :key="item.i"
            @click="goBusList(item)"
          >
            <div class="left">
              <i class="iconfont icon-luxian"></i>
            </div>
            <div class="middle">
              <div class="tit">{{item.n}}</div>
              <div class="info">{{item.start}} -- {{item.end}}</div>
            </div>
            <div class="right">
              <i class="iconfont icon-more1"></i>
            </div>
          </li>

          <li
            v-for="(item,index) in searchInfoList.bus.station"
            :key="item.i"
            @click="goStationInfo(item)"
          >
            <div class="left">
              <i class="iconfont icon-busstopd"></i>
            </div>
            <div class="middle">
              <div class="tit">{{item.n}}</div>
              <div class="info">途经{{item.c}}条线路</div>
            </div>
            <div class="right">
              <i class="iconfont icon-more1"></i>
            </div>
          </li>
          <!-- 暂无当行页面，待确认
            <li v-for="(item,index) in searchInfoList.gd" :key="item.id">
            <div class="left">
              <i class="iconfont icon-ddd"></i>
            </div>
            <div class="middle">
              <div class="tit">{{item.name}}</div>
              <div class="info">{{item.address}}</div>
            </div>
            <div class="right">
              <i class="iconfont icon-more1"></i>
            </div>
          </li>-->
        </template>

        <div class="btn-clear" v-show="btnClearShow" @click="clearHis">清空历史记录</div>
      </ul>
    </section>
  </div>
</template>

<script>
import { postByName } from "@/api/api";
let timeout;
export default {
  name: "busSearchH5",
  data() {
    return {
      list: [1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 12, 3, 4, 5, 5, 6, 7, 8, 8, 9, 0, 0],
      searchName: "",
      btnClearShow:true,
      searchInfoList: {
        bus: {
          station: [],
          route: []
        }
      }
    };
  },
  created() {
    this.loadHistory()
  },
  methods: {
    loadHistory() {
      if(localStorage.getItem("busInfoHis")){
        let arr = JSON.parse(localStorage.getItem("busInfoHis"));
        for(var i=0;i<arr.length;i++){
          if(arr[i].start){
            this.searchInfoList.bus.route.push(arr[i]);
          }else if(arr[i].longitude){
            this.searchInfoList.bus.station.push(arr[i]);
          }
        }
        console.log(this.searchInfoList);
      }
    },
    changeInput() {
      if (timeout) {
        clearTimeout(timeout);
        timeout = null;
      }
      //设置延迟请求接口，避免多次请求搜索接口导致数据错乱
      timeout = setTimeout(() => {
        this.loadSearch();
      }, 200);
    },
    clearInput() {
      this.searchName = "";
      this.searchInfoList.bus.route = [];
      this.searchInfoList.bus.station = [];
      this.btnClearShow = true;
      this.loadHistory();
    },
    loadSearch() {
      console.log(1);
      if(this.searchName == ''){
        this.searchInfoList.bus.route = [];
        this.searchInfoList.bus.station = [];
        this.btnClearShow = true;
        this.loadHistory();
      }else{
        this.btnClearShow = false;
      }
      postByName({
        name: this.searchName
      }).then(res => {
        if (res.retCode == 0) {
          this.searchInfoList = res.retData;

        } else {
          Dialog({ message: res.retMsg });
        }
      });
    },
    goBusList(item) {
      sessionStorage.setItem(
        "busInfo",
        JSON.stringify({
          d: 0,
          dn: item.start + "-" + item.end,
          ri: item.i,
          rn: item.n
        })
      );

      if(localStorage.getItem("busInfoHis")){
        let arr = JSON.parse(localStorage.getItem("busInfoHis"));
        console.log(arr);
        arr.unshift(item);
        for(var i=0; i<arr.length; i++){
            for(var j=i+1; j<arr.length; j++){
                if(arr[i].n==arr[j].n){  
                    arr.splice(j,1);
                    j--;
                }
            }
        }
        localStorage.setItem("busInfoHis",JSON.stringify(arr));
      }else{
        let arr = [];
        console.log(item);
        arr.push(item);
        localStorage.setItem("busInfoHis",JSON.stringify(arr));
      }


      this.$router.push({
        name: "busListH5"
      });
    },
    goStationInfo(item) {
      sessionStorage.setItem("stationInfo", JSON.stringify(item));
      if(localStorage.getItem("busInfoHis")){
        let arr = JSON.parse(localStorage.getItem("busInfoHis"));
        console.log(arr);
        arr.unshift(item);
        for(var i=0; i<arr.length; i++){
            for(var j=i+1; j<arr.length; j++){
                if(arr[i].n==arr[j].n){  
                    arr.splice(j,1);
                    j--;
                }
            }
        }
        localStorage.setItem("busInfoHis",JSON.stringify(arr));
      }else{
        let arr = [];
        console.log(item);
        arr.push(item);
        localStorage.setItem("busInfoHis",JSON.stringify(arr));
      }
      
      this.$router.push({
        name: "busStationInfoH5"
      });
    },
    backToBusIndex() {
      this.$router.push({ name: "busIndexH5" });
    },
    clearHis(){
      let arr = [];
      localStorage.setItem("busInfoHis",arr);
      this.searchInfoList.bus.route = [];
      this.searchInfoList.bus.station = [];
      
    }
  }
};
</script>

<style lang="less" scoped>
@import "../assets/font/iconfont.css";
@bg: #eeeeee;
@blue: #24a1ee;
@line: #e6e6e6;
@gray: #888;
.busSearchH5 {
  height: 100%;
  header {
    display: flex;
    height: 54px;
    border-bottom: 1px solid @line;
    .ipt-search {
      width: 82%;
      height: 30px;
      border: 1px solid @line;
      border-radius: 15px;
      position: relative;
      margin: 10px 2%;
      input {
        width: 80%;
        height: 28px;
        line-height: 28px;
        margin: 1px 0 0 4%;
        font-size: 1.4rem;
        border: none;
      }
      i {
        position: absolute;
        top: 4px;
        right: 3%;
        font-size: 1.6rem;
      }
    }
    .btn-search {
      width: 14%;
      border: none;
      background: none;
      font-size: 1.4rem;
      color: @blue;
    }
  }
  section {
    height: calc(100% - 54px);
    overflow: scroll;
    ul {
      width: 100%;
      height: 100%;
      li {
        display: flex;
        height: 48px;
        box-sizing: border-box;
        border-bottom: 1px solid @line;
        .left {
          flex: 1;
          text-align: center;
          i {
            font-size: 2rem;
            line-height: 47px;
            color: @blue;
          }
        }
        .middle {
          flex: 8;
          // display: flex;
          // width: 100%;
          .tit {
            font-size: 1.4rem;
            font-weight: 600;
            margin: 4px 0 0 0;
          }
          .info {
            font-size: 1.2rem;
            color: @gray;
            width: 260px;
            // flex: 1;
            height: 15px;
            overflow:hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            
          }
        }
        .right {
          flex: 1;
          text-align: center;
          i {
            font-size: 2rem;
            line-height: 47px;
          }
        }
      }
      .btn-clear {
        font-size: 1.2rem;
        font-weight: 600;
        text-align: center;
        margin: 10px 0;
      }
    }
  }
}
</style>