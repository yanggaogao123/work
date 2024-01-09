<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel">
    <template slot="footer">
      <a-button type="primary" @click="handleReply(1)">转发</a-button>
      <a-button type="primary" @click="handleWithdraw()" v-if="this.replyReocrd.withdrawFlag != '1'">撤回</a-button>
      <a-button type="primary" @click="handleReply(3)">再次发送</a-button>
      <a-button key="back" @click="handleCancel">关闭</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <div class="body">
      <div style="text-align: left">
        <h3>主题：{{emailTitle}}<a @click="readClick()" style="margin-left: 8px;"><a-icon type="eye"/></a></h3>
      </div>
      <div style="display: flex">
        <div style="width: 50%;text-align: left">
          <span>收件人：{{realname}}</span>
        </div>
        <div style="width: 50%;text-align: right" class="author">
          <span>  {{sendTime}}</span>
        </div>
      </div>
      <div v-if="this.replyReocrd.withdrawFlag == '1'">
        <div style="width: 50%;text-align: left">
          <span>撤回状态：已撤回</span>
          <a @click="handleDetail()" style="margin-left: 8px">
            {{ detailStatus ? '关闭' : '查看详情' }}
          </a>
        </div>
        <div v-if="detailStatus">
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
      <div style="margin-top: 20px;border-top: 1px solid #e0e0e0;">
        <div v-html="content"></div>
      </div>
      <div v-if="fileList&&fileList.length>0">
        <j-upload  :text="text" v-model="fileList" :disabled="disabled" :buttonVisible="!disabled"></j-upload>
      </div>
    </div>
    </a-spin>
    <eoa-email-read-modal ref="readState"></eoa-email-read-modal>
  </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import {duplicateCheck } from '@/api/api'
  import JUpload from '@/components/jeecg/JUpload'
  import { axios } from '@/utils/request'
  import EoaEmailReadModal from './EoaEmailReadModal'
  export default {
    name: "EoaMailViewModal",
    components: {
      EoaEmailReadModal,
      JUpload
    },
    data () {
      return {
        description:"发件箱详情页面",
        title:"查看邮件",
        visible: false,
        text:"上传",
        previewVisible: false,
        disabled:true,
        model: {},
        sendTime:"",
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
        realname:"",
        status:"",
        emailTitle:"",
        content:"",
        url: {
          list:"/email/eoaMailboxInfo/list",
          userList: "/sys/user/list",
          emailWithdraw:"/email/eoaMailboxInfo/emailWithdraw",
          receiverPageList:"/email/eoaMailboxInfo/receiverPageList",
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
        detailStatus:false
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    methods: {
      readClick(){
        this.$refs.readState.loadReader(this.replyReocrd.id);
      },
      handleDetail(){
        this.detailStatus = !this.detailStatus;
      },
      loadData(arg) {
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        getAction(this.url.receiverPageList, {mailId:this.replyReocrd.id}).then((res) => {
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
        //1为转发  2为回复 3为再次发送
        this.replyReocrd.replyType = type;
        this.$emit('reply',this.replyReocrd);
      },
      handleWithdraw(){
        var that = this;
        this.$confirm({
          title: '撤回',
          content: '确定要撤回吗；如果对方已读则不可撤回；如果撤回成功，对方将只能看到邮件的主题，并得到已被撤回的提示',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          onOk() {
            getAction(that.url.emailWithdraw,{id:that.replyReocrd.id}).then((res) =>{
              if(res.success){
                if(res.result == '2'){
                  that.$message.success("撤回部分邮件成功")
                }else if(res.result == '1'){
                  that.$message.success("撤回邮件成功")
                }else{
                  that.$message.error("撤回邮件失败，对方可能已阅读或者删除邮件，无法撤回")
                }
                that.handleOk()
                //刷新列表
                that.$emit('loadList');
              }
            })
          }
        });
      },
      view (record) {
        this.replyReocrd = record;
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.detailStatus=false
        this.visible=true
        this.confirmLoading=true;
        if(record.withdrawFlag == '1'){
          this.loadData(1);
        }
        getAction(this.url.list,{id:record.id}).then((res) =>{
            if(res.success){
                this.emailTitle=res.result.records[0].title;
                this.sendTime=res.result.records[0].sendTime;
                var content=res.result.records[0].content;
                this.realname=res.result.records[0].receiverIds_dictText;
                this.content=content;
                this.fileList=res.result.records[0].attachment
            }
          this.confirmLoading=false;
        })
      },
      close () {
        this.visible = false;
        this.$emit('close');
        this.fileList=[];
        this.emailTitle=''
        this.content=""
        this.realname=[]
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

<style lang="less" scoped>

</style>