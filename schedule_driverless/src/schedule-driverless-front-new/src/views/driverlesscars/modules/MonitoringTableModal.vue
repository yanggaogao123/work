<template>
  <div class="main">
    <header>
      <div>
        <a-radio-group v-model="see" button-style="solid">
          <a-radio-button value="a">发班调度</a-radio-button>
          <a-radio-button value="b">支援计划</a-radio-button>
        </a-radio-group>
      </div>
    </header>
    <section>
      <div class="con-table1" v-if="see == 'a'">
        <div class="up-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="mainUpColumns"
              :dataSource="mainUpData"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ x: 1000, y: 400 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(text).format("HH:mm") }}</span>
              </span>

              <span slot="customTitle"
                ><a-icon type="arrow-up" style="color: #1890ff" />状态</span
              >
              <span slot="type" slot-scope="text, record">
                <span
                  class="colorBox"
                  v-if="['1', '2', '3', '4', '5'].includes(text)"
                  style="background: #ffff9b; color: #ffff9b"
                  >{{ text }}</span
                >
                <span
                  class="colorBox"
                  v-if="
                    ['0', '6', '7', '8'].includes(text) && record.status == '0'
                  "
                  style="background: #00ffff; color: #00ffff"
                  >{{ text }}</span
                >
                <span
                  class="colorBox"
                  v-if="
                    ['0', '6', '7', '8'].includes(text) && record.status == '9'
                  "
                  style="background: #009afe; color: #009afe"
                  >{{ text }}</span
                >
                <span
                  class="colorBox"
                  v-if="['0', '7'].includes(text) && record.status == '2'"
                  style="background: #0003cb; color: #0003cb"
                  >{{ text }}</span
                >
                <span
                  class="colorBox"
                  v-if="['0', '7'].includes(text) && record.status == '3'"
                  style="background: #bd0000; color: #bd0000"
                  >{{ text }}</span
                >
                <span
                  class="colorBox"
                  v-if="['0', '8'].includes(text) && record.status == '5'"
                  style="background: #bd0000; color: #bd0000"
                  >{{ text }}</span
                >
                <span
                  class="colorBox"
                  v-if="record.showBlue"
                  style="background: #0003cb; color: #0003cb"
                  >{{ text }}</span
                >
              </span>
            </a-table>
          </div>
          <!-- <div class="right-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="mainDownColumns"
              :dataSource="mainDownData"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div> -->
        </div>
        <div class="down-part">
          <!-- <div class="left-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="supUpColumns"
              :dataSource="supUpData"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="supDownColumns"
              :dataSource="supDownData"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div> -->
        </div>
      </div>
      <div class="con-table2" v-else>
        <div class="up-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="mainUpColumns"
              :dataSource="mainUpData"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 400 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(record).format("HH:mm:ss") }}</span>
              </span>
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="mainDownColumns"
              :dataSource="mainDownData"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 400 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(record).format("HH:mm:ss") }}</span>
              </span>
            </a-table>
          </div>
        </div>
        <div class="down-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="supUpColumns"
              :dataSource="supUpData"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :columns="supDownColumns"
              :dataSource="supDownData"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 400 }"
            >
            </a-table>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import Vue from "vue";
import axios from "axios";
import moment from "moment";
import "@/assets/less/base.css";
import "@/assets/less/TableExpand.less";
export default {
  name: "MonitoringTableModal",
  props: ["sendData"],
  // mixins: [JeecgListMixin],
  data() {
    return {
      mes: "",
      url: {
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
      },
      routeId: "",
      routeName: "",
      supRouteId: "",
      supRouteName: "",
      runDate: "",
      time: "",

      see: "a",
      seeType: "a",

      mainUpColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },

        // {
        //   title: `${this.baseData.routeName}(下行) 时段`,
        //   align: "center",
        //   dataIndex: "time",
        // },
        {
          // title: "状态",
          align: "center",
          slots: { title: "customTitle" },
          dataIndex: "msgName",
          width: 70,
        },
        {
          title: "车辆",
          align: "center",
          dataIndex: "busName",
          width: 70,
        },
        {
          title: "时间",
          align: "center",
          dataIndex: "time",
          width: 70,
          scopedSlots: { customRender: "time" },
        },
        {
          title: "驾驶员",
          align: "center",
          dataIndex: "employeeName",
          width: 70,
        },
        {
          title: "发班间隔",
          align: "center",
          dataIndex: "interval",
          width: 70,
        },
        {
          title: "内容",
          align: "center",
          dataIndex: "content",
          // width: "40%",
        },
      ],
      mainDownColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },

        // {
        //   title: `${this.baseData.routeName}(下行) 时段`,
        //   align: "center",
        //   dataIndex: "time",
        // },
        {
          title: "(上行)状态",
          align: "center",
          dataIndex: "msgName",
          width: "10%",
        },
        {
          title: "车辆",
          align: "center",
          dataIndex: "busName",
          width: "10%",
        },
        {
          title: "时间",
          align: "center",
          dataIndex: "time",
          width: "10%",
          scopedSlots: { customRender: "time" },
        },
        {
          title: "驾驶员",
          align: "center",
          dataIndex: "employeeName",
          width: "10%",
        },
        {
          title: "发班间隔",
          align: "center",
          dataIndex: "interval",
          width: "10%",
        },
        {
          title: "内容",
          align: "center",
          dataIndex: "content",
          width: "40%",
        },
      ],
      supUpColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },

        // {
        //   title: `${this.baseData.routeName}(下行) 时段`,
        //   align: "center",
        //   dataIndex: "time",
        // },
        {
          title: "(上行)状态",
          align: "center",
          dataIndex: "msgName",
          width: "10%",
        },
        {
          title: "车辆",
          align: "center",
          dataIndex: "busName",
          width: "10%",
        },
        {
          title: "时间",
          align: "center",
          dataIndex: "time",
          width: "10%",
          scopedSlots: { customRender: "time" },
        },
        {
          title: "驾驶员",
          align: "center",
          dataIndex: "employeeName",
          width: "10%",
        },
        {
          title: "发班间隔",
          align: "center",
          dataIndex: "interval",
          width: "10%",
        },
        {
          title: "内容",
          align: "center",
          dataIndex: "content",
          width: "40%",
        },
      ],
      supDownColumns: [
        // {
        //   title: "序号",
        //   dataIndex: "",
        //   key: "rowIndex",
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   },
        // },

        // {
        //   title: `${this.baseData.routeName}(下行) 时段`,
        //   align: "center",
        //   dataIndex: "time",
        // },
        {
          title: "(上行)状态",
          align: "center",
          dataIndex: "msgName",
          width: "10%",
        },
        {
          title: "车辆",
          align: "center",
          dataIndex: "busName",
          width: "10%",
        },
        {
          title: "时间",
          align: "center",
          dataIndex: "time",
          width: "10%",
          scopedSlots: { customRender: "time" },
        },
        {
          title: "驾驶员",
          align: "center",
          dataIndex: "employeeName",
          width: "10%",
        },
        {
          title: "发班间隔",
          align: "center",
          dataIndex: "interval",
          width: "10%",
        },
        {
          title: "内容",
          align: "center",
          dataIndex: "content",
          width: "40%",
        },
      ],

      mainUpData: [],
      mainDownData: [],
      supUpData: [],
      supDownData: [],
    };
  },
  watch: {
    sendData() {
      console.log("sendData", this.sendData);
      this.centerData = this.sendData.centerData;
      this.runDate = this.sendData.runDate;
      this.time = moment(this.sendData.time, "HH:mm:ss");
      this.routeId = this.sendData.routeId;
      this.routeName = this.sendData.routeName;
      this.supRouteId = this.sendData.supRouteId;
      this.supRouteName = this.sendData.supRouteName;
      if (this.mainUpColumns[0].title != this.routeName) {
        this.mainUpColumns.unshift({
          title: this.routeName,
          align: "center",
          dataIndex: "type",
          width: 74,
          scopedSlots: { customRender: "type" },
        });
        this.mainDownColumns.unshift({
          title: this.routeName,
          align: "center",
          dataIndex: "type",
          width: "10%",
          scopedSlots: { customRender: "type" },
        });
        this.supUpColumns.unshift({
          title: this.supRouteName,
          align: "center",
          dataIndex: "type",
          width: "10%",
          scopedSlots: { customRender: "type" },
        });
        this.supDownColumns.unshift({
          title: this.supRouteName,
          align: "center",
          dataIndex: "type",
          width: "10%",
          scopedSlots: { customRender: "type" },
        });
      }
      this.tableInit();
    },
    seeType() {
      if (this.seeType == "a") {
        this.tableInit();
      } else if (this.seeType == "b") {
        this.tableInit2();
      }
    },
  },
  created() {
    const queryString = window.location.search;
    const searchParams = new URLSearchParams(queryString);
    const paramString = searchParams.get("paramString");
    // console.log(paramString);
    this.mes = new URLSearchParams();
    this.mes.append("paramString", paramString);
    // console.log(this.mes.toString());
    // this.tableInit();
    console.log("see", this.see);
    console.log("see2", this.seeType);
    console.log(this.mainUpColumns);
  },
  mounted() {
    let elements = document.getElementsByClassName("ant-table-header");
    console.log(elements);
  },
  methods: {
    moment,
    tableInit() {
      let params = this.mes;
      let data = {
        routeId: this.routeId,
        runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
      };
      axios.post(`${this.url.getMonitorInfo}`, data, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }

        let info = res.data.info;
        let runBus = res.data.runBus;

        info.forEach((item) => {
          const matchRunBusInfo = runBus.find((runItem) => {
            return item.busId == runBus.runBus && runItem.runStatus == "1";
          });
          if (matchRunBusInfo) {
            Vue.set(item, "showBlue", true);
          } else {
            Vue.set(item, "showBlue", false);
          }
        });

        this.mainUpData = info.filter((item) => {
          return item.direction == "0";
        });
        console.log("mainUpData", this.mainUpData);
        this.mainDownData = info.filter((item) => {
          return item.direction == "1";
        });
      });

      let data1 = {
        routeId: this.supRouteId,
        runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
      };
      axios
        .post(`${this.url.getMonitorInfo}`, data1, { params })
        .then((res) => {
          console.log(res);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }

          this.supUpData = res.data.info.filter((item) => {
            return item.direction == "0";
          });
          this.supDownData = res.data.info.filter((item) => {
            return item.direction == "1";
          });
        });
    },
    tableInit2() {},
  },
};
</script>

<style lang="less" scoped>
.main {
  width: 100%;
  background: #dbefff;

  header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 70px;
    box-sizing: border-box;
    padding: 0 70px;
    border-top: 1px solid #99bbe8;
    border-bottom: 1px solid #99bbe8;
    .name {
      font-size: 16px;
      font-weight: 600;
    }
  }
  section {
    .con-table1,
    .con-table2 {
      width: 100%;
      padding: 0 70px;
      box-sizing: border-box;
      .up-part,
      .down-part {
        display: flex;
        height: 384px;
        .left-part,
        .right-part {
          width: 50%;
          height: 100%;
          box-sizing: border-box;
          div {
            width: 100%;
            height: 100%;
          }
        }
      }
      .up-part {
        margin-top: 20px;
      }
    }

    .con-table {
      .up-part,
      .down-part {
        display: flex;
        height: 484px;
        .left-part,
        .right-part {
          width: 50%;
          height: 100%;
          box-sizing: border-box;
          div {
            width: 100%;
            height: 100%;
          }
        }
      }
      .down-part {
        margin-top: 20px;
      }
    }

    /deep/.ant-table td {
      white-space: nowrap !important;
    }
    /deep/table,
    /deep/td,
    /deep/tr,
    /deep/th {
      font-size: 12px !important;
    }
    .colorBox {
      display: block;
      width: 100%;
      height: 100%;
    }
    .table-blue {
      /deep/.ant-table-thead tr th {
        background: #b8deff !important;
      }
      /deep/.ant-table-hide-scrollbar {
        // min-width: 5px !important;
      }
    }
    .table-blue ::-webkit-scrollbar {
      // width: 5px;
      //   height: 2px;
      //   background: #ccc;
      //   border-radius: 10px; /*外层轨道*/
    }
    .table-green {
      /deep/.ant-table-thead tr th {
        background: #bce9f0 !important;
      }
      /deep/.ant-table-hide-scrollbar {
        min-width: 5px !important;
      }
    }
    .table-green ::-webkit-scrollbar {
      width: 5px;
      //   height: 2px;
      //   background: #ccc;
      //   border-radius: 10px; /*外层轨道*/
    }
  }
}
</style>
