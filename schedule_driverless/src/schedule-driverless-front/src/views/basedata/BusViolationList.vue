<template>
    <a-card :bordered="false">
        <!-- 车辆违规信息页面 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery" :form="form">
                <a-row :gutter="24">
                    <a-col :xxl="5" :xl="6" :lg="4" :md="4" :sm="24">
                        <a-form-item label="线路名称">
                            <GCIRouteSelect
                                item="id"
                                v-decorator="['routeId', validatorRules.routeId]"
                                @change="onChangeRoute"
                            ></GCIRouteSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="5" :xl="6" :lg="4" :md="4" :sm="24">
                        <a-form-item label="车辆">
                            <GCIBusSelect :routeId="queryParam.routeId" v-model="queryParam.busId" item="busId" />
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="8" :xl="12" :lg="7" :md="7" :sm="24">
                        <a-form-item label="发生时间">
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
                    <a-col :xxl="5" :xl="6" :lg="4" :md="4" :sm="24">
                        <a-form-item label="类型">
                            <j-dict-select-tag v-model="queryParam.alarmType" placeholder="" dictCode="alarm_type" />
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="8" :xl="8" :lg="5" :md="5" :sm="24">
                        <a-button type="primary" @click="searchQuery2" icon="search" style="margin-bottom: 10px;"
                            >查询</a-button
                        >
                        <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 10px"
                            >重置</a-button
                        >
                        <a-button
                            type="primary"
                            @click="exportExcel"
                            icon="download"
                            style="margin-left: 10px"
                            :loading="excelLoading"
                            >导出</a-button
                        >
                    </a-col>
                </a-row>
            </a-form>
        </div>

        <!-- table区域-begin -->
        <div>
            <!--
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>
      -->

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
        </div>
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
    name: 'BusViolationList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        GCIRouteSelect,
        GCIBusSelect
    },
    data() {
        return {
            excelLoading: false,
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
                    title: '线路名称',
                    align: 'center',
                    dataIndex: 'routeNameRun'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '站台名称',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                {
                    title: '方向',
                    align: 'center',
                    dataIndex: 'direction'
                },
                {
                    title: '速度',
                    align: 'center',
                    dataIndex: 'speed'
                },
                {
                    title: '违规位置',
                    align: 'center',
                    dataIndex: '',
                    customRender: function(t, r, index) {
                        return '(' + r.latitude + ' , ' + r.longtude + ')';
                    }
                },
                {
                    title: '发生时间',
                    align: 'center',
                    dataIndex: 'occurTime'
                },
                {
                    title: '班次',
                    align: 'center',
                    dataIndex: 'tripCode'
                },
                {
                    title: '告警类型',
                    align: 'center',
                    dataIndex: 'alarmTypeName'
                }
            ],
            dateFormat: 'YYYY-MM-DD HH:mm:ss',
            queryParam: {
                startTimeQuery: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endTimeQuery: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59'),
                alarmType: 1
            },
            timeSegs: [moment(this.getCurrentData(), this.dateFormat), moment(this.getCurrentData1(), this.dateFormat)],
            url: {
                list: '/base/busViolation/list',
                export: '/base/busViolation/exportXls'
            },
            form: this.$form.createForm(this),
            validatorRules: {
                routeId: {
                    rules: [{ required: true, message: '请选择线路' }]
                },
                timeSegs: {
                    rules: [{ required: true, message: '请选择发生时间' }, { validator: this.checkTimeSeg }]
                }
            }
        };
    },
    mounted() {
        this.searchReset();
    },
    methods: {
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
                    .format('YYYY-MM-DD 23:59:59'),
                alarmType: 1
            };
            this.timeSegs = [
                moment(this.getCurrentData(), this.dateFormat),
                moment(this.getCurrentData1(), this.dateFormat)
            ];
            this.form.resetFields();
            this.form.setFieldsValue({ timeSegs: this.timeSegs });
            this.loadData(1);
        },
        onChangeRoute(value) {
            this.queryParam.routeId = value;
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

            let params = this.getQueryParams(); // 查询条件
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
        async exportExcel() {
            let that = this;
            const params = this.getQueryParams(); // 查询条件
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
                    that.excelLoading = true;
                    downloadFileAwait(this.url.export, '车辆违规信息.xls', params);
                    that.excelLoading = false;
                }
            });
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
                var start = this.queryParam.startValue;
                var end = this.queryParam.endValue;
                if (new Date(end).getTime() - new Date(start).getTime() >= 31 * 86400000) {
                    callback('时间间隔不能超过31天');
                } else {
                    callback();
                }
            }
        }
    }
};
</script>

<style scoped>
@import '~@assets/less/common.less';
</style>
