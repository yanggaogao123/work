<template>
  <j-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="obuId">
          <a-input placeholder="请输入obuId" v-decorator="['obuId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="dataSerial">
          <a-input placeholder="请输入dataSerial" v-decorator="['dataSerial', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeCode">
          <a-input placeholder="请输入routeCode" v-decorator="['routeCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="runningNo">
          <a-input placeholder="请输入runningNo" v-decorator="['runningNo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="obuTime">
          <a-date-picker v-decorator="[ 'obuTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="tripCode">
          <a-input placeholder="请输入tripCode" v-decorator="['tripCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="redisTime">
          <a-date-picker v-decorator="[ 'redisTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="tripLogId">
          <a-input placeholder="请输入tripLogId" v-decorator="['tripLogId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="busId">
          <a-input placeholder="请输入busId" v-decorator="['busId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="busCode">
          <a-input placeholder="请输入busCode" v-decorator="['busCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="busName">
          <a-input placeholder="请输入busName" v-decorator="['busName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="numberPlate">
          <a-input placeholder="请输入numberPlate" v-decorator="['numberPlate', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="organId">
          <a-input placeholder="请输入organId" v-decorator="['organId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="organCode">
          <a-input placeholder="请输入organCode" v-decorator="['organCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="organName">
          <a-input placeholder="请输入organName" v-decorator="['organName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeIdBelongTo">
          <a-input placeholder="请输入routeIdBelongTo" v-decorator="['routeIdBelongTo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeNameBelongTo">
          <a-input placeholder="请输入routeNameBelongTo" v-decorator="['routeNameBelongTo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeId">
          <a-input placeholder="请输入routeId" v-decorator="['routeId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeName">
          <a-input placeholder="请输入routeName" v-decorator="['routeName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="direction">
          <a-input placeholder="请输入direction" v-decorator="['direction', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeSubId">
          <a-input placeholder="请输入routeSubId" v-decorator="['routeSubId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="routeSubName">
          <a-input placeholder="请输入routeSubName" v-decorator="['routeSubName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="fromStationId">
          <a-input placeholder="请输入fromStationId" v-decorator="['fromStationId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="fromStationName">
          <a-input placeholder="请输入fromStationName" v-decorator="['fromStationName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="toStationId">
          <a-input placeholder="请输入toStationId" v-decorator="['toStationId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="toStationName">
          <a-input placeholder="请输入toStationName" v-decorator="['toStationName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="serviceNumber">
          <a-input placeholder="请输入serviceNumber" v-decorator="['serviceNumber', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="serviceName">
          <a-input placeholder="请输入serviceName" v-decorator="['serviceName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="employeeId">
          <a-input placeholder="请输入employeeId" v-decorator="['employeeId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="employeeName">
          <a-input placeholder="请输入employeeName" v-decorator="['employeeName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="qualification">
          <a-input placeholder="请输入qualification" v-decorator="['qualification', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="gpskey">
          <a-input placeholder="请输入gpskey" v-decorator="['gpskey', {}]" />
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "TriplogBeginModal",
    data () {
      return {
        title:"操作",
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
        validatorRules:{
        },
        url: {
          add: "/dy/triplogBegin/add",
          edit: "/dy/triplogBegin/edit",
        },
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'obuId','dataSerial','routeCode','runningNo','tripCode','tripLogId','busId','busCode','busName','numberPlate','organId','organCode','organName','routeIdBelongTo','routeNameBelongTo','routeId','routeName','direction','routeSubId','routeSubName','fromStationId','fromStationName','toStationId','toStationName','serviceNumber','serviceName','employeeId','employeeName','qualification','gpskey'))
		  //时间格式化
          this.form.setFieldsValue({obuTime:this.model.obuTime?moment(this.model.obuTime):null})
          this.form.setFieldsValue({redisTime:this.model.redisTime?moment(this.model.redisTime):null})
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            formData.obuTime = formData.obuTime?formData.obuTime.format():null;
            formData.redisTime = formData.redisTime?formData.redisTime.format():null;

            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>