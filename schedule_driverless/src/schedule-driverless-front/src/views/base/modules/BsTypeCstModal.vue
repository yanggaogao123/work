<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item label="字典组" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['domain', validatorRules.domain]" placeholder="请输入字典组"></a-input>
        </a-form-item>
        <a-form-item label="字段" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['field', validatorRules.field]" placeholder="请输入字段"></a-input>
        </a-form-item>
        <a-form-item label="值" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['value', validatorRules.value]" placeholder="请输入值"></a-input>
        </a-form-item>
        <a-form-item label="简体中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['zhCn',validatorRules.zhCn]" placeholder="请输入简体中文"></a-input>
        </a-form-item>
        <a-form-item label="繁体中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['zhHk',validatorRules.zhHk]" placeholder="请输入繁体中文"></a-input>
        </a-form-item>
        <a-form-item label="水巴简体中文" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['zhCnWb',validatorRules.zhCnWb]" placeholder="请输入水巴简体中文"></a-input>
        </a-form-item>
        <a-form-item label="美式英语" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['enUs',validatorRules.enUs]" placeholder="请输入美式英语"></a-input>
        </a-form-item>
        <a-form-item label="葡萄牙语" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['ptPt',validatorRules.ptPt]" placeholder="请输入葡萄牙语"></a-input>
        </a-form-item>
        <a-form-item label="类型(0:系统,1:用户)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag type="list" v-decorator="['type']" :trigger-change="true" dictCode="cst_type" placeholder="请选择类型(0:系统,1:用户)"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import {validateRemark, isNumbersAndLetters, validateText} from '@/utils/validate'


  export default {
    name: "BsTypeCstModal",
    components: {
      JDictSelectTag,
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
          domain: {
            rules: [
              { required: true, message: '请输入字典组!'},
              { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: isNumbersAndLetters }
            ]
          },
          field: {
            rules: [
              { required: true, message: '请输入字段!'},
              { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: isNumbersAndLetters }
            ]
          },
          value: {
            rules: [
              { required: true, message: '请输入值!'},
              { max: 30, message: '长度最大为 30 ', trigger: 'blur' }
            ]
          },
          zhCn:{
            rules: [
              {required: true,message: '请输入简体中文'},
              { max: 30, message: '长度最大为 30 ', trigger: 'blur' }
            ]
          },
          zhHk:{
            rules: [
              { max: 30, message: '长度最大为 30 ', trigger: 'blur' }
            ]
          },
          zhCnWb:{
            rules: [
              { max: 30, message: '长度最大为 30 ', trigger: 'blur' }
            ]
          },
          enUs:{
            rules: [
              { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: isNumbersAndLetters }
            ]
          },
          ptPt:{
            rules: [
              { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: isNumbersAndLetters }
            ]
          },
        },
        url: {
          add: "/base/bsTypeCst/add",
          edit: "/base/bsTypeCst/edit",
        }
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'domain','enUs','field','ptPt','type','value','zhCn','zhHk','zhCnWb'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }

        })
      },
      handleCancel () {
        this.close()
      },
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'domain','enUs','field','ptPt','type','value','zhCn','zhHk','zhCnWb'))
      },


    }
  }
</script>