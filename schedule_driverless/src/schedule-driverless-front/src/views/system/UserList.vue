<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="12">

            <a-form-item label="行政机构">
              <g-org-select type="orgCategorys" :orgCategorys="orgCategorys"
                            showSearch :multiple="multiple" allowClear treeNodeFilterProp="title" placeholder='请选择行政部门'
                :treeDefaultExpandAll="false" v-model="orgCodes" @change="onChange"></g-org-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="12">
            <a-form-item label="账号">
              <!--<a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>-->
              <j-input placeholder="输入账号模糊查询" v-model="queryParam.username"></j-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item label="用户类型">
              <a-select placeholder="请选择用户类型" :disabled="userTypeDisabled" v-model="queryParam.type">
                <a-select-option v-for="(item, key) in userTypeOptions" :key="key" :value="item.value">
              <span style="display: inline-block;width: 100%" :title=" item.text || item.label ">
                {{ item.text || item.label }}
              </span>
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="真实名字">
                <a-input placeholder="请输入真实名字" v-model="queryParam.realname"></a-input>
              </a-form-item>
            </a-col>

            <a-col :md="6" :sm="8">
              <a-form-item label="手机号码">
                <a-input placeholder="请输入手机号码查询" v-model="queryParam.phone"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="性别">
                <a-select v-model="queryParam.sex" placeholder="请选择性别">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">男性</a-select-option>
                  <a-select-option value="2">女性</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="用户状态">
                <a-select v-model="queryParam.status" placeholder="请选择">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="1">正常</a-select-option>
                  <a-select-option value="2">冻结</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="reset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator" style="border-top: 5px">
      <a-button @click="handleAdd" type="primary" icon="plus">添加用户</a-button>
<!--      <a-button @click="handleSyncUser"  v-has="'user:syncbpm'" type="primary" icon="plus">同步流程</a-button>-->
      <a-button type="primary" icon="download" @click="handleExportXls('用户信息')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <a-button type="primary" icon="hdd" @click="recycleBinVisible=true">回收站</a-button>
      <a-button type="primary" icon="hdd" @click="syncUser2Apts">同步用户</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay" @click="handleMenuClick">
          <a-menu-item key="1">
            <a-icon type="delete" @click="batchDel"/>
            删除
          </a-menu-item>
          <a-menu-item key="2">
            <a-icon type="lock" @click="batchFrozen('2')"/>
            冻结
          </a-menu-item>
          <a-menu-item key="3">
            <a-icon type="unlock" @click="batchFrozen('1')"/>
            解冻
          </a-menu-item>
          <a-menu-item key="4">
            <a-icon type="unlock" @click="syncUser2Apts()"/>
            同步用户
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

        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user"/>
          </div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>

          <a-dropdown>
            <a class="ant-dropdown-link">
              更多 <a-icon type="down"/>
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a href="javascript:;" @click="handleDetail(record)">详情</a>
              </a-menu-item>

              <a-menu-item>
                <a href="javascript:;" @click="handleChangePassword(record.username)">密码</a>
              </a-menu-item>

              <a-menu-item>
              <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>

              <a-menu-item v-if="record.status==1">
                <a-popconfirm title="确定冻结吗?" @confirm="() => handleFrozen(record.id,2,record.username)">
                  <a>冻结</a>
                </a-popconfirm>
              </a-menu-item>

              <a-menu-item v-if="record.status==2">
                <a-popconfirm title="确定解冻吗?" @confirm="() => handleFrozen(record.id,1,record.username)">
                  <a>解冻</a>
                </a-popconfirm>
              </a-menu-item>
<!--              <a-menu-item>-->
<!--                <a href="javascript:;" @click="handleAgentSettings(record.username)">代理人</a>-->
<!--              </a-menu-item>-->

            </a-menu>
          </a-dropdown>
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->

    <user-modal ref="modalForm" @ok="modalFormOk"></user-modal>

    <password-modal ref="passwordmodal" @ok="passwordModalOk"></password-modal>

<!--    <sys-user-agent-modal ref="sysUserAgentModal"></sys-user-agent-modal>-->

    <!-- 用户回收站 -->
    <user-recycle-bin-modal :visible.sync="recycleBinVisible" @ok="modalFormOk"/>

  </a-card>
</template>

<script>
  import UserModal from './modules/UserModal'
  import PasswordModal from './modules/PasswordModal'
  import {putAction,getFileAccessHttpUrl} from '@/api/manage';
  import {frozenBatch,ajaxGetDictItems} from '@/api/api'
  import {getAction} from '@/api/manage';
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import SysUserAgentModal from "./modules/SysUserAgentModal";
  import JInput from '@/components/jeecg/JInput'
  import UserRecycleBinModal from './modules/UserRecycleBinModal'
  import GOrgSelect from './components/GOrgSelect'
  import {deleteAction, postAction} from "../../api/manage";

  export default {
    name: "UserList",
    mixins: [JeecgListMixin],
    components: {
      // SysUserAgentModal,
      UserModal,
      PasswordModal,
      JInput,
      UserRecycleBinModal,
      GOrgSelect
    },
    data() {
      return {
        userTypeDisabled:false,
        orgCodes: '',
        orgCategorys: [0,1,5,7],
        multiple: false,
        description: '这是用户管理页面',
        queryParam: {},
        recycleBinVisible: false,
        userTypeOptions: [],
        columns: [
          /*{
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },*/
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
            title: '行政机构',
            align: "center",
            width: 180,
            dataIndex: 'orgCode_dictText'
          },
          // {
          //   title: '行政部门',
          //   align: "center",
          //   width: 180,
          //   dataIndex: 'departCode_dictText'
          // },
          {
            title: '用户类型',
            align: "center",
            width: 100,
            dataIndex: 'type_dictText',
            sorter: true
          },
          {
            title: '性别',
            align: "center",
            width: 80,
            dataIndex: 'sex_dictText',
            sorter: true
          },
          {
            title: '生日',
            align: "center",
            width: 100,
            dataIndex: 'birthday'
          },
          {
            title: '手机号码',
            align: "center",
            width: 100,
            dataIndex: 'phone'
          },
          {
            title: '用户状态',
            align: "center",
            width: 80,
            dataIndex: 'status_dictText'
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
          syncUser2Apts: "/sys/user/syncUser2Apts",
          list: "/sys/user/list",
          delete: "/sys/user/delete",
          deleteBatch: "/sys/user/deleteBatch",
          exportXlsUrl: "/sys/user/exportXls",
          importExcelUrl: "sys/user/importExcel",
        },
      }
    },
    created() {
      this.loadUserTypes()
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      //加载用户类型数据
      loadUserTypes() {
        //根据字典Code, 初始化字典数组
        ajaxGetDictItems('user_type', null).then((res) => {
          if (res.success) {
            let userType = this.$store.getters.userInfo.type
            console.log('userType',userType)
            this.userTypeOptions = res.result.filter((item) => {
              if(userType === 9999){
                return item
              }
              if(userType === 1 && item.value != 9999){
                return item
              }
              if(userType === 0 && item.value == 0){
                return item
              }
            });
          }
        })
      },
      getAvatarView: function (avatar) {
        return getFileAccessHttpUrl(avatar)
      },
      handleDelete: function (record) {
        if (!this.url.delete) {
          this.$message.error("请设置url.delete属性!")
          return
        }
        var that = this;
        console.log('record.username',record.username)
        console.log('this.$store.getters.userInfo.username',this.$store.getters.userInfo.username)
        if (record.username === 'admin' || this.$store.getters.userInfo.username === record.username) {
          that.$message.warning('不允许此操作,请重新选择！');
          return;
        }
        deleteAction(that.url.delete, {
          id: record.id
        }).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      batchFrozen: function (status) {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！');
          return false;
        } else {
          let ids = "";
          let that = this;
          let isAdmin = false;
          that.selectionRows.forEach(function (row) {
            if (row.username == 'admin') {
              isAdmin = true;
            }
          });
          if (isAdmin) {
            that.$message.warning('管理员账号不允许此操作,请重新选择！');
            return;
          }
          that.selectedRowKeys.forEach(function (val) {
            ids += val + ",";
          });
          that.$confirm({
            title: "确认操作",
            content: "是否" + (status == 1 ? "解冻" : "冻结") + "选中账号?",
            onOk: function () {
              frozenBatch({ids: ids, status: status}).then((res) => {
                if (res.success) {
                  that.$message.success(res.message);
                  that.loadData();
                  that.onClearSelected();
                } else {
                  that.$message.warning(res.message);
                }
              });
            }
          });
        }
      },
      handleMenuClick(e) {
        if (e.key == 1) {
          this.batchDel();
        } else if (e.key == 2) {
          this.batchFrozen(2);
        } else if (e.key == 3) {
          this.batchFrozen(1);
        }else if(e.key == 4) {
          this.syncUser2Apts()
        }
      },
      handleFrozen: function (id, status, username) {
        let that = this;
        //TODO 后台校验管理员角色
        if ('admin' == username) {
          that.$message.warning('管理员账号不允许此操作！');
          return;
        }
        frozenBatch({ids: id, status: status}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      handleChangePassword(username) {
        this.$refs.passwordmodal.show(username);
      },
      handleAgentSettings(username){
        this.$refs.sysUserAgentModal.agentSettings(username);
        this.$refs.sysUserAgentModal.title = "用户代理人设置";
      },
      handleSyncUser() {
        var that = this;
        putAction(that.url.syncUser, {}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
          } else {
            that.$message.warning(res.message);
          }
        })
      },
      passwordModalOk() {
        //TODO 密码修改完成 不需要刷新页面，可以把datasource中的数据更新一下
      },
      onChange(value) {
        console.log(value)
        this.orgCodes = value
        this.queryParam.orgCodes = value
      },
      onSearch() {
        console.log(...arguments)
      },
      onSelect() {
        console.log(...arguments)
      },
      // getCheckboxProps: record => ({
      //   props: {
      //     disabled: record.username === 'admin'// || this.$store.getters.userInfo.username===record.username, // Column configuration not to be checked
      //   }
      // }),
      //下面方法在沒有用箭頭函數的時候可以使用this
      getCheckboxProps(record) {
        let a = {
          props: {
            disabled: record.username === 'admin' || this.$store.getters.userInfo.username === record.username,
          }
        }
        return a
      },
      reset(){
        this.searchReset()
        this.orgCodes = ''
      },
      syncUser2Apts(){
        postAction(this.url.syncUser2Apts,this.selectedRowKeys).then(res => {
          if(res.success){
            this.$message.success("同步操作已完成，请等待后台数据同步")
          }else {
            this.$message.warning("同步操作失败，请联系管理员处理")
          }
        })
      },
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>