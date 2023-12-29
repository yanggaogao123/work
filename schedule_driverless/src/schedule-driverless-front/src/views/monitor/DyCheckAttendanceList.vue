<template>
    <!-- 考勤管理 -->
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xxl="3" :xl="4" :lg="4" :md="6" :sm="24">
                        <a-form-item label="所属线路">
                            <GCIRouteSelect
                                placeholder="请选择所属线路"
                                v-model="queryParam.routeId"
                                @change="changeRoute"
                            ></GCIRouteSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="5" :xl="6" :lg="4" :md="6" :sm="24">
                        <a-form-item label="车辆">
                            <GCIBusSelect :routeId="routeId" v-model="queryParam.busId" item="busId" />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="4" :md="6" :sm="24">
                        <a-form-item label="员工姓名">
                            <a-input placeholder="请输入司机名称" v-model="queryParam.employeeName"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="4" :md="6" :sm="24">
                        <a-form-item label="考勤类型">
                            <j-dict-select-tag
                                v-model="queryParam.operateType"
                                placeholder="请选择考勤类型"
                                dictCode="checkin_type"
                            />
                        </a-form-item>
                    </a-col>

                    <a-col :xl="6" :lg="12" :md="12" :sm="24">
                        <a-form-item label="考勤时间">
                            <a-range-picker
                                format="YYYY-MM-DD"
                                :placeholder="['开始时间', '结束时间']"
                                v-model="RangeTime"
                                :allow-clear="allowClear"
                                @change="onChange"
                            />
                        </a-form-item>
                    </a-col>

                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <a-button
                                type="primary"
                                icon="download"
                                @click="handleExportXls1('考勤查询')"
                                style="margin-left: 8px"
                                >导出</a-button
                            >
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- table区域-begin -->
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
            </a-table>
        </div>
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import JDate from '@/components/jeecg/JDate.vue';
import { postAction } from '@/api/manage';
import GCIBusSelect from '@/components/gci/GCIBusSelect';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import moment from 'moment';

export default {
    name: 'DyCheckAttendanceList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        JDate,
        GCIBusSelect,
        GCIRouteSelect
    },
    data() {
        return {
            description: 'dy_check_attendance管理页面',
            allowClear: false,
            searchTimeSt: '',
            searchTimeEnd: '',
            /* 排序参数 */
            isorter: {
                column: 'operateTime',
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
                    title: '所属机构',
                    align: 'center',
                    dataIndex: 'organId',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.organId_dictText;
                    }
                },
                {
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName',
                    sorter: true
                },
                {
                    title: '车辆名称',
                    align: 'center',
                    dataIndex: 'busName',
                    sorter: true
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate',
                    sorter: true
                },
                {
                    title: '员工姓名',
                    align: 'center',
                    dataIndex: 'employeeName',
                    sorter: true
                },
                {
                    title: '资格证号码',
                    align: 'center',
                    dataIndex: 'qualification',
                    sorter: true
                },
                {
                    title: '考勤时间',
                    align: 'center',
                    dataIndex: 'operateTime',
                    customRender: function(text) {
                        return !text ? '' : text.length > 10 ? text.substr(0, 19) : text;
                    },
                    sorter: true
                },
                {
                    title: '考勤地点',
                    align: 'center',
                    dataIndex: 'operatePlace',
                    sorter: true
                },
                {
                    title: '考勤类型',
                    align: 'center',
                    dataIndex: 'operateType',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.operateType_dictText;
                    }
                },
                {
                    title: '录入方式',
                    align: 'center',
                    dataIndex: 'inputType',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.inputType_dictText;
                    }
                },
                {
                    title: '录入人员',
                    align: 'center',
                    dataIndex: 'operatorCode',
                    sorter: true
                },
                {
                    title: '考勤状态',
                    align: 'center',
                    dataIndex: 'checkStatus',
                    sorter: true,
                    customRender: function(text, record) {
                        return record.checkStatus_dictText;
                    }
                }
                // {
                //   title:'流水号',
                //   align:"center",
                //   dataIndex: 'dataSerial'
                // },
                // {
                //   title:'经度',
                //   align:"center",
                //   dataIndex: 'longitude'
                // },
                // {
                //   title:'纬度',
                //   align:"center",
                //   dataIndex: 'latitude'
                // },
                // {
                //   title:'司机机构编号',
                //   align:"center",
                //   dataIndex: 'organId'
                // },
                // {
                //   title:'考勤线路站台ID',
                //   align:"center",
                //   dataIndex: 'stationId'
                // },
                // {
                //   title:'考勤线路站台名称',
                //   align:"center",
                //   dataIndex: 'stationName'
                // }
            ],
            url: {
                list: '/monitor/dyCheckAttendance/list',
                delete: '/monitor/dyCheckAttendance/delete',
                deleteBatch: '/monitor/dyCheckAttendance/deleteBatch',
                exportXlsUrl: '/monitor/dyCheckAttendance/exportXls',
                importExcelUrl: 'monitor/dyCheckAttendance/importExcel'
            },
            dictOptions: {},
            RangeTime: [],
            routeId: ''
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        handleExportXls1() {
            if (this.searchTimeSt === null || this.searchTimeSt === '') {
                this.$message.warning('数据量过多，请选择进出站时间区间段后再操作!');
                return;
            }
            this.queryParam.searchTimeSt = this.searchTimeSt;
            this.queryParam.searchTimeEnd = this.searchTimeEnd;
            this.queryParam.beginTime = this.searchTimeSt + ' 00:00:00';
            this.queryParam.endTime = this.searchTimeEnd + ' 23:59:59';
            this.handleExportXls('考勤查询');
        },
        changeRoute(value) {
            this.routeId = value;
        },
        initDictConfig() {
            let start = moment(new Date());
            let end = moment(new Date());
            console.log(start.format('YYYY-MM-DD'));
            this.searchTimeSt = start.format('YYYY-MM-DD');
            this.searchTimeEnd = end.format('YYYY-MM-DD');
            this.RangeTime = [start, end];
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
            if (!this.searchTimeSt || !this.searchTimeEnd) {
                this.initDictConfig();
            }
            let params = this.getQueryParams(); // 查询条件
            params.beginTime = this.searchTimeSt + ' 00:00:00';
            params.endTime = this.searchTimeEnd + ' 23:59:59';
            this.loading = true;
            postAction(this.url.list, params).then(res => {
                if (res.success) {
                    this.dataSource = res.result.records;
                    this.ipagination.total = res.result.total;
                }
                if (res.code === 510) {
                    this.$message.warning(res.message);
                }
                this.loading = false;
            });
        },
        onChange(value, dateString) {
            console.log('Formatted Selected Time: ', dateString);
            this.RangeTime = value;
            this.searchTimeSt = dateString[0];
            this.searchTimeEnd = dateString[1];
        },
        searchReset() {
            this.queryParam = {};
            this.initDictConfig();
            this.loadData();
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
