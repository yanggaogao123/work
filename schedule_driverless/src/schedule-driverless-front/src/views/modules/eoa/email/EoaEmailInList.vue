<template>
  <div v-if="show">
    <div style="background-color: white">
      <div style="float:left;text-align:center;background-color: white;width: 100%">
        <h2 style="margin-top: 10px;">收件箱</h2>
      </div>
      <div style="display: inline-flex; margin-left: 10px;">
        <div>
          <a-dropdown :trigger="['click']">
            <a-button class="ant-dropdown-link" icon="edit">操作 <a-icon type="down" /> </a-button>
            <a-menu slot="overlay" @click="onClick">
              <a-menu-item value="取消标签" key="QXFL">取消标签</a-menu-item>
              <a-divider style="margin:2px 0 !important;"/>
              <a-menu-item v-for="item in mailboxTypeOptions" :key="item.value" :value="item.value">{{item.text}}</a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
        <div style="margin-left: 10px;">
          <a-dropdown :trigger="['click']">
            <a-button class="ant-dropdown-link" icon="tags">标记 <a-icon type="down" /> </a-button>
            <a-menu slot="overlay">
              <a-menu-item value="已读" @click="setReaded(1)">已读</a-menu-item>
              <a-menu-item value="未读" @click="setReaded(0)">未读</a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
        <div style="margin-left: 10px;">
          <a-dropdown :trigger="['click']">
            <a-button class="ant-dropdown-link" icon="delete">删除 <a-icon type="down" /> </a-button>
            <a-menu slot="overlay">
              <a-menu-item value="删除" @click="deleteChange(1)">删除</a-menu-item>
              <a-divider style="margin:2px 0 !important;"/>
              <a-menu-item value="彻底删除" @click="deleteChange(2)">彻底删除</a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
      </div>
      <div>
        <s-table
          ref="table"
          :rowKey="(record) => record.receiverId"
          size="default"
          :columns="columns"
          :data="loadData"
          :showAlertInfo="true"
          @onSelect="onChange"
          @clearAll="clearAll"
          style="margin-top: 12px;">
          <!-- 字符串超长截取省略号显示-->
          <span slot="action" slot-scope="text,record">
            <a @click="handClick(record)">
              <a-icon type="alert" title="紧急" style="color:red" v-if="record.urgentFlag == '1'"/>
              <j-ellipsis :value="text" :length="12"/>
            </a>
          </span>
          <span slot="tags" slot-scope="tags">
            <template v-for="tag in tags.slice(0,5)">
              <a-tag :color="tag.color" closable :key="tag.value" @close="handleClose(tag)">
                <span @click="tagChange(tag)"><j-ellipsis :value="tag.title" :length="4"/></span>
              </a-tag>
            </template>
            <template v-if="tags.length>5">
              <span>...</span>
            </template>
          </span>
        </s-table>
      </div>
    </div>
    <eoaEmailIn-modal ref="eoaEmailInModal" @ok="handOk" @reply="handReply"></eoaEmailIn-modal>
  </div>
</template>

<script>
  import { getAction,putAction } from '@/api/manage'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import store from '@/store'
  import EoaEmailInModal from "./modals/EoaEmailInModal"
  import JEllipsis from "@/components/jeecg/JEllipsis";
  import STable from '@/components/table/'

  export default {
    name: "EoaEmailInList",
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
        loading:false,
        message:'',
        confirmLoading: false,
        form: this.$form.createForm(this),
        listData:[],
        mailboxTypeOptions:[],
        url: {
          list:"/email/eoaMailboxInfo/sendList",
          userList: "/sys/user/list",
          updateEmailDelete:"/email/eoaMailboxInfo/updateEmailDelete",
          updateMailboxType: "/email/eoaMailboxInfo/updateMailboxType",
          mailboxCategory:"/email/eoaMailboxInfo/mailboxCategory",
          getLabelName:"/email/eoaMailboxCategory/getLabelName",
          updateBatchReadFlag:"/email/eoaMailboxInfo/updateBatchReadFlag",
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
        tags:[],
        color:["purple","red","orange","green","cyan","blue","pink"],
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
            title: '状态',
            align: 'center',
            dataIndex: 'readFlag',
            customRender: (t) => t === '0' ? '未读' : (t === '1' ? '已读' : t)
          },
          {
            title: '发件人',
            align: 'center',
            dataIndex: 'senderId_dictText',
            customRender:function (t,r) {
              if(!t){
                return r.senderId
              }else{
                return t;
              }
            }
          },
          {
            title: '日期',
            align: 'center',
            dataIndex: 'sendTime'
          },
          {
            title: '标签',
            align: 'center',
            dataIndex: 'tags',
            scopedSlots: { customRender: 'tags' },
          }
        ],
        // 加载数据方法 必须为 Promise 对象
        loadData: parameter => {
          return getAction('/email/eoaMailboxInfo/sendList', {
            pageNo:parameter.pageNo,
            pageSize:parameter.pageSize,
            userId:store.getters.userInfo.id,
            status:'0'
          }).then(res => {
            let result = res.result
            let listData = res.result.records
            if(listData){
              this.plainOptions=[];
              this.tags=[];
              var color=this.color;
              for(let i=0;i<listData.length;i++){
                var tool=[];
                if(listData[i].mailboxType){
                  var mailboxValue=listData[i].mailboxType.split(",");
                  let labelName = listData[i].labelName.split(",");
                  var j=0;
                  for(let k=0;k<mailboxValue.length;k++){
                    var tab={};
                    tab.value=mailboxValue[k];
                    tab.id=listData[i].receiverId;
                    if(j>=color.length){
                      j=0;
                    }
                    tab.title=labelName[k];
                    tab.color=color[j];
                    j++;
                    tool.push(tab);
                  }
                }
                listData[i].tags = tool;
              }
              this.listData=listData;
            }
            result.data = this.listData;
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
      this.initDictConfig();
    },
    methods: {
      handReply(record){
        this.$emit('reply',record);
      },
      setReaded(flag){
        var checkedList=this.checkedList;
        if(checkedList.length>0){
          //请求后台接口 e.key
          var param={};
          param.receiverId = checkedList.join(",");
          param.readFlag = flag;
          putAction(this.url.updateBatchReadFlag,param).then((res) =>{
            if(res.success){
              this.$message.success(res.message);
              this.$refs.table.refresh();
              this.onClearSelected();
            }
          })
        }else{
          this.$message.warning("请选择一条数据！")
        }
      },
      handleClose(e) {
        var param = {};
        param.id = e.id;
        param.mailboxValue = e.value;
        putAction(this.url.mailboxCategory, param).then((res) => {
          if (res.success) {
            this.$message.success(res.result);
            this.$refs.table.refresh();
          }
        })
      },
      initDictConfig() {
        getAction(this.url.getLabelName,{userId:store.getters.userInfo.id}).then((res) =>{
          if(res.success){
            var type=[];
            for(let i=0;i<res.result.length;i++){
              type.push({
                text: res.result[i].labelName,
                value:res.result[i].id
              })
            }
            this.mailboxTypeOptions=type;
          }
        })
      },
      onChange (row) {
        this.checkedList = row.selectedRowKeys
      },
      clearAll () {
        this.checkedList = []
      },
      loadDate(){
        this.show=true;
      },
      getList(){
        if(this.$refs.table){
          this.$refs.table.refresh();
        }
      },
      deleteChange(flag){
        var title = flag == "2"?"彻底":"";
        var checkedList=this.checkedList;
        var that=this
        if(checkedList.length>0){
          this.$confirm({
            title: title+'删除',
            content: '确定要'+title+'删除吗',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
              putAction(that.url.updateEmailDelete,{checkedList:checkedList,delFlag:flag}).then((res) =>{
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
        this.$refs.eoaEmailInModal.view(data);
      },
      handOk(){
        this.$refs.table.refresh();
      },
      onClick(e){
        var checkedList=this.checkedList;
        if(checkedList.length>0){
          //请求后台接口 e.key
          var param={};
          param.mailboxId=checkedList.join(",");
          param.mailboxValue=e.key;
          putAction(this.url.updateMailboxType,param).then((res) =>{
            if(res.success){
              this.$message.success(res.result);
              this.$refs.table.refresh();
            }
          })
        }else{
          this.$message.warning("请选择一条数据！")
        }
      },
      tagChange(value){
        var param=[];
        param.value=value.value;
        param.text=value.title;
        this.$emit('emailType',param);
      }
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
    border-top: 1px solid #e7eaec;
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