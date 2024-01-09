<template>
  <div>
    <!-- 操作按钮区域 -->

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
          <template v-if="record.endTime&&record.endTime!=''">
            <a @click="showHistory(record)" >
              历史
            </a>
          </template>
          <template v-else>
            <a-dropdown>
              <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
              <a-menu slot="overlay">
                <a-menu-item  @click="taskNotify(record)">催办</a-menu-item>
                <a-menu-item >
                  <a-popconfirm title="确定要作废流程吗?" @confirm="() => invalidProcess(record)">
                    <a>
                      作废流程
                    </a>
                  </a-popconfirm>
                </a-menu-item>
                <a-menu-item >
                  <a-popconfirm title="确定要取回流程吗?" @confirm="() => callBackProcess(record)">
                    <a>
                      取回流程
                    </a>
                  </a-popconfirm>
                </a-menu-item>
                <a-menu-item  @click="showHistory(record)">历史</a-menu-item>
              </a-menu>
            </a-dropdown>
          </template>
        </span>

        <!-- 字符串超长截取省略号显示-->
        <span slot="bpmBizTitle" slot-scope="text, record">
          <j-ellipsis :value="text" :length="15"/>
        </span>
        <span slot="prcocessDefinitionName" slot-scope="text, record">
          <j-ellipsis :value="text" :length="15"/>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 弹出框 -->
    <his-task-deal-modal ref="taskDealModal" :path="path" :formData="formData"></his-task-deal-modal>
    <task-notify-modal ref="taskNotifyModal"></task-notify-modal>
  </div>
</template>

<script>
  import { filterObj } from '@/utils/util'
  import { getAction,putAction } from '@/api/manage'
  import HisTaskDealModal from "@/views/modules/bpm/task/HisTaskDealModal";
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import {BpmNodeInfoMixin} from '@/views/modules/bpm/mixins/BpmNodeInfoMixin'
  import TaskNotifyModal from "@/views/modules/extbpm/process/TaskNotifyModal";

  export default {
    name: 'MyApplyProcessList',
    mixins: [JeecgListMixin,BpmNodeInfoMixin],
    components: {
      TaskNotifyModal,
      HisTaskDealModal,
      JEllipsis
    },
    data() {
      return {
        description: '我发起的流程',
        queryParam: {},
        dataSource: [],
        loading: false,
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize:5,
          total:5
        },
        columns: [
          {
            title: '业务标题',
            dataIndex: 'bpmBizTitle',
            scopedSlots: { customRender: 'bpmBizTitle' }
          },
          {
            title: '流程名称',
            dataIndex: 'prcocessDefinitionName',
            scopedSlots: { customRender: 'prcocessDefinitionName' }
          },
          {
            title: '开始日期',
            dataIndex: 'startTime'
          },
          {
            title: '耗时',
            dataIndex: 'spendTimes'
          },
          {
            title: '状态',
            dataIndex: 'bpmStatus',
            filters: [
              { text: '已完成', value: 'isFinished' },
              { text: '未完成', value: 'isUnfinished' },
            ],
            //onFilter: (value, record) => record.bpmStatus.includes(value),
            customRender: (text) => {
              switch(text) {
                case '1':
                  return '待提交';
                case '2':
                  return '处理中';
                case '3':
                  return '已完成';
                case 'rejectProcess':
                  return '已驳回';
                case 'callBackProcess':
                  return '已取回';
                case 'invalidProcess':
                  return '已作废';
              }
              return text;
            }
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
          }

        ],
        url: {
          list:"/act/task/myApplyProcessList",
          invalidProcess:"/act/task/invalidProcess",
          callBackProcess:"/act/task/callBackProcess",
        },
        path:"modules/bpm/task/form/FormLoading",
        formData:{},

      }
    },
    methods: {
      clickThenCheck(record) {
        return {
          on: {
            click: () => {
              this.showHistory(record);
            }
          }
        };
      },
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
      handleOK(){
        this.loadData();
        this.$emit("ok");
      },
      // 流程作废
      invalidProcess(record) {
        var that = this;
        var params = {
          processInstanceId:record.processInstanceId
        };//查询条件
        console.log("流程作废",params)
        putAction(that.url.invalidProcess, params).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.handleOK();
          } else {
            that.$message.warning(res.message);
            that.handleOK();
          }
        })
      },
      // 流程取回
      callBackProcess(record) {
        var that = this;
        var params = {
          processInstanceId:record.processInstanceId
        };//查询条件
        console.log("流程取回",params)
        putAction(that.url.callBackProcess, params).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.handleOK();
          } else {
            that.$message.warning(res.message);
            that.handleOK();
          }
        })
      },
      // 历史
      showHistory(record){
        this.getHisProcessNodeInfo(record);
      },
      //催办
      taskNotify(record){
        this.$refs.taskNotifyModal.notify(record);
        this.$refs.taskNotifyModal.title="催办提醒";
        this.handleOK();
      },
      handleTableChange(pagination, filters) {
        if(filters.bpmStatus && filters.bpmStatus.length==1){
          this.queryParam.finishedStateQuery = filters.bpmStatus[0];
        }else{
          delete this.queryParam.finishedStateQuery
        }
        this.loadData();
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

  /** Button按钮间距 */
  .ant-btn {
    margin-left: 3px
  }
</style>