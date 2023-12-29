<template>
  <div  v-if="show">
    <div style="background-color: white">
      <div style="float:left;text-align:center;background-color: white;width: 100%">
        <h2 style="margin-top: 10px;">{{title}}</h2>
      </div>
      <div style="display: inline-flex; margin-left: 1%;">
        <div >
          <a-button type="primary" @click="deleteChange"><a-icon type="delete"/>删除</a-button>
        </div>
      </div>

      <div class="header1">
        <div style="margin-top: 10px;margin-left: 12px;">
          <a-checkbox
            @change="onCheckAllChange"
            :checked="checkAll"
            :indeterminate="indeterminate"
          />
        </div>
        <div style="margin-top: 10px;width: 34%;">
          <span>
           标题
          </span>
        </div>
        <div style="width: 12%;margin-top: 10px">
          <span>
           状态
          </span>
        </div>
        <div style="width: 25%;margin-top: 10px">
          <span>
          发件人
          </span>
        </div>
        <div style="width: 19%;margin-top: 10px;">
          <span>
          日期
          </span>
        </div>
      </div>
      <div style="background-color: white">
        <a-list
          size="large"
          :pagination="pagination"
          :dataSource="listData"
          :loading="loading"
        >
          <a-list-item slot="renderItem" slot-scope="item, index" key="item.title">
            <div style="margin-left: 12px;">
              <a-checkbox-group :options=[{value:item.receiverId}] v-model="checkedList" @change="onChange" />
            </div>
            <a style="width: 100%;display: flex" @click="handClick(item)">
              <div style="width: 31%;margin-left: 2%;">
          <span>
            <j-ellipsis :value="item.title" :length="18"/>
           </span>
              </div>
              <div style="width: 9%;margin-left:4%;">
          <span>
          {{item.readFlagText}}
          </span>
              </div>
              <div style="width: 21%;margin-left:4%">
          <span>
          {{item.senderId_dictText}}
          </span>
              </div>
              <div style="width:18%;text-align: center;margin-left: 3%">
          <span>
             {{item.sendTime.substring(0,10)}}
          </span>
              </div>
            </a>
          </a-list-item>
        </a-list>
      </div>
      <div style="height: 20px"></div>
  </div>
    <eoaEmailIn-modal ref="eoaEmailInModal" @ok="handOk"></eoaEmailIn-modal>
  </div>
</template>

<script>
    import { httpAction,getAction,putAction } from '@/api/manage'
    import { ACCESS_TOKEN } from "@/store/mutation-types"
    import Vue from 'vue'
    import store from '@/store'
    import JEllipsis from "@/components/jeecg/JEllipsis";
    import EoaEmailInModal from "./EoaEmailInModal"
    export default {
        name: "EoaEmailTypeModal",
        components: {
            JEllipsis,
            EoaEmailInModal
        },
        data () {
            return {
                description:"",
                title:"",
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
                pagination: {
                    current: 1,
                    showSizeChanger: true,
                    showQuickJumper: true,
                    pageSize: 10,
                    pageSizeOptions: ['10','20','30'],
                    total: 0,
                    onChange: (page,pageSize) => {
                        this.boxChange(page,pageSize);
                    },
                    onShowSizeChange:(current,pageSize) =>{
                        this.boxChange(current,pageSize);
                    },
                    showTotal: (total, range) => {
                        return range[0] + "-" + range[1] + " 共" + total + "条"
                    },
                },
                url: {
                    list:"/email/eoaMailboxInfo/sendList",
                    userList: "/sys/user/list",
                    updateEmailDelete:"/email/eoaMailboxInfo/updateEmailDelete",
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
                mailboxType:"",
            }
        },
        created () {
            const token = Vue.ls.get(ACCESS_TOKEN);
            this.headers = {"X-Access-Token":token}

        },
        methods: {
            onCheckAllChange (e) {
                Object.assign(this, {
                    checkedList: e.target.checked ? this.plainOptions : [],
                    indeterminate: false,
                    checkAll: e.target.checked,
                })
            },
            onChange (checkedList) {
                this.indeterminate = !!checkedList.length && (checkedList.length < this.plainOptions.length)
                this.checkAll = checkedList.length === this.plainOptions.length
            },
            loadDate(arg){
                this.checkAll=false;
                this.checkedList=[];
                this.indeterminate=false;
                this.show=true
                this.loading=true
                var param=[];
                if(arg){
                    param.pageNo = 1;
                }else{
                    param.pageNo = this.pagination.current;
                }
                this.mailboxType=arg;
                param.pageSize = this.pagination.pageSize;
                param.userId=store.getters.userInfo.id;
                param.status="0";
                param.mailboxType=arg;
                getAction(this.url.list,param).then((res) =>{
                    if(res.success){
                        var listData=res.result.records
                        if(listData){
                            this.plainOptions=[];
                            for(let i=0;i<listData.length;i++){
                                var readFlag=listData[i].readFlag;
                                var readFlagText="";
                                if(readFlag==="0"){
                                    readFlagText="未读"
                                }else{
                                    readFlagText="已读";
                                }
                                listData[i].readFlagText=readFlagText;
                                if(!listData[i].senderId_dictText){
                                    listData[i].senderId_dictText="系统发布";
                                }
                                this.plainOptions.push(listData[i].receiverId);
                            }
                            this.listData=listData;
                            this.pagination.total = res.result.total;
                            this.loading=false
                        }
                    }
                })

            },
            classification() {
                var checkedList=this.checkedList;
                if(checkedList.length>0){
                    this.$refs.eoaClassificationModal.edit(checkedList);
                }else{
                    this.$message.warning("请选择一条数据！")
                }
            },
            deleteChange(){
                var checkedList=this.checkedList;
                var that=this
                if(checkedList.length>0){
                    this.$confirm({
                        title: '删除',
                        content: '确定要删除吗',
                        okText: '确定',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            putAction(that.url.updateEmailDelete,{checkedList:checkedList}).then((res) =>{
                                if(res.success){
                                    that.$message.success(res.message)
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
                this.loadDate(this.mailboxType);
            },
            boxChange(page, pageSize) {
                this.pagination.pageSize = pageSize;
                this.pagination.current=arg;
                this.loadDate();
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