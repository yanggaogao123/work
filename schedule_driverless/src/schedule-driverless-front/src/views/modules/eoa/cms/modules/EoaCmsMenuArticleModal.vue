<template>
  <a-list
    size="large"
    :pagination="pagination"
    :dataSource="listData"
    :loading="spinLoading"
  >
    <a-list-item @click="showDetail(item)" slot="renderItem" :key="index" slot-scope="item, index" v-if="item.type!='file'">
      <a-list-item-meta :description="item.content | contentFilter" >
        <a slot="title" href="#">{{item.title}}</a>
        <img slot="avatar" :src="getAvatarView(item.imageHref)" style="width:220px;height:110px;"/>
      </a-list-item-meta>
    </a-list-item>
    <a-list-item @click="recordDownload(item)" slot="renderItem" :key="index" slot-scope="item, index"  v-else>
      <a-list-item-meta>
        <a slot="title" :href="getFileDownloadUrl(item.fileUrl)" target="_blank">{{item.title}}</a>
      </a-list-item-meta>
      <div class="ant-list-item-content">{{item.createTime}}</div>
    </a-list-item>
  </a-list>
</template>

<script>
  import Vue from 'vue'
  import { getAction } from '@/api/manage'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import { getFileAccessHttpUrl } from '@/api/manage'
  import store from '@/store'
  export default {
    name: "EoaCmsMenuArticleModal",
    data () {
      return {
        type:"1",
        spinLoading:false,
        listData:[],
        pagination: {
          current: 1,
          showSizeChanger: false,
          showQuickJumper: true,
          pageSize: 4,
          pageSizeOptions: ['12','24','36'],
          total: 0,
          onChange: (page,pageSize) => {
            this.pageChange(page,pageSize);
          },
          onShowSizeChange:(current,pageSize) =>{
            this.pageChange(current,pageSize);
          },
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
        },
        url: {
          list:"/cms/eoaCmsArticle/menuArticlelist",
        },
        headers:{},
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    filters: {
      contentFilter(content) {
        //let reg1=/<().*?>/g;
        let reg2 = /<\/?.+?\/?>/g;
        content = content.replace(reg2, "").replace(new RegExp("&nbsp;", "gm"), '').replace(new RegExp("&darr;", "gm"), '')
        if (content.length > 100) {
          content = content.slice(0, 100) + "......"
        }
        return content;
      }
    },
    methods: {
      getFileDownloadUrl: function (path) {
        return getFileAccessHttpUrl(path)
      },
      recordDownload(item){
        if(item.id && store.getters.userInfo.username){
          getAction("/cms/eoaCmsArticleRead/recordReader",{articleId:item.id,username:store.getters.userInfo.username}).then((res) =>{
            if(res.success){
              console.log(res);
            }
          });
        }else{
          if(!this.item.id){
            this.$message.warning("文章信息异常");
          }else{
            this.$message.warning("登录信息异常");
          }
        }
      },
      loadDate(arg){
        this.spinLoading=true
        this.pagination.current=arg;
        var param={};
        param.type=this.type;
        param.pageNo = this.pagination.current;
        param.pageSize = this.pagination.pageSize;
        getAction(this.url.list,param).then((res) =>{
          if(res.success){
            let listData=res.result.records;
            this.listData=listData
            this.pagination.total = res.result.total;
          }
          this.spinLoading=false
        })

      },
      loadList(type){
        console.log("load===type==",type)
        this.type=type;
        this.loadDate(1);
      },
      getAvatarView(url){
        return getFileAccessHttpUrl(url)
      },
      pageChange(page,pageSize){
        this.pagination.pageSize=pageSize;
        this.loadDate(page);
      },
      showDetail(item){
        console.log("showDetail==>item", item);
        this.$router.push({ path: '/modules/eoa/cms/modules/eoaCmsArticleDetailModal',query : { item:item }})
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