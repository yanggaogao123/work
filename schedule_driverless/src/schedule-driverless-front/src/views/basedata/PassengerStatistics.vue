<template>
    <!-- 客流统计分析页面 -->
    <a-card :bordered="false">
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="6" :lg="6" :md="6" :sm="24">
                        <a-form-item label="统计方式">
                            <a-radio-group v-model="searchType" @change="searchTypeChange">
                                <a-radio v-for="(item, index) in searchOptions" :key="index" :value="item.value"
                                    >{{ item.label }}
                                </a-radio>
                            </a-radio-group>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="8" :lg="8" :md="8" :sm="24" v-show="searchType === 'sum' ? true : false">
                        <a-form-item label="汇总方式">
                            <a-radio-group v-model="sumType" @change="sumTypeChange">
                                <a-radio v-for="(item, index) in sumOptions" :key="index" :value="item.value"
                                    >{{ item.label }}
                                </a-radio>
                            </a-radio-group>
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row :gutter="24">
                    <a-col :xl="5" :lg="4" :md="4" :sm="24">
                        <a-form-item label="所属线路">
                            <GCIRouteSelect
                                placeholder="请选择线路"
                                v-model="queryParam.routeId"
                                @change="changeRoute"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="7" :lg="4" :md="4" :sm="24" v-show="sumType === 'station' ? false : true">
                        <a-form-item label="车辆">
                            <GCIBusSelect :routeId="queryParam.routeId" v-model="queryParam.busId" item="busId" />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="11" :lg="7" :md="7" :sm="24">
                        <a-form-item label="统计时间">
                            <a-range-picker
                                show-time
                                :value="timeSegs"
                                @change="onChangeRangeDate"
                                format="YYYY-MM-DD HH:mm:ss"
                                :placeholder="['开始时间', '结束时间']"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="5" :lg="5" :md="5" :sm="24" v-show="sumType === 'bus' ? false : true">
                        <a-form-item label="站点名称">
                            <a-select
                                v-model="stationStr"
                                allowClear
                                show-search
                                placeholder=""
                                option-filter-prop="children"
                                @change="changeStation"
                            >
                                <a-select-option
                                    v-for="item in stationList"
                                    v-if="item.direction == '0'"
                                    :key="item.routeStaId"
                                    :value="item.stationId + '-' + item.routeStaId"
                                >
                                    ▲ {{ item.stationName }}
                                </a-select-option>
                                <a-select-option
                                    v-for="item in stationList"
                                    v-if="item.direction == '1'"
                                    :key="item.routeStaId"
                                    :value="item.stationId + '-' + item.routeStaId"
                                >
                                    ▼ {{ item.stationName }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="5" :lg="4" :md="4" :sm="24" v-show="searchType === 'sum' ? false : true">
                        <a-form-item label="客流采集设备类型">
                            <j-dict-select-tag
                                v-model="queryParam.apparatusType"
                                placeholder=""
                                dictCode="apparatus_type"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="8" :lg="5" :md="5" :sm="24">
                        <a-button type="primary" @click="searchQuery2" icon="search" style="margin-bottom: 10px;"
                            >查询</a-button
                        >
                        <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px"
                            >重置</a-button
                        >
                        <a-button type="primary" @click="exportExcel" style="margin-left: 10px" :loading="excelLoading"
                            >导出
                        </a-button>
                    </a-col>
                </a-row>
            </a-form>
        </div>

        <!-- table区域-begin -->
        <a-table
            ref="table"
            size="middle"
            bordered
            :columns="showColumns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            class="j-table-force-nowrap"
            @change="handleTableChange"
        >
        </a-table>
        <!-- table区域-end -->
    </a-card>
</template>

<script>
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { getAction, postAction, downloadFileAwait } from '@/api/manage';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import GCIBusSelect from '@/components/gci/GCIBusSelect';
import moment from 'moment';

export default {
    name: 'PassengerStatistics',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIRouteSelect,
        GCIBusSelect
    },
    data() {
        return {
            excelLoading: false,
            dateFormat: 'YYYY-MM-DD HH:mm:ss',
            searchType: 'detail',
            sumType: 'station',
            searchOptions: [
                {
                    label: '明细',
                    value: 'detail'
                },
                {
                    label: '汇总',
                    value: 'sum'
                }
            ],
            sumOptions: [
                {
                    label: '按站点汇总',
                    value: 'station'
                },
                {
                    label: '按车辆汇总',
                    value: 'bus'
                },
                {
                    label: '按车辆/站点汇总',
                    value: 'busStation'
                }
            ],
            // 表头
            showColumns: [],
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
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '站序',
                    align: 'center',
                    dataIndex: 'orderNumber'
                },
                {
                    title: '站点编码',
                    align: 'center',
                    dataIndex: 'busstopCode'
                },
                {
                    title: '站点名称',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                {
                    title: '统计时间',
                    align: 'center',
                    dataIndex: 'obuTime'
                },
                {
                    title: '运营日期',
                    align: 'center',
                    dataIndex: 'runDate'
                },
                {
                    title: '上车人数',
                    align: 'center',
                    dataIndex: 'upCount'
                },
                {
                    title: '下车人数',
                    align: 'center',
                    dataIndex: 'downCount'
                },
                {
                    title: '班次',
                    align: 'center',
                    dataIndex: 'runningNo'
                },
                {
                    title: '客流采集设备类型',
                    align: 'center',
                    dataIndex: 'apparatusTypeDesc'
                }
            ],
            stationColumns: [
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
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '站点编码',
                    align: 'center',
                    dataIndex: 'busstopCode'
                },
                {
                    title: '站点名称',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                {
                    title: '上车人数',
                    align: 'center',
                    dataIndex: 'upCount'
                }
            ],
            busColumns: [
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
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '上车人数',
                    align: 'center',
                    dataIndex: 'upCount'
                }
            ],
            busStationColumns: [
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
                    title: '所属线路',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '站点编码',
                    align: 'center',
                    dataIndex: 'busstopCode'
                },
                {
                    title: '站点名称',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                {
                    title: '上车人数',
                    align: 'center',
                    dataIndex: 'upCount'
                }
            ],
            stationStr: '',
            stationList: [],
            queryParam: {
                startTimeQuery: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endTimeQuery: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59')
            },
            timeSegs: [moment(this.getCurrentData(), this.dateFormat), moment(this.getCurrentData1(), this.dateFormat)],
            url: {
                list: '/flow/passengerStatistics/list',
                export: '/flow/passengerStatistics/exportXls',
                stationUrl: '/baseservice/bsRoute/getRouteStationByRouteId'
            },
            disableMixinCreated: true
        };
    },
    methods: {
        filterOption(value, option) {
            return option.componentOptions.children[0].text.indexOf(value) >= 0;
        },
        searchQuery2() {
            this.isorter = {};
            this.searchQuery();
        },
        searchReset() {
            this.queryParam = {
                startTimeQuery: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endTimeQuery: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59')
            };
            this.timeSegs = [
                moment(this.getCurrentData(), this.dateFormat),
                moment(this.getCurrentData1(), this.dateFormat)
            ];
            this.stationStr = '';
            this.loadData(1);
        },
        changeRoute(value) {
            this.queryParam.routeId = value;
            let params = {
                routeId: value
            };
            this.stationList = [];
            this.stationStr = '';
            getAction(this.url.stationUrl, params).then(res => {
                if (res.success) {
                    this.stationList = res.result;
                }
                if (res.code === 510) {
                    this.$message.warning(res.message);
                }
            });
        },
        searchTypeChange() {
            if (this.searchType === 'sum') {
                this.queryParam.searchType = 'sum';
                if (this.queryParam.sumType) {
                    this.sumType = this.queryParam.sumType;
                } else {
                    this.sumType = 'station';
                    this.queryParam.sumType = 'station';
                }
            } else {
                this.queryParam.searchType = 'detail';
                this.sumType = '';
                this.queryParam.sumType = '';
            }
        },
        sumTypeChange() {
            this.queryParam.sumType = this.sumType;
        },
        changeStation(value) {
            console.info('station.val=' + value);
            this.queryParam.stationId = '';
            if (value) {
                this.queryParam.stationId = value.split('-')[0];
            }
        },
        onChangeRangeDate(value, dateString) {
            this.timeSegs = value;
            this.queryParam.startTimeQuery = dateString[0];
            this.queryParam.endTimeQuery = dateString[1];
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
            if (this.searchType === 'detail') {
                this.showColumns = this.columns;
                this.queryParam.searchType = 'detail';
            } else if (this.searchType === 'sum') {
                this.queryParam.searchType = 'sum';
                if (this.sumType === 'station') {
                    this.showColumns = this.stationColumns;
                    this.queryParam.sumType = 'station';
                } else if (this.sumType === 'bus') {
                    this.showColumns = this.busColumns;
                    this.queryParam.sumType = 'bus';
                } else if (this.sumType === 'busStation') {
                    this.showColumns = this.busStationColumns;
                    this.queryParam.sumType = 'busStation';
                }
            }
            let params = this.getQueryParams(); // 查询条件
            if (params.routeId == null || params.routeId == '') {
                this.$message.warning('请选择线路');
                return;
            }
            if (params.startTimeQuery == null || params.endTimeQuery == null) {
                this.$message.warning('请选择时间段');
                return;
            }
            if (new Date(params.endTimeQuery).getTime() - new Date(params.startTimeQuery).getTime() > 86400000 * 31) {
                this.$message.warning('时间段不能超过31天');
                return;
            }
            this.loading = true;
            postAction(this.url.list, params)
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
        async exportExcel() {
            if (this.searchType === 'detail') {
                this.queryParam.searchType = 'detail';
            } else if (this.searchType === 'sum') {
                this.queryParam.searchType = 'sum';
                this.queryParam.sumType = this.sumType;
            }
            let params = this.getQueryParams(); // 查询条件
            if (params.routeId == null || params.routeId == '') {
                this.$message.warning('请选择线路');
                return;
            }
            if (params.startTimeQuery == null || params.endTimeQuery == null) {
                this.$message.warning('请选择时间段');
                return;
            }
            if (new Date(params.endTimeQuery).getTime() - new Date(params.startTimeQuery).getTime() > 86400000 * 31) {
                this.$message.warning('时间段不能超过31天');
                return;
            }
            this.excelLoading = true;
            var fileName = '客流统计分析.xlsx';
            if (this.sumType === 'station') {
                fileName = '客流统计分析汇总(站点).xlsx';
            } else if (this.sumType === 'bus') {
                fileName = '客流统计分析汇总(车辆).xlsx';
            } else if (this.sumType === 'busStation') {
                fileName = '客流统计分析汇总(车辆站点).xlsx';
            }
            await downloadFileAwait(this.url.export, fileName, this.queryParam);
            this.excelLoading = false;
        },
        getCurrentData() {
            return moment()
                .subtract(1, 'days')
                .format('YYYY-MM-DD 00:00:00')
                .toString();
        },
        getCurrentData1() {
            return moment()
                .subtract(1, 'days')
                .format('YYYY-MM-DD 23:59:59')
                .toString();
        }
    }
};
</script>

<style scoped>
@import '~@assets/less/common.less';
</style>
