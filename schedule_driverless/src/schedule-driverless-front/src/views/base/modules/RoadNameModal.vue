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
        <!--
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="所属部门">
          <a-input placeholder="请输入所属部门" v-decorator="['sysOrgCode'," />
        </a-form-item>-->
        <!--
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="路名ID ,主鍵,自增">
          <a-input placeholder="请输入路名ID ,主鍵,自增" v-decorator="['roadNameId', validatorRules.roadNameId ]" />
        </a-form-item> -->
        <!--
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="版本号">
          <a-input placeholder="请输入版本号" v-decorator="['version', validatorRules.version ]" />
        </a-form-item> -->
        <!--
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="起草人">
          <a-input placeholder="请输入起草人" v-decorator="['createUser', validatorRules.createUser ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="创建日期">
          <a-date-picker v-decorator="[ 'dateCreated', validatorRules.dateCreated ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="最近修改日期">
          <a-date-picker v-decorator="[ 'lastUpdated', {}]" />
        </a-form-item>-->
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="道路名">
          <a-input placeholder="请输入道路名" v-decorator="['roadName', validatorRules.roadName ]" />
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
    name: "RoadNameModal",
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
        roadNameId:{rules: [{ required: true, message: '请输入路名ID ,主鍵,自增!' }]},
        version:{rules: [{ required: true, message: '请输入版本号!' }]},
        createUser:{rules: [{ required: true, message: '请输入起草人!' }]},
        dateCreated:{rules: [{ required: true, message: '请输入创建日期!' }]},
        roadName:{rules: [{ required: true, message: '请输入道路名称!' }]},
        },
        url: {
          add: "/base/roadName/add",
          edit: "/base/roadName/edit",
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
          this.form.setFieldsValue(pick(this.model,'sysOrgCode','roadNameId','version','createUser','roadName'))
		  //时间格式化
          this.form.setFieldsValue({dateCreated:this.model.dateCreated?moment(this.model.dateCreated):null})
          this.form.setFieldsValue({lastUpdated:this.model.lastUpdated?moment(this.model.lastUpdated):null})
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
            formData.dateCreated = formData.dateCreated?formData.dateCreated.format():null;
            formData.lastUpdated = formData.lastUpdated?formData.lastUpdated.format():null;

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