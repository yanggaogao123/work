<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="类型">
              <j-dict-select-tag v-model="queryParam.type" dictCode="mettingType" placeholder="请选择类型" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('会议管理表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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
        class="j-table-force-nowrap"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="location" slot-scope="text, record, index">
          <span><j-ellipsis :value="text&&text.length>0?text:record.customLocation" :length="10"></j-ellipsis></span>
        </template>
        <template slot="action" slot-scope="text, record">
          <!--不走流程-->
          <template v-if="record.approvalStatus=='0'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical"/>
            <a @click="handleDelete(record.id)">删除</a>
          </template>
          <!--走流程-->
          <template v-if="record.approvalStatus=='1'">
            <template v-if="record.bpmStatus === '1'">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" v-if="record.status !== '3'"/>
              <a @click="startProcess(record)" v-if="record.status !== '3'">提交流程</a>
              <a-divider type="vertical"/>
              <a @click="handleDelete(record.id)">删除</a>
            </template>
            <template v-if="record.bpmStatus === '2' || record.bpmStatus === '3'">
              <template v-if="record.bpmStatus === '3'">
                <a @click="handleEdit(record)">编辑</a>
                <a-divider type="vertical"/>
              </template>
              <a @click="handlePreviewPic(record)">审批进度</a>
            </template>
            <template v-if="record.bpmStatus === '4'">
              <a @click="handlePreviewPic(record)">历史进度</a>
            </template>
          </template>
        </template>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <eoaMetting-modal ref="modalForm" @ok="modalFormOk"></eoaMetting-modal>
   <ext-act-process-inst-pic-modal ref="extActProcessInstPicModal"/>
  </a-card>
</template>

<script>
  import EoaMettingModal from './modules/EoaMettingModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {  postAction } from '@api/manage'
  import ExtActProcessInstPicModal from '@views/modules/extbpm/process/modules/ExtActProcessInstPicModal'
  import JEllipsis from "@/components/jeecg/JEllipsis.vue";
  export default {
    name: "EoaMettingList",
    mixins:[JeecgListMixin],
    components: {
      EoaMettingModal,
      JEllipsis,
      ExtActProcessInstPicModal
    },
    data () {
      return {
        description: '会议管理表管理页面',
        flowCode: 'desform_eoa_metting',
        // 表头
        columns: [{
            title: '名称',
            align:"center",
            dataIndex: 'name'
           },{
            title: '会议地点',
            align:"center",
            dataIndex: 'location_dictText',
            scopedSlots: { customRender: 'location' }
        },{
            title: '召集人',
            align:"center",
            dataIndex: 'originator_dictText'
           },{
            title: '联系人',
            align:"center",
            dataIndex: 'contacts_dictText'
           },{
            title: '开始时间',
            align:"center",
            dataIndex: 'beginTime'
           },{
            title: '结束时间',
            align:"center",
            dataIndex: 'endTime'
           },{
            title: '会议状态',
            align:"center",
            dataIndex: 'status_dictText'
           },{
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/metting/eoaMetting/list",
          delete: "/metting/eoaMetting/delete",
          deleteBatch: "/metting/eoaMetting/deleteBatch",
          exportXlsUrl: "metting/eoaMetting/exportXls",
          importExcelUrl: "metting/eoaMetting/importExcel",
          startProcess: '/process/extActProcess/startMutilProcess'
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
      startProcess(record) {
        this.$confirm({
          title: '提示',
          content: '确认提交流程吗?',
          onOk: () => {
            this.loading = true
            postAction(this.url.startProcess, {
              flowCode: this.flowCode,
              id: record.id,
              formUrl: 'modules/eoa/metting/modules/EoaMettingForm',
              formUrlMobile: 'modules/eoa/metting/modules/EoaMettingForm'
            }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.loadData()
                this.onClearSelected()
              } else {
                this.$message.warning(res.message)
              }
            }).finally(() => {
              this.loading = false
            })
          }
        })
      },
      handlePreviewPic(record) {
        this.$refs.extActProcessInstPicModal.title = '流程图'
        this.$refs.extActProcessInstPicModal.preview(this.flowCode, record.id)
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  @import '~@/assets/less/TableExpand.less';
</style>