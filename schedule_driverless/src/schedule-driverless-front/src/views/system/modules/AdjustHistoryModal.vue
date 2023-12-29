<template>

  <a-modal
    title="调整历史记录"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @cancel="handleCancel"
    @ok="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-card :bordered="false">
        <!-- table区域-begin -->
        <div>
          <a-table
            size="middle"
            bordered
            :columns="columns"
            :loading="loading"
            :scroll="{ x: true }"
            :data-source="data"
            :pagination="ipagination"
            @change="handleTableChange"
          >
          </a-table>
        </div>
      </a-card>
    </a-spin>
  </a-modal>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import {downloadFile, getAction, postAction} from '@/api/manage'
export default {
  name: "AdjustHistoryModal",
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      visible: false,
      description: '用户信息调整历史',
      disableMixinCreated: true,
      data:[],
      /* 排序参数 */
      isorter:{
        column: 'updateTime',
        order: 'desc',
      },
      // 表头
      columns: [
        {
          title: '用户名',
          align: 'center',
          dataIndex: 'userName'
        },
        {
          title: '原用户类型',
          align: 'center',
          dataIndex: 'sourceUserType_dictText'
        },
        {
          title: '原行政机构',
          align: 'center',
          dataIndex: 'sourceOrgName'
        },
        {
          title: '原登录机构',
          align: 'center',
          dataIndex: 'sourceLoginOrgName'
        },
        {
          title: '原角色',
          align: 'center',
          dataIndex: 'sourceRoleName'
        },
        {
          title: '新用户类型',
          align: 'center',
          dataIndex: 'userType_dictText'
        },
        {
          title: '现行政机构',
          align: 'center',
          dataIndex: 'orgName'
        },
        {
          title: '现登录机构',
          align: 'center',
          dataIndex: 'loginOrgName'
        },
        {
          title: '现角色',
          align: 'center',
          dataIndex: 'roleName'
        },
        {
          title: '变更人',
          align: 'center',
          dataIndex: 'createBy'
        },
        {
          title: '变更时间',
          align: 'center',
          dataIndex: 'createTime'
        }
      ],
      url: {
        list: '/sys/sysOrg/listAdjustUserLogs',
      },
      width: 600,
      confirmLoading: false,
      importVisible: false,
      format: false,
      fileList: [],
      dictOptions: {}
    }
  },
  methods: {
    initData(record){
      console.log('record ',record)
      this.queryParam.userId = record.id
      this.loadData()
      this.visible = true
    },
    loadData(){
      let params = this.getQueryParams();//查询条件
      getAction(this.url.list,params).then((res) => {
        if (res.success) {
          this.data = res.result.records;
          this.ipagination.total = res.result.total;
        }
        if(res.code===510){
          this.$message.warning(res.message)
        }
        this.loading = false;
      })
    },
    handleCancel(){
      this.visible = false
    }
  }

}
</script>

<style scoped>
@import '~@assets/less/common.less';
</style>