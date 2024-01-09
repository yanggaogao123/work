<template>
  <div v-if="show">
  <div class="bodyForm" style="height: 100%;">
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
                style="width:86%;margin-top: 4px;"
             />
                <a-button type="primary" style="margin-left: 10px;margin-top: 4px" @click="selectUser" >选择</a-button>
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
              <j-upload  :text="text" v-model="fileList" class="j-eoa-plan-field-remark"></j-upload>
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
              <a-col :span="7">
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
        <div class="footer">
          <a-button type="primary" @click="send(1)">发送</a-button>
          <a-button type="primary" @click="send(2)" style="margin-left: 10px">发送继续</a-button>
          <a-button style="margin-left: 10px" @click="save">存为草稿</a-button>
        </div>
      </a-form>
    <eoaEmailUser-modal ref="eoaEmailUserModal" @ok="review"></eoaEmailUser-modal>
  </div>
  </div>
</template>

<script>
  import { httpAction,getAction,postAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import JEditor from '@/components/jeecg/JEditor.vue';
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import JUpload from '@/components/jeecg/JUpload'
  import Vue from 'vue'
  import store from '@/store'
  import EoaEmailUserModal from "./EoaEmailUserModal"
  export default {
    name: "EoaEmailBioModal",
    components: {
      JEditor,
      JUpload,
      EoaEmailUserModal
    },
    data () {
      return {
        description:"写信",
        title:"操作",
        text:"上传",
        visible: false,
        imageHref:"",
        emailTitle:"",
        realname:[],
        model: {},
        disabled:true,
        list:"",
        fileList:[],
        plainOptions:[
          { label: '是', value: '1' },
          { label: '否', value: '0' }
        ],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol1: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol1: {
          xs: { span: 24 },
          sm: { span: 15 },
        },
        loading:false,
        message:'',
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          receiverIds: {
            rules: [
              {required: true, message: '请选择收件人!'},
            ]
          }
        },
        url: {
          addEmail:"/email/eoaMailboxInfo/addEmail",
          saveEmail:"/email/eoaMailboxInfo/saveEmail",
        },
        columnId:null,
        treeData:[],
        headers:{},
        uploadFileUid:0,
        content:"",
        show:false,
        ids:"",
        urgentFlag:[],
        receiptFlag:[],
        replyReocrd:[],
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    methods: {
      loadDate(){
        this.show=true
        if(this.replyReocrd && this.replyReocrd.replyType){
          let title = '';
          let oldTitle = this.replyReocrd.title;
          if(this.replyReocrd.replyType == '1'){
            title = '转发：'+ oldTitle;
          }else if(this.replyReocrd.replyType == '2'){
            if(oldTitle.startsWith('回复：')){
              title = oldTitle;
            }else{
              title = '回复：' + oldTitle;
            }
            this.realname = this.replyReocrd.senderId_dictText
            this.ids = this.replyReocrd.senderId
          }else if(this.replyReocrd.replyType == '3'){
            //再次发送
            this.realname = this.replyReocrd.receiverIds_dictText
            this.ids = this.replyReocrd.receiverIds
            title = oldTitle
          }
          this.emailTitle = title;
          if(this.replyReocrd.replyType == '3'){
            this.content = this.replyReocrd.content;
          }else{
            this.content = "<br/><br/>------------------ 原始邮件 ------------------<br>发件人:"
              +this.replyReocrd.senderId_dictText
              +"</br><br>发送时间:"+this.replyReocrd.sendTime
              +"</br><br>收件人:"+this.replyReocrd.receiverIds_dictText
              +"</br><br>主题:"+this.replyReocrd.title
              +"</br>"+this.replyReocrd.content;
          }
        }
      },
      load(){
        this.show=false
        this.emailTitle=""
        this.realname=[]
        this.ids="";
        this.content="";
        this.emailTitle="";
        this.fileList=[];
        this.urgentFlag = [];
        this.receiptFlag = [];
      },
      send(flag){
        const that = this;
          if(this.realname){
            if(this.realname.length>0){
              if(!this.emailTitle){
                this.$message.warning("请填写标题！")
                return
              }
              let formData = {};
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
              }
              if(this.receiptFlag.length>0){
                formData.receiptFlag=this.receiptFlag[0];
              }
              postAction(this.url.addEmail,formData).then((res) =>{
                if(res.success){
                  if(flag == '1'){
                    this.$emit('ok')
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

          }else{
            this.$message.warning("请选择收件人！")
          }
      },
      selectUser(){
        let ids = []
        if(this.ids){
          ids = this.ids.split(",")
        }
        this.$refs.eoaEmailUserModal.loadDate(ids);
      },
      review(data){
        this.realname=data.realname
        this.ids=data.ids;
      },
      save(){
        if(this.realname) {
          if(this.realname.length>0){
            if(!this.emailTitle){
              this.$message.warning("请填写标题！")
              return
            }
            let formData = {};
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
            if (this.content != "") {
              formData.content = this.content
            }
            if (this.fileList) {
              if (this.fileList.length > 0) {
                formData.attachment = this.fileList
              }
            }
            formData.title = this.emailTitle;
            if(this.urgentFlag.length>0){
              formData.urgentFlag=this.urgentFlag[0];
            }
            if(this.receiptFlag.length>0){
              formData.receiptFlag=this.receiptFlag[0];
            }
            postAction(this.url.saveEmail, formData).then((res) => {
              if(res.success ) {
              this.$emit('ok')
              this.$message.success("保存成功！")
            }
          })
          }else{
            this.$message.warning("请选择收件人！")
          }
         }else{
         this.$message.warning("请选择收件人！")
         }
      }
    }
  }
</script>

<style lang="less" scoped>
  .bodyForm{
    background-color: white;
  }
  .footer{
    border-top: 1px solid #e7eaec;
    padding: 20px;
    text-align: right;
    background-color: #ffffff;
    border-top: 0;
    margin-bottom: 20px;
  }

  .send{
    background-color: #1ab394;
    border-color: #1ab394;
    color: #FFFFFF;
  }
  .save{
    color: inherit;
    background: white;
  }
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

  /deep/ button[title='插入/编辑媒体'] {
    display: none;
  }

  .j-eoa-plan-field-remark {
    /deep/ .tox.tox-tinymce {
      min-height: 200px;
    }
  }
</style>