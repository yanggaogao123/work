<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="12" :md="12" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect
                v-model="queryParam.orgCode"
                :isGetOption="true"
                placeholder="请选择所属机构"
                @changeOptions="queryListRoute"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="12" :md="12" :sm="24">
            <a-form-item label="线路">
              <a-select
                allowClear
                placeholder="请选择线路"
                v-model="queryParam.routeId"
                show-search
                :filter-option="addFilterOption"
                @change="handleChangeRoute">
                <a-select-option v-for="(item,index) in routeData" :key="index" :value="item.id">
                  {{ item.routeName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="12" :md="12" :sm="24">
            <a-form-item label="车辆">
              <GCIBusSelect :routeId="routeId" v-model="queryParam.obuId"/>
            </a-form-item>
          </a-col>
          <a-col :xl="10":lg="12" :md="12" :sm="24">
            <a-form-item label="进出站时间">
              <a-range-picker
                :show-time="showTime"
                format="YYYY-MM-DD HH:mm:ss"
                :placeholder="['开始时间', '结束时间']"
                v-model="rangeTime"
                @change="handleChangeTime"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :xl="12" :lg="12" :md="24" :sm="24" style="margin-bottom: 10px">
            <a-button type="primary" @click="searchQuery2" icon="search">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
            <a-button type="primary" @click="exportExcel" icon="download" :loading="excelLoading" style="margin-left: 10px">导出</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>
  </a-card>
</template>

<!--视频客流查询-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { getAction, downloadFileAwait } from '@/api/manage'
  import GCIBusSelect from '@/components/gci/GCIBusSelect'
  import moment from 'moment'
  export default {
    name: 'PassengerFlux',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect, GCIBusSelect
    },
    data() {
      return {
        excelLoading: false,
        routeId: '',
        showTime: {
          format: 'HH:mm:ss',
          defaultValue: [
            moment('00:00:00', 'HH:mm:ss'),
            moment('23:59:59', 'HH:mm:ss')
          ]
        },
        beginTime: moment(new Date()).startOf('day'),
        endTime: moment(new Date()).endOf('day'),
        beginTimeStr: moment(new Date()).startOf('day').format('YYYY-MM-DD HH:mm:ss'),
        endTimeStr: moment(new Date()).endOf('day').format('YYYY-MM-DD HH:mm:ss'),
        rangeTime: [moment(new Date()).startOf('day'), moment(new Date()).endOf('day')],
        oprSelect: undefined,
        routeData: [],
        queryParam: {
          routeId: undefined,
          beginTime: moment(new Date()).startOf('day').format('YYYY-MM-DD HH:mm:ss'),
          endTime: moment(new Date()).endOf('day').format('YYYY-MM-DD HH:mm:ss')
        },
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '线路',
            align: 'center',
            dataIndex: 'routeName'
          },
          {
            title: '车辆名称',
            align: 'center',
            dataIndex: 'busName'
          },
          {
            title: '车牌',
            align: 'center',
            dataIndex: 'numberPlate'
          },
          {
            title: '站点',
            align: 'center',
            dataIndex: 'routeStationName'
          },
          {
            title: '方向',
            align: 'center',
            dataIndex: 'routesubDirection'
          },
          {
            title: '站序',
            align: 'center',
            dataIndex: 'orderNumber'
          },
          {
            title: '时间',
            align: 'center',
            dataIndex: 'obuTime'
          },
          {
            title: '上车人数',
            align: 'center',
            dataIndex: 'upCount'
          },
          {
            title: '下车人数',
            align: 'center',
            dataIndex: 'downCount'
          }
        ],
        url: {
          list: '/base/passengerSum/list',
          export: '/base/passengerSum/export',
          listRoute: '/common/sys/queryRouteByOrganId'
        }
      }
    },
    methods: {
      checkParam() {
        if (this.rangeTime.length === 0) {
          this.$message.warn('请选择时间段')
          return false;
        }
        if (Math.abs(this.rangeTime[0].toDate().getTime() - this.rangeTime[1].toDate().getTime()) > 24 * 60 * 60 * 1000) {
          this.$message.warn('时间差不能大于24小时')
          return false;
        }
        return true
      },
      searchQuery2() {
        this.isorter = {}
        if (this.checkParam()) {
          this.queryParam.beginTime = this.beginTimeStr
          this.queryParam.endTime = this.endTimeStr
          this.searchQuery()
        }
      },
      searchReset() {
        this.routeId = ''
        this.oprSelect = undefined
        this.queryParam = {}
        this.$set(this.queryParam, 'routeId', undefined)
        this.$set(this.queryParam, 'beginTime', moment(new Date()).startOf('day').format('YYYY-MM-DD HH:mm:ss'))
        this.$set(this.queryParam, 'endTime', moment(new Date()).endOf('day').format('YYYY-MM-DD HH:mm:ss'))
        this.rangeTime = [ this.beginTime, this.endTime ]
        this.loadData(1)
      },
      queryListRoute(orgData) {
        this.queryParam.isLeaf = orgData.isLeaf
        this.queryParam.organId = orgData.id
        let params = { organId: this.queryParam.organId, isLeaf: this.queryParam.isLeaf }
        getAction(this.url.listRoute, params).then((res) => {
          if (res.success) {
            this.routeData = res.result
          }
        })
      },
      handleChangeRoute(value) {
        this.queryParam.routeId = value
        this.routeId = value
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      },
      handleChangeTime(date, dateStr) {
        this.rangeTime = date
        this.beginTimeStr = dateStr[0]
        this.endTimeStr = dateStr[1]
      },
      async exportExcel() {
        if (this.checkParam()) {
          this.excelLoading = true
          this.queryParam.beginTime = this.beginTimeStr
          this.queryParam.endTime = this.endTimeStr
          await downloadFileAwait(this.url.export, '视频客流.xlsx', this.queryParam)
          this.excelLoading = false
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .ant-input-number /deep/ .ant-input-number-handler-wrap {
    visibility: hidden;
  }
</style>
