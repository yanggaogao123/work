<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect
                v-model="queryParam.orgCode"
                :isGetOption="true"
                placeholder="请选择所属机构"
                @changeOptions="queryListRoute"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="5" :md="8" :sm="24">
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
              <a-select v-model="queryParam.direction">
                <a-select-option v-for="(item, key) in directionOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="7" :lg="7" :md="8" :sm="24">
            <a-button type="primary" @click="searchQuery2" icon="search">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
          </a-col>
        </a-row>
      </a-form>
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
      class="j-table-force-nowrap"
      @change="handleTableChange">
      <template slot="edRouteName" slot-scope="text,record" >
        <span :style="record.markRouteName === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="bsRouteName" slot-scope="text,record" >
        <span :style="record.markRouteName === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="edRouteStationName" slot-scope="text,record" >
        <span :style="record.markRouteStationName === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="bsRouteStationName" slot-scope="text,record" >
        <span :style="record.markRouteStationName === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="edOrderNumber" slot-scope="text,record" >
        <span :style="record.markOrderNumber === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="bsOrderNumber" slot-scope="text,record" >
        <span :style="record.markOrderNumber === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="edBusStopCode" slot-scope="text,record" >
        <span :style="record.markBusStopCode === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="bsBusStopCode" slot-scope="text,record" >
        <span :style="record.markBusStopCode === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="edRoutesubInfo" slot-scope="text,record" >
        <span :style="record.markRoutesubInfo === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="bsRoutesubInfo" slot-scope="text,record" >
        <span :style="record.markRoutesubInfo === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="edMileage" slot-scope="text,record" >
        <span :style="record.markMileage === '1' ? 'color:red':''">{{ text }}</span>
      </template>
      <template slot="bsMileage" slot-scope="text,record" >
        <span :style="record.markMileage === '1' ? 'color:red':''">{{ text }}</span>
      </template>
    </a-table>
  </a-card>
</template>

<!--线路信息对照-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { getAction } from '@/api/manage'
  export default {
    name: 'RouteInfo',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect
    },
    data() {
      return {
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
        queryParam: {
          direction: ''
        },
        // 表头
        columns: [
          {
            title: '编辑表(ED)数据',
            children: [
              {
                title: '线路',
                align: 'center',
                dataIndex: 'edRouteName',
                scopedSlots: { customRender: 'edRouteName' }
              },
              {
                title: '站台编码',
                align: 'center',
                dataIndex: 'edBusStopCode',
                scopedSlots: { customRender: 'edBusStopCode' }
              },
              {
                title: '站台名称',
                align: 'center',
                dataIndex: 'edRouteStationName',
                scopedSlots: { customRender: 'edRouteStationName' }
              },
              {
                title: '站序',
                align: 'center',
                dataIndex: 'edOrderNumber',
                scopedSlots: { customRender: 'edOrderNumber' }
              },
              {
                title: '支线路',
                align: 'center',
                dataIndex: 'edRoutesubInfo',
                scopedSlots: { customRender: 'edRoutesubInfo' }
              },
              {
                title: '里程(KM)',
                align: 'center',
                dataIndex: 'edMileage',
                scopedSlots: { customRender: 'edMileage' }
              }
            ]
          },
          {
            title: '发布表(BS)数据',
            children: [
              {
                title: '线路',
                align: 'center',
                dataIndex: 'bsRouteName',
                scopedSlots: { customRender: 'bsRouteName' }
              },
              {
                title: '站台编码',
                align: 'center',
                dataIndex: 'bsBusStopCode',
                scopedSlots: { customRender: 'bsBusStopCode' }
              },
              {
                title: '站台名称',
                align: 'center',
                dataIndex: 'bsRouteStationName',
                scopedSlots: { customRender: 'bsRouteStationName' }
              },
              {
                title: '站序',
                align: 'center',
                dataIndex: 'bsOrderNumber',
                scopedSlots: { customRender: 'bsOrderNumber' }
              },
              {
                title: '支线路',
                align: 'center',
                dataIndex: 'bsRoutesubInfo',
                scopedSlots: { customRender: 'bsRoutesubInfo' }
              },
              {
                title: '里程(KM)',
                align: 'center',
                dataIndex: 'bsMileage',
                scopedSlots: { customRender: 'bsMileage' }
              }
            ]
          }
        ],
        url: {
          list: '/base/routeInfo/list',
          listRoute: '/common/sys/queryRouteByOrganId'
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
        this.queryParam = {}
        this.$set(this.queryParam, 'direction', '')
        this.loadData(1)
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
      }
    }
  }
</script>

<style lang="less" scoped>
  .ant-input-number /deep/ .ant-input-number-handler-wrap {
    visibility: hidden;
  }
</style>
