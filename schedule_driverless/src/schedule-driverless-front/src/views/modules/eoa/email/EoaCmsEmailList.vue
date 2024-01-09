<template>
  <div class="page-header-index-wide page-header-wrapper-grid-content-main">

    <a-row type="flex" :gutter="24">
      <a-col :md="24" :lg="5">
        <a-card :bordered="false" style="height: 100%;">
          <div class="account-center-avatarHolder">
            <a-button type="primary" block @click="bioClick('0')">写信</a-button>
          </div>
          <div class="account-center-detail">
            <ul style="padding: 0">
             <li>
               <a @click="inbox"><i class="inbox"></i>收件箱
                 <span class="label first">{{inboxCount}}</span>
               </a>
             </li>
              <li>
                <a @click="send"><i class="send"/>发件箱
                  <span class="label first">{{sendCount}}</span>
                </a>
              </li>
              <li>
                <a @click="drafts"><i class="drafts"/>草稿箱
                  <span class="label second">{{draftsCount}}</span>
                </a>
              </li>
              <li>
                <a @click="dustbin"><i class="dustbin" />垃圾箱
                  <span class="label second">{{dustbinCount}}</span>
                </a>
              </li>
            </ul>
            <ul style="padding: 0;margin-top: 20px;display: flex">
              <li style="border-bottom: none;">标签</li>
              <a-tag  @click="addCategory" style="background: #fff; borderStyle: dashed;margin-left: 10px">
                <a-icon type="plus" />新建标签
              </a-tag>
            </ul>
          </div>
          <template  v-for="tag in mailboxTypeOptions" >
            <div style="width: 90px;float: left">
            <a-tag  @click="classification(tag)"   :color="tag.color" :key="tag.value" style="margin-top: 10px;width: 68px">
              <span ><j-ellipsis :value="tag.text" :length="3"/></span>
            </a-tag>
            <a-icon  type="close" style="position: relative;right: 24px;" @click="handleClose(tag.value)"/>
            </div>
          </template>
        </a-card>
      </a-col>
      <a-col :md="24" :lg="19">
        <eoaEmailBio-modal ref="eoaEmailBioModal" @ok="bioOk"></eoaEmailBio-modal>
        <eoaEmailInbox-model ref="eoaEmailInboxModel" @ok="bioOk" @reply="handleReply"></eoaEmailInbox-model>
        <eoaMailDrafts-list ref="eoaMailDraftsList" @ok="draftsOk"></eoaMailDrafts-list>
        <eoaEmailIn-list ref="eoaEmailInList" @ok="inOk" @emailType="emailType" @reply="handleReply"></eoaEmailIn-list>
        <eoaMialDustbin-list ref="eoaMialDustbinList" @ok="dustbinOk" @reply="handleReply"></eoaMialDustbin-list>
        <eoa-email-type-modal ref="eoaEmailTypeModal" @ok="emailTypeOk"></eoa-email-type-modal>
        <eoaMailboxCategory-modal ref="categoryModal" @ok="categoryOk"></eoaMailboxCategory-modal>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import EoaEmailBioModal from './modals/EoaEmailBioModal'
  import EoaEmailInboxModel from './modals/EoaEmailInboxModel'
  import EoaMailDraftsList from './EoaMailDraftsList'
  import EoaEmailInList from './EoaEmailInList'
  import EoaMialDustbinList from './EoaMialDustbinList'
  import EoaEmailTypeModal from './modals/EoaEmailTypeModal'
  import { mapGetters } from 'vuex'
  import { getAction,deleteAction,httpAction } from '@/api/manage'
  import store from '@/store'
  import {initDictOptions} from '@/components/dict/JDictSelectUtil'
  import JEllipsis from "@/components/jeecg/JEllipsis";
  import EoaMailboxCategoryModal from './modals/EoaMailboxCategoryModal'
  export default {
    components: {
      EoaEmailBioModal,
      EoaEmailInboxModel,
      EoaMailDraftsList,
      EoaEmailInList,
      EoaMialDustbinList,
      EoaEmailTypeModal,
      JEllipsis,
      EoaMailboxCategoryModal
    },
    data() {
      return {
        tagInputVisible: false,
        tagInputValue: '',
        teams: [],
        teamSpinning: true,
        noTitleKey: "",
        show:"bio",
        url:{
          emailCount:"/email/eoaMailboxInfo/emailCount",
          getLabelName:"/email/eoaMailboxCategory/getLabelName",
          delete: "/email/eoaMailboxCategory/delete",
        },
        inboxCount:"0",
        sendCount:"0",
        draftsCount:"0",
        dustbinCount:"0",
        mailboxTypeOptions:[],
        color:["purple","red","orange","green","cyan","blue","pink"],
        toType : this.$route.query.type,
        replyReocrd:[],
        currentTypeValue:{},
      }
    },
    created(){
    },
    mounted () {
      this.$refs.eoaEmailBioModal.loadDate();
      this.loadData();
      if(this.toType === 'inbox'){
        this.inbox();
      }else if(this.toType === 'process'){
        this.handleReply(this.$route.query.rec)
      }
    },
    methods: {
      handleReply(record){
        this.replyReocrd = record;
        this.bioClick();
      },
      loadData(){
      getAction(this.url.emailCount,{userId:store.getters.userInfo.id}).then((res) =>{
        if(res.success){
          var result=res.result
          if(result){
          this.draftsCount=result.draftsCount;
          this.sendCount=result.sendCount;
          this.inboxCount=result.inboxCount;
          this.dustbinCount=result.dustbinCount;
          }
        }
      })
        this.initDictConfig();
      },
      initDictConfig() {
        getAction(this.url.getLabelName,{userId:store.getters.userInfo.id}).then((res) =>{
            if(res.success){
                var type=[];
                var color=this.color;
                var j=0;
                for(let i=0;i<res.result.length;i++){
                    if(j>=color.length){
                        j=0;
                    }
                    type.push({
                        text: res.result[i].labelName,
                        value:res.result[i].id,
                        color:color[j]
                    })
                    j++;
                }
                this.mailboxTypeOptions=type;
            }
        })
      },
      classification(value){
         this.currentTypeValue=value;
         this.$refs.eoaEmailBioModal.load();
         this.$refs.eoaEmailInboxModel.load()
         this.$refs.eoaMailDraftsList.load()
         this.$refs.eoaEmailInList.load();
         this.$refs.eoaMialDustbinList.load();
         this.$refs.eoaEmailTypeModal.loadDate(value.value);
         this.$refs.eoaEmailTypeModal.title=value.text;
      },
      bioClick(flag){
        if(flag&&flag=='0'){
          this.replyReocrd=[]
        }
        if(this.replyReocrd){
          this.$refs.eoaEmailBioModal.replyReocrd = this.replyReocrd;
        }
        this.$refs.eoaEmailBioModal.loadDate(1);
        this.$refs.eoaEmailInboxModel.load();
        this.$refs.eoaMailDraftsList.load();
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.load();
      },
      inbox(){
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load()
        this.$refs.eoaMailDraftsList.load()
        this.$refs.eoaEmailInList.loadDate(1);
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.load();
      },
      send(){
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.loadDate(1);
        this.$refs.eoaMailDraftsList.load();
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.load();
      },
      drafts(){
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load();
        this.$refs.eoaMailDraftsList.loadDate(1);
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.load();
      },
      dustbin(){
        this.$refs.eoaMialDustbinList.loadDate(1);
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load();
        this.$refs.eoaMailDraftsList.load();
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaEmailTypeModal.load();
      },
      bioOk(){
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.loadDate(1);
        this.$refs.eoaMailDraftsList.load();
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.load();
        this.loadData();
      },
      dustbinOk(){
        this.$refs.eoaMialDustbinList.loadDate(1);
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load();
        this.$refs.eoaMailDraftsList.load();
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaEmailTypeModal.load();
        this.loadData();
      },
      draftsOk(){
        this.$refs.eoaMailDraftsList.loadDate(1);
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load();
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaEmailTypeModal.load();
        this.loadData();
      },
      inOk(){
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load();
        this.$refs.eoaMailDraftsList.load();
        this.$refs.eoaEmailInList.loadDate(1);
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.load();
        this.loadData();
      },
      emailTypeOk(){
        this.$refs.eoaEmailBioModal.load();
        this.$refs.eoaEmailInboxModel.load()
        this.$refs.eoaMailDraftsList.load()
        this.$refs.eoaEmailInList.load();
        this.$refs.eoaMialDustbinList.load();
        this.$refs.eoaEmailTypeModal.loadDate(this.currentTypeValue.value);
        this.$refs.eoaEmailTypeModal.title=this.currentTypeValue.text;
      },
      handleInputChange (e) {
        this.tagInputValue = e.target.value
      },
      emailType(value){
          this.classification(value);
      },
      handleClose(id) {
          deleteAction(this.url.delete,{id: id}).then((res) => {
              if (res.success) {
                  this.$message.success(res.message);
                  this.$refs.eoaEmailInList.getList(1);
                  this.$refs.eoaEmailInList.initDictConfig();
                  this.loadData();
              } else {
                  this.$message.warning(res.message);
              }
          })
      },
      addCategory(){
          this.$refs.categoryModal.add();
      },
      categoryOk(){
        this.loadData();
        this.$refs.eoaEmailInList.initDictConfig();
      },
  }
  }
</script>

<style lang="less" scoped>
  a{
    color: #666666;
    display: block;
    padding: 5px 0;
  }
  .inbox{
    /*background: url(./image/shoujianxiang.jpg) no-repeat center;*/
    /*width: 40px;*/
    /*height: 35px;*/
    /*display: inline-block;*/
  }
  .page-header-wrapper-grid-content-main {
    width: 100%;
    height: 100%;
    min-height: 100%;
    transition: .3s;

  .account-center-avatarHolder {
    text-align: center;
    margin-bottom: 24px;

  & > .avatar {
        margin: 0 auto;
        width: 104px;
        height: 104px;
        margin-bottom: 20px;
        border-radius: 50%;
        overflow: hidden;
  img {
    height: 100%;
    width: 100%;
  }
  }

  .username {
    color: rgba(0, 0, 0, 0.85);
    font-size: 20px;
    line-height: 28px;
    font-weight: 500;
    margin-bottom: 4px;
  }
  }

  .account-center-detail {

  p {
    margin-bottom: 8px;
    padding-left: 26px;
    position: relative;
  }

  .title {
    background-position: 0 0;
  }
  .group {
    background-position: 0 -22px;
  }
  .address {
    background-position: 0 -44px;
  }
  }

  .account-center-tags {
  .ant-tag {
    margin-bottom: 8px;
  }
  }

  .tagsTitle, .teamTitle {
    font-weight: 500;
    color: rgba(0,0,0,.85);
    margin-bottom: 12px;
  }

  }
.bio{
  background-color: #1ab394;
  border-color: #1ab394;
  color: #FFFFFF;
  width: 100%;
}
  li{
    list-style-type:none;
    border-bottom: 1px solid #e7eaec;
    display: block;
    box-sizing: border-box;
    text-align: -webkit-match-parent;
  }
  .label {
    display: inline;
    font-size: 10px;
    font-weight: 600;
    padding: 3px 8px;
    text-shadow: none;
    line-height: 1;
    text-align: center;
    white-space: nowrap;
    vertical-align: baseline;
    border-radius: .25em;
    color: #FFFFFF;
    float: right!important;
  }
  .first{
    background-color: #f8ac59;
  }
  .second {
    background-color: #ed5565;
  }
</style>