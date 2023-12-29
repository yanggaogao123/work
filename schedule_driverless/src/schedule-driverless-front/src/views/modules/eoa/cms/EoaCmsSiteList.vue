<template>
  <a-card :bordered="false">

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

        <span slot="siteLogo" slot-scope="text, record">
          <img  style="width:50px" :src="getAvatarView(record.siteLogo)" />
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <!--
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
              -->
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <eoaCmsSite-modal ref="modalForm" @ok="modalFormOk"></eoaCmsSite-modal>
  </a-card>
</template>

<script>
  import EoaCmsSiteModal from './modules/EoaCmsSiteModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {getFileAccessHttpUrl} from '@/api/manage';

  export default {
    name: "EoaCmsSiteList",
    mixins:[JeecgListMixin],
    components: {
      EoaCmsSiteModal
    },
    data () {
      return {
        description: '站点信息管理',
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
            title: '站点名称',
            align:"center",
            dataIndex: 'siteName'
          },
          {
            title: '站点Logo',
            align:"center",
            dataIndex: 'siteLogo',
            scopedSlots: { customRender: 'siteLogo' }
          },
          {
            title: '公司电话',
            align:"center",
            dataIndex: 'companyTel'
          },
          {
            title: '站点模板',
            align:"center",
            dataIndex: 'siteTemplateStyle'
          },
          {
            title: '经度',
            align:"center",
            dataIndex: 'longitude'
          },
          {
            title: '纬度',
            align:"center",
            dataIndex: 'latitude'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/cms/eoaCmsSite/list",
          delete: "/cms/eoaCmsSite/delete",
          deleteBatch: "/cms/eoaCmsSite/deleteBatch",
          exportXlsUrl: "cms/eoaCmsSite/exportXls",
          importExcelUrl: "cms/eoaCmsSite/importExcel",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      getAvatarView: function (avatar) {
        return getFileAccessHttpUrl(avatar)
      },

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>