<template xmlns:a-col="http://www.w3.org/1999/html">
  <j-modal
    :title="logTitle"
    :width="logWidth"
    :visible="showLog"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="closeLogs"
    @cancel="closeLogs"
    cancelText="关闭">
    <a-table
      ref="table"
      size="middle"
      rowKey="id"
      bordered
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      class="j-table-force-nowrap"
      @change="handleTableChange">

    </a-table>
  </j-modal>
</template>

<script>

  import pick from 'lodash.pick'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'
  import {getAction, postAction} from "../../../api/manage";
  import {mixinDevice} from "../../../utils/mixin";
  import Vue from "vue";
  import {math} from "../../../utils/math";



  export default {
    name: "bsRouteMileageLogModal",
    mixins:[JeecgListMixin, mixinDevice],
    components: { 
      JDate,
    },
    data () {
      return {
        columns:[
          {
            title:'站点名',
            align:"center",
            dataIndex: 'routeStationName'
          },
          {
            title:'原里程',
            align:"center",
            dataIndex: 'mileage'
          },
          {
            title:'修改里程',
            align:"center",
            dataIndex: 'mileageUpdate'
          },
          {
            title:'修改人',
            align:"center",
            dataIndex: 'updateBy'
          },
          {
            title:'修改时间',
            align:"center",
            dataIndex: 'updateTime'
          },
        ],
        showLog:false,
        record:'',
        logTitle:'修改日志记录',
        logWidth: 800,
        confirmLoading: false,
        /* 分页参数 */
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        url: {
          list:"/routeSubSta/getRouteSubStaLogs",
        }
      }
    },
    created () {
    },
    methods: {
      loadData(arg){

      },
      showLogs(record){
        if(record){
          this.record = record
        }
        this.getRouteSubStaLogs()
      },
      getRouteSubStaLogs(){
        console.log('record',this.record)
        getAction(this.url.list,{
          routeId: this.record.routeId,
          pageNo: this.ipagination.current,
          pageSize: this.ipagination.pageSize
        }).then(res => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if (res.code === 500) {
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      closeLogs(){
        this.showLog = false
      },
      handleTableChange(pagination, filters, sorter) {
        // 分页、排序、筛选变化时触发
        // TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = sorter.order == 'ascend' ? 'asc' : 'desc'
        }
        this.ipagination = pagination;
        this.getRouteSubStaLogs();
      },
      
    }
  }
</script>