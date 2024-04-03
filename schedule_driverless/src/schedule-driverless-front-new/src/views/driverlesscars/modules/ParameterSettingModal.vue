<template>
  <div id="content">
    <a-modal
      title="线路选择-发班参数设置"
      :visible="visible"
      :width="width"
      switchFullscreen
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      :confirmLoading="clickFlag"
      :destroyOnClose="true"
    >
      <a-radio-group :options="idList" v-model="value1" @change="onChange" />
    </a-modal>
    <a-modal title="发班参数设置" :visible="visible1" :width="width" switchFullscreen @ok="handleOk1" @cancel="handleCancel1" cancelText="关闭" :confirmLoading="clickFlag" :destroyOnClose="true">
      <a-spin tip="Loading..." :spinning="spinning">
        <a-table
          ref="table"
          size="middle"
          bordered
          :rowKey="(record) => record.routeSubId"
          :columns="columns"
          :dataSource="tableData"
          :pagination="false"
          :customRow="click"
          class="j-table-force-wrap"
        >
          <span slot="direction" slot-scope="text, record">
            {{ text == 0 ? '上行' : '下行' }}
          </span>
        </a-table>
      </a-spin>
    </a-modal>
  </div>
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
  name: 'ParameterSettingModal',
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
      idList: [],

      allRouteList: [],
      routeList: [],
      supRouteList: [],

      value1: '',
      options: [
        { label: 'Apple', value: 'Apple' },
        { label: 'Pear', value: 'Pear' },
        { label: 'Orange', value: 'Orange' },
      ],
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

      record.forEach((ele) => {
        if (ele.routeId) {
          this.idList.push({ label: ele.routeName, value: ele.routeId });
          this.idList.push({ label: ele.supRouteName, value: ele.supRouteId });
        }
      });
      function removeDuplicates(array, key) {
        const uniqueArray = [];
        const seen = {};

        array.forEach((item) => {
          const value = item[key];
          if (!seen[value]) {
            uniqueArray.push(item);
            seen[value] = true;
          }
        });

        return uniqueArray;
      }

      this.idList = removeDuplicates(this.idList, 'value');
      console.log(this.idList);
      // this.routeId = Number(record.routeId);
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
    onChange(e) {
      console.log('radio1 checked', e.target);
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

    handleOk1() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }
      this.visible1 = false;
    },
    handleCancel1() {
      this.visible1 = false;
    },
  },
};
</script>

<style lang="less" scoped>
.content {
}
</style>
