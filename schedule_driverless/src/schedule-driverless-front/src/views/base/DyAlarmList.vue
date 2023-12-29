<template>
    <a-card :bordered="false">
        <!-- 超速记录查询 -->
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery" :form="form">
                <a-row :gutter="24">
                    <a-col :xl="4" :lg="4" :md="4" :sm="24">
                        <a-form-item label="线路名称">
                            <GCIRouteSelect
                                item="id"
                                v-decorator="['routeId', validatorRules.routeId]"
                                @change="onChangeRoute"
                            ></GCIRouteSelect>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="5" :lg="4" :md="4" :sm="24">
                        <a-form-item label="超速速度(KM/H)">
                            <a-input-number
                                placeholder="请输入超速速度"
                                v-decorator="['overSpeed', validatorRules.overSpeed]"
                                style="width: 100%"
                                @change="onChangeSpeed"
                            ></a-input-number>
                        </a-form-item>
                    </a-col>
                    <a-col :xxl="8" :xl="11" :lg="7" :md="7" :sm="24">
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
                    
                    <a-col :xxl="7" :xl="8" :lg="7" :md="8" :sm="24">
                        <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                            <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                            <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px"
                                >重置</a-button
                            >
                            <a-button
                                type="primary"
                                @click="handleExportExcel"
                                icon="download"
                                style="margin-left: 8px"
                                :loading="excelLoading"
                                >导出excel</a-button
                            >
                            <a-button
                                type="primary"
                                @click="handleExportCsv"
                                icon="download"
                                style="margin-left: 8px"
                                :loading="csvLoading"
                                >导出csv</a-button
                            >

                        </span>
                    </a-col>
                </a-row>
            </a-form>
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
                @change="handleTableChange"
            >
                <span slot="action" slot-scope="text, record">
                    <a @click="handleEdit(record)">编辑</a>

                    <a-divider type="vertical" />
                    <a-dropdown>
                        <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                        <a-menu slot="overlay">
                            <a-menu-item>
                                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                                    <a>删除</a>
                                </a-popconfirm>
                            </a-menu-item>
                        </a-menu>
                    </a-dropdown>
                </span>

                <span slot="locationSlot" slot-scope="text, record">
                    <a @click="showBusTrackPlayback(record)">{{ record.latitude }}, {{ record.longtude }}</a>
                </span>

            </a-table>
        </div>
        <!-- table区域-end -->
    </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import { getAction, postAction, downloadFileAwait } from '@/api/manage';
import GCIRouteSelect from '@comp/gci/GCIRouteSelect';
import moment from 'moment';

import Vue from 'vue';
import { ACCESS_TOKEN, ENCRYPTED_STRING } from '@/store/mutation-types';
import { encryption, getEncryptedString } from '@/utils/encryption/aesEncrypt';

export default {
    name: 'DyAlarmList',
    mixins: [JeecgListMixin],
    components: {
        GCIRouteSelect
    },
    data() {
        return {
            description: '超速记录管理页面',
            // 时间
            // startValue: null,
            // endValue: null,
            // endOpen: false,
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
                    dataIndex: 'routeName'
                },
                {
                    title: '车辆牌照',
                    align: 'center',
                    dataIndex: 'numberPlate'
                },
                {
                    title: '车辆编码',
                    align: 'center',
                    dataIndex: 'busCode'
                },
                {
                    title: '驾驶员',
                    align: 'center',
                    dataIndex: 'employeeName'
                },
                {
                    title: '发生时间',
                    align: 'center',
                    dataIndex: 'occurTime'
                },
                {
                    title: '结束时间',
                    align: 'center',
                    dataIndex: 'endTime'
                },
                {
                    title: '违规位置',
                    align: 'center',
                    scopedSlots: { customRender: 'locationSlot' }
                },
                {
                    title: '站台',
                    align: 'center',
                    dataIndex: 'stationName'
                },
                //  {
                //       title: '报警类型',
                //       align:"center",
                //       dataIndex: 'alarmType'
                //      },
                {
                    title: '平均速度',
                    align: 'center',
                    dataIndex: 'speed'
                },
                {
                    title: '最大速度',
                    align: 'center',
                    dataIndex: 'maxSpeed'
                },
                
            ],
            url: {
                list: '/dy/dyAlarm/listPage',
                exportXlsUrl: 'dy/dyAlarm/exportXls',
                exportCsv: '/dy/dyAlarm/exportCsv',
                appList: '/sys/sysApp/list',
                zydPlayback_gz: '/Macau/module/monitor/zydPlayback_para.jsp'
            },
            timeSegs: [moment(this.getCurrentData(), this.dateFormat), moment(this.getCurrentData1(), this.dateFormat)],
            queryParam: {
                overSpeed: 50,
                startValue: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endValue: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59')
            },
            disableMixinCreated: true,
            excelLoading: false,
            csvLoading: false,
            form: this.$form.createForm(this),
            validatorRules: {
                routeId: {
                    rules: [{ required: true, message: '请选择线路' }]
                },
                overSpeed: {
                    rules: [{ required: true, message: '请输入超速速度' }]
                },
                timeSegs: {
                    rules: [{ required: true, message: '请选择发生时间' }, { validator: this.checkTimeSeg }]
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
    // watch: {
    //   startValue(val) {
    //     console.log('startValue', val);
    //     this.queryParam.beginTime = !!this.startValue?this.startValue.format('YYYY-MM-DD'):null;
    //     console.log(this.queryParam)
    //   },
    //   endValue(val) {
    //     console.log('endValue', val);
    //     this.queryParam.endTime = !!this.endValue?this.endValue.format('YYYY-MM-DD'):null;
    //     console.log(this.queryParam)
    //   },
    // },
    methods: {
        // disabledStartDate(startValue) {
        //   const endValue = this.endValue;
        //   if (!startValue || !endValue) {
        //     return false;
        //   }
        //   return startValue.valueOf() > endValue.valueOf();
        // },
        // disabledEndDate(endValue) {
        //   const startValue = this.startValue;
        //   if (!endValue || !startValue) {
        //     return false;
        //   }
        //   return startValue.valueOf() >= endValue.valueOf();
        // },
        // handleStartOpenChange(open) {
        //   if (!open) {
        //     this.endOpen = true;
        //   }
        // },
        // handleEndOpenChange(open) {
        //   this.endOpen = open;
        // },
        onChangeRangeDate(value, dateString) {
            this.timeSegs = value;
            this.queryParam.startValue = dateString[0];
            this.queryParam.endValue = dateString[1];
        },
        onChangeRoute(value) {
            this.queryParam.routeId = value;
        },
        onChangeSpeed(value) {
            this.queryParam.overSpeed = value;
        },
        searchReset() {
            this.queryParam = {
                overSpeed: 50,
                startValue: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 00:00:00'),
                endValue: moment()
                    .subtract(1, 'days')
                    .format('YYYY-MM-DD 23:59:59')
            };
            this.timeSegs = [
                moment(this.getCurrentData(), this.dateFormat),
                moment(this.getCurrentData1(), this.dateFormat)
            ];
            this.form.resetFields();
            this.form.setFieldsValue({ overSpeed: this.queryParam.overSpeed });
            this.form.setFieldsValue({ timeSegs: this.timeSegs });
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
                    this.onClearSelected()
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
        handleExportCsv() {
            let that = this;
            const params = this.getQueryParams(); // 查询条件
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
                    that.csvLoading = true;
                    downloadFileAwait(this.url.exportCsv, '超速记录查询.csv', params);
                    that.csvLoading = false;
                }
            });
        },
        handleExportExcel() {
            let that = this;
            const params = this.getQueryParams(); // 查询条件
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
                    that.excelLoading = true;
                    downloadFileAwait(this.url.exportXlsUrl, '超速记录查询.xls', params);
                    that.excelLoading = false;
                }
            });
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
        },
        showBusTrackPlayback(record) {
            if (record.busCode == null) {
                this.$message.warning('车辆不存在');
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
                record.occurTime.substr(0, 10) +
                '&begTime=' +
                record.occurTime.substr(11, 5) +
                '&endTime=' +
                record.endTime.substr(11, 5) +
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
                        paramString + '&language=zh_CN';
                    window.open(url);
                } else {
                    this.$message.warning('轨迹回放跳转失败');
                }
            });
            // window.open("http://localhost:8080"+this.url.zydPlayback_gz+urlParam+"&paramString="+paramString)
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
    }
};
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
