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
              <j-dict-select-tag v-model="queryParam.type" dict-code="user_type"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="真实名字">
                <a-input placeholder="请输入真实名字" v-model="queryParam.realname"></a-input>
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
      <a-button @click="showAdjustOrgs" type="primary" icon="hdd" :disabled="selectedRowKeys.length == 0">批量调整</a-button>
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
        :rowSelection="{selectedRowKeys: selectedRowKeys, selectionRows: selectionRows,onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user"/>
          </div>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="showAdjustOrg(record)">调整</a>
          <a-divider type="vertical"/>
          <a @click="showAdjustHistory(record)">历史</a>
<!--          <a @click="showLoginOrg(record)">调整登录机构</a>-->
<!--          <a-divider type="vertical"/>-->
        </span>


      </a-table>
    </div>
    <!-- table区域-end -->
    <adjust-user-org-modal ref="adjustUserOrg" @ok="modalFormOk"></adjust-user-org-modal>
    <!-- 调整历史记录-->
    <adjust-history-modal  ref="adjustUserHistory" @ok="modalFormOk"></adjust-history-modal>


  </a-card>
</template>

<script>
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import GOrgSelect from './components/GOrgSelect'
  import JInput from '@/components/jeecg/JInput'
  import { postAction} from "../../api/manage";
  import AdjustUserOrgModal from "./modules/AdjustUserOrgModal";
  import AdjustHistoryModal from "./modules/AdjustHistoryModal";

  export default {
    name: "AdjustUserList",
    mixins: [JeecgListMixin],
    components: {
      AdjustHistoryModal,
      AdjustUserOrgModal,
      JInput,
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
        selectedOrgCodeValue: '',
        selectedOrgCodeKey: '',
        selectUserName: '',
        checkedDepartKeys:[],
        renderLoginOrg:false,
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
            dataIndex: 'userName',
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
            dataIndex: 'orgName'
          },
          {
            title: '登录机构',
            align: "center",
            width: 180,
            dataIndex: 'loginOrgs'
          },
          {
            title: '登录机构是否超出',
            align: "center",
            width: 100,
            dataIndex: 'exceedOrgFlag',
            customCell:this.renderExceedOrgFlagBackground,
            customRender:function (text,record,index) {
              return text ? "是" : "否";
            }
          },
          {
            title: '用户角色',
            align: "center",
            width: 180,
            dataIndex: 'rolesStr',
          },
          {
            title: '角色是否超出',
            align: "center",
            width: 100,
            dataIndex: 'exceedRoleFlag',
            customCell:this.renderExceedRoleFlagBackground,
            customRender:function (text,record,index) {
              return text ? "是" : "否";
            }
          },
          {
            title: '用户类型',
            align: "center",
            width: 100,
            dataIndex: 'type_dictText',
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
            width: 100
          }

        ],
        url: {
          list: "/sys/user/listAdjustUsers",
          adjustUserLoginOrgs: "/sys/sysOrg/adjustUserLoginOrgs",
          importExcelUrl: "sys/user/importExcel",
        },
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {

      onChange(value) {
        console.log(value)
        this.orgCodes = value
        this.queryParam.orgCode = value
      },
      onSearch() {
        console.log(...arguments)
      },
      onSelect() {
        console.log(...arguments)
      },
      renderExceedOrgFlagBackground(record){
        if(record.exceedOrgFlag){
          return {
            style: {
              'background-color': 'rgb(255,150,150)',
            },
          }
        }
      },
      renderExceedRoleFlagBackground(record){
        if(record.exceedRoleFlag){
          return {
            style: {
              'background-color': 'rgb(255,150,150)',
            },
          }
        }
      },
      reset(){
        this.searchReset()
        this.orgCodes = ''
      },
      showAdjustOrg(record){
        let records = [record]
        this.$refs.adjustUserOrg.initData(records)
      },
      showAdjustHistory(record){
        this.$refs.adjustUserHistory.initData(record)
      },
      showAdjustOrgs(){
        console.log('this.selectionRows',this.selectionRows )
        this.$refs.adjustUserOrg.initData(this.selectionRows)
      },
      // 可登录部门
      showLoginOrg(record){
        if(!record || !record.id){
          this.$message.warning("请先选择行政机构")
          return
        }
        this.selectedOrgCodeKey = record.orgCode
        this.selectedOrgCodeValue = record.orgCode
        this.selectUserName = record.userName
        console.log('login checkedDepartKeys',this.checkedDepartKeys)
        this.$nextTick(() => {
          this.$refs.departWindow.add(this.checkedDepartKeys,record.id);
        })

      },
      selectLoginOrgs(userLoginOrgs){
        console.log('checkedDepartKeys',this.checkedDepartKeys)
        let orgIds = []
        if(userLoginOrgs.departIdList){
          for(let i = 0;i<userLoginOrgs.departIdList.length;i++){
            orgIds.push(userLoginOrgs.departIdList[i].value)
          }
        }
        postAction(this.url.adjustUserLoginOrgs,{userId : userLoginOrgs.userId,orgIds : orgIds}).then(res => {
          if(res.success){
            this.$message.success("用户登录机构调整成功")
            this.loadData()
          }else {
            this.$message.warning("用户登录机构调整失败")
          }
        })
      }
    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>