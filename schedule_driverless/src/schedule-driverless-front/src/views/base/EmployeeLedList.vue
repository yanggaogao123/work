<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect  v-model="queryParam.organId" placeholder="请选择所属机构" :selectType="selectType" :isGetOption="isGetOption" ></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="资格证号">
              <a-input
                placeholder="请输入资格证号"
                v-model="queryParam.qualification"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="员工姓名">
              <a-input
                placeholder="请输入员工姓名"
                v-model="queryParam.employeeName"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="7" :md="8" :sm="24">
            <a-form-item label="LED显示控制">
              <a-select placeholder="请选择LED显示控制" v-model="queryParam.ledShow" allow-clear>
                <!-- <a-select-option value="-1">全部</a-select-option> -->
                <a-select-option value="1">不显示</a-select-option>
                <a-select-option value="0">显示</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button v-has="'2876:edit'" type="primary" @click="edit" icon="edit" style="margin-left: 8px">编辑</a-button>
              <a-button type="primary" @click="importData" icon="upload" style="margin-left: 8px">导入</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 操作按钮区域 -->
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="employeeId"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys,onChange: onSelectChange}"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>
    <!-- 导入 弹窗 -->
    <a-modal
      title="批量修改LED控制"
      :width="width"
      :visible="importVisible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      cancelText="取消"
      okText="导入"
      @cancel="handleCancel"
      @ok="handleOkItem">
          <a-spin :spinning="confirmLoading">
            <a-form-item label="" >
              <a class="table-page-search-wrapper">所属机构：</a><GCIOrgTreeSelect  v-model="uploadOrgCode" placeholder="请选择所属机构" style="width: 70%"></GCIOrgTreeSelect>
            </a-form-item>
            <a-upload
              name="file"
              :multiple="false"
              :fileList="fileList"
              :remove="handleRemove"
              :beforeUpload="beforeUpload">
              <a-button>
                <a-icon
                  type="upload" /> 上传文件 </a-button>
            </a-upload>
            <span
              class="upload"
              style="color: rgb(61, 126, 234);float: right;
                  display: block; margin-top: -71px;z-index: 999; height: 32px;line-height: 32px;padding: 24px;text-align: center;"
              @click="exportData">
            <a-icon type="file-excel" />下载员工信息</span>
          </a-spin>
    </a-modal>

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
      @ok="handleResultOkItem">
      <span>{{handleResult}}</span>
    </a-modal>
    <EmployeeLedModal ref="employeeLedModal" @ok="modalFormOk" v-show="showModal"></EmployeeLedModal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
  import GCIRouteSelect from '@/components/gci/GCIRouteSelect'
  import { postAction } from '@/api/manage';
  import EmployeeLedModal from './modules/EmployeeLedModal'
  import GCIOrgTreeSelect from '../../components/gci/GCIOrgTreeSelect';

  export default {
    name: 'EmployeeLedList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect,
      JSearchSelectTag,
      JMultiSelectTag,
      GCIRouteSelect,
      EmployeeLedModal
    },
    data () {
      return {
        isGetOption: true,
        selectType: 'id',
        showModal: false,
        selectValue: '',
        description: '员工LED显示控制',
        selectionRows: [],
        handleResult: '',
        handleResultVisible: false,
        uploadOrgCode: '',
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '所属机构',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '所属线路',
            align: 'center',
            dataIndex: 'routeName'
          },
          {
            title: '员工姓名',
            align: 'center',
            dataIndex: 'employeeName'
          },
          {
            title: '资格证号',
            align: 'center',
            dataIndex: 'qualification'
          },
          {
            title: '员工类别',
            align: 'center',
            dataIndex: 'employeeType',
            customRender: function (text) {
              return text === '0' ? '司机' : (text === '1' ? '调度员' : '乘务员');
            }
          },
          {
            title: '电话号码',
            align: 'center',
            dataIndex: 'mobile'
          },
          {
            title: 'LED显示控制',
            align: 'center',
            dataIndex: 'ledShow',
            customRender: function (text) {
              return text === '0' ? '显示' : '不显示';
            }
          }
        ],
        url: {
          list: '/base/employee/getEmployeeLedList',
          importExcelUrl: '/base/employee/importExcel',
          exportXlsUrl: '/base/employee/exportExcel'
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
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
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
          this.ipagination.current = 1;
        }
        const params = this.getQueryParams();// 查询条件
        this.loading = true;
        postAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      edit() {
        if (!this.selectionRows || this.selectionRows.length === 0) {
          this.$message.warning('请选择需要编辑的数据')
          return
        }
        this.showModal = true
        this.$refs.employeeLedModal.edit(this.selectedRowKeys, this.selectionRows)
      },
      handleRemove(file) {
            const index = this.fileList.indexOf(file)
            const newFileList = this.fileList.slice()
            newFileList.splice(index, 1)
            this.fileList = newFileList
      },
      handleCancel() {
            this.importVisible = false
            this.handleResultVisible = false
      },
        // 导入
      importData(title) {
        this.importVisible = true;
        this.fileList = [];
      },
      exportData() {
        this.queryParam = {
          orgCode: this.uploadOrgCode
        }
        this.handleExportXls('员工显示控制')
      },
      handleResultOkItem() {
        this.handleResultVisible = false
        this.importData()
      },
      initDictConfig() {
      },
        beforeUpload(file) {
            console.log(file);
            const excel = file.type.indexOf('excel') > -1;
            const sheet = file.type.indexOf('sheet') > -1;
            if (!excel && !sheet) {
                this.$message.warning('请选择excel!')
                this.format = false;
                return false
            }
            this.format = true;
            // 只允许上传一个文件
            this.fileList = []
            this.fileList = [...this.fileList, file]

            return true;
        },
        handleOkItem() {
            if (!this.format) {
                this.$message.warning('请选择文件!');
                return;
            }
              const {
                  fileList
              } = this
              const formData = new FormData()
              fileList.forEach((file) => {
                  formData.append('file', file)
              });
              postAction(this.url.importExcelUrl, formData).then((res) => {
                  if (res.success) {
                      this.format = false;
                      this.fileList = []
                      this.handleResult = res.message
                      this.importVisible = false;
                      this.handleResultVisible = true;
                  } else {
                    this.format = false;
                    this.handleResult = res.message
                    this.importVisible = false;
                    this.handleResultVisible = true;
                  }
              }).finally(() => {
                  this.format = false;
              })
        }
    }
  }
</script>
<style scoped>
  @import '../../assets/less/common.less';
</style>
