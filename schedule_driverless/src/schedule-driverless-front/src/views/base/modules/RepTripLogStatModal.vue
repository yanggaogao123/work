<template>
  <div>
    <a-modal
      title="车辆选择"
      v-model="visible"
      @ok="handleOk"
      @cancel="handleCancel"
      centered
      :width="1200"
      :height="680"
      :footer="null"
    >
      <!--表单搜索区域-->
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :xl="5" :lg="6" :md="6" :sm="24">
              <a-form-item label="所属线路">
                <GCIRouteSelect
                  v-model="queryParam.routeId"
                  placeholder="请选择线路"
                  mode="mode"
                  item="id"
                  :width="145"
                  @change="fieldChange"></GCIRouteSelect>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="5" :md="5" :sm="24">
              <a-form-item label="车辆编码">
                <a-input placeholder="请输入编码" v-model="queryParam.busCode" @change="fieldChange"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="5" :md="5" :sm="24">
              <a-form-item label="车辆牌照">
                <a-input placeholder="请输入车牌" v-model="queryParam.numberPlate" @change="fieldChange"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="5" :lg="5" :md="5" :sm="24">
              <a-form-item label="终端ID">
                <a-input placeholder="请输入终端ID" v-model="queryParam.obuId" @change="fieldChange"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="3" :lg="3" :md="3" :sm="24">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div class="table-container">
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="busId"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          :customRow="clickRow"
          class="j-table-force-nowrap"
          @change="handleTableChange"
          style="display: inline-block;width: 900px">
          <template slot="htmlSlot" slot-scope="text">
            <div v-html="text"></div>
          </template>
        </a-table>
        <div style="position: relative;height: 560px">
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="busId"
            :columns="columns2"
            :dataSource="dataSource2"
            :pagination="false"
            class="j-table-force-nowrap"
            style="display: inline-block; width: 255px;height: 508px;margin-left: 30px"
            :scroll="{y:415}">
            <span slot="action" slot-scope="text, record">
              <a-icon style="color:red" type="delete" @click="deleteBus(record)" />
            </span>
          </a-table>
          <div style="position: absolute; right: 28px; bottom: 0">
            <a-button type="primary" @click="clearAll" style="margin-right: 30px" >清空</a-button>
            <a-button type="primary" @click="handleOk">确定</a-button>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>
<script>
  import { filterObj } from '@/utils/util'
  import GCIRouteSelect from '@/components/gci/GCIRouteSelect'
  import { queryBusList } from '@/api/api'

  export default {
    name: 'GCIBusSelectWin',
    props: {
      value: String,
      routeId: [String, Number]
    },
    components: { GCIRouteSelect },
    data() {
      return {
        isChange: false,
        visible: false,
        loading: false,
        dataSource: [],
        dataSource2: [],
        // 查询条件
        queryParam: {},
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          total: 0
        },
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '车辆编码',
            align: 'center',
            dataIndex: 'busCode'
          },
          {
            title: '车辆牌照',
            align: 'center',
            dataIndex: 'numberPlate'
          },
          {
            title: '车辆名称',
            align: 'center',
            dataIndex: 'busName'
          },
          {
            title: '所属机构',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '所属线路',
            align: 'center',
            dataIndex: 'routeName'
          },
          {
            title: '终端ID',
            align: 'center',
            dataIndex: 'obuId'
          },
          {
            title: '是否启用',
            align: 'center',
            dataIndex: 'isActive',
            customRender: function (text) {
              return text === '0' ? '是' : '否';
            }
          }
        ],
        columns2: [{
          title: '已选择的车辆',
          children: [
            {
              title: '#',
              dataIndex: '',
              key: 'rowIndex',
              width: 40,
              align: 'center',
              customRender: function(t, r, index) {
                return parseInt(index) + 1
              }
            }, {
              title: '车辆编码',
              align: 'center',
              dataIndex: 'busCode'
            },
            {
              title: '所属线路',
              align: 'center',
              dataIndex: 'routeName'
            },
            {
              title: '操作',
              dataIndex: 'action',
              align: 'center',
              width: 45,
              scopedSlots: { customRender: 'action' }
            }
          ]
        }]
      }
    },
    created() {
      this.loadData();
    },
    methods: {
      fieldChange() {
        this.isChange = true;
      },
      open() {
        this.visible = true;
        this.loadData();
      },
      searchQuery() {
        if (this.isChange) {
          this.ipagination.current = 1;
        }
        this.loadData();
      },
      searchReset() {
        this.routeId = undefined;
        this.queryParam = {}
        this.loadData()
      },
      loadData(arg) {
        if (this.routeId && !this.queryParam.routeId) {
          this.queryParam.routeId = this.routeId
        }
        // 加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        // 查询条件
        var params = this.getQueryParams();
        queryBusList(params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
        })
      },
      getQueryParams() {
        var param = Object.assign({}, this.queryParam);
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      handleTableChange(pagination, filters, sorter) {
        this.ipagination = pagination;
        this.loadData();
      },
      handleOk() {
        this.queryParam.routeId = undefined;
        this.visible = false;
        let busCodes = this.dataSource2.map((item) => {
          return item.busCode;
        })
        this.$emit('ok', busCodes);
      },
      handleCancel() {
        this.queryParam.routeId = undefined
        this.visible = false
         let busCodes = this.dataSource2.map((item) => {
            return item.busCode;
          })
        this.$emit('ok', busCodes);
      },
      clearAll() {
        this.dataSource2 = []
      },
      clickRow(record, index) {
        return {
          on: {
            click: () => {
              let result = this.dataSource2.findIndex(s => s.busId === record.busId)
              if (result < 0) {
                this.dataSource2.push(record)
              }
            }
          }
        }
      },
      deleteBus(record) {
        let busId = record['busId']
        this.dataSource2.splice(this.dataSource2.findIndex(item => item.busId === busId), 1)
      }
    }
  }
</script>
<style lang="less" scoped>
  .table-container {
    margin-top: 10px;
    display: flex;
  }
</style>
