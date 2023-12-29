<template>
  <div>
    <!-- table区域-begin -->
    <div>

      <a-table
        ref="table"
        :scroll="{x: 1800}"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="false"
        :loading="loading"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <template v-if="record.taskAssigneeName&&record.taskAssigneeName!=''">
            <a  @click="handleProcess(record)">
              办理
            </a>
          </template>
          <template v-else>
            <a @click="handleClaim(record)" >
              签收
            </a>
          </template>
        </span>

        <!-- 字符串超长截取省略号显示-->
        <span slot="bpmBizTitle" slot-scope="text">
          <j-ellipsis :value="text" :length="15"/>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- 弹出框 -->
    <task-deal-modal ref="taskDealModal" :path="path" :formData="formData" @ok="handleOk"></task-deal-modal>
  </div>
</template>

<script>
  import { filterObj } from '@/utils/util'
  import { deleteAction,getAction,postAction,putAction } from '@/api/manage'
  import TaskDealModal from "@/views/modules/bpm/task/TaskDealModal";
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import {BpmNodeInfoMixin} from '@/views/modules/bpm/mixins/BpmNodeInfoMixin'

  export default {
    name: "MyGroupTaskList",
    mixins: [JeecgListMixin,BpmNodeInfoMixin],
    components: {
      TaskDealModal,
      JEllipsis
    },
    data () {
      return {
        description: '我的组任务',
        // 查询条件
        queryParam: {},
        processPortalFlag:true,
        // 表头
        columns: [
          {
            title: '#',
            width: 60,
            dataIndex: '',
            key: 'rowIndex',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          }, {
            title: '业务标题',
            dataIndex: 'bpmBizTitle',
            scopedSlots: { customRender: 'bpmBizTitle' }
          }, {
            title: '流程编号',
            dataIndex: 'processDefinitionId'
          },{
            title: '流程名称',
            dataIndex: 'processDefinitionName'
          },{
            title: '流程实例',
            dataIndex: 'processInstanceId'
          },{
            title: '任务ID',
            dataIndex: 'taskId'
          }, {
            title: '发起人',
            dataIndex: 'processApplyUserName'
          },{
            title: '开始时间',
            dataIndex: 'taskBeginTime'
          }, {
            title: '结束时间',
            dataIndex: 'taskEndTime'
          }, {
            title: '当前环节',
            dataIndex: 'taskName'
          }, {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width:150,
            scopedSlots: {customRender: 'action'}
          }
        ],
        path:"modules/bpm/task/form/FormLoading",
        formData:{},
		    url: {
          list: "/act/task/taskGroupList",
          claim: "/act/task/claim",
        },
        
      }
    },
    methods: {
      handleProcess(record){
        this.getProcessNodeInfo(record);
      },
      handleClaim(record){
        var that = this;
        var params = {taskId:record.id};//查询条件
        this.$confirm({
          title:"确认签收吗",
          content:"是否签收该任务?",
          onOk: function(){
            putAction(that.url.claim, params).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.loadData();
              } else {
                that.$message.warning(res.message);
                that.loadData();
              }
            })
          }
        });
      },
      handleOk(){
        this.loadData();
      }
    }
  }
</script>
<style lang="less" scoped>

</style>