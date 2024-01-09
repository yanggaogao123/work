<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="6" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect
                v-model="queryParam.sysOrgCode"
                placeholder="请选择所属机构"
                @changeOptions="handleChangeOrgan"
                :isGetOption="true"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="5" :md="8" :sm="24">
            <a-form-item label="终端芯片编号">
              <a-input v-model="queryParam.obuChipCode">
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="ICCID">
              <a-input v-model="queryParam.simCode">
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="终端IP">
              <a-input v-model="queryParam.wlanIp">
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :xl="6" :lg="6" :md="8" :sm="24">
            <a-form-item label="状态" style="margin-left: 25px">
              <a-select :allowClear="true" placeholder="请选择" v-model="queryParam.obuStatus">
                <a-select-option v-for="(item, key) in statusOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="5" :lg="5" :md="8" :sm="24">
            <a-form-item label="是否启用">
              <a-select :allowClear="true" placeholder="请选择" v-model="queryParam.isActive">
                <a-select-option v-for="(item, key) in enableOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="发布状态">
              <a-select :allowClear="true" placeholder="请选择" v-model="queryParam.publishStatus">
                <a-select-option v-for="(item, key) in publicOptions" :key="key" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :xl="24" :lg="24" :md="24" :sm="24">
            <a-button type="primary" @click="searchQuery2" icon="search" style="margin-bottom: 10px;">查询</a-button>
            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px">重置</a-button>
            <a-button type="primary" @click="addData" icon="plus" style="margin-left: 10px">新建</a-button>
            <a-button type="primary" @click="editData" icon="edit" style="margin-left: 10px">编辑</a-button>
            <a-button type="primary" @click="deleteData" icon="delete" style="margin-left: 10px">删除</a-button>
            <a-button type="primary" @click="exportExcel" :loading="excelLoading" icon="download" style="margin-left: 10px">导出</a-button>
            <!-- <a-button type="primary" @click="openImportModal" icon="import" style="margin-left: 10px">导入</a-button> -->
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
          selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>
    </div>
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="equipmentId"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>

    <BsEquipmentModal ref="modalForm" @ok="modalFormOk"></BsEquipmentModal>
  </a-card>
</template>

<!--设备管理-->
<script>
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
  import { downloadFileAwait, delByIdsAction } from '@/api/manage'
  import ARow from 'ant-design-vue/es/grid/Row'
  import ACol from 'ant-design-vue/es/grid/Col'
  import BsEquipmentModal from './modules/BsEquipmentModal'
  export default {
    name: 'BsEquipment',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
      ACol,
      ARow,
      GCIOrgTreeSelect,
      BsEquipmentModal
    },
    data() {
      return {
        excelLoading: false,
        statusOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '正常',
          value: '0'
        }, {
          label: '报废',
          value: '2'
        }],
        enableOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '是',
          value: '0'
        }, {
          label: '否',
          value: '1'
        }],
        publicOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '起草',
          value: '0'
        }, {
          label: '已发布',
          value: '4'
        }],
        organId: undefined,
        sysOrgCode: undefined,
        queryParam: {
          orgCode: ''
        },
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: 'ICCID',
            align: 'center',
            dataIndex: 'simCode'
          },
          {
            title: '所属机构',
            align: 'center',
            dataIndex: 'organName'
          },
          {
            title: '终端芯片编号',
            align: 'center',
            dataIndex: 'obuChipCode'
          },
          {
            title: '终端IP',
            align: 'center',
            dataIndex: 'wlanIp'
          },
          {
            title: '发布状态',
            align: 'center',
            dataIndex: 'publishStatus',
            customRender: function (text) {
              if (text === '0') {
                return '起草';
              } else if (text === '4') {
                return '已发布';
              }
            }
          },
          {
            title: '状态',
            align: 'center',
            dataIndex: 'obuStatus',
            customRender: function (text) {
              if (text === '0') {
                return '正常';
              } else if (text === '1') {
                return '维修';
              } else if (text === '2') {
                return '报废';
              }
            }
          },
          {
            title: '是否启用',
            align: 'center',
            dataIndex: 'isActive',
            customRender: function (text) {
              if (text === '0') {
                return '是';
              } else if (text === '1') {
                return '否';
              }
            }
          }
        ],
        url: {
          list: '/basedata/bsEquipment/listData',
          delete: '/basedata/bsEquipment/delete',
          export: '/basedata/bsEquipment/export'
        }
      }
    },
    methods: {
      searchQuery2() {
        this.isorter = {}
        this.searchQuery()
      },
      searchReset() {
        this.queryParam = {}
        this.loadData(1)
      },
      handleChangeOrgan(orgData) {
        if (orgData === undefined) {
          this.queryParam.isLeaf = ''
          this.queryParam.organId = ''
        }
        if (orgData !== undefined) {
          this.queryParam.isLeaf = orgData.isLeaf
          this.queryParam.organId = orgData.id
        }
      },
      deleteData() {
        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择需要删除的数据')
          return
        }
        for (let r of this.selectionRows) {
          if (r.isActive === '0' || r.obuStatus !== '2') {
            this.$message.warning('设备['+ r.obuChipCode +']已经启用或者非报废状态，不能删除！')
            return
          }
        }
        this.$confirm({
          content: '是否删除选中的数据?',
          onOk: () => {
            this.handleDeleteData()
          }
        })
      },
      handleDeleteData() {
        let ids = this.selectedRowKeys
        delByIdsAction(this.url.delete, ids).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.searchQuery()
          } else {
            this.$message.error(res.message)
          }
        })
      },
      async exportExcel() {
        this.excelLoading = true
        await downloadFileAwait(this.url.export, '设备管理.xlsx', this.queryParam)
        this.excelLoading = false
      },
      addData() {
        this.$refs.modalForm.edit({})
        this.$refs.modalForm.title = '新增'
        this.$refs.modalForm.disableSubmit = false
      },
      editData() {
        if (this.selectionRows.length <= 0) {
          this.$message.warning('请选择需要编辑的数据')
          return
        }
        if (this.selectionRows.length !== 1) {
          this.$message.warning('编辑只能选择一条数据')
          return
        }
        this.$refs.modalForm.edit(this.selectionRows[0])
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.disableSubmit = false
      },
      openImportModal() {
        this.$refs.modalForm.openImportModal()
      }
    }
  }
</script>

<style lang="less" scoped>
</style>
