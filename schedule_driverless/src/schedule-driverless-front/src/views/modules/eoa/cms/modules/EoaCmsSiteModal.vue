<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-divider orientation="left" >站点配置</a-divider>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="站点名称">
          <a-input placeholder="请输入站点名称" v-decorator="['siteName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="站点模板">
          <a-input placeholder="请输入站点模板" v-decorator="['siteTemplateStyle', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="站点背景图片">
          <j-image-upload v-model="bgImg"></j-image-upload>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="站点Logo">
          <j-image-upload v-model="logoImg"></j-image-upload>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="站点轮播图">
          <j-image-upload v-model="carouselImg" isMultiple></j-image-upload>
        </a-form-item>
        <a-divider orientation="left" >分享配置</a-divider>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="分享给朋友标题">
          <a-input placeholder="请输入分享给朋友标题" v-decorator="['shareFriendTitle', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="分享给朋友描述">
          <a-input placeholder="请输入分享给朋友描述" v-decorator="['shareFriendDesc', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="分享到朋友圈标题">
          <a-input placeholder="请输入分享到朋友圈标题" v-decorator="['shareFriendCircle', {}]" />
        </a-form-item>
        <a-divider orientation="left" >公司信息</a-divider>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="公司电话">
          <a-input placeholder="请输入公司电话" v-decorator="['companyTel', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="经度">
          <a-input placeholder="请输入经度" v-decorator="['longitude', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="纬度">
          <a-input placeholder="请输入纬度" v-decorator="['latitude', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备案信息">
          <j-editor v-decorator="['recordInformation', {} ]" triggerChange></j-editor>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import JImageUpload from '../../../../../components/jeecg/JImageUpload'
  import JEditor from '../../../../../components/jeecg/JEditor'

  export default {
    name: "EoaCmsSiteModal",
    components: { JEditor, JImageUpload },
    data () {
      return {
        bgImg:[],
        logoImg:[],
        carouselImg:[],
        fileType:"image",
        recordInformation:'',
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
          add: "/cms/eoaCmsSite/add",
          edit: "/cms/eoaCmsSite/edit",
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
          this.form.setFieldsValue(pick(this.model,'companyTel','siteLogo','siteName','siteTemplateStyle','recordInformation','longitude','latitude','jwid','hdurl','shortUrl','projectCode','shareFriendTitle','shareFriendDesc','shareFriendCircle','appid','siteBackgroundImg'))
          //时间格式化
        });
        if(this.model.id){
          setTimeout(() => {
            this.logoImg = record.siteLogo
            this.bgImg = record.siteBackgroundImg
            this.carouselImg = record.carouselImg
          }, 5)
        }
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
            formData.siteLogo = this.logoImg;
            formData.siteBackgroundImg = this.bgImg
            formData.carouselImg = this.carouselImg;
            //时间格式化
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