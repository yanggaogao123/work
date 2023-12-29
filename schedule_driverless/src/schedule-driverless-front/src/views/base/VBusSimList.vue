<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="所属机构">
              <GCIOrgTreeSelect v-model="queryParam.organCode" placeholder="请选择所属机构"></GCIOrgTreeSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="线路名称">
              <GCIRouteSelect placeholder="请选择线路" v-model="queryParam.routeId" @change="changeRoute" mode="mode"></GCIRouteSelect>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="ICCID">
              <a-input placeholder="请输入ICCID" v-model="queryParam.iccid"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="IMSI识别码">
              <a-input placeholder="请输入IMSI识别码" v-model="queryParam.simChipCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车辆牌号">
              <a-input placeholder="请输入车辆牌号" v-model="queryParam.numberPlate"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="终端Id">
              <a-input placeholder="请输入终端Id" v-model="queryParam.obuid"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="终端IMEI">
              <a-input placeholder="请输入终端IMEI" v-model="queryParam.gprsImei"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
      <a-button type="primary" @click="searchReset" icon="reload">重置</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('终端SIM查询')">导出</a-button>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="uploadFile(text)">
            下载
          </a-button>
        </template>

      </a-table>
    </div>

  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import BsStationNameModal from './modules/BsStationNameModal'
  import { httpAction } from '@/api/manage'
  import { getAction, postAction } from '../../api/manage';
  import GCIOrgTreeSelect from "@/components/gci/GCIOrgTreeSelect";
  import GCIRouteSelect from "@/components/gci/GCIRouteSelect";

  export default {
    name: "VBusSimList",
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      GCIOrgTreeSelect,GCIRouteSelect
    },
    data () {
      return {
        description: '终端sim查询',
          isorter: {
              column: 'organName',
              order: 'desc'
          },
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
            title:'所属机构',
            align:"center",
            dataIndex: 'organName'
          },
          {
            title:'线路编码',
            align:"center",
            dataIndex: 'routeCode'
          },
          {
            title:'线路名',
            align:"center",
            dataIndex: 'routeName'
          },
          {
            title:'终端Id',
            align:"center",
            dataIndex: 'obuid'
          },
          {
            title:'ICCID',
            align:"center",
            dataIndex: 'iccid'
          },
          {
            title:'车辆名称',
            align:"center",
            dataIndex: 'busName'
          },
          {
            title:'车辆牌号',
            align:"center",
            dataIndex: 'numberPlate'
          },
          {
            title:'IMSI识别码',
            align:"center",
            dataIndex: 'simChipCode'
          },

          {
            title:'终端芯片号',
            align:"center",
            dataIndex: 'obuChipCode'
          },
          {
            title:'手机号码',
            align:"center",
            dataIndex: 'phoneNumber'
          },
          {
            title:'终端IMEI',
            align:"center",
            dataIndex: 'gprsImei'
          },
/*          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            // fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }*/
        ],
        url: {
          list: "/base/vBusSim/list",
          exportXlsUrl: "/base/vBusSim/exportXls",
        },
        dictOptions:{},
      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
        loadData(arg) {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!')
                return
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            var params = this.getQueryParams();// 查询条件
            console.log(params);
            this.loading = true;
            postAction(this.url.list, params).then((res) => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                } else {
                    this.$message.warning(res.message)
                }
                this.loading = false;
            })
        },
        changeRoute(value) {
            console.log(value)
            this.queryParam.routeId = value
        },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>