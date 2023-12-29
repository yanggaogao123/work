<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="栏目名称">
              <a-input placeholder="请输入栏目名称" v-model="queryParam.menuName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="栏目">
              <a-tree-select
                showSearch
                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                :treeData="treeData"
                placeholder='请选择栏目列表'
                allowClear
                :getPopupContainer="node => node.parentNode"
                v-model="queryParam.id"
                @change="handleChangePigsty('other',$event)"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">条件清除</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('分类栏目管理')">
        导出
      </a-button>
      <a-button type="primary" icon="delete" @click="batchDel">删除</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :expandedRowKeys="expandedRowKeys"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @expand="handleExpand"
        @change="handleTableChange">

        <span slot="linkUrl" slot-scope="text">
          <a v-if="text!=null" :href="text" target="_blank">
            <j-ellipsis :value="text" :length="10">
              {{text}}
            </j-ellipsis>
          </a>
        </span>
        <span slot="imageHref" slot-scope="text, record">
          <img style="width:50px" :src="getAvatarView(record.imageHref)" v-if="record.imageHref"/>
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <eoaCmsMenu-modal ref="modalForm" @ok="modalFormOk"></eoaCmsMenu-modal>
  </a-card>
</template>

<script>
  import EoaCmsMenuModal from './modules/EoaCmsMenuModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction,getFileAccessHttpUrl } from '@/api/manage'
  import JEllipsis from "@/components/jeecg/JEllipsis";
  export default {
    name: "EoaCmsMenuList",
    mixins:[JeecgListMixin],
    components: {
      EoaCmsMenuModal,
      JEllipsis
    },
    data () {
      return {
        description: 'cms分类表管理页面',
        treeData:[],
        // 表头
        columns: [
		   {
            title: '栏目名称',
            align:"center",
            dataIndex: 'menuName'
           },
		   {
            title: '编码',
            align:"center",
            dataIndex: 'menuCode'
           },
          {
            title: '图片',
            align:"center",
            dataIndex: 'imageHref',
            scopedSlots: { customRender: 'imageHref' }
          },
		       {
            title: '链接地址',
            align:"center",
            dataIndex: 'linkUrl',
            scopedSlots: {customRender: 'linkUrl'},
           },
		   {
            title: '模版编码',
            align:"center",
            dataIndex: 'templateCode'
           },
		   {
            title: '显示',
            align:"center",
            dataIndex: 'isShow_dictText'
           },
          {
            title: '排序',
            align:"center",
            dataIndex: 'serialNumber'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/chat/eoaCmsMenu/list",
          rootlist: "/chat/eoaCmsMenu/rootList",
          childList: "/chat/eoaCmsMenu/childList",
          delete: "/chat/eoaCmsMenu/delete",
          deleteBatch: "/chat/eoaCmsMenu/deleteBatch",
          exportXlsUrl: "chat/eoaCmsMenu/exportXls",
          importExcelUrl: "chat/eoaCmsMenu/importExcel",
          selectTree:"/chat/eoaCmsMenu/selectTree",
       },
        queryParam:{},
        expandedRowKeys:[],
        hasChildrenField:"hasChild"
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    created() {
      this.selectTree();
    },
    methods: {
      loadData(arg){
        if(arg==1){
          this.ipagination.current=1
        }
        this.loading = true
        this.expandedRowKeys = []
        let params = this.getQueryParams()
        return new Promise((resolve) => {
          getAction(this.url.rootlist,params).then(res=>{
            if(res.success){
              console.log("res===root",res)
              let result = res.result
              if(Number(result.total)>0){
                this.ipagination.total = Number(result.total)
                this.dataSource = this.getDataByResult(res.result.records)
                resolve()
              }else{
                this.ipagination.total=0
                this.dataSource=[]
              }
            }else{
              this.$message.warning(res.message)
            }
            this.loading = false
          })
        })
      },
      getDataByResult(result){
        return result.map(item=>{
          //判断是否标记了带有子节点
          if(item[this.hasChildrenField]>0){
            let loadChild = { id: item.id+'_loadChild', name: 'loading...', isLoading: true }
            item.children = [loadChild]
          }
          return item
        })
      },
      handleExpand(expanded, record){
        console.log("handleExpand==>",expanded,record)
        // 判断是否是展开状态
        if (expanded) {
          this.expandedRowKeys.push(record.id)
          if (record.children.length>0 && record.children[0].isLoading === true) {
            let params = {};//查询条件
            params.id = record.id
            getAction(this.url.childList,params).then((res)=>{
              if(res.success){
                if(res.result && res.result.length>0){
                  record.children = this.getDataByResult(res.result)
                  this.dataSource = [...this.dataSource]
                }else{
                  record.children=''
                  record.hasChild=0
                }
              }else{
                this.$message.warning(res.message)
              }
            })
          }
        }else{
          let keyIndex = this.expandedRowKeys.indexOf(record.id)
          if(keyIndex>=0){
            this.expandedRowKeys.splice(keyIndex, 1);
          }
        }
      },
      getAvatarView: function (avatar) {
        let imgArray = avatar.split(",");
        return getFileAccessHttpUrl(imgArray[0])
      },
      handleChangePigsty(flag,value) {
        this.queryParam.id = value
      },
      selectTree(){
        getAction(this.url.selectTree).then((res) => {
          if(res.success ){
          this.treeData = res.result;
        }
      })
      },
      modalFormOk(){
        this.loadData();
        this.selectTree();
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>