<template>
  <div>
    <a-card :bordered="false">
      <a-row style="width: 100%;" :gutter="24">
        <a-col :span="4">
          <!-- 部门树形结构  -->
          <a-directory-tree
             selectable
            :selectedKeys="selectedDepIds"
            :checkStrictly="true"
            :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
            :treeData="departTree"
            :expandAction="false"
            :expandedKeys.sync="expandedKeys"
            @select="onDepSelect"
          />
        </a-col>
        <a-col :span="20">
          <eoa-metting-calendar-use ref="calendarUser" @change="resourceChange"></eoa-metting-calendar-use>
          <a-table
            v-if="!resourceIsNull"
            ref="table"
            size="middle"
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
            />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script>
  import moment from 'moment';
  import { queryDepartTreeList} from '@/api/api'
  import EoaMettingCalendarUse from './modules/EoaMettingCalendarUse'
  import { getAction } from '@/api/manage'

  export default {
    name: "EoaMettingRoomUse",
    components: {
      EoaMettingCalendarUse
    },
    data () {
      return {
        columns: [
          {
            title: '会议名称',
            align:"center",
            dataIndex: 'name'
          }, {
            title: '召集人',
            align:"center",
            dataIndex: 'originator_dictText'
          },{
            title: '位置',
            align:"center",
            dataIndex: 'location_dictText'
          }, {
            title: '开始时间',
            align:"center",
            dataIndex: 'beginTime'
          }, {
            title: '结束时间',
            align:"center",
            dataIndex: 'endTime'
          }, {
            title: '状态',
            align:"center",
            dataIndex: 'status',
            customRender:function(text){
              if(text=='0'){
                return "未开始"
              }else{
                return "已结束"
              }
            }
          }
        ],
        loading:false,
        dataSource: [],
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        /* table选中keys*/
        selectedRowKeys: [],
        /* table选中records*/
        selectionRows: [],
        selectedDepIds: [],
        departTree: [],
        expandedKeys: [],
        visible: true,
        resourceIsNull: true,
				tranLeft: 0,
        tranTop: 0
    }
  },
    created() {
      this.queryDepartTree();
    },
    methods: {
      moment,
      resourceChange(value){
        console.log("数据源改变了")
        if(value!=null&&value.length>0){
          this.resourceIsNull=false
        }else{
          this.resourceIsNull=true
        }
      },
      loadMetting(departId) {
        var params ={};//查询条件
        params.pageNo = this.ipagination.current;
        params.pageSize = this.ipagination.pageSize;
        params.departId=departId
        this.loading = true;
        getAction("/metting/eoaMetting/queryList", params).then((res) => {
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
      queryDepartTree() {
        queryDepartTreeList().then((res) => {
          if (res.success) {
            this.departTree = res.result;
            // 默认展开父节点
            this.expandedKeys = this.departTree.map(item => item.id);
            // 默认选中部门
            console.log("this.expandedKeys[0]==>",this.expandedKeys[0])
            let departId="";
            if(this.expandedKeys!=null&&this.expandedKeys.length >0){
             departId=this.expandedKeys[0];
            }
            this.$refs.calendarUser.loadRoom(departId);
            this.loadMetting(departId);
          }
        })
      },
      // 点击树节点,筛选出对应的会议室
      onDepSelect(selectedDepIds) {
        console.log("onDepSelect==>",selectedDepIds)
        if (selectedDepIds[0] != null) {
          this.$refs.calendarUser.loadRoom(selectedDepIds[0]);
          this.loadMetting(selectedDepIds[0]);
          // 调用方法根据选选择的id查询会议室信息
          if (this.selectedDepIds[0] !== selectedDepIds[0]) {
            this.selectedDepIds = [selectedDepIds[0]];
          }
        }
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  @import '~@/assets/less/TableExpand.less';
</style>