<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="打卡日期">
              <a-range-picker :value="queryParamTime" @change="handleChangeParamTime"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6">
            <a-form-item label="打卡类型">
              <a-select placeholder="请选择打卡类型" :options="signTypeOptions" v-model="queryParam.signType"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="6">
            <a-form-item label="打卡状态">
              <a-select placeholder="请选择打卡状态" :options="signStatusOptions" v-model="queryParam.signStatus"/>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="6">
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
      <a-button @click="handleSignNow" type="primary" icon="plus">立即打卡</a-button>
      <!--<a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>-->
      <a-button type="primary" @click="handleExportXls('考勤打卡信息记录表')" icon="download">导出</a-button>
      <a-button @click="statisticsVisible=true" type="primary" ghost icon="pie-chart">打卡统计</a-button>
      <!--      <a-upload v-if="false" name="file" :showUploadList="false" :multiple="false" <i class="iconfont iconexport" style="margin-right: 10px;"/> :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
      <!--        <a-button type="primary" icon="import">导入</a-button>-->
      <!--      </a-upload>-->
      <a-dropdown v-if="false && selectedRowKeys.length > 0">
        <a-button @click="batchDel" icon="delete" type="primary" ghost>批量删除</a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-alert showIcon type="info" style="margin-bottom: 18px">
        <div slot="message">
          <span>已选择</span>
          <a style="padding: 0 4px;">{{ selectedRowKeys.length }}</a>
          <span>项</span>
          <a-divider type="vertical"/>
          <a @click="onClearSelected">清空</a>
        </div>
      </a-alert>

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

        <template slot="signStatus" slot-scope="text, record">
          <a-tag v-if="text === SignStatusEnum.NO_SIGN.value" :color="record.isPatch ? 'green' : '#D3D3D3'">缺卡</a-tag>
          <a-tag v-else-if="text === SignStatusEnum.NORMAL.value" color="green">正常</a-tag>
          <a-tag v-else-if="text === SignStatusEnum.LATE.value" :color="record.isPatch ? 'green' : 'red'">迟到</a-tag>
          <a-tag v-else-if="text === SignStatusEnum.ABSENT.value" :color="record.isPatch ? 'green' : 'red'">旷工</a-tag>
          <a-tag v-else-if="text === SignStatusEnum.LEAVE_EARLY.value" :color="record.isPatch ? 'green' : 'red'">早退</a-tag>
          <a-tag v-else>{{text}}</a-tag>

          <a-tag v-if="record.isPatch" color="orange">已补卡</a-tag>
        </template>

        <span slot="signRemarks" slot-scope="text">
          <j-ellipsis :value="text" :length="25"/>
        </span>

        <template slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a @click="handleDetail(record)">详情</a>
        </template>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <oa-sign-info-modal ref="modalForm" @ok="modalFormOk"/>
    <!-- 立即打卡 -->
    <oa-sign-now-modal :visible.sync="signNow.visible" @ok="modalFormOk"/>
    <!-- 打卡统计 -->
    <oa-sign-statistics-modal :visible.sync="statisticsVisible"/>

  </a-card>
</template>

<script>
  import moment from 'moment/moment'
  import { SignStatusEnum } from './utils/constant'
  import { getOptionsSignStatus, getOptionsSignType, getSignType } from './utils/util'
  import JEllipsis from '@comp/jeecg/JEllipsis'
  import OaSignNowModal from './modules/OaSignNowModal'
  import OaSignStatisticsModal from './modules/OaSignStatisticsModal'
  import OaSignInfoModal from './modules/OaSignInfoModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'OaSignInfoList',
    mixins: [JeecgListMixin],
    components: { JEllipsis, OaSignNowModal, OaSignInfoModal, OaSignStatisticsModal },
    data() {
      return {
        description: '考勤打卡信息记录表管理页面',
        SignStatusEnum,
        signNow: {
          visible: false,
        },
        signStatusOptions: getOptionsSignStatus(),
        signTypeOptions: getOptionsSignType(),
        queryParamTime: [],
        isorter: {
          column: 'signTime',
          order: 'desc',
        },
        statisticsVisible: false,
        // 表头
        columns: [
          {
            title: '打卡人员',
            key: 'realname',
            align: 'center',
            width: '18%',
            dataIndex: 'realname'
          },
          {
            title: '日期',
            key: 'signTime_date',
            align: 'center',
            width: '18%',
            dataIndex: 'signTime',
            customRender: (text) => moment(text).format('YY/MM/DD')
          },
          {
            title: '时间',
            key: 'signTime_time',
            align: 'center',
            width: '15%',
            dataIndex: 'signTime',
            customRender: (text) => moment(text).format('HH:mm:ss')
          },
          {
            title: '类型',
            align: 'center',
            width: '15%',
            dataIndex: 'signType',
            customRender: (text) => getSignType(text)
          },
          {
            title: '状态',
            align: 'center',
            width: '15%',
            dataIndex: 'signStatus',
            scopedSlots: { customRender: 'signStatus' }
          },
          // {
          //   title: '备注',
          //   align: 'left',
          //   width: '28%',
          //   dataIndex: 'signRemarks',
          //   scopedSlots: { customRender: 'signRemarks' }
          // },
          {
            title: '操作',
            align: 'center',
            width: '120px',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: '/sign/info/list',
          delete: '/sign/info/delete',
          deleteBatch: '/sign/info/deleteBatch',
          exportXlsUrl: 'sign/info/exportXls',
          importExcelUrl: 'sign/info/importExcel',
        },
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      searchReset() {
        this.queryParamTime = []
        this.queryParam = {}
        this.loadData(1)
      },
      handleSignNow() {
        this.signNow.visible = true
      },
      handleEdit(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.disableSubmit=false
      },
      handleDetail(record){
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.disableSubmit=true
      },
      handleChangeParamTime(value) {
        if (value && value.length > 0) {
          this.queryParamTime = value
          this.queryParam.signTimeBegin = moment(value[0]).format('YYYY-MM-DD')
          this.queryParam.signTimeEnd = moment(value[1]).format('YYYY-MM-DD')
        } else {
          this.queryParamTime = []
          this.queryParam.signTimeBegin = null
          this.queryParam.signTimeEnd = null
        }
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>