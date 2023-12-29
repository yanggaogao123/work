<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="6" :md="8" :sm="24">
            <a-form-item label="所属机构">
            <GCIOrgTreeSelect
              v-model="queryParam.sysOrgCode"
              placeholder="请选择所属机构"
              @changeOptions="handleChangeOrgan"
              :isGetOption="true"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="资格证">
              <a-input v-model="queryParam.qualification">
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="员工姓名">
              <a-input v-model="queryParam.employeeName">
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="14" :lg="14" :md="24" :sm="24">
            <a-button type="primary" @click="searchQuery2" icon="search" style="margin-bottom: 10px;">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
            <a-button type="primary" @click="batchVisible = true" icon="import" style="margin-left: 10px">导入</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
          selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>
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
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>

    <!--批量新增弹出框-->
    <a-modal
      title="导入"
      width="600px"
      :visible="batchVisible"
      :confirmLoading="uploadBatchLoading"
      okText="导入"
      @ok="handleUpload"
      @cancel="handleCancel">
      <a-form>
        <a-row>
          <a-form-item label="所属机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <GCIOrgTreeSelect
              v-model="sysOrgCode"
              placeholder="请选择所属机构"
              @changeOptions="handleSelectOrganId"
              :isGetOption="true"></GCIOrgTreeSelect>
          </a-form-item>
        </a-row>
        <a-row>
          <a-form-item label="选择文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <div style="display: flex;">
              <a-upload
                name="file"
                accept=".xlsx,.xls"
                :multiple="false"
                :file-list="fileList"
                :beforeUpload="beforeUpload"
                @change="handleChange"
                :customRequest="customRequest"
                style="width: 250px">
                <a-button>
                  <a-icon type="upload"/>
                  选择文件
                </a-button>
              </a-upload>
              <div @click="downloadTemplate()" style="margin-left: 10px;color: rgb(61, 126, 234);cursor: pointer">
                <a-icon type="file-text"/>
                模板下载
              </div>
            </div>
          </a-form-item>
        </a-row>
      </a-form>
    </a-modal>
  </a-card>
</template>

<!--员工电话管理-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { downloadFile, uploadAction } from '@/api/manage'
  import LineCallBusServiceModal from './modules/LineCallBusServiceModal'
  import ARow from 'ant-design-vue/es/grid/Row'
  import ACol from 'ant-design-vue/es/grid/Col'

  export default {
    name: 'EmployeeMobile',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      ACol,
      ARow,
      GCIOrgTreeSelect,
      LineCallBusServiceModal
    },
    data() {
      return {
        batchVisible: false,
        uploadBatchLoading: false,
        batchLoading: false,
        organId: undefined,
        sysOrgCode: undefined,
        fileList: [],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        queryParam: {
          orgCode: ''
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
            title: '所属机构',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '员工姓名',
            align: 'center',
            dataIndex: 'employeeName'
          },
          {
            title: '资格证',
            align: 'center',
            dataIndex: 'qualification'
          },
          {
            title: '手机',
            align: 'center',
            dataIndex: 'mobile'
          },
          {
            title: '最后更新账号',
            align: 'center',
            dataIndex: 'lastUpdatedUser'
          },
          {
            title: '最后更新时间',
            align: 'center',
            dataIndex: 'lastUpdatedTime'
          }
        ],
        url: {
          list: '/baseData/employeeMobile/list',
          importExcel: '/baseData/employeeMobile/importExcel',
          downloadTemplate: '/baseData/employeeMobile/downloadTemplate'
        }
      }
    },
    methods: {
      searchQuery2() {
        this.isorter = {}
        this.searchQuery()
      },
      searchReset() {
        this.queryParam = {}
        this.loadData(1)
      },
      handleChangeOrgan(orgData) {
        if (orgData === undefined) {
          this.queryParam.isLeaf = ''
          this.queryParam.organId = ''
        }
        if (orgData !== undefined) {
          this.queryParam.isLeaf = orgData.isLeaf
          this.queryParam.organId = orgData.id
        }
      },
      handleCancel() {
        this.batchVisible = false
        this.fileList = []
        this.organId = undefined
        this.sysOrgCode = undefined
      },
      handleUpload() {
        if (this.organId === undefined) {
          this.$message.warn('请先选择机构')
          return
        }
        if (this.fileList.length === 0) {
          this.$message.warn('请先选择Excel文件！')
          return
        }
        this.uploadBatchLoading = true
        const formData = new FormData()
        formData.append('organId', this.organId)
        formData.append('file', this.fileList[0].originFileObj)
        uploadAction(this.url.importExcel, formData).then((res) => {
          if (res.success) {
            this.uploadBatchLoading = false
            this.handleCancel()
            this.$message.success(res.message)
            this.searchQuery2()
          } else {
            this.searchQuery2()
            this.uploadBatchLoading = false
            this.$message.warn(res.message)
          }
        }).catch(() => {
          this.uploadBatchLoading = false
          this.$message.error('请求超时')
        })
      },
      handleChange(info) {
        info.file.status = 'done'
        let fileList = [...info.fileList]
        this.fileList = fileList.slice(-1)
      },
      handleSelectOrganId(orgData) {
        if (orgData === undefined) {
          this.organId = undefined
        }
        if (orgData !== undefined) {
          this.organId = orgData.id
        }
      },
      downloadTemplate() {
        let title = '员工电话管理导入模板.xlsx'
        downloadFile(this.url.downloadTemplate, title)
      },
      beforeUpload(file) {
        const name = file.name
        const suffix = name.substr(name.lastIndexOf('.'))
        if (suffix !== '.xls' && suffix !== '.xlsx') {
          this.$message.warn('请上传Excel文件！')
          return false
        }
        return true
      },
      customRequest(file) {
      }
    }
  }
</script>

<style lang="less" scoped>
</style>
