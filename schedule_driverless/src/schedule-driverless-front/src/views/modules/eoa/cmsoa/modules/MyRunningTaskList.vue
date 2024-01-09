<template>
  <div>
    <!-- table区域-begin -->
    <a-table
      ref="table"
      size="middle"
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :loading="loading"
      :pagination="false"
      @change="handleTableChange"
      :customRow="clickThenCheck">

      <span slot="action" slot-scope="text, record">
        <template v-if="record.taskAssigneeName&&record.taskAssigneeName!=''">
          <a @click="handleProcess(record)">
            办理
          </a>
          <a-divider type="vertical" />
          <a @click.stop="selectEntruster(record)">
            委托
          </a>
        </template>
        <template v-else>
          <a @click="handleClaim(record)" >
            签收
          </a>
        </template>

      </span>
      <!-- 字符串超长截取省略号显示-->
      <span slot="bpmBizTitle" slot-scope="text, record">
        <a-icon
          title="催办提醒"
          v-if="record.taskUrge"
          theme="twoTone"
          twoToneColor="#eb2f96"
          @click="taskNotify(record)"
          type="notification"/>
        <j-ellipsis :value="text" :length="15"/>
      </span>

    </a-table>

    <!-- 弹出框 -->
<task-deal-modal ref="taskDealModal" :path="path" :formData="formData" @ok="handleOk"></task-deal-modal>
<select-entruster-modal ref="selectEntrusterModal" @selectFinished="handleEntruster"></select-entruster-modal>
<task-notify-me-modal ref="taskNotifyMeModal"></task-notify-me-modal>
<select-single-user-modal ref="selectSingleUserModal" @selectFinished="selectUserOK"></select-single-user-modal>
</div>
</template>

<script>
  import { filterObj } from '@/utils/util'
  import { deleteAction,getAction,postAction,putAction } from '@/api/manage'
  import TaskDealModal from "@/views/modules/bpm/task/TaskDealModal";
  import SelectEntrusterModal from "@/views/modules/bpm/task/form/SelectEntrusterModal";
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import {BpmNodeInfoMixin} from '@/views/modules/bpm/mixins/BpmNodeInfoMixin'
  import TaskNotifyMeModal from "@/views/modules/extbpm/process/TaskNotifyMeModal.vue";
  import SelectSingleUserModal from "@/views/modules/bpm/task/form/SelectSingleUserModal.vue";

  export default {
    name: "MyRunningTaskList",
    mixins: [JeecgListMixin,BpmNodeInfoMixin],
    components: {
      SelectSingleUserModal,
      TaskNotifyMeModal,
      SelectEntrusterModal,
      TaskDealModal,
      JEllipsis
    },
    data () {
      return {
        description: '我的任务',
        // 查询条件
        queryParam: {},
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 5,
          total: 5
        },
        model:{
          userName:""
        },
        // 表头
        columns: [
          {
            title: '业务标题',
            dataIndex: 'bpmBizTitle',
            scopedSlots: { customRender: 'bpmBizTitle' }
          }, {
            title: '流程名称',
            dataIndex: 'processDefinitionName'
          }, {
            title: '开始时间',
            dataIndex: 'taskBeginTime'
          }, {
            title: '当前环节',
            dataIndex: 'taskName'
          },{
            title: '操作',
            dataIndex: 'action',
            scopedSlots: {customRender: 'action'}
          }
        ],
        path:"modules/bpm/task/form/FormLoading",
        formData:{},
        url: {
          list: "/act/task/list",
          claim: "/act/task/claim",
          taskEntrust:"/act/task/taskEntrust",
        },
        taskId:'',
      }
    },
    methods: {
      clickThenCheck(record) {
        return {
          on: {
            click: () => {
              if(record.taskAssigneeName&&record.taskAssigneeName!=''){
                this.handleProcess(record);
              }else{
                this.handleClaim(record)
              }
            }
          }
        };
      },
      searchReset() {
        this.queryParam = {};
        this.model.userName ="";
        this.loadData(1);
      },
      selectUserOK: function(data){
        this.model.userName = data.realname;
        this.queryParam.userName = data.username;
      },
      handleSelect: function(){
        this.$refs.selectSingleUserModal.select();
      },
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
                that.handleOk();
              } else {
                that.$message.warning(res.message);
                that.handleOk();
              }
            })
          }
        });
      },
      handleOk(){
        this.loadData();
        this.$emit("ok");
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
      selectEntruster(record){
        this.taskId = record.id;
        this.$refs.selectEntrusterModal.select(record);
        this.$refs.selectEntrusterModal.title="选择委托人";
      },
      handleEntruster(data){
        var that = this;
        var params = {
          taskId:this.taskId,
          taskAssignee:data.username
        };//查询条件
        console.log("委托",params)
        putAction(that.url.taskEntrust, params).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.handleOk();
          } else {
            that.$message.warning(res.message);
            that.handleOk();
          }
        })

      },
      //催办
      taskNotify(record){
        this.$refs.taskNotifyMeModal.notify(record);
        this.$refs.taskNotifyMeModal.title="催办提醒";
      },
    }
  }
</script>
<style lang="less" scoped>
  /** Button按钮间距 */
  .ant-btn {
    margin-left: 3px
  }
</style>