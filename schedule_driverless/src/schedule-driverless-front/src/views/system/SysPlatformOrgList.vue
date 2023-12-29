<template>
  <a-row :gutter="10">
    <a-col :md="leftColMd" :sm="24" style="margin-bottom: 20px">
      <a-card :bordered="false" style="height: 100%;">

        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">

              <a-col :md="6" :sm="8">
                <a-form-item label="平台名称">
                  <j-input placeholder="请输入平台名称" v-model="queryParam.platformName"></j-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="8">
                <a-form-item label="平台编码">
                  <a-input placeholder="请输入平台编码" v-model="queryParam.platformCode"></a-input>
                </a-form-item>
              </a-col>
              <template v-if="toggleSearchStatus">
                <a-col :md="6" :sm="8">
                  <a-form-item label="描述">
                    <a-input placeholder="请输入描述" v-model="queryParam.description"></a-input>
                  </a-form-item>
                </a-col>
              </template>
              <a-col :md="6" :sm="8">
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                  <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                  <a @click="handleToggleSearch" style="margin-left: 8px">
                    {{ toggleSearchStatus ? '收起' : '展开' }}
                    <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
                  </a>
                </span>
              </a-col>

            </a-row>
          </a-form>
        </div>

        <!-- 操作按钮区域 -->
        <div class="table-operator">
          <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
          <a-button type="primary" icon="download" @click="handleExportXls('平台表')">导出</a-button>
          <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader"
            :action="importExcelUrl" @change="handleImportExcel">
            <a-button type="primary" icon="import">导入</a-button>
          </a-upload>
          <a-dropdown v-if="selectedRowKeys.length > 0">
            <a-menu slot="overlay">
              <a-menu-item key="1" @click="batchDel">
                <a-icon type="delete" />删除</a-menu-item>
            </a-menu>
            <a-button style="margin-left: 8px"> 批量操作
              <a-icon type="down" />
            </a-button>
          </a-dropdown>
        </div>

        <!-- table区域-begin -->
        <div>
          <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
            <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a
              style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
            <a style="margin-left: 24px" @click="onClearSelected">清空</a>
          </div>

          <a-table ref="table" size="middle" bordered rowKey="id" :columns="columns" :dataSource="dataSource"
            :pagination="ipagination" :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange,type:'radio'}"
            @change="handleTableChange">

            <span slot="action" slot-scope="text, record">
              <a @click="handleOpen(record)">机构</a>
              <a-divider type="vertical" />
              <a @click="handleEdit(record)">编辑</a>

              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link">更多
                  <a-icon type="down" /></a>
                <a-menu slot="overlay">
                  <a-menu-item>
                    <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                      <a>删除</a>
                    </a-popconfirm>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </span>

          </a-table>
        </div>
        <!-- table区域-end -->

        <!-- 表单区域 -->
        <sysPlatform-modal ref="modalForm" @ok="modalFormOk"></sysPlatform-modal>
      </a-card>
    </a-col>
    <a-col :md="rightColMd" :sm="24" v-if="this.rightcolval == 1">
      <a-card :bordered="false">
        <a-button @click="handleSave" type="primary">保存</a-button>
        <a-tree multiple treeCheckable="tree" checkable autoExpandParent :checkedKeys="checkedKeys"
          :expandedKeys.sync="expandedKeys" allowClear="true" :checkStrictly="true" @check="onCheck"
          :selectedKeys="selectedRowKeys1" :dropdownStyle="{maxHeight:'200px',overflow:'auto'}" :treeData="orgTree"
          placeholder="请选择上级部门">
        </a-tree>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
  import SysPlatformModal from './modules/SysPlatformModal'
  import OrgWindow from './modules/OrgWindow'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import JInput from '@/components/jeecg/JInput'
  import {
    queryOrgTreeByUserId,
    editPlatformOrgs
  } from '@/api/api'
  import {
    getAction
  } from '@/api/manage'

  export default {
    name: "SysPlatformList",
    mixins: [JeecgListMixin],
    components: {
      SysPlatformModal,
      OrgWindow,
      JInput
    },
    data() {
      return {
        description: '平台表管理页面',
        selectedRowKeys1: [],
        rightcolval: 0,
        model: null,
        currentPlatformId: '',
        orgList: [], // 存储部门信息
        orgTree: [],
        checkedKeys: [],
        expandedKeys: [],
        // 表头
        columns: [{
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '平台名称',
            align: "center",
            dataIndex: 'platformName'
          },
          {
            title: '平台编码',
            align: "center",
            dataIndex: 'platformCode'
          },
          {
            title: '描述',
            align: "center",
            dataIndex: 'description'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            scopedSlots: {
              customRender: 'action'
            },
          }
        ],
        url: {
          list: "/sys/platform/list",
          delete: "/sys/platform/delete",
          deleteBatch: "/sys/platform/deleteBatch",
          exportXlsUrl: "sys/platform/exportXls",
          importExcelUrl: "sys/platform/importExcel",
          platformWithOrg: "sys/platform/platformOrgList", // 引入为指定平台查看部门信息需要的url
        },
      }
    },
    computed: {
      importExcelUrl: function () {
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
      onSelectChange(selectedRowKeys, selectionRows) {
        this.checkedKeys = []
        this.rightcolval = 1
        this.selectedRowKeys1 = selectedRowKeys
        this.model1 = Object.assign({}, selectionRows[0])
        this.currentPlatformId = selectedRowKeys[0]
        this.queryOrgTree()
      },
      handleOpen(record) {
        if (this.currentPlatformId != record.id) {
          this.checkedKeys = []
          this.rightcolval = 1
          this.selectedRowKeys1 = [record.id]
          this.model = Object.assign({}, record)
          this.currentPlatformId = record.id
          this.queryOrgTree()
        }
      },
      // 选择部门时作用的API
      onCheck(checkedKeys, info) {
        this.orgList = [];
        this.checkedKeys = checkedKeys.checked;
        let checkedNodes = info.checkedNodes;
        for (let i = 0; i < checkedNodes.length; i++) {
          let de = checkedNodes[i].data.props;
          let org = {
            key: "",
            value: "",
            title: ""
          };
          org.key = de.value;
          org.value = de.value;
          org.title = de.title;
          this.orgList.push(org);
        }
        console.log('onCheck', checkedKeys, info);
      },
      queryOrgTree() {
        queryOrgTreeByUserId({
          userId: this.$store.getters.userInfo.id
        }).then((res) => {
          if (res.success) {
            this.orgTree = res.result;
            this.loadCheckedOrgs()
          }
        })
        // queryIdTree().then((res)=>{
        //   if(res.success){
        //     this.orgTree = res.result;
        //   }
        // })
      },
      loadCheckedOrgs() {
        let that = this;
        if (!that.currentPlatformId) {
          return
        }
        getAction(that.url.platformWithOrg, {
          platformId: that.currentPlatformId
        }).then((res) => {
          if (res.success) {
            for (let i = 0; i < res.result.length; i++) {
              that.checkedKeys.push(res.result[i].key);
            }
          } else {
            console.log(res.message);
          }
        })
      },
      handleSave() {
        let params = {
          platformId: this.currentPlatformId
        }
        let keys = []
        this.orgList.forEach((item) => {
          keys.push(item.key)
        })
        params.orgs = keys.length > 0 ? keys.join(",") : ''
        editPlatformOrgs(params).then((res) => {
          if (res.success) {
            this.$message.success(res.message);
          } else {
            console.log(res.message);
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>