<template>
    <!-- 路单查询页面 -->
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xxl="6" :lg="8" :md="8" :sm="24">
                        <a-form-item label="线路">
                            <div style="width: 140px; display: inline-block">
                                <a-radio-group v-model="routeTypeSelect" :default-value="2" @change="changeRouteType">
                                    <a-radio :value="1">所属</a-radio>
                                    <a-radio :value="2">运营</a-radio>
                                </a-radio-group>
                            </div>
                            <div style="display: inline-block">
                                <!--
                <GCIRouteSelect placeholder="所属线路" item="routeId" v-model="queryParam.routeIdBelongTo" @change="changeRouteIdBelong" v-if="routeTypeSelect === 1" :width="routeWidth"/>
                <GCIRouteSelect placeholder="运营线路" item="routeId" v-model="queryParam.routeIdRun" @change="changeRouteIdRun" v-else :width="routeWidth" />
                -->
                                <GCIRouteSelect
                                    item="id"
                                    v-model="selectRouteId"
                                    @change="changeRouteSelect"
                                    :width="routeWidth"
                                />
                            </div>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="6" :lg="8" :md="8" :sm="12">
                        <a-form-item label="车辆">
                            <GCIBusSelect :routeId="routeId" v-model="queryParam.busId" item="busId" />
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="5" :lg="8" :md="4" :sm="24">
                        <a-form-item label="运营日期">
                            <!--
              <a-range-picker
                format="YYYY-MM-DD"
                :placeholder="['开始时间', '结束时间']"
                v-model="runDateRangeTime"
                @change="onrunDateChange"/>
                :default-value="moment(new Date())"
                -->
                            <a-date-picker
                                :allowClear="false"
                                placeholder="请选择日期"
                                class="datePicker"
                                format="YYYY-MM-DD"
                                @change="handleDateChange"
                                v-model="runDate"
                            >
                            </a-date-picker>
                        </a-form-item>
                    </a-col>
                    <template v-if="toggleSearchStatus">
                        <a-col :xxl="8" :lg="12" :md="12" :sm="24">
                            <a-form-item label="路单开始时间">
                                <!-- <a-range-picker
                                    :show-time="{ format: 'HH:mm:ss' }"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    :placeholder="['开始时间', '结束时间']"
                                    :disabledDate="disabledDate"
                                    v-model="RangeTime"
                                    @change="onChange"
                                /> -->
                                <a-date-picker
                                    v-model="beginTime"
                                    :disabled-date="disabledStartDate"
                                    :show-time="{ format: 'HH:mm:ss' }"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    placeholder="开始日期"
                                    @change="handleStartChange"
                                />
                                ~
                                <a-date-picker
                                    v-model="endTime"
                                    :disabled-date="disabledEndDate"
                                    :show-time="{ format: 'HH:mm:ss' }"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    placeholder="结束日期"
                                    @change="handleEndChange"
                                />
                            </a-form-item>
                        </a-col>
                        <a-col :xxl="4" :lg="6" :md="8" :sm="12">
                            <a-form-item label="方向">
                                <a-select :allowClear="true" placeholder="请选择状态" v-model="queryParam.direction">
                                    <a-select-option value="">全部</a-select-option>
                                    <a-select-option value="0">上行</a-select-option>
                                    <a-select-option value="1">下行</a-select-option>
                                    <a-select-option value="2">无</a-select-option>
                                </a-select>
                            </a-form-item>
                        </a-col>
                        <a-col :xxl="4" :lg="6" :md="8" :sm="12">
                            <a-form-item label="任务类型">
                                <a-select
                                    :allowClear="true"
                                    show-search
                                    option-filter-prop="children"
                                    :filter-option="filterOption"
                                    placeholder="请选择任务类型"
                                    v-model="queryParam.serviceType"
                                >
                                    <a-select-option
                                        v-for="item in busStatusOptions"
                                        :key="item.id"
                                        :value="item.value"
                                    >
                                        {{ item.type }}
                                    </a-select-option>
                                </a-select>
                            </a-form-item>
                        </a-col>
                        <a-col :xxl="4" :lg="6" :md="8" :sm="12">
                            <a-form-item label="记录状态">
                                <a-select
                                    :allowClear="true"
                                    placeholder="请选择记录状态"
                                    v-model="queryParam.recordStatus"
                                >
                                    <a-select-option value="0">开始记录</a-select-option>
                                    <a-select-option value="1">结束(自动报站)</a-select-option>
                                    <a-select-option value="2">结束(人工报站)</a-select-option>
                                    <a-select-option value="3">审核通过</a-select-option>
                                    <a-select-option value="4">未审核</a-select-option>
                                </a-select>
                            </a-form-item>
                        </a-col>
                    </template>
                    <a-col :xxl="6" :lg="6" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <a @click="handleToggleSearch" style="margin-left: 8px">
                                {{ toggleSearchStatus ? '收起' : '展开' }}
                                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
                            </a>
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin-bottom:10px;">
            <!-- <a-button type="primary" icon="download" @click="showExportXls">导出</a-button> -->
            <a-dropdown>
                <a-menu slot="overlay">
                    <a-menu-item @click="showExportTable"><a-icon type="download" />常规导出</a-menu-item>
                    <a-menu-item @click="showExportElcTable"><a-icon type="download" />电子路单导出</a-menu-item>
                </a-menu>
                <a-button type="primary" icon="download" style="margin-left: 8px">导出<a-icon type="down"/></a-button>
            </a-dropdown>
        </div>
        <!-- table区域-begin -->
        <div>
            <a-table
                size="middle"
                bordered
                :columns="columns"
                :loading="loading"
                :scroll="{ x: true }"
                :data-source="dataSource"
                :pagination="ipagination"
                @change="handleTableChange"
            >
                <template slot="action1" slot-scope="text, record">
                    <a @click="showBusTroughStationInf(record)">查看</a>
                </template>
                <template slot="action2" slot-scope="text, record">
                    <a @click="showBusTrackPlayback(record)">查看</a>
                </template>
                <template slot="action3" slot-scope="text, record">
                    <a @click="showHistory(record)">查看</a>
                </template>
            </a-table>
        </div>
        <bus-trough-station-inf-modal ref="busTroughStationInfModal"></bus-trough-station-inf-modal>
        <trip-log-history-modal ref="tripLogHistory"></trip-log-history-modal>
        <!-- 导出 弹窗 -->
        <a-modal
            :title="exportTitle"
            :width="exportWidth"
            :visible="exportVisible"
            :confirmLoading="confirmLoading"
            switchFullscreen
            cancelText="取消"
            okText="导出"
            @cancel="handleExportCancel"
            @ok="handleExportOkItem"
        >
            <a-spin :spinning="confirmLoading">
                <a-form :form="exportForm">
                    <a-form-item label="运营日期">
                        <a-date-picker
                            v-model="beginTimeExport"
                            :disabled-date="disabledExportBeginDate"
                            format="YYYY-MM-DD"
                            placeholder="开始日期"
                            @change="handleExportStartChange"
                        />
                        ~
                        <a-date-picker
                            v-model="endTimeExport"
                            format="YYYY-MM-DD"
                            placeholder="结束日期"
                            :disabled-date="disabledExportEndDate"
                            @change="handleExportEndChange"
                        />
                    </a-form-item>
                    <a-form-item>
                        <a-radio-group v-model="exportType" default-value="1">
                            <a-radio value="1">所属线路</a-radio>
                            <a-radio value="2">运营线路</a-radio>
                            <a-radio value="3">机构</a-radio>
                        </a-radio-group>
                    </a-form-item>
                    <a-form-item label="" v-show="exportType != '3'">
                        <GCIRouteSelectNew
                            v-model="exportRouteId"
                            placeholder="请选择线路"
                            mode="multiple"
                            :width="500"
                            item="id"
                            :send="sendSth"
                            @receive="receiveSth"
                            @receiveName="receiveName"
                        ></GCIRouteSelectNew>
                    </a-form-item>
                    <a-form-item label="" v-show="exportType == '3'">
                        <GCIOrgTreeSelect
                            v-model="exportOrgCode"
                            placeholder="请选择所属机构"
                            style="width: 70%"
                        ></GCIOrgTreeSelect>
                    </a-form-item>
                </a-form>
            </a-spin>
        </a-modal>
    </a-card>
</template>

<script>
import Vue from 'vue';
import { postAction, getAction } from '@api/manage';
import JDate from '@comp/jeecg/JDate.vue';
import moment from 'moment';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import busTroughStationInfModal from './modules/busTroughStationInfModal';
import tripLogHistoryModal from './modules/tripLogHistoryModal';
import GCIRouteSelect from '@comp/gci/GCIRouteSelect';
import GCIBusSelect from '@comp/gci/GCIHisBusSelect';

import { ACCESS_TOKEN, ENCRYPTED_STRING } from '@/store/mutation-types';
import { encryption, getEncryptedString } from '@/utils/encryption/aesEncrypt';
import GCIOrgTreeSelect from '../../components/gci/GCIOrgTreeSelect';
import GCIRouteSelectNew from '../../components/gci/GCIRouteSelectNew';
import { downloadFileByFileName } from '../../api/manage';
import pick from 'lodash.pick';

export default {
    name: 'tripLogSearch',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIOrgTreeSelect,
        JDate,
        busTroughStationInfModal,
        tripLogHistoryModal,
        GCIRouteSelect,
        GCIRouteSelectNew,
        GCIBusSelect
    },
    props: {},
    data() {
        return {
            moment,
            routeId: '',
            busStatusOptions: [],
            loading: false,
            RangeTime: [],
            beginTime: '',
            endTime: '',
            runDateRangeTime: [],
            runDate: undefined,
            routeTypeSelect: 2, //类型默认选择运营
            selectRouteId: '',
            exportVisible: false,
            exportOrgCode: undefined,
            confirmLoading: false,
            exportWidth: 600,
            exportType: '1',
            exportRouteId: '',
            beginTimeExport: '',
            beginDate: undefined,
            endTimeExport: '',
            endDate: undefined,
            sendSth: {},
            validatorRules: {
                routeIds: {
                    rules: [{ required: true, message: '请选择线路!' }]
                }
            },
            exportForm: this.$form.createForm(this),
            ipagination: {
                current: 1,
                pageSize: 10,
                pageSizeOptions: ['10', '20', '30', '50'],
                showTotal: (total, range) => {
                    return range[0] + '-' + range[1] + ' 共' + total + '条';
                },
                showQuickJumper: true,
                showSizeChanger: true,
                total: 0
            },
            columns: [
                {
                    title: '序号',
                    dataIndex: '',
                    key: 'rowIndex',
                    width: 80,
                    align: 'center',
                    customRender: function(t, r, index) {
                        return parseInt(index) + 1;
                    }
                },
                {
                    title: '所属线路',
                    // width: 80,
                    dataIndex: 'routeNameBelongTo',
                    align: 'center'
                },
                {
                    title: '车辆名称',
                    // width: 80,
                    dataIndex: 'busName',
                    align: 'center'
                },
                {
                    title: '资格证号',
                    dataIndex: 'qualification',
                    align: 'center',
                    scopedSlots: {
                        customRender: 'qualification'
                    }
                },
                {
                    title: '驾驶员',
                    // width: 60,
                    dataIndex: 'employeeName',
                    align: 'center'
                },
                {
                    title: '任务类型',
                    dataIndex: 'serviceName',
                    align: 'center'
                },
                {
                    title: '方向',
                    // width: 50,
                    dataIndex: 'direction',
                    align: 'center',
                    customRender: function(text) {
                        if (text == '0') {
                            return '上行';
                        } else if (text == '1') {
                            return '下行';
                        } else if (text == '2') {
                            return '无';
                        }
                    }
                },
                {
                    title: '首站',
                    dataIndex: 'fromStationName',
                    align: 'center'
                },
                {
                    title: '末站',
                    dataIndex: 'toStationName',
                    align: 'center'
                },
                {
                    title: '开始时间',
                    dataIndex: 'triplogBeginTime',
                    align: 'center'
                },
                {
                    title: 'GPS出站时间',
                    dataIndex: 'autoBeginTime',
                    align: 'center'
                },
                {
                    title: '结束时间',
                    dataIndex: 'triplogEndTime',
                    align: 'center'
                },
                {
                    title: '运营时长',
                    dataIndex: 'runDuration',
                    align: 'center'
                },
                {
                    title: '停站时长',
                    dataIndex: 'anchorDuration',
                    align: 'center'
                },
                {
                    title: '发车间隔',
                    dataIndex: 'departInterval',
                    align: 'center'
                },
                {
                    title: '里程(km)',
                    dataIndex: 'runMileage',
                    align: 'center'
                },
                {
                    title: '记录状态',
                    dataIndex: 'recordStatus',
                    align: 'center',
                    customRender: function(text) {
                        if (text == '0') {
                            return '开始记录';
                        } else if (text == '1') {
                            return '结束(自动报站)';
                        } else if (text == '2') {
                            return '结束(人工报站)';
                        } else if (text == '3') {
                            return '审核通过';
                        } else if (text == '4') {
                            return '未审核';
                        }
                    }
                },
                {
                    title: '备注',
                    dataIndex: 'remark',
                    align: 'center'
                },
                {
                    title: '变更信息',
                    dataIndex: 'changeInfo',
                    align: 'center'
                },
                {
                    title: '运营线路',
                    dataIndex: 'routeNameRun',
                    align: 'center'
                },
                {
                    title: '运营日期',
                    dataIndex: 'runDate',
                    align: 'center',
                    customRender: function(text) {
                        return !text ? '' : text.length > 10 ? text.substr(0, 10) : text;
                    }
                },
                {
                    title: '更新用户',
                    dataIndex: 'updateUser',
                    align: 'center'
                },
                {
                    title: '更新时间',
                    dataIndex: 'updateTime',
                    align: 'center'
                },
                {
                    title: '车辆编码',
                    dataIndex: 'busCode',
                    align: 'center'
                },
                {
                    title: '车辆牌照',
                    dataIndex: 'numberPlate',
                    align: 'center'
                },
                {
                    title: '所属机构',
                    dataIndex: 'organName',
                    align: 'center'
                },
                {
                    title: '支线路',
                    dataIndex: 'routeSubName',
                    align: 'center'
                },
                {
                    title: '进出站信息',
                    dataIndex: 'action1',
                    align: 'center',
                    fixed: 'right',
                    scopedSlots: {
                        customRender: 'action1'
                    }
                },
                {
                    title: '轨迹回放',
                    dataIndex: 'action2',
                    align: 'center',
                    fixed: 'right',
                    scopedSlots: {
                        customRender: 'action2'
                    }
                },
                {
                    title: '历史修改记录',
                    dataIndex: 'action3',
                    align: 'center',
                    fixed: 'right',
                    scopedSlots: {
                        customRender: 'action3'
                    }
                }
            ],
            exportTitle: '常规导出',
            routeWidth: '1200',
            dataSource: [],
            url: {
                list: '/triplog/dyTriplogSearch/list',
                serviceType: '/base/bsPreptripTypeAlias/getTaskTypeList',
                exportXlsUrl: '/triplog/dyTriplogSearch/exportOrgXls',
                exportElcXls: '/triplog/dyTriplogSearch/exportElcXls',
                exportListCount: '/triplog/dyTriplogSearch/exportListCount',
                appList: '/sys/sysApp/list',
                zydPlayback_gz: '/Macau/module/monitor/zydPlayback_para.jsp'
            },
            busTroughStationInfModelIsShow: false
        };
    },
    watch: {
        runDate() {
            console.log(this.runDate);
            if (!this.runDate) {
                let start = moment(new Date());
                this.queryParam.runDate = start.format('YYYY-MM-DD');
                this.runDate = start;
            }
        }
    },
    computed: {},
    created: function() {
        this.routeWidth = document.documentElement.clientWidth / 14;
    },
    mounted() {
        this.initDictConfig();
    },
    methods: {
        // 初始化数据字典
        initDictConfig() {
            this.getBusStatusList();
        },
        handleDateChange(mom, dateStr) {
            this.queryParam.runDate = dateStr;
        },
        loadData(arg) {
            /**
        if (this.queryParam.runDate_begin == null && this.queryParam.runDate_end == null){
          let start = moment(new Date())
          this.queryParam.runDate_begin =  start.format("YYYY-MM-DD");
          this.queryParam.runDate_end = start.format("YYYY-MM-DD");
          this.runDateRangeTime = [start,start]
        }*/
            /***/
            if (this.queryParam.runDate == null) {
                let start = moment(new Date());
                this.queryParam.runDate = start.format('YYYY-MM-DD');
                this.runDate = start;
            }

            //加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }
            let params = this.getQueryParams();
            if (!params.routeIdBelongTo && !params.routeIdRun && !params.busId) {
                this.$message.warning('线路或车辆不能为空！');
                return;
            }
            if (!params.runDate) {
                this.$message.warning('日期不能为空！');
                return;
            }
            this.loading = true;
            getAction(this.url.list, params)
                .then(res => {
                    if (res.success) {
                        this.dataSource = res.result.records;
                        this.ipagination.total = res.result.total;
                    }
                    if (res.code === 510) {
                        this.$message.warning(res.message);
                    }
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        getBusStatusList() {
            let sendData = {};
            getAction(this.url.serviceType).then(res => {
                console.log(res);
                if (res.success) {
                    this.busStatusOptions = res.result;
                } else {
                    this.$message.warning(res.message);
                }
            });
        },
        changeRouteType(event) {
            if (event.target.value === 1) {
                this.queryParam.routeIdBelongTo = this.selectRouteId;
                this.queryParam.routeIdRun = null;
            }
            if (event.target.value === 2) {
                this.queryParam.routeIdRun = this.selectRouteId;
                this.queryParam.routeIdBelongTo = null;
            }
        },
        changeRouteSelect() {
            if (this.routeTypeSelect === 1) {
                this.queryParam.routeIdBelongTo = this.selectRouteId;
                this.queryParam.routeIdRun = null;
            }
            if (this.routeTypeSelect === 2) {
                this.queryParam.routeIdRun = this.selectRouteId;
                this.queryParam.routeIdBelongTo = null;
            }
            this.routeId = this.selectRouteId;
        },
        onChange(value, dateString) {
            this.RangeTime = value;
            this.queryParam.triplogBeginTimeBegin = dateString[0];
            this.queryParam.triplogBeginTimeEnd = dateString[1];
        },
        // 日期
        disabledStartDate(startValue) {
            // console.log(startValue);
            // this.queryParam.triplogBeginTimeBegin = dateString;
        },
        disabledEndDate(endValue) {
            if (!this.queryParam.triplogBeginTimeBegin) {
                return true;
            } else {
                return !(
                    endValue >
                        moment(this.queryParam.triplogBeginTimeBegin)
                            .subtract(1, 'days')
                            .endOf('day') &&
                    endValue <
                        moment(this.queryParam.triplogBeginTimeBegin)
                            .subtract(-1, 'days')
                            .endOf('day')
                );
            }
        },
        // 日期
        disabledExportBeginDate(startValue) {
            // console.log(startValue);
            // this.queryParam.triplogBeginTimeBegin = dateString;
        },
        disabledExportEndDate(endValue) {
            if (!this.beginTimeExport) {
                return true;
            } else {
                return !(
                    endValue >
                        moment(this.beginTimeExport)
                            .subtract(1, 'days')
                            .endOf('day') &&
                    endValue <
                        moment(this.beginTimeExport)
                            .subtract(-1, 'months')
                            .endOf('day')
                );
            }
        },
        handleStartChange(date, dateString) {
            this.queryParam.triplogBeginTimeBegin = dateString;
            this.endTime = '';
            this.queryParam.triplogBeginTimeEnd = '';
        },
        handleEndChange(date, dateString) {
            this.queryParam.triplogBeginTimeEnd = dateString;
        },
        handleExportStartChange(date, dateString) {
            this.beginTimeExport = dateString;
            this.beginDate = date;
            this.endTimeExport = '';
        },
        handleExportEndChange(date, dateString) {
            this.endTimeExport = dateString;
            this.endDate = date;
        },
        /****************/

        onrunDateChange(value, dateString) {
            this.runDateRangeTime = value;
            this.queryParam.runDate_begin = dateString[0];
            this.queryParam.runDate_end = dateString[1];
        },
        // 线路类型修改
        routeTypeonChange(e) {
            console.log('类型', e.target.value);
        },
        showBusTroughStationInf(record) {
            console.log(record);
            // TODO: 临时关闭
            /**
        if (record.triplogEndTime == null){
          this.$message.warning('路单未结束')
          return
        }*/
            this.$refs.busTroughStationInfModal.visible = true;
            this.$refs.busTroughStationInfModal.initData(record);
        },
        showBusTrackPlayback(record) {
            if (record.triplogEndTime == null) {
                this.$message.warning('路单未结束');
                return;
            }
            let that = this;
            // 带参数到轨迹回放页面
            let token = Vue.ls.get(ACCESS_TOKEN);
            let menuId = this.$route.meta.id;
            let manageUrl = process.env.VUE_APP_DOMAIN_URL + process.env.VUE_APP_BASE_API;
            let time = new Date().getTime();
            let params = [manageUrl, token, menuId, time].join(';');
            this.getEncrypte();
            let paramString = encryption(params, this.encryptedString.key, this.encryptedString.iv);
            let urlParam =
                '?begDate=' +
                record.triplogBeginTime.substr(0, 10) +
                '&begTime=' +
                record.triplogBeginTime.substr(11, 5) +
                '&endTime=' +
                record.triplogEndTime.substr(11, 5) +
                '&buscode=' +
                record.busCode;
            getAction(this.url.appList, { name: 'Macau' }).then(res => {
                if (res.success && res.result.records.length == 1) {
                    let re = res.result.records[0];
                    let url =
                        re.protocol +
                        '://' +
                        re.address +
                        that.url.zydPlayback_gz +
                        urlParam +
                        '&paramString=' +
                        paramString +
                        '&language=zh_CN';
                    window.open(url);
                } else {
                    this.$message.warning('轨迹回放跳转失败');
                }
            });
            // window.open("http://localhost:8080"+this.url.zydPlayback_gz+urlParam+"&paramString="+paramString)
        },
        showHistory(record) {
            this.$refs.tripLogHistory.visible = true;
            this.$refs.tripLogHistory.initData(record);
            // TODO: 模拟数据
            // this.$refs.tripLogHistory.initData(858968205)
        },
        searchReset() {
            this.queryParam = [];
            this.runDateRangeTime = [];
            this.RangeTime = [];
            this.routeId = '';
            this.selectRouteId = undefined;
            // this.loadData()
        },
        receiveSth(data) {
            console.log(data);
            this.exportRouteId = data.toString();

            // this.form.setFieldsValue({ routeName: '123路' }, { routeName: '123路' });
        },
        receiveName(data) {
            console.log(data);

            // this.form.setFieldsValue({ routeName: '123路' }, { routeName: '123路' });
        },
        changeExportType() {},
        showExportTable(){  //常规导出
        this.exportTitle = '常规导出';
        this.showExportXls();
        },
        showExportElcTable(){  //电子路单导出
            this.exportTitle = '电子路单导出';
            this.showExportXls();
        },
        showExportXls() {
            this.exportVisible = true;
            this.exportForm.resetFields();
            this.model = Object.assign({}, { routeIds: undefined });
            this.$nextTick(() => {
                this.exportForm.setFieldsValue(pick(this.model, 'routeIds'));
            });
        },
        handleExportCancel() {
            this.exportVisible = false;
        },
        handleExportOkItem() {
            if(this.exportTitle == '电子路单导出'){
            this.handleExportXls2('电子路单表.xls');
            }else{
                this.handleExportXls2('路单表.xls');
            }
        },
        handleExportXls2(fileName) {
            // if (this.queryParam.runDate == null) {
            //     let start = moment(new Date());
            //     this.queryParam.runDate = start.format('YYYY-MM-DD');
            //     this.runDate = start;
            // }
            //
            // let params = this.getQueryParams();
            // if (!params.routeIdBelongTo && !params.routeIdRun && !params.busId) {
            //     this.$message.warning('线路或车辆不能为空！');
            //     return;
            // }
            // this.handleExportXls(fileName);
          if(!this.beginTimeExport){
            this.$message.warning('请选择开始日期')
            return
          }
          if(!this.endTimeExport){
            this.$message.warning('请选择结束日期')
            return
          }
          if(this.exportType === '1' || this.exportType === '2'){
            if(!this.exportRouteId) {
              this.$message.warning('请先选择线路')
              return
            }
          }else {
            if(!this.exportOrgCode){
              this.$message.warning('请先选择机构')
              return
            }
          }
          let param = {
            exportType:this.exportType,
            exportRouteIds:this.exportRouteId,
            exportOrgCode: this.exportOrgCode,
            beginTimeExport:this.beginTimeExport,
            endTimeExport:this.endTimeExport
          };
          //请求导出行数
          getAction(this.url.exportListCount,param).then(res => {
                console.log(res);
                if (res.success) {
                    if(this.exportTitle == '电子路单导出'){
                        downloadFileByFileName(this.url.exportElcXls,fileName,param);
                    }else{
                        downloadFileByFileName(this.url.exportXlsUrl,fileName,param);
                    }
                    this.exportTitle = '常规导出';
                    //导出
                    this.handleExportCancel();
                } else {
                    //提示过大
                    this.$message.warning(res.message);
                }
            });
        },
        //获取密码加密规则
        getEncrypte() {
            var encryptedString = Vue.ls.get(ENCRYPTED_STRING);
            if (encryptedString == null) {
                getEncryptedString().then(data => {
                    this.encryptedString = data;
                });
            } else {
                this.encryptedString = encryptedString;
            }
        },
        filterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        disabledDate(current) {
            return current > moment().add(1, 'days');
        }
    }
};
</script>

<style lang="less" scoped>
.ant-table td {
    white-space: nowrap;
}
/deep/.datePicker .ant-calendar-picker-clear {
    display: none !important;
}
</style>
