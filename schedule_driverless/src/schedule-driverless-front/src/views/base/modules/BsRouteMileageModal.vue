<template xmlns:a-col="http://www.w3.org/1999/html">
    <j-modal
        :title="title"
        :width="width"
        :visible="visible"
        :confirmLoading="confirmLoading"
        switchFullscreen
        @ok="handleOk"
        okText="保存"
        @cancel="handleCancel"
        cancelText="关闭"
    >
        <template slot="footer">
            <a-button @click="handleCancel">关闭</a-button>
            <a-button
                v-show="!disabledEdit"
                @click="handleOk"
                style=" color: #fff !important;background-color: #1890ff !important;border-color: #1890ff !important;"
                >保存</a-button
            >
        </template>
        <a-spin :spinning="confirmLoading">
            <a-form :form="form">
                <a-row>
                    <a-col :span="4">
                        <a-form-item label="线路名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.routeName" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="线路编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.routeCode" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="线路版本" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.version" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="下行首班时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.downFirstTime" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="下行末班时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.downLatestTime" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="运营机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.organName" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="4">
                        <a-form-item label="运营区域" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.runningPlaceDesc" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="运营类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.operatorTypeDesc" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="班次类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.tripsTypeDesc" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="上行首班时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.upFirstTime" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="上行末班时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.upLatestTime" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="4">
                        <a-form-item label="开始日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.beginDate" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-form-item label="结束日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-model="routeInfo.endDate" style="width: 100%" disabled />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="备注" :labelCol="labelColRemark" :wrapperCol="wrapperColRemark">
                            <a-input type="textarea" v-model="routeInfo.remark" style="width: 100%;" disabled />
                        </a-form-item>
                    </a-col>
                </a-row>

                <a-row>
                    <a-col :span="10">
                        <a-row>
                            <a-col :span="6">
                                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                                    上行站点
                                </a-form-item>
                            </a-col>
                            <a-col :span="10">
                                <a-form-item label="所属子线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
                                    <a-select
                                        v-if="upRouteSubs && upRouteSubs.length > 0"
                                        v-decorator="['upRouteSub', { initialValue: upRouteSubs[0].routesubId }]"
                                        :filter-option="false"
                                    >
                                        <a-select-option
                                            v-for="(item, index) in upRouteSubs"
                                            :key="index"
                                            :value="item.routesubId"
                                        >
                                            <span style="display: inline-block;width: 100%">
                                                {{ item.routeSubName }}
                                            </span>
                                        </a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :span="8">
                                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                                    总里程: {{ upRouteSub.sumMileage }}
                                </a-form-item>
                            </a-col>
                        </a-row>
                    </a-col>
                    <a-col :span="2"></a-col>
                    <a-col :span="11">
                        <a-row>
                            <a-col :span="6">
                                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                                    下行站点
                                </a-form-item>
                            </a-col>

                            <a-col :span="10">
                                <a-form-item label="所属子线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
                                    <a-select
                                        v-if="downRouteSubs && downRouteSubs.length > 0"
                                        v-decorator="['downRouteSub', { initialValue: downRouteSubs[0].routesubId }]"
                                        :filter-option="false"
                                    >
                                        <a-select-option
                                            v-for="(item, index) in downRouteSubs"
                                            :key="index"
                                            :value="item.routesubId"
                                        >
                                            <span style="display: inline-block;width: 100%">
                                                {{ item.routeSubName }}
                                            </span>
                                        </a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :span="8">
                                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                                    总里程: {{ downRouteSub.sumMileage }}
                                </a-form-item>
                            </a-col>
                        </a-row>
                    </a-col>
                </a-row>
                <a-row>
                    <a-col :span="11">
                        <a-table
                            ref="upTable"
                            size="small"
                            bordered
                            rowKey="routesubStaId"
                            :columns="columns"
                            :dataSource="upRouteSubStas"
                            :loading="loading"
                            class="j-table-force-nowrap"
                        >
                            <template slot="editMileage" slot-scope="text, record">
                                <a-input
                                    :disabled="disabledEdit"
                                    :value="text"
                                    @change="e => handleInputColChange(e.target.value, record, 'mileage', '0')"
                                />
                            </template>
                        </a-table>
                    </a-col>
                    <a-col :span="1"></a-col>
                    <a-col :span="11">
                        <a-table
                            ref="downTable"
                            size="small"
                            bordered
                            rowKey="routesubStaId"
                            :columns="columns"
                            :dataSource="downRouteSubStas"
                            :loading="loading"
                            class="j-table-force-nowrap"
                        >
                            <template slot="editMileage" slot-scope="text, record">
                                <a-input
                                    :disabled="disabledEdit"
                                    :value="text"
                                    @change="e => handleInputColChange(e.target.value, record, 'mileage', '1')"
                                />
                            </template>
                        </a-table>
                    </a-col>
                </a-row>
            </a-form>
        </a-spin>
    </j-modal>
</template>

<script>
import pick from 'lodash.pick';
import { JeecgListMixin } from '@/mixins/JeecgListMixin';
import JDate from '@/components/jeecg/JDate';
import { getAction, postAction } from '../../../api/manage';
import { mixinDevice } from '../../../utils/mixin';
import Vue from 'vue';
import { math } from '../../../utils/math';

export default {
    name: 'BsRouteModal',
    mixins: [JeecgListMixin, mixinDevice],
    components: {
        JDate
    },
    watch: {
        upRouteSub: {
            handler: function(newVal, oldVal) {
                this.upRouteSubStas = newVal.subStas;
            }
        },
        downRouteSub: {
            handler: function(newVal, oldVal) {
                this.downRouteSubStas = newVal.subStas;
            }
        }
    },
    data() {
        return {
            disabledEdit: false,
            routeInfo: {},
            upRouteSubs: [],
            downRouteSubs: [],
            upRouteSub: {}, //选择的上行子线路
            downRouteSub: {}, //选择的下行子线路
            upRouteSubStas: [],
            downRouteSubStas: [],
            columns: [
                {
                    title: '站序',
                    align: 'center',
                    dataIndex: 'orderNumber'
                },
                {
                    title: '编码',
                    align: 'center',
                    dataIndex: 'busStopCode'
                },
                {
                    title: '站台名称',
                    align: 'center',
                    dataIndex: 'routeStationName'
                },
                {
                    title: '站间里程(KM)',
                    align: 'center',
                    dataIndex: 'mileage',
                    scopedSlots: { customRender: 'editMileage' }
                },
                {
                    title: '里程(KM)',
                    align: 'center',
                    dataIndex: 'sumMileage'
                }
            ],
            form: this.$form.createForm(this),
            title: '操作',
            width: 1366,
            visible: false,
            model: {},
            labelCol: {
                xs: { span: 15 },
                sm: { span: 10 }
            },
            wrapperCol: {
                xs: { span: 15 },
                sm: { span: 12 }
            },
            labelColRemark: {
                xs: { span: 15 },
                sm: { span: 3 }
            },
            wrapperColRemark: {
                xs: { span: 15 },
                sm: { span: 20 }
            },
            confirmLoading: false,
            url: {
                list: '',
                saveMileages: '/routeSubSta/saveRouteSubStaMileages',
                getRouteSubStaMileageByRouteId: '/routeSubSta/getRouteSubStaMileageByRouteId'
            }
        };
    },
    created() {},
    methods: {
        loadData(arg) {},
        handleInputColChange(value, record, col, dir) {
            console.log('value', value);
            console.log('record', record);
            let regPos = /^[0-9]+.?[0-9]*/; //判断是否是数字
            if (!regPos.test(value)) {
                this.$message.warning('请输入数字!');
                return;
            }
            Vue.set(record, col, value);

            //实时计算站点与首站的总间距
            if ('0' === dir) {
                let sumMil = 0;
                for (let i in this.upRouteSubStas) {
                    sumMil = math.add(this.upRouteSubStas[i].mileage, sumMil);
                    this.upRouteSubStas[i].sumMileage = sumMil;
                }
                this.upRouteSub.sumMileage = sumMil;
            } else {
                let sumMil = 0;
                for (let i in this.downRouteSubStas) {
                    sumMil = math.add(this.downRouteSubStas[i].mileage, sumMil);
                    this.downRouteSubStas[i].sumMileage = sumMil;
                }
                this.downRouteSub.sumMileage = sumMil;
            }
        },
        add() {
            this.edit({});
        },
        edit(record) {
            this.form.resetFields();
            this.upRouteSubs = [];
            this.downRouteSubs = [];
            console.log('record', record);
            //获取用户信息
            let userInfo = this.$store.getters.userInfo;
            console.log('userInfo', userInfo);
            if (record.routeType === 1) {
                this.disabledEdit = true;
                if (userInfo.sysOrgId === '2478') {
                    if (userInfo.type === 1) {
                        this.disabledEdit = false;
                    }
                }
            }else {
              this.disabledEdit = false;
            }

            let that = this;
            getAction(this.url.getRouteSubStaMileageByRouteId, { routeId: record.routeId })
                .then(res => {
                    that.model = Object.assign({}, res.result);
                    that.routeInfo = res.result;

                    if (res.result && res.result.routeSubs) {
                        let subs = res.result.routeSubs;
                        for (let i in subs) {
                            console.log('sub', subs[i]);
                            if (subs[i].direction === '0') {
                                that.upRouteSubs.push(subs[i]);
                            } else {
                                that.downRouteSubs.push(subs[i]);
                            }
                        }
                    }
                    if (that.upRouteSubs.length > 0) {
                        that.upRouteSub = that.upRouteSubs[0];
                    }
                    if (that.downRouteSubs.length > 0) {
                        that.downRouteSub = that.downRouteSubs[0];
                    }
                    that.visible = true;
                })
                .catch(err => {
                    that.$message.warning('获取线路站间里程数据接口异常');
                });
        },
        close() {
            this.$emit('close');
            this.visible = false;
        },

        handleOk() {
            //提交站间里程数据
            let params = {
                routeId: this.routeInfo.routeId,
                upRouteSubStas: this.upRouteSubStas,
                downRouteSubStas: this.downRouteSubStas,
                upRouteSub: this.upRouteSub,
                downRouteSub: this.downRouteSub
            };
            let that = this;
            this.confirmLoading = true;
            postAction(this.url.saveMileages, params)
                .then(res => {
                    this.confirmLoading = false;
                    if (res.success) {
                        that.$message.success(res.message);
                        that.close();
                    } else {
                        that.$message.warning(res.message);
                    }
                })
                .catch(err => {
                    this.confirmLoading = false;
                    that.$message.warning('提交站间里程数据接口异常');
                });
        },
        handleCancel() {
            this.close();
        },
        popupCallback(row) {
            this.form.setFieldsValue(
                pick(
                    row,
                    'routeId',
                    'version',
                    'auditResult',
                    'auditTime',
                    'auditUser',
                    'beginDate',
                    'createUser',
                    'dateCreated',
                    'direction',
                    'downFirstTime',
                    'downLatestTime',
                    'endDate',
                    'firstStationId',
                    'firstStationName',
                    'isActive',
                    'lastStationId',
                    'lastStationName',
                    'lastUpdated',
                    'mileage',
                    'operatorType',
                    'organId',
                    'organRunId',
                    'recordStatus',
                    'remark',
                    'routeCode',
                    'routeName',
                    'routeVersion',
                    'runningPlace',
                    'shortName',
                    'tripsType',
                    'upFirstTime',
                    'upLatestTime',
                    'stationTarget',
                    'sort',
                    'lastOperator',
                    'operation',
                    'routeType',
                    'normalTicketPrice'
                )
            );
        }
    }
};
</script>
