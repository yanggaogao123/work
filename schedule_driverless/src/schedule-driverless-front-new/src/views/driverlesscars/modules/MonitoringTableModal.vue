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
              size="small"
              bordered
              rowKey="id"
              :columns="mainUpColumns"
              :dataSource="mainUpData"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ x: 1100, y: 286 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(text).format('HH:mm') }}</span>
              </span>

              <span slot="customTitle"><a-icon type="arrow-up" style="color: #1890ff" />状态</span>
              <span slot="type" slot-scope="text, record">
                <!-- 司机考勤 淡黄色 -->
                <span class="colorBox" v-if="['1', '2', '3', '4', '5'].includes(text)" style="background: #ffff9b; color: #ffff9b">{{ text }}</span>
                <!-- 已下发 天蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '0'" style="background: #00ffff; color: #00ffff">{{ text }}</span>
                <!-- 通讯成功 淡蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '9'" style="background: #009afe; color: #009afe">{{ text }}</span>
                <!-- 同意调度 深蓝色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '2'" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <!-- 不同意调度 深红色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '3'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 班次启动失败 深红色 -->
                <span class="colorBox" v-if="['0', '8'].includes(text) && record.status == '5'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 执行任务 深黄色 -->
                <span class="colorBox" v-if="record.showBlue" style="background: #0003cb; color: #0003cb">{{ text }}</span>
              </span>
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="small"
              bordered
              rowKey="busId"
              :columns="mainDownColumns"
              :dataSource="mainDownData"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ x: 1100, y: 286 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(text).format('HH:mm') }}</span>
              </span>

              <span slot="customTitle"><a-icon type="arrow-down" style="color: #1890ff" />状态</span>
              <span slot="type" slot-scope="text, record">
                <!-- 司机考勤 淡黄色 -->
                <span class="colorBox" v-if="['1', '2', '3', '4', '5'].includes(text)" style="background: #ffff9b; color: #ffff9b">{{ text }}</span>
                <!-- 已下发 天蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '0'" style="background: #00ffff; color: #00ffff">{{ text }}</span>
                <!-- 通讯成功 淡蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '9'" style="background: #009afe; color: #009afe">{{ text }}</span>
                <!-- 同意调度 深蓝色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '2'" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <!-- 不同意调度 深红色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '3'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 班次启动失败 深红色 -->
                <span class="colorBox" v-if="['0', '8'].includes(text) && record.status == '5'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 执行任务 深黄色 -->
                <span class="colorBox" v-if="record.showBlue" style="background: #0003cb; color: #0003cb">{{ text }}</span>
              </span>
            </a-table>
          </div>
        </div>
        <div class="down-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="small"
              bordered
              rowKey="busId"
              :columns="supUpColumns"
              :dataSource="supUpData"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ x: 1100, y: 286 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(text).format('HH:mm') }}</span>
              </span>

              <span slot="customTitle"><a-icon type="arrow-up" style="color: #1890ff" />状态</span>
              <span slot="type" slot-scope="text, record">
                <!-- 司机考勤 淡黄色 -->
                <span class="colorBox" v-if="['1', '2', '3', '4', '5'].includes(text)" style="background: #ffff9b; color: #ffff9b">{{ text }}</span>
                <!-- 已下发 天蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '0'" style="background: #00ffff; color: #00ffff">{{ text }}</span>
                <!-- 通讯成功 淡蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '9'" style="background: #009afe; color: #009afe">{{ text }}</span>
                <!-- 同意调度 深蓝色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '2'" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <!-- 不同意调度 深红色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '3'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 班次启动失败 深红色 -->
                <span class="colorBox" v-if="['0', '8'].includes(text) && record.status == '5'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 执行任务 深黄色 -->
                <span class="colorBox" v-if="record.showBlue" style="background: #0003cb; color: #0003cb">{{ text }}</span>
              </span>
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="small"
              bordered
              rowKey="id"
              :columns="supDownColumns"
              :dataSource="supDownData"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ x: 1100, y: 286 }"
            >
              <span slot="time" slot-scope="text, record">
                <span> {{ moment(text).format('HH:mm') }}</span>
              </span>

              <span slot="customTitle"><a-icon type="arrow-down" style="color: #1890ff" />状态</span>
              <span slot="type" slot-scope="text, record">
                <!-- 司机考勤 淡黄色 -->
                <span class="colorBox" v-if="['1', '2', '3', '4', '5'].includes(text)" style="background: #ffff9b; color: #ffff9b">{{ text }}</span>
                <!-- 已下发 天蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '0'" style="background: #00ffff; color: #00ffff">{{ text }}</span>
                <!-- 通讯成功 淡蓝色 -->
                <span class="colorBox" v-if="['0', '6', '7', '8'].includes(text) && record.status == '9'" style="background: #009afe; color: #009afe">{{ text }}</span>
                <!-- 同意调度 深蓝色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '2'" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <!-- 不同意调度 深红色 -->
                <span class="colorBox" v-if="['0', '7'].includes(text) && record.status == '3'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 班次启动失败 深红色 -->
                <span class="colorBox" v-if="['0', '8'].includes(text) && record.status == '5'" style="background: #bd0000; color: #bd0000">{{ text }}</span>
                <!-- 执行任务 深黄色 -->
                <span class="colorBox" v-if="record.showBlue" style="background: #0003cb; color: #0003cb">{{ text }}</span>
              </span>
            </a-table>
          </div>
        </div>
      </div>
      <div class="con-table2" v-else>
        <div class="up-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="small"
              bordered
              :rowKey="busId"
              :columns="mainUpColumns2"
              :dataSource="mainUpData2"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 286 }"
            >
              <span slot="customTitle"><a-icon type="arrow-up" style="color: #1890ff" />状态</span>
              <span slot="statusColor" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <span class="colorBox" v-if="text == 2" style="background: #ffcc00; color: #ffcc00">{{ text }}</span>
                <span class="colorBox" v-if="text == 3" style="background: #1bb291; color: #1bb291">{{ text }}</span>
              </span>
              <span slot="statusText" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1">未开始</span>
                <span class="colorBox" v-if="text == 2">执行中</span>
                <span class="colorBox" v-if="text == 3">已完成</span>
              </span>
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="small"
              bordered
              :rowKey="busId"
              :columns="mainDownColumns2"
              :dataSource="mainDownData2"
              :pagination="false"
              class="j-table-force-wrap table-blue"
              :scroll="{ y: 286 }"
            >
              <span slot="customTitle"><a-icon type="arrow-down" style="color: #1890ff" />状态</span>
              <span slot="statusColor" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <span class="colorBox" v-if="text == 2" style="background: #ffcc00; color: #ffcc00">{{ text }}</span>
                <span class="colorBox" v-if="text == 3" style="background: #1bb291; color: #1bb291">{{ text }}</span>
              </span>
              <span slot="statusText" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1">未开始</span>
                <span class="colorBox" v-if="text == 2">执行中</span>
                <span class="colorBox" v-if="text == 3">已完成</span>
              </span>
            </a-table>
          </div>
        </div>
        <div class="down-part">
          <div class="left-part">
            <a-table
              ref="table"
              size="small"
              bordered
              :rowKey="busId"
              :columns="supUpColumns2"
              :dataSource="supUpData2"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 286 }"
            >
              <span slot="customTitle"><a-icon type="arrow-up" style="color: #1890ff" />状态</span>
              <span slot="statusColor" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <span class="colorBox" v-if="text == 2" style="background: #ffcc00; color: #ffcc00">{{ text }}</span>
                <span class="colorBox" v-if="text == 3" style="background: #1bb291; color: #1bb291">{{ text }}</span>
              </span>
              <span slot="statusText" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1">未开始</span>
                <span class="colorBox" v-if="text == 2">执行中</span>
                <span class="colorBox" v-if="text == 3">已完成</span>
              </span>
            </a-table>
          </div>
          <div class="right-part">
            <a-table
              ref="table"
              size="small"
              bordered
              :rowKey="busId"
              :columns="supDownColumns2"
              :dataSource="supDownData2"
              :pagination="false"
              class="j-table-force-wrap table-green"
              :scroll="{ y: 286 }"
            >
              <span slot="customTitle"><a-icon type="arrow-down" style="color: #1890ff" />状态</span>
              <span slot="statusColor" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1" style="background: #0003cb; color: #0003cb">{{ text }}</span>
                <span class="colorBox" v-if="text == 2" style="background: #ffcc00; color: #ffcc00">{{ text }}</span>
                <span class="colorBox" v-if="text == 3" style="background: #1bb291; color: #1bb291">{{ text }}</span>
              </span>
              <span slot="statusText" slot-scope="text, record">
                <!-- 1：未开始，2：执行中，3：已完成 -->
                <span class="colorBox" v-if="text == 1">未开始</span>
                <span class="colorBox" v-if="text == 2">执行中</span>
                <span class="colorBox" v-if="text == 3">已完成</span>
              </span>
            </a-table>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import Vue from 'vue';
import axios from 'axios';
import moment from 'moment';
import '@/assets/less/base.css';
import '@/assets/less/TableExpand.less';
export default {
  name: 'MonitoringTableModal',
  props: ['sendData'],
  // mixins: [JeecgListMixin],
  data() {
    return {
      mes: '',
      url: {
        getMonitorInfo: `${process.env.VUE_APP_BUS_API}/schedule/getMonitorInfo`,
      },
      routeId: '',
      routeName: '',
      supRouteId: '',
      supRouteName: '',
      runDate: '',
      time: '',

      busTimer: '',
      see: 'a',
      seeType: 'a',

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
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'msgName',
          width: 70,
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
          width: 70,
        },
        {
          title: '时间',
          align: 'center',
          dataIndex: 'time',
          width: 70,
          scopedSlots: { customRender: 'time' },
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
          width: 70,
        },
        {
          title: '发班间隔',
          align: 'center',
          dataIndex: 'interval',
          width: 70,
        },
        {
          title: '内容',
          align: 'left',
          dataIndex: 'content',
          // width: "40%",
        },
      ],
      mainDownColumns: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'msgName',
          width: 70,
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
          width: 70,
        },
        {
          title: '时间',
          align: 'center',
          dataIndex: 'time',
          width: 70,
          scopedSlots: { customRender: 'time' },
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
          width: 70,
        },
        {
          title: '发班间隔',
          align: 'center',
          dataIndex: 'interval',
          width: 70,
        },
        {
          title: '内容',
          align: 'left',
          dataIndex: 'content',
          // width: "40%",
        },
      ],
      supUpColumns: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'msgName',
          width: 70,
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
          width: 70,
        },
        {
          title: '时间',
          align: 'center',
          dataIndex: 'time',
          width: 70,
          scopedSlots: { customRender: 'time' },
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
          width: 70,
        },
        {
          title: '发班间隔',
          align: 'center',
          dataIndex: 'interval',
          width: 70,
        },
        {
          title: '内容',
          align: 'left',
          dataIndex: 'content',
          // width: "40%",
        },
      ],
      supDownColumns: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'msgName',
          width: 70,
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
          width: 70,
        },
        {
          title: '时间',
          align: 'center',
          dataIndex: 'time',
          width: 70,
          scopedSlots: { customRender: 'time' },
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
          width: 70,
        },
        {
          title: '发班间隔',
          align: 'center',
          dataIndex: 'interval',
          width: 70,
        },
        {
          title: '内容',
          align: 'left',
          dataIndex: 'content',
          // width: "40%",
        },
      ],

      mainUpData: [],
      mainDownData: [],
      supUpData: [],
      supDownData: [],

      mainUpColumns2: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'status',
          scopedSlots: { customRender: 'statusText' },
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
        },
        {
          title: '发班时间',
          align: 'center',
          dataIndex: 'tripBeginTime',
          scopedSlots: { customRender: 'tripBeginTime' },
        },
        {
          title: '结束时间',
          align: 'center',
          dataIndex: 'tripEndTime',
          scopedSlots: { customRender: 'tripEndTime' },
        },
        {
          title: '发车间隔',
          align: 'center',
          dataIndex: 'interval',
        },
        {
          title: '运行时长',
          align: 'center',
          dataIndex: 'fullTime',
        },
        {
          title: '计划发班',
          align: 'center',
          dataIndex: 'planTime',
        },
      ],
      mainDownColumns2: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'status',
          scopedSlots: { customRender: 'statusText' },
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
        },
        {
          title: '发班时间',
          align: 'center',
          dataIndex: 'tripBeginTime',
          scopedSlots: { customRender: 'tripBeginTime' },
        },
        {
          title: '结束时间',
          align: 'center',
          dataIndex: 'tripEndTime',
          scopedSlots: { customRender: 'tripEndTime' },
        },
        {
          title: '发车间隔',
          align: 'center',
          dataIndex: 'interval',
        },
        {
          title: '运行时长',
          align: 'center',
          dataIndex: 'fullTime',
        },
        {
          title: '计划发班',
          align: 'center',
          dataIndex: 'planTime',
        },
      ],
      supUpColumns2: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'status',
          scopedSlots: { customRender: 'statusText' },
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
        },
        {
          title: '发班时间',
          align: 'center',
          dataIndex: 'tripBeginTime',
          scopedSlots: { customRender: 'tripBeginTime' },
        },
        {
          title: '结束时间',
          align: 'center',
          dataIndex: 'tripEndTime',
          scopedSlots: { customRender: 'tripEndTime' },
        },
        {
          title: '发车间隔',
          align: 'center',
          dataIndex: 'interval',
        },
        {
          title: '运行时长',
          align: 'center',
          dataIndex: 'fullTime',
        },
        {
          title: '计划发班',
          align: 'center',
          dataIndex: 'planTime',
        },
      ],
      supDownColumns2: [
        {
          // title: "状态",
          align: 'center',
          slots: { title: 'customTitle' },
          dataIndex: 'status',
          scopedSlots: { customRender: 'statusText' },
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName',
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName',
        },
        {
          title: '发班时间',
          align: 'center',
          dataIndex: 'tripBeginTime',
          scopedSlots: { customRender: 'tripBeginTime' },
        },
        {
          title: '结束时间',
          align: 'center',
          dataIndex: 'tripEndTime',
          scopedSlots: { customRender: 'tripEndTime' },
        },
        {
          title: '发车间隔',
          align: 'center',
          dataIndex: 'interval',
        },
        {
          title: '运行时长',
          align: 'center',
          dataIndex: 'fullTime',
        },
        {
          title: '计划发班',
          align: 'center',
          dataIndex: 'planTime',
        },
      ],

      mainUpData2: [],
      mainDownData2: [],
      supUpData2: [],
      supDownData2: [],
    };
  },
  watch: {
    sendData() {
      console.log('sendData', this.sendData);
      this.centerData = this.sendData.centerData;
      this.runDate = this.sendData.runDate;
      this.time = moment(this.sendData.time, 'HH:mm:ss');
      this.routeId = this.sendData.routeId;
      this.routeName = this.sendData.routeName;
      this.supRouteId = this.sendData.supRouteId;
      this.supRouteName = this.sendData.supRouteName;
      if (this.mainUpColumns[0].title != this.routeName) {
        this.mainUpColumns.unshift({
          title: this.routeName,
          align: 'center',
          dataIndex: 'type',
          width: 50,
          scopedSlots: { customRender: 'type' },
        });
        this.mainDownColumns.unshift({
          title: this.routeName,
          align: 'center',
          dataIndex: 'type',
          width: 50,
          scopedSlots: { customRender: 'type' },
        });
        this.supUpColumns.unshift({
          title: this.supRouteName,
          align: 'center',
          dataIndex: 'type',
          width: 50,
          scopedSlots: { customRender: 'type' },
        });
        this.supDownColumns.unshift({
          title: this.supRouteName,
          align: 'center',
          dataIndex: 'type',
          width: 50,
          scopedSlots: { customRender: 'type' },
        });
      }

      if (this.mainUpColumns2[0].title != this.routeName) {
        this.mainUpColumns2.unshift({
          title: this.routeName,
          align: 'center',
          dataIndex: 'status',
          width: 74,
          scopedSlots: { customRender: 'statusColor' },
        });
        this.mainDownColumns2.unshift({
          title: this.routeName,
          align: 'center',
          dataIndex: 'status',
          width: 74,
          scopedSlots: { customRender: 'statusColor' },
        });
        this.supUpColumns2.unshift({
          title: this.supRouteName,
          align: 'center',
          dataIndex: 'status',
          width: 74,
          scopedSlots: { customRender: 'statusColor' },
        });
        this.supDownColumns2.unshift({
          title: this.supRouteName,
          align: 'center',
          dataIndex: 'status',
          width: 74,
          scopedSlots: { customRender: 'statusColor' },
        });
      }
      clearInterval(this.busTimer);

      this.busTimer = setInterval(() => {
        this.tableInit();
      }, 10000);
    },
    seeType() {
      if (this.seeType == 'a') {
        this.tableInit();
      } else if (this.seeType == 'b') {
        this.tableInit2();
      }
      clearInterval(this.busTimer);
      this.busTimer = setInterval(() => {
        if (this.seeType == 'a') {
          this.tableInit();
        } else if (this.seeType == 'b') {
          this.tableInit2();
        }
      }, 10000);
    },
  },
  created() {
    const queryString = window.location.search;
    const searchParams = new URLSearchParams(queryString);
    const paramString = searchParams.get('paramString');
    // console.log(paramString);
    this.mes = new URLSearchParams();
    this.mes.append('paramString', paramString);
    // console.log(this.mes.toString());
    // this.tableInit();
    console.log('see', this.see);
    console.log('see2', this.seeType);
    console.log(this.mainUpColumns);
  },
  mounted() {
    let elements = document.getElementsByClassName('ant-table-header');
    console.log(elements);
  },
  methods: {
    moment,
    tableInit() {
      let params = this.mes;
      let data = {
        routeId: this.routeId,
        // runDate: `${moment(this.runDate).format("YYYY-MM-DD")} 00:00:00`,
        runDate: `2024-02-19 00:00:00`,
      };
      axios.post(`${this.url.getMonitorInfo}`, data, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }

        // 修改表格样式
        // let elements = document.querySelectorAll('.table-blue .ant-table-header');
        // console.log('elements', elements);
        // elements.forEach((item) => {
        //     item.style.background = `#C4E4FF`;
        // });

        let info = res.data.info;
        let runBus = res.data.runBus;
        let supportInfo = res.data.supportInfo;

        // info.forEach((item) => {
        //   const matchRunBusInfo = runBus.find((runItem) => {
        //     return item.busId == runBus.runBus && runItem.runStatus == "1";
        //   });
        //   if (matchRunBusInfo) {
        //     Vue.set(item, "showBlue", true);
        //   } else {
        //     Vue.set(item, "showBlue", false);
        //   }
        // });

        this.mainUpData = info.filter((item) => {
          return item.direction == '0';
        });
        console.log('mainUpData', this.mainUpData);
        this.mainDownData = info.filter((item) => {
          return item.direction == '1';
        });

        this.mainUpData2 = supportInfo.filter((item) => {
          return item.direction == '0';
        });
        console.log('mainUpData2', this.mainUpData2);
        this.mainDownData2 = supportInfo.filter((item) => {
          return item.direction == '1';
        });
      });

      let data1 = {
        routeId: this.supRouteId,
        runDate: `${moment(this.runDate).format('YYYY-MM-DD')} 00:00:00`,
      };
      axios.post(`${this.url.getMonitorInfo}`, data1, { params }).then((res) => {
        console.log(res);
        if (res.data.retCode != 0) {
          this.$message.error(res.data.retMsg);
          return;
        }

        this.supUpData = res.data.info.filter((item) => {
          return item.direction == '0';
        });
        this.supDownData = res.data.info.filter((item) => {
          return item.direction == '1';
        });

        this.supUpData2 = res.data.supportInfo.filter((item) => {
          return item.direction == '0';
        });
        this.supDownData2 = res.data.supportInfo.filter((item) => {
          return item.direction == '1';
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
    height: 50px;
    box-sizing: border-box;
    padding: 0 20px;
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
      padding: 0 20px;
      box-sizing: border-box;
      .up-part,
      .down-part {
        display: flex;
        background: #f5f9fc;
        // height: 290px;
        justify-content: space-between;
        margin-bottom: 30px;
        .left-part,
        .right-part {
          width: 49.5%;
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
      /deep/.ant-table-header {
        background: #b8deff !important;
      }
      /deep/.ant-table-thead tr th {
        background: #b8deff !important;
      }
      /deep/.ant-table-hide-scrollbar {
        scrollbar-color: #c4e4ff !important;
      }
    }
    .table-blue ::-webkit-scrollbar {
      width: 5px;
      height: 5px;
      background: #cbe3f9;
      border-radius: 10px; /*外层轨道*/
      color: #99bbe8;
    }
    /* 滚动条滑块 */
    .table-blue ::-webkit-scrollbar-thumb {
      background: #99bbe8; /* 滑块颜色 */
      border-radius: 6px;
    }

    .table-green {
      /deep/.ant-table-header {
        background: #bce9f0 !important;
      }
      /deep/.ant-table-thead tr th {
        background: #bce9f0 !important;
      }
      /deep/.ant-table-hide-scrollbar {
        scrollbar-color: #c4e4ff !important;
      }
    }
    .table-green ::-webkit-scrollbar {
      width: 5px;
      height: 5px;
      background: #cbe3f9;
      border-radius: 10px; /*外层轨道*/
      color: #99bbe8;
    }
    /* 滚动条滑块 */
    .table-green ::-webkit-scrollbar-thumb {
      background: #99bbe8; /* 滑块颜色 */
      border-radius: 6px;
    }
  }
}
</style>
