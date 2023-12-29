<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect v-model="queryParam.orgName"
                                placeholder="请选择所属机构"
                                selectType="orgName"
                                :isGetOption="true"
                                @change="out">
              </GCIOrgTreeSelect>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车辆牌照">
              <a-input placeholder="请输入车辆牌照" v-model="queryParam.numberPlate" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="终端ID">
              <a-input placeholder="请输入终端ID" v-model="queryParam.obuid" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车辆类型">
              <j-dict-select-tag
                v-model="queryParam.busType"
                placeholder="请选择车辆类型"
                dictCode="bus_type"
              />
            </a-form-item>
          </a-col>

          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <!--
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a> -->
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('车辆对比')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>
    -->

    <!-- table区域-begin -->
    <div>
      <!--
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div> -->

      <a-table
        class="j-table-force-nowrap"
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :scroll="{ x: 1600 }"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">
        <!--   :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"     -->

        <template slot="edNumberPlateSlot" slot-scope="text, record">
          <span :style="record.markNumberPlate=='1'?'color:red;':''">
            <j-ellipsis :value="record.edNumberPlate"/>
          </span>
        </template>
        <template slot="bsNumberPlateSlot" slot-scope="text, record">
          <span :style="record.markNumberPlate=='1'?'color:red;':''">
            <j-ellipsis :value="record.bsNumberPlate"/>
          </span>
        </template>
        <!--  车辆标识       -->
        <template slot="edBusCodeSlot" slot-scope="text, record">
          <span :style="record.markBusCode=='1'?'color:red;':''">
            <j-ellipsis :value="record.edBusCode"/>
          </span>
        </template>
        <template slot="bsBusCodeSlot" slot-scope="text, record">
          <span :style="record.markBusCode=='1'?'color:red;':''">
            <j-ellipsis :value="record.bsBusCode"/>
          </span>
        </template>
        <!-- 终端ID       -->
        <template slot="edObuIdSlot" slot-scope="text, record">
          <span :style="record.markObuId=='1'?'color:red;':''">{{record.edObuId}}</span>
        </template>
        <template slot="bsObuIdSlot" slot-scope="text, record">
          <span :style="record.markObuId=='1'?'color:red;':''">{{record.bsObuId}}</span>
        </template>
        <!-- 所属机构       -->
        <template slot="edOrganNameSlot" slot-scope="text, record">
          <span :style="record.markOrganName=='1'?'color:red;':''">
            <j-ellipsis :value="record.edOrganName"/>
          </span>
        </template>
        <template slot="bsOrganNameSlot" slot-scope="text, record">
          <span :style="record.markOrganName=='1'?'color:red;':''">
            <j-ellipsis :value="record.bsOrganName"/>
          </span>
        </template>
        <!-- 所属线路       -->
        <template slot="edRouteNameSlot" slot-scope="text, record">
          <span :style="record.markRouteName=='1'?'color:red;':''">
            <j-ellipsis :value="record.edRouteName"/>
          </span>
        </template>
        <template slot="bsRouteNameSlot" slot-scope="text, record">
          <span :style="record.markRouteName=='1'?'color:red;':''">
            <j-ellipsis :value="record.bsRouteName"/>
          </span>
        </template>
        <!-- 终端信息       -->
        <template slot="edObuInfoSlot" slot-scope="text, record">
          <span :style="record.markObuInfo=='1'?'color:red;':''">
            <j-ellipsis :value="record.edObuInfo"/>
          </span>
        </template>
        <template slot="bsObuInfoSlot" slot-scope="text, record">
          <span :style="record.markObuInfo=='1'?'color:red;':''">
            <j-ellipsis :value="record.bsObuInfo"/>
          </span>
        </template>

        <!--   iccid     -->
        <template slot="edSimInfoSlot" slot-scope="text, record">
          <span :style="record.markSimInfo=='1'?'color:red;':''">
            <j-ellipsis :value="record.edSimInfo"/>
          </span>
        </template>
        <template slot="bsSimInfoSlot" slot-scope="text, record">
          <span :style="record.markSimInfo=='1'?'color:red;':''">
            <j-ellipsis :value="record.bsSimInfo"/>
          </span>
        </template>

        <!-- 车辆类型    -->
        <template slot="edBusTypeSlot" slot-scope="text, record">
          <span :style="record.markBusType=='1'?'color:red;':''">{{record.edBusType}}</span>
        </template>
        <template slot="bsBusTypeSlot" slot-scope="text, record">
          <span :style="record.markBusType=='1'?'color:red;':''">{{record.bsBusType}}</span>
        </template>

        <!--  车辆状态    -->
        <template slot="edBusStatusSlot" slot-scope="text, record">
          <span :style="record.markBusStatus=='1'?'color:red;':''">{{record.edBusStatus}}</span>
        </template>
        <template slot="bsBusStatusSlot" slot-scope="text, record">
          <span :style="record.markBusStatus=='1'?'color:red;':''">{{record.bsBusStatus}}</span>
        </template>

        <!-- 最近修改时间  -->
        <template slot="edLastUpdatedSlot" slot-scope="text, record">
<!--          <span :style="record.bsLastUpdated==record.edLastUpdated?'':'color:red;'">{{record.edLastUpdated}}</span>-->
          <span :style="record.bsLastUpdated==record.edLastUpdated?'':'color:red;'">
            <j-ellipsis :value="record.edLastUpdated"/>
          </span>
        </template>
        <template slot="bsLastUpdatedSlot" slot-scope="text, record">
          <span :style="record.bsLastUpdated==record.edLastUpdated?'':'color:red;'">
            <j-ellipsis :value="record.bsLastUpdated"/>
          </span>
        </template>


        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

        <span slot="edNumberPlateTitle">
          <a-tooltip title="车辆牌照">车辆牌照</a-tooltip>
        </span>
        <span slot="bsNumberPlateTitle">
          <a-tooltip title="车辆牌照">车辆牌照</a-tooltip>
        </span>

        <span slot="edBusCodeTitle">
          <a-tooltip title="车辆标识">车辆标识</a-tooltip>
        </span>
        <span slot="bsBusCodeTitle">
          <a-tooltip title="车辆标识">车辆标识</a-tooltip>
        </span>

        <span slot="edObuIdTitle">
          <a-tooltip title="终端ID">终端ID</a-tooltip>
        </span>
        <span slot="bsObuIdTitle">
          <a-tooltip title="终端ID">终端ID</a-tooltip>
        </span>

        <span slot="edOrganNameTitle">
          <a-tooltip title="所属机构">所属机构</a-tooltip>
        </span>
        <span slot="bsOrganNameTitle">
          <a-tooltip title="所属机构">所属机构</a-tooltip>
        </span>

        <span slot="edRouteNameTitle">
          <a-tooltip title="所属线路">所属线路</a-tooltip>
        </span>
        <span slot="bsRouteNameTitle">
          <a-tooltip title="所属线路">所属线路</a-tooltip>
        </span>

        <span slot="edObuInfoTitle">
          <a-tooltip title="终端信息">终端信息</a-tooltip>
        </span>
        <span slot="bsObuInfoTitle">
          <a-tooltip title="终端信息">终端信息</a-tooltip>
        </span>

        <span slot="edSimInfoTitle">
          <a-tooltip title="ICCID">ICCID</a-tooltip>
        </span>
        <span slot="bsSimInfoTitle">
          <a-tooltip title="ICCID">ICCID</a-tooltip>
        </span>

        <span slot="edBusTypeTitle">
          <a-tooltip title="车辆类型">车辆类型</a-tooltip>
        </span>
        <span slot="bsBusTypeTitle">
          <a-tooltip title="车辆类型">车辆类型</a-tooltip>
        </span>

        <span slot="edBusStatusTitle">
          <a-tooltip title="车辆状态">车辆状态</a-tooltip>
        </span>
        <span slot="bsBusStatusTitle">
          <a-tooltip title="车辆状态">车辆状态</a-tooltip>
        </span>

        <span slot="edLastUpdatedTitle">
          <a-tooltip title="最近修改时间">最近修改时间</a-tooltip>
        </span>
        <span slot="bsLastUpdatedTitle">
          <a-tooltip title="最近修改时间">最近修改时间</a-tooltip>
        </span>
      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <busCompare-modal ref="modalForm" @ok="modalFormOk"></busCompare-modal>
  </a-card>
</template>

<script>
  import '@/assets/less/TableExpand.less'
  import BusCompareModal from './modules/BusCompareModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import GCIOrgTreeSelect from '@comp/gci/GCIOrgTreeSelect'
  import JEllipsis from '@/components/jeecg/JEllipsis'

  export default {
    name: "BusCompareList",
    mixins:[JeecgListMixin],
    components: {
      GCIOrgTreeSelect,
      BusCompareModal,
      JEllipsis
    },
    data () {
      return {
        description: '车辆对比管理页面',
        // 表头
        columns: [
          {
            title: '编辑表（ED）数据',
            children: [
              {
                // title: '车辆牌照',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edNumberPlateSlot'},
                slots: { title: 'edNumberPlateTitle'}
              },
              {
                // title: '车辆标识',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edBusCodeSlot'},
                slots: { title: 'edBusCodeTitle'}
              },
              {
                // title: '终端ID',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edObuIdSlot'},
                slots: { title: 'edObuIdTitle'}
              },
              {
                // title: '所属机构',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edOrganNameSlot'},
                slots: { title: 'edOrganNameTitle'}
              },
              {
                // title: '所属线路',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edRouteNameSlot'},
                slots: { title: 'edRouteNameTitle'}
              },

              {
                // title: '终端信息',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edObuInfoSlot'},
                slots: { title: 'edObuInfoTitle'}
              },

              {
                // title: 'ICCID',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edSimInfoSlot'},
                slots: { title: 'edSimInfoTitle'}
              },
              {
                // title: '车辆类型',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edBusTypeSlot'},
                slots: { title: 'edBusTypeTitle'}
              },
              {
                // title: '车辆状态',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edBusStatusSlot'},
                slots: { title: 'edBusStatusTitle'}
              },
              {
                // title: '最近修改时间',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'edLastUpdatedSlot'},
                slots: { title: 'edLastUpdatedTitle'}
              },
            ]
          },
          {
            title: '发布表（BS）数据',
            children: [
              {
                // title: '车辆牌照',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsNumberPlateSlot'},
                slots: { title: 'bsNumberPlateTitle'}
              },
              {
                // title: '车辆标识',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsBusCodeSlot'},
                slots: { title: 'bsBusCodeTitle'}
              },
              {
                // title: '终端ID',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsObuIdSlot'},
                slots: { title: 'bsObuIdTitle'}
              },
              {
                // title: '所属机构',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsOrganNameSlot'},
                slots: { title: 'bsOrganNameTitle'}
              },
              {
                // title: '所属线路',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsRouteNameSlot'},
                slots: { title: 'bsRouteNameTitle'}
              },
              {
                // title: '终端信息',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsObuInfoSlot'},
                slots: { title: 'bsObuInfoTitle'}
              },
              {
                // title: 'ICCID',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsSimInfoSlot'},
                slots: { title: 'bsSimInfoTitle'}
              },
              {
                // title: '车辆类型',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsBusTypeSlot'},
                slots: { title: 'bsBusTypeTitle'}
              },
              {
                // title: '车辆状态',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsBusStatusSlot'},
                slots: { title: 'bsBusStatusTitle'}
              },
              {
                // title: '最近修改时间',
                align:"center",
                width: 50,
                ellipsis: true,
                scopedSlots: {customRender: 'bsLastUpdatedSlot'},
                slots: { title: 'bsLastUpdatedTitle'}
              }
            ]
          }
        ],
		url: {
          list: "/basedata/busCompare/list",
          delete: "/basedata/busCompare/delete",
          deleteBatch: "/basedata/busCompare/deleteBatch",
          exportXlsUrl: "basedata/busCompare/exportXls",
          importExcelUrl: "basedata/busCompare/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
      out(){
        //debugger
        console.info(this.queryParam)
      }

    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';
</style>