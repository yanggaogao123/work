<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

        <a-col :md="6" :sm="8">
            <a-form-item label="类别名称">
              <a-input placeholder="请输入类别名称" v-model="queryParam.labelName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" @click="batchDel"><a-icon type="delete"/>删除</a-button>
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <eoaMailboxCategory-modal ref="modalForm" @ok="modalFormOk"></eoaMailboxCategory-modal>
  </a-card>
</template>

<script>
  import EoaMailboxCategoryModal from './modals/EoaMailboxCategoryModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getAction} from '@/api/manage'
  import store from '@/store'
  export default {
    name: "EoaMailboxCategoryList",
    mixins:[JeecgListMixin],
    components: {
      EoaMailboxCategoryModal
    },
    data () {
      return {
        description: '邮箱分类管理页面',
        // 表头
        columns: [
		   {
            title: '类别名称',
            align:"center",
            dataIndex: 'labelName'
           },
		   // {
       //      title: '标签颜色',
       //      align:"center",
       //      dataIndex: 'labelColor'
       //     },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/email/eoaMailboxCategory/list",
          delete: "/email/eoaMailboxCategory/delete",
          deleteBatch: "/email/eoaMailboxCategory/deleteBatch",
          exportXlsUrl: "email/eoaMailboxCategory/exportXls",
          importExcelUrl: "email/eoaMailboxCategory/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
        loadData(arg) {
            if(!this.url.list){
                this.$message.error("请设置url.list属性!")
                return
            }
            //加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            var params = this.getQueryParams();//查询条件
            params.userId=store.getters.userInfo.id;
            this.loading = true;
            getAction(this.url.list, params).then((res) => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                }
                if(res.code===510){
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>