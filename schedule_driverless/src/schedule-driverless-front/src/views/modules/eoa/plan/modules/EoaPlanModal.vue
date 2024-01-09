<template>
  <a-config-provider>
    <a-modal
      :title="title"
      :width="1000"
      :body-style="{padding:'24px 74px 24px 24px'}"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭">

      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-divider orientation="left" style="margin-bottom: 40px">基本信息</a-divider>
          <a-row style="width: 100%;">
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="日程标题">
                <a-input placeholder="请输入标题" :read-only="allReadOnly" v-decorator="['title', validatorRules.title ]"/>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="日程类型">
                <j-dict-select-tag placeholder="请选择类型" type="select"  :disabled="allReadOnly" v-decorator="[ 'type', {}]" :trigger-change="true" dictCode="eoa_plan_type"/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row style="width: 100%;">
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="提醒类型">
                <a-select placeholder="请选择提醒类型" mode="multiple" :disabled="allReadOnly" v-decorator="['remindType', validatorRules.remindType]">
                  <a-select-option value="1">邮件</a-select-option>
                  <a-select-option value="2">短信</a-select-option>
                  <a-select-option value="4">系统消息</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="紧急程度">
                <j-dict-select-tag placeholder="请选择紧急程度" type="select"  v-decorator="[ 'urgentLevel', validatorRules.urgentLevel]" :trigger-change="true" dictCode="urgent_level"/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="提醒时间">
                <j-dict-select-tag placeholder="请选择提醒时间" type="select"   v-decorator="['remind',validatorRules.isNotEmpty]" :trigger-change="true" dictCode="remindTime"/>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="重复提醒">
                <j-dict-select-tag placeholder="请选择重复提醒" type="select"   v-decorator="['reminders',validatorRules.isNotEmpty]" :trigger-change="true" dictCode="reminders"/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row style="width: 100%;">
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="日程时间"
              >
                <a-range-picker v-bind="dateRangePickerProps" :disabled="allReadOnly" v-decorator="[ 'rangeTime', validatorRules.rangeTime]"/>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="是否全天">
                <j-checkbox :options="[{value:'1'}]" :disabled="allReadOnly" v-decorator="['allDay', {}]" @change="allDayValue=$event"/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-divider orientation="left" style="margin-bottom: 40px">其他信息</a-divider>
          <a-row>
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="颜色">
                <a-tooltip class="setting-drawer-theme-color-colorBlock" v-for="(item, index) in colorList" :key="index">
                  <template slot="title">
                    {{ item.key }}
                  </template>
                  <a-tag :color="item.color" @click="changeColor(item.color)">
                    <a-icon type="check" v-if="item.color === primaryColor"></a-icon>
                  </a-tag>
                </a-tooltip>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item
                :labelCol="labelColX2"
                :wrapperCol="wrapperColX2"
                label="参与人">
                <j-select-user-by-dep v-decorator="['joinPerson', {}]" :multi="true"></j-select-user-by-dep>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row style="width: 100%;">
            <a-col :span="24">
              <a-form-item
                class="j-eoa-plan-field-remark"
                :labelCol="labelColX1"
                :wrapperCol="wrapperColX1"
                label="备注"
              >
                <j-editor
                  placeholder="请输入备注"
                  :triggerChange="true"
                  :disabled="allReadOnly"
                  v-decorator="['remark', {}]"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-spin>

      <template slot="footer">
        <a-button type="primary" :loading="confirmLoading" @click="handleOk">
          保存
        </a-button>
        <a-popconfirm v-if="model.id" title="您确定要删除日程安排吗？" @confirm="handleDelete">
          <a-button type="danger" :loading="confirmLoading">删除</a-button>
        </a-popconfirm>
        <a-button type="dashed" @click="handleCancel" :loading="confirmLoading">取消</a-button>
      </template>

    </a-modal>
  </a-config-provider>
</template>

<script>
  import { httpAction, deleteAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from 'moment'
  import store from '@/store/'
  import JEditor from '@/components/jeecg/JEditor'
  import JCheckbox from '@comp/jeecg/JCheckbox'
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
  import { calendarViewType } from '@/utils/LunarFullCalendarUtil'
  export default {
    name: 'EoaPlanModal',
    components: { JCheckbox, JEditor,JSelectUserByDep,JMultiSelectTag },
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},

        labelColX1: {
          xs: { span: 24 },
          sm: { span: 3 },
        },
        wrapperColX1: {
          xs: { span: 24 },
          sm: { span: 24 - 3 },
        },

        labelColX2: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperColX2: {
          xs: { span: 24 },
          sm: { span: 24 - 6 },
        },

        labelColX3: {
          xs: { span: 24 },
          sm: { span: 7 },
        },
        wrapperColX3: {
          xs: { span: 24 },
          sm: { span: 24 - 7 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          title: { rules: [{ required: true, message: '请输入标题!' }] },
          rangeTime: { rules: [{ required: true, message: '请选择时间!' }] },
          // urgentLevel: { rules: [{ required: true, message: '请选择紧急程度!' }] },
          remindType: { rules: [{ required: true, message: '请选择提醒类型!' }] },
          isNotEmpty:{rules:[{required:true,message:"该项不能为空!"}]},
        },

        allDayValue: '',
        allReadOnly: false,
        colorList: [{
          key: '薄暮', color: '#F5222D',
        },
          {
            key: '火山', color: '#FA541C',
          },
          {
            key: '日暮', color: '#FAAD14',
          },
          {
            key: '明青', color: '#13C2C2',
          },
          {
            key: '极光绿', color: '#52C41A',
          },
          {
            key: '拂晓蓝（默认）', color: '#1890FF',
          },
          {
            key: '极客蓝', color: '#2F54EB',
          },
          {
            key: '酱紫', color: '#722ED1',
          },
        ],
        primaryColor:'#1890FF',
        url: {
          add: '/eoa/plan/add',
          edit: '/eoa/plan/edit',
          delete: '/eoa/plan/delete',
        },
      }
    },
    computed: {
      dateRangePickerProps() {
        return {
          style: {width:'340px'},
          showTime: this.isAllDay ? false : {
            format: 'HH:mm',
            hideDisabledOptions: true,
            defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('00:00:00', 'HH:mm:ss')]
          },
          disabledTime: () => {
            return {
              disabledMinutes: () => this.dateRange(0, 59).filter(i => !([0, 15, 30, 45].includes(i))),
            }
          },
          format: this.dateFormat,
          getCalendarContainer: (el) => el.parentNode
        }
      },

      isAllDay() {
        return (this.allDayValue === '1')
      },

      dateFormat() {
        return 'YYYY-MM-DD' + (this.isAllDay ? '' : ' HH:mm')
      },
    },
    created() {
    },
    methods: {
      dateRange(start, end) {
        const result = []
        for (let i = start; i <= end; i++) {
          result.push(i)
        }
        return result
      },

      /** 新增日程计划 */
      addEvent(start,end, view, isAllDay,currentUser) {
        console.log("currentUser==》新增设置参与人",currentUser)
        this.title = '新增日程计划'
        this.visible = true
        let { type } = view
        let values = {}
        if (type === 'dayGridMonth'|| type===calendarViewType.month) {
          // 当显示月时，天数会自动加一天，故减一天
          var timestamp = end /1000;
          timestamp = (timestamp-86400)*1000;
          values['rangeTime'] = [start, moment(timestamp)]
        } else if (type === 'timeGridWeek' || type === 'timeGridDay'||type===calendarViewType.agendaDay || type===calendarViewType.agendaWeek) {
          values['rangeTime'] = [start, end]
        }
        // 如果是全天则选中全天
        this.allDayValue = isAllDay ? '1' : ''
        values['allDay'] = this.allDayValue
        if(currentUser){
          console.log("currentUser==》新增设置参与人",currentUser)
          values['joinPerson'] = currentUser
        } else {
          values['joinPerson'] = store.getters.userInfo.username
        }
        this.$nextTick(() => this.form.setFieldsValue(values))
      },

      add() {
        this.edit({}, '新增日程计划')
      },
      view(record) {
        this.allReadOnly = true
        this.edit(record, '查看日程计划')
      },
      edit(record, title = '变更日程计划') {
        this.title = title
        this.visible = true
        this.model = Object.assign({}, record)
        this.primaryColor=this.model.planColor;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            'title',
            'type',
            'allDay',
            'urgentLevel',
            // 'receiverIds',
            'remark',
            'remind',
            'reminders',
            'joinPerson'
            // 'status'
          ))

          let conventValues = {}

          //时间格式化
          if (this.model.startTime && this.model.endTime) {
            conventValues['rangeTime'] = [this.model.startTime, this.model.endTime].map(i => i ? moment(i) : null)
          }

          // 转换 checkbox 的值
          this.allDayValue = this.model.allDay
          conventValues['allDay'] = (this.model.allDay === '1' ? '1' : '')

          // 转换多选 select 的值
          conventValues['remindType'] = []
          if (this.model.remindType) {
            conventValues['remindType'] = this.model.remindType.split(',')
          }

          this.form.setFieldsValue(conventValues)
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
        this.allReadOnly = false
        this.primaryColor='#1890FF';
        setTimeout(() => {
          this.model = {}
          this.form.resetFields()
        }, 300)

      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
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
            let times = formData.rangeTime.map(i => i ? i.format('YYYY-MM-DD HH:mm:ss') : null)
            formData.startTime = times[0]
            formData.endTime = times[1]
            formData.planColor=this.primaryColor;
            // 转换 checkbox 的值
            formData.allDay = (formData.allDay === '1' ? '1' : '0')

            // 转换多选 select 的值
            formData.remindType = formData.remindType.join(',')

            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
            })


          }
        })
      },
      handleCancel() {
        this.close()
      },

      handleDelete() {
        this.confirmLoading = true
        deleteAction(this.url.delete, { id: this.model.id }).then(res => {
          if (res.success) {
            this.$message.success(res.message)
            this.$emit('ok')
            this.close()
          } else {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.confirmLoading = false
        })
      },
      changeColor(color){
        this.primaryColor=color;
      }
    }
  }
</script>

<style lang="less" scoped>
  .j-eoa-plan-field-remark {

    /deep/ button[title='插入/编辑图片'],
    /deep/ button[title='插入/编辑媒体'] {
      display: none;
    }

    /deep/ .tox.tox-tinymce {
      min-height: 200px;
    }

  }
  .setting-drawer-theme-color-colorBlock {
    width: 20px;
    height: 20px;
    border-radius: 2px;
    cursor: pointer;
    float: left;
    margin-right: 8px;
    padding-left: 0;
    padding-right: 0;
    text-align: center;
    color: #fff;
    font-weight: 700;
    margin-top: 10px;
    i {
      font-size: 14px;
    }
  }
</style>