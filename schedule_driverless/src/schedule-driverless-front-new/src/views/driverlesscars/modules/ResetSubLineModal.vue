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
        dispatchTaskFromMainStation: `${process.env.VUE_APP_BUS_API}/monitor/dispatchTaskFromMainStation`,
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
      direction: '',

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
      recordData: '',
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
      this.routeId = Number(record.routeId);
      this.direction = record.direction;
      this.recordData = record;

      axios.get(`${this.url.dispatchTaskFromMainStation}?routeId=4950&direction=0&&sessionId=''&path=''`, {}, params).then((res) => {
        console.log('子线路任务', res.data.data);
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
            this.recordData.serviceName = record.serviceName;
            this.recordData.serviceType = record.serviceType;
            delete this.recordData.routeId;
            console.log(this.recordData);
            this.$emit('receive', this.recordData);
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
