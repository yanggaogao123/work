<template>
  <div>
    <j-modal
      :title="title"
      :width="width"
      :visible="visible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      @ok="handleOk"
      @cancel="handleCancel"
      :okButtonProps="{ props: { disabled: disableSubmit } }"
      cancelText="关闭"
    >
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item label="终端芯片编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input
              v-decorator="['obuChipCode', validatorRules.obuChipCode]"
              placeholder="请输入终端芯片编号"
            ></a-input>
          </a-form-item>
          <a-form-item label="终端IP" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-decorator="['wlanIp', validatorRules.wlanIp]" placeholder="请输入终端IP"></a-input>
          </a-form-item>
          <a-form-item label="所属机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <GCIOrgTreeSelect
              v-decorator="['organId', validatorRules.organId]"
              placeholder="请选择机构"
              :select-type="'id'"
              :isGetOption="true"
            >
            </GCIOrgTreeSelect>
          </a-form-item>
          <a-form-item label="ICCID" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input :disabled="true" v-decorator="['simCode']"
              ><a-icon slot="addonAfter" type="search" style="cursor: pointer" @click="handleSimOpen"
            /></a-input>
          </a-form-item>
          <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-select
              placeholder="请选择"
              v-decorator="['obuStatus', validatorRules.obuStatus]"
              allowClear
              show-search
              :filter-option="addFilterOption"
            >
              <a-select-option v-for="(item, key) in statusOptions" :key="key" :value="item.value">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="是否启用" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-select
              placeholder="请选择"
              v-decorator="['isActive', validatorRules.isActive]"
              allowClear
              show-search
              :filter-option="addFilterOption"
            >
              <a-select-option v-for="(item, key) in enableOptions" :key="key" :value="item.value">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-textarea v-decorator="['remark', validatorRules.remark]"></a-textarea>
          </a-form-item>
        </a-form>
      </a-spin>
    </j-modal>
    <!--新增编辑弹窗-->
    <a-modal
      title="ICCID选择"
      :width="700"
      :height="500"
      :visible="simVisible"
      @ok="handleSimOk"
      @cancel="handleSimCancel"
      centered
    >
      <!--表单搜索区域-->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="loadSimData">
          <a-row :gutter="24">
            <a-col :xl="14" :lg="14" :md="14" :sm="24">
              <a-form-item label="ICCID">
                <a-input placeholder="请输入" v-model="simCode"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="10" :lg="10" :md="10" :sm="24">
              <a-button type="primary" @click="loadSimData" icon="search">查询</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="simId"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{ type: 'radio', selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
      </a-table>
    </a-modal>
    <!--导入弹窗-->
    <a-modal
      title="导入"
      width="600px"
      :visible="batchVisible"
      :confirmLoading="uploadBatchLoading"
      okText="导入"
      @ok="handleUpload"
      @cancel="handleImportCancel"
    >
      <a-form>
        <a-row>
          <a-form-item label="所属机构" :labelCol="{ span: 5 }" :wrapperCol="{ span: 16 }">
            <GCIOrgTreeSelect
              v-model="sysOrgCode"
              placeholder="请选择所属机构"
              @changeOptions="handleSelectOrganId"
              :isGetOption="true"
            ></GCIOrgTreeSelect>
          </a-form-item>
        </a-row>
        <a-row>
          <a-form-item label="选择文件" :labelCol="{ span: 5 }" :wrapperCol="{ span: 16 }">
            <div style="display: flex;">
              <a-upload
                name="file"
                accept=".xlsx,.xls"
                :multiple="false"
                :file-list="fileList"
                :beforeUpload="beforeUpload"
                :customRequest="customRequest"
                @change="handleChange"
                style="width: 250px"
              >
                <a-button>
                  <a-icon type="upload" />
                  选择文件
                </a-button>
              </a-upload>
              <!-- <div @click="downloadTemplate()" style="margin-left: 10px;color: rgb(61, 126, 234);cursor: pointer">
                <a-icon type="file-text"/>
                模板下载
              </div> -->
              <div style="margin-left: 10px;color: rgb(61, 126, 234);cursor: pointer">
                <a-icon type="file-text" />
                <a :href="path + '/fileDown/base/equipmentModel.xls'" download="equipmentModel.xls">模板下载</a>
              </div>
            </div>
          </a-form-item>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { httpAction, getAction, downFile, uploadAction } from '@/api/manage'
import JDate from '@/components/jeecg/JDate'
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
import pick from 'lodash.pick'
import { validateRemark, validateIP, validateText } from '@/utils/validate'

export default {
  name: 'BsEquipmentModal',
  components: {
    JDate,
    GCIOrgTreeSelect
  },
  data() {
    return {
      uploadBatchLoading: false,
      fileList: [],
      importOrganId: undefined,
      batchVisible: false,
      simId: '',
      simVisible: false,
      simCode: '',
      confirmLoading: false,
      disableSubmit: true,
      sysOrgCode: undefined,
      statusOptions: [
        {
          label: '请选择',
          value: ''
        },
        {
          label: '正常',
          value: '0'
        },
        {
          label: '报废',
          value: '2'
        }
      ],
      enableOptions: [
        {
          label: '请选择',
          value: ''
        },
        {
          label: '是',
          value: '0'
        },
        {
          label: '否',
          value: '1'
        }
      ],
      form: this.$form.createForm(this),
      title: '操作',
      width: 700,
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
      },
      validatorRules: {
        obuChipCode: {
          rules: [
            { required: true, message: '请输入终端芯片编号' },
            { max: 20, message: '长度最大为20位' },
            { validator: validateText }
          ]
        },
        wlanIp: {
          rules: [{ max: 15, message: '长度最大为15位' }, { validator: validateIP }]
        },
        organId: {
          rules: [
            {
              required: true,
              message: '请选择机构'
            }
          ]
        },
        obuStatus: {
          rules: [
            {
              required: true,
              message: '请选择状态'
            }
          ]
        },
        isActive: {
          rules: [
            {
              required: true,
              message: '请选择是否启用'
            }
          ]
        },
        remark: {
          rules: [{ max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: validateRemark }]
        }
      },
      /* table选中keys */
      selectedRowKeys: [],
      /* table选中records */
      selectionRows: [],
      loading: false,
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
          title: 'ICCID',
          align: 'center',
          dataIndex: 'simCode'
        }
      ],
      ipagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '30'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        total: 0
      },
      dataSource: [],
      url: {
        add: '/basedata/bsEquipment/saveOrUpdate',
        search: '/basedata/bsEquipment/findByCode',
        importExcel: '/basedata/bsEquipment/importExcel'
      },
      path: '',
    }
  },
  created() {
    this.path = process.env.VUE_APP_PUBLIC_PATH
  },
  methods: {
    edit: function(record) {
      this.simId = ''
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(
            this.model,
            'equipmentId',
            'obuChipCode',
            'organId',
            'wlanIp',
            'simCode',
            'obuStatus',
            'isActive',
            'remark'
          )
        )
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let httpurl = this.url.add
          let method = 'post'
          let formData = Object.assign(this.model, values)
          formData.simId = this.simId
          console.log('表单提交数据', formData)
          httpAction(httpurl, formData, method)
            .then(res => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.confirmLoading = false
                that.close()
              } else {
                if (res.code === 500) {
                  that.confirmLoading = false
                  that.disableSubmit = false
                  that.$message.warning(res.message)
                } else {
                  that.confirmLoading = false
                  that.$message.warning(res.message)
                }
              }
            })
            .catch(() => {
              that.confirmLoading = false
            })
        }
      })
    },
    handleCancel() {
      this.close()
    },
    handleSimCancel() {
      this.simVisible = false
    },
    handleSimOk() {
      if (this.selectionRows.length === 0) {
        this.$message.warn('请选择SIM卡')
        return
      }
      this.simId = this.selectionRows[0].simId
      this.form.setFieldsValue({ simCode: this.selectionRows[0].simCode })
      this.simVisible = false
    },
    handleSimOpen() {

      //判断是否选择机构
      if(!this.form.getFieldValue('organId') || this.form.getFieldValue('organId') == undefined){
        this.$message.warning('请先选择所属机构')
        return
      }

      this.dataSource = []
      this.simCode = ''
      this.simVisible = true
      this.onClearSelected()
    },
    onClearSelected() {
      this.selectedRowKeys = []
      this.selectionRows = []
    },
    addFilterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows
    },
    handleTableChange(pagination, filters, sorter) {
      this.ipagination = pagination
      this.loadSimData()
    },
    loadSimData(arg) {
      // 加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1
      }
      let params = { simCode: this.simCode,organId: this.form.getFieldValue('organId') }
      params.pageNo = this.ipagination.current
      params.pageSize = this.ipagination.pageSize
      // 查询条件
      getAction(this.url.search, params).then(res => {
        if (res.success) {
          if(res.result && res.result.records){
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }else {
            this.dataSource = []
            this.ipagination.total = 0
          }

        }
      })
    },
    openImportModal() {
      this.batchVisible = true
      this.sysOrgCode = undefined
    },
    handleImportCancel() {
      this.batchVisible = false
      this.fileList = []
      this.importOrganId = undefined
      this.sysOrgCode = undefined
    },
    handleUpload() {
      if (this.importOrganId === undefined) {
        this.$message.warn('请先选择机构')
        return
      }
      if (this.fileList.length === 0) {
        this.$message.warn('请先选择Excel文件！')
        return
      }
      this.uploadBatchLoading = true
      const formData = new FormData()
      formData.append('organId', this.importOrganId)
      formData.append('file', this.fileList[0].originFileObj)
      uploadAction(this.url.importExcel, formData)
        .then(res => {
          if (res.success) {
            this.uploadBatchLoading = false
            this.handleImportCancel()
            this.$message.success(res.message)
            this.$emit('ok')
          } else {
            this.uploadBatchLoading = false
            this.$message.warn(res.message)
          }
        })
        .catch(() => {
          this.uploadBatchLoading = false
          this.$message.error('请求超时')
        })
    },
    downloadTemplate() {
      this.downFile('/common/sys/getExcelModel?modelName=equipmentModel.xls', 'equipmentModel')
    },
    handleSelectOrganId(orgData) {
      if (orgData === undefined) {
        this.importOrganId = undefined
      }
      if (orgData !== undefined) {
        this.importOrganId = orgData.id
      }
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
    handleChange(info) {
      info.file.status = 'done'
      let fileList = [...info.fileList]
      this.fileList = fileList.slice(-1)
    },
    downFile(url, fileName, param) {
      downFile(url, param).then(data => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data], { type: 'application/vnd.ms-excel' }), fileName + '.xls')
        } else {
          let url = window.URL.createObjectURL(new Blob([data], { type: 'application/vnd.ms-excel' }))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', fileName + '.xls')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) // 下载完成移除元素
          window.URL.revokeObjectURL(url) // 释放掉blob对象
        }
      })
    },
    // 防止选择文件后直接请求
    customRequest(file) {}
  }
}
</script>
