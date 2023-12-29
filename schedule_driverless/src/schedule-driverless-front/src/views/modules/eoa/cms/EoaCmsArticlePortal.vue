<template>
  <div>
    <!--轮播图-->
    <template>
      <a-carousel autoplay>
        <template v-for="(item, index) in carouselImg" v-if="carouselImg.length>0">
          <img :src="getAvatarView(item)" :key="index" style="height: 220px;width:100%"/>
        </template>
        <template v-else>
          <!--<img src="@assets/nodata.png" :key="index" style="height: 220px;width:100%"/>-->
          <a-empty />
        </template>
      </a-carousel>
    </template>
    <a-row :gutter="24">
      <!--目录树结构-->
      <a-card style="width: 100%;margin:12px 12px;" :style="{height:'700px' }">
        <a-col :span="4">
          <template>
            <a-directory-tree defaultExpandAll :defaultSelectedKeys="defaultSelectedKeys" @select="onSelect" @expand="onExpand">
              <a-tree-node title="目录" key="A0">
                <a-tree-node title="新闻动态" key="A01" isLeaf />
                <a-tree-node title="规章制度" key="A02" isLeaf />
                <a-tree-node title="签约新闻" key="A03" isLeaf />
                <a-tree-node title="常用下载" key="A04" isLeaf />
              </a-tree-node>
            </a-directory-tree>
          </template>
        </a-col>
        <!--目录内容-->
        <a-col :span="16">
          <eoa-cms-menu-article-modal ref="eoaCmsMenuArticleModal"></eoa-cms-menu-article-modal>
        </a-col>
      </a-card>

    </a-row>
  </div>

</template>

<script>

  import ACol from "ant-design-vue/es/grid/Col"
  import { getFileAccessHttpUrl } from '@/api/manage'
  import { getSiteInfo} from '@/api/api'
  import EoaCmsMenuArticleModal from './modules/EoaCmsMenuArticleModal'

  export default {
    name: "EoaCmsArticlePortal",
    components: {
      ACol,
      EoaCmsMenuArticleModal
    },
    data() {
      return {
        carouselImg:[],
        defaultSelectedKeys:[]
       }
    },
    created() {
      if(this.$route.query.type){
        this.defaultSelectedKeys=[];
        this.defaultSelectedKeys.push(this.$route.query.type);
      }
    },
    mounted () {
      this.$refs.eoaCmsMenuArticleModal.loadList(this.$route.query.type);
      this.loadMenuArticle()
    },
    methods: {
      loadMenuArticle () {
        var _this=this;
        getSiteInfo().then((res)=>{
          if(res.success && res.result.site[0].carouselImg){
            _this.carouselImg=res.result.site[0].carouselImg.split(",");
          }
        })
      },
      onSelect(keys) {
       if(keys[0]!=='A0'){
         this.$refs.eoaCmsMenuArticleModal.loadList(keys[0]);
       }
      },
      onExpand() {
        console.log('Trigger Expand');
      },
      handleInfiniteOnLoad() {
        this.$message.warning('已加载全部');
      },
      getAvatarView(url){
        return getFileAccessHttpUrl(url)
      },
    }
  }
</script>

<style lang="less" scoped>
  .ant-list-item-meta-title {
   font-weight: 600;
  }
  .demo-infinite-container {
    border: 1px solid #e8e8e8;
    overflow: auto;
    padding: 8px 24px;
    overflow: auto;
    height: 100px;
  }
  .demo-loading-container {
    position: absolute;
    bottom: 40px;
    width: 100%;
    text-align: center;
  }
</style>