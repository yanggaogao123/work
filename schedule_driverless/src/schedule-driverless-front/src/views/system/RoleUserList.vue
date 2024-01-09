<template>
  <a-row :gutter="10">
    <a-col :md="leftColMd" :sm="24" style="margin-bottom: 20px">
      <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter.native="searchQuery">

            <a-row :gutter="24">
             <a-col :md="5" :sm="12">
                <a-form-item label="平台">
                  <a-select allowClear labelInValue :value="{key:this.platformSelected.platformCode}"
                    @change="platformChange">
                    <a-select-option value="">请选择所属平台</a-select-option>
                    <a-select-option v-for="(item, key) in platforms" :key="key" :value="item.platformCode">{{item.platformName}}</a-select-option>
                  </a-select>
                </a-form-item>
             </a-col>
              <a-col :md="5" :sm="12">
                <a-form-item label="机构">
                  <g-org-select v-model="orgCode" showSearch treeNodeFilterProp="title" placeholder='请选择机构' :treeDefaultExpandAll="false"
                                @change="onChangeOrg" type="role"></g-org-select>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="12">
                <a-form-item label="角色名称" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                  <a-input placeholder="" v-model="queryParam.roleName"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="4">
                <a-form-item  label="角色类型">
                  <a-select v-model="queryParam.roleType">
                    <a-select-option :value="1">
                      机构级
                    </a-select-option>
                    <a-select-option :value="2">
                      用户级
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="6" :sm="24">
               <a-button type="primary" @click="searchQuery" icon="search" style="margin-left: 21px">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </a-col>
          </span>
            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin: 5px 0 10px 2px">
          <a-button @click="handleAdd" type="primary" icon="plus">新建角色</a-button>
          <!--<a-button @click="handleEdit(model1)" type="primary" icon="plus">角色编辑</a-button>-->
<!--          <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--            <a-button type="primary" icon="import">导入</a-button>-->
<!--          </a-upload>-->
          <a-button type="primary" icon="download" @click="handleExportXls('角色管理')">导出</a-button>
        </div>

        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
          <i class="anticon anticon-info-circle ant-alert-icon">
          </i> 已选择 <a><b>{{ selectedRowKeys1.length }}</b></a>项
          <a style="margin-left: 24px" @click="onClearSelected1">清空</a>
        </div>

        <div style="margin-top: 15px">
          <a-table
            style="height:500px"
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1, type:'radio'}"
            @change="handleTableChange">
          <span slot="action" slot-scope="text, record">
            <a @click="handleOpen(record)">用户</a>
            <a-divider type="vertical" />

<!--            <a @click="handleDesign(record.id)">工单授权</a>-->
<!--            <a-divider type="vertical"/>-->

            <a-dropdown>
              <a class="ant-dropdown-link">
                更多 <a-icon type="down"/>
              </a>
              <a-menu slot="overlay">
                <a-menu-item>
                  <a @click="handlePerssion(record)">授权</a>
                </a-menu-item>
                <a-menu-item>
                  <a @click="handleEdit(record)">编辑</a>
                </a-menu-item>
                <a-menu-item>
                  <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete1(record.id)">
                    <a>删除</a>
                  </a-popconfirm>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
        </span>
          </a-table>
        </div>
        <!-- 右侧的角色权限配置 -->
        <user-role-modal ref="modalUserRole"></user-role-modal>
        <role-modal ref="modalForm" @ok="modalFormOk"></role-modal>
<!--        <sys-role-design-modal ref="roleDesignModal"></sys-role-design-modal>-->
      </a-card>
    </a-col>
    <a-col :md="rightColMd" :sm="24" v-if="this.rightcolval == 1">
      <a-card :bordered="false">
        <div style="text-align: right;">
          <a-icon type="close-circle" @click="hideUserList" />
        </div>
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="24">

              <a-col :md="12" :sm="12">
                <a-form-item label="用户账号">
                  <a-input placeholder="" v-model="queryParam2.username"></a-input>
                </a-form-item>
              </a-col>
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="9" :sm="24">
             <a-button type="primary" @click="searchQuery2" icon="search" style="margin-left: 21px">查询</a-button>
              <a-button type="primary" @click="searchReset2" icon="reload" style="margin-left: 8px">重置</a-button>

            </a-col>
          </span>
            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class="table-operator" :md="24" :sm="24">
<!--          <a-button @click="handleAdd2" type="primary" icon="plus" style="margin-top: 16px">新增用户</a-button>-->
          <!--<a-button @click="handleEdit2" type="primary" icon="edit" style="margin-top: 16px">用户编辑</a-button>-->
          <a-button @click="handleAddUserRole" type="primary" icon="plus" style="margin-top: 16px; margin-bottom: 16px;">已有用户</a-button>

          <a-dropdown v-if="selectedRowKeys2.length > 0">
            <a-menu slot="overlay">
              <a-menu-item key="1" @click="batchDel2">
                <a-icon type="delete"/>
                删除
              </a-menu-item>
            </a-menu>
            <a-button style="margin-left: 8px"> 批量操作
              <a-icon type="down"/>
            </a-button>
          </a-dropdown>
        </div>
        <!-- table区域-begin -->
        <div>
          <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
            <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
            selectedRowKeys2.length }}</a>项
            <a style="margin-left: 24px" @click="onClearSelected2">清空</a>
          </div>
          <a-table
            style="height:500px"
            ref="table2"
            bordered
            size="middle"
            rowKey="id"
            :columns="columns2"
            :dataSource="dataSource2"
            :pagination="ipagination2"
            :loading="loading2"
            :rowSelection="{selectedRowKeys: selectedRowKeys2, onChange: onSelectChange2}"
            @change="handleTableChange2">
           <span slot="action" slot-scope="text, record">
<!--           <a @click="handleEdit2(record)">编辑</a>-->
             <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete2(record.id)">
              <a>删除</a>
             </a-popconfirm>
<!--          <a-divider type="vertical"/>-->
<!--          <a-dropdown>-->
<!--            <a class="ant-dropdown-link">-->
<!--              更多 <a-icon type="down"/>-->
<!--            </a>-->
<!--            <a-menu slot="overlay">-->
<!--              <a-menu-item>-->
<!--                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete2(record.id)">-->
<!--                  <a>删除</a>-->
<!--                </a-popconfirm>-->
<!--              </a-menu-item>-->
<!--            </a-menu>-->
<!--          </a-dropdown>-->
        </span>
          </a-table>
        </div>
        <!-- 表单区域 -->
        <role-modal ref="modalForm" @ok="modalFormOk"></role-modal>
        <user-modal ref="modalForm2" @ok="modalFormOk2"></user-modal>
        <select-user-modal ref="selectUserModal" @selectFinished="selectOK"></select-user-modal>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { deleteAction, postAction, getAction } from '@/api/manage'
  import SelectUserModal from './modules/SelectUserModal'
  import RoleModal from './modules/RoleModal'
  import UserModal from './modules/UserModal'
  import { filterObj } from '@/utils/util'
  import UserRoleModal from './modules/UserRoleModal'
  import moment from 'moment'
  import SysRoleDesignModal from './modules/SysRoleDesignModal'
  import GOrgSelect from "./components/GOrgSelect";

  export default {
    name: 'RoleUserList',
    mixins: [JeecgListMixin],
    components: {
      SysRoleDesignModal,
      UserRoleModal,
      SelectUserModal,
      RoleModal,
      UserModal,
      GOrgSelect,
      moment
    },
    data() {
      return {
        orgCode:'',
        orgCategorys: [0,1,5,7],
        multiple: false,
        platformSelected: {
          platformCode: '',
          platformName: ''
        },
        platforms: [],
        model1: {},
        model2: {},
        currentRoleId: '',
        queryParam1: {},
        queryParam2: {},
        dataSource1: [],
        dataSource2: [],
        ipagination1: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        ipagination2: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter1: {
          column: 'createTime',
          order: 'desc'
        },
        isorter2: {
          column: 'createTime',
          order: 'desc'
        },
        filters1: {},
        filters2: {},
        loading1: false,
        loading2: false,
        selectedRowKeys1: [],
        selectedRowKeys2: [],
        selectionRows1: [],
        selectionRows2: [],
        test: {},
        rightcolval: 0,
        columns:
          [
            {
              title: '机构',
              align: 'center',
              dataIndex: 'orgCode_dictText',
              sorter: true
            },
            {
              title: '平台',
              align: 'center',
              dataIndex: 'platformCode_dictText',
              sorter: true
            },
            {
              title: '角色类型',
              align: 'center',
              dataIndex: 'roleType_dictText',
              sorter: true
            },
            {
              title: '角色编码',
              align: 'center',
              dataIndex: 'roleCode',
              sorter: true
            },
            {
              title: '角色名称',
              align: 'center',
              dataIndex: 'roleName',
              sorter: true
            },
            {
              title: '创建人',
              dataIndex: 'createBy',
              align: 'center',
              sorter: true
            },
            {
              title: '创建时间',
              dataIndex: 'createTime',
              align: 'center',
              sorter: true,
              customRender: (text) => {
                return moment(text).format('YYYY-MM-DD')
              }
            },
            {
              title: '操作',
              dataIndex: 'action',
              align: 'center',
              scopedSlots: { customRender: 'action' }
            }
          ],
        columns2: [{
          title: '用户账号',
          align: 'center',
          dataIndex: 'username',
          width: 120
        },
          {
            title: '用户名称',
            align: 'center',
            width: 100,
            dataIndex: 'realname'
          },
          {
            title: '状态',
            align: 'center',
            width: 80,
            dataIndex: 'status_dictText'
          },

          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            align: 'center',
            width: 120
          }],

        // 高级查询参数
        superQueryParams2: '',
        // 高级查询拼接条件
        superQueryMatchType2: 'and',
        url: {
          list: '/sys/role/list',
          delete: '/sys/role/delete',
          list2: '/sys/user/userRoleList',
          addUserRole: '/sys/user/addSysUserRole',
          delete2: '/sys/user/deleteUserRole',
          deleteBatch2: '/sys/user/deleteUserRoleBatch',
          exportXlsUrl: 'sys/role/exportXls',
          importExcelUrl: 'sys/role/importExcel'
        }
      }
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
      leftColMd() {
        return this.selectedRowKeys1.length === 0 ? 24 : 12
      },
      rightColMd() {
        return this.selectedRowKeys1.length === 0 ? 0 : 12
      }
    },
    methods: {
      onChangeOrg(value) {
        console.log(value)
        this.orgCode = value
        this.queryParam.orgCode = value
      },
      loadPlatformList() {
        this.platforms = this.$store.getters.userPlatforms
        // if (this.platformSelected.platformCode == "") {
        //   this.platformSelected.platformCode = this.platforms[0].platformCode
        //   this.platformSelected.platformName = this.platforms[0].platformName
        // }
        this.queryParam.platformCode = this.platformSelected.platformCode
      },
      loadData(arg) {
        this.loadPlatformList()
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        var params = this.getQueryParams() //查询条件
        this.loading = true
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
      },
      // async loadPlatformList() {
      //   await getPlatformList().then(res => {
      //     if (res.success) {
      //       console.log(res.result)
      //       if (res.result.records.length > 0) {
      //         this.platforms = res.result.records
      //         if (this.platformSelected.platformCode == "") {
      //           this.platformSelected.platformCode = res.result.records[0].platformCode
      //           this.platformSelected.platformName = res.result.records[0].platformName
      //         }
      //       }
      //     }
      //   })
      //   this.queryParam.platformCode = this.platformSelected.platformCode
      //   this.loadData()
      // },
      onSelectChange2(selectedRowKeys, selectionRows) {
        this.selectedRowKeys2 = selectedRowKeys
        this.selectionRows2 = selectionRows
      },
      onClearSelected2() {
        this.selectedRowKeys2 = []
        this.selectionRows2 = []
      },
      onClearSelected1() {
        this.selectedRowKeys1 = []
        this.selectionRows1 = []
      },
      onSelectChange1(selectedRowKeys, selectionRows) {
        this.rightcolval = 1
        this.selectedRowKeys1 = selectedRowKeys
        this.selectionRows1 = selectionRows
        this.model1 = Object.assign({}, selectionRows[0])
        console.log(this.model1)
        this.currentRoleId = selectedRowKeys[0]
        this.loadData2()
      },
      onClearSelected() {
      },

      getQueryParams2() {
        //获取查询条件
        let sqp = {}
        if (this.superQueryParams2) {
          sqp['superQueryParams'] = encodeURI(this.superQueryParams2)
          sqp['superQueryMatchType'] = this.superQueryMatchType2
        }
        var param = Object.assign(sqp, this.queryParam2, this.isorter2, this.filters2)
        param.field = this.getQueryField2()
        param.pageNo = this.ipagination2.current
        param.pageSize = this.ipagination2.pageSize
        return filterObj(param)
      },
      getQueryField2() {
        //TODO 字段权限控制
        var str = 'id,'
        this.columns2.forEach(function(value) {
          str += ',' + value.dataIndex
        })
        return str
      },
      handleAdd: function() {
        // if (this.platformSelected.platformCode == '') {
        //   this.$message.error('请选择平台！')
        //   return
        // }
        this.$refs.modalForm.add(this.platformSelected)
        this.$refs.modalForm.title = '新增'
        this.$refs.modalForm.disableSubmit = false
      },
      handleEdit: function(record) {
        // record = Object.assign(record, this.platformSelected)
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.disableSubmit = false
      },
      handleDetail: function(record) {
        record = Object.assign(record, this.platformSelected)
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '详情'
        this.$refs.modalForm.disableSubmit = true
      },
      handleEdit2: function(record) {
        this.$refs.modalForm2.title = '编辑'
        this.$refs.modalForm2.roleDisabled = true
        this.$refs.modalForm2.edit(record)
      },
      handleAdd2: function() {
        if (this.currentRoleId == '') {
          this.$message.error('请选择一个角色!')
        } else {
          this.$refs.modalForm2.roleDisabled = true
          this.$refs.modalForm2.selectedRole = [this.currentRoleId]

          this.$refs.modalForm2.add()

          this.$refs.modalForm2.title = '新增'
        }
      },
      modalFormOk2() {
        // 新增/修改 成功时，重载列表
        this.loadData2()
      },
      loadData2(arg) {
        if (!this.url.list2) {
          this.$message.error('请设置url.list2属性!')
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination2.current = 1
        }
        if (this.currentRoleId === '') return
        let params = this.getQueryParams2()//查询条件
        params.roleId = this.currentRoleId
        this.loading2 = true
        getAction(this.url.list2, params).then((res) => {
          if (res.success) {
            this.dataSource2 = res.result.records
            this.ipagination2.total = res.result.total

          }
          this.loading2 = false
        })

      },
      handleDelete1: function(id) {
        this.handleDelete(id)
        this.dataSource2 = []
        this.currentRoleId = ''
      },
      handleDelete2: function(id) {
        if (!this.url.delete2) {
          this.$message.error('请设置url.delete2属性!')
          return
        }
        var that = this
        deleteAction(that.url.delete2, { roleId: this.currentRoleId, userId: id }).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.loadData2()
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      batchDel2: function() {

        if (!this.url.deleteBatch2) {
          this.$message.error('请设置url.deleteBatch2属性!')
          return
        }
        if (this.selectedRowKeys2.length <= 0) {
          this.$message.warning('请选择一条记录！')
          return
        } else {
          var ids = ''
          for (var a = 0; a < this.selectedRowKeys2.length; a++) {
            ids += this.selectedRowKeys2[a] + ','
          }
          var that = this
          console.log(this.currentDeptId)
          this.$confirm({
            title: '确认删除',
            content: '是否删除选中数据?',
            onOk: function() {
              deleteAction(that.url.deleteBatch2, { roleId: that.currentRoleId, userIds: ids }).then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.loadData2()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.message)
                }
              })
            }
          })
        }
      },
      selectOK(data) {
        let params = {}
        params.roleId = this.currentRoleId
        params.userIdList = []
        for (var a = 0; a < data.length; a++) {
          params.userIdList.push(data[a])
        }
        console.log(params)
        postAction(this.url.addUserRole, params).then((res) => {
          if (res.success) {
            this.loadData2()
            this.$message.success(res.message)
          } else {
            this.$message.warning(res.message)
          }
        })
      },

      handleAddUserRole() {
        console.log('触发已有用户')
        if (this.currentRoleId == '') {
          this.$message.error('请选择一个角色!')
        } else {
          this.$refs.selectUserModal.visible = true
          this.$refs.selectUserModal.roleId = this.currentRoleId
          this.$refs.selectUserModal.show()
        }
      },
      handleOpen(record) {
        this.rightcolval = 1
        this.selectedRowKeys1 = [record.id]
        this.model1 = Object.assign({}, record)
        this.currentRoleId = record.id
        this.onClearSelected2()
        this.loadData2()
      },
      /*handleEdit: function(record) {
        if (this.currentRoleId == '') {
          this.$message.error('请选择一个角色!')
        } else {
          this.$refs.modalForm.edit(record)
          this.$refs.modalForm.title = '编辑'
        }
      },*/
      searchQuery2() {
        this.loadData2(1)
      },
      searchReset(){
        this.queryParam = {}
        this.platformSelected.platformCode = ''
        this.orgCode = ''
        this.loadData(1);
      },
      searchReset2() {
        this.queryParam2 = {}
        this.loadData2(1)
      },
      handleTableChange2(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter2.column = sorter.field
          this.isorter2.order = 'ascend' == sorter.order ? 'asc' : 'desc'
        }
        this.ipagination2 = pagination
        this.loadData2()
      },
      hideUserList() {
        //this.rightcolval = 0
        this.selectedRowKeys1 = []
      },
      handlePerssion(role) {
        this.$refs.modalUserRole.show(role)
      },
      handleDesign: function(id) {
        this.$refs.roleDesignModal.edit(id)
      },
      platformChange(selVal) {
        if (selVal) {
          this.platformSelected.platformCode = selVal.key
          this.platformSelected.platformName = selVal.label
          this.queryParam.platformCode = this.platformSelected.platformCode
        } else {
          this.platformSelected.platformCode = ''
          this.platformSelected.platformName = ''
          this.queryParam.platformCode = this.platformSelected.platformCode
        }

      }
    }
  }
</script>
<style scoped>
  /** Button按钮间距 */
  .ant-btn {
    margin-left: 8px
  }
</style>