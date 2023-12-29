<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="工作日，定义周几需要打卡 0=周日">
              <a-input placeholder="请输入工作日，定义周几需要打卡 0=周日" v-model="queryParam.workingDays"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="上班时间">
              <a-input placeholder="请输入上班时间" v-model="queryParam.onTime"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="上班迟到时间（上班时间后到迟到时间之前打卡算迟到）">
                <a-input placeholder="请输入上班迟到时间（上班时间后到迟到时间之前打卡算迟到）" v-model="queryParam.onTimeLate"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="上班旷工时间（该时间后打卡记为旷工）">
                <a-input placeholder="请输入上班旷工时间（该时间后打卡记为旷工）" v-model="queryParam.onTimeAbsent"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="下班打卡时间">
                <a-input placeholder="请输入下班打卡时间" v-model="queryParam.offTime"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
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

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('考勤打卡规则')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
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
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
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
    <oaSignRule-modal ref="modalForm" @ok="modalFormOk"></oaSignRule-modal>
  </a-card>
</template>

<script>
  import OaSignRuleModal from './modules/OaSignRuleModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'OaSignRuleList',
    mixins: [JeecgListMixin],
    components: {
      OaSignRuleModal
    },
    data() {
      return {
        description: '考勤打卡规则管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '工作日，定义周几需要打卡 0=周日',
            align: 'center',
            dataIndex: 'workingDays'
          },
          {
            title: '上班时间',
            align: 'center',
            dataIndex: 'onTime'
          },
          {
            title: '上班迟到时间（上班时间后到迟到时间之前打卡算迟到）',
            align: 'center',
            dataIndex: 'onTimeLate'
          },
          {
            title: '上班旷工时间（该时间后打卡记为旷工）',
            align: 'center',
            dataIndex: 'onTimeAbsent'
          },
          {
            title: '下班打卡时间',
            align: 'center',
            dataIndex: 'offTime'
          },
          {
            title: '下班早退时间：该时间前打卡记为早退，反之记为正常打卡',
            align: 'center',
            dataIndex: 'offTimeEarly'
          },
          {
            title: '元旦假期时间',
            align: 'center',
            dataIndex: 'holidayYuanDan'
          },
          {
            title: '春节假期时间',
            align: 'center',
            dataIndex: 'holidayChunJie'
          },
          {
            title: '清明假期时间',
            align: 'center',
            dataIndex: 'holidayQingMing'
          },
          {
            title: '五一劳动节假期时间',
            align: 'center',
            dataIndex: 'holidayLaoDong'
          },
          {
            title: '端午假期时间',
            align: 'center',
            dataIndex: 'holidayDuanWu'
          },
          {
            title: '中秋假期时间',
            align: 'center',
            dataIndex: 'holidayZhongQiu'
          },
          {
            title: '国庆假期时间',
            align: 'center',
            dataIndex: 'holidayGuoQing'
          },
          {
            title: '其他假期时间',
            align: 'center',
            dataIndex: 'holidayOther'
          },
          {
            title: '不参与打卡人员',
            align: 'center',
            dataIndex: 'notjoining'
          },
          {
            title: '组织机构编码',
            align: 'center',
            dataIndex: 'sysOrgCode'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: '/sign/oaSignRule/list',
          delete: '/sign/oaSignRule/delete',
          deleteBatch: '/sign/oaSignRule/deleteBatch',
          exportXlsUrl: 'sign/oaSignRule/exportXls',
          importExcelUrl: 'sign/oaSignRule/importExcel',
        },
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {}
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>