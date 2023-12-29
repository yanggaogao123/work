<template>
  <!-- SIM卡管理页面 -->
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect
                v-model="queryParam.orgCode"
                :type="'1'"
                placeholder="请选择所属机构"
              ></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="ICCID">
              <a-input placeholder="请输入ICCID号码" v-model="queryParam.simCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="IMSI识别码">
              <a-input placeholder="请输入IMSI识别码" v-model="queryParam.simChipCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="手机号码">
              <a-input placeholder="请输入手机号码" v-model="queryParam.phoneNumber"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="SIM卡状态">
              <a-select allowClear :value="false" placeholder="请选择SIM卡状态" v-model="queryParam.simStatus">
                <a-select-option :value="0">正常</a-select-option>
                <a-select-option :value="2">报废</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="是否启用">
              <a-select allowClear :value="false" placeholder="请选择是否启用" v-model="queryParam.isActive">
                <a-select-option :value="0">是</a-select-option>
                <a-select-option :value="1">否</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="发布状态">
              <a-select allowClear :value="false" placeholder="请选择发布状态" v-model="queryParam.publishStatus">
                <a-select-option :value="0">起草</a-select-option>
                <!-- <a-select-option :value="1">待审核</a-select-option> -->
                <!-- <a-select-option :value="2">审核通过</a-select-option> -->
                <!-- <a-select-option :value="3">审核不通过</a-select-option> -->
                <a-select-option :value="4">已发布</a-select-option>
              </a-select>
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
      <a-button type="primary" icon="download" @click="handleExportXls('SIM卡管理')">导出</a-button>
      <!--<a-upload name="file" :showUploadList="false" :fileList="fileList" :multiple="false"  :beforeUpload="beforeUpload" @change="handleOkItem">
        <a-button  type="primary" icon="import">导入</a-button>
      </a-upload>-->
      <!-- <a-button type="primary" icon="import" @click="handleImportXlsMy('import')">导入</a-button> -->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down" />
        </a-button>
      </a-dropdown>
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

    <bsSim-modal ref="modalForm" @ok="modalFormOk"></bsSim-modal>

    <!-- 导入弹窗  -->
    <a-modal
      title="导入SIM"
      :width="width"
      :visible="importVisible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      cancelText="关闭"
      @cancel="handleCancel"
      @ok="handleOkItem"
    >
      <a-spin :spinning="confirmLoading">
        <a-upload
          name="file"
          :multiple="false"
          :fileList="fileList"
          :remove="handleRemove"
          :beforeUpload="beforeUpload"
        >
          <a-button><a-icon type="upload" />上传文件</a-button>
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
          <a :href="path + 'fileDown/base/simModel.xls'" download="simModel.xls">模板下载</a>
        </span>
      </a-spin>
    </a-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BsSimModal from './modules/BsSimModal'
import { postAction } from '@/api/manage'
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'

export default {
  name: 'BsSimList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    BsSimModal,
    GCIOrgTreeSelect
  },
  data() {
    return {
      orgTree: [],
      description: '公交sim管理页面',
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
          dataIndex: 'orgCode_dictText',
          ellipsis: true,
          sorter: true
        },
        {
          title: 'ICCID',
          align: 'center',
          dataIndex: 'simCode',
          sorter: true
        },
        {
          title: 'IMSI识别码', // SIM卡晶片号码
          align: 'center',
          dataIndex: 'simChipCode',
          sorter: true
        },
        {
          title: '手机号码', // SIM卡编码
          align: 'center',
          dataIndex: 'phoneNumber',
          sorter: true
        },
        {
          title: 'SIM卡状态',
          align: 'center',
          dataIndex: 'simStatus_dictText',
          sorter: true
        },
        {
          title: '是否启用',
          align: 'center',
          dataIndex: 'isActive_dictText',
          sorter: true
        },
        {
          title: '发布状态',
          align: 'center',
          dataIndex: 'publishStatus_dictText',
          sorter: true
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remark',
          ellipsis: true,
          sorter: true
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
        list: '/base/bsSim/list',
        delete: '/base/bsSim/delete',
        deleteBatch: '/base/bsSim/deleteBatch',
        exportXlsUrl: '/base/bsSim/exportXls',
        importExcelUrl: '/base/bsSim/importExcel'
      },
      confirmLoading: false,
      width: 600,
      importVisible: false, // 导入
      format: false,
      fileList: [],
      uploading: false,
      orgCode: '',
      visibleItem: false,
      dictOptions: {},
      path: ''
    }
  },
  created() {
    this.path = process.env.VUE_APP_PUBLIC_PATH
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    showImport() {
      this.visibleItem = true
    },
    beforeUpload(file) {
      console.log(file)
      const excel = file.type.indexOf('excel') > -1
      const sheet = file.type.indexOf('sheet') > -1
      if (!excel && !sheet) {
        this.$message.warning('请选择excel!')
        return false
      }
      this.format = true
      // 只允许上传一个文件
      this.fileList = []
      this.fileList = [...this.fileList, file]

      return false
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    handleCancelItem() {
      this.visibleItem = false
    },
    handleCancel() {
      this.importVisible = false
    },
    handleImportXlsMy(title) {
      if (title === 'import') {
        this.importVisible = true
        this.fileList = []
      }
    },
    downloadTemplate() {
      window.location.href =
        'http://10.91.123.27:8190/gci/file/channel/getFile?appID=filecommu&fileID=1218bc7b-c76e-47f6-8a78-0b496c694ec0'
    },
    handleOkItem() {
      if (!this.format) {
        this.$message.warning('请选择文件!')
        return
      }
      const { fileList } = this
      const formData = new FormData()
      fileList.forEach(file => {
        formData.append('file', file)
      })
      postAction(this.url.importExcelUrl, formData).then(res => {
        this.uploading = false
        if (res.success) {
          this.format = false
          this.fileList = []
          this.$message.success(res.message)
          this.searchReset()
          this.visibleItem = false
          this.importVisible = false
        } else {
          this.format = false
          this.$message.warning(res.message)
          this.importVisible = false
        }
      })
    },
    searchQuery() {
      if (this.queryParam.orgCode) {
        this.loadData(1)
      } else {
        this.$message.error('请选择机构后再查询')
      }
    },
    searchReset() {
      this.queryParam = { orgCode: this.queryParam.orgCode }
      this.loadData(1)
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
