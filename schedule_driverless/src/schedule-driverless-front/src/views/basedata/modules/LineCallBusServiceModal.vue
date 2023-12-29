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
        <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['routeId', validatorRules.routeId]"
            allowClear
            placeholder="请选择线路"
            show-search>
            <a-select-option v-for="(item,index) in routeDataParam" :key="index" :value="item.id">
              {{ item.routeName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="方向" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            placeholder="请选择"
            v-decorator="['direction', validatorRules.direction]"
            allowClear
            show-search
            :filter-option="addFilterOption">
            <a-select-option v-for="(item, key) in directionOptions" :key="key" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始服务时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-time-picker
            format="HH:mm"
            style="width: 100%"
            v-decorator="['startServiceTime', validatorRules.startServiceTime]">
          </a-time-picker>
        </a-form-item>
        <a-form-item label="结束服务时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-time-picker
            format="HH:mm"
            style="width: 100%"
            v-decorator="['endServiceTime', validatorRules.endServiceTime]">
          </a-time-picker>
        </a-form-item>
        <a-form-item label="有效需求时间(分钟)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            type="number"
            v-decorator="['effectiveTime', validatorRules.effectiveTime]"
            placeholder="请输入"
          ></a-input>
        </a-form-item>
        <a-form-item label="票价(元)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            type="number"
            v-decorator="['price', validatorRules.price]"
            placeholder="请输入"
          ></a-input>
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['remark']"
          ></a-input>
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
    name: 'LineCallBusServiceModal',
    components: {
      JDate, GCIOrgTreeSelect
    },
    data() {
      return {
        confirmLoading: false,
        disableSubmit: true,
        routeId: undefined,
        routeDataParam: [],
        //  方向
        directionOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '上行',
          value: '0'
        }, {
          label: '下行',
          value: '1'
        },
          {
            label: '无',
            value: '2'
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
          routeId: {
            rules: [
              {
                /* 必填校验，数字，长度32位以内，空格 */
                required: true, message: '请选择线路'
              }
            ]
          },
          direction: {
            rules: [{
              /* 必填校验 */
              required: true, message: '请选择方向'
            }
            ]
          },
          startServiceTime: {
            rules: [{
              /* 必填校验 */
              required: true, message: '请选择开始服务时间'
            }
            ]
          },
          endServiceTime: {
            rules: [{
              /* 必填校验 */
              required: true, message: '请选择结束服务时间'
            }
            ]
          },
          effectiveTime: {
            rules: [{
              /* 必填校验 */
              required: true, message: '请输入有效需求时间'
            }
            ,
              { validator: this.checkEffectiveTime }
            ]
          },
          price: {
            rules: [{
              /* 必填校验 */
              required: true, message: '请输入票价'
            },
              { validator: this.checkPrice }
            ]
          }
        },
        url: {
          add: '/baseData/lineCallBusService/add',
          edit: '/baseData/lineCallBusService/edit'
        }
      }
    },
    methods: {
      add(record, routeData, operateType) {
        this.edit(record, routeData, operateType)
      },
      edit: function(record, routeData, operateType) {
        this.form.resetFields()
        this.routeDataParam = routeData
        this.model = Object.assign({}, record)
        if (operateType === 'edit') {
          this.model.startServiceTime = moment(record.startServiceTime, 'HH:mm')
          this.model.endServiceTime = moment(record.endServiceTime, 'HH:mm')
        }
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'routeId', 'direction', 'startServiceTime', 'endServiceTime', 'effectiveTime', 'price', 'remark'))
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
          console.log(values)
          if (!err) {
            let starTtime = new Date(Date.parse(values.startServiceTime));
            let endTime = new Date(Date.parse(values.endServiceTime));
            if (starTtime >= endTime) {
              that.$message.warning('开始时间不能大于等于结束时间')
              return
            }
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
            } else {
              httpurl += this.url.edit
              method = 'post'
            }
            let formData = Object.assign(this.model, values)
            formData.startServiceTime = formData.startServiceTime.format('HH:mm')
            formData.endServiceTime = formData.endServiceTime.format('HH:mm')
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
      handleCancel() {
        this.close()
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      },
      checkEffectiveTime(rule, value, callback) {
        if (!value) {
          return callback()
        } else {
          let reg = /^\d+$/
          if (!String(value).match(reg)) {
            callback(new Error('必须为整数且取值范围为：0~9999'))
            return
          }
          if (value < 0 || value > 9999) {
            callback(new Error('取值范围为：0~9999'))
            return
          }
          callback()
        }
      },
      checkPrice(rule, value, callback) {
        if (!value) {
          return callback()
        } else {
          if (isNaN(value)) {
            callback(new Error('必须为数字且取值范围为：0~10.00'))
            return
          }
          let reg = /^\d+(\.\d{1}(\d{1})?)?$/
          if (!value.match(reg)) {
            callback(new Error('取值范围为：0~10.00且最多保留两位小数'))
            return
          }
          if (Number(value) < 0 || Number(value) > 10) {
            callback(new Error('取值范围为：0~10.00'))
            return
          }
          callback()
        }
      }
    }
  }
</script>
