<template>
  <a-modal title="子线路" :visible="visible" :width="width" switchFullscreen @ok="handleOk" @cancel="handleCancel" cancelText="关闭" :confirmLoading="clickFlag" :destroyOnClose="true">
    <a-spin tip="Loading..." :spinning="spinning">
      <a-table ref="table" size="middle" bordered :rowKey="(record) => record.routeSubId" :columns="columns" :dataSource="tableData" :pagination="false" :customRow="click" class="j-table-force-wrap">
        <span slot="direction" slot-scope="text, record">
          {{ text == 0 ? '上行' : '下行' }}
        </span>
      </a-table>
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
moment.locale('zh-cn');

export default {
  name: 'SubLineModal',
  data() {
    return {
      url: {
        mountCarPlan: `${process.env.VUE_APP_BUS_API}/schedule/mountCarPlan`,
        recentRunBus: `${process.env.VUE_APP_BUS_API}/schedule/recentRunBus`,
        dispatchTask: `${process.env.VUE_APP_BUS_API}/schedule/dispatchTask`,
        saveMountCar: `${process.env.VUE_APP_BUS_API}/schedule/saveMountCar`,
      },
      spinning: false,
      visible: false,
      clickFlag: false,
      selectType: 'id',
      isGetOption: true,
      confirmLoading: false,
      width: 590,

      routeId: '',
      routeName: '',
      supRouteId: '',
      supRouteName: '',
      runDate: '',
      runDateStr: '',
      startDirection: '',
      taskType: '',
      index: '',

      allRouteList: [],
      routeList: [],

      supRouteList: [],
      columns: [
        {
          title: '任务类型',
          align: 'center',
          // slots: { title: 'customTitle' },
          dataIndex: 'serviceName',
          scopedSlots: { customRender: 'serviceName' },
          // width: 70,
        },
        {
          title: '方向',
          align: 'center',
          dataIndex: 'direction',
          scopedSlots: { customRender: 'direction' },
          // width: 70,
        },
        {
          title: '子线路',
          align: 'center',
          dataIndex: 'name',
          scopedSlots: { customRender: 'name' },
          // width: 70,
        },
      ],
      tableData: [],
      busList: [],
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
      this.runDate = record.runDate;
      this.runDateStr = moment(record.runDate).format('YYYY-MM-DD dddd');
      this.routeName = record.routeName;
      this.routeList = [];
      this.routeList = this.allRouteList.filter((route) => route.routeName.includes(this.routeName));
      this.routeId = Number(record.routeId);
      this.startDirection = record.startDirection;
      this.taskType = record.taskType;
      this.index = record.index;
      axios
        .get(
          `${this.url.dispatchTask}/${this.taskType}/${this.routeId}/${this.startDirection}/${moment(this.runDate).subtract(7, 'days').format('YYYY-MM-DD')}/${moment(this.runDate).format(
            'YYYY-MM-DD'
          )}`
        )
        .then((res) => {
          console.log('2.15.挂车-获取获取批量挂车任务弹出框数据接口', res);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          this.tableData = res.data.data;
        });
    },
    click(record, index) {
      return {
        on: {
          click: () => {
            console.log(record, index);
            let records = record;
            records.taskType = this.taskType;
            records.index = this.index;
            this.$emit('receive', record);
            this.visible = false;
          },
        },
      };
    },

    handleOk() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }
      this.visible = false;
    },
    handleCancel() {
      this.visible = false;
    },
  },
};
</script>

<style lang="less" scoped>
.content {
}
</style>
