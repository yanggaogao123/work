<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="预计外出日期">
              <a-range-picker :value="queryParamTime" @change="handleChangeParamTime"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6">
            <a-form-item label="是否用车">
              <a-select placeholder="请选择是否用车" v-model="queryParam.useCar">
                <a-select-option :value="1">需要用车</a-select-option>
                <a-select-option :value="0">不需要用车</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6">
            <a-form-item label="车牌号">
              <a-input placeholder="请输入车牌号" v-model="queryParam.carNumber"></a-input>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增外勤计划</a-button>
      <a-button type="primary" @click="handleExportXls('考勤外出信息记录表')" icon="download">导出</a-button>
      <!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-button @click="batchDel" icon="delete" type="primary" ghost>批量删除</a-button>
      </a-dropdown>
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
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

        <template slot="action" slot-scope="text, record">
          <template v-if="record.bpmStatus === '1'">
            <a @click="handleEdit(record, 'edit-before')">编辑</a>
            <a-divider type="vertical"/>
            <a @click="startProcess(record)">提交流程</a>
            <a-divider type="vertical"/>
            <a @click="handleDelete(record.id)">删除</a>
          </template>
          <template v-if="record.bpmStatus === '2' || record.bpmStatus === '3'">
            <template v-if="record.bpmStatus === '3'">
              <a @click="handleEdit(record, 'edit-after')">编辑</a>
              <a-divider type="vertical"/>
            </template>
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
    <oa-sign-out-modal ref="modalForm" @ok="modalFormOk"/>
    <ext-act-process-inst-pic-modal ref="extActProcessInstPicModal"/>

  </a-card>
</template>

<script>
  import moment from 'moment'
  import { deleteAction, postAction } from '@api/manage'
  import OaSignOutModal from './modules/OaSignOutModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { filterDictText, initDictOptions } from '@/components/dict/JDictSelectUtil'
  import ExtActProcessInstPicModal from '@views/modules/extbpm/process/modules/ExtActProcessInstPicModal'

  export default {
    name: 'OaSignOutList',
    mixins: [JeecgListMixin],
    components: { ExtActProcessInstPicModal, OaSignOutModal },
    data() {
      return {
        description: '考勤外出信息记录表管理页面',
        flowCode: 'oa_sign_out_001',
        bpmStatusDictOptions: [],
        queryParamTime: [],
        // 表头
        columns: [
          {
            title: '外出地点',
            align: 'center',
            dataIndex: 'outPosition'
          },
          {
            title: '外出事由',
            align: 'center',
            dataIndex: 'outReason'
          },
          {
            title: '是否用车',
            align: 'center',
            dataIndex: 'useCar',
            customRender: (t) => t === 1 ? '需要用车' : '不需要用车'
          },
          {
            title: '车牌号',
            align: 'center',
            dataIndex: 'carNumber'
          },
          {
            title: '预期',
            children: [
              {
                title: '外出时间',
                align: 'center',
                dataIndex: 'expectedStartTime',
                customRender: (text) => moment(text).format('YY/MM/DD HH:mm:ss')
              },
              {
                title: '回来时间',
                align: 'center',
                dataIndex: 'expectedEndTime',
                customRender: (text) => moment(text).format('YY/MM/DD HH:mm:ss')
              },
            ]
          },
          {
            title: '实际',
            children: [
              {
                title: '外出时间',
                align: 'center',
                dataIndex: 'actualStartTime',
                customRender: (text) => moment(text).format('YY/MM/DD HH:mm:ss')
              },
              {
                title: '回来时间',
                align: 'center',
                dataIndex: 'actualEndTime',
                customRender: (text) => moment(text).format('YY/MM/DD HH:mm:ss')
              },
            ]
          },

          {
            title: '流程状态',
            align: 'center',
            dataIndex: 'bpmStatus',
            customRender: (text) => filterDictText(this.bpmStatusDictOptions, text)
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: '/sign/out/list',
          delete: '/sign/out/delete',
          deleteBatch: '/sign/out/deleteBatch',
          exportXlsUrl: 'sign/out/exportXls',
          importExcelUrl: 'sign/out/importExcel',
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
      startProcess(record) {
        this.$confirm({
          title: '提示',
          content: '确认提交流程吗?',
          onOk: () => {
            this.loading = true
            postAction(this.url.startProcess, {
              flowCode: this.flowCode,
              id: record.id,
              formUrl: 'modules/eoa/sign/modules/OaSignOutForm',
              formUrlMobile: 'modules/eoa/sign/modules/OaSignOutForm'
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
      handleChangeParamTime(value) {
        if (value && value.length > 0) {
          this.queryParamTime = value
          this.queryParam.expectedStartTimeBegin = moment(value[0]).format('YYYY-MM-DD')
          this.queryParam.expectedStartTimeEnd = moment(value[1]).format('YYYY-MM-DD')
        } else {
          this.queryParamTime = []
          this.queryParam.expectedStartTimeBegin = null
          this.queryParam.expectedStartTimeEnd = null
        }
      },
      handleEdit(record, action) {
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.open(record, action)
      },
      handleAdd() {
        this.$refs.modalForm.title = '新增'
        this.$refs.modalForm.open({}, 'add')
      },
      handlePreviewPic(record) {
        this.$refs.extActProcessInstPicModal.title = '流程图'
        this.$refs.extActProcessInstPicModal.preview(this.flowCode, record.id)
      },
      batchDel() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！')
        } else {
          let ids = this.selectionRows.filter(row => row.bpmStatus === '1').map(row => row.id).join(',')
          this.$confirm({
            title: '确认删除',
            content: <div><p>是否删除选中数据?</p><p>注：已提交流程的数据无法删除</p></div>,
            onOk: () => {
              deleteAction(this.url.deleteBatch, { ids: ids }).then((res) => {
                if (res.success) {
                  this.$message.success(res.message)
                  this.loadData()
                  this.onClearSelected()
                } else {
                  this.$message.warning(res.message)
                }
              })
            }
          })
        }
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>