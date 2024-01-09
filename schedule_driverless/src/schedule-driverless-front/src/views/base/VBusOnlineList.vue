<template>
    <a-card :bordered="false">
        <!-- 车辆运营状态监控 -->
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xxl="5" :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="所属机构">
                            <GCIOrgTreeSelect
                                v-model="selectOrgId"
                                placeholder="请选择所属机构"
                                selectType="id"
                                isGetOption="true"
                                @change="changeOrg"
                            ></GCIOrgTreeSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="8" :xl="10" :lg="7" :md="8" :sm="24">
                        <a-form-item label="线路">
                            <div style="width: 140px; display: inline-block">
                                <a-radio-group v-model="routeTypeSelect" :default-value="2" @change="changeRouteType">
                                    <a-radio :value="1">所属</a-radio>
                                    <a-radio :value="2">运营</a-radio>
                                </a-radio-group>
                            </div>
                            <div style="width: 200px; display: inline-block">
                                <a-select
                                    allowClear
                                    placeholder="请选择所属线路"
                                    @change="handleChangeRoute"
                                    show-search
                                    v-model="routeSelect"
                                    :filter-option="addFilterOption"
                                    @search="handleRouteSearch"
                                    :loading="routeLoading"
                                >
                                    <a-select-option
                                        v-for="(item, index) in routeList"
                                        :key="index"
                                        :value="item.routeId"
                                    >
                                        {{ item.routeName }}
                                    </a-select-option>
                                </a-select>
                            </div>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="6" :xl="8" :lg="6" :md="8" :sm="12">
                        <a-form-item label="车辆">
                            <GCIBusSelect
                                :routeId="routeSelect"
                                :organId="selectOrgId"
                                v-model="queryParam.busId"
                                item="busId"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="5" :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="状态">
                            <j-dict-select-tag
                                style="width: 200px"
                                v-model="queryParam.status"
                                placeholder="请选择状态"
                                dictCode="v_bus_online_status"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="ICCID">
                            <a-input placeholder="请输入ICCID" v-model="queryParam.busSimCode" allowClear></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="终端芯片编号">
                            <a-input
                                placeholder="请输入终端芯片编号"
                                v-model="queryParam.busObuCode"
                                allowClear
                            ></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="IP">
                            <a-input placeholder="请输入IP" v-model="queryParam.ip" allowClear></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <!--              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>-->
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button type="primary" icon="download" @click="handleExportXls('车辆运营状态监控')">导出</a-button>
        </div>

        <!-- table区域-begin -->
        <div>
            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                class="j-table-force-nowrap"
                @change="handleTableChange"
            >
                <template slot="htmlSlot" slot-scope="text">
                    <div v-html="text"></div>
                </template>
                <template slot="imgSlot" slot-scope="text">
                    <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
                    <img
                        v-else
                        :src="getImgView(text)"
                        height="25px"
                        alt=""
                        style="max-width:80px;font-size: 12px;font-style: italic;"
                    />
                </template>
                <template slot="fileSlot" slot-scope="text">
                    <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
                    <a-button
                        v-else
                        :ghost="true"
                        type="primary"
                        icon="download"
                        size="small"
                        @click="uploadFile(text)"
                    >
                        下载
                    </a-button>
                </template>

                <!-- <span slot="action" slot-scope="text, record">
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
        </span> -->

                <template slot="customSlot" slot-scope="text, record">
                    <span :style="record.status == '0' ? '' : 'color:red;'">{{ text }}</span>
                </template>

                <template slot="statusSlot" slot-scope="text, record">
                    <span v-if="record.status == '0'">在线</span>
                    <span v-if="record.status == '1'" style="color:red;">下线</span>
                </template>
            </a-table>
        </div>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { getAction, postAction } from '../../api/manage';
import GCIRouteSelect from '@comp/gci/GCIRouteSelect';
import GCIBusSelect from '@comp/gci/GCIBusSelect';
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect';

export default {
    name: 'VBusOnlineList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIOrgTreeSelect,
        GCIRouteSelect,
        GCIBusSelect
    },
    data() {
        return {
            description: 'V_BUS_ONLINE管理页面',
            // 表头
            columns: [
                {
                    title: '#',
                    dataIndex: '',
                    key: 'rowIndex',
                    width: 60,
                    align: 'center',
                    customRender: function(t, r, index) {
                        return parseInt(index) + 1;
                    }
                },
                {
                    title: '状态',
                    align: 'center',
                    dataIndex: 'status',
                    scopedSlots: { customRender: 'statusSlot' }
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '车辆编码',
                    align: 'center',
                    dataIndex: 'busCode',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '终端ID',
                    align: 'center',
                    dataIndex: 'obuId',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '机构名称',
                    align: 'center',
                    dataIndex: 'organName',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '运营线路',
                    align: 'center',
                    dataIndex: 'routeNameRun',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: 'IMSI识别码',
                    align: 'center',
                    dataIndex: 'simChipCode',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: 'ICCID',
                    align: 'center',
                    dataIndex: 'busSimCode',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '终端芯片编号',
                    align: 'center',
                    dataIndex: 'busObuCode',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: 'ip',
                    align: 'center',
                    dataIndex: 'ip',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '最近站台',
                    align: 'center',
                    dataIndex: 'stationName',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '终端软件版本号',
                    align: 'center',
                    dataIndex: 'appVersion',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '终端固件号',
                    align: 'center',
                    dataIndex: 'obuRootVersion',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '主配置版本号',
                    align: 'center',
                    dataIndex: 'videoVersion',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '更新时间',
                    align: 'center',
                    dataIndex: 'lastUpdated',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '任务',
                    align: 'center',
                    dataIndex: 'serviceName',
                    scopedSlots: { customRender: 'customSlot' }
                },
                {
                    title: '线路版本号',
                    align: 'center',
                    dataIndex: 'routeVersion',
                    scopedSlots: { customRender: 'customSlot' }
                }
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
                list: '/base/VBusOnline/list',
                exportXlsUrl: '/base/VBusOnline/exportXls',
                getRouteUrl: '/baseservice/bsRoute/getRouteList'
            },
            dictOptions: {},
            selectOrgId: '',
            routeSelect: '',
            routeList: [],
            routeTypeSelect: 2, //类型默认选择运营
            routeLoading: false
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    created: function() {
        this.searchQuery();
        this.routeWidth = document.documentElement.clientWidth / 14;
    },
    methods: {
        initDictConfig() {
            this.changeRoute();
        },
        changeRouteType(event) {
            if (event.target.value === 1) {
                this.queryParam.routeIdBelongTo = this.routeSelect;
                this.queryParam.routeIdRun = null;
            }
            if (event.target.value === 2) {
                this.queryParam.routeIdRun = this.routeSelect;
                this.queryParam.routeIdBelongTo = null;
            }
        },
        changeRouteSelect() {
            if (this.routeTypeSelect === 1) {
                this.queryParam.routeIdBelongTo = this.routeSelect;
                this.queryParam.routeIdRun = null;
            }
            if (this.routeTypeSelect === 2) {
                this.queryParam.routeIdRun = this.routeSelect;
                this.queryParam.routeIdBelongTo = null;
            }
            this.routeId = this.routeSelect;
        },
        changeOrg(value) {
            this.routeSelect = '';
            this.queryParam.routeId = '';
            if (value) {
                this.changeRoute(value);
            }
            this.selectOrgId = value;
            this.queryParam.organId = value;
        },
        changeRoute(organId, routeName) {
            let param = {
                organRunId: organId,
                routeName: routeName,
                pageNo: 1,
                pageSize: 30
            };
            let that = this;
            getAction(this.url.getRouteUrl, param).then(res => {
                if (res.success) {
                    that.routeList = res.result.records;
                }
            });
        },
        addFilterOption(input, option) {
            let flag = option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
            return flag;
        },
        handleRouteSearch(value) {
            this.routeNameSearch = value;
            this.changeRoute(this.selectOrgId, value);
        },
        handleChangeRoute(value) {
            console.log('value', value);
            this.queryParam.routeId = value;
        },
        searchReset() {
            this.queryParam = {};
            this.loadData(1);
        },
        searchQuery() {
            this.loadData(1);
        },
        loadData(arg) {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!');
                return;
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }

            this.loading = true;
            var params = this.getQueryParams(); // 查询条件
            postAction(this.url.list, params)
                .then(res => {
                    if (res.success) {
                        this.dataSource = res.result.records;
                        this.ipagination.total = res.result.total;
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .finally(() => {
                    this.loading = false;
                });
        }
    }
};
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
