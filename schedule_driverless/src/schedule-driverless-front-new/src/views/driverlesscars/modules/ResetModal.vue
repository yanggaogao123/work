<template>
  <a-modal title="发班重打时间" :visible="visible" :width="width" switchFullscreen @ok="handleOk" :confirmLoading="clickFlag" :destroyOnClose="true">
    <template slot="footer">
      <a-button key="back" @click="handleCancel"> 关闭 </a-button>
      <a-button type="primary" @click="handleOk" v-show="!countBool"> 确认 </a-button>
      <a-button type="primary" @click="recount" v-show="countBool"> 重新计算 </a-button>
    </template>
    <a-spin tip="Loading..." :spinning="spinning">
      <div class="top">
        <div class="left"><div>非调度员请按关闭按钮,关闭弹出窗口!</div></div>
        <div class="middle">
          <div class="time-count">{{ timeNum }}</div>
          <a-button style="margin: 20px 0" @click="stopTimer">暂停</a-button>
        </div>
        <div class="right"><div>不同意某台车重发时间,取消勾选,确认即可!</div></div>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        :rowKey="(record) => record.routeSubId"
        :columns="columns"
        :dataSource="tableData"
        :pagination="false"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        class="j-table-force-wrap"
      >
        <template slot="title" slot-scope="currentPageData">发班重打时间{{ routeName }}（{{ resetData.expireTime }}）-{{ resetData.reDispatchTriggerType }}</template>
        <span slot="direction" slot-scope="text, record">
          {{ text == 0 ? '上行' : '下行' }}
        </span>
        <span slot="serviceName" slot-scope="text, record" @click="showLine(record)" style="color: #1890ff; cursor: pointer">
          {{ text }}
        </span>
        <span slot="tripBeginTime" slot-scope="text, record">
          {{ moment(text).format('HH:mm') }}
        </span>
        <span slot="newTripBeginTime" slot-scope="text, record">
          {{ moment(text).format('HH:mm') }}
        </span>
      </a-table>

      <reset-sub-line-modal ref="ResetSubLineModal" @receive="receiveMsg"></reset-sub-line-modal>
    </a-spin>
  </a-modal>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import moment from 'moment';
import '@/assets/less/base.css';
import '@/assets/less/TableExpand.less';
import 'moment/locale/zh-cn';
import ResetSubLineModal from './ResetSubLineModal.vue';
moment.locale('zh-cn');

export default {
  name: 'ResetModal',
  components: {
    ResetSubLineModal,
  },
  data() {
    return {
      url: {
        redispatchList: `${process.env.VUE_APP_BUS_API}/monitor/redispatchList`,
        dispatchTaskFromMainStation: `${process.env.VUE_APP_BUS_API}/monitor/dispatchTaskFromMainStation`,
        routeReDispatch: `${process.env.VUE_APP_BUS_API}/monitor/routeReDispatch`,
        redispatch: `${process.env.VUE_APP_BUS_API}/monitor/redispatch`,
      },
      spinning: false,
      visible: false,
      clickFlag: false,
      selectType: 'id',
      isGetOption: true,
      confirmLoading: false,
      width: 620,

      routeId: '',
      routeName: '',
      supRouteId: '',
      supRouteName: '',
      direction: '',

      columns: [
        {
          title: '方向',
          align: 'center',
          dataIndex: 'direction',
          scopedSlots: { customRender: 'direction' },
          // width: 70,
        },
        {
          title: '车辆名称',
          align: 'center',
          dataIndex: 'busName',
          scopedSlots: { customRender: 'busName' },
          // width: 70,
        },
        {
          title: '任务类型',
          align: 'center',
          dataIndex: 'serviceName',
          scopedSlots: { customRender: 'serviceName' },
          // width: 70,
        },
        {
          title: '原调度时间',
          align: 'center',
          dataIndex: 'tripBeginTime',
          scopedSlots: { customRender: 'tripBeginTime' },
          // width: 70,
        },
        {
          title: '新调度时间',
          align: 'center',
          dataIndex: 'newTripBeginTime',
          scopedSlots: { customRender: 'newTripBeginTime' },
          // width: 70,
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'name',
          scopedSlots: { customRender: 'calCulMark' },
          // width: 70,
        },
      ],
      tableData: [],
      resetData: '',
      busList: [],
      selectedRowKeys: [],
      timer: '',
      timeNum: 90,
      countBool: false,
    };
  },
  filters: {
    ensureZero: function (value) {
      return value || value == 0 ? value : 0;
    },
  },
  created() {},
  methods: {
    moment,
    async edit(record) {
      let params = this.mes;
      console.log(record);
      this.visible = true;
      this.routeName = record.routeName;
      this.routeId = Number(record.routeId);
      this.direction = record.direction;
      axios.get(`${this.url.redispatchList}/${this.routeId}`).then((res) => {
        console.log('redispatchList', res.data.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.resetData = res.data.data[this.routeId];
        this.tableData = res.data.data[this.routeId].reDispatchList;

        clearInterval(this.timer);
        this.timeNum = 90;
        this.timer = setInterval(() => {
          if (this.timeNum >= 1) {
            this.timeNum -= 1;
          }
          if (this.timeNum <= 0) {
            clearInterval(this.timer);
            this.handleOk();
          }
        }, 1000);
      });
    },
    onSelectChange(selectedRowKeys) {
      console.log('selectedRowKeys changed: ', selectedRowKeys);
      this.selectedRowKeys = selectedRowKeys;
    },
    stopTimer() {
      clearInterval(this.timer);
    },
    showLine(record) {
      let records = record;
      records.routeId = this.routeId;
      console.log(records);
      this.$refs.ResetSubLineModal.edit(records);
    },
    receiveMsg(msg) {
      let that = this;
      console.log(msg);
      for (let i = 0; i < that.tableData.length; i++) {
        if (that.tableData[i].id == msg.id) {
          Vue.set(that.tableData, i, msg);
        }
      }
      this.countBool = true;
    },

    handleOk() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }
      let arr = [];
      this.selectedRowKeys.forEach((item) => {
        arr.push(this.tableData[item].id);
      });
      let data = { routeId: this.routeId, ids: arr, remark: '单简图触发' };

      axios.post(this.url.redispatch, data).then((res) => {
        console.log('重排保存接口', res.data.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.$message.success('保存成功');
        this.visible = false;
      });
    },
    recount() {
      let that = this;
      let data = [
        {
          routeId: this.routeId,
          reDispatchList: [],
        },
      ];
      this.selectedRowKeys.forEach((item) => {
        data[0].reDispatchList.push({
          busId: that.tableData[item].busId,
          busName: that.tableData[item].busName,
          serviceType: that.tableData[item].serviceType,
          serviceName: that.tableData[item].serviceName,
          taskId: that.tableData[item].taskId,
          direction: that.tableData[item].direction,
          checkRow: that.tableData[item].checkRow,
        });
      });
      let data1 = [
        {
          routeId: 4950,
          reDispatchList: [
            { busId: 3010456, busName: '39649', serviceType: 1, serviceName: '全程', taskId: -859862, direction: '0', checkRow: '1' },
            { busId: 3010458, busName: '39653', serviceType: '2', serviceName: '短线', taskId: -1198843, direction: '0', checkRow: '1' },
          ],
        },
      ];

      axios.post(this.url.routeReDispatch, data).then((res) => {
        console.log('重新计算接口', res.data.data);
        if (res.data.data.retCode != 0) {
          this.$message.error(res.data.data.retMsg);
          return;
        }
        this.$message.success('线路重排成功');
        this.countBool = false;
      });
      // let obj = {
      //   routeId: 4950,
      //   reDispatchList: [
      //     {
      //       busId: 3016342,
      //       busName: '5598',
      //       serviceType: '1',
      //       serviceName: '全程',
      //       taskId: -406212,
      //       direction: '0',
      //       checkRow: '1',
      //     },
      //   ],
      // };
    },
    handleCancel() {
      this.visible = false;
    },
  },
};
</script>

<style lang="less" scoped>
.top {
  width: 100%;
  display: flex;
  justify-content: space-between;
  .left,
  .middle,
  .right {
    flex: 1;
    text-align: center;
  }
  .left,
  .right {
    div {
      width: 134px;
      font-weight: 400;
      font-size: 18px;
      color: #1d1d1d;
      margin: 0 auto;
    }
  }
  .middle {
    .time-count {
      width: 128px;
      height: 92px;
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #99bbe8;
      line-height: 92px;
      font-size: 58px;
      font-weight: 400;
      color: #595959;
      text-align: center;
      margin: 0 auto;
    }
  }
}
</style>
