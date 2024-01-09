<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="发布时间">
              <a-range-picker
                style="width:100%"
                format="YYYY-MM-DD"
                @change="datePickerChange"
                v-model="datepickerApplyDate"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="标题">
              <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input>
            </a-form-item>
          </a-col>
        <a-col :md="6" :sm="8">
            <a-form-item label="栏目">
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
      <a-button type="primary" icon="download" @click="handleExportXls('文章管理')">
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">
        <span slot="imageHref" slot-scope="text, record">
          <img style="width:50px" :src="getAvatarView(record.imageHref)" v-if="record.imageHref"/>
        </span>

        <span slot="titleSlot" slot-scope="text">
          <j-ellipsis :value="text" :length="20"/>
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a v-if="record.isPublish==1" @click="handleRelease(record)">取消发布</a>
          <a v-else @click="handleRelease(record)">发布</a>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <eoaCmsArticle-modal ref="modalForm" @ok="modalFormOk"></eoaCmsArticle-modal>
  </a-card>
</template>

<script>
  import EoaCmsArticleModal from './modules/EoaCmsArticleModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getAction,getFileAccessHttpUrl } from '@/api/manage'
  import JEllipsis from '@/components/jeecg/JEllipsis';
  import moment from 'moment'
  export default {
    name: "EoaCmsArticleList",
    mixins:[JeecgListMixin],
    components: {
      EoaCmsArticleModal,
      JEllipsis
    },
    data () {
      return {
        description: 'cms文章表管理页面',
        // 表头
        columns: [
          {
            title: '发布时间',
            align:"center",
            dataIndex: 'publishDate',
            width:150,
            customRender: (text) => {
            console.log(text)
            return moment(text).format('YYYY/MM/DD HH:mm:ss')
          }
          },{
            title: '标题',
            align:"center",
            dataIndex: 'title',
            scopedSlots: { customRender: 'titleSlot' }
           },{
            title: '封面图片',
            align:"center",
            dataIndex: 'imageHref',
            scopedSlots: { customRender: 'imageHref' }
           },{
            title: '作者',
            align:"center",
            dataIndex: 'author'
           },{
            title: '关键字',
            align:"center",
            dataIndex: 'keywords'
       },{
            title: '栏目',
            align:"center",
            dataIndex: 'columnId_dictText'
           },
          {
            title: '发布状态',
            align:"center",
            dataIndex: 'isPublish_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }

        ],
		url: {
          list: "/cms/eoaCmsArticle/list",
          delete: "/cms/eoaCmsArticle/delete",
          deleteBatch: "/cms/eoaCmsArticle/deleteBatch",
          exportXlsUrl: "cms/eoaCmsArticle/exportXls",
          importExcelUrl: "cms/eoaCmsArticle/importExcel",
          selectTree:"/chat/eoaCmsMenu/selectTree",
          updateIsPublish: "cms/eoaCmsArticle/updateIsPublish",
          menuList: "/chat/eoaCmsMenu/list",
       },
        datepickerApplyDate:null,
        queryParam:{},
        treeData:[]
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
      getAvatarView: function (avatar) {
        let url = getFileAccessHttpUrl(avatar);
        return url
      },
      modalFormOk(){
        this.loadData();
        this.selectTree();
      },
      handleChangePigsty(flag,value) {
      this.queryParam.columnId=value
      },
      searchReset() {
        this.datepickerApplyDate = null;
        this.queryParam = {};
        this.loadData(1);
      },
      datePickerChange(date,dateString){
        this.queryParam.publishDateStart = dateString[0];
        this.queryParam.publishDateEnd = dateString[1];
      },
      selectTree(){
        getAction(this.url.selectTree).then((res) => {
          if(res.success ){
          this.treeData = res.result;
        }
      })
      },
      handleRelease(record){
        var that = this
        var params = {};
        params.id = record.id
        if (record.isPublish == "0") {
          params.isPublish = "1"
        } else {
          params.isPublish = "0"
        }
       getAction(this.url.updateIsPublish,params).then((res) =>{
          if(res.success){
          if(record.isPublish=="1"){
            that.$message.success("取消发布成功");
          }else{
            that.$message.success("发布成功");
          }
          that.loadData();
          }
        })
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>