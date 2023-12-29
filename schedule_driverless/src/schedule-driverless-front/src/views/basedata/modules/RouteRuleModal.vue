<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    :okButtonProps="{ props: {disabled: disableSubmit} }"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <GCIOrgTreeSelect
            v-decorator="['organId', validatorRules.organId]"
            placeholder="请选择机构"
            :select-type="'id'"
            @changeOptions="handleSelectOrgan"
            :isGetOption="true"></GCIOrgTreeSelect>
        </a-form-item>
        <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['routeId', validatorRules.routeId]"
            allowClear
            placeholder="请选择线路"
            :filter-option="addFilterOption"
            show-search>
            <a-select-option v-for="(item,index) in routeData" :key="index" :value="item.id">
              {{ item.routeName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            format="YYYY-MM-DD"
            v-decorator="['beginDate', validatorRules.beginDate]"
            allowClear
            placeholder="请选择"
            show-search>
          </a-date-picker>
        </a-form-item>
        <a-form-item label="结束日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            format="YYYY-MM-DD"
            v-decorator="['endDate', validatorRules.endDate]"
            allowClear
            placeholder="请选择"
            show-search>
          </a-date-picker>
        </a-form-item>
        <a-form-item label="适用日类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['ruleType', validatorRules.ruleType]"
            allowClear
            placeholder="请选择"
            show-search>
            <a-select-option v-for="(item,index) in ruleTypeOptions" :key="index" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="首站首班" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-time-picker
            format="HH:mm"
            style="width: 100%"
            v-decorator="['fromFirTime', validatorRules.fromFirTime]">
          </a-time-picker>
        </a-form-item>
        <a-form-item label="首站末班" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-time-picker
            format="HH:mm"
            style="width: 100%"
            v-decorator="['fromLasTime', validatorRules.fromLasTime]">
          </a-time-picker>
        </a-form-item>
        <a-form-item label="末站首班" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-time-picker
            format="HH:mm"
            style="width: 100%"
            v-decorator="['toFirTime', validatorRules.toFirTime]">
          </a-time-picker>
        </a-form-item>
        <a-form-item label="末站末班" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-time-picker
            format="HH:mm"
            style="width: 100%"
            v-decorator="['toLasTime', validatorRules.toLasTime]">
          </a-time-picker>
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['remark', validatorRules.remark]">
          </a-input>
        </a-form-item>
        <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['status', validatorRules.status]"
            allowClear
            placeholder="请选择"
            show-search>
            <a-select-option v-for="(item,index) in statusOptions" :key="index" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import JDate from '@/components/jeecg/JDate'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import moment from 'moment'
  import pick from 'lodash.pick'

  export default {
    name: 'RouteRuleModal',
    components: {
      JDate, GCIOrgTreeSelect
    },
    data() {
      return {
        routeData: [],
        dateData: [],
        ruleTypeOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '所有适用',
          value: '0'
        }, {
          label: '平时',
          value: '1'
        }, {
          label: '周末',
          value: '2'
        }, {
          label: '节假日',
          value: '3'
        }
        ],
        statusOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '有效',
          value: '0'
        }, {
          label: '无效',
          value: '1'
        }
        ],
        confirmLoading: false,
        disableSubmit: true,
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
          organId: {
            rules: [
              {
                required: true, message: '请选择机构'
              }
            ]
          },
          routeId: {
            rules: [{
              required: true, message: '请选择线路'
            }
            ]
          },
          beginDate: {
            rules: [{
              required: true, message: '请选择开始日期'
            }
            ]
          },
          endDate: {
            rules: [{
              required: true, message: '请选择结束日期'
            }
            ]
          },
          ruleType: {
            rules: [{
              required: true, message: '请选择适用日类型'
            }
            ]
          },
          fromFirTime: {
            rules: [{
              required: true, message: '请选择首站首班'
            }
            ]
          },
          fromLasTime: {
            rules: [{
              required: true, message: '请选择首站末班'
            }
            ]
          },
          toFirTime: {
            rules: [{
              required: true, message: '请选择末站首班'
            }
            ]
          },
          toLasTime: {
            rules: [{
              required: true, message: '请选择末站末班'
            }
            ]
          },
          status: {
            rules: [{
              required: true, message: '请选择状态'
            }
            ]
          }
        },
        url: {
          add: '/base/routeRule/add',
          edit: '/base/routeRule/edit',
          listRoute: '/common/sys/queryRouteByOrganId'
        }
      }
    },
    methods: {
      add(record, operateType) {
        this.edit(record, operateType)
      },
      edit: function(record, operateType) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        if (operateType === 'edit') {
          let params = { organId: record.organId }
          getAction(this.url.listRoute, params).then((res) => {
            if (res.success) {
              this.routeData = res.result
            }
          })
          this.model.beginDate = moment(record.beginDate)
          this.model.endDate = moment(record.endDate)
          this.model.fromFirTime = moment(record.fromFirTime, 'HH:mm')
          this.model.fromLasTime = moment(record.fromLasTime, 'HH:mm')
          this.model.toFirTime = moment(record.toFirTime, 'HH:mm')
          this.model.toLasTime = moment(record.toLasTime, 'HH:mm')
        }
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'routeId', 'organId', 'beginDate', 'endDate', 'ruleType',
            'fromFirTime', 'fromLasTime', 'toFirTime', 'toLasTime', 'status', 'remark'))
        })
      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            if (!this.model.routeRuleId) {
              httpurl += this.url.add
              method = 'post'
            } else {
              httpurl += this.url.edit
              method = 'post'
            }
            let formData = Object.assign(this.model, values)
            formData.beginDate = formData.beginDate.format('YYYY-MM-DD')
            formData.endDate = formData.endDate.format('YYYY-MM-DD')
            formData.fromFirTime = formData.fromFirTime.format('HH:mm')
            formData.fromLasTime = formData.fromLasTime.format('HH:mm')
            formData.toFirTime = formData.toFirTime.format('HH:mm')
            formData.toLasTime = formData.toLasTime.format('HH:mm')
            console.log('表单提交数据', formData)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.confirmLoading = false
                that.close()
              } else {
                if (res.code === 501) {
                  that.confirmLoading = false
                  that.disableSubmit = false
                  that.$message.warning(res.message)
                } else {
                  that.confirmLoading = false
                  that.$message.warning(res.message)
                }
              }
            }).catch(() => {
              that.confirmLoading = false
            })
          }
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleCancel() {
        this.close()
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      },
      handleSelectOrgan(value) {
        this.form.setFieldsValue({ routeId: undefined })
        this.routeData = []
        console.log('机构的', value)
        let params = { organId: value.id }
        getAction(this.url.listRoute, params).then((res) => {
          if (res.success) {
            this.routeData = res.result
          }
        })
      }
    }
  }
</script>
