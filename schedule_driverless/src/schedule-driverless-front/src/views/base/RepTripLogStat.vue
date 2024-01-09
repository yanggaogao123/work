<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="9":lg="10" :md="12" :sm="24">
            <a-form-item label="路单开始时间">
              <a-range-picker
                :show-time="showTime"
                format="YYYY-MM-DD HH"
                :placeholder="['开始时间', '结束时间']"
                v-model="rangeTime"
                @change="handleChangeTime"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="8" :md="12" :sm="24">
            <a-form-item label="统计方式">
              <a-radio-group v-model="searchType">
                <a-radio v-for="(item,index) in searchOptions" :key="index" :value="item.value">{{ item.label }}
                </a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :xl="7":lg="7" :md="12" :sm="24" v-show="searchType ==='ByRoute'?true:false">
            <a-form-item label="所属线路">
              <GCIRouteSelect placeholder="请选择线路" v-model="queryParam.routeId" @change="changeRoute" :width="200"></GCIRouteSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="5":lg="5" :md="12" :sm="24" v-show="searchType ==='ByBus'?true:false">
            <a-form-item label="车辆">
              <a-tooltip placement="leftBottom">
                <template slot="title">
                  <span v-if="tooltipData !==''">{{ tooltipData }}</span>
                </template>
                <a-button type="primary" @click="openModal">已选车辆</a-button>
              </a-tooltip>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-operator" style="margin-top:-5px;margin-bottom: 10px">
      <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
      <a-button type="primary" @click="searchReset"icon="reload" style="margin-left: 10px">重置</a-button>
    </div>
    <rep-trip-log-stat-modal ref="modal" @ok="busSelectOk"></rep-trip-log-stat-modal>
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
qs.stringify
<!--路单统计报表-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import GCIRouteSelect from '@/components/gci/GCIRouteSelect'
  import moment from 'moment/moment'
  import { getAction } from '@/api/manage'
  import RepTripLogStatModal from './modules/RepTripLogStatModal'
  export default {
    name: 'RepTripLogStat',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect, GCIRouteSelect, RepTripLogStatModal
    },
    data() {
      return {
        showTable: 'false',
        tooltipData: '',
        searchType: 'ByRoute',
        searchOptions: [{
          label: '按线路',
          value: 'ByRoute'
        }, {
          label: '按车辆',
          value: 'ByBus'
        }],
        showTime: {
          format: 'HH',
          defaultValue: [
            moment('00', 'HH'),
            moment('23', 'HH')
          ]
        },
        beginTimeStr: '',
        endTimeStr: '',
        routId: '',
        rangeTime: [moment(new Date()).startOf('day'), moment(new Date()).endOf('day')],
        routeData: [],
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: false,
          showSizeChanger: true,
          total: 0
        },
        queryParam: {
          routeId: undefined,
          busCodes: []
        },
        // 表头
        columns: [
          {
            title: '运营时段',
            align: 'center',
            dataIndex: 'runTimeNum'
          },
          {
            title: '上行总班次',
            align: 'center',
            dataIndex: 'tripsUp'
          },
          {
            title: '下行总班次',
            align: 'center',
            dataIndex: 'tripsDown'
          },
          {
            title: '上行全程班次',
            align: 'center',
            dataIndex: 'tripsNormalUp'
          },
          {
            title: '下行全程班次',
            align: 'center',
            dataIndex: 'tripsNormalDown'
          },
          {
            title: '上行短线班次',
            align: 'center',
            dataIndex: 'tripsShortUp'
          },
          {
            title: '下行短线班次',
            align: 'center',
            dataIndex: 'tripsShortDown'
          },
          {
            title: '上行运力',
            align: 'center',
            dataIndex: 'capacityTotalUp'
          },
          {
            title: '下行运力',
            align: 'center',
            dataIndex: 'capacityTotalDown'
          },
          {
            title: '上行总运营时间',
            align: 'center',
            dataIndex: 'runTimeUp'
          },
          {
            title: '下行总运营时间',
            align: 'center',
            dataIndex: 'runTimeDown'
          },
          {
            title: '上行停总站时间',
            align: 'center',
            dataIndex: 'anchorTimeUp'
          },
          {
            title: '下行停总站时间',
            align: 'center',
            dataIndex: 'anchorTimeDown'
          },
          {
            title: '上行总运行里程',
            align: 'center',
            dataIndex: 'milRunUp'
          },
          {
            title: '下行总运行里程',
            align: 'center',
            dataIndex: 'milRunDown'
          }
        ],
        url: {
          list: '/base/repTriplogStat/list'
        }
      }
    },
    methods: {
    loadData(arg) {
      // 加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      let ipagination = { pageNo: this.ipagination.current, pageSize: this.ipagination.pageSize }
      let params = Object.assign(this.queryParam, ipagination)
      params.busCodes = params.busCodes.toString()
      this.loading = true;
      getAction(this.url.list, params).then((res) => {
        if (res.success) {
          this.dataSource = res.result.records;
          this.ipagination.total = res.result.total;
        }
        if (res.code === 510) {
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },
    checkParam() {
      if (this.searchType === 'ByRoute') {
        if (this.queryParam.routeId === undefined || this.queryParam.routeId === '') {
          this.$message.warn('请选择线路')
          return false;
        }
      } else if (this.searchType === 'ByBus') {
        if (this.queryParam.busCodes.length === 0) {
          this.$message.warn('请选择车辆')
          return false;
        }
      }
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
    searchQuery() {
      if (this.checkParam()) {
        this.queryParam.searchType = this.searchType
        this.queryParam.runDate = this.rangeTime[0].format('YYYY-MM-DD')
        this.queryParam.beginRunDateNum = this.rangeTime[0].format('HH')
        this.queryParam.endRunDateNum = this.rangeTime[1].format('HH')
        this.loadData(1)
      }
    },
    searchReset() {
      this.searchType = 'ByRoute'
      this.queryParam = {}
      this.$set(this.queryParam, 'routeId', undefined)
      this.$set(this.queryParam, 'busCodes', [])
      this.rangeTime = [moment(new Date()).startOf('day'), moment(new Date()).endOf('day')]
      this.tooltipData = ''
      this.$refs.modal.dataSource2 = []
      this.dataSource = []
    },
    changeRoute(value) {
      this.queryParam.routeId = value
    },
    handleChangeTime(date, dateStr) {
      this.rangeTime = date
      this.beginTimeStr = dateStr[0]
      this.endTimeStr = dateStr[1]
    },
    openModal() {
      this.$refs.modal.open()
    },
    busSelectOk(busCodes) {
      this.tooltipData = busCodes.join(',')
      this.queryParam.busCodes = busCodes
    },
      handleTableChange(pagination) {
        let temp = JSON.parse(JSON.stringify(pagination));
        this.ipagination = temp;
        this.loadData();
      }
  }
  }
</script>
