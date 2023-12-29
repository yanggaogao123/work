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

        <a-form-item label="应用名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['name',validatorRules.name]" placeholder="请输入应用名称"></a-input>
        </a-form-item>
        <a-form-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['address',validatorRules.address]" placeholder="请输入地址"></a-input>
        </a-form-item>
        <a-form-item label="协议" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="['protocol',validatorRules.protocol]" placeholder="请选择协议">
            <a-select-option value="http">http</a-select-option>
            <a-select-option value="https">https</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="应用介绍" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['remark',validatorRules.remark]" placeholder="请输入应用介绍"></a-input>
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import JDate from '@/components/jeecg/JDate'

  export default {
    name: 'SysAppModal',
    components: {
      JDate
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title: '操作',
        width: 800,
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
          add: '/sys/sysApp/add',
          edit: '/sys/sysApp/edit'
        },
        validatorRules: {
            name: { rules: [{ required: true, message: '请输入应用名称!' }] },
            address: { rules: [{ required: true, message: '请输入应用地址!' }, { validator: this.addressValid }] },
            protocol: { rules: [{ required: true, message: '请选择协议!' }] },
            remark: { rules: [{ required: true, message: '请输入备注信息!' }] }

          }
      }

    },
    created () {
    },
    methods: {
      addressValid (rule, value, callback) {
        if (!value) {
          return callback()
        } else {
          const ipReg = /^(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5]):([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/
          const hostReg = /^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/

          if (!ipReg.test(value) && !hostReg.test(value)) {
            callback(new Error('请输入【ip:port】或 【ip】或【域名】'))
            return
          }
          callback()
        }
      },
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'name', 'address', 'protocol', 'remark'))
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
        this.form.setFieldsValue(pick(row, 'name', 'address', 'protocol', 'remark'))
      },

    }
  }
</script>
