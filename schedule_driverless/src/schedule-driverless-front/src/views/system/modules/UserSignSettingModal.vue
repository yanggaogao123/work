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

        <a-form-item label="用户名" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['username',validatorRules.username]" :disabled="true"></a-input>
        </a-form-item>
        <a-form-item label="限制登陆数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['maxOccurs',validatorRules.maxOccurs]" placeholder="请输入最大限制登录数 负数则为无限制"></a-input>
        </a-form-item>
        <a-form-item label="登录策略" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="['occursType',validatorRules.occursType]" placeholder="请选择策略">
            <a-select-option :value="0">后登录踢出先登录</a-select-option>
            <a-select-option :value="1">已登录禁止再登录</a-select-option>
          </a-select>
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { putAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import JDate from '@/components/jeecg/JDate'

  export default {
    name: 'SysSignSettingModal',
    components: {
      JDate
    },
    data () {
      const isNum = (rule, value, callback) => {
        const age= /^(-|\+)?\d+$/
        if (!age.test(value)) {
          callback(new Error('只能为数字'))
        }else{
          callback()
        }
      }
      const limitMax = (rule, value, callback) => {
        if(value > 9999){
          callback(new Error('限制次数最大为9999'))
        }else {
          callback()
        }
      }
      return {
        form: this.$form.createForm(this),
        title: '操作',
        width: 600,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        confirmLoading: false,
        url: {
          edit: '/sys/userSign/edit'
        },
        validatorRules: {
          username: { rules: [{ required: true, message: '请输入用户名!' }] },
          maxOccurs: { rules: [{ required: true, message: '请输入限制次数!' },{ validator: isNum, trigger: "blur"},{validator: limitMax,trigger: "blur"}] },
          occursType: { rules: [{ required: true, message: '请选择登录策略!' }] }

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
          this.form.setFieldsValue(pick(this.model, 'username', 'maxOccurs', 'occursType'))
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
            httpurl += this.url.edit;
            method = 'put';
            let formData = Object.assign(this.model, values);
            let params = {
              id: formData.id,
              num: formData.maxOccurs,
              type: formData.occursType
            }
            putAction(httpurl, params, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.$emit('ok');
              } else {
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
      popupCallback(row) {
        this.form.setFieldsValue(pick(row, 'username', 'maxOccurs', 'occursType'))
      }

    }
  }
</script>
