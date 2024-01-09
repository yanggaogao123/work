<template>
  <div class="container">
  <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="所属机构">
                <GCIOrgTreeSelect
                  v-model="queryParam.sysOrgCode"
                  placeholder="请选择所属机构"
                  @changeOptions="queryListRoute"
                  :isGetOption="true"></GCIOrgTreeSelect>
              </a-form-item>
            </a-col>
            <a-col :xl="4" :lg="4" :md="6" :sm="24">
              <a-form-item label="线路">
                <a-select
                  allowClear
                  placeholder="请选择线路"
                  v-model="oprSelect"
                  show-search
                  :filter-option="addFilterOption"
                  @change="handleChangeRoute">
                  <a-select-option v-for="(item,index) in routeData" :key="index" :value="item.id">
                    {{ item.routeName }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="14" :lg="24" :md="24" :sm="24">
              <a-button type="primary" @click="searchQuery2" icon="search" style="margin-bottom: 10px;">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
              <a-button type="primary" @click="addData" icon="plus" style="margin-left: 10px;" v-has="'1080:edit'">新建</a-button>
              <a-button type="primary" @click="editData" icon="edit" style="margin-left: 10px" v-has="'1080:edit'">编辑</a-button>
              <a-button type="primary" @click="deleteData" icon="delete" style="margin-left: 10px" v-has="'1080:edit'">删除</a-button>
              <a-button type="primary" @click="exportExcel" style="margin-left: 10px" :loading="excelLoading">导出</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div>
        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
          <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
            selectedRowKeys.length }}</a>项
          <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        </div>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="routeRuleId"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">
        <span slot="action" slot-scope="text, record">
          <a @click="showRouteRuleSub(record)">查看详情</a>
        </span>
      </a-table>
      <route-rule-modal ref="routeRuleModal" @ok="modalFormOk"></route-rule-modal>
      <route-rule-sub ref="routeRuleSub"></route-rule-sub>
  </a-card>
  </div>
</template>

<!--行业发班规则设置-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { getAction, delByIdsAction, downloadFileAwait } from '@/api/manage'
  import routeRuleSub from './modules/RouteRuleSub'
  import routeRuleModal from './modules/RouteRuleModal'

  export default {
    name: 'RouteRule',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect, routeRuleSub, routeRuleModal
    },
    data() {
      return {
        excelLoading: false,
        oprSelect: undefined,
        queryParam: {},
        routeData: [],
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '线路名称',
            align: 'center',
            dataIndex: 'routeName'
          },
          {
            title: '所属机构',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '开始日期',
            align: 'center',
            dataIndex: 'beginDate'
          },
          {
            title: '结束日期',
            align: 'center',
            dataIndex: 'endDate'
          },
          {
            title: '适用日类型',
            align: 'center',
            dataIndex: 'ruleType',
            customRender: function(text) {
              if (text === '0') {
                return '所有适用'
              } else if (text === '1') {
                return '平时'
              } else if (text === '2') {
                return '周末'
              } else if (text === '3') {
                return '节假日'
              }
            }
          },
          {
            title: '首站首班',
            align: 'center',
            dataIndex: 'fromFirTime'
          },
          {
            title: '首站末班',
            align: 'center',
            dataIndex: 'fromLasTime'
          },
          {
            title: '末站首班',
            align: 'center',
            dataIndex: 'toFirTime'
          },
          {
            title: '末站末班',
            align: 'center',
            dataIndex: 'toLasTime'
          },
          {
            title: '状态',
            align: 'center',
            dataIndex: 'status',
            customRender: function(text) {
              if (text === '0') {
                return '有效'
              } else if (text === '1') {
                return '无效'
              }
            }
          },
          {
            title: '操作',
            align: 'center',
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: '/base/routeRule/list',
          listRoute: '/common/sys/queryRouteByOrganId',
          delete: '/base/routeRule/delete',
          export: '/base/routeRule/export'
        }
      }
    },
    methods: {
      searchQuery2() {
        this.isorter = {}
        this.searchQuery()
      },
      searchReset() {
        this.oprSelect = undefined
        this.routeData = []
        this.queryParam = {}
        this.loadData(1)
      },
      queryListRoute(orgData) {
        if (orgData === undefined) {
          this.routeData = []
          this.queryParam.isLeaf = ''
          this.queryParam.organId = ''
        }
       if (orgData !== undefined) {
         this.queryParam.isLeaf = orgData.isLeaf
         this.queryParam.organId = orgData.id
         let params = { organId: this.queryParam.organId, isLeaf: this.queryParam.isLeaf }
         getAction(this.url.listRoute, params).then((res) => {
           if (res.success) {
             this.routeData = res.result
           }
         })
       }
      },
      handleChangeRoute(value) {
        this.queryParam.routeId = value
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      },
      addData() {
        this.$refs.routeRuleModal.add({}, 'add')
        this.$refs.routeRuleModal.title = '新增'
        this.$refs.routeRuleModal.disableSubmit = false
      },
      editData() {
        if (this.selectionRows.length <= 0) {
          this.$message.warning('请选择需要编辑的数据')
          return
        }
        if (this.selectionRows.length !== 1) {
          this.$message.warning('编辑只能选择一条数据')
          return
        }
        this.$refs.routeRuleModal.edit(this.selectionRows[0], 'edit')
        this.$refs.routeRuleModal.title = '编辑'
        this.$refs.routeRuleModal.disableSubmit = false
      },
      modalFormOk() {
        this.onClearSelected()
        // 新增 修改 成功时，重载列表
        this.loadData()
      },
      deleteData() {
        if (this.selectedRowKeys.length <= 0) {
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
        let ids = this.selectedRowKeys
        delByIdsAction(this.url.delete, ids).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.onClearSelected()
            this.searchQuery()
          } else {
            this.$message.error(res.message)
          }
        })
      },
      async exportExcel() {
        this.excelLoading = true
        await downloadFileAwait(this.url.export, '行业发班规则设置.xlsx', this.queryParam)
        this.excelLoading = false
      },
      showRouteRuleSub(record) {
        this.$refs.routeRuleSub.showSub(record)
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
      }
    }
  }
</script>

<style lang="less" scoped>
  .container {
    position: relative;
  }
</style>
