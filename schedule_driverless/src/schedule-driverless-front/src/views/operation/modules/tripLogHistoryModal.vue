<template>
  <j-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @cancel="handleCancel"
    @ok="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-card :bordered="false">
        <!-- table区域-begin -->
        <div>
          <a-table size="middle" bordered :columns="columns" :loading="loading" :data-source="data" :pagination="false" :scroll="{ x: true}">
            <template slot="direction" slot-scope="text">
              {{ text == 0 ? '上行' : text == 1 ? '下行' : '无' }}
            </template>
            <template slot="busName" slot-scope="text, record">
              <span v-if="record.new && record.new.busName" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="employeeName" slot-scope="text, record">
              <span v-if="record.new && record.new.employeeName" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="serviceName" slot-scope="text, record">
              <span v-if="record.new && record.new.serviceName" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="fromStationName" slot-scope="text, record">
              <span v-if="record.new && record.new.fromStationName" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="toStationName" slot-scope="text, record">
              <span v-if="record.new && record.new.toStationName" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="triplogBeginTime" slot-scope="text, record">
              <span v-if="record.new && record.new.triplogBeginTime" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="triplogEndTime" slot-scope="text, record">
              <span v-if="record.new && record.new.triplogEndTime" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="runMileage" slot-scope="text, record">
              <span v-if="record.new && record.new.runMileage" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="routeNameRun" slot-scope="text, record">
              <span v-if="record.new && record.new.routeNameRun" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="dispatchSendTime" slot-scope="text, record">
              <span v-if="record.new && record.new.dispatchSendTime" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="recordStatus" slot-scope="text, record">
              <span v-if="record.new && record.new.recordStatus" class="red">
                <span v-if="text == '0'">开始记录</span>
                <span v-if="text == '1'">结束(自动报站)</span>
                <span v-if="text == '2'">结束(人工报站)</span>
                <span v-if="text == '3'">审核通过</span>
                <span v-if="text == '4'">未审核</span>
                </span>
              <span v-else>
                <span v-if="text == '0'">开始记录</span>
                <span v-if="text == '1'">结束(自动报站)</span>
                <span v-if="text == '2'">结束(人工报站)</span>
                <span v-if="text == '3'">审核通过</span>
                <span v-if="text == '4'">未审核</span>
              </span>
            </template>
            <template slot="operator" slot-scope="text, record">
              <span v-if="record.new && record.new.operator" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
            <template slot="modifyTime" slot-scope="text, record">
              <span v-if="record.new && record.new.modifyTime" class="red">{{ text }}</span>
              <span v-else>{{ text }}</span>
            </template>
          </a-table>
        </div>
      </a-card>
    </a-spin>
  </j-modal>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { postAction, getAction } from '@/api/manage'
// import {
//   mixinDevice
// } from '@/utils/mixin'
// import {
//   JeecgListMixin
// } from '@/mixins/JeecgListMixin'
function getNewArr(a, b) {
  const arr = [...a, ...b]
  const newArr = arr.filter(item => {
    return !(a.includes(item) && b.includes(item))
  })
  return newArr
}
export default {
  name: '',
  // mixins: [JeecgListMixin, mixinDevice],
  components: {},
  props: {},
  data() {
    return {
      description: '电子路单记录历史变更情况跟踪',
      title: '历史变更记录',
      width: 1600,
      visible: false,
      confirmLoading: false,
      loading: false,
      // 表头
      columns: [
        {
          title: '序号',
          dataIndex: '',
          key: 'triplog_id',
          width: 60,
          align: 'center',
          customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '所属线路',
          dataIndex: 'routeName',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'routeName'
          }
        },
        {
          title: '车辆',
          dataIndex: 'busName',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'busName'
          }
        },
        {
          title: '司机',
          dataIndex: 'employeeName',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'employeeName'
          }
        },
        {
          title: '任务类型',
          dataIndex: 'serviceName',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'serviceName'
          }
        },
        {
          title: '方向',
          dataIndex: 'direction',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'direction'
          }
        },
        {
          title: '开出站点',
          dataIndex: 'fromStationName',
          align: 'center',
          width: 100,
          scopedSlots: {
            customRender: 'fromStationName'
          }
        },
        {
          title: '到达站点',
          dataIndex: 'toStationName',
          align: 'center',
          width: 100,
          scopedSlots: {
            customRender: 'toStationName'
          }
        },
        {
          title: '开始时间',
          dataIndex: 'triplogBeginTime',
          align: 'center',
          width: 100,
          scopedSlots: {
            customRender: 'triplogBeginTime'
          }
        },
        {
          title: '结束时间',
          dataIndex: 'triplogEndTime',
          align: 'center',
          width: 100,
          scopedSlots: {
            customRender: 'triplogEndTime'
          }
        },
        {
          title: '里程(km)',
          dataIndex: 'runMileage',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'runMileage'
          }
        },
        {
          title: '备注',
          dataIndex: 'remark',
          align: 'center',
          width: 100,
          ellipsis: true
        },
        {
          title: '运营线路',
          dataIndex: 'routeNameRun',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'routeNameRun'
          }
        },
        {
          title: '调度下发时间',
          dataIndex: 'dispatchSendTime',
          align: 'center',
          width: 100,
          scopedSlots: {
            customRender: 'dispatchSendTime'
          }
        },
        {
          title: '状态',
          dataIndex: 'recordStatus',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'recordStatus'
          }
        },
        {
          title: '修改人',
          dataIndex: 'operator',
          align: 'center',
          width: 80,
          scopedSlots: {
            customRender: 'operator'
          }
        },
        {
          title: '修改时间',
          dataIndex: 'modifyTime',
          align: 'center',
          width: 100,
          scopedSlots: {
            customRender: 'modifyTime'
          }
        }
      ],
      data: [],
      url: {
        list: '/triplog/dyTriplogSearch/triplogChangeRecord' //7.11.	电子路单历史变更接口
      },
      dictOptions: {}
    }
  },
  created() {},
  mounted() {
    // this.initData()
  },
  computed: {},
  methods: {
    initData(record) {
      // this.url.list
      console.log(record)
      let sendData = {
        triplogId: record.id,
        runDate: record.runDate
      }
      this.loading = true
      getAction(this.url.list, sendData).then(res => {
        console.log(res)
        if (res.success) {
          // this.data = res.result
          // let data = this.data
          let data = res.result
          for (let i = 0; i < data.length; i++) {
            if (i > 0) {
              data[i].new = {}
              console.log(data, data[i - 1])
              if (data[i].routeName != data[i - 1].routeName) {
                data[i].new.routeName = data[i].routeName
              }
              if (data[i].busName != data[i - 1].busName) {
                data[i].new.busName = data[i].busName
              }
              if (data[i].employeeName != data[i - 1].employeeName) {
                data[i].new.employeeName = data[i].employeeName
              }
              if (data[i].serviceName != data[i - 1].serviceName) {
                data[i].new.serviceName = data[i].serviceName
              }
              if (data[i].direction != data[i - 1].direction) {
                data[i].new.direction = data[i].direction
              }
              if (data[i].fromStationName != data[i - 1].fromStationName) {
                data[i].new.fromStationName = data[i].fromStationName
              }
              if (data[i].toStationName != data[i - 1].toStationName) {
                data[i].new.toStationName = data[i].toStationName
              }
              if (data[i].triplogBeginTime != data[i - 1].triplogBeginTime) {
                data[i].new.triplogBeginTime = data[i].triplogBeginTime
              }
              if (data[i].triplogEndTime != data[i - 1].triplogEndTime) {
                data[i].new.triplogEndTime = data[i].triplogEndTime
              }
              if (data[i].runMileage != data[i - 1].runMileage) {
                data[i].new.runMileage = data[i].runMileage
              }
              if (data[i].remark != data[i - 1].remark) {
                data[i].new.remark = data[i].remark
              }
              if (data[i].routeNameRun != data[i - 1].routeNameRun) {
                data[i].new.routeNameRun = data[i].routeNameRun
              }
              if (data[i].dispatchSendTime != data[i - 1].dispatchSendTime) {
                data[i].new.dispatchSendTime = data[i].dispatchSendTime
              }
              if (data[i].recordStatus != data[i - 1].recordStatus) {
                data[i].new.recordStatus = data[i].recordStatus
              }
              if (data[i].operator != data[i - 1].operator) {
                data[i].new.operator = data[i].operator
              }
              if (data[i].modifyTime != data[i - 1].modifyTime) {
                data[i].new.modifyTime = data[i].modifyTime
              }
            }
          }
          this.data = data
          console.log(this.data)
        } else {
          this.$message.warning(res.message)
        }
        this.loading = false
      })
    },
    close() {
      this.visible = false
    },
    handleCancel() {
      this.close()
    }
  }
}
</script>
<style scoped lang="less">
@import '~@assets/less/common.less';

.ant-table td {
  white-space: nowrap;
}

.red {
  color: red;
}
</style>
