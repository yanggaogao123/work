<template>
    <!-- 车辆进出站监控 -->
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery" :form="form">
                <a-row :gutter="24">
                    <a-col :xxl="5" :xl="5" :lg="4" :md="4" :sm="24">
                        <a-form-item label="运营线路">
                            <GCIRouteSelect
                                v-decorator="['routeId', validatorRules.routeId]"
                                placeholder="请选择线路"
                                mode="mode"
                                @change="onChangeRoute"
                            ></GCIRouteSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="5" :xl="7" :lg="4" :md="4" :sm="24">
                        <a-form-item label="站台">
                            <a-select
                                allowClear
                                show-search
                                v-model="stationStr"
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
                    <a-col :xxl="5" :xl="7" :lg="4" :md="4" :sm="24">
                        <a-form-item label="车辆">
                            <GCIBusSelect :routeId="queryParam.routeId" v-model="queryParam.obuid" />
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="8" :xl="11" :lg="7" :md="7" :sm="24">
                        <a-form-item label="进出站时间">
                            <a-range-picker
                                show-time
                                :value="timeSegs"
                                @change="onChangeRangeDate"
                                format="YYYY-MM-DD HH:mm:ss"
                                :placeholder="['开始时间', '结束时间']"
                                v-decorator="['timeSegs', validatorRules.timeSegs]"
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
                                :loading="excelLoading"
                                type="primary"
                                icon="download"
                                @click="handleExportXls1('车辆进出站监控')"
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
        <a-table
            ref="table"
            size="middle"
            bordered
            :columns="columns"
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
import '@/assets/less/TableExpand.less';
import { mixinDevice } from '@/utils/mixin';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { getAction, postAction, downloadFileAwait } from '@/api/manage';
import GCIBusSelect from '@/components/gci/GCIHisBusSelect';
import moment from 'moment';
import GCIRouteSelect from '../../components/gci/GCIRouteSelect';

export default {
    name: 'DyAdrealList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIBusSelect,
        GCIRouteSelect
    },
    data() {
        return {
          disableMixinCreated:true,
            routeId: '',
            description: 'dy_adreal管理页面',
            searchTimeSt: '',
            searchTimeEnd: '',
            selectedBus: {},
            numberPlate: undefined,
            stationList: [],
            stationStr: '',
            /* 排序参数 */
            isorter: {
                column: 'adTime',
                order: 'asc'
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
                    dataIndex: 'organName'
                },
                {
                    title: '运营支线路',
                    align: 'center',
                    dataIndex: 'routeSubName'
                },
                {
                    title: '站点编码',
                    align: 'center',
                    dataIndex: 'busStopCode'
                },
                {
                    title: '站点名称',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                {
                    title: '支线路站台序号',
                    align: 'center',
                    dataIndex: 'orderNumber'
                },
                {
                    title: '进站标志',
                    align: 'center',
                    dataIndex: 'adFlag'
                },
                {
                    title: '进出站时间',
                    align: 'center',
                    dataIndex: 'adTime',
                    sorter: true,
                    customRender: function(text) {
                        return !text ? '' : text.length > 10 ? text.substr(0, 19) : text;
                    }
                },
                {
                    title: '入库时间',
                    align: 'center',
                    dataIndex: 'inTime',
                    customRender: function(text) {
                        return !text ? '' : text.length > 10 ? text.substr(0, 19) : text;
                    }
                },
                {
                    title: '终端编号',
                    align: 'center',
                    dataIndex: 'obuid'
                },
                {
                    title: '报站类型',
                    align: 'center',
                    dataIndex: 'reportType',
                    customRender: function(text, record) {
                        return record.reportType_dictText;
                    }
                },
                {
                    title: '服务编码',
                    align: 'center',
                    dataIndex: 'serviceNumber'
                },
                {
                    title: '趟次号',
                    align: 'center',
                    dataIndex: 'runningboard'
                },
                {
                    title: '车辆编码',
                    align: 'center',
                    dataIndex: 'busCode'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '运营线路编码',
                    align: 'center',
                    dataIndex: 'routeCode'
                },
                {
                    title: '运营线路',
                    align: 'center',
                    dataIndex: 'routeName'
                }
            ],
            url: {
                list: '/monitor/dyAdreal/list',
                exportXlsUrl: '/monitor/dyAdreal/exportXls',
                stationUrl: '/baseservice/bsRoute/getRouteStationByRouteId'
            },
            dictOptions: {},
            dateFormat: 'YYYY-MM-DD HH:mm:ss',
            queryParam: {
                beginTime: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endTime: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59')
            },
            timeSegs: [moment(this.getCurrentData(), this.dateFormat), moment(this.getCurrentData1(), this.dateFormat)],
            excelLoading: false,
            form: this.$form.createForm(this),
            validatorRules: {
                routeId: {
                    rules: [{ required: true, message: '请选择线路' }]
                },
                timeSegs: {
                    rules: [{ required: true, message: '请选择进出站时间' }, { validator: this.checkTimeSeg }]
                }
            }
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    mounted() {
        this.searchReset();
    },
    methods: {
        loadData(arg) {
            if (!this.url.list) {
                this.$message.error('请设置url.list属性!');
                return;
            }
            // 加载数据 若传入参数1则加载第一页的内容
            if (arg === 1) {
                this.ipagination.current = 1;
            }

            let params = this.getQueryParams(); // 查询条件
            if (!params.routeId) {
              this.$message.warning("请先选择线路")
                return;
            }
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
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
                }
            });
        },
        searchReset() {
            this.queryParam = {
                beginTime: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endTime: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59')
            };
            (this.timeSegs = [
                moment(this.getCurrentData(), this.dateFormat),
                moment(this.getCurrentData1(), this.dateFormat)
            ]),
                (this.stationStr = '');
            this.form.resetFields();
            this.form.setFieldsValue({ timeSegs: this.timeSegs });
        },
        handleExportXls1() {
            let that = this;
            const params = this.getQueryParams(); // 查询条件
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
                    that.excelLoading = true;
                    downloadFileAwait(this.url.exportXlsUrl, '车辆进出站监控.xls', params);
                    that.excelLoading = false;
                }
            });
        },
        onChangeRoute(value) {
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
        onChangeRangeDate(value, dateString) {
            this.timeSegs = value;
            this.queryParam.beginTime = dateString[0];
            this.queryParam.endTime = dateString[1];
        },
        changeStation(value) {
            console.info('station.val=' + value);
            this.queryParam.stationId = '';
            if (value) {
                this.queryParam.stationId = value.split('-')[0];
            }
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
        },
        checkTimeSeg(rule, value, callback) {
            if (value == '' || value == undefined || value == null) {
                callback();
            } else {
                var start = this.queryParam.beginTime;
                var end = this.queryParam.endTime;
                if (new Date(end).getTime() - new Date(start).getTime() > 86400000) {
                    callback('时间间隔不能超过24小时');
                } else {
                    callback();
                }
            }
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
