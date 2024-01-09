<template>
  <div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="false"
        :loading="loading"
        @change="handleTableChange"
        :customRow="clickThenCheck">

        <span slot="action" slot-scope="text, record">
          <a @click="showHistory(record)">
            查看审批
          </a>
        </span>

        <!-- 字符串超长截取省略号显示-->
        <span slot="bpmBizTitle" slot-scope="text">
          <j-ellipsis :value="text" :length="15"/>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 弹出框 -->
    <his-task-deal-modal ref="taskDealModal" :path="path" :formData="formData"></his-task-deal-modal>
  </div>
</template>

<script>
  import { filterObj } from '@/utils/util'
  import { getAction,postAction } from '@/api/manage'
  import HisTaskDealModal from "@/views/modules/bpm/task/HisTaskDealModal";
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import {BpmNodeInfoMixin} from '@/views/modules/bpm/mixins/BpmNodeInfoMixin'

  export default {
    name: 'MyCcHisProcessList',
    mixins: [JeecgListMixin,BpmNodeInfoMixin],
    components: {
      HisTaskDealModal,
      JEllipsis
    },
    data() {
      return {
        description: '我的抄送',
        queryParam: {},
        dataSource: [],
        loading: false,
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 5,
          total: 5
        },
        columns: [
          {
            title: '业务标题',
            dataIndex: 'bpmBizTitle',
            scopedSlots: { customRender: 'bpmBizTitle' }
          },{
            title: '流程名称',
            dataIndex: 'processDefinitionName'
          },{
            title: '任务名称',
            dataIndex: 'taskName'
          }, {
            title: '耗时',
            dataIndex: 'durationStr'
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
          }

        ],
        url: {
          list:"/act/task/taskAllCcHistoryList",
        },
        path:"modules/bpm/task/form/FormLoading",
        formData:{},

      }
    },
    created(){

    },
    methods: {
      loadData(arg) {
        if(!this.url.list){
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      clickThenCheck(record) {
        return {
          on: {
            click: () => {
              this.showHistory(record);
            }
          }
        };
      },
      // 历史
      showHistory(record){
        this.getHisProcessNodeInfo(record);
      },
    }

  }
</script>
<style scoped>
  .ant-card-body {
    margin-bottom: 18px;
  }

  .table-operator button {
    margin-bottom: 18px;
    margin-right: 5px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }

  .anty-img-wrap {
    height: 25px;
    position: relative;
  }

  .anty-img-wrap > img {
    max-height: 100%;
  }
</style>