<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel">
    <template slot="footer">
      <a-button type="primary" @click="handleReply(1)">转发</a-button>
      <a-button type="primary" @click="handleReply(2)" v-if="replyReocrd.senderId != '系统通知'">回复</a-button>
      <a-button type="primary" @click="handleReply(3)" v-if="replyReocrd.type && replyReocrd.type=='1'">再次发送</a-button>
      <a-button key="back" @click="handleCancel">关闭</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <div class="body">
        <div style="text-align: left">
          <h3>主题：{{emailTitle}}</h3>
        </div>
        <div style="display: flex">
          <div >
            <template v-for="tag in tags">
              <a-tag :color="tag.color" :key="tag.value" style="margin-top: 10px;">
                <span >{{tag.title}}</span>
              </a-tag>
            </template>
          </div>
        </div>
        <div style="margin-top: 10px" v-if="tags.length>0"></div>
        <div style="display: flex;border-bottom: 1px solid #e8e8e8;">
          <div style="text-align: left;width: 50%">
            <span>发件人：{{realname}}</span>
          </div>
          <div style="text-align: right;width: 50%;" class="author">
            <span>  {{sendTime}}</span>
          </div>
        </div>
        <div style="margin-top: 20px;">
          <div v-html="content" v-if="replyReocrd.senderId != '系统通知'"></div>
          <div v-if="replyReocrd.senderId == '系统通知'">
            <a-table
              ref="table"
              size="small"
              bordered
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource"
              :pagination="ipagination">
            </a-table>
          </div>
        </div>
        <div v-if="fileList&&fileList.length>0">
          <j-upload :text="text" v-model="fileList" :disabled="disabled" :value="fileList" :buttonVisible="!disabled"></j-upload>
        </div>
      </div>
    </a-spin>
    <eoa-email-bio-modal ref="eoaEmailBioModal"></eoa-email-bio-modal>
  </a-modal>
</template>

<script>
  import { postAction,getAction,putAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import {duplicateCheck } from '@/api/api'
  import JUpload from '@/components/jeecg/JUpload'
  import { axios } from '@/utils/request'
  import store from '@/store'
  import EoaEmailBioModal from './EoaEmailBioModal'

  export default {
    name: "EoaEmailInModal",
    components: {
      EoaEmailBioModal,
      JUpload
    },
    inject:['closeCurrent'],
    data () {
      return {
        description:"收件箱和垃圾箱详情页面",
        title:"查看邮件",
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
        emailTitle:"",
        sendTime:"",
        content:"",
        urgentFlag:"",
        color:["purple","red","orange","green","cyan","blue","pink"],
        tags:[],
        url: {
          sendList:"/email/eoaMailboxInfo/sendList",
          userList: "/sys/user/list",
          updateReadFlag:"/email/eoaMailboxInfo/updateReadFlag",
          addEmail:"/email/eoaMailboxInfo/addEmail",
          list:"/email/eoaMailboxInfo/receiverPageList",
        },
        replyReocrd:[],
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '收件人',
            align: 'center',
            dataIndex: 'userId_dictText'
          },
          {
            title: '撤回状态',
            align: 'center',
            dataIndex: 'withdrawFlag',
            customRender: (t) => t === '1' ? '成功' : (t === '2' ? '失败' : (t === '0' ? '' : t))
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'isDelete',
            customRender:function(t,r){
              if(r.withdrawFlag=='2'){
                return "对方可能已经阅读或删除该邮件，无法撤回。";
              }else{
                return "";
              }
            }
          }
        ],
        dataSource:[],
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    methods: {
      loadData(arg) {
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        getAction(this.url.list, {mailId:this.replyReocrd.content}).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
        })
      },
      handleReply(type){
        this.close();
        //1为转发  2为回复
        this.replyReocrd.replyType = type;
        this.$emit('reply',this.replyReocrd);
      },
      handleReceipt(record){
        //回执邮件
        let formData = {};
        formData.receiverIds = record.senderId;
        formData.senderId = store.getters.userInfo.id;
        formData.content = '您发送的这封邮件已经被打开。';
        formData.title = "回执：" + record.title;
        postAction(this.url.addEmail,formData).then((res) =>{})
      },
      view (record) {
        this.replyReocrd = record;
        //如果邮件需要回执，则发一封回执邮件
        if(record.receiptFlag == '1' && record.readFlag == '0' ){
          this.handleReceipt(record);
        }
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible=true
        this.confirmLoading=true;
        putAction(this.url.updateReadFlag,{ receiverId:record.receiverId}).then((res) =>{})
        if(this.replyReocrd.senderId == '系统通知'){
          this.loadData(1);
        }
        getAction(this.url.sendList,{id:record.id}).then((res) =>{
            if(res.success){
                this.emailTitle=res.result.records[0].title;
                this.content=res.result.records[0].content;
                if(res.result.records[0].senderId_dictText){
                    this.realname=res.result.records[0].senderId_dictText;
                }else{
                    this.realname="系统发布";
                }
                this.fileList=res.result.records[0].attachment;
                this.sendTime=res.result.records[0].sendTime;
                this.urgentFlag = res.result.records[0].urgentFlag;
                var tool=[];
                var j=0;
                var color=this.color;
                if(res.result.records[0].labelName){
                    var labelName=res.result.records[0].labelName.split(",");
                    var mailboxValue = res.result.records[0].categoryId.split(",");
                    for(let k=0;k<mailboxValue.length;k++){
                        var tab={};
                        tab.value=mailboxValue[k];
                        tab.id=res.result.records[0].receiverId;
                        if(j>=color.length){
                            j=0;
                        }
                        tab.title=labelName[k];
                        tab.color=color[j];
                        j++;
                        tool.push(tab);
                    }
                }

                this.tags=tool;
            }
            this.confirmLoading=false;
      })
      },
      close () {
        this.visible = false;
        this.$emit('close');
        this.$emit('ok');
        this.fileList=[];
        this.emailTitle=''
        this.content=""
        this.realname=[];
        this.tags=[];
      },
      handleOk () {
        this.close()
      },
      handleCancel () {
        this.close()
      },
  }
  }
</script>

<style lang="less" >
  .title{
    font-size: 1.17em;
    word-wrap: break-word
  }
  .body{
    position: relative;
    /*padding: 0 76px 16px 0;*/
  }
  div{
    padding: 0;
    margin: 0;
  }
  .author{
    height: 28px;
    color: #858585;
    overflow: hidden;
    text-align: right;
  }
  .summary{
    color: rgba(0,0,0,.75);
    font-variant-ligatures: common-ligatures;
    line-height: 1.625;
  }
  p{
    margin-bottom: 2em;
  }
</style>