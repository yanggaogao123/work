<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="线路名称">
              <GCIRouteSelect
                placeholder="请选择线路"
                v-model="queryParam.routeId"
                @change="changeRoute"
                mode="mode"
                item="id"
              ></GCIRouteSelect>
            </a-form-item>
          </a-col>

          <a-col :xl="7" :lg="7" :md="8" :sm="24">
            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button> -->

      <!--      <a-button type="primary" icon="download" @click="handleExportXls('线路')">导出</a-button>-->

      <!-- <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown> -->
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
        >项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
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
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img
            v-else
            :src="getImgView(text)"
            height="25px"
            alt=""
            style="max-width:80px;font-size: 12px;font-style: italic;"
          />
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button v-else :ghost="true" type="primary" icon="download" size="small" @click="uploadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleExport(record)">导出</a>
          <a-divider type="vertical" />
          <a @click="openLogs(record)">修改日志</a>
        </span>
      </a-table>
    </div>

    <bsRouteMileage-modal ref="modalForm" @ok="modalFormOk"></bsRouteMileage-modal>
    <BsRouteMileageLogModal ref="bsRouteMileageLog" @ok="modalFormOk"></BsRouteMileageLogModal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BsRouteMileageModal from './modules/BsRouteMileageModal'
import GCIRouteSelect from '@/components/gci/GCIRouteSelect'
import { downloadFileByFileName, getAction } from '../../api/manage'
import BsRouteMileageLogModal from './modules/BsRouteMileageLogModal'

export default {
  name: 'BsRouteMileageList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    BsRouteMileageModal,
    GCIRouteSelect,
    BsRouteMileageLogModal
  },
  data() {
    return {
      description: '站间里程设置页面',
      // 表头
      columns: [
        {
          title: '运营机构',
          align: 'center',
          dataIndex: 'organRunId_dictText'
        },
        {
          title: '线路名称',
          align: 'center',
          dataIndex: 'routeName'
        },

        {
          title: '线路编码',
          align: 'center',
          dataIndex: 'routeCode'
        },
        {
          title: '线路简称',
          align: 'center',
          dataIndex: 'shortName'
        },
        {
          title: '线路版本',
          align: 'center',
          dataIndex: 'routeVersion'
        },

        {
          title: '更新人',
          align: 'center',
          dataIndex: 'lastOperator'
        },

        {
          title: '修改日期',
          align: 'center',
          dataIndex: 'lastUpdated',
          customRender: function(text) {
            return !text ? '' : text.length > 10 ? text.substr(0, 10) : text
          }
        },

        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          // fixed:"right",
          width: 147,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/baseservice/bsRoute/getRouteList',
        delete: '/baseservice/bsRoute/delete',
        deleteBatch: '/baseservice/bsRoute/deleteBatch',
        exportXlsUrl: '/routeSubSta/exportRouteSubStaMileages',
        importExcelUrl: 'baseservice/bsRoute/importExcel'
      },
      dictOptions: {}
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    initDictConfig() {},
    changeRoute(value) {
      console.log(value)
      this.queryParam.routeId = value
    },
    handleExport(record) {
      downloadFileByFileName(this.url.exportXlsUrl, '线路站间里程.xls', { routeId: record.routeId })
    },
    openLogs(record) {
      this.$refs.bsRouteMileageLog.showLog = true
      this.$refs.bsRouteMileageLog.showLogs(record)
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
