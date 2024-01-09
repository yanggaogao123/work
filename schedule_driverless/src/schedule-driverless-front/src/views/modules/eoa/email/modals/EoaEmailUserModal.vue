<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="modalLoading"
    cancelText="关闭">
    <a-row :gutter="10" style="background-color: #ececec; padding: 10px; margin: -10px">
      <a-col :md="8" :sm="24">
        <a-card :bordered="false">
          <!--组织机构-->
          <a-directory-tree
            selectable
            :selectedKeys="selectedDepIds"
            :checkStrictly="true"
            @select="onDepSelect"
            :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
            :treeData="departTree"
          />
        </a-card>
      </a-col>
      <a-col :md="16" :sm="24">
        <a-card :bordered="false">
          用户账号:
          <a-input-search
            :style="{width:'150px',marginBottom:'15px'}"
            placeholder="请输入用户账号"
            v-model="queryParam.username"
            @search="onSearch"
          />
          <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
    <!-- table区域-begin -->
    <div>

      <a-table
        ref="table"
        bordered
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

              </a-table>
            </div>
            <!-- table区域-end -->
        </a-card>
      </a-col>
    </a-row>

    </a-modal>
</template>

<script>
  import {putAction,getAction} from '@/api/manage';
  import {frozenBatch} from '@/api/api'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import { axios } from '@/utils/request'
  import {queryDepartTreeList, getUserList, queryUserByDepId} from '@/api/api'
  export default {
    name: "EoaEmailUserModal",
    mixins: [JeecgListMixin],
    components: {
    },
    data() {
      return {
        description: '用户选择页面',
        queryParam: {},
        title:"选择收件人",
        selectedDepIds: [],
        selectUserIds:[],
        departTree: [],
        columns: [
          {
            title: '用户账号',
            align: "center",
            dataIndex: 'username',
            width: 120
          },
          {
            title: '用户姓名',
            align: "center",
            width: 100,
            dataIndex: 'realname',
          },
          {
            title: '手机号码',
            align: "center",
            width: 100,
            dataIndex: 'phone'
          },
          {
            title: '邮箱',
            align: "center",
            dataIndex: 'email'
          }
        ],
        selectedRowKeys:[],
        url: {
          list: "/sys/user/list",
        },
        visible:false,
        modalLoading:false
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
     searchReset(num) {
         let that = this;
         if (num !== 0) {
             that.queryParam = {};
             that.loadData(1);
         }
         that.selectedRowKeys = [];
         that.selectUserIds = [];
         that.selectedDepIds = [];
     },
     // 点击树节点,筛选出对应的用户
     onDepSelect(selectedDepIds) {
         if (selectedDepIds[0] != null) {
             this.initQueryUserByDepId(selectedDepIds); // 调用方法根据选选择的id查询用户信息
             if (this.selectedDepIds[0] !== selectedDepIds[0]) {
                 this.selectedDepIds = [selectedDepIds[0]];
               }
         }
     },
     // 根据选择的id来查询用户信息
     initQueryUserByDepId(selectedDepIds) {
         queryUserByDepId({id: selectedDepIds.toString()}).then((res) => {
             if (res.success) {
                 this.dataSource = res.result;
                 this.ipagination.total = res.result.length;
             }
         })
     },
     queryDepartTree() {
         queryDepartTreeList().then((res) => {
             if (res.success) {
                 this.departTree = res.result;
             }
         })
      },
      loadDate(record){
        if(record){
          this.selectedRowKeys=record
        }
        this.queryDepartTree();
        this.visible=true
      },
      handleOk (num) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          this.modalLoading=true
          let ids = "";
          let that = this;
          this.getSelectUserRows();
          var date=[];
          date.realname=this.selectUserRows;
          date.ids=this.selectUserIds;
          this.$emit('ok',date);
          this.close();
          this.modalLoading=false;
        }

      },
      handleCancel () {
        this.close()
      },
      close () {
        this.visible = false;
        this.selectedRowKeys=[];
        this.selectUserIds = [];
        this.selectedDepIds = [];
      },
        //获取选择用户信息
      getSelectUserRows(rowId) {
        let dataSource = this.dataSource;
        let userIds = "";
        this.selectUserRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
            if (this.selectedRowKeys.includes(dataSource[i].id)) {
                this.selectUserRows.push(dataSource[i].realname);
                userIds = userIds + "," + dataSource[i].id
            }
        }
        this.selectUserIds = userIds.substring(1);
      },
      onSearch() {
        this.loadData(1);
      },
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>