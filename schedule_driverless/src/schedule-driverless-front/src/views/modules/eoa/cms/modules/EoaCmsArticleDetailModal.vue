<template>
  <div>
    <!--栏目文章详情-->
    <a-card>
      <div style="text-align: right;">
        <a-icon type="close-circle" style="fontSize:20px" @click="close()" />
      </div>
      <div class="title">{{item.title}}</div>
      <div class="author">发布人：{{item.author}}  <span style="margin-left: 15px">{{item.publishDate}}</span><span @click.stop="eyeClick(item)"><a-icon type="eye"  style="margin: 0 5px 0 18px" />{{item.readTimes==0?'':item.readTimes}} </span></div>
      <a-divider style="margin: 10px 0 10px 0"/>
      <div v-html="item.content"></div>
    </a-card>
    <eoa-cms-article-reader-modal ref="readerModal"></eoa-cms-article-reader-modal>
  </div>

</template>

<script>
  import { getAction } from '@/api/manage'
  import store from '@/store'
  import EoaCmsArticleReaderModal from './EoaCmsArticleReaderModal'
  export default {
    name: "EoaCmsArticleDetailModal",
    components: { EoaCmsArticleReaderModal },
    inject:['closeCurrent'],
    data() {
      return {
        item:{}
       }
    },
    created() {
        this.item=this.$route.query.item;
        this.recordReader();
    },
    mounted () {
        this.item=this.$route.query.item;
    },
    methods: {
      recordReader(){
        if(this.item.id && store.getters.userInfo.username){
          getAction("/cms/eoaCmsArticleRead/recordReader",{articleId:this.item.id,username:store.getters.userInfo.username}).then((res) =>{
            if(res.success){
              this.item.readTimes=this.item.readTimes+1
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
      eyeClick(item){
        this.$refs.readerModal.loadReader(item);
      },
      close(){
        this.closeCurrent();
      }
    }
  }
</script>

<style lang="less" scoped>
  .title{
    text-align: center;
    font-size: 30px;
    font-weight: 600;
  }
  .author{
    font-size:12px;
    margin-left: 50px;
    text-align: left;
  }
</style>