<template>
    <a-card :bordered="false">
        <!-- 线路发布页面 -->
        <div id="recover">
            <!-- 版本恢复弹框 -->
            <a-modal v-model="visible" title="版本恢复" :width="1300" :footer="false" :afterClose="clearChoice">
                <!-- 查询区域 -->
                <div class="table-page-search-wrapper">
                    <a-form layout="inline" @keyup.enter.native="searchQuery">
                        <a-row :gutter="24">
                            <a-col :xl="4" :lg="7" :md="8" :sm="24">
                                <a-form-item label="方向">
                                    <a-select
                                        default-value="0"
                                        placeholder="请选择方向"
                                        v-model="direction"
                                        allow-clear
                                    >
                                        <!-- <a-select-option value="-1">全部</a-select-option> -->
                                        <a-select-option value="0">上行</a-select-option>
                                        <a-select-option value="1">下行</a-select-option>
                                        <a-select-option value="2">无</a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :xl="6" :lg="7" :md="8" :sm="24">
                                <a-form-item label="支线路">
                                    <a-select placeholder="请选择支线路" v-model="routesubId" allowClear>
                                        <a-select-option
                                            v-if="routesubs.length > 0"
                                            v-for="routesub in routesubs"
                                            :key="routesub.routesubId"
                                            :value="routesub.routesubId"
                                        >
                                            {{ routesub.routesubName }}
                                        </a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :xl="5" :lg="7" :md="10" :sm="24">
                                <a-form-item label="站台名称">
                                    <a-input
                                        placeholder="请输入站台名称"
                                        style="width:156px;"
                                        v-model="stationName"
                                    ></a-input>
                                </a-form-item>
                            </a-col>
                            <a-col :xl="6" :lg="7" :md="8" :sm="24">
                                <a-form-item label="历史版本号">
                                    <a-select placeholder="请选择版本" v-model="recoveryHisVersion" allowClear>
                                        <a-select-option
                                            v-if="hisVersions.length > 0"
                                            v-for="hisVersion in hisVersions"
                                            :key="hisVersion"
                                            :value="hisVersion"
                                        >
                                            {{ hisVersion }}
                                        </a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :xl="4" :lg="7" :md="8" :sm="24">
                                <span style="float:left; overflow: hidden" class="table-page-search-submitButtons">
                                    <a-button type="primary" @click="queryRecord('1')" icon="search">查询</a-button>
                                    <a-button type="primary" @click="recoveryVersion" style="margin-left: 8px"
                                        >恢复</a-button
                                    >
                                </span>
                            </a-col>
                        </a-row>
                    </a-form>
                </div>
                <!--        恢复版本页面表格-->
                <div>
                    <a-table
                        :scroll="{ y: 500 }"
                        ref="table"
                        size="middle"
                        bordered
                        rowKey="id"
                        :columns="recoveryeColumns"
                        :dataSource="hisRecord"
                        :pagination="ipaginationRecoverye"
                        :loading="loading"
                        @change="handleTableChangeRecoverye"
                    >
                        <!--            版本号-->
                        <template slot="hisVersion">{{ hisVersion }}</template>

                        <template slot="currentVersion"> {{ currentVersion }}</template>

                        <!--           标红 -->

                        <template slot="hisMileage" slot-scope="text, record">
                            <span :style="!record.mileageSame ? 'color:red;' : ''">{{ record.hisMileage }}</span>
                        </template>
                        <template slot="bsMileage" slot-scope="data">
                            <span :style="!data.mileageSame ? 'color:red;' : ''">{{ data.bsMileage }}</span>
                        </template>

                        <template slot="hisOrderNumber" slot-scope="data">
                            <span :style="!data.orderNumberSame ? 'color:red;' : ''">{{ data.hisOrderNumber }}</span>
                        </template>
                        <template slot="bsOrderNumber" slot-scope="data">
                            <span :style="!data.orderNumberSame ? 'color:red;' : ''">{{ data.bsOrderNumber }}</span>
                        </template>

                        <template slot="hisRouteStationName" slot-scope="data">
                            <span :style="!data.routeStationNameSame ? 'color:red;' : ''">{{
                                data.hisRouteStationName
                            }}</span>
                        </template>
                        <template slot="bsRouteStationName" slot-scope="data">
                            <span :style="!data.routeStationNameSame ? 'color:red;' : ''">{{
                                data.bsRouteStationName
                            }}</span>
                        </template>

                        <template slot="hisBusStopCode" slot-scope="data">
                            <span :style="!data.busStopCodeSame ? 'color:red;' : ''">{{ data.hisBusStopCode }}</span>
                        </template>
                        <template slot="bsBusStopCode" slot-scope="data">
                            <span :style="!data.busStopCodeSame ? 'color:red;' : ''">{{ data.bsBusStopCode }}</span>
                        </template>

                        <template slot="hisRouteName" slot-scope="data">
                            <span :style="!data.routeNameSame ? 'color:red;' : ''">{{ data.hisRouteName }}</span>
                        </template>
                        <template slot="bsRouteName" slot-scope="data">
                            <span :style="!data.routeNameSame ? 'color:red;' : ''">{{ data.bsRouteName }}</span>
                        </template>

                        <template slot="hisRouteSubName" slot-scope="data">
                            <span :style="!data.routeSubNameSame ? 'color:red;' : ''">{{ data.hisRouteSubName }}</span>
                        </template>
                        <template slot="bsRouteSubName" slot-scope="data">
                            <span :style="!data.routeSubNameSame ? 'color:red;' : ''">{{ data.bsRouteSubName }}</span>
                        </template>
                    </a-table>
                </div>
            </a-modal>
        </div>
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-form-item label="运营机构">
                    <GCIOrgTreeSelect
                        v-model="queryParam.orgCode"
                        style="width:300px"
                        placeholder="请选择所属机构"
                    ></GCIOrgTreeSelect>
                </a-form-item>
                <a-form-item label="线路编码">
                    <a-input
                        allowClear
                        placeholder="请输入线路编码"
                        v-model="queryParam.routeCode"
                        style="width:200px;"
                    ></a-input>
                </a-form-item>
                <a-form-item label="线路名称">
                    <a-input
                        allowClear
                        placeholder="请输入线路名称"
                        v-model="queryParam.routeName"
                        style="width:200px;"
                    ></a-input>
                </a-form-item>
                <a-form-item label="定时发布时间">
                    <a-range-picker
                        allowClear
                        v-model="publishTimes"
                        style="width: 100%"
                        format="YYYY-MM-DD"
                        :placeholder="['开始日期', '结束日期']"
                        @change="onChangeDate"
                    />
                </a-form-item>
                <span class="table-page-search-submitButtons">
                    <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                    <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>

                    <!-- <a-button type="primary" @click="batchPub('pre')" style="margin-left: 8px">预发布</a-button> -->
                    <a-tooltip>
                        <template slot="title">
                            <div>问：线路发布的“预发布”有什么作用</div>
                            <div>
                                答：预发布就是线路发布测试，测试是否可成功发布
                            </div>
                        </template>
                        <a-button
                            type="primary"
                            icon="question-circle"
                            @click="batchPub('pre')"
                            style="margin-left: 8px"
                            >预发布</a-button
                        >
                    </a-tooltip>
                    <a-button type="primary" @click="batchPub" style="margin-left: 8px">发布</a-button>
                    <a-button type="primary" @click="recovery" style="margin-left: 8px">恢复历史版本</a-button>
                    <a-button type="primary" @click="openRegularRelease" style="margin-left: 8px">定时发布</a-button>
                </span>
                <template v-if="toggleSearchStatus">
                    <a-form-item label="定时发布时间">
                        <j-date
                            class="query-group-cust"
                            dateFormat="YYYY-MM-DD"
                            placeholder="请选择日期"
                            v-model="releaseTime"
                        />
                    </a-form-item>
                    <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                        <a-button type="primary" @click="recovery">恢复历史版本</a-button>
                        <a-button type="primary" @click="regularRelease" style="margin-left: 8px">定时发布</a-button>
                    </span>
                </template>
            </a-form>
        </div>
        <a-modal
            title="请选择定时发布时间"
            :width="width"
            :visible="timeShow"
            :confirmLoading="confirmLoading"
            switchFullscreen
            cancelText="关闭"
            okText="确认"
            @cancel="handleCancel"
            @ok="regularRelease"
        >
            <j-date class="query-group-cust" dateFormat="YYYY-MM-DD" placeholder="请选择日期" v-model="releaseTime" />
        </a-modal>
        <!-- 查询区域-END -->

        <div>
            <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
                <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
                >项
                <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div>

            <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :columns="columns"
                :dataSource="dataSource"
                :pagination="ipagination"
                :loading="loading"
                :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
                class="j-table-force-nowrap"
                @change="handleTableChange"
            >
                <template #changeTime>
                    <a-tooltip>
                        <template slot="title">
                            <div>问：线路发布模块的“修改时间”列是指什么的修改时间？</div>
                            <div>
                                答：修改时间列是指线路的最后一次修改时间，不是线路发布时间
                            </div>
                        </template>
                        <a-icon type="question-circle" />
                        修改时间
                    </a-tooltip>
                </template>
            </a-table>
        </div>
    </a-card>
</template>
<script>
import { getAction } from '@/api/manage';

import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import EdRouteModalDev from './modules/EdRouteModalDev';
import JDate from '@/components/jeecg/JDate.vue';
import GCIOrgTreeSelect from '../../components/gci/GCIOrgTreeSelect';
import { postAction } from '../../api/manage';

export default {
    name: 'EdRouteList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIOrgTreeSelect,
        EdRouteModalDev,
        JDate
    },
    data() {
        return {
            timeShow: false,
            queryParam: { status: '0' },
            hisVersion: '历史版本：',
            currentVersion: '当前版本：',
            routesubId: undefined,
            hisVersion2Recovery: '',
            recoveryHisVersion: '',
            routeVersion: '',
            stationName: '',
            direction: '0',
            publishTimes: [],
            routeList: [],
            routesubs: [],
            hisVersions: [],
            hisRecord: [],
            visible: false,
            releaseTime: null,
            description: 'ed_route管理页面',
            /* 排序参数 */
            isorter: {
                column: 'lastUpdated',
                order: 'desc'
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
                        return parseInt(index) + 1;
                    }
                },
                {
                    title: '运营机构',
                    align: 'center',
                    dataIndex: 'organId',
                    scopedSlots: { customRender: 'action' },
                    customRender: function(text, record) {
                        return record.organRunName;
                    },
                    sorter: true
                },
                {
                    title: '线路名称',
                    align: 'center',
                    dataIndex: 'routeName',
                    sorter: true
                },
                {
                    title: '线路编码',
                    align: 'center',
                    dataIndex: 'routeCode',
                    sorter: true
                },
                {
                    title: '线路简称',
                    align: 'center',
                    dataIndex: 'shortName',
                    sorter: true
                },
                {
                    title: '线路版本',
                    align: 'center',
                    dataIndex: 'routeVersion',
                    sorter: true
                },
                {
                    title: '记录状态',
                    align: 'center',
                    dataIndex: 'recordStatus',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.recordStatusText;
                    }
                },
                // {
                //     title: '是否定点发班',
                //     align: 'center',
                //     dataIndex: 'isFix_dictText',
                //     sorter: true
                // },
                {
                    title: '更新者',
                    align: 'center',
                    dataIndex: 'lastOperator',
                    sorter: true
                },
                {
                    // title: '修改时间',
                    align: 'center',
                    dataIndex: 'lastUpdated',
                    sorter: true,
                    slots: {
                        title: 'changeTime'
                    }
                },
                {
                    title: '定时发布时间',
                    align: 'center',
                    dataIndex: 'publishTime',
                    sorter: true
                }
            ],
            /* 分页参数 */
            ipaginationRecoverye: {
                current: 1,
                pageSize: 50,
                pageSizeOptions: ['10', '20', '30'],
                showTotal: (total, range) => {
                    return range[0] + '-' + range[1] + ' 共' + total + '条';
                },
                showQuickJumper: true,
                showSizeChanger: true,
                total: 0
            },
            recoveryeColumns: [
                {
                    scopedSlots: {
                        title: 'currentVersion'
                    },
                    align: 'center',
                    dataIndex: 'currentVersion',
                    customHeaderCell: this.customHeaderCellLefit,
                    children: [
                        {
                            title: '线路',
                            align: 'center',
                            width: 70,
                            scopedSlots: { customRender: 'bsRouteName' }
                        },
                        {
                            title: '站台编码',
                            align: 'center',
                            width: 90,
                            scopedSlots: { customRender: 'bsBusStopCode' }
                        },
                        {
                            title: '站台名称',
                            align: 'center',
                            scopedSlots: { customRender: 'bsRouteStationName' }
                            // width:200
                        },
                        {
                            title: '站序',
                            align: 'center',
                            width: 60,
                            scopedSlots: { customRender: 'bsOrderNumber' }
                        },
                        {
                            title: '支线路',
                            align: 'center',
                            scopedSlots: { customRender: 'bsRouteSubName' }
                        },
                        {
                            title: '里程',
                            align: 'center',
                            width: 60,
                            scopedSlots: { customRender: 'bsMileage' },
                            customHeaderCell: this.customHeaderCellTwoLeft,
                            customCell: () => {
                                return {
                                    style: {
                                        'border-right': '2px solid rgba(93,95,94,0.45)'
                                    }
                                };
                            }
                        }
                    ]
                },
                {
                    scopedSlots: {
                        title: 'hisVersion'
                    },
                    align: 'center',
                    dataIndex: 'hisVersion',
                    customHeaderCell: this.customHeaderCellRight,

                    children: [
                        {
                            title: '线路',
                            width: 70,
                            align: 'center',
                            scopedSlots: { customRender: 'hisRouteName' },
                            customHeaderCell: this.customHeaderCellTwoRight,
                            customCell: () => {
                                return {
                                    style: {
                                        'border-left': '2px solid rgba(93,95,94,0.45)'
                                    }
                                };
                            }
                        },
                        {
                            title: '站台编码',
                            align: 'center',
                            width: 90,
                            scopedSlots: { customRender: 'hisBusStopCode' }
                        },
                        {
                            title: '站台名称',
                            align: 'center',
                            // width:200,
                            scopedSlots: { customRender: 'hisRouteStationName' }
                        },
                        {
                            title: '站序',
                            align: 'center',
                            width: 60,
                            scopedSlots: { customRender: 'hisOrderNumber' }
                        },
                        {
                            title: '支线路',
                            align: 'center',
                            scopedSlots: { customRender: 'hisRouteSubName' }
                        },
                        {
                            title: '里程',
                            align: 'center',
                            width: 60,
                            scopedSlots: { customRender: 'hisMileage' }
                        }
                    ]
                }
            ],
            url: {
                list: '/base/edRoute/listDev',
                publishBatch: '/base/edRoute/publish',
                prePublish: '/base/edRoute/prePublish',
                regularRelease: '/base/edRoute/regularRelease',
                recovery: '/base/edRoute/recovery',
                edition: '/base/edRoute/getHisRouteVersionAndBsRoutesubsVo',
                getBsRouteAndHisRouteList: '/base/edRoute/getBsRouteAndHisRouteList',
                syncBaseRoute: '/base/edRoute/syncBaseRoute'
            },
            dictOptions: {}
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        initDictConfig() {},
        handleCancel() {
            this.timeShow = false;
        },
        openRegularRelease() {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
                return;
            }
            this.timeShow = true;
        },
        searchReset() {
            this.queryParam = {};
            this.publishTimes = [];
            this.loadData(1);
        },
        onChangeDate(value, dateString) {
            this.queryParam.beginDate = dateString[0];
            this.queryParam.endDate = dateString[1];
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
            var params = this.getQueryParams(); // 查询条件
            this.loading = true;
            postAction(this.url.list, params)
                .then(res => {
                    if (res.success) {
                        this.dataSource = res.result.records;
                        this.ipagination.total = res.result.total;
                    } else {
                        this.$message.warning(res.message);
                    }
                    this.loading = false;
                })
                .finally(() => {
                    this.loading = false;
                });
            this.onClearSelected();
        },
        /* 发布 */
        batchPub(operator) {
            let that = this;
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
            } else {
                let ids = this.selectionRows.map(route => route.id);
                console.log('operator == ', operator);
                let operatorUrl = operator === 'pre' ? this.url.prePublish : this.url.publishBatch;
                this.$confirm({
                    title: '确认发布',
                    content: '是否发布选中数据?',
                    onOk: () => {
                        that.loading = true;
                        postAction(operatorUrl, ids)
                            .then(res => {
                                if (res.success) {
                                    this.$message.warning(res.message);
                                    this.loadData();
                                } else {
                                    alert(res.message);
                                }
                            })
                            .finally(() => {
                                that.loading = false;
                            });
                    }
                });
            }
        },
        syncBaseRoute() {
            getAction(this.url.syncBaseRoute).then(res => {
                if (res.success) {
                    this.$message.success(res.message);
                    this.loadData();
                } else {
                    this.$message.warning(res.message);
                }
            });
        },
        /* 定时发布 */
        regularRelease() {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
                this.handleCancel();
            } else {
                let ids = this.selectionRows.map(row => row.id);
                let time = this.releaseTime;
                this.$confirm({
                    title: '确认定时发布',
                    content: '是否定时发布选中数据?',
                    onOk: () => {
                        if (time == null) {
                            this.$message.warning('未选择时间');
                        } else {
                            postAction(this.url.regularRelease, { routeIds: ids, releaseTime: time }).then(res => {
                                if (res.success) {
                                    this.$message.success(res.message);
                                    this.loadData();
                                    this.releaseTime = null;
                                    this.handleCancel();
                                } else {
                                    this.$message.warning(res.message);
                                }
                            });
                        }
                    }
                });
            }
        },
        /* 恢复版本 */
        recovery() {
            if (this.selectedRowKeys.length <= 0) {
                this.$message.warning('请选择一条记录！');
            } else if (this.selectedRowKeys.length > 1) {
                this.$message.warning('恢复线路只能一次选一条线路！');
                this.onClearSelected();
            } else {
                this.direction = '0';
                this.visible = true;
                this.getValue();
                this.queryRecord('1');
            }
        },
        /* 查询版本号和支线路 */
        getValue() {
            let param = this.selectionRows.map(row => row.id).join(',');
            getAction(this.url.edition, { routeId: param })
                .then(res => {
                    if (res.success) {
                        this.routesubs = res.result.bsRouteSubVos;
                        this.hisVersions = res.result.hisVersions;
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .catch(e => {
                    console.error(e);
                })
                .then(() => {
                    this.confirmLoading = false;
                });
        },
        /* 查询历史记录和基础记录 */
        queryRecord(pageNo) {
            if (!pageNo) {
                this.ipaginationRecoverye.current = pageNo;
            }
            let routId = this.selectionRows.map(row => row.id).join(',');
            postAction(this.url.getBsRouteAndHisRouteList, {
                direction: this.direction,
                routeStationName: this.stationName,
                routeSubId: this.routeSubId,
                hisVersion: this.recoveryHisVersion,
                pageNo: this.ipaginationRecoverye.current,
                pageSize: this.ipaginationRecoverye.pageSize,
                routeId: routId
            })
                .then(res => {
                    if (res.success) {
                        this.hisRecord = res.result.list;
                        this.ipaginationRecoverye.total = res.result.total;
                        this.hisVersion2Recovery = res.result.hisVersion;
                        this.currentVersion = '当前版本：' + res.result.currentVersion;
                        this.hisVersion = '历史版本：' + res.result.hisVersion;
                    } else {
                        this.$message.warning(res.message);
                    }
                })
                .catch(e => {
                    console.error(e);
                })
                .then(() => {
                    this.confirmLoading = false;
                });
        },
        handleTableChangeRecoverye(pagination, filters, sorter) {
            // 分页、排序、筛选变化时触发
            this.ipaginationRecoverye = pagination;
            this.queryRecord(pagination.current);
        },
        /* 恢复 */
        recoveryVersion() {
            if (this.hisVersion2Recovery == null || this.hisVersion2Recovery === '') {
                this.$message.warning('没有可恢复的历史版本');
                return;
            }
            this.$confirm({
                title: '确认恢复当前线路版本？',
                content: '是否恢复当前线路版本为' + this.hisVersion2Recovery,
                onOk: () => {
                    let ids = this.selectionRows.map(row => row.id);
                    if (this.hisVersion2Recovery == null || this.hisVersion2Recovery === '') {
                        this.$message.warning('没有恢复版本');
                    } else {
                        getAction(this.url.recovery, { routeId: ids[0], hisVersion: this.hisVersion2Recovery }).then(
                            res => {
                                if (res.success) {
                                    this.$message.success(res.message);
                                    this.loadData();
                                    this.visible = false; // 关闭弹窗
                                } else {
                                    this.$message.warning(res.message);
                                }
                            }
                        );
                    }
                }
            });
        },
        /* 恢复版本左边一级表头颜色 */
        customHeaderCellLefit(column) {
            /* if (column=="currentVersion"){
                 } */
            return {
                // 这个style就是我自定义的属性，也就是官方文档中的props
                style: {
                    // 行背景色
                    'background-color': '#cdff6d',
                    'border-right': '2px solid rgba(93,95,94,0.45)'
                }
            };
        },
        /* 恢复版本左边二级级表头颜色 */
        customHeaderCellTwoLeft(column) {
            /* if (column=="currentVersion"){
                 } */
            return {
                // 这个style就是我自定义的属性，也就是官方文档中的props
                style: {
                    // 行背景色
                    'border-right': '2px solid rgba(93,95,94,0.45)'
                }
            };
        },
        /* 恢复版本右边一级表头颜色 */
        customHeaderCellRight(column) {
            /* if (column=="currentVersion"){
                 } */
            return {
                // 这个style就是我自定义的属性，也就是官方文档中的props
                style: {
                    // 行背景色
                    'border-left': '2px solid rgba(93,95,94,0.45)'
                }
            };
        },
        /* 恢复版本右边二级表头颜色 */
        customHeaderCellTwoRight(column) {
            /* if (column=="currentVersion"){
                 } */
            return {
                // 这个style就是我自定义的属性，也就是官方文档中的props
                style: {
                    // 行背景色
                    'border-left': '2px solid rgba(93,95,94,0.45)'
                }
            };
        },
        clearChoice() {
            this.recoveryHisVersion = '';
            this.hisVersion2Recovery = '';
            this.hisVersion = '历史版本：';
            this.currentVersion = '当前版本：';
        },
        handleChange(value) {
            this.AA = value;
        }
    },
    created() {}
};
</script>
<style scoped lang="less">
@import '../../assets/less/common.less';
.j-table-force-nowrap-aa {
    td,
    th {
        white-space: nowrap;
    }
    .ant-table-selection-column {
        padding: 12px 22px !important;
    }

    /** 列自适应，弊端会导致列宽失效 */
    &.ant-table-wrapper .ant-table-content {
        overflow-x: auto;
    }
}
.table-page-search-wrapper .ant-form-inline .ant-form-item {
    display: inline-block !important;
    margin-right: 20px;
}
</style>
