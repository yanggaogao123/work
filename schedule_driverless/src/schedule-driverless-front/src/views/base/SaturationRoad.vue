<template>
  <a-card :bordered="false">
    
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="道路id">
              <a-select v-model="selectRoadId">
                <a-select-option v-for="item in roadList" :key="item.roadId">{{item.roadName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <!-- <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button> -->
              <!-- <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a> -->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    <div class="content">
      <div class="top">
        <div></div>
        <div class="tips">
          <div class="l0">
            <div class="color-box"></div>低负荷(0-6)
          </div>
          <div class="l1">
            <div class="color-box"></div>中负荷(7-10)
          </div>
          <div class="l2">
            <div class="color-box"></div>高负荷(10以上)
          </div>
        </div>
      </div>
      <div class="container">
        <div v-for="staurationItem in staurationData" :key="staurationItem.roadId">
          <div class="station-list">
            <div class="station-item" v-for="item,index in staurationItem.stations" :key="index">
              <div class="up">
                <div :class="{l0: item.upLevel == '0', l1: item.upLevel == '1', l2: item.upLevel == '2'}">{{item.up}}
                </div>
              </div>
              <div class="down">
                <div :class="{l0: item.downLevel == '0', l1: item.downLevel == '1', l2: item.downLevel == '2'}">
                  {{item.down}}</div>
              </div>
              <div class="title">
                <div class="title-box">{{item.stationName}}</div>
              </div>
            </div>

          </div>
          <div class="total-data">
            <div class="total">车辆总数:{{staurationItem.totalCount}}</div>
            <div class="up">去程:{{staurationItem.upTotalCount}}</div>
            <div class="down">回程:{{staurationItem.downTotalCount}}</div>
          </div>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script>
  import {
    getAction
  } from "@/api/manage";
  import {
    connection,
    disConnection,
    unsubscribe,
    changeSendData
  } from '@/utils/sorket'

  export default {
    components: {},
    props: {

    },
    data() {
      return {
        staurationData: [],
        url: {
          getRoadList: "/base/saturation/roadList",
        },
        stompObj: {},// 用于存放sorket实例
        socketUrl: '',
        selectRoadId: '',
        roadList: [],
        isConnection: false,
      };
    },
    watch: {

    },
    computed: {},
    created() {
    },
    mounted() {
      this.getRoadList(this.stompObj)
      // this.connection()
    },
    destroyed: function () { // 离开页面生命周期函数
      // that.$message.warning('websorket连接断开')
      disConnection()
      this.isConnection = false
    },
    methods: {
      getRoadList() {
        getAction(this.url.getRoadList, {}).then((res) => {
          if (res.success) {
            this.roadList = res.result
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      searchQuery() {
        if(this.isConnection){
          changeSendData(this.stompObj, this.selectRoadId)
        }else {
          this.connection()
        }
      },
      // searchReset() {
      //   this.selectRoadId = ''
      // },
      connection() {
        let that = this
        let sendData = { roadId: this.selectRoadId }
        this.isConnection = true
        connection(that.stompObj, 'saturation-road', sendData, function (e) {
          console.log('websorket连接:', e)
          that.bookServe(e)
          // setTimeout(()=>{
          // that.$message.warning('websorket取消订阅')
          // unsubscribe()
          // that.$message.warning('websorket连接断开')
          // disConnection()
          // },30*1000)
        })
      },
      bookServe(data) { //对websorket返回的数据进行处理
        let that = this
				if(data.code == 200){
					let tmpArr = data.result
					// 数据处理
					this.staurationData = []
					for(let key in tmpArr){
						let tmpItem = tmpArr[key][0]
						this.staurationData.push({roadId: key})
						let k = this.staurationData.length - 1
						let totalData = {
							downTotalCount: tmpItem.downTotalCount,
							totalCount: tmpItem.totalCount,
							upTotalCount: tmpItem.upTotalCount,
						}
						this.staurationData[k] = Object.assign(this.staurationData[k], totalData)
						this.staurationData[k].stations = []
						for(let i = 0;i < tmpItem.vsaturationDetailList.length;i++){
							let item = tmpItem.vsaturationDetailList[i]
							if(this.staurationData[k].stations.filter(stationItem => stationItem.stationName == item.staGroupName).length == 0){
								if(item.direction == '0'){
									this.staurationData[k].stations.push({
										stationName: item.staGroupName,
										up: item.busCount,
										upLevel: item.level,
										down: 0,
										downLevel: 0,
									})
								}else {
									this.staurationData[k].stations.push({
										stationName: item.staGroupName,
										up: 0,
										upLevel: 0,
										down: item.busCount,
										downLevel: item.level,
									})
								}
							}else {
								if(item.direction == '0'){
									this.staurationData[k].stations.filter(stationItem => stationItem.stationName == item.staGroupName)[0].up = item.busCount
									this.staurationData[k].stations.filter(stationItem => stationItem.stationName == item.staGroupName)[0].upLevel = item.busCount.level
								}else {
									this.staurationData[k].stations.filter(stationItem => stationItem.stationName == item.staGroupName)[0].down = item.busCount
									this.staurationData[k].stations.filter(stationItem => stationItem.stationName == item.staGroupName)[0].downLevel = item.level
								}
							}
						}
					}
					console.log(this.staurationData)
				}else {
					this.$message.warning(data.message)
				}
      },
    },
  };
</script>

<style lang='less' scoped>
  .content {
    width: 100%;

    .top {
      width: 100%;
      height: 24px;
      margin-bottom: 32px;
      display: flex;
      justify-content: space-between;

      .tips {
        font-size: 16px;
        font-weight: 600;

        >div {
          margin-right: 16px;
          display: inline-block;

          .color-box {
            width: 30px;
            height: 20px;
            margin: 0 10px;
            border-radius: 4px;
            display: inline-block;
            vertical-align: text-bottom;
          }
        }

        .l0 {
          .color-box {
            background: #9FDD5C;
          }
        }

        .l1 {
          .color-box {
            background: #FFC300;
          }
        }

        .l2 {
          .color-box {
            background: #FB6400;
          }
        }
      }
    }

    .container {
      .station-list {
        width: 90%;
        margin: 0 auto;
        display: flex;
        flex-wrap: wrap;

        .station-item {
          max-width: 40%;
          display: flex;
          flex-flow: column;
          flex: 1;

          .up {
            width: 100%;
            margin-bottom: 16px;
            position: relative;
            transform: translateX(50%);

            >div {
              width: 100%;
              line-height: 30px;
              height: 30px;
              text-align: center;
              font-size: 16px;
              font-weight: 600;
              color: #fff;
              // border: 1px solid rgba(0,0,0,.2);
              position: relative;
            }

            .l0 {
              background: #9FDD5C;
            }

            .l1 {
              background: #FFC300;
            }

            .l2 {
              background: #FB6400;
            }

            >div::after {
              content: '';
              width: 18px;
              height: 18px;
              background: #fff;
              border-radius: 50%;
              -webkit-box-shadow: 0 0 5px;
              box-shadow: 0 0 4px #000;
              position: absolute;
              top: 50%;
              left: -10px;
              transform: translate(0, -50%);
            }
          }

          .down {
            width: 100%;
            position: relative;
            transform: translateX(50%);

            >div {
              width: 100%;
              line-height: 30px;
              height: 30px;
              text-align: center;
              font-size: 16px;
              font-weight: 600;
              color: #fff;
              // border: 1px solid rgba(0,0,0,.2);
            }

            .l0 {
              background: #9FDD5C;
            }

            .l1 {
              background: #FFC300;
            }

            .l2 {
              background: #FB6400;
            }

            >div::after {
              content: '';
              width: 18px;
              height: 18px;
              background: #fff;
              border-radius: 50%;
              -webkit-box-shadow: 0 0 5px;
              box-shadow: 0 0 4px #000;
              position: absolute;
              top: 50%;
              left: -10px;
              transform: translate(0, -50%);
            }
          }

          .title {
            .title-box {
              width: 24px;
              margin: 12px auto;
              font-size: 16px;
              text-align: center;
            }
          }
        }
      }

      .total-data {
        margin: 24px 0 36px;
        display: flex;
        font-size: 18px;
        font-weight: 600;
        justify-content: center;

        >div {
          margin-right: 14px;
        }
      }
    }
  }
</style>