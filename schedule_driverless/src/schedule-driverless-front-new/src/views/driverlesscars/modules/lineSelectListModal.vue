<template>
  <div class="sele-list">
    <a-select
      show-search
      allowClear
      :value="routeId"
      placeholder="请选择主线路"
      :default-active-first-option="false"
      :show-arrow="false"
      :filter-option="false"
      :not-found-content="null"
      style="width: 170px"
      @search="handleSearch"
      @change="handleChange"
    >
      <a-select-option v-for="item in routeList" :value="item.routeId">
        {{ item.routeName }}
      </a-select-option>
    </a-select>
    -
    <a-select allowClear v-model="supRouteId" placeholder="请选择关联线路" style="width: 170px" @change="seleChange">
      <a-select-option v-for="item in supRouteList" :value="item.supportRouteId">
        {{ item.supportRouteName }}
      </a-select-option>
    </a-select>
  </div>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import '@/assets/less/base.css';
import moment from 'moment';
export default {
  name: 'lineSelectListModal',
  props: ['number'],
  data() {
    return {
      url: {
        generateSupportSchedule: `${process.env.VUE_APP_BUS_API}/schedule/generateSupportSchedule`,
        getScheduleBySort: `${process.env.VUE_APP_BUS_API}/schedule/getScheduleBySort`,
        adrealInfo: `${process.env.VUE_APP_BUS_API}/simulation/adrealInfo`,
        getRouteList: `${process.env.VUE_APP_BUS_API}/route/getRouteList`,
        getUnionRouteInfo: `${process.env.VUE_APP_BUS_API}/route/getUnionRouteInfo`,
        getMinPlanTime: `${process.env.VUE_APP_BUS_API}/simulation/getMinPlanTime`,
        getRouteUpDownInfo: `${process.env.VUE_APP_BUS_API}/route/getRouteUpDownInfo`,
        busConfigure: `${process.env.VUE_APP_BUS_API}/schedule/busConfigure`,
        getScheduleCountResult: `${process.env.VUE_APP_BUS_API}/scheduleCount/getScheduleCountResult`,
        getRuningScheduleConfig: `${process.env.VUE_APP_BUS_API}/schedule/getRuningScheduleConfig`,
        runBus: `${process.env.VUE_APP_BUS_API}/schedule/runBus`,
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
      },
      mes: '',
      routeId: '',
      supRouteId: '',
      routeName: '',
      supRouteName: '',
      //   runDate: moment(),
      runDate: '2024-02-05',
      allRouteList: [],
      routeList: [],
      supRouteList: [],
      type: '',
    };
  },
  watch: {
    number() {
      // console.log('number', this.number);
    },
  },
  created() {
    this.getData();
  },
  methods: {
    moment,
    getData() {
      const queryString = window.location.search;
      const searchParams = new URLSearchParams(queryString);
      const paramString = searchParams.get('paramString');
      const params = new URLSearchParams();
      params.append('paramString', paramString);
      this.mes = params;
      console.log(params.toString());
      axios.get(`${this.url.getRouteList}?${params}`, {}, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }
        this.allRouteList = res.data.data;
      });

      axios
        .post(
          `${this.url.getMonitorInfo}`,
          {
            routeId: this.routeId,
            runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
          },
          { params }
        )
        .then((res) => {
          console.log(res);
        });

      axios
        .post(
          `${this.url.getMonitorInfo}`,
          {
            routeId: this.supRouteId,
            runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
          },
          { params }
        )
        .then((res) => {
          console.log(res);
        });
    },
    handleSearch(value) {
      console.log(value);
      if (value == '') {
        this.routeList = [];
        return;
      }
      this.routeList = [];
      this.routeList = this.allRouteList.filter((route) => route.routeName.includes(value));
      console.log(this.routeList);
    },
    handleChange(value, option) {
      // console.log('前置操作', value);
      let params = this.mes;
      // console.log(value, option);
      // console.log(this.routeList);
      let arr = this.routeList.filter((route) => route.routeId == value);
      this.routeId = value;
      this.routeName = this.routeId ? arr[0].routeName : '';
      if (value) {
        // if (this.number == 0) {
        //   this.$emit('showOne', false);
        // } else if (this.number == 1) {
        //   this.$emit('showTwo', false);
        // }
        axios.post(this.url.getUnionRouteInfo, { routeId: value }, { params }).then((res) => {
          console.log('关联线路信息', res);
          if (res.data.retCode != 0) {
            this.$message.error(res.data.retMsg);
            return;
          }
          this.supRouteList = res.data.data;
        });
      } else {
        if (this.number == 0) {
          this.$emit('showOne', true);
        } else if (this.number == 1) {
          this.$emit('showTwo', true);
        }
      }
    },
    seleChange(value) {
      // console.log('后续操作', value);
      // console.log(this.supRouteList);
      let arr = this.supRouteList.filter((route) => route.supportRouteId == value);
      this.supRouteId = value;
      this.supRouteName = this.supRouteId ? arr[0].supportRouteName : '';
      this.type = this.supRouteId ? arr[0].type : '';
      if (value) {
        if (this.number == 0) {
          this.$emit('showOne', false);
        } else if (this.number == 1) {
          this.$emit('showTwo', false);
        }
      } else {
        if (this.number == 0) {
          this.$emit('showOne', true);
        } else if (this.number == 1) {
          this.$emit('showTwo', true);
        }
      }
      this.$emit('lineInfo', {
        routeId: this.routeId,
        routeName: this.routeName,
        supRouteId: this.supRouteId,
        supRouteName: this.supRouteName,
        number: this.number,
        type: this.type,
      });
    },
  },
};
</script>

<style></style>
