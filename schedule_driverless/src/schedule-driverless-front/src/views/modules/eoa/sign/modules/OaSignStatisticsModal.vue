<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="loading"
    :style="{top:'20px'}"
    :footer="false"
    @cancel="handleCancel">

    <a-spin :spinning=" loading" size="large" class="spin">

      <a-form layout="horizontal">
        <a-form-item label="统计月份" :labelCol="{span:4}" :wrapperCol="{span:20}">
          <a-month-picker v-model="monthValue" placeholder="请选择统计月" format="YYYY年MM月"/>
        </a-form-item>
      </a-form>

      <a-tabs v-model="tabKey">
        <a-tab-pane tab="柱状图" key="bar">
          <bar :dataSource="barDataSource"/>
        </a-tab-pane>
        <a-tab-pane tab="饼图" key="pie">
          <pie :dataSource="pieDataSource"/>
        </a-tab-pane>
      </a-tabs>

      <a-divider orientation="left">详细数据</a-divider>

      <a-form layout="horizontal">
        <a-row :gutter="10">
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="预期工作" read-only v-model="data.expectedWorkingDays" addonAfter="天"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="实际工作" read-only v-model="data.actualWorkingDays" addonAfter="天"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="本月总共" read-only v-model="data.monthDayTotal" addonAfter="天"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="10">
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="正常次数" read-only v-model="data.normalTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="迟到次数" read-only v-model="data.lateTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="旷工次数" read-only v-model="data.absentTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="10">
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="早退次数" read-only v-model="data.leaveEarlyTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="请假天数" read-only v-model="data.leaveTotal" addonAfter="天"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="外勤次数" read-only v-model="data.outTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="10">
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="出差天数" read-only v-model="data.businessTravelTotal" addonAfter="天"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="补卡次数" read-only v-model="data.patchTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="" v-bind="formItemProps">
              <a-input addonBefore="缺卡次数" read-only v-model="data.missingTotal" addonAfter="次"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>

    </a-spin>

  </a-modal>
</template>

<script>
  import moment from 'moment'
  import { getAction } from '@api/manage'
  import Pie from '@/components/chart/Pie'
  import Bar from '@/components/chart/Bar'

  export default {
    name: 'OaSignStatisticsModal',
    components: { Pie, Bar },
    props: {
      title: {
        type: String,
        default: '考勤统计'
      },
      visible: {
        type: Boolean,
        default: false
      },
      username: {
        type: String,
        default: ''
      },
      month: {
        type: Object,
        default: () => moment()
      },
    },
    data() {
      return {
        loading: false,
        tabKey: 'bar',
        monthValue: this.month,
        data: {},
        pieDataSource: [],
        barDataSource: [],
        formItemProps: {
          labelCol: { md: 24, sm: 24 },
          wrapperCol: { md: 24, sm: 24 }
        },
        url: {
          statisticsByMonth: '/sign/statistics/statisticsByMonth',
        },
      }
    },
    computed: {},
    watch: {
      visible() {
        if (this.visible) {
          this.queryStatisticsByMonth()
        }
      },
      month() {
        this.monthValue = this.month
      },
      monthValue() {
        if (this.visible) {
          this.queryStatisticsByMonth()
        }
      }
    },
    created() {
    },
    methods: {

      queryStatisticsByMonth() {
        this.loading = true
        let date = this.monthValue
        getAction(this.url.statisticsByMonth, {
          username: this.username ? this.username : undefined,
          year: date.format('YYYY'),
          month: date.format('MM')
        }).then(res => {
          if (res.success) {
            let { normalTotal, lateTotal, absentTotal, leaveEarlyTotal, missingTotal, patchTotal, leaveTotal, outTotal, businessTravelTotal } = res.result
            // 饼图
            let pieDataSource = []
            pieDataSource.push({ item: '正常打卡', count: normalTotal })
            pieDataSource.push({ item: '迟到', count: lateTotal })
            pieDataSource.push({ item: '旷工', count: absentTotal })
            pieDataSource.push({ item: '早退', count: leaveEarlyTotal })
            pieDataSource.push({ item: '补卡', count: patchTotal })
            pieDataSource.push({ item: '外勤', count: outTotal })
            pieDataSource.push({ item: '出差', count: businessTravelTotal })
            pieDataSource.push({ item: '请假', count: leaveTotal })
            pieDataSource.push({ item: '缺卡', count: missingTotal })
            this.pieDataSource = pieDataSource
            // 柱状图
            this.barDataSource = pieDataSource.map(d => ({ x: d.item, y: d.count }))
            this.data = res.result
          } else throw res.message
        }).catch(e => {
          this.$warning({ title: '考勤统计查询失败', content: (e || {}).message || e })
          this.close()
        }).finally(() => {
          this.loading = false
        })
      },

      close(ok) {
        if (!this.loading) {
          this.tabKey = 'bar'
          if (ok) this.$emit('ok')
          this.$emit('update:visible', false)
        }
      },
      handleCancel() {
        this.close()
      },
    }

  }
</script>

<style lang="less" scoped>
  .spin {
    min-height: 320px;
    min-width: 100%;
    overflow: hidden;
  }
</style>