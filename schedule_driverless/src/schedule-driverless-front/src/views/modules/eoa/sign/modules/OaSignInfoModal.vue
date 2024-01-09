<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :okButtonProps="{ props: {disabled: disableSubmit} }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="打卡时间">
          <a-input disabled :value="model.signTime"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="打卡类型">
          <a-input disabled :value="model.signTypeText"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="打卡状态">
          <a-input disabled :value="model.signStatusText"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注">
          <a-textarea placeholder="请输入备注" v-decorator="['signRemarks', {}]"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import moment from 'moment/moment'
  import pick from 'lodash.pick'
  import { httpAction } from '@api/manage'
  import { getSignStatus, getSignType } from '../utils/util'


  export default {
    name: 'OaSignInfoModal',
    components: {},
    data() {
      return {
        title: '详情',
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
        form: this.$form.createForm(this),
        validatorRules: {},
        url: {
          add: '/sign/info/add',
          edit: '/sign/info/edit',
        },
        disableSubmit:false
      }
    },
    created() {
    },
    methods: {
      edit(record) {
        this.form.resetFields()
        //时间格式化
        record.signTime = record.signTime ? moment(record.signTime).format('YYYY-MM-DD HH:mm:ss') : null
        record.signTypeText = getSignType(record.signType)
        record.signStatusText = getSignStatus(record.signStatus)

        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'signRemarks'))
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleOk() {
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            this.confirmLoading = true
            let formData = Object.assign({ id: this.model.id }, values)
            httpAction(this.url.edit, formData, 'put').then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.$emit('ok')
              } else {
                this.$message.warning(res.message)
              }
            }).finally(() => {
              this.confirmLoading = false
              this.close()
            })
          }
        })
      },
      handleCancel() {
        this.close()
      }
    }
  }
</script>

<style lang="less" scoped>

</style>