<template>
  <a-spin :spinning="loading" :class="{pd:!modalMode}">
    <a-form :form="form">
      <a-row :gutter="10">
        <a-col :md="12" :sm="24">
          <a-form-item label="外出地点" v-bind="formItemProps">
            <a-input :disabled="isEditAfter" :read-only="isEditDetail" placeholder="请输入外出地点" v-decorator="['outPosition', validatorRules.outPosition]"/>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label="外出事由" v-bind="formItemProps">
            <a-input :disabled="isEditAfter" :read-only="isEditDetail" placeholder="请输入外出事由" v-decorator="['outReason', validatorRules.outReason]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="10">
        <a-col :md="12" :sm="24">
          <a-form-item label="是否用车" v-bind="formItemProps">
            <a-select :disabled="isEditAfter" :open="isEditDetail?false:null" placeholder="请选择是否用车" @change="handleChangeUseCar" v-decorator="['useCar', validatorRules.useCar]">
              <a-select-option :value="1">需要用车</a-select-option>
              <a-select-option :value="0">不需要用车</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label="车牌号" v-bind="formItemProps">
            <a-input :disabled="isEditAfter" :read-only="isEditDetail" placeholder="请输入车牌号" v-decorator="['carNumber', validatorRules.carNumber]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="10">
        <a-col :md="12" :sm="24">
          <a-form-item label="预期外出时间" v-bind="formItemProps">
            <a-input v-show="isEditDetail" read-only v-model="model.expectedStartTime"/>
            <a-date-picker v-show="!isEditDetail" placeholder="请选择预期外出时间" v-bind="expectedTimeProps" v-decorator="['expectedStartTime', validatorRules.expectedStartTime]" @change="v=>handleTimeChange(v,true)"/>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label="预期回来时间" v-bind="formItemProps">
            <a-input v-show="isEditDetail" read-only v-model="model.expectedEndTime"/>
            <a-date-picker v-show="!isEditDetail" placeholder="请选择预期回来时间" v-bind="expectedTimeProps" v-decorator="['expectedEndTime', validatorRules.expectedEndTime]" @change="v=>handleTimeChange(v,false)"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row v-show="isEditAfter" :gutter="10">
        <a-col :md="12" :sm="24">
          <a-form-item label="实际外出时间" v-bind="formItemProps">
            <a-input v-show="isEditDetail" read-only v-model="model.actualStartTime"/>
            <a-date-picker v-show="!isEditDetail" placeholder="请选择实际外出时间" v-bind="timeProps" v-decorator="['actualStartTime', validatorRules.actualStartTime]"/>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label="实际回来时间" v-bind="formItemProps">
            <a-input v-show="isEditDetail" read-only v-model="model.actualEndTime"/>
            <a-date-picker v-show="!isEditDetail" placeholder="请选择实际回来时间" v-bind="timeProps" v-decorator="['actualEndTime', validatorRules.actualEndTime]"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-spin>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from 'moment'
  import { getAction, httpAction } from '@api/manage'

  export default {
    name: 'OaSignOutForm',
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
            xs: { span: 24 },
            sm: { span: 6 },
          },
          wrapperCol: {
            xs: { span: 24 },
            sm: { span: 24 - 6 },
          },
        },
        validatorRules: {
          outPosition: { rules: [{ required: true, message: '请输入外出地点' }] },
          outReason: { rules: [{ required: true, message: '请输入外出事由' }] },
          useCar: { rules: [{ required: true, message: '请选择是否用车' }] },
          carNumber: { rules: [{ required: false, message: '请输入车牌号' }] },
          expectedStartTime: { rules: [{ required: true, message: '请选择预期外出时间' }] },
          expectedEndTime: {
            rules: [
              { required: false, message: '请选择预期回来时间' },
              { validator: (rule, value, callback) => this.validatorEndTime('expectedStartTime', rule, value, callback) }
            ]
          },
          actualStartTime: {
            rules: [
              { required: true, message: '请选择实际外出时间' },
              { validator: (rule, value, callback) => this.validatorEndTime('expectedStartTime', rule, value, callback) }
            ]
          },
          actualEndTime: {
            rules: [
              { required: false, message: '请选择实际回来时间' },
              { validator: (rule, value, callback) => this.validatorEndTime('actualStartTime', rule, value, callback) }
            ]
          },
        },
        url: {
          queryById: '/sign/out/queryById',
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
          this.validatorRules.carNumber.rules[0].required = (this.model.useCar === 1)

          this.form.setFieldsValue(pick(this.model, 'outReason', 'outPosition', 'useCar', 'carNumber'))
          //时间格式化
          this.form.setFieldsValue({ expectedStartTime: this.model.expectedStartTime ? moment(this.model.expectedStartTime) : null })
          this.form.setFieldsValue({ expectedEndTime: this.model.expectedEndTime ? moment(this.model.expectedEndTime) : null })
          this.form.setFieldsValue({ actualStartTime: this.model.actualStartTime ? moment(this.model.actualStartTime) : null })
          this.form.setFieldsValue({ actualEndTime: this.model.actualEndTime ? moment(this.model.actualEndTime) : null })
        })
      },
      validatorEndTime(startTimeKey, rule, value, callback) {
        let startTime = this.form.getFieldValue(startTimeKey)
        if (startTime != null) {
          let fmt = 'YYYY-MM-DD'
          if (startTime.format(fmt) !== value.format(fmt)) {
            callback('外出时间不能跨天')
          }
        }
        callback()
      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.$emit('update:loading', true)
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
            } else {
              httpurl += this.url.edit
              method = 'put'
            }
            let formData = Object.assign(this.model, values)
            //时间格式化
            formData.expectedStartTime = formData.expectedStartTime ? formData.expectedStartTime.format('YYYY-MM-DD HH:mm:ss') : null
            formData.expectedEndTime = formData.expectedEndTime ? formData.expectedEndTime.format('YYYY-MM-DD HH:mm:ss') : null
            formData.actualStartTime = formData.actualStartTime ? formData.actualStartTime.format('YYYY-MM-DD HH:mm:ss') : null
            formData.actualEndTime = formData.actualEndTime ? formData.actualEndTime.format('YYYY-MM-DD HH:mm:ss') : null
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.$emit('update:loading', false)
            })
          }
        })
      },
      handleChangeUseCar(value) {
        this.validatorRules.carNumber.rules[0].required = (value === 1)
        this.form.setFields({ 'carNumber': { errors: null } })
      },
      handleTimeChange(value, start) {
        let key = 'actualEndTime'
        if (start) {
          key = 'actualStartTime'
        }
        this.form.setFields({ [key]: { value } })
      },
    }
  }
</script>

<style lang="less" scoped>
  .pd {
    padding: 20px;
  }
</style>