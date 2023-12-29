<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="5" :lg="5" :md="8" :sm="24">
            <a-form-item label="运营日期">
              {{ queryParam.runDate }}
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect v-model="queryParam.sysOrgCode" placeholder="请选择所属机构" @changeOptions="queryListRoute" :isGetOption="true"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="日夜班">
              <a-select v-model="queryParam.tripsType">
                <a-select-option v-for="(item, key) in dayOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-operator" style="margin-top:-5px;margin-bottom: 10px">
      <a-button type="primary" @click="searchQuery2" icon="search">查询</a-button>
      <a-button type="primary" @click="searchReset"icon="reload" style="margin-left: 10px">重置</a-button>
      <a-button type="primary" @click="exportExcel" icon="download" :loading="excelLoading" style="margin-left: 10px">导出</a-button>
    </div>
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="rowIndex"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>
  </a-card>
</template>

<!--实时首末班发班时间查询-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import moment from 'moment/moment'
  import { getAction, downloadFileAwait } from '@/api/manage'
  export default {
    name: 'FirstLastSetImmediately',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect
    },
    data() {
      return {
        excelLoading: false,
        oprSelect: undefined,
        dayOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '日班',
          value: '0'
        }, {
          label: '夜班',
          value: '1'
        },
          {
            label: '全班',
            value: '2'
          }
        ],
        queryParam: {
          tripsType: '',
          runDate: moment(new Date()).format('YYYY-MM-DD')
        },
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '机构名称',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '运营线路',
            align: 'center',
            dataIndex: 'routeName'
          },
          {
            title: '上行首班时间',
            align: 'center',
            dataIndex: 'upFirstStart'
          },
          {
            title: '上行末班时间',
            align: 'center',
            dataIndex: 'upLastStart'
          },
          {
            title: '下行首班时间',
            align: 'center',
            dataIndex: 'downFirstStart'
          },
          {
            title: '下行末班时间',
            align: 'center',
            dataIndex: 'downLastStart'
          }
        ],
        url: {
          list: '/base/firstLastSetImmediately/list',
          export: '/base/firstLastSetImmediately/export'
        }
      }
    },
    methods: {
      searchQuery2() {
        this.isorter = {}
        if (this.queryParam.sysOrgCode === undefined || this.queryParam.sysOrgCode === '') {
          this.$message.warning('请选择机构');
          return
        }
        this.searchQuery()
      },
      searchReset() {
        this.oprSelect = undefined
        this.queryParam = {}
        this.$set(this.queryParam, 'tripsType', '')
        this.$set(this.queryParam, 'runDate', moment(new Date()).format('YYYY-MM-DD'))
        this.loadData(1);
      },
      queryListRoute(orgData) {
        this.queryParam.isLeaf = orgData.isLeaf
        this.queryParam.organId = orgData.id
      },
      handleChangeRoute(value) {
        this.queryParam.routeId = value
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        );
      },
      async exportExcel() {
        if (this.queryParam.sysOrgCode === undefined || this.queryParam.sysOrgCode === '') {
          this.$message.warning('请选择机构');
          return
        }
        this.excelLoading = true
        await downloadFileAwait(this.url.export, '实时首末班发班时间查询.xlsx', this.queryParam)
        this.excelLoading = false
      }
    }
  }
</script>

<style  lang="less" scoped>
</style>
