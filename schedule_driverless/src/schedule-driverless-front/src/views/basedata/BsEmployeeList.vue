<template>
  <!-- 员工管理页面 -->
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect
                v-model="queryParam.organId"
                placeholder="请选择所属机构"
                :selectType="selectType"
                :isGetOption="isGetOption"
              ></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="资格证号">
              <a-input placeholder="请输入资格证号" v-model="queryParam.qualification" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="员工姓名">
              <a-input placeholder="请输入员工姓名" v-model="queryParam.employeeName" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="记录状态">
              <j-dict-select-tag v-model="queryParam.recordStatus" placeholder="请选择记录状态" dictCode="employee_record_type" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
      <a-button type="primary" @click="searchReset" icon="reload">重置</a-button>
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('员工管理表')">导出</a-button>
      <a-button type="primary" icon="import" @click="handleImportXlsMy('import')">导入</a-button>
      <a-button type="primary" @click="release">发布</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="deleteBatch"><a-icon type="delete" />删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down"/></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
        >项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="employeeId"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>
    <!-- 导入 弹窗 -->
    <a-modal
      title="导入员工"
      :width="width"
      :visible="importVisible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      cancelText="关闭"
      @cancel="handleCancel"
      @ok="handleOkItem"
    >
      <a-spin :spinning="confirmLoading">
        <a-form-item label="">
          <a class="table-page-search-wrapper">所属机构：</a
          ><GCIOrgTreeSelect
            v-model="uploadOrganId"
            placeholder="请选择所属机构"
            :selectType="selectType"
            :isGetOption="isGetOption"
            style="width: 70%"
          ></GCIOrgTreeSelect>
        </a-form-item>
        <a-upload
          name="file"
          :multiple="false"
          :fileList="fileList"
          :remove="handleRemove"
          :beforeUpload="beforeUpload"
        >
          <a-button>
            <a-icon
              type="
          upload"
            />
            上传文件
          </a-button>
        </a-upload>
        <!-- <span
          class="upload"
          style="color: rgb(61, 126, 234);float: right;
            display: block; margin-top: -71px;z-index: 999; height: 32px;line-height: 32px;padding: 24px;text-align: center;"
          @click="downloadTemplate()"
        >
          <a-icon type="file-excel" /> 模板下载
        </span> -->
        <span
          class="upload"
          style="color: rgb(61, 126, 234);float: right;
            display: block; margin-top: -71px;z-index: 999; height: 32px;line-height: 32px;padding: 24px;text-align: center;"
        >
          <a-icon type="file-excel" />
          <a :href="path + 'fileDown/base/employeeModel.xls'" download="employeeModel.xls">模板下载</a>
        </span>
      </a-spin>
    </a-modal>
    <!-- 导入 弹窗end -->

    <!-- 完成确认 弹窗 -->
    <a-modal
      title="操作结果"
      :width="width"
      :visible="handleResultVisible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      cancelText="关闭"
      okText="继续添加"
      @cancel="handleCancel"
      @ok="handleResultOkItem"
    >
      <span>{{ handleResult }}</span>
    </a-modal>
    <bsEmployee-modal ref="modalForm" @ok="modalFormOk"></bsEmployee-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BsEmployeeModal from './modules/BsEmployeeModal'
import { getAction, postAction } from '@/api/manage'
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'

export default {
  name: 'BsEmployeeList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    BsEmployeeModal,
    GCIOrgTreeSelect
  },
  data() {
    return {
      description: '调度中心员工雇员表管理页面',
      isGetOption: true,
      selectType: 'id',
      handleResult: '',
      handleResultVisible: false,
      uploadOrganId: '',
      // 表头
      columns: [
        {
          title: '所属机构',
          align: 'center',
          dataIndex: 'organ_id',
          sorter: true,
          customRender: function(text, record) {
            return record.organId_dictText
          }
        },
        {
          title: '所属线路',
          align: 'center',
          dataIndex: 'routeId',
          customRender: function(text, record) {
            return record.routeName
          }
        },
        {
          title: '员工姓名',
          align: 'center',
          dataIndex: 'employeeName',
          sorter: true
        },
        {
          title: '资格证号',
          align: 'center',
          dataIndex: 'qualification',
          sorter: true
        },
        {
          title: '员工工号',
          align: 'center',
          dataIndex: 'employeeCode',
          sorter: true
        },
        {
          title: '身份证',
          align: 'center',
          dataIndex: 'identityCard',
          sorter: true
        },
        {
          title: '生日',
          align: 'center',
          dataIndex: 'birthDay',
          sorter: true
        },
        {
          title: '性别',
          align: 'center',
          dataIndex: 'sex_dictText',
          sorter: true
        },
        {
          title: '记录状态',
          align: 'center',
          dataIndex: 'recordStatus',
          sorter: true,
          customRender: function(text, record) {
            return record.recordStatus_dictText
          }
        },
        {
          title: '员工类别',
          align: 'center',
          dataIndex: 'employeeType_dictText',
          sorter: true
        },
        {
          title: '手机号',
          align: 'center',
          dataIndex: 'telephone',
          sorter: true
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          width: '160px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      treeData: [],
      url: {
        list: '/base/employee/getEmployeeList',
        delete: '/base/employee/delete',
        exportXlsUrl: '/base/employee/exportEmployee',
        importExcelUrl: '/base/employee/importEmployee',
        organTree: '/sys/sysDepart/queryTreeList',
        publishBatch: '/base/employee/publish'
      },
      width: 600,
      confirmLoading: false,
      importVisible: false,
      format: false,
      dictOptions: {},
      visibleItem: false,
      orgCode: null,
      fileList: [],
      uploading: false,
      path: ''
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    loadData(arg) {
      if (!this.url.list) {
        this.$message.error('请设置url.list属性!')
        return
      }
      // 加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1
      }
      const params = this.getQueryParams() // 查询条件
      this.loading = true
      postAction(this.url.list, params).then(res => {
        if (res.success) {
          this.dataSource = res.result.records
          this.ipagination.total = res.result.total
        }
        if (res.code === 510) {
          this.$message.warning(res.message)
        }
        this.loading = false
      }).finally(() => {
        this.loading = false;
      });
      this.onClearSelected();
    },
    handleCancel() {
      this.importVisible = false
      this.handleResultVisible = false
    },
    // 导入
    handleImportXlsMy(title) {
      if (title === 'import') {
        this.importVisible = true
        this.fileList = []
      }
    },
    downloadTemplate() {
      this.costomDownFile('/common/sys/getExcelModel?modelName=employeeModel.xls', 'employeeModel')
    },
    handleResultOkItem() {
      this.handleResultVisible = false
      this.importData()
    },
    importData(title) {
      this.importVisible = true
      this.fileList = []
    },
    handleDelete(record) {
      if (record.recordStatus && record.recordStatus === '2') {
        this.$message.warning(record.employeeName + '已经发布，不能删除！')
        return
      }
      let ids = [record.employeeId]
      postAction(this.url.delete, ids).then(res => {
        if (res.success) {
          this.$message.success(res.message)
          this.loadData()
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    deleteBatch(value) {
      if (this.selectedRowKeys.length <= 0) {
        this.$message.warning('请选择一条记录！')
        return
      }
      for (let r of this.selectionRows) {
        if (r.recordStatus && r.recordStatus === '2') {
          this.$message.warning(r.employeeName + '已经发布，不能删除！')
          return
        }
      }
      let ids = this.selectedRowKeys
      this.$confirm({
        title: '确认发布',
        content: '是否删除选中数据?',
        onOk: () => {
          postAction(this.url.delete, ids).then(res => {
            if (res.success) {
              this.$message.success(res.message)
              this.loadData()
            } else {
              this.$message.warning(res.message)
            }
          })
        }
      })
    },
    /* 发布 */
    release() {
      if (this.selectedRowKeys.length <= 0) {
        this.$message.warning('请选择一条记录！')
      } else {
        for (let r of this.selectionRows) {
          if (r.recordStatus && r.recordStatus === '2') {
            this.$message.warning(r.employeeName + '已经发布，请重新选择')
            return
          }
        }
        let ids = this.selectedRowKeys
        this.$confirm({
          title: '确认发布',
          content: '是否发布选中数据?',
          onOk: () => {
            postAction(this.url.publishBatch, ids).then(res => {
              if (res.success) {
                this.$message.success(res.message)
                this.loadData()
              } else {
                this.$message.warning(res.message)
              }
            })
          }
        })
      }
    },
    initDictConfig() {},
    initOrganInfo() {
      console.log(11)
      getAction(this.url.organTree)
        .then(res => {
          if (res.success) {
            this.treeData = res.result
          } else {
            this.$message.warning(res.message)
          }
        })
        .catch(e => {
          console.error(e)
        })
        .then(() => {
          this.confirmLoading = false
        })
    },
    showImport() {
      this.visibleItem = true
    },

    handleCancelItem() {
      this.visibleItem = false
    },
    handleOkItem() {
      let that = this
      if (!this.format) {
        this.$message.warning('请选择文件!')
        return
      }
      if (!this.uploadOrganId) {
        this.$message.warning('请选择所属机构!')
        return
      }

      const formData = new FormData()
      this.fileList.forEach(file => {
        formData.append('file', file)
      })
      formData.append('organId', this.uploadOrganId)
      postAction(this.url.importExcelUrl, formData)
        .then(res => {
          if (res.success) {
            that.fileList = []
          }
          that.handleResult = res.message
        })
        .finally(() => {
          this.format = false
          this.importVisible = false
          this.handleResultVisible = true
        })
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload(file) {
      const excel = file.type.indexOf('excel') > -1
      const sheet = file.type.indexOf('sheet') > -1
      if (!excel && !sheet) {
        this.$message.warning('请选择excel!')
        this.format = false
        return false
      }
      this.format = true
      // 只允许上传一个文件
      this.fileList = []
      this.fileList = [...this.fileList, file]

      return false
    }
  },
  created() {
    this.initOrganInfo()
    this.path = process.env.VUE_APP_PUBLIC_PATH
    console.log('二级路劲', this.path)
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';

.ant-popover-buttons button {
  margin-left: 6px;
}
</style>
