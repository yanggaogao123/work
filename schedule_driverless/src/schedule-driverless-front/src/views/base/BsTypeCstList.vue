<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="字典组">
              <a-input placeholder="请输入字典组" v-model="queryParam.domain"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="字段">
              <a-input placeholder="请输入字段" v-model="queryParam.fie"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="内容">
              <a-input placeholder="请输入内容" v-model="queryParam.zhCn"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" v-has="'361:edit'" type="primary" icon="plus">新增</a-button>
<!--      <a-button type="primary" icon="download" @click="handleExportXls('列表国际化')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" v-has="'361:edit'" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
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
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="uploadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)" v-has="'361:edit'">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a v-has="'361:edit'">删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <bsTypeCst-modal ref="modalForm" @ok="modalFormOk"></bsTypeCst-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import BsTypeCstModal from './modules/BsTypeCstModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import { httpAction } from '@/api/manage'
  import { getAction, postAction } from '../../api/manage';

  export default {
    name: "BsTypeCstList",
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      BsTypeCstModal
    },
    data () {
      return {
        description: '列表国际化管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'英文',
            align:"center",
            dataIndex: 'enUs'
          },
          {
            title:'葡萄牙文',
            align:"center",
            dataIndex: 'ptPt'
          },
          {
            title:'简体中文',
            align:"center",
            dataIndex: 'zhCn'
          },
          {
            title:'简体中文（水巴）',
            align:"center",
            dataIndex: 'zhCnWb'
          },
          {
            title:'繁体中文',
            align:"center",
            dataIndex: 'zhHk'
          },
          {
            title:'字典组',
            align:"center",
            dataIndex: 'domain'
          },
          {
            title:'字段',
            align:"center",
            dataIndex: 'field'
          },
          {
            title:'值',
            align:"center",
            dataIndex: 'value'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            // fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/base/bsTypeCst/list",
          delete: "/base/bsTypeCst/delete",
          deleteBatch: "/base/bsTypeCst/deleteBatch",
          exportXlsUrl: "/base/bsTypeCst/exportXls",
          importExcelUrl: "base/bsTypeCst/importExcel",
        },
        dictOptions:{},
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
        loadData(arg) {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!')
                return
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            var params = this.getQueryParams();// 查询条件
            console.log(params);
            this.loading = true;
            postAction(this.url.list, params).then((res) => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                } else {
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>