<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="补卡日期">
              <a-range-picker :value="queryParamTime" @change="handleChangeParamTime"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6">
            <a-form-item label="异常类型">
              <a-select placeholder="请选择异常类型" :options="signTypeOptions" v-model="queryParam.patchType"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6">
            <a-form-item label="异常状态">
              <a-select placeholder="请选择打卡状态" :options="signStatusOptions" v-model="queryParam.patchStatus"/>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="6">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">条件清除</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAddPatch" type="primary" icon="plus">添加补卡</a-button>
      <a-button type="primary" @click="handleExportXls('考勤补卡信息记录表')"  icon="download">导出</a-button>
      <!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
      <!--<a-dropdown v-if="selectedRowKeys.length > 0">-->
        <!--<a-button @click="batchDel" icon="delete" type="primary" ghost>批量删除</a-button>-->
      <!--</a-dropdown>-->
    </div>

    <!-- table区域-begin -->
    <div>
      <a-alert showIcon type="info" style="margin-bottom: 18px">
        <div slot="message">
          <span>已选择</span>
          <a style="padding: 0 4px;">{{ selectedRowKeys.length }}</a>
          <span>项</span>
          <a-divider type="vertical"/>
          <a @click="onClearSelected">清空</a>
        </div>
      </a-alert>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="patchReason" slot-scope="text">
          <j-ellipsis :value="text" :length="20"/>
        </span>
        <template slot="action" slot-scope="text, record">
          <template v-if="record.bpmStatus === '1'">
            <a @click="startProcess(record)">提交流程</a>
            <a-divider type="vertical"/>
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </template>
          <template v-if="record.bpmStatus === '2' || record.bpmStatus === '3'">
            <a @click="handlePreviewPic(record)">审批进度</a>
          </template>
          <template v-if="record.bpmStatus === '4'">
            <a @click="handlePreviewPic(record)">历史进度</a>
          </template>
        </template>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <oa-sign-patch-now-modal :visible.sync="visible" @ok="modalFormOk"/>
    <!--流程审批进度表单 -->
    <ext-act-process-inst-pic-modal ref="extActProcessInstPicModal"/>
  </a-card>
</template>

<script>
  import moment from 'moment/moment'
  import JEllipsis from '@comp/jeecg/JEllipsis'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getOptionsSignStatus, getOptionsSignType, getSignStatus, getSignType } from './utils/util'
  import OaSignPatchNowModal from './modules/OaSignPatchNowModal'
  import { postAction } from '@api/manage'
  import ExtActProcessInstPicModal from '@views/modules/extbpm/process/modules/ExtActProcessInstPicModal'
  import { filterDictText, initDictOptions } from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'OaSignPatchList',
    mixins: [JeecgListMixin],
    components: { OaSignPatchNowModal, JEllipsis,ExtActProcessInstPicModal },
    data() {
      return {
        description: '考勤补卡信息记录表管理页面',
        visible: false,
        flowCode: 'oa_sign_patch_001',
        bpmStatusDictOptions: [],
        signStatusOptions: getOptionsSignStatus(),
        signTypeOptions: getOptionsSignType(),
        queryParamTime: [],
        // 表头
        columns: [
          {
            title: '补卡类型',
            align: 'center',
            dataIndex: 'patchType',
            customRender: (text) => getSignType(text)
          },
          {
            title: '补卡日期',
            align: 'center',
            key: 'abnormalTime_date',
            dataIndex: 'abnormalTime',
            customRender: (text) => moment(text).format('YY/MM/DD')
          },
          {
            title: '异常时间',
            align: 'center',
            key: 'abnormalTime_time',
            dataIndex: 'abnormalTime',
            customRender: (text) => moment(text).format('HH:mm:ss')
          },
          {
            title: '正常时间',
            align: 'center',
            dataIndex: 'patchTime',
            customRender: (text) => text + ':00'
          },
          {
            title: '异常状态',
            align: 'center',
            dataIndex: 'patchStatus',
            customRender: (text) => getSignStatus(text)
          },
          {
            title: '补卡原因',
            align: 'center',
            dataIndex: 'patchReason',
            scopedSlots: { customRender: 'patchReason' }
          },
          {
            title: '操作日期',
            align: 'center',
            dataIndex: 'createTime',
            customRender: (text) => moment(text).format('YY/MM/DD HH:mm:ss')
          },
          {
            title: '审批状态',
            align: 'center',
            dataIndex: 'bpmStatus',
            customRender: (text, record) => {
              let tbTypeText = filterDictText(this.bpmStatusDictOptions, text)
              if(record.bizStatus === '1'){
                tbTypeText = '已同意'
              }
              if (record.bizStatus === '2') {
                tbTypeText = '未同意'
              }
              return tbTypeText;
            }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            width: '120px',
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: '/sign/patch/list',
          delete: '/sign/patch/delete',
          deleteBatch: '/sign/patch/deleteBatch',
          exportXlsUrl: 'sign/patch/exportXls',
          importExcelUrl: 'sign/patch/importExcel',
          startProcess: '/process/extActProcess/startMutilProcess',
        },
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      initDictConfig() {
        initDictOptions('bpm_status').then((res) => {
          if (res.success) {
            this.bpmStatusDictOptions = res.result
          }
        })
      },
      handlePreviewPic(record) {
        this.$refs.extActProcessInstPicModal.title = '流程图'
        this.$refs.extActProcessInstPicModal.preview(this.flowCode, record.id)
      },
      startProcess(record) {
        this.$confirm({
          title: '提示',
          content: '确认提交流程吗?',
          onOk: () => {
            this.loading = true
            postAction(this.url.startProcess, {
              flowCode: this.flowCode,
              id: record.id,
              formUrl: 'modules/eoa/sign/modules/OaSignPatchForm',
              formUrlMobile: 'modules/eoa/sign/modules/OaSignPatchForm'
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
      searchReset() {
        this.queryParamTime = []
        this.queryParam = {}
        this.loadData(1)
      },
      handleAddPatch() {
        this.visible = true
      },
      handleChangeParamTime(value) {
        if (value && value.length > 0) {
          this.queryParamTime = value
          this.queryParam.patchTimeBegin = moment(value[0]).format('YYYY-MM-DD')
          this.queryParam.patchTimeEnd = moment(value[1]).format('YYYY-MM-DD')
        } else {
          this.queryParamTime = []
          this.queryParam.patchTimeBegin = null
          this.queryParam.patchTimeEnd = null
        }
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>