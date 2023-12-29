<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="12">
            <a-form-item label="账号">
              <!--<a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>-->
              <j-input placeholder="输入账号模糊查询" v-model="queryParam.username"></j-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="真实名字">
              <a-input placeholder="请输入真实名字" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>


          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
<!--      <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay" @click="handleMenuClick">
          <a-menu-item key="1">
            <a-icon type="lock" @click="batchMaxOccurs(1)"/>
            单一登录
          </a-menu-item>
          <a-menu-item key="2">
            <a-icon type="unlock" @click="batchMaxOccurs(-1)"/>
            不限制登录
          </a-menu-item>
          <a-menu-item key="3">
            <a-icon type="swap-right" @click="batchOccursType(0)"/>
            后登录踢出先登录
          </a-menu-item>
          <a-menu-item key="4">
            <a-icon type="swap-left" @click="batchOccursType(1)"/>
            已登录禁止再登录
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;<a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项&nbsp;&nbsp;
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

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
          <span slot="type" slot-scope="text, record">
                <a-radio-group name="radioGroup" v-model="record.occursType" :disabled="true">
                  <a-radio :value="0">
                    后登录踢出先登录
                  </a-radio>
                  <a-radio :value="1">
                    已登录禁止再登录
                  </a-radio>
                </a-radio-group>
          </span>
        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user"/>
          </div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
        </span>


      </a-table>
    </div>
    <UserSignSetting-modal ref="modalForm" @ok="modalFormOk"></UserSignSetting-modal>

  </a-card>
</template>

<script>
  import {putAction,getFileAccessHttpUrl} from '@/api/manage';
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import JInput from '@/components/jeecg/JInput'
  import UserSignSettingModal from './modules/UserSignSettingModal'

  export default {
    name: "UserList",
    mixins: [JeecgListMixin],
    components: {
      JInput,
      UserSignSettingModal
    },
    data() {
      return {
        sysOrgIds: '',
        description: '这是用户管理页面',
        queryParam: {},
        recycleBinVisible: false,
        columns: [
          {
            title: '账号',
            align: "center",
            dataIndex: 'username',
            width: 120,
            sorter: true
          },
          {
            title: '真实名字',
            align: "center",
            width: 100,
            dataIndex: 'realname',
          },
          {
            title: '最大登录设备数',
            align: "center",
            width: 100,
            dataIndex: 'maxOccurs',
            customRender:function(text){
              if(text == 1 ){
                return "单一登录";
              }else if (text == 0) {
                return "禁止登录";
              }else if (text > 1) {
                return text;
              }else {
                return "不限制";
              }
            }
          },
          {
            title: '登录策略',
            align: "center",
            width: 180,
            dataIndex: 'occursType',
            scopedSlots: { customRender: 'type' },
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: {customRender: 'action'},
            align: "center",
            width: 170
          }

        ],
        url: {
          list: "/sys/user/list",
          exportXlsUrl: "/sys/user/exportXls",
          updateNumUrl: "/sys/userSign/updateOccurs",
          updateTypeUrl: "/sys/userSign/updateType",
        },
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      getAvatarView: function (avatar) {
        return getFileAccessHttpUrl(avatar)
      },
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchMaxOccurs(1);
        } else if (e.key == 2) {
          this.batchMaxOccurs(-1);
        } else if (e.key == 3) {
          this.batchOccursType(0);
        }else if (e.key == 4) {
          this.batchOccursType(1);
        }
      },
      batchMaxOccurs: function (status) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          let ids = "";
          let that = this;
          that.selectedRowKeys.forEach(function (val) {
            ids += val + ",";
          });
          let params = {
            ids: ids,
            num: status,
          }
          that.$confirm({
            title: "确认操作",
            content: "是否修改选中账号为 "+ (status == 1 ? "单一登录" : "不限制登录") + "?",
            onOk: function () {
              putAction(that.url.updateNumUrl, params).then((res) => {
                if (res.success) {
                  that.$message.success(res.message);
                  that.loadData()
                }
                if (res.code === 510) {
                  that.$message.warning(res.message)
                }
                that.loading = false;
              })
            }
          });
        }
      },
      batchOccursType: function (status) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          let ids = "";
          let that = this;
          that.selectedRowKeys.forEach(function (val) {
            ids += val + ",";
          });
          let params = {
            ids: ids,
            type: status,
          }
          that.$confirm({
            title: "确认操作",
            content: "是否修改选中账号登录策略为 "+ (status == 1 ? "已登录禁止再登录" : "后登录踢出先登录") + "?",
            onOk: function () {
              putAction(that.url.updateTypeUrl, params).then((res) => {
                if (res.success) {
                  that.$message.success(res.message);
                  that.loadData()
                }
                if (res.code === 510) {
                  that.$message.warning(res.message)
                }
                that.loading = false;
              })
            }
          });
        }
      },
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>