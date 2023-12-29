<template>
  <div class="sub" v-show="showModal">
    <a-card :bordered="false">
      <div style="display: inline-block;color: #1890ff;">
        线路发班时段明细 ---- {{ this.subRecord.routeName }}( {{ this.subRecord.beginDate }} - {{ this.subRecord.endDate }} )
        {{ this.subRecord.ruleName }}
      </div>
      <a-button type="primary" @click="closeSub" style="margin-left: 20px">返回</a-button>
      <a-button type="primary" @click="addData" style="margin-left: 10px" v-has="'1080:edit'">新建</a-button>
      <a-button type="primary" @click="editData" style="margin-left: 10px" v-has="'1080:edit'">编辑</a-button>
      <a-button type="primary" @click="deleteSub" style="margin-left: 10px" v-has="'1080:edit'">删除</a-button>

      <div class="table-container">
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="rrSubId"
          :pagination="false"
          :columns="columns"
          :loading="loading"
          :dataSource="dataSource1"
          :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
          style="display: inline-block;">
          <template slot="title1">{{ this.stationName.fromStartStaion }} -> {{ this.stationName.fromEndStaion }}</template>
        </a-table>
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="rrSubId"
          :pagination="false"
          :columns="columns"
          :loading="loading"
          :dataSource="dataSource2"
          :rowSelection="{selectedRowKeys: selectedRowKeys2, onChange: onSelectChange2}"
          style="display: inline-block; margin-left: 20px"
        >
          <template v-if="column2" slot="title1">{{ this.stationName.toStartStaion }} -> {{ this.stationName.toEndStation }}</template>
        </a-table>
      </div>
      <route-rule-sub-modal ref="routeRuleSubModal" @ok="modalFormOk"></route-rule-sub-modal>
    </a-card>
  </div>
</template>

<!--行业发班规则详情设置-->
<script>
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { getAction, delByIdsAction } from '@/api/manage'
  import routeRuleSubModal from './RouteRuleSubModal'
  export default {
    name: 'RouteRuleSub',
    components: {
      GCIOrgTreeSelect, routeRuleSubModal
    },
    data() {
      return {
        showModal: false,
        loading: false,
        subRecord: {},
        stationName: {},
        column2: true,
        dataSource: [],
        dataSource1: [],
        dataSource2: [],
        selectedRowKeys1: [],
        selectedRowKeys2: [],
        selectionRows1: [],
        selectionRows2: [],
        columns: [
          {
            scopedSlots: {
              title: 'title1'
            },
            children: [
              {
                title: '时段开始',
                align: 'center',
                dataIndex: 'timeSegmentBegin'
              },
              {
                title: '时段结束',
                align: 'center',
                dataIndex: 'timeSegmentEnd'
              },
              {
                title: '发班间隔(分钟)',
                align: 'center',
                dataIndex: 'interval'
              },
              {
                title: '最大发班数',
                align: 'center',
                dataIndex: 'tripMax'
              },
              {
                title: '最低发班数',
                align: 'center',
                dataIndex: 'tripMin'
              },
              {
                title: '准点允许误差范围(分钟)',
                align: 'center',
                dataIndex: 'pointRange'
              }]
          }
        ],
        url: {
          list: '/base/routeRuleSub/list',
          add: '/base/routeRuleSub/add',
          edit: '/base/routeRuleSub/edit',
          delete: '/base/routeRuleSub/delete',
          routeInfo: '/common/sys/queryRouteByOrganId'
        }
      }
    },
    methods: {
      searchQuery() {
        this.dataSource1 = []
        this.dataSource2 = []
        let params = { routeRuleId: this.subRecord.routeRuleId, routeId: this.subRecord.routeId }
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.repRouteRuleSubList
            this.stationName.fromStartStaion = res.result.fromStartStaion
            this.stationName.fromEndStaion = res.result.fromEndStaion
            this.stationName.toStartStaion = res.result.toStartStaion
            this.stationName.toEndStation = res.result.toEndStation
            for (let i in this.dataSource) {
              if (this.dataSource[i].direction === '0') {
                this.dataSource1.push(this.dataSource[i])
              } else if (this.dataSource[i].direction === '1') {
                this.dataSource2.push(this.dataSource[i])
              }
            }
            // console.log(this.dataSource.filter(item => item.direction === '0'))
          }
          this.loading = false
        }).catch(e => {
          this.$message.error('服务错误')
          this.loading = false
        })
      },
      showSub(record) {
        this.loading = true
        this.stationName = {}
        this.subRecord.routeRuleId = record.routeRuleId
        this.subRecord.routeId = record.routeId
        this.subRecord = record
        if (record.ruleType === '0') {
          this.subRecord.ruleName = '所有适用'
        } else if (record.ruleType === '1') {
          this.subRecord.ruleName = '平时'
        } else if (record.ruleType === '2') {
          this.subRecord.ruleName = '周末'
        } else if (record.ruleType === '3') {
          this.subRecord.ruleName = '节假日'
        }
        this.searchQuery()
        this.showModal = !this.showModal
      },
      closeSub() {
        this.showModal = !this.showModal
        this.subRecord = []
        this.clearSelect()
      },
      onSelectChange1(selectedRowKeys, selectionRows) {
        this.selectedRowKeys1 = selectedRowKeys;
        this.selectionRows1 = selectionRows;
      },
      onSelectChange2(selectedRowKeys, selectionRows) {
        this.selectedRowKeys2 = selectedRowKeys;
        this.selectionRows2 = selectionRows;
      },
      deleteSub() {
        if (this.selectedRowKeys1.length <= 0 && this.selectedRowKeys2.length <= 0) {
          this.$message.warning('请选择需要删除的数据')
          return
        }
        this.$confirm({
          content: '是否删除选中的数据?',
          onOk: () => {
            this.handleDeleteData()
          }
        })
      },
      handleDeleteData() {
        let ids = [...this.selectedRowKeys1, ...this.selectedRowKeys2]
        delByIdsAction(this.url.delete, ids).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.clearSelect()
            this.searchQuery()
          } else {
            this.$message.error(res.message)
          }
        })
      },
      addData() {
        this.$refs.routeRuleSubModal.add({}, 'add', this.subRecord.routeRuleId)
        this.$refs.routeRuleSubModal.title = '新增'
        this.$refs.routeRuleSubModal.disableSubmit = false
      },
      editData() {
        if (this.selectedRowKeys1.length <= 0 && this.selectedRowKeys2.length <= 0) {
           this.$message.warning('请选择需要编辑的数据')
           return
        }
        if (this.selectedRowKeys1.length > 1 || this.selectedRowKeys2.length > 1 ||
          (this.selectedRowKeys1.length === 1 && this.selectedRowKeys2.length === 1)) {
          this.$message.warning('编辑只能选择一条数据')
          return
        }
        if (this.selectedRowKeys1.length === 1) {
          this.$refs.routeRuleSubModal.edit(this.selectionRows1[0], 'edit', this.subRecord.routeRuleId)
        } else {
          this.$refs.routeRuleSubModal.edit(this.selectionRows2[0], 'edit', this.subRecord.routeRuleId)
        }
        this.$refs.routeRuleSubModal.title = '编辑'
        this.$refs.routeRuleSubModal.disableSubmit = false
      },
      modalFormOk() {
         this.clearSelect()
        // 新增 修改 成功时，重载列表
        this.searchQuery()
      },
      clearSelect(){
        this.selectedRowKeys1 = []
        this.selectedRowKeys2 = []
        this.selectionRows1 = []
        this.selectionRows2 = []
      }
    }
  }
</script>

<style lang="less" scoped>
  .sub {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    background: #fff;
  }

  .table-container {
    margin-top: 30px;
    display: flex;
  }
</style>
