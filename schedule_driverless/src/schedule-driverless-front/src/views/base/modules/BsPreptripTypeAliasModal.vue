<template>
  <a-card :bordered="false">
    <j-modal
      title="预案类型属性"
      :width="width"
      :visible="visible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭">
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">

          <a-form-item label="预案类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-select placeholder="请选择预案类型"
              v-decorator="['taskTypeId', validatorRules.taskTypeId]"
              optionFilterProp="children"
              :filterOption="filterOption"
              allowClear
              @change="serviceTypeChange"
              show-search >
              <a-select-option v-for="(item) in dictOptions" :key="item.id" :value="item.value">
                {{ item.type }}
              </a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="预案别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-decorator="['taskTypeAlias', validatorRules.taskTypeAlias]" placeholder="请输入预案别名" allowClear></a-input>
          </a-form-item>
        </a-form>
      </a-spin>
    </j-modal>
    <!-- 完成确认 弹窗 -->
    <a-modal
      title="操作结果"
      :width="width"
      :visible="handleResultVisible"
      :confirmLoading="confirmLoading"
      switchFullscreen
      cancelText="关闭"
      okText="继续添加"
      @cancel="handleResultCancel"
      @ok="handleResultOkItem">
      <span>{{handleResult}}</span>
    </a-modal>
  </a-card>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import {validateRemark} from '@/utils/validate'


  export default {
    name: 'BsPreptripTypeAliasModal',
    data () {
      return {
        form: this.$form.createForm(this),
        title: '操作',
        width: 800,
        visible: false,
        model: {},
        handleResultVisible: false,
        handleResult: '',
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        confirmLoading: false,
        validatorRules: {
          taskTypeId: {
            rules: [
              { required: true, message: '请输入预案类别!' }
            ]
          },
          // taskTypeName: {
          //   rules: [
          //     { required: true, message: '请输入预案类型名称!' }
          //   ]
          // },
          taskTypeAlias: {
            rules: [
              { required: true, message: '请输入预案别名!' },
              { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: validateRemark }
            ]
          }
        },
        url: {
          add: '/base/bsPreptripTypeAlias/add',
          edit: '/base/bsPreptripTypeAlias/edit',
          getDictItem: '/base/bsPreptripTypeAlias/getTaskTypeList'
        },
        dictOptions: []
      }
    },
    created () {
    },
    methods: {
      
      add () {
        this.edit({});
      },
      edit (record) {
        this.initDictData();
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'taskTypeId', 'taskTypeName', 'taskTypeAlias', 'createTime', 'createBy', 'updateTime', 'sysOrgCode'))
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          console.log('values:', values)
          console.log('err:', err)
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if (!this.model.id) {
              httpurl += this.url.add;
              method = 'post';
            } else {
              httpurl += this.url.edit;
              method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log('表单提交数据', formData)
            httpAction(httpurl, formData, method).then((res) => {
              this.handleResult = res.message
              this.handleResultVisible = true
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
        })
      },
      handleResultOkItem() {
        this.initDictData()
        this.handleResultVisible = false
      },
      handleResultCancel() {
        this.handleResultVisible = false
        this.$emit('ok')
        this.close()
      },
      handleCancel () {
        this.$emit('ok')
        this.close()
      },
      popupCallback(row) {
        this.form.setFieldsValue(pick(row, 'taskTypeId', 'taskTypeName', 'taskTypeAlias', 'createTime', 'createBy', 'updateTime', 'sysOrgCode'))
      },
      initDictData() {
        this.dictOptions = [];
        getAction(this.url.getDictItem, {  }).then((res) => {
          if (res.success) {
            this.dictOptions = res.result;
          }
        })
      },
      serviceTypeChange(e) {
        this.model.taskTypeId = e;
        console.log('this.model.taskTypeId', this.model.taskTypeId)
        for (let t of this.dictOptions) {
          if (e === t.value) {
            this.model.taskTypeName = t.type;
            this.form.setFieldsValue({ taskTypeAlias: t.type });
            this.model.taskTypeSort = t.sort;
            break;
          }
        }
        console.log('this.model', this.model)
      },
      filterOption(input, option) {
        return option.componentOptions.children[0].text.indexOf(input) >= 0
      }

    }
  }
</script>
