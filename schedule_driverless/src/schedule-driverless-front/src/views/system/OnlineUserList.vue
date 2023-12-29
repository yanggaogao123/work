<template>
  <a-card :bordered="false" class="card-area">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <!-- 搜索区域 -->
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="账号" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
              <a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="真实名称" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
              <a-input placeholder="请输入真实名称查询" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="在线状态" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
              <a-select  :default-value="1" disabled>
                <a-select-option :value="1">在线</a-select-option>
                <a-select-option :value="0">离线</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="6" :sm="24">
              <a-button type="primary" @click="searchQuery">查询</a-button>
              <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator"  style="margin-top: 5px">

      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>下线</a-menu-item>
        </a-menu>
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择&nbsp;<a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项&nbsp;&nbsp;
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleOffline(record)">下线</a>
          <a-divider type="vertical" />
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'
  import { postAction, getAction } from '@/api/manage'


  export default {
    name: "OnlineUserList",
    mixins:[JeecgListMixin],
    components: {
      JDate
    },
    data () {
      return {
        dataSource:[],
        description: '在线用户管理页面',
        // 查询条件
        queryParam: {},
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title: '账户',
            align:"center",
            dataIndex: 'username'
          },
          {
            title: '真实名字',
            align:"center",
            dataIndex: 'realname'
          },
          {
            title: 'IP地址',
            align:"center",
            dataIndex: 'host'
          },
          {
            title: '所属机构',
            align:"center",
            dataIndex: 'orgName'
          },
          {
            title: '角色',
            dataIndex: 'roleName',
            align:"center",
          },
          {
            title: '在线状态',
            dataIndex: 'onlineStatus',
            align:"center",
            customRender: function (text) {
              if (text === 1) {
                return '在线'
              } else if (text === 0) {
                return '离线'
              } else {
                return text
              }
            }
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/sys/onlineUser/list",
          offLine: "/sys/onlineUser/offline",
          exportXlsUrl: "/sys/onlineUser/exportXls",
        },
      }
    },
    computed: {
    },
    created() {
    },
    methods: {
      handleOffline(record){
        let reparams = {
          id: record.id,
          username: record.username
        }
        getAction(this.url.offLine, reparams).then(res => {
          if (res.success) {
            this.loadData()
            this.$message.success('处理完成!')
          } else {
            this.$message.warning('处理失败!')
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>