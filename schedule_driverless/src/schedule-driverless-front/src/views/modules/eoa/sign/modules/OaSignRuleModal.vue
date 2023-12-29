<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="工作日，定义周几需要打卡 0=周日">
          <a-input placeholder="请输入工作日，定义周几需要打卡 0=周日" v-decorator="['workingDays', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="上班时间">
          <a-input placeholder="请输入上班时间" v-decorator="['onTime', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="上班迟到时间（上班时间后到迟到时间之前打卡算迟到）">
          <a-input placeholder="请输入上班迟到时间（上班时间后到迟到时间之前打卡算迟到）" v-decorator="['onTimeLate', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="上班旷工时间（该时间后打卡记为旷工）">
          <a-input placeholder="请输入上班旷工时间（该时间后打卡记为旷工）" v-decorator="['onTimeAbsent', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="下班打卡时间">
          <a-input placeholder="请输入下班打卡时间" v-decorator="['offTime', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="下班早退时间：该时间前打卡记为早退，反之记为正常打卡">
          <a-input placeholder="请输入下班早退时间：该时间前打卡记为早退，反之记为正常打卡" v-decorator="['offTimeEarly', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="元旦假期时间">
          <a-input placeholder="请输入元旦假期时间" v-decorator="['holidayYuanDan', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="春节假期时间">
          <a-input placeholder="请输入春节假期时间" v-decorator="['holidayChunJie', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="清明假期时间">
          <a-input placeholder="请输入清明假期时间" v-decorator="['holidayQingMing', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="五一劳动节假期时间">
          <a-input placeholder="请输入五一劳动节假期时间" v-decorator="['holidayLaoDong', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="端午假期时间">
          <a-input placeholder="请输入端午假期时间" v-decorator="['holidayDuanWu', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="中秋假期时间">
          <a-input placeholder="请输入中秋假期时间" v-decorator="['holidayZhongQiu', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="国庆假期时间">
          <a-input placeholder="请输入国庆假期时间" v-decorator="['holidayGuoQing', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="其他假期时间">
          <a-input placeholder="请输入其他假期时间" v-decorator="['holidayOther', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="不参与打卡人员">
          <a-input placeholder="请输入不参与打卡人员" v-decorator="['notjoining', {}]"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="组织机构编码">
          <a-input placeholder="请输入组织机构编码" v-decorator="['sysOrgCode', {}]"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@api/manage'
  import pick from 'lodash.pick'

  export default {
    name: 'OaSignRuleModal',
    data() {
      return {
        title: '操作',
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
          add: '/sign/oaSignRule/add',
          edit: '/sign/oaSignRule/edit',
        },
      }
    },
    created() {
    },
    methods: {
      add() {
        this.edit({})
      },
      edit(record) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'workingDays', 'onTime', 'onTimeLate', 'onTimeAbsent', 'offTime', 'offTimeEarly', 'holidayYuanDan', 'holidayChunJie', 'holidayQingMing', 'holidayLaoDong', 'holidayDuanWu', 'holidayZhongQiu', 'holidayGuoQing', 'holidayOther', 'notjoining', 'sysOrgCode'))
          //时间格式化
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

            console.log(formData)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              that.close()
            })


          }
        })
      },
      handleCancel() {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>