<template>
<div  v-if="show">
  <div style="height: 100%;background-color: white">
    <div style="float:left;text-align:center;background-color: white;width: 100%">
      <h2 style="margin-top: 10px;">发件箱</h2>
    </div>

    <div style="margin-left: 1%;">
      <a-dropdown :trigger="['click']">
        <a-button class="ant-dropdown-link" icon="delete">删除 <a-icon type="down" /> </a-button>
        <a-menu slot="overlay">
          <a-menu-item value="删除" @click="deleteChange(1)">删除</a-menu-item>
          <a-divider style="margin:2px 0 !important;"/>
          <a-menu-item value="彻底删除" @click="deleteChange(3)">彻底删除</a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>
    <div>
      <s-table
        ref="table"
        :rowKey="(record) => record.id"
        size="default"
        :columns="columns"
        :data="loadData"
        :showAlertInfo="true"
        @onSelect="onChange"
        @clearAll="checkedList = []"
        style="margin-top: 12px;">
        <!-- 字符串超长截取省略号显示-->
        <span slot="action" slot-scope="text,record">
          <a @click="handClick(record)">
            <a-icon type="alert" title="紧急" style="color:red" v-if="record.urgentFlag == '1'"/>
            <a-icon type="rollback" title="已撤回" style="color:red" v-if="record.withdrawFlag == '1'"/>
            <j-ellipsis :value="text" :length="18"/>
          </a>
        </span>
      </s-table>
    </div>
  </div>
<eoaMailView-modal ref="eoaMailViewModal" @reply="handReply" @loadList="handLoad"></eoaMailView-modal>
</div>
</template>

<script>
  import { getAction,putAction } from '@/api/manage'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import store from '@/store'
  import EoaMailViewModal from "./EoaMailViewModal"
  import EoaEmailDraftsModal from "./EoaEmailDraftsModal"
  import JEllipsis from "@/components/jeecg/JEllipsis";
  import STable from '@/components/table/'

  export default {
    name: "EoaEmailInboxModel",
    components: {
      EoaMailViewModal,
      EoaEmailDraftsModal,
      JEllipsis,
      STable
    },
    data () {
      return {
        description:"发件箱展示页面",
        title:"查看邮件",
        indeterminate: false,
        checkAll: false,
        text:"上传",
        visible: false,
        checkedList:[],
        imageHref:"",
        model: {},
        list:"",
        fileList:[],
        show:false,
        spinLoading:false,
        loading:false,
        message:'',
        confirmLoading: false,
        plainOptions:[],
        form: this.$form.createForm(this),
        listData:[],
        url: {
          list:"/email/eoaMailboxInfo/list",
          userList: "/sys/user/list",
          updateEmailStatus:"/email/eoaMailboxInfo/updateEmailStatus",
        },
        columnId:null,
        treeData:[],
        headers:{},
        uploadFileUid:0,
        content:"",
        queryParam: {},
        // 表头
        columns: [
          {
            title: '标题',
            align: 'center',
            dataIndex: 'title',
            scopedSlots: { customRender: 'action' },
          },
          {
            title: '收件人',
            align: 'center',
            dataIndex: 'receiverIds_dictText'
          },
          {
            title: '日期',
            align: 'center',
            dataIndex: 'sendTime'
          },
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return getAction('/email/eoaMailboxInfo/list', {
            pageNo:parameter.pageNo,
            pageSize:parameter.pageSize,
            senderId:store.getters.userInfo.id,
            status:'2'
          }).then(res => {
            let result = res.result
            result.data = res.result.records;
            result.pageSize = result.size;
            result.pageNo = result.current;
            result.totalPage = result.pages;
            result.totalCount = result.total
            return result
          })
        },
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}

    },
    methods: {
      handLoad(){
        this.$refs.table.refresh();
      },
      handReply(record){
        this.$emit('reply',record);
      },
      onChange (row) {
        this.checkedList = row.selectedRowKeys
      },
      loadDate(){
        this.show=true;
      },
      deleteChange(flag){
        var title = flag == "3"?"彻底":"";
        var checkedList=this.checkedList;
        var that=this
        if(checkedList.length>0){
          this.$confirm({
            title: title + '删除',
            content: '确定要' + title + '删除吗',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
              putAction(that.url.updateEmailStatus,{checkedList:checkedList,delFlag:flag}).then((res) =>{
              if(res.success){
                that.$message.success(res.message)
                that.$refs.table.refresh();
                that.close();
              }else{
                that.$message.warning(res.message)
              }
            })
            }
          });

        }else{
          this.$message.warning("请选择一条数据！")
        }
      },
      load(){
        this.show=false
        this.indeterminate=false;
        this.checkAll=false;
        this.plainOptions=[];
        this.checkedList=[];
      },
      close(){
        this.show=false
        this.indeterminate=false;
        this.checkAll=false;
        this.plainOptions=[];
        this.checkedList=[];
        this.$emit('ok');
      },
      handClick(data){
       this.$refs.eoaMailViewModal.view(data)
      },

    }
  }
</script>

<style lang="less" scoped>
  .header1{
    width: 100%;
    text-align: center;
    display: flex;
    background-color: white;
    border-bottom: 1px solid #e7eaec;
    height: 42px;
  ; border-top: 1px solid #e7eaec;
    margin-top: 10px;
  }
  a{
    color: black;
    text-align: center;
  }
  #deleteBtn{
    background-color: transparent !important;
  }
  #deleteBtn:hover{
    color: #ff4d4f !important;
    border-color: #d9d9d9 !important;
  }
  #deleteBtn:focus{
    color: #ff4d4f !important;
    border-color: #d9d9d9 !important;
  }
  .ant-btn-primary:hover, .ant-btn-primary:focus {
    background-color: #FF0000;
    border-color: #FF0000;
    color: white;
  }
</style>