<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel">
    <template slot="footer">
      <a-button type="primary" @click="handleOk(1)">发送</a-button>
      <a-button type="primary" @click="handleOk(2)" style="margin-left: 10px">发送继续</a-button>
      <a-button key="submit" type="primary" @click="save">保存</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <a-form :form="form" style="padding-top: 20px">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="发送到"
        >
          <div style="display: flex">
            <a-select
              mode="multiple"
              placeholder="请选择收件人"
              v-model="realname"
              :disabled="disabled"
              style="width:90%;margin-top: 4px;"
            />
            <a-button type="primary" @click="selectUser" style="margin-left: 12px;margin-top: 4px;">选择</a-button>
          </div>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="标题">
          <a-textarea placeholder="请输入标题" v-model="emailTitle"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="内容">
          <j-editor
            v-model="content"
            ref="editor" style="width: 100%;"></j-editor>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="附件上传">
          <j-upload  :text="text" v-model="fileList"></j-upload>
        </a-form-item>
        <a-row class="form-row" :span="24">
          <a-col :span="5" :offset="4">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="紧急">
              <a-checkbox-group v-model="urgentFlag">
                <a-checkbox value="1"></a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col>
          <a-col :span="7" :offset="3">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="需要回执">
              <a-checkbox-group v-model="receiptFlag">
                <a-checkbox value="1" ></a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <eoaEmailUser-modal ref="eoaEmailUserModal" @ok="review"></eoaEmailUser-modal>
  </a-modal>
</template>

<script>
  import { httpAction,getAction,postAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import store from '@/store'
  import {duplicateCheck } from '@/api/api'
  import JUpload from '@/components/jeecg/JUpload'
  import { axios } from '@/utils/request'
   import EoaEmailUserModal from "./EoaEmailUserModal"
  import JEditor from '@/components/jeecg/JEditor.vue';
  export default {
    name: "EoaEmailDraftsModal",
    components: {
      JUpload,
      EoaEmailUserModal,
      JEditor
    },
    data () {
      return {
        description:"写邮件",
        title:"写邮件",
        visible: false,
        text:"上传",
        previewVisible: false,
        disabled:true,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        headers:{
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        parentId:null,
        fileList:[],
        show:true,
        realname:[],
        status:"",
        ids:"",
        emailTitle:"",
        content:"",
        url: {
          list:"/email/eoaMailboxInfo/list",
          userList: "/sys/user/list",
          addEmail:"/email/eoaMailboxInfo/addEmail",
          saveEmail:"/email/eoaMailboxInfo/saveEmail",
        },
        urgentFlag:[],
        receiptFlag:[],
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    methods: {
      loadDate (record) {
        this.form.resetFields();
        this.model = Object.assign({},record);
        this.visible=true
        this.confirmLoading=true;
        getAction(this.url.list,{id:record.id}).then((res) =>{
            if(res.success){
                this.emailTitle=res.result.records[0].title;
                var content=res.result.records[0].content;
                this.realname=res.result.records[0].receiverIds_dictText.split(",");
                this.content=content;
                this.fileList=res.result.records[0].attachment;
                this.ids=res.result.records[0].receiverIds;
                this.urgentFlag.push(res.result.records[0].urgentFlag);
                this.receiptFlag.push(res.result.records[0].receiptFlag);
            }
          this.confirmLoading=false;
      })
      },
      review(data){
        this.realname=data.realname
        this.ids=data.ids;
      },
      close () {
        this.fileList=[];
        this.emailTitle=''
        this.content=""
        this.realname=[]
        this.visible = false;
        this.urgentFlag = [];
        this.receiptFlag = [];
      },
     handleCancel(){
         this.$emit('close');
         this.close();
     },
      handleOk (flag) {
        if(this.realname){
          let formData = Object.assign(this.model);
          if(!this.emailTitle){
            this.$message.warning("请填写标题！")
            return
          }
          if(this.ids!=""){
            if(typeof this.ids=='string'){
              formData.receiverIds=this.ids;
            }else{
              var ids="";
              this.ids.forEach(function (val) {
                ids += val + ",";
              });
              ids=ids.substr(0,ids.length-1);
              formData.receiverIds=ids;
            }

          }
          formData.senderId= store.getters.userInfo.id
          if(this.content!=""){
            formData.content=this.content
          }
          if (this.fileList) {
            if(this.fileList.length>0){
              formData.attachment = this.fileList
            }
          }
          formData.title=this.emailTitle;
          if(this.urgentFlag.length>0){
            formData.urgentFlag=this.urgentFlag[0];
          }else{
            formData.urgentFlag='';
          }
          if(this.receiptFlag.length>0){
            formData.receiptFlag=this.receiptFlag[0];
          }else{
            formData.receiptFlag='';
          }
          postAction(this.url.addEmail,formData).then((res) =>{
            if(res.success){
              if(flag == '1'){
                this.$emit('ok')
                this.close()
              }else{
                this.realname=[]
                this.ids="";
                this.content="";
                this.emailTitle="";
                this.fileList=[];
                this.urgentFlag = [];
                this.receiptFlag = [];
              }
              this.$message.success("发送成功！")
          }
        })
        }else{
          this.$message.warning("请选择收件人！")
        }

      },
        save () {
        if(this.realname) {
          if(!this.emailTitle){
            this.$message.warning("请填写标题！")
            return
          }
          let formData = Object.assign(this.model);
          if(this.ids!=""){
            if(typeof this.ids=='string'){
              formData.receiverIds=this.ids;
            }else{
              var ids="";
              this.ids.forEach(function (val) {
                ids += val + ",";
              });
              ids=ids.substr(0,ids.length-1);
              formData.receiverIds=ids;
            }
          }
          formData.senderId = store.getters.userInfo.id
          if(this.content!=""){
            formData.content=this.content
          }
          if (this.fileList) {
            if(this.fileList.length>0){
              formData.attachment = this.fileList
            }
          }
            formData.title = this.emailTitle;
          if(this.urgentFlag.length>0){
            formData.urgentFlag=this.urgentFlag[0];
          }else{
            formData.urgentFlag = '';
          }
          if(this.receiptFlag.length>0){
            formData.receiptFlag=this.receiptFlag[0];
          }else{
            formData.receiptFlag = '';
          }
          postAction(this.url.saveEmail, formData).then((res) => {
            if(res.success ) {
            this.$emit('close');
            this.close()
            this.$message.success("保存成功！")
          }
        })
        }else{
          this.$message.warning("请选择收件人！")
        }

      },
      selectUser(){
        this.$refs.eoaEmailUserModal.loadDate(this.ids.split(","));
      },
  }
  }
</script>

<style lang="less" scoped>
  .comment{
    border: 1px solid #e7eaec;
    padding: 5px 10px;
    font-size: 12px;
    line-height: 1.5;
    border-radius: 5px;
  }
  .btn{
    padding: 5px 10px;
    font-size: 12px;
    line-height: 1.5;
    color: white;
    background-color: blue;
    margin-left: 10px;
  }
</style>