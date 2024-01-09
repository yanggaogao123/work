<template>
  <div class="rank">
      <a-form >
        <a-row>
            <a-form-item>
              <a-tree-select
                showSearch
                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                :treeData="treeData"
                placeholder='请选择分类列表'
                allowClear
                :getPopupContainer="node => node.parentNode"
                v-model="queryParam.columnId"
                @change="handleChangePigsty('other',$event)"/>
            </a-form-item>
        </a-row>
        <a-row>
          <table>
            <tr v-for="list in eoaCmsList" @click="onClick(list.id)" :key="list.id">
              <td><img :src="list.imageHref"  style="width:50px"/></td>
              <td><tr class="tittle">{{list.title}}</tr>
                  <tr class="desc">
                  <j-ellipsis :length="16" :value="list.summary"></j-ellipsis>
                    </tr>
                </td>
            </tr>
          </table>
        </a-row>
      </a-form>
    <!--表单区域-->
    <eoaCmsView-modal ref="modalForm"></eoaCmsView-modal>
  </div>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction } from '@/api/manage'
  import JEllipsis from '@/components/jeecg/JEllipsis';
  import EoaCmsViewModal from './EoaCmsViewModal';
  export default {
    name: "EoaCmsListModel",
    mixins:[JeecgListMixin],
    components: {
      JEllipsis,
      EoaCmsViewModal
    },
    data () {
      return {
        url: {
          list: "/cms/eoaCmsArticle/list",
          selectTree:"/chat/eoaCmsMenu/selectTree"
        },
        queryParam:{},
        treeData:[],
        eoaCmsList:[]
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    created() {
      this.loadData(1);
      //初始化字典配置 在自己页面定义
      this.selectTree();
    },
    methods: {
      loadData(arg) {
        var params={};
        params.isPublish="1";
        if(arg!==1){
            params.columnId=arg
        }
        getAction(this.url.list,params).then((res) => {
          if (res.success) {
          this.eoaCmsList=res.result.records;
          this.ipagination.total = res.result.total;
        }
      })
      },
      handleChangePigsty(flag,value) {
        this.loadData(value);
      },
      selectTree(){
        getAction(this.url.selectTree).then((res) => {
          if(res.success ){
          this.treeData = res.result;
        }
      })
      },
      onClick(record){
        console.log(record)
        this.$refs.modalForm.edit(record);
      }
    }
  }
</script>
<style scoped>
  .rank {
    padding: 0 32px 32px 72px;
  }
  table td{
    padding: 10px 0 0 10px;
  }
  .tittle{
    font-weight: bolder;
    font-size: x-small;
    color: #000;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    word-wrap: normal;
  }
  .desc{
    font-size: x-small;
    color: #999;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    line-height: 1.3;
  }
</style>