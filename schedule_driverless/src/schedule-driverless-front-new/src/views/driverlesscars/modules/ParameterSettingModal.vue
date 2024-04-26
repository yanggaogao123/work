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
      <a-radio-group :options="idList" v-model="value1" />
    </a-modal>
    <a-modal title="发班参数设置" :visible="visible1" :width="width1" switchFullscreen @ok="handleOk1" @cancel="handleCancel1" cancelText="关闭" :confirmLoading="clickFlag" :destroyOnClose="true">
      <a-spin tip="Loading..." :spinning="spinning">
        <div class="con">
          <ul>
            <li>
              <div class="left">最大发班间隔(默认19分钟)</div>
              <div class="right">
                <a-input-number :min="1" :max="100000" v-model="settingData.maxDispatchInterval" />
                分钟
              </div>
            </li>
            <li>
              <div class="left">高峰最大发班间隔</div>
              <div class="right">
                <a-input-number :min="1" :max="100000" v-model="settingData.maxPeakParamInterval" />
                分钟
              </div>
            </li>
            <li>
              <div class="left">上行最小吃饭时间(默认15分钟)</div>
              <div class="right">
                <a-input-number :min="1" :max="100000" v-model="settingData.upMinEatTime" />
                分钟
              </div>
            </li>
            <li>
              <div class="left">断位最小停站时间(默认2分钟)</div>
              <div class="right">
                <a-input-number :min="1" :max="100000" v-model="settingData.brokenMinStop" />
                分钟
              </div>
            </li>
            <li>
              <div class="left">首轮顶位时间(默认5分钟)</div>
              <div class="right">
                <a-input-number :min="1" :max="100000" v-model="settingData.frReplaceTime" />
                分钟
              </div>
            </li>
            <li>
              <div class="left">发班计划最大数量</div>
              <div class="right">
                <a-input-number :min="1" :max="99" v-model="settingData.dispatchPlanMaxNum" />
                (限两位数)
              </div>
            </li>
          </ul>
          <div class="checkbox-box">
            <a-checkbox-group :options="options" v-model="value2" @change="onChange" />
          </div>
        </div>
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
        getTripParam: `${process.env.VUE_APP_BUS_API}/monitor/getTripParam`, //请求参数
        saveTripParam: `${process.env.VUE_APP_BUS_API}/monitor/saveTripParam`, //保存参数
      },
      spinning: false,
      visible: false,
      visible1: false,
      clickFlag: false,
      selectType: 'id',
      isGetOption: true,
      confirmLoading: false,
      width: 590,
      width1: 480,

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
        { label: '尾二等末班', value: 'waitTailBus' },
        { label: '短线保尾车', value: 'ensureTailBusByShortLine' },
        { label: '保长发短', value: 'ensureLongDispatchShort ' },
      ],
      value2: '',
      settingData: '',
      sendData: '',
    };
  },
  filters: {
    ensureZero: function (value) {
      return value || value == 0 ? value : 0;
    },
  },
  created() {
    // const queryString = window.location.search;
    // const searchParams = new URLSearchParams(queryString);
    // const paramString = searchParams.get('paramString');
    // const params = new URLSearchParams();
    // params.append('paramString', paramString);
    // this.mes = params;
    // console.log(params.toString());
  },
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
      console.log(`checked = ${e}`);
      this.settingData.waitTailBus = 0;
      this.settingData.ensureTailBusByShortLine = 0;
      this.settingData.ensureLongDispatchShort = 0;
      if (e.includes('waitTailBus')) {
        this.settingData.waitTailBus = 1;
      }
      if (e.includes('ensureTailBusByShortLine')) {
        this.settingData.ensureTailBusByShortLine = 1;
      }
      if (e.includes('ensureLongDispatchShort')) {
        this.settingData.ensureLongDispatchShort = 1;
      }
    },
    handleOk() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }
      console.log(this.value1);
      this.visible = false;
      this.visible1 = true;
      axios.get(`${this.url.getTripParam}/${this.value1}`).then((res) => {
        console.log('getTripParam', res.data.data);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.settingData = res.data.data[this.value1];
        this.value2 = [];
        if (this.settingData.waitTailBus == 1) {
          this.value2.push('waitTailBus');
        } else if (this.settingData.ensureTailBusByShortLine == 1) {
          this.value2.push('ensureTailBusByShortLine');
        } else if (this.settingData.ensureLongDispatchShort == 1) {
          this.value2.push('ensureLongDispatchShort');
        }
      });
    },
    handleCancel() {
      this.visible = false;
    },

    handleOk1() {
      if (this.clickFlag == true) {
        this.$message.warning('请勿重复点击');
        return;
      }
      axios.post(this.url.saveTripParam, this.settingData).then((res) => {
        console.log('saveTripParam', res);
        if (!res.data.success) {
          this.$message.error(res.data.message);
          return;
        }
        this.$message.success(res.data.message);
        this.visible1 = false;
      });
    },
    handleCancel1() {
      this.visible1 = false;
    },
  },
};
</script>

<style lang="less" scoped>
.con {
  ul {
    li {
      display: flex;
      justify-content: space-between;
      line-height: 32px;
      margin-bottom: 10px;
      .left {
        flex: 3;
        text-align: right;
        padding: 0 10px 0 0;
      }
      .right {
        flex: 2;
      }
    }
  }
  .checkbox-box {
    width: 100%;
    height: 60px;
    border: 1px solid #99bbe8;
    text-align: center;
    .ant-checkbox-group {
      margin-top: 20px;
    }
  }
}
</style>
