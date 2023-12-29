<template>
  <!-- 线路分配 -->
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="线路">
              <GCIRouteSelect
                v-model="queryParam.routeIds"
                placeholder="请选择线路"
                mode="mode"
                :width="200"
                item="id"
              ></GCIRouteSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="9" :lg="7" :md="8" :sm="24">
            <a-form-item label="用户姓名">
              <j-select-multi-user placeholder="请选择用户" v-model="queryParam.userId" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" v-has="'1015:edit'" type="primary" icon="plus">新增</a-button>
      <!--      <a-button type="primary" icon="download" @click="handleExportXls('线路分配')">导出</a-button>-->
      <!--      <a-button type="primary" v-has="'1015:edit'" icon="import" @click="handleImportXlsMy('import')">导入</a-button>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" v-has="'1015:edit'" @click="batchDel"><a-icon type="delete" />删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down"/></a-button>
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

        <span slot="action" slot-scope="text, record" v-has="'1015:edit'">
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
    <!-- 导入 弹窗 -->
    <a-modal
      title="导入线路分配"
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
          <a-button>
            <a-icon
              type="
          upload"
            />
            上传文件
          </a-button>
        </a-upload>
        <span
          class="upload"
          style="color: rgb(61, 126, 234);float: right;
    display: block; margin-top: -71px;z-index: 999; height: 32px;line-height: 32px;padding: 24px;text-align: center;"
          @click="downloadTemplate()"
        >
          <a-icon type="file-excel" /> 模板下载
        </span>
      </a-spin>
    </a-modal>
    <!-- 导入 弹窗end -->
    <priUserRoute-modal ref="modalForm" @ok="modalFormOk"></priUserRoute-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import PriUserRouteModal from './modules/PriUserRouteModal'
import { filterMultiDictText } from '@/components/dict/JDictSelectUtil'
import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
import JSelectMultiUser from '@/components/jeecgbiz/JSelectMultiUser'
import GCIRouteSelect from '@/components/gci/GCIRouteSelect'
import { postAction } from '@/api/manage'

export default {
  name: 'PriUserRouteList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    JSelectMultiUser,
    JSearchSelectTag,
    PriUserRouteModal,
    JMultiSelectTag,
    GCIRouteSelect
  },
  data() {
    return {
      selectValue: '',
      description: '线路分配页面',
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
          title: '账户',
          align: 'center',
          dataIndex: 'accountName'
        },
        {
          title: '用户姓名',
          align: 'center',
          dataIndex: 'userName'
        },
        {
          title: '线路',
          align: 'center',
          dataIndex: 'routeName',
          sorter: true
        },
        {
          title: '调度',
          align: 'center',
          dataIndex: 'authorize',
          sorter: true,
          customRender: function(text) {
            if (text) {
              return text.indexOf('3') > -1 ? '启用' : '停用'
            } else {
              return '停用'
            }
          }
        },
        {
          title: '查询',
          align: 'center',
          dataIndex: 'authorize',
          sorter: true,
          customRender: function(text) {
            if (text) {
              return text.indexOf('0') > -1 ? '启用' : '停用'
            } else {
              return '停用'
            }
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
        list: '/routeuser/priUserRoute/list',
        delete: '/routeuser/priUserRoute/delete',
        deleteBatch: '/routeuser/priUserRoute/deleteBatch',
        exportXlsUrl: '/routeuser/priUserRoute/exportXls',
        importExcelUrl: 'routeuser/priUserRoute/importExcel'
      },
      width: 600,
      confirmLoading: false,
      importVisible: false,
      format: false,
      fileList: [],
      dictOptions: {}
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    handleCancel() {
      this.importVisible = false
    },
    // 导入
    handleImportXlsMy(title) {
      if (title == 'import') {
        this.importVisible = true
        this.fileList = []
      }
    },
    downloadTemplate() {
      window.location.href =
        'http://10.91.123.27:8190/gci/file/channel/getFile?appID=filecommu&fileID=2214e7a7-a304-41c3-b256-60de727fc232'
    },
    initDictConfig() {},
    beforeUpload(file) {
      console.log(file)
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

      return true
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
      postAction(this.url.importExcelUrl, formData)
        .then(res => {
          if (res.success) {
            this.format = false
            this.fileList = []
            this.$message.success(res.message)
            this.searchReset()
            this.importVisible = false
          } else {
            this.format = false
            this.$message.warning(res.message)
            this.importVisible = false
          }
        })
        .finally(() => {
          this.format = false
        })
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
