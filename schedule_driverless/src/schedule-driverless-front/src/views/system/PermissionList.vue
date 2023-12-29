<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="平台">
              <a-select labelInValue :value="{ key: this.platformSelected.platformCode }" @change="platformChange">
                <a-select-option v-for="(item, key) in platforms" :key="key" :value="item.platformCode">{{
                  item.platformName
                }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="菜单名">
              <j-input placeholder="输入菜单名模糊查询" v-model="queryParam.name" type=""></j-input>
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
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button @click="batchDel" v-if="selectedRowKeys.length > 0" ghost type="primary" icon="delete"
        >批量删除</a-button
      >
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;
        <a style="font-weight: 600"> {{ selectedRowKeys.length }} </a>项&nbsp;&nbsp;
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        :columns="columns"
        size="middle"
        :pagination="false"
        :dataSource="dataSource"
        :loading="loading"
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :scroll="{ y: 540 }"
      >
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">
              更多
              <a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a href="javascript:;" @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;" @click="handleAddSub(record)">添加子菜单</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;" @click="handleDataRule(record)">数据规则</a>
              </a-menu-item>

              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
              <a-menu-item v-show="false">
                <a href="javascript:;" @click="handleRoles(record)">角色授权</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
        <!-- 字符串超长截取省略号显示 -->
        <span slot="url" slot-scope="text">
          <j-ellipsis :value="text" :length="25" />
        </span>
        <!-- 字符串超长截取省略号显示-->
        <span slot="component" slot-scope="text">
          <j-ellipsis :value="text" />
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <permission-modal ref="modalForm" @ok="modalFormOk"></permission-modal>
    <permission-data-rule-list ref="PermissionDataRuleList" @ok="modalFormOk"></permission-data-rule-list>
    <permission-role-list ref="PermissionRoleList" @ok="modalFormOk"></permission-role-list>
  </a-card>
</template>

<script>
import PermissionModal from './modules/PermissionModal'
import { getPermissionList } from '@/api/api'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import PermissionDataRuleList from './PermissionDataRuleList'
import PermissionRoleList from './PermissionRoleList'
import JEllipsis from '@/components/jeecg/JEllipsis'
import { getPlatformList } from '@/api/api'
import JInput from '@/components/jeecg/JInput'

const columns = [
  {
    title: '菜单名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '菜单类型',
    dataIndex: 'menuType',
    key: 'menuType',
    customRender: function(text) {
      if (text == 0) {
        return '菜单'
      } else if (text == 1) {
        return '菜单'
      } else if (text == 2) {
        return '按钮/权限'
      } else {
        return text
      }
    }
  },
  /*{
        title: '权限编码',
        dataIndex: 'perms',
        key: 'permissionCode',
      },*/ {
    title: 'icon',
    dataIndex: 'icon',
    key: 'icon'
  },
  {
    title: '组件',
    dataIndex: 'component',
    key: 'component',
    scopedSlots: { customRender: 'component' }
  },
  {
    title: '路径',
    dataIndex: 'url',
    key: 'url',
    scopedSlots: { customRender: 'url' }
  },
  {
    title: '应用',
    dataIndex: 'appName',
    key: 'appName'
  },
  {
    title: '排序',
    dataIndex: 'sortNo',
    key: 'sortNo'
  },
  {
    title: '操作',
    dataIndex: 'action',
    scopedSlots: { customRender: 'action' },
    align: 'center',
    width: 150
  }
]

export default {
  name: 'PermissionList',
  mixins: [JeecgListMixin],
  components: {
    PermissionDataRuleList,
    PermissionRoleList,
    PermissionModal,
    JEllipsis,
    JInput
  },
  data() {
    return {
      description: '这是菜单管理页面',
      // 表头
      columns: columns,
      loading: false,
      url: {
        list: '/sys/permission/list',
        delete: '/sys/permission/delete',
        deleteBatch: '/sys/permission/deleteBatch'
      },
      platformSelected: { platformCode: '', platformName: '' },
      platforms: null
    }
  },
  methods: {
    async loadData() {
      await getPlatformList().then(res => {
        if (res.success) {
          console.log(res.result)
          if (res.result.records.length > 0) {
            this.platforms = res.result.records
            if (this.platformSelected.platformCode == '') {
              this.platformSelected.platformCode = res.result.records[0].platformCode
              this.platformSelected.platformName = res.result.records[0].platformName
            }
          }
        }
      })

      this.dataSource = []
      let params = {
        platformCode: this.platformSelected.platformCode,
        name: this.queryParam.name === '' ? null : this.queryParam.name
      }
      getPermissionList(params).then(res => {
        if (res.success) {
          console.log(res.result)
          this.dataSource = res.result
        }
      })
    },
    handleAdd: function() {
      this.$refs.modalForm.add(this.platformSelected)
      this.$refs.modalForm.title = '新增'
      this.$refs.modalForm.disableSubmit = false
    },
    handleEdit: function(record) {
      record = Object.assign(record, this.platformSelected)
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
    // 打开数据规则编辑
    handleDataRule(record) {
      this.$refs.PermissionDataRuleList.edit(record)
    },
    // 打开角色授权列表
    handleRoles(record) {
      this.$refs.PermissionRoleList.edit(record)
    },
    handleAddSub(record) {
      this.$refs.modalForm.title = '添加子菜单'
      this.$refs.modalForm.localMenuType = 1
      this.$refs.modalForm.disableSubmit = false
      this.$refs.modalForm.edit({
        status: '1',
        permsType: '1',
        route: true,
        parentId: record.id,
        platformName: this.platformSelected.platformName,
        platformCode: this.platformSelected.platformCode
      })
    },
    platformChange(selVal) {
      console.log(selVal)
      this.platformSelected.platformCode = selVal.key
      this.platformSelected.platformName = selVal.label
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
