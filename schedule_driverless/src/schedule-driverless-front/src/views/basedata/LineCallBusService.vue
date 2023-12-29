<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="6" :md="8" :sm="24">
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
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="方向">
              <a-select placeholder="请选择" v-model="queryParam.direction">
                <a-select-option v-for="(item, key) in directionOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="14" :lg="14" :md="24" :sm="24">
            <a-button type="primary" @click="searchQuery2" icon="search" style="margin-bottom: 10px;">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-button type="primary" @click="addData" icon="plus" style="margin-left: 12px;margin-bottom: 10px;">新建
          </a-button>
          <a-button type="primary" @click="editData" icon="edit" style="margin-left: 10px">编辑</a-button>
          <a-button type="primary" @click="deleteData" icon="delete" style="margin-left: 10px">删除</a-button>
          <a-button type="primary" @click="exportExcel" style="margin-left: 10px" :loading="excelLoading">导出excel
          </a-button>
          <a-button type="primary" @click="exportCsv" style="margin-left: 10px" :loading="csvLoading">导出csv</a-button>
        </a-row>
      </a-form>
    </div>
    <!-- table区域-begin -->
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
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>
    <LineCallBusServiceModal ref="modalForm" @ok="modalFormOk"></LineCallBusServiceModal>
  </a-card>
</template>

<!--线路叫车服务管理-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { getAction, delByIdsAction, downloadFileAwait } from '@/api/manage'
  import LineCallBusServiceModal from './modules/LineCallBusServiceModal'

  export default {
    name: 'LineCallBusService',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect, LineCallBusServiceModal
    },
    data() {
      return {
        excelLoading: false,
        csvLoading: false,
        directionOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '上行',
          value: '0'
        }, {
          label: '下行',
          value: '1'
        },
          {
            label: '无',
            value: '2'
          }
        ],
        oprSelect: undefined,
        routeData: [],
        queryParam: {},
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
            title: '线路',
            align: 'center',
            dataIndex: 'routeName'
          },
          {
            title: '方向',
            align: 'center',
            dataIndex: 'direction',
            customRender: function (text) {
              if (text === '0') {
                return '上行';
              } else if (text === '1') {
                return '下行';
              } else if (text === '2') {
                return '无';
              }
            }
          },
          {
            title: '叫车服务时间',
            align: 'center',
            dataIndex: 'serviceTime'
          },
          {
            title: '有效需求时间',
            align: 'center',
            dataIndex: 'effectiveTime'
          },
          {
            title: '票价',
            align: 'center',
            dataIndex: 'price'
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark'
          }
        ],
        url: {
          list: '/baseData/lineCallBusService/list',
          delete: '/baseData/lineCallBusService/delete',
          export: '/baseData/lineCallBusService/export',
          routeList: '/base/lineCallBusRule/routeList'
        }
      }
    },
    created() {
      this.queryListRoute()
    },
    methods: {
      searchQuery2() {
        this.isorter = {}
        this.searchQuery()
      },
      searchReset() {
        this.oprSelect = undefined
        this.queryParam = {}
        this.loadData(1)
      },
      queryListRoute() {
        getAction(this.url.routeList).then((res) => {
          if (res.success) {
            this.routeData = res.result
          }
        })
      },
      handleChangeRoute(value) {
        this.queryParam.routeId = value
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      },
      async exportCsv() {
        this.csvLoading = true
        let exportType = { exportType: 'csv' }
        let params = Object.assign(exportType, this.queryParam)
        console.log('params', params)
        await downloadFileAwait(this.url.export, '线路叫车服务管理.csv', params)
        this.csvLoading = false
      },
      async exportExcel() {
        this.excelLoading = true
        let exportType = { exportType: 'excel' }
        let params = Object.assign(exportType, this.queryParam)
        console.log('params', params)
        await downloadFileAwait(this.url.export, '线路叫车服务管理.xlsx', params)
        this.excelLoading = false
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
      addData() {
        this.$refs.modalForm.add({}, this.routeData, 'add')
        this.$refs.modalForm.title = '新增'
        this.$refs.modalForm.disableSubmit = false
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
        this.$refs.modalForm.edit(this.selectionRows[0], this.routeData, 'edit')
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.disableSubmit = false
      },
      modalFormOk() {
         this.onClearSelected()
        // 新增/修改 成功时，重载列表
        this.loadData();
      },
      convertOperator(text) {
        if (text === '1') {
          return '>';
        } else if (text === '2') {
          return '>=';
        } else if (text === '3') {
          return '<';
        } else if (text === '4') {
          return '<=';
        } else if (text === '5') {
          return '=';
        } else if (text === '6') {
          return '[A,B]';
        } else if (text === '7') {
          return '(A,B)';
        } else if (text === '99') {
          return '无';
        }
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
      }

    }
  }
</script>

<style lang="less" scoped>
</style>
