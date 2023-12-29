<template>
  <div  v-if="show">
    <div style="height: 100%;background-color: white">
      <div style="float:left;text-align:center;background-color: white;width: 100%">
        <h2 style="margin-top: 10px;">垃圾箱</h2>
      </div>
      <div style="display: inline-flex; margin-left: 10px;">
        <div>
          <a-button type="primary" @click="revertBtn"><a-icon type="rollback"/>恢复</a-button>
        </div>
        <div style="margin-left: 10px;">
          <a-button type="primary" @click="deleteChange"><a-icon type="delete"/>删除</a-button>
        </div>
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
              <j-ellipsis :value="text" :length="18"/>
            </a>
          </span>
        </s-table>
      </div>
    </div>
    <EoaEmailIn-modal ref="eoaEmailInModal" @reply="handReply"></EoaEmailIn-modal>
  </div>
</template>

<script>
  import { getAction,postAction } from '@/api/manage'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import store from '@/store'
  import EoaEmailInModal from "./modals/EoaEmailInModal"
  import JEllipsis from "@/components/jeecg/JEllipsis";
  import STable from '@/components/table/'

  export default {
    name: "EoaMialDustbinList",
    components: {
      EoaEmailInModal,
      JEllipsis,
      STable
    },
    data () {
      return {
        description:"",
        title:"操作",
        text:"上传",
        visible: false,
        imageHref:"",
        model: {},
        list:"",
        fileList:[],
        show:false,
        spinLoading:false,
        loading:false,
        message:'',
        confirmLoading: false,
        form: this.$form.createForm(this),
        listData:[],
        url: {
          list:"/email/eoaMailboxInfo/selectDustbin",
          userList: "/sys/user/list",
          updateBatch:"/email/eoaMailboxInfo/updateBatch",
          revertList:"/email/eoaMailboxInfo/revertList",
        },
        columnId:null,
        treeData:[],
        headers:{},
        uploadFileUid:0,
        content:"",
        indeterminate: false,
        checkAll: false,
        plainOptions:[],
        checkedList:[],
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
            dataIndex: 'senderId_dictText'
          },
          {
            title: '日期',
            align: 'center',
            dataIndex: 'sendTime'
          },
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return getAction('/email/eoaMailboxInfo/selectDustbin', {
            pageNo:parameter.pageNo,
            pageSize:parameter.pageSize,
            senderId:store.getters.userInfo.id,
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
      handReply(record){
        this.$emit('reply',record);
      },
      onChange (row) {
        this.checkedList = row.selectedRows
      },
      loadDate(){
        this.show=true
      },
      deleteChange(){
        var checkedList=this.checkedList;
        var that=this
        var id="";
        if(checkedList.length>0){
          checkedList=JSON.stringify(checkedList)
          this.$confirm({
            title: '删除',
            content: '确定要删除吗',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
              postAction(that.url.updateBatch,{checkedList:checkedList}).then((res) =>{
              if(res.success){
                that.$message.success(res.message)
                that.$refs.table.refresh();
                that.$refs.table.onClearSelected();
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
      revertBtn(){
        var checkedList=this.checkedList;
        if(checkedList.length>0){
          checkedList=JSON.stringify(checkedList)
          postAction(this.url.revertList,{checkedList:checkedList}).then((res) =>{
          if(res.success){
            this.$message.success(res.message)
            this.$refs.table.refresh();
            this.$refs.table.onClearSelected();
            this.close();
          }else{
            this.$message.warning(res.message)
          }
        })
        }else{
          this.$message.warning("请选择一条数据！")
        }
      },
      close(){
        this.show=false
        this.indeterminate=false;
        this.checkAll=false;
        this.plainOptions=[];
        this.checkedList=[];
        this.$emit('ok');
      },
      load(){
        this.show=false;
        this.indeterminate=false;
        this.checkAll=false;
        this.plainOptions=[];
        this.checkedList=[];
      },
      handClick(data){
        this.$refs.eoaEmailInModal.view(data)
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
</style>
