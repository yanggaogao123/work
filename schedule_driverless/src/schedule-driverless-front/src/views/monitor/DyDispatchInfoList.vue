<template>
    <!-- 终端交互信息查询 -->
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xxl="5" :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="车辆">
                            <GCIBusSelect v-model="queryParam.busId" item="busId" />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="7" :md="8" :sm="24">
                        <a-form-item label="信息内容">
                            <a-input placeholder="请输入信息内容" v-model="queryParam.infoContent"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="4" :lg="6" :md="6" :sm="24">
                        <a-form-item label="事件类型">
                            <j-dict-select-tag
                                v-model="queryParam.infoType"
                                placeholder="请输入事件类型"
                                dictCode="dy_dispatch_info_type"
                                type="select"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :xl="10" :lg="12" :md="12" :sm="24">
                        <a-form-item label="终端交互时间">
                            <a-range-picker
                                :show-time="{ format: 'HH:mm:ss' }"
                                format="YYYY-MM-DD HH:mm:ss"
                                :placeholder="['开始时间', '结束时间']"
                                v-model="RangeTime"
                                @change="onChange"
                            />
                        </a-form-item>
                    </a-col>

                    <!--          <template v-if="toggleSearchStatus">-->

                    <!--          </template>-->
                    <a-col :xl="5" :lg="7" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <a-button type="primary" icon="download" @click="exportByDay" style="margin-left: 8px"
                                >按天导出</a-button
                            >
                            <a-button
                                type="primary"
                                icon="download"
                                @click="handleExportXls1('终端交互信息查询')"
                                style="margin-left: 8px"
                                >导出Excel</a-button
                            >
                            <a-button
                                type="primary"
                                icon="download"
                                @click="handleExportCsv('终端交互信息查询')"
                                style="margin-left: 8px"
                                >导出Csv</a-button
                            >
                        </span>
                    </a-col>
                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->
        <a-modal
            title="请选择导出日期"
            :visible="showDayFlag"
            :confirmLoading="confirmLoading"
            switchFullscreen
            cancelText="关闭"
            okText="确认"
            @cancel="handleCancel"
            @ok="handleDayOk"
        >
            <j-date class="query-group-cust" dateFormat="YYYY-MM-DD" placeholder="请选择日期" v-model="exportDay" />
        </a-modal>
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
import GCIBusSelect from '@/components/gci/GCIBusSelect';
import moment from 'moment';
import { postAction, downloadFileAwait } from '../../api/manage';

export default {
    name: 'DyDispatchInfoList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        JDate,
        GCIBusSelect
    },
    data() {
        return {
            description: 'dy_dispatch_info管理页面',
            searchTimeSt: '',
            searchTimeEnd: '',
            selectedBus: {},
            numberPlate: undefined,
            showDayFlag: false,
            exportDay: undefined,
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
                    title: '车牌',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '车辆编码',
                    align: 'center',
                    dataIndex: 'busCode'
                },
                {
                    title: '车辆名称',
                    align: 'center',
                    dataIndex: 'busId_dictText'
                },
                {
                    title: '终端交互时间',
                    align: 'center',
                    dataIndex: 'infoTime',
                    customRender: function(text) {
                        return !text ? '' : text.length > 10 ? text.substr(0, 19) : text;
                    }
                },
                {
                    title: '信息内容',
                    align: 'center',
                    dataIndex: 'infoContent'
                },
                {
                    title: '信息状态', // 0－初始状态 1－超时无应答 2－同意调度 3－不同意调度 4－触发成功 5－触发失败 6－同意任务申请 7－不同意任务申请 8－已阅读 9－通信成功
                    align: 'center',
                    dataIndex: 'infoStatus_dictText'
                },
                {
                    title: '创建时间',
                    align: 'center',
                    dataIndex: 'dateCreated',
                    customRender: function(text) {
                        return !text ? '' : text.length > 10 ? text.substr(0, 19) : text;
                    }
                },
                {
                    title: '线路名称',
                    align: 'center',
                    dataIndex: 'routeName'
                },
                {
                    title: '站台名称',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                {
                    title: '事件类型', // 0－调度指令；1－上班签到；2－运营签到；3－交接班；4－运营签退；5－下班签退；6－发车提示；7－通知；8－触发指令；9－进总站；A－固定资讯（不需要调度员应答）；B－固定资讯（需要调度员应答））
                    align: 'center',
                    dataIndex: 'infoType_dictText'
                },
                {
                    title: '调度用户',
                    align: 'center',
                    dataIndex: 'dispatchUser'
                },
                {
                    title: '终端ID',
                    align: 'center',
                    dataIndex: 'obuId'
                }
                // ,
                // {
                //   title:'月台id',
                //   align:"center",
                //   dataIndex: 'stationId'
                // },
                // {
                //   title:'线路类型',
                //   align:"center",
                //   dataIndex: 'serviceType'
                // },
                // {
                //   title:'子线路',
                //   align:"center",
                //   dataIndex: 'routeSubId'
                // },
                // {
                //   title:'运营线路ID',
                //   align:"center",
                //   dataIndex: 'routeId'
                // },
                // {
                //   title:'失效时间',
                //   align:"center",
                //   dataIndex: 'infoTimeOut',
                //   customRender:function (text) {
                //     return !text?"":(text.length>10?text.substr(0,10):text)
                //   }
                // },
                // {
                //   title:'资讯内容历史',
                //   align:"center",
                //   dataIndex: 'infoContentHist'
                // },
                // {
                //   title: '发车时间（调度）',
                //   align: 'center',
                //   dataIndex: 'dispatchTime'
                // }
                // {
                //   title:'信息流水',
                //   align:"center",
                //   dataIndex: 'dataSerial'
                // },
                // {
                //   title:'固定资讯ID',
                //   align:"center",
                //   dataIndex: 'fixMessageId'
                // },
            ],
            url: {
                list: '/monitor/dyDispatchInfo/list',
                delete: '/monitor/dyDispatchInfo/delete',
                deleteBatch: '/monitor/dyDispatchInfo/deleteBatch',
                exportXlsUrl: '/monitor/dyDispatchInfo/exportXls',
                importExcelUrl: 'monitor/dyDispatchInfo/importExcel',
                exportCsv: 'monitor/dyDispatchInfo/exportCsv'
            },
            dictOptions: {},
            RangeTime: []
        };
    },
    computed: {
        importExcelUrl: function() {
            return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
        }
    },
    methods: {
        exportByDay() {
            const params = this.getQueryParams(); // 查询条件
            if (!params.busId) {
                this.$message.warning('请选择车辆');
                return;
            }
            this.showDayFlag = true;
        },
        handleCancel() {
            this.showDayFlag = false;
        },
        handleDayOk() {
            const params = this.getQueryParams(); // 查询条件
            if (!params.busId) {
                this.$message.warning('请选择车辆');
                return;
            }
            if (!this.exportDay) {
                this.$message.warn('请选择导出日期');
                return;
            }
            console.log('params', params);
            params.exportDay = this.exportDay;
            params.exportType = 'day';
            console.log('exportDay', this.exportDay);
            this.queryParam = params;
            this.handleExportXls('终端交互信息查询');
            this.showDayFlag = false;
        },
        handleExportXls1() {
            if (this.ipagination.total === 0) {
                this.$message.warning('当前记录为0条,请重新查询后再导出');
                return;
            }
            this.queryParam.startTime = this.searchTimeSt;
            this.queryParam.endTime = this.searchTimeEnd;
            this.handleExportXls('终端交互信息查询');
        },
        async handleExportCsv() {
            if (this.ipagination.total === 0) {
                this.$message.warning('当前记录为0条,请重新查询后再导出');
                return;
            }
            const params = this.getQueryParams(); // 查询条件
            params.startTime = this.searchTimeSt;
            params.endTime = this.searchTimeEnd;
            // getAction(this.url.exportCsv, this.queryParam)
            await downloadFileAwait(this.url.exportCsv, '终端交互信息.csv', params);
        },
        initDictConfig() {
            let start = moment(new Date().setHours(0));
            let end = moment(new Date());
            console.log(start.format('YYYY-MM-DD HH:mm:ss'));
            this.searchTimeSt = start.format('YYYY-MM-DD HH:mm:ss');
            this.searchTimeEnd = end.format('YYYY-MM-DD HH:mm:ss');
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
            const params = this.getQueryParams(); // 查询条件
            console.log('params', params);
            if (arg !== 2) {
                params.startTime = this.searchTimeSt;
                params.endTime = this.searchTimeEnd;
                params.column = 'info_time';
            }
            if (!params.busId) {
                this.$message.warning('请选择车辆');
                return;
            }
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
            this.numberPlate = null;
            this.RangeTime = [];
            this.queryParam = {};
            this.searchTimeSt = '';
            this.searchTimeEnd = '';
            this.loadData(2);
        }
    }
};
</script>
<style scoped>
@import '../../assets/less/common.less';
</style>
