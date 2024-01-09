<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="运营日期">
              {{ todayRunTime }}
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="统计方式">
              <a-radio-group v-model="searchType" @change="countTypeChange">
                <a-radio v-for="(item,index) in searchOptions" :key="index" :value="item.value">{{ item.label }}
                </a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24" v-show="searchType ==='sum'?true:false">
            <a-form-item label="类型" >
              <a-radio-group v-model="sumType">
                <a-radio v-for="(item,index) in sumOptions" :key="index" :value="item.value">{{ item.label }}
                </a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="6" :md="8" :sm="24">
            <a-form-item label="机构">
              <GCIOrgTreeSelect
                v-model="queryParam.orgCode"
                :isGetOption="true"
                placeholder="请选择所属机构"
                @changeOptions="queryListRoute"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="6" :md="8" :sm="24" v-show="sumType ==='route'?true:false">
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
          <a-col :xl="5" :lg="5" :md="8" :sm="24" v-show="searchType ==='detail'?true:false">
            <a-form-item label="方向">
              <a-select v-model="queryParam.direction">
                <a-select-option v-for="(item, key) in directionOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="5" :md="8" :sm="24" v-show="searchType ==='detail'?true:false">
            <a-form-item label="是否违规">
              <a-select v-model="queryParam.ruleType">
                <a-select-option v-for="(item, key) in ruleOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="8" :lg="10" :md="10" :sm="24">
            <a-button type="primary" @click="searchQuery2" icon="search">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
            <a-button type="primary" @click="exportExcel"  style="margin-left: 10px" :loading="excelLoading">导出</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="rowIndex"
      :columns="searchType === 'sum'? columns1:columns2"
      :dataSource="searchType === 'sum'? dataSource1:dataSource2"
      :pagination="searchType === 'sum'? ipagination1:ipagination2"
      :loading="searchType === 'sum'? loading1:loading2"
      class="j-table-force-nowrap"
      @change="handleTableChange"
      style="margin-top: 10px">
      <template slot="difference" slot-scope="text,record" >
        <span :style="record.violationMark === '1' ? 'color:red':''">{{ text }}</span>
      </template>
    </a-table>
  </a-card>
</template>

<!--实时服从率统计-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  // import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { getAction, downloadFileAwait } from '@/api/manage'
  import moment from 'moment'

  export default {
    name: 'RepTripTime',
    mixins: [ mixinDevice],
    components: {
      GCIOrgTreeSelect
    },
    data() {
      return {
        excelLoading: false,
        todayRunTime: moment().format('YYYY-MM-DD'),
        searchType: 'sum',
        sumType: 'organ',
        searchOptions: [{
          label: '汇总',
          value: 'sum'
        }, {
          label: '明细',
          value: 'detail'
        }],
        sumOptions: [{
          label: '按机构',
          value: 'organ'
        }, {
          label: '按线路',
          value: 'route'
        }],
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
        ruleOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '是',
          value: '1'
        }, {
          label: '否',
          value: '0'
        },
          {
            label: '不明',
            value: '2'
          }
        ],
        /* 数据源 */
        dataSource1: [],
        /* 数据源 */
        dataSource2: [],
        /* table加载状态 */
        loading1: false,
        loading2: false,
        /* 分页参数 */
        ipagination1: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          // showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        /* 分页参数 */
        ipagination2: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          // showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        oprSelect: undefined,
        routeData: [],
        queryParam: {
          direction: '',
          ruleType: ''
        },
        // 表头
        columns1: [
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
            title: '机构名称',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '准点次数',
            align: 'center',
            dataIndex: 'punctuality'
          },
          {
            title: '不准点次数',
            align: 'center',
            dataIndex: 'notpunctuality'
          },
          {
            title: '服从率',
            align: 'center',
            dataIndex: 'punctualityRate'
          }
        ],
        columns2: [
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
            title: '机构名称',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '线路名称',
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
            title: '车辆牌照',
            align: 'center',
            dataIndex: 'numberPlate'
          },
          {
            title: '车辆编码',
            align: 'center',
            dataIndex: 'busCode'
          },
          {
            title: '资格证',
            align: 'center',
            dataIndex: 'qualification'
          },
          {
            title: '驾驶员',
            align: 'center',
            dataIndex: 'employeeName'
          },
          {
            title: '站台名称',
            align: 'center',
            dataIndex: 'stationName'
          },
          {
            title: '服务类型',
            align: 'center',
            dataIndex: 'serviceName'
          },
          {
            title: '调度开始时间',
            align: 'center',
            dataIndex: 'timePlan'
          },
          {
            title: 'GPS出站时间',
            align: 'center',
            dataIndex: 'timeReal'
          },
          {
            title: '执行差(时分秒)',
            align: 'center',
            scopedSlots: { customRender: 'difference' },
            dataIndex: 'difference'
          },
          {
            title: '允许差(秒)',
            align: 'center',
            dataIndex: 'differenceAllow'
          },
          {
            title: '是否违规',
            align: 'center',
            dataIndex: 'violationMark',
            customRender: function (text) {
              if (text === '1') {
                return <span style="color: red" >是</span>;
              } else if (text === '0') {
                return '否';
              } else if (text === '2') {
                return '不明';
              }
            }
          }
        ],
        url: {
          list: '/base/repTripTime/list',
          export: '/base/repTripTime/export',
          listRoute: '/common/sys/queryRouteByOrganId'
        }
      }
    },
    created() {
      this.loadData()
    },
    methods: {
      loadData(arg) {
        // 加载数据 若传入参数1则加载第一页的内容
        let ipagination;
        // 处理分页和表格列
        if (this.searchType === 'sum') {
          if (this.sumType === 'organ') {
            this.columns1 = this.columns1.filter((item) => item.dataIndex !== 'routeName')
          } else {
            let routeFlag = this.columns1.findIndex((item) => {
              return item.dataIndex === 'routeName'
            })
            if (routeFlag === -1) {
              let routeColumn = { title: '线路名称', align: 'center', dataIndex: 'routeName' }
              this.columns1.splice(2, 0, routeColumn);
            }
          }
          this.loading1 = true;
          if (arg === 1) {
            this.ipagination1.current = 1
          }
         ipagination = { pageNo: this.ipagination1.current, pageSize: this.ipagination1.pageSize }
        } else if (this.searchType === 'detail') {
          this.loading2 = true
          if (arg === 1) {
            this.ipagination2.current = 1
          }
          ipagination = { pageNo: this.ipagination2.current, pageSize: this.ipagination2.pageSize }
        }
        let type = { searchType: this.searchType, sumType: this.sumType }
        let params = Object.assign(this.queryParam, type, ipagination)
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            if (this.searchType === 'sum') {
              this.dataSource1 = res.result.records;
              this.ipagination1.total = res.result.total;
            } else if (this.searchType === 'detail') {
              this.dataSource2 = res.result.records;
              this.ipagination2.total = res.result.total;
            }
          }
          if (res.code === 510) {
            this.$message.error(res.message)
          }
          this.loading1 = false;
          this.loading2 = false;
        })
      },
      searchQuery2() {
        this.loadData()
      },
      searchReset() {
        this.oprSelect = undefined
        this.queryParam = {}
        this.$set(this.queryParam, 'direction', '')
        this.$set(this.queryParam, 'ruleType', '')
      },
      queryListRoute(orgData) {
        this.queryParam.isLeaf = orgData.isLeaf
        this.queryParam.organId = orgData.id
        let params = { organId: this.queryParam.organId, isLeaf: this.queryParam.isLeaf }
        getAction(this.url.listRoute, params).then((res) => {
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
      countTypeChange() {
        this.oprSelect = undefined
        this.queryParam = {}
        this.$set(this.queryParam, 'direction', '')
        this.$set(this.queryParam, 'ruleType', '')
        if (this.searchType === 'sum') {
          this.sumType = 'organ'
        } else {
          this.sumType = 'route'
        }
      },
      handleTableChange(pagination, filters, sorter) {
        let temp = JSON.parse(JSON.stringify(pagination));
        if (this.searchType === 'sum') {
          this.ipagination1 = temp;
        } else if (this.searchType === 'detail') {
          this.ipagination2 = temp;
        }
        this.loadData();
      },
      async exportExcel() {
        this.excelLoading = true
        let type = { searchType: this.searchType, sumType: this.sumType }
        let params = Object.assign(this.queryParam, type)
        console.log('params', params)
        await downloadFileAwait(this.url.export, '实时服从率统计.xlsx', params)
        this.excelLoading = false
      }
    }
  }
</script>

<style lang="less" scoped>
  .ant-input-number /deep/ .ant-input-number-handler-wrap {
    visibility: hidden;
  }
</style>
