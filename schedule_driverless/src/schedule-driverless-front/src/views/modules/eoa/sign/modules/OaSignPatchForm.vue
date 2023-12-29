<template>
  <a-spin :spinning="loading" :class="{pd:!modalMode}">
    <a-form :form="form">
      <a-row :gutter="10">
        <a-col :md="12" :sm="24">
          <a-form-item label="补卡类型 " v-bind="formItemProps">
            <a-input disabled v-decorator="['signType', {}]"/>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label="异常状态 " v-bind="formItemProps">
            <a-input disabled v-decorator="['abnormalStatus', {}]"/>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="10">
        <a-col :md="12" :sm="24">
          <a-form-item label="异常时间" v-bind="formItemProps">
            <a-input disabled v-decorator="['abnormalTime', {}]"/>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label="正常时间" v-bind="formItemProps">
            <a-input disabled v-decorator="['patchTime', {}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item
        label="补卡原因"
        :labelCol="{sm : { span: 24 }, md: { span: 3 } }"
        :wrapperCol="{ sm: { span: 24 }, md: { span: 24-3 } }"
      >
        <a-textarea placeholder="请输入补卡原因" :rows="5" v-decorator="['patchReason', {}]"/>
      </a-form-item>

    </a-form>
  </a-spin>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from 'moment'
  import { getAction, httpAction } from '@api/manage'
  import { getSignStatus, getSignType } from '../utils/util'

  export default {
    name: 'OaSignPatchForm',
    props: {
      formData: {
        type: Object,
        default: () => ({})
      },
      // 是否运行在 modal 模式，modal模式下不进行查询操作
      modalMode: {
        type: Boolean,
        default: false
      },
      loading: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        title: '操作',
        model: {},
        action: 'add',
        form: this.$form.createForm(this),
        formItemProps: {
          labelCol: {
            sm: { span: 24 },
            md: { span: 6 } },
          wrapperCol: {
            sm: { span: 24 },
            md: { span: 24 - 6 } },
        },
        validatorRules: {
        },
        url: {
          queryById: '/sign/patch/queryById',
          add: '/sign/out/add',
          edit: '/sign/out/edit',
        },
      }
    },
    computed: {
      isAdd() {
        return this.action === 'add'
      },
      isEdit() {
        return this.action.indexOf('edit') !== -1
      },
      isEditBefore() {
        return this.action === 'edit-before'
      },
      isEditAfter() {
        return this.action === 'edit-after'
      },
      isEditDetail() {
        return this.action === 'edit-detail'
      },
      timeProps() {
        return {
          showTime: true,
          format: 'YYYY-MM-DD HH:mm:ss',
          style: { 'width': '100%' }
        }
      },
      expectedTimeProps() {
        return Object.assign({}, this.timeProps, {
          disabled: this.isEditAfter
        })
      },
    },
    watch: {
      isEditAfter(val) {
        this.validatorRules.carNumber.rules[0].required = val === true
      }
    },
    created() {
      if (!this.modalMode) {
        this.queryById()
      }
    },
    methods: {
      queryById() {
        this.$emit('update:loading', true)
        getAction(this.url.queryById, {
          id: this.formData.dataId
        }).then(res => {
          if (res.success) {
            this.setValue(res.result, 'edit-detail')
          } else throw res.message
        }).catch(e => {
          this.$warning({ title: '查询外勤详细信息失败', content: (e || {}).message || e })
        }).finally(() => {
          this.$emit('update:loading', false)
        })
      },
      setValue(record, action) {
        this.action = action
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'abnormalTime', 'patchTime', 'patchReason'))
        })
        this.form.setFieldsValue({signType:getSignType(record.patchType),abnormalStatus:getSignStatus(record.patchStatus)})
      },
    }
  }
</script>

<style lang="less" scoped>
  .pd {
    padding: 20px;
  }
</style>