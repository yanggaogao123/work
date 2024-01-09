<template>
  <!--    switchFullscreen-->
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :okButtonProps="{ props: {disabled: disableSubmit} }"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="方向" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-checkbox-group v-decorator="['direction', validatorRules.direction]" :disabled="directionDisabled">
            <a-checkbox v-for="(item,index) in directionOptions" :key="index" :value="item.value">{{ item.label }}
            </a-checkbox>
          </a-checkbox-group>
        </a-form-item>
        <a-form-item label="时段峰期类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['segmentType', validatorRules.segmentType]"
            allowClear
            placeholder="请选择"
            show-search>
            <a-select-option v-for="(item,index) in segmentTypeOptions" :key="index" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-row :gutter="24">
          <a-col :span="16" style="margin-left: 55px">
            <a-form-item label="时段开始(>=)" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-radio-group v-decorator="['beginNextDay', validatorRules.beginNextDay]" @change="changBeginRadio">
                <a-radio v-for="(item,index) in beginNextDayOptions" :key="index" :value="item.value">{{ item.label }}
                </a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="8" style="padding-left: 0px; margin-left: -93px;">
            <a-form-item :wrapperCol="wrapperCol" style="width: 267px;" >
              <a-time-picker
                format="HH:mm"
                v-decorator="['timeSegmentBegin', validatorRules.timeSegmentBegin]"
                style="width: 160px;"
                @openChange="changTimeSegmentBegin">
              </a-time-picker>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="16" style="margin-left: 55px">
            <a-form-item label="时段结束(<=)" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-radio-group v-decorator="['endNextDay', validatorRules.beginNextDay]" @change="changEndRadio">
                <a-radio v-for="(item,index) in beginNextDayOptions" :key="index" :value="item.value">{{ item.label }}
                </a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="8" style="padding-left: 0px; margin-left: -93px;">
            <a-form-item :wrapperCol="wrapperCol" style="width: 267px;">
              <a-time-picker
                format="HH:mm"
                v-decorator="['timeSegmentEnd', validatorRules.timeSegmentEnd]"
                style="width: 160px;"
                @openChange="changTimeSegmentEnd">
              </a-time-picker>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="发班间隔(分钟)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            type="number"
            v-decorator="['interval', validatorRules.interval]"
            :disabled="intervalDisabled"
            @change="changInterval"
            onkeyup="(function () {value = /^[1-9]\d*$/.test(value)? value: ''})()">
          </a-input>
        </a-form-item>
        <a-form-item label="最低发班数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            type="number"
            v-decorator="['tripMin', validatorRules.tripMin]"
            :disabled="tripMinDisabled"
            @change="changTripMin"
            onkeyup="(function () {value = /^[1-9]\d*$/.test(value)? value: ''})()">
          </a-input>
        </a-form-item>
        <a-form-item label="最大发班数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['tripMax', validatorRules.tripMax]">
          </a-input>
        </a-form-item>
        <a-form-item label="准点允许误差范围(分钟)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['pointRange', validatorRules.pointRange]">
          </a-input>
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
  import AFormItem from 'ant-design-vue/es/form/FormItem'
  import ARow from 'ant-design-vue/es/grid/Row'
  import ACol from 'ant-design-vue/es/grid/Col'

  export default {
    name: 'RouteRuleSubModal',
    components: {
      ACol,
      ARow,
      AFormItem,
      JDate,
      GCIOrgTreeSelect
    },
    // beforeCreate() {
    //   this.form = this.$form.createForm(this, {
    //     onValuesChange: this.changTimeSegmentBegin()
    //   });
    // },
    data() {
      return {
        operateType: undefined,
        lockType: undefined,
        directionDisabled: false,
        intervalDisabled: false,
        tripMinDisabled: false,
        dateData: [],
        // 默认选中上行
        directionSelect: ['0'],
        beginNextDay: '0',
        segmentTypeOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '平峰',
          value: '1'
        }, {
          label: '早高峰',
          value: '2'
        }, {
          label: '晚高峰',
          value: '3'
        }
        ],
        directionOptions: [{
          label: '上行',
          value: '0'
        }, {
          label: '下行',
          value: '1'
        }],
        beginNextDayOptions: [{
          label: '当日',
          value: '0'
        }, {
          label: '次日',
          value: '1'
        }],
        confirmLoading: false,
        disableSubmit: true,
        form: this.$form.createForm(this),
        title: '操作',
        width: 550,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 14 }
        },
        validatorRules: {
          direction: {
            rules: [
              {
                required: true, message: '请选择方向'
              }
            ]
          },
          segmentType: {
            rules: [{
              required: true, message: '请选择时段峰期类型'
            }
            ]
          },
          // beginNextDay: {
          //   rules: [{
          //     required: true, message: '请选择开始时段类型'
          //   }
          //   ]
          // },
          timeSegmentBegin: {
            rules: [{
              required: true, message: '请选择时间'
            },
              { validator: this.timeSegmentBeginValidate }
            ]
          },
          timeSegmentEnd: {
            rules: [{
              required: true, message: '请选择时间'
            },
              { validator: this.timeSegmentEndValidate }
            ]
          },
          interval: {
            rules: [{
              required: true,
              pattern: new RegExp(/^[1-9]\d*$/, 'g'),
              getValueFromEvent: (event) => {
                return event.target.value.replace(/\D/g, '')
              },
              initialValue: '',
              message: '数据格式不正确'
            }
            ]
          },
          tripMin: {
            rules: [{
              required: true,
              pattern: new RegExp(/^[1-9]\d*$/, 'g'),
              getValueFromEvent: (event) => {
                return event.target.value.replace(/\D/g, '')
              },
              initialValue: '',
              message: '数据格式不正确'
            }
            ]
          },
          tripMax: {
            rules: [{
              required: true,
              pattern: new RegExp(/^[1-9]\d*$/, 'g'),
              getValueFromEvent: (event) => {
                return event.target.value.replace(/\D/g, '')
              },
              initialValue: '',
              message: '数据格式不正确'
            }
            ]
          },
          pointRange: {
            rules: [{
              required: true,
              pattern: new RegExp(/^[1-9]\d*$/, 'g'),
              getValueFromEvent: (event) => {
                return event.target.value.replace(/\D/g, '')
              },
              initialValue: '',
              message: '数据格式不正确'
            }
            ]
          }
        },
        url: {
          add: '/base/routeRuleSub/add',
          edit: '/base/routeRuleSub/edit'
        }
      }
    },
    methods: {
      add(record, operateType, routeRuleId) {
        this.edit(record, operateType, routeRuleId)
      },
      edit: function(record, operateType, routeRuleId) {
        this.lockType = 'unlock'
        this.operateType = operateType
        this.tripMinDisabled = false
        this.intervalDisabled = false
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.model.routeRuleId = routeRuleId
        if (operateType === 'edit') {
          this.directionDisabled = true
          this.model.timeSegmentBegin = moment(record.timeSegmentBegin, 'HH:mm')
          this.model.timeSegmentEnd = moment(record.timeSegmentEnd, 'HH:mm')
        } else {
          this.directionDisabled = false
        }
        this.visible = true
        this.$nextTick(() => {
          if (operateType === 'add') {
            this.form.setFieldsValue({ direction: this.directionSelect })
            this.form.setFieldsValue({ beginNextDay: this.beginNextDay })
            this.form.setFieldsValue({ endNextDay: this.beginNextDay })
          }
          if (operateType === 'edit') {
            let direction = []
            direction.push(record.direction)
            this.form.setFieldsValue({ direction: direction })
          }
          this.form.setFieldsValue(pick(this.model, 'timeSegmentBegin', 'timeSegmentEnd', 'interval', 'tripMin',
            'tripMax', 'pointRange', 'beginNextDay', 'segmentType', 'endNextDay'))
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
            let formData = Object.assign(this.model, values)
            formData.timeSegmentBegin = formData.timeSegmentBegin.format('HH:mm')
            formData.timeSegmentEnd = formData.timeSegmentEnd.format('HH:mm')
            if (!this.model.rrSubId) {
              httpurl += this.url.add
              method = 'post'
            } else {
              httpurl += this.url.edit
              method = 'post'
              formData.direction = this.model.direction[0]
            }
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
                  that.$message.error(res.message)
                } else {
                  that.confirmLoading = false
                  that.$message.error(res.message)
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
      // 时间校验
      timeSegmentBeginValidate(rule, value, callback) {
        let timeSegmentEnd = this.form.getFieldValue('timeSegmentEnd')
        if (!value || !timeSegmentEnd) {
          callback()
          return
        }
        if (this.compareTime(value, timeSegmentEnd)) {
          callback()
        } else {
          callback('开始时间需小于结束时间')
        }
      },
      // 时间校验
      timeSegmentEndValidate(rule, value, callback) {
        let timeSegmentBegin = this.form.getFieldValue('timeSegmentBegin')
        if (!value || !timeSegmentBegin) {
          callback()
          return
        }
        if (this.compareTime(timeSegmentBegin, value)) {
          callback()
        } else {
          callback('结束时间需大于开始时间')
        }
      },
      changTimeSegmentBegin(open) {
        if (open) {
          return
        }
        this.changeIntervalOrTripMin('timeType', '')
      },
      changTimeSegmentEnd(open) {
        if (open) {
          return
        }
        this.changeIntervalOrTripMin('timeType', '')
      },
      changBeginRadio(e) {
        this.changeIntervalOrTripMin('radioBeginType', e.target.value)
      },
      changEndRadio(e) {
        this.changeIntervalOrTripMin('radioEndType', e.target.value)
      },
      changInterval(e) {
        let interval = e.target.value
        if (interval === undefined || interval === '') {
          this.lockType = 'unlock'
          this.tripMinDisabled = false
          this.intervalDisabled = false
          this.form.setFieldsValue({ tripMin: undefined })
        } else {
          this.lockType = 'tripMin'
          this.tripMinDisabled = true
        }
        this.changeIntervalOrTripMin('numType', interval)
      },
      changTripMin(e) {
        let tripMin = e.target.value
        if (tripMin === undefined || tripMin === '') {
          this.lockType = 'unlock'
          this.tripMinDisabled = false
          this.intervalDisabled = false
          this.form.setFieldsValue({ interval: undefined })
        } else {
          this.lockType = 'interval'
          this.intervalDisabled = true
        }
        this.changeIntervalOrTripMin('numType', tripMin)
      },
      compareTime(startValue, endValue) {
        let beginTime = startValue.format('HH:mm')
        let endTime = endValue.format('HH:mm')
        let today = moment().format('YYYY-MM-DD')
        let nextDay = moment().add(1, 'day').format('YYYY-MM-DD')
        let beginNextDay = this.form.getFieldValue('beginNextDay')
        let endNextDay = this.form.getFieldValue('endNextDay')
        if (beginNextDay === '1') {
          beginTime = nextDay + ' ' + beginTime
        } else {
          beginTime = today + ' ' + beginTime
        }
        if (endNextDay === '1') {
          endTime = nextDay + ' ' + endTime
        } else {
          endTime = today + ' ' + endTime
        }
        return moment(beginTime).isBefore(endTime)
      },
      changeIntervalOrTripMin(countType, countNum) {
        let timeSegmentBegin = this.form.getFieldValue('timeSegmentBegin')
        let timeSegmentEnd = this.form.getFieldValue('timeSegmentEnd')
        let interval = this.form.getFieldValue('interval')
        let tripMin = this.form.getFieldValue('tripMin')
        let beginNextDay = this.form.getFieldValue('beginNextDay')
        let endNextDay = this.form.getFieldValue('endNextDay')
        if (interval !== undefined && tripMin !== undefined && interval !== '' && tripMin !== '') {
            if (this.operateType === 'edit' && (this.lockType === 'unlock' || this.lockType === undefined)) {
                this.lockType = 'tripMin'
               this.tripMinDisabled = true
            }
        }
        if (countType === 'radioBeginType') {
          beginNextDay = countNum
        } else if (countType === 'radioEndType') {
          endNextDay = countNum
        }
        if ((countType === 'timeType' || countType === 'radioBeginType' || countType === 'radioEndType') && this.lockType === 'interval') {
          countNum = this.form.getFieldValue('tripMin')
        } else if ((countType === 'timeType' || countType === 'radioBeginType' || countType === 'radioEndType') && this.lockType === 'tripMin') {
          countNum = this.form.getFieldValue('interval')
        }
        if (timeSegmentBegin !== undefined && timeSegmentEnd !== undefined) {
          let beginTime = timeSegmentBegin.format('HH:mm')
          let endTime = timeSegmentEnd.format('HH:mm')
          let today = moment().format('YYYY-MM-DD')
          let nextDay = moment().add(1, 'day').format('YYYY-MM-DD')
          if (beginNextDay === '1') {
            beginTime = nextDay + ' ' + beginTime
          } else {
            beginTime = today + ' ' + beginTime
          }
          if (endNextDay === '1') {
            endTime = nextDay + ' ' + endTime
          } else {
            endTime = today + ' ' + endTime
          }
          if (moment(beginTime).isAfter(endTime)) {
            return
          }
          if (this.lockType !== undefined && (countNum !== '' || countNum !== undefined)) {
            let timeDiff = moment(endTime).diff(moment(beginTime), 'minute')
            let tempCount = Math.floor(timeDiff / countNum)
            if (tempCount === 0) {
              tempCount = 1
            }
            if (this.lockType === 'interval') {
              this.form.setFieldsValue({ interval: tempCount })
            } else if (this.lockType === 'tripMin') {
              this.form.setFieldsValue({ tripMin: tempCount })
            }
          }
        }
      }
    }
  }
</script>
