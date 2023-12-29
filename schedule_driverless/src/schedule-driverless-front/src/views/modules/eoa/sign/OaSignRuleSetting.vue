<template>
  <a-card :bordered="false">
    <a-spin :spinning="loading">
      <a-form :form="form" class="time-picker-bigger">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="工作日">
          <a-checkbox-group v-decorator="['workingDays', validatorRules.workingDays]">
            <a-checkbox value="1">星期一</a-checkbox>
            <a-checkbox value="2">星期二</a-checkbox>
            <a-checkbox value="3">星期三</a-checkbox>
            <a-checkbox value="4">星期四</a-checkbox>
            <a-checkbox value="5">星期五</a-checkbox>
            <a-checkbox value="6">星期六</a-checkbox>
            <a-checkbox value="0">星期日</a-checkbox>
          </a-checkbox-group>
          <span class="tip">定义星期几需要打卡</span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="上班时间">
          <a-time-picker placeholder="请选择上班时间" v-bind="timePickerProps" v-decorator="['onTime', validatorRules.onTime]"/>
          <span class="tip"></span>
        </a-form-item>
        <a-form-item
          v-show="false"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="上班迟到时间">
          <a-time-picker placeholder="请选择上班迟到时间" v-bind="timePickerProps" v-decorator="['onTimeLate', validatorRules.onTimeLate]"/>
          <span class="tip">上班时间之后到迟到时间之前打卡记为迟到</span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="上班旷工时间">
          <a-time-picker placeholder="请选择上班旷工时间" v-bind="timePickerProps" v-decorator="['onTimeAbsent', validatorRules.onTimeAbsent]"/>
          <span class="tip">该时间后打卡记为旷工</span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="下班时间">
          <a-time-picker placeholder="请选择下班时间" v-bind="timePickerProps" v-decorator="['offTime', validatorRules.offTime]"/>
          <span class="tip"></span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="下班早退时间">
          <a-time-picker placeholder="请选择下班早退时间" v-bind="timePickerProps" v-decorator="['offTimeEarly', validatorRules.offTimeEarly]"/>
          <span class="tip">该时间前打卡记为早退，反之记为正常打卡</span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="不参与打卡人员">
          <j-select-multi-user placeholder="请选择不参与打卡人员" v-decorator="['notJoining', {}]" style="max-width: 700px;"/>
        </a-form-item>
        <div class="holiday-box" :class="{show:showHoliday}">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="元旦假期时间">
            <a-range-picker v-bind="getRangePickerProps('元旦')" v-decorator="['holidayYuanDan', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="春节假期时间">
            <a-range-picker v-bind="getRangePickerProps('春节')" v-decorator="['holidayChunJie', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="清明假期时间">
            <a-range-picker v-bind="getRangePickerProps('清明')" v-decorator="['holidayQingMing', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="五一劳动节假期时间">
            <a-range-picker v-bind="getRangePickerProps('五一')" v-decorator="['holidayLaoDong', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="端午假期时间">
            <a-range-picker v-bind="getRangePickerProps('端午')" v-decorator="['holidayDuanWu', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="中秋假期时间">
            <a-range-picker v-bind="getRangePickerProps('中秋')" v-decorator="['holidayZhongQiu', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="国庆假期时间">
            <a-range-picker v-bind="getRangePickerProps('国庆')" v-decorator="['holidayGuoQing', {}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="其他假期时间">
            <a-range-picker v-bind="getRangePickerProps('其他')" v-decorator="['holidayOther', {}]"/>
          </a-form-item>
        </div>

        <div class="bottom-btn">
          <a-button @click="showHoliday=!showHoliday" type="primary" ghost :style="{marginRight: '8px'}">{{showHoliday?'收起':'展开'}}假期安排</a-button>
          <a-button type="primary" @click="handleClickSubmit">保存</a-button>
        </div>

      </a-form>
    </a-spin>
  </a-card>
</template>

<script>
  import moment from 'moment/moment'
  import pick from 'lodash.pick'
  import { getAction, postAction } from '@api/manage'
  import JSelectMultiUser from '@comp/jeecgbiz/JSelectMultiUser'

  export default {
    name: 'OaSignRuleSetting',
    components: { JSelectMultiUser },
    data() {
      return {
        form: this.$form.createForm(this),
        loading: false,
        model: {},
        timePickerProps: {
          format: 'HH:mm',
          getPopupContainer: (node) => node.parentNode
        },
        getRangePickerProps(text) {
          return {
            format: 'YYYY-MM-DD',
            placeholder: [`${text}假期开始时间`, `${text}假期结束时间`],
            getPopupContainer: (node) => node.parentNode,
            style: { width: '550px' }
          }
        },
        showHoliday: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        validatorRules: {
          workingDays: { rules: [{ required: true, message: '请至少选择一项' }] },
          onTime: { rules: [{ required: true, message: '上班时间必须填写' }] },
          onTimeLate: { rules: [{ required: false, message: '上班迟到时间必须填写' }] },
          onTimeAbsent: { rules: [{ required: true, message: '上班旷工时间必须填写' }] },
          offTime: { rules: [{ required: true, message: '下班时间必须填写' }] },
          offTimeEarly: { rules: [{ required: true, message: '下班早退时间必须填写' }] }
        },
        url: {
          query: '/sign/rule/query',
          saveOrUpdate: '/sign/rule/saveOrUpdate'
        },
      }
    },
    created() {
      this.query()
    },
    methods: {
      query() {
        this.loading = true
        getAction(this.url.query).then(res => {
          let { result } = res
          if (result == null) {
            this.model = {}
          } else {
            this.model = result
          }
          this.$nextTick(() => {
            // 时间格式化
            this.formatDateTime('HH:mm', this.model, ['onTime', 'onTimeLate', 'onTimeAbsent', 'offTime', 'offTimeEarly'], true)
            this.formatDateTime('YYYY-MM-DD', this.model, ['holidayYuanDan[]', 'holidayChunJie[]', 'holidayQingMing[]', 'holidayLaoDong[]', 'holidayDuanWu[]', 'holidayZhongQiu[]', 'holidayGuoQing[]', 'holidayOther[]'], true)
            this.model.workingDays = this.model.workingDays ? this.model.workingDays.split(',') : []
            this.form.setFieldsValue(pick(this.model,
              'workingDays',
              'onTime',
              'onTimeLate',
              'onTimeAbsent',
              'offTime',
              'offTimeEarly',
              'notJoining',
              'holidayYuanDan',
              'holidayChunJie',
              'holidayQingMing',
              'holidayLaoDong',
              'holidayDuanWu',
              'holidayZhongQiu',
              'holidayGuoQing',
              'holidayOther'
            ))
          })
        }).finally(() => {
          this.loading = false
        })
      },

      handleClickSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            // this.transFileListToString(values)
            let formData = Object.assign({}, this.model, values)
            // 时间格式化
            this.formatDateTime('HH:mm', formData, ['onTime', 'onTimeLate', 'onTimeAbsent', 'offTime', 'offTimeEarly'])
            this.formatDateTime('YYYY-MM-DD', formData, ['holidayYuanDan[]', 'holidayChunJie[]', 'holidayQingMing[]', 'holidayLaoDong[]', 'holidayDuanWu[]', 'holidayZhongQiu[]', 'holidayGuoQing[]', 'holidayOther[]'])
            formData.workingDays = formData.workingDays.join(',')
            this.loading = true
            postAction(this.url.saveOrUpdate, formData).then(res => {
              if (res.success) {
                this.$message.success('保存成功')
              } else throw res.message
            }).catch(e => {
              this.$warning({ title: '保存失败', content: (e || {}).message || e })
            }).finally(() => {
              this.loading = false
            })
          }
        })
      },

      formatDateTime(fmt, obj, keys, isString = false) {
        keys.forEach(key => {
          let value = obj[key]
          let arr = key.split('[]')
          if (arr.length === 2) {
            value = obj[arr[0]]
            if (value) {
              if (isString) {
                obj[arr[0]] = value.split(',').map(v => moment(v, fmt))
              } else {
                obj[arr[0]] = value.map(v => v.format(fmt)).join(',')
              }
            } else {
              obj[arr[0]] = undefined
            }
          } else if (value) {
            obj[key] = isString ? moment(value, fmt) : value.format(fmt)
          }
        })
      }

    }
  }
</script>

<style lang="less" scoped>

  .tip {
    color: #a0a0a0;
    margin-left: 10px;
  }

  .time-picker-bigger {

    /deep/ .ant-time-picker {
      width: 200px;
      &-panel-narrow &-panel-input-wrap {
        max-width: 200px;
      }
      &-panel-select {
        width: 100px;
        overflow-y: auto;
        li {
          padding: 0;
          text-align: center;
        }
      }
    }

  }

  .holiday-box {
    height: 0;
    opacity: 0;
    overflow: hidden;
    margin-bottom: 20px;
    transition: height 0.3s, opacity 1s;

    &.show {
      opacity: 1;
      height: 520px;
    }

  }

  .bottom-btn {
    width: 100%;
    border-top: 1px solid #e9e9e9;
    padding: 10px 16px;
    background: #fff;
    text-align: center;
  }

</style>