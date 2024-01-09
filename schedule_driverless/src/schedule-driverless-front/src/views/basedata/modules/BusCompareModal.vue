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
          label="edBusId">
          <a-input placeholder="请输入edBusId" v-decorator="['edBusId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsBusId">
          <a-input placeholder="请输入bsBusId" v-decorator="['bsBusId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markBusId">
          <a-input placeholder="请输入markBusId" v-decorator="['markBusId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edNumberPlate">
          <a-input placeholder="请输入edNumberPlate" v-decorator="['edNumberPlate', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsNumberPlate">
          <a-input placeholder="请输入bsNumberPlate" v-decorator="['bsNumberPlate', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markNumberPlate">
          <a-input placeholder="请输入markNumberPlate" v-decorator="['markNumberPlate', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edBusCode">
          <a-input placeholder="请输入edBusCode" v-decorator="['edBusCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsBusCode">
          <a-input placeholder="请输入bsBusCode" v-decorator="['bsBusCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markBusCode">
          <a-input placeholder="请输入markBusCode" v-decorator="['markBusCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edObuId">
          <a-input placeholder="请输入edObuId" v-decorator="['edObuId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsObuId">
          <a-input placeholder="请输入bsObuId" v-decorator="['bsObuId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markObuId">
          <a-input placeholder="请输入markObuId" v-decorator="['markObuId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edOrganName">
          <a-input placeholder="请输入edOrganName" v-decorator="['edOrganName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsOrganName">
          <a-input placeholder="请输入bsOrganName" v-decorator="['bsOrganName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markOrganName">
          <a-input placeholder="请输入markOrganName" v-decorator="['markOrganName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edRouteName">
          <a-input placeholder="请输入edRouteName" v-decorator="['edRouteName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsRouteName">
          <a-input placeholder="请输入bsRouteName" v-decorator="['bsRouteName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markRouteName">
          <a-input placeholder="请输入markRouteName" v-decorator="['markRouteName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edBusType">
          <a-input placeholder="请输入edBusType" v-decorator="['edBusType', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsBusType">
          <a-input placeholder="请输入bsBusType" v-decorator="['bsBusType', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markBusType">
          <a-input placeholder="请输入markBusType" v-decorator="['markBusType', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edBusStatus">
          <a-input placeholder="请输入edBusStatus" v-decorator="['edBusStatus', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsBusStatus">
          <a-input placeholder="请输入bsBusStatus" v-decorator="['bsBusStatus', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markBusStatus">
          <a-input placeholder="请输入markBusStatus" v-decorator="['markBusStatus', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edObuInfo">
          <a-input placeholder="请输入edObuInfo" v-decorator="['edObuInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsObuInfo">
          <a-input placeholder="请输入bsObuInfo" v-decorator="['bsObuInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markObuInfo">
          <a-input placeholder="请输入markObuInfo" v-decorator="['markObuInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edSimInfo">
          <a-input placeholder="请输入edSimInfo" v-decorator="['edSimInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsSimInfo">
          <a-input placeholder="请输入bsSimInfo" v-decorator="['bsSimInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="markSimInfo">
          <a-input placeholder="请输入markSimInfo" v-decorator="['markSimInfo', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="edLastUpdated">
          <a-date-picker v-decorator="[ 'edLastUpdated', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="bsLastUpdated">
          <a-date-picker v-decorator="[ 'bsLastUpdated', {}]" />
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
    name: "BusCompareModal",
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
          add: "/basedata/busCompare/add",
          edit: "/basedata/busCompare/edit",
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
          this.form.setFieldsValue(pick(this.model,'edBusId','bsBusId','markBusId','edNumberPlate','bsNumberPlate','markNumberPlate','edBusCode','bsBusCode','markBusCode','edObuId','bsObuId','markObuId','edOrganName','bsOrganName','markOrganName','edRouteName','bsRouteName','markRouteName','edBusType','bsBusType','markBusType','edBusStatus','bsBusStatus','markBusStatus','edObuInfo','bsObuInfo','markObuInfo','edSimInfo','bsSimInfo','markSimInfo'))
		  //时间格式化
          this.form.setFieldsValue({edLastUpdated:this.model.edLastUpdated?moment(this.model.edLastUpdated):null})
          this.form.setFieldsValue({bsLastUpdated:this.model.bsLastUpdated?moment(this.model.bsLastUpdated):null})
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
            formData.edLastUpdated = formData.edLastUpdated?formData.edLastUpdated.format():null;
            formData.bsLastUpdated = formData.bsLastUpdated?formData.bsLastUpdated.format():null;

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