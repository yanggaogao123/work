<template>
  <a-drawer
    :title="title"
    :width="drawerWidth"
    @close="onClose"
    :visible="visible">

    <!-- 抽屉内容的border -->
    <div
      :style="{
        padding:'10px',
        border: '1px solid #e9e9e9',
        background: '#fff',
      }">
      <div class="table-page-search-wrapper">
        <a-form @keyup.enter.native="searchQuery">
          <a-row :gutter="24">

            <a-col :md="8" :sm="12">

              <a-form-item label="平台"  :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <a-select allowClear labelInValue :value="{key:this.platformSelected.platformCode}"
                          @change="platformChange">
                  <a-select-option value="">请选择所属平台</a-select-option>
                  <a-select-option v-for="(item, key) in platforms" :key="key" :value="item.platformCode">{{item.platformName}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="12">
              <a-form-item label="所属部门" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <g-org-select v-model="orgCode" labelInValue showSearch treeNodeFilterProp="title" placeholder='请选择所属部门' :treeDefaultExpandAll="false"
                              @change="onChangeOrg" type="role"></g-org-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="12">
              <a-form-item label="角色名称" :labelCol="{span: 5}" :wrapperCol="{span: 10, offset: 1}">
                <a-input placeholder="" v-model="queryParam.roleName"></a-input>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="8" :sm="12">
              <a-form-item  label="角色类型" :labelCol="{span: 5}" :wrapperCol="{span: 10, offset: 1}">
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
            <a-col :md="7" :sm="8">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              </span>
            </a-col>
          </a-row>

          <a-row>
            <a-col :md="24" :sm="24">
                 <a-button style="margin-bottom: 10px" @click="addPermissionRule"  type="primary" icon="plus">添加</a-button>
            </a-col>
          </a-row>
        </a-form>

        <a-table
          ref="table"
          rowKey="id"
          size="middle"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
          @change="handleTableChange"
          :rowClassName="getRowClassname">
          <span slot="action" slot-scope="text, record">
            <a @click="handleEdit(record)">
              <a-icon type="edit"/>编辑
            </a>
            <a-divider type="vertical"/>
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </span>
        </a-table>

      </div>
    </div>
  </a-drawer>
</template>
<script>
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import {getAction, postAction} from "../../api/manage";
  import GOrgSelect from "./components/GOrgSelect";

  export default {
    name: 'PermissionDataRuleList',
    mixins: [JeecgListMixin],
    components: {
      GOrgSelect
    },
    data() {
      return {
        title: '角色授权列表',
        queryParam: {},
        drawerWidth: '70rem',
        platformSelected: {
          platformCode: '',
          platformName: ''
        },
        platforms:[],
        orgCode:'',
        columns:
          [
            {
              title: '所属机构',
              align: 'center',
              dataIndex: 'orgCode_dictText',
              sorter: true
            },
            {
              title: '所属平台编码',
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
            }
          ],
        isSuperAdmin:false,
        permId: '',
        visible: false,
        form: this.$form.createForm(this),
        loading: false,
        url: {
          list: "/sys/permission/getRolesByPermissionId",
          delete: "/sys/permission/deletePermissionRule",
        },
      }
    },
    created() {
      // this.resetScreenSize()
    },
    methods: {
      loadData() {
        this.loadPlatformList()
        if(!this.permId){
          return
        }
        let that = this
        this.dataSource = []
        const params = this.getQueryParams();//查询条件
        postAction(this.url.list,params).then((res) => {
          if (res.success) {
            that.dataSource = res.result.records
            that.ipagination.total = res.result.total
          }
        })
      },
      onChangeOrg(value) {
        console.log(value)
        this.orgCode = value
        this.queryParam.orgCode = value
      },
      loadPlatformList() {
        this.platforms = this.$store.getters.userPlatforms
        this.queryParam.platformCode = this.platformSelected.platformCode
      },
      edit(record) {
        this.title = record.name + ' 角色授权列表'
        if (record.id) {
          this.visible = true
          this.permId = record.id
        }
        this.queryParam = {}
        this.queryParam.permissionId = record.id
        this.visible = true
        this.loadData()
      },
      addPermissionRule() {
        this.$refs.modalForm.add(this.permId)
        this.$refs.modalForm.title = '新增'
      },
      searchQuery() {
        var params = this.getQueryParams();
        params.permissionId = this.permId;
        postAction(this.url.list,params).then((res) => {
          if (res.success) {
            that.dataSource = res.result.records
            that.ipagination.total = res.result.total
          }
        })
      },
      searchReset() {
        this.queryParam = {}
        this.queryParam.permissionId = this.permId
        this.loadData(1);
      },
      onClose() {
        this.visible = false
      },
      // 根据屏幕变化,设置抽屉尺寸
      resetScreenSize() {
        let screenWidth = document.body.clientWidth
        if (screenWidth < 500) {
          this.drawerWidth = screenWidth
        } else {
          this.drawerWidth = 650
        }
      },
      getRowClassname(record){
        if(record.status!=1){
          return "data-rule-invalid"
        }
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

<style>
  .data-rule-invalid{
    background: #f4f4f4;
    color: #bababa;
  }
</style>