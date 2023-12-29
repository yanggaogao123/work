<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="机构名称">
              <GCIOrgTreeSelect
                v-model="queryParam.sysOrgCode"
                placeholder="请选择机构"
                @changeOptions="queryListRoute"
                :isGetOption="true"
              ></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="6" :sm="24">
            <a-form-item label="运营线路">
              <a-select
                allowClear
                placeholder="请选择线路"
                v-model="oprSelect"
                show-search
                :filter-option="addFilterOption"
                @change="handleChangeRoute"
              >
                <a-select-option v-for="(item, index) in routeData" :key="index" :value="item.id">
                  {{ item.routeName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="运营日期">
              <j-date allowClear dateFormat="YYYY-MM-DD" placeholder="请选择日期" v-model="queryParam.runDate" />
            </a-form-item>
          </a-col>
          <a-col :xl="9" :lg="9" :md="8" :sm="24">
            <a-form-item label="停站时长(秒)">
              <a-input-number style="width: 70px" v-model="queryParam.minSecond" :min="0" />
              到
              <a-input-number style="width: 70px" v-model="queryParam.maxSecond" :min="0" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-operator" style="margin-top:-5px;margin-bottom: 10px">
      <a-button type="primary" @click="searchQuery2" icon="search">查询</a-button>
      <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
      <a-button type="primary" @click="exportExcel" style="margin-left: 10px" :loading="excelLoading">导出</a-button>
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
      @change="handleTableChange"
    >
    </a-table>
  </a-card>
</template>

<!--车辆滞站查询-->
<script>
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
import moment from 'moment/moment'
import { getAction, downloadFileAwait } from '@/api/manage'
import JDate from '@/components/jeecg/JDate.vue'

export default {
  name: 'ArrivalAndDeparture',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    GCIOrgTreeSelect,
    JDate
  },
  data() {
    return {
      excelLoading: false,
      oprSelect: undefined,
      routeData: [],
      queryParam: {
        runDate: moment(new Date()).format('YYYY-MM-DD'),
        minSecond: 20,
        maxSecond: 30
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
          title: '机构名称',
          align: 'center',
          dataIndex: 'organName'
        },
        {
          title: '运营线路',
          align: 'center',
          dataIndex: 'routeNameRun'
        },
        {
          title: '车辆牌照',
          align: 'center',
          dataIndex: 'numberPlate'
        },
        {
          title: '当前站',
          align: 'center',
          dataIndex: 'curStationName'
        },
        {
          title: '进站时间',
          align: 'center',
          dataIndex: 'arrivalTime'
        },
        {
          title: '出站时间',
          align: 'center',
          dataIndex: 'departTime'
        },
        
        {
          title: '本站停站时长',
          align: 'center',
          dataIndex: 'stopTime'
        },
        {
          title: '运营日期',
          align: 'center',
          dataIndex: 'runDate'
        }
      ],
      url: {
        list: '/base/arrivalAndDeparture/list',
        listRoute: '/common/sys/queryRouteByOrganId',
        export: '/base/arrivalAndDeparture/exportXls'
      }
    }
  },
  methods: {
    searchQuery2() {
      this.isorter = {}
      if(!this.queryParam.routeId) {
        this.$message.warning('请选择线路!')
        return;
      }
      if(!this.queryParam.runDate) {
        this.$message.warning('请选择日期!')
        return;
      }
      this.searchQuery()
    },
    searchReset() {
      this.oprSelect = undefined
      this.routeData = []
      this.queryParam = {}
      this.$set(this.queryParam, 'minSecond', 20)
      this.$set(this.queryParam, 'maxSecond', 30)
      this.$set(this.queryParam, 'runDate', moment(new Date()).format('YYYY-MM-DD'))
      this.loadData(1)
    },
    queryListRoute(orgData) {
      this.queryParam.isLeaf = orgData.isLeaf
      this.queryParam.organId = orgData.id
      let params = { organId: this.queryParam.organId, isLeaf: this.queryParam.isLeaf }
      getAction(this.url.listRoute, params).then(res => {
        if (res.success) {
          this.routeData = res.result
        }
      })
    },
    async exportExcel() {
      if(!this.queryParam.routeId) {
        this.$message.warning('请选择线路!')
        return;
      }
      if(!this.queryParam.runDate) {
        this.$message.warning('请选择日期!')
        return;
      }
      this.excelLoading = true
      await downloadFileAwait(this.url.export, '车辆滞站信息.xlsx', this.queryParam)
      this.excelLoading = false
    },
    handleChangeRoute(value) {
      this.queryParam.routeId = value
    },
    addFilterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }
  }
}
</script>

<style lang="less" scoped>
.ant-input-number /deep/ .ant-input-number-handler-wrap {
  visibility: hidden;
}
</style>
