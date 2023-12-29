<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="12" :md="12" :sm="24">
            <a-form-item label="所属线路">
              <GCIRouteSelect item="id" v-model="routeId" @change="changeRouteSelect"></GCIRouteSelect>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="12" :md="12" :sm="24">
            <a-form-item label="车辆">
              <GCIBusSelect item="busId" :routeId="routeId" v-model="queryParam.busId"></GCIBusSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="12" :md="12" :sm="24">
            <a-form-item label="路单时间">
              <a-range-picker
                :style="'width: 100%'"
                :value="rangeTime"
                :ranges="{
                  今天: [moment(today[0]), moment(today[1])],
                  近一个月: [moment().subtract(1, 'months'), moment()]
                }"
                show-time
                format="YYYY-MM-DD HH:mm:ss"
                @change="onChangeTime"
              ></a-range-picker>
            </a-form-item>
          </a-col>

          <!--
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="obuId">
              <a-input placeholder="请输入obuId" v-model="queryParam.obuId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="dataSerial">
              <a-input placeholder="请输入dataSerial" v-model="queryParam.dataSerial"></a-input>
            </a-form-item>
          </a-col> -->
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="routeCode">
                <a-input placeholder="请输入routeCode" v-model="queryParam.routeCode"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="runningNo">
                <a-input placeholder="请输入runningNo" v-model="queryParam.runningNo"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="8" :lg="7" :md="8" :sm="24">
              <a-form-item label="obuTime">
                <a-input placeholder="请输入obuTime" v-model="queryParam.obuTime"></a-input>
              </a-form-item>
            </a-col>
          </template>

          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <span style="float: left;ov erflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a-button type="primary" icon="download" style="margin-left: 8px" @click="handleExportXls('调度日志')"
                >导出</a-button
              >
              <!--
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
              -->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!--<a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <!--      <a-button type="primary" icon="download" @click="handleExportXls('调度日志')">导出</a-button>-->
      <!--
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete" />删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down"/></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <!--
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>
      -->
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
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <triplogBegin-modal ref="modalForm" @ok="modalFormOk"></triplogBegin-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import TriplogBeginModal from './modules/TriplogBeginModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import GCIRouteSelect from '@comp/gci/GCIRouteSelect'
import GCIBusSelect from '@comp/gci/GCIBusSelect'
import moment from 'moment'
import {getAction} from "../../api/manage";

export default {
  name: 'TriplogBeginList',
  mixins: [JeecgListMixin],
  components: {
    TriplogBeginModal,
    GCIRouteSelect,
    GCIBusSelect
  },
  data() {
    return {
      moment,
      description: '调度日志管理页面',
      routeId: '',
      rangeTime: [],
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
          title: '车辆牌照',
          align: 'center',
          dataIndex: 'numberPlate'
        },
        {
          title: '车辆编码',
          align: 'center',
          dataIndex: 'busCode'
        },
        {
          title: '车辆',
          align: 'center',
          dataIndex: 'busName'
        },
        {
          title: '终端ID',
          align: 'center',
          dataIndex: 'obuId'
        },
        {
          title: '调度开始时间',
          align: 'center',
          dataIndex: 'obuTime'
        },
        {
          title: '所属线路',
          align: 'center',
          dataIndex: 'routeName'
        },
        {
          title: '触发支线路',
          align: 'center',
          dataIndex: 'routeSubName'
        },
        {
          title: '趟次号',
          align: 'center',
          dataIndex: 'runningNo'
        },
        {
          title: '首站名称',
          align: 'center',
          dataIndex: 'fromStationName'
        },
        {
          title: '末站名称',
          align: 'center',
          dataIndex: 'toStationName'
        },
        {
          title: '司机资格证',
          align: 'center',
          dataIndex: 'qualification'
        },
        {
          title: '驾驶员',
          align: 'center',
          dataIndex: 'employeeName'
        },
        {
          title: '通讯时间',
          align: 'center',
          dataIndex: 'redisTime'
        }
        /**
          ,
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
           */
      ],
      url: {
        list: '/dy/triplogBegin/list',
        delete: '/dy/triplogBegin/delete',
        deleteBatch: '/dy/triplogBegin/deleteBatch',
        exportXlsUrl: 'dy/triplogBegin/exportXls',
        importExcelUrl: 'dy/triplogBegin/importExcel'
      },
      today: []
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  created() {
    this.initTimes()
  },
  methods: {
    initDictConfig(){
      this.initTimes()
    },
    initTimes(){
      let a = moment().format('YYYY-MM-DD 00:00:00')
      let b = moment().format('YYYY-MM-DD 23:59:59')
      this.today = [...this.today, a, b]
      console.log(this.today)
      //初始化时间
      this.rangeTime = [this.today[0],this.today[1]]
      this.queryParam.beginTime = a
      this.queryParam.endTime = b
    },
    changeRouteSelect() {
      this.queryParam.routeId = this.routeId
      console.info('routeId:' + this.routeId + ' busId: ' + this.queryParam.busId)
    },
    onChangeTime(dates, dateStrings) {
      console.log('From: ', dates[0], ', to: ', dates[1])
      console.log('From: ', dateStrings[0], ', to: ', dateStrings[1])
      this.rangeTime = [dateStrings[0],dateStrings[1]]
      this.queryParam.beginTime = dateStrings[0]
      this.queryParam.endTime = dateStrings[1]
    },
    loadData(arg) {
      if (!this.url.list) {
        this.$message.error('请设置url.list属性!')
        return
      }
      // 加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      const params = this.getQueryParams();// 查询条件
      if(!params.beginTime && !params.endTime){
        this.$message.error('请先选择路单时间!')
        return
      }
      this.loading = true;
      getAction(this.url.list, params).then((res) => {
        if (res.success) {
          this.dataSource = res.result.records;
          this.ipagination.total = res.result.total;
        }
        if (res.code === 500) {
          this.$message.warning(res.message)
        }
        this.loading = false;
      }).finally(() => {
        this.loading = false;
      });
      this.onClearSelected();
    },
    searchReset(){
      this.queryParam = {}
      this.routeId = undefined
      this.initTimes()
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
