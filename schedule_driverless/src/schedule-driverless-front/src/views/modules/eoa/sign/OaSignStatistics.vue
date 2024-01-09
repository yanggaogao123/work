<template>
  <a-card :bordered="false">

    <a-form layout="inline">
      <a-form-item label="统计月份">
        <a-month-picker v-model="monthValue" placeholder="请选择统计月" format="YYYY年MM月"/>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleSubmit">立即统计</a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleExport"  icon="download">导出</a-button>
      </a-form-item>
    </a-form>

    <a-alert showIcon type="info" style="margin: 20px 0">
      <div slot="message">
        <span>当前统计的是：</span>
        <a> {{alertMonthShow}} </a>
        <a-divider type="vertical"/>
        <span>该月共有</span>
        <a> {{alertInfo.monthDayTotal}} </a>
        <span>天</span>
        <a-divider type="vertical"/>
        <span>其中工作日共有</span>
        <a> {{alertInfo.expectedWorkingDays}} </a>
        <span>天</span>
      </div>
    </a-alert>

    <a-table
      rowKey="username"
      bordered
      size="middle"
      :loading="loading"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      @change="handleTableChange"
    >

      <template slot="action" slot-scope="text,record">
        <a @click="handleDetail(record)">详情</a>
      </template>

    </a-table>

    <!-- 打卡统计详情 -->
    <oa-sign-statistics-modal :title="title" :username="username" :month="alertMonthValue" :visible.sync="statisticsVisible"/>

  </a-card>
</template>

<script>
  import moment from 'moment'
  import { getAction } from '@api/manage'
  import OaSignStatisticsModal from './modules/OaSignStatisticsModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'OaSignStatistics',
    mixins: [JeecgListMixin],
    components: { OaSignStatisticsModal },
    data() {
      let currentMonth = moment()
      return {
        loading: true,
        dataSource: [],
        columns: [
          {
            title: '姓名',
            align: 'center',
            dataIndex: 'realname'
          },
          {
            title: '工作天数',
            align: 'center',
            dataIndex: 'actualWorkingDays'
          },
          {
            title: '正常打卡',
            align: 'center',
            dataIndex: 'normalTotal'
          },
          {
            title: '迟到天数',
            align: 'center',
            dataIndex: 'lateTotal'
          },
          {
            title: '旷工天数',
            align: 'center',
            dataIndex: 'absentTotal'
          },
          {
            title: '早退天数',
            align: 'center',
            dataIndex: 'leaveEarlyTotal'
          },
          {
            title: '补卡次数',
            align: 'center',
            dataIndex: 'patchTotal'
          },
          {
            title: '外勤天数',
            align: 'center',
            dataIndex: 'outTotal'
          },
          {
            title: '出差天数',
            align: 'center',
            dataIndex: 'businessTravelTotal'
          },
          {
            title: '请假次数',
            align: 'center',
            dataIndex: 'leaveTotal'
          },
          {
            title: '缺卡次数',
            align: 'center',
            dataIndex: 'missingTotal'
          },
          {
            title: '操作',
            key: 'action',
            width: '80px',
            align: 'center',
            scopedSlots: { customRender: 'action' }
          }
        ],
        title: '考勤统计',
        username: null,
        monthValue: currentMonth,
        statisticsVisible: false,
        alertMonthShow: currentMonth.format('YYYY年MM月'),
        alertMonthValue: currentMonth,
        alertInfo: {},
        url: {
          statisticsByMonthAll: '/sign/statistics/statisticsByMonthAll',
          exportXlsUrl: '/sign/statistics/exportXls'
        },
      }
    },
    computed: {},
    created() {
      this.loadData()
    },
    methods: {

      loadData(pageNum) {
        let date = this.monthValue
        if (date == null) {
          this.$message.warn('请选择统计月份')
          return
        }

        if (typeof pageNum === 'number') {
          this.ipagination.current = pageNum
        }

        this.loading = true
        getAction(this.url.statisticsByMonthAll, {
          ...this.getQueryParams(),
          year: date.format('YYYY'),
          month: date.format('MM')
        }).then(res => {
          if (res.success) {
            this.dataSource = res.result.records
            if (this.dataSource.length >= 1) {
              this.alertInfo = this.dataSource[0]
            }
            this.ipagination.total = res.result.total
          } else throw res.message
        }).catch(e => {
          this.$warning({ title: '统计失败，请稍后重试', content: (e || {}).message || e })
        }).finally(() => {
          this.loading = false
        })
      },

      handleSubmit() {
        this.alertMonthShow = this.monthValue.format('YYYY年MM月')
        this.alertMonthValue = this.monthValue
        this.loadData()

      },

      handleExport() {
        let title = '考勤统计 - ' + this.alertMonthValue.format('（YYYY年MM月）')
        this.queryParam = {
          title: title,
          year: this.alertMonthValue.format('YYYY'),
          month: this.alertMonthValue.format('MM')
        }
        this.handleExportXls(title)
      },

      handleDetail(record) {
        this.title = '考勤统计：' + record.realname
        this.username = record.username
        this.statisticsVisible = true
      }

    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>