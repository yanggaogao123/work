<template>
  <j-modal
    title="考勤间隔"
    :visible="visible"
    :width="width"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :confirmLoading="clickFlag"
  >
    <a-spin :spinning="confirmLoading">
      <a-form ref="ruleForm" :form="form">
        <a-form-item label="企业" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <GCIOrgTreeSelect
            placeholder="请选择所属机构"
            :selectType="selectType"
            :isGetOption="isGetOption"
            @changeOptions="queryListRoute"
            v-decorator="['organId', { rules: [{ required: true, message: '请选择所属机构' }] }]"
          ></GCIOrgTreeSelect>
        </a-form-item>
        <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
<!--          <GCIRouteSelect-->
<!--            placeholder="请选择线路"-->
<!--            @change="changeRoute"-->
<!--            :width="routeWidth"-->
<!--            v-decorator="['routeId', { rules: [{ required: true, message: '请选择线路' }] }]"-->
<!--          ></GCIRouteSelect>-->
          <a-select
            allowClear
            placeholder="请选择线路"
            show-search
            :filter-option="filterOption"
            @change="changeRoute"
            v-decorator="['routeId', { rules: [{ required: true, message: '请选择线路' }] }]"
          >
            <a-select-option v-for="(item, index) in routeData" :key="index" :value="item.id">
              {{ item.routeName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="考勤时间间隔" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            style="width: 100px"
            v-decorator="['atndInvlDaily', { rules: [{ required: true, message: '请填写时间' }] }]"
            oninput="if(!/^[0-9]+$/.test(value)) value=value.replace(/\D/g,'');if(value.length>3)value=value.slice(0,3)"
            maxlength="3"
          ></a-input>
          小时
        </a-form-item>
        <a-form-item label="可连续上班天数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            style="width: 100px"
            v-decorator="['serialWorkDays', { rules: [{ required: true, message: '请填写时间' }] }]"
            oninput="if(!/^[0-9]+$/.test(value)) value=value.replace(/\D/g,'');if(value.length>3)value=value.slice(0,3)"
            maxlength="3"
          ></a-input>
          天
        </a-form-item>
        <a-form-item label="允许误差" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            style="width: 100px"
            v-decorator="['allowRange', { rules: [{ required: true, message: '请填写时间' }] }]"
            oninput="value=value.replace(/[^\-?\d.]/g,'');if(value.length>3)value=value.slice(0,3)"
            maxlength="3"
          ></a-input>
          分钟(可设置负数)
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
import { postAction } from '@/api/manage'
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
import GCIRouteSelect from '@/components/gci/GCIRouteSelect'
import {getAction} from "../../../api/manage";
import pick from "lodash.pick";

export default {
  name: 'BsAtndInvlOrganModal',
  components: {
    GCIOrgTreeSelect,
    GCIRouteSelect
  },
  data() {
    return {
      form: this.$form.createForm(this),
      queryOrgId: undefined,
      visible: false,
      clickFlag: false,
      selectType: 'id',
      isGetOption: true,
      confirmLoading: false,
      saveParam: {
        organId: '',
        routeId: '',
        atndInvlDaily: '',
        serialWorkDays: '',
        allowRange: ''
      },
      rules: {
        organId: [{ required: true, message: '请选择所属机构', trigger: 'blur' }],
        routeId: [{ required: true, message: '请选择线路', trigger: 'blur' }],
        atndInvlDaily: [{ required: true, message: '请填写时间', trigger: 'blur' }],
        serialWorkDays: [{ required: true, message: '请填写时间', trigger: 'blur' }],
        allowRange: [{ required: true, message: '请填写时间', trigger: 'blur' }]
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      width: 800,
      routeWidth: 500,
      routeData: [],
      url: {
        save: '/base/bsAtndInvlOrgan/save',
        listRoute: '/common/sys/queryRouteByOrganId'
      }
    }
  },
  methods: {
    queryListRoute(data) {
      this.routeData = [];
      this.form.setFieldsValue({'routeId':''})
      getAction(this.url.listRoute,{organId: data.id}).then(res => {
        this.routeData = res.result
      })
    },
    handleOk() {
      if(this.clickFlag == true){
        this.$message.warning('请勿重复点击')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log('Received values of form: ', values)
          values.id = this.saveParam.id
          this.clickFlag = true
          postAction(this.url.save, values)
            .then(res => {
              if (res.success) {
                this.$message.success(res.message)
                this.visible = false
                this.$emit('ok')
              } else {
                this.$message.warning(res.message)
              }
            })
            .finally(() => {
              this.format = false
              this.clickFlag = false
            })
        }
      })
    },
    handleCancel() {
      this.visible = false
    },
    add() {
      this.saveParam = {}
      this.form.resetFields()
      this.visible = true
    },
    edit(record) {
      this.queryListRoute({id:record.organId})
      this.saveParam.id = record.id
      this.form.resetFields()
      let model = Object.assign({}, record);
      this.visible = true;
      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(
            model,'id','organId','routeId','serialWorkDays','allowRange','atndInvlDaily','atndInvlSerial'
          ))
      })
    },
    changeRoute() {},
    handleEdit(e) {
      let value = e.replace(/[^\-\d]/g, '') // 只能输入-和数字
      value = value.replace(/\-{2,}/g, '-') // -只能保留一个
      value = value.replace(/(\d)\-/g, '$1') // 数字后面不能接-，不能出现类似-11-2,12-，11-23
      value = value.replace(/-(0+)/g, '0') // 不能出现-0,-001,-0001类似
      value = value.replace(/^0+(\d)/, '$1') // 第一位0开头，0后面为数字，则过滤掉，取后面的数字
      value = value.replace(/(-?\d{15})\d*/, '$1') // 最多保留15位整数
      this.height = value
    },
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
  }
}
</script>

<style scoped>
@import '../../../assets/less/common.less';
</style>
