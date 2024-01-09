<template>
    <j-modal
        :title="title"
        :width="width"
        :visible="visible"
        :confirmLoading="confirmLoading"
        switchFullscreen
        @ok="handleOk"
        @cancel="handleCancel"
        cancelText="关闭"
    >
        <a-spin :spinning="confirmLoading">
            <a-form :form="form">
                <a-form-item label="类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                        v-decorator="['serviceType', validatorRules.serviceType]"
                        optionFilterProp="children"
                        :filterOption="filterOption"
                        @change="serviceTypeChange"
                        show-search
                    >
                        <a-select-option v-for="item in dictOptions" :key="item.id" :value="item.value">
                            {{ item.type }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="类别备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['serviceTypeRemark', validatorRules.serviceTypeRemark]"
                        placeholder="请输入类别备注"
                    ></a-input>
                </a-form-item>
                <a-form-item label="方向" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                        v-decorator="['direction', validatorRules.direction]"
                        :filter-option="false"
                        @change="directionChange"
                    >
                        <a-select-option v-for="(item, index) in directionList" :key="index" :value="item.key">
                            <span style="display: inline-block;width: 100%" :title="item.value">
                                {{ item.value }}
                            </span>
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <template v-slot:label>
                        <a-tooltip>
                            <template slot="title">
                                <div>问：预案明细的触发号选项找不到相应的服务线路</div>
                                <div>
                                    答：需要去“线路发布”模块重新发布线路后再查看是否出现触发号
                                </div>
                            </template>
                            <a-icon type="question-circle" />
                            触发号
                        </a-tooltip>
                    </template>
                    <a-select
                        style="width: 80%"
                        v-model="model.routeSubId"
                        :filter-option="false"
                        @change="serviceNumberChange"
                        show-search
                        @search="getRoutesub"
                        allowClear
                    >
                        <a-select-option v-for="(item, index) in routesubList" :key="index" :value="item.id">
                            <span style="display: inline-block;width: 100%" :title="item.serviceNumber">
                                {{ item.routeSubName }}-{{ item.serviceName }}-{{ item.firstStationName }}-{{
                                    item.lastStationName
                                }}({{ item.serviceNumber }})
                            </span>
                        </a-select-option>
                    </a-select>
                    <a-input style="width: 20%" v-decorator="['serviceNumber', validatorRules.serviceNumber]"></a-input>
                </a-form-item>
                <a-form-item label="首站" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                        v-decorator="['firstStationId', validatorRules.firstStationId]"
                        optionFilterProp="children"
                        :filterOption="filterOption"
                        @change="routeStationChange"
                        show-search
                    >
                        <a-select-option v-for="(item, index) in routeStationList" :key="index" :value="item.stationId">
                            {{ item.routeStationName }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="末站" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                        v-decorator="['lastStationId', validatorRules.lastStationId]"
                        optionFilterProp="children"
                        :filterOption="filterOption"
                        @change="routeStationChange2"
                        show-search
                    >
                        <a-select-option v-for="(item, index) in routeStationList" :key="index" :value="item.stationId">
                            {{ item.routeStationName }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <!-- label="里程" -->
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <template v-slot:label>
                        <a-tooltip>
                            <template slot="title">
                                <div>1、问：审核里程更新后为什么添加预案时不能显示最新修改的里程？</div>
                                <div>
                                    答：更新审核里程后需要在“线路发布”模块重新发布线路后预案管理模块才能使用最新的里程。
                                </div>
                                <div>2、问：修改预案明细的首末站为什么不能自动根据修改后首末站显示相应里程？</div>
                                <div>
                                    答：目前修改首末站后里程暂时不能自动根据阶梯里程显示，需要手动修改下，后期会优化功能
                                </div>
                                <div>3、问：预案明细的自动获取的里程数来自哪里？</div>
                                <div>
                                    答：根据选择触发号来定，选择的是子线路或服务线路则来自子线路或服务线路的“审核里程”，没有审核里程则来自“站间里程设置”的里程
                                    选择触发号为非运营专线则里程来自专线的“线路运营里程”
                                </div>
                            </template>
                            <a-icon type="question-circle" />
                            里程
                        </a-tooltip>
                    </template>
                    <a-input-number
                        v-decorator="['mileage']"
                        placeholder="请输入里程"
                        style="width: 100%"
                        :min="0"
                        :precision="2"
                    />
                </a-form-item>
                <a-form-item label="间隔" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        maxlength="3"
                        v-decorator="['interval', validatorRules.interval]"
                        placeholder="请输入数字（0-999）"
                        style="width: 100%"
                        oninput="if(value.length>3)value=value.slice(0,3);"
                    />
                </a-form-item>
                <!-- <a-form-item label="序号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input maxlength=3 v-decorator="['orderNum', validatorRules.orderNum]" placeholder="请输入数字（1-99）" style="width: 100%"
            oninput="if(value.length>3)value=value.slice(0,3);"/>
        </a-form-item> -->
            </a-form>
        </a-spin>
    </j-modal>
</template>

<script>
import { httpAction, getAction } from '@/api/manage';
import pick from 'lodash.pick';
import JDate from '@/components/jeecg/JDate';
import { ajaxGetDictItems, getDictItemsFromCache } from '@/api/api';
import { isValidText } from '@/utils/validate';
import { postAction } from '../../../api/manage';

export default {
    name: 'BsPreptripModal',
    components: {
        JDate
    },
    data() {
        return {
            form: this.$form.createForm(this),
            title: '操作',
            width: 1000,
            visible: false,
            model: {},
            serviceType: '',
            labelCol: {
                xs: { span: 24 },
                sm: { span: 5 }
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 16 }
            },
            confirmLoading: false,
            url: {
                add: '/base/bsPreptrip/add',
                edit: '/base/bsPreptrip/edit',
                getRoutesubList: '/baseservice/bsRoute/getRoutesubList',
                bsRouteFix: '/bsroute/bsRouteFix/list',
                getRouteStationList: '/baseservice/bsRoute/getRouteStationList',
                bsRouteStaFix: '/bsroute/bsRouteStaFix/list',
                getDictItem: '/base/bsPreptripTypeAlias/getTaskTypeList'
            },
            validatorRules: {
                direction: {
                    rules: [{ required: true, message: '请选择方向!' }]
                },
                serviceNumber: {
                    rules: [{ required: true, message: '请输入触发号!' }, { validator: this.checkServiceNumber }]
                },
                serviceType: {
                    rules: [{ required: true, message: '请选择类别!' }]
                },
                mileage: {
                    rules: [{ required: true, message: '请输入里程!' }]
                },
                serviceTypeRemark: {
                    rules: [{ required: false, message: '请输入类别备注!' }, { validator: this.checkServiceTypeRemark }]
                },
                firstStationId: {
                    rules: [{ required: true, message: '请选择首站!' }]
                },
                lastStationId: {
                    rules: [{ required: true, message: '请选择末站!' }]
                },
                interval: {
                    rules: [{ validator: this.checkInterval }]
                }
                // orderNum: {
                //     rules: [{ required: true, message: '请输入序号!' }, { validator: this.checkOrderNum }]
                // }
            },
            routeId: null,
            orgCode: null,
            tripdefineId: null,
            directionList: [],
            routesubList: [],
            routeStationList: [],
            dictOptions: [],
            serviceNumber: null,
            subLoading: false,
            serviceNumberFix: 'FF'
        };
    },
    created() {},
    methods: {
        checkServiceTypeRemark(rule, value, callback) {
            if (value && !isValidText(value)) {
                callback(new Error('请输入合法字符'));
            }
            if (value && value.split(' ').join('').length < value.length) {
                callback(new Error('不能输入空格'));
            }
            if (value && value.length > 50) {
                callback(new Error('字符长度请控制在50以内'));
            } else {
                callback();
            }
            callback();
        },
        checkServiceNumber(rule, value, callback) {
            var regex = /^[0-9A-Z][0-9A-Z]$/;
            if (value && !regex.test(value)) {
                callback(new Error('请输入两位数字或大写字母'));
            } else {
                callback();
            }
        },
        checkInterval(rule, value, callback) {
            var regex = /^([0-9]|[1-9][0-9]|[1-9][0-9][0-9])$/;
            if (value && !regex.test(value)) {
                callback(new Error('请输入数字（0-999）'));
            } else {
                callback();
            }
        },
        checkOrderNum(rule, value, callback) {
            var regex = /^([1-9]|[1-9][0-9])$/;
            if (value && !regex.test(value)) {
                callback(new Error('请输入数字（1-99）'));
            } else {
                callback();
            }
        },
        add() {
            this.edit({});
        },
        edit(record) {
            this.form.resetFields();
            console.log('aa record', record);
            this.model = Object.assign({}, record);
            console.log('aa record this.model', this.model);
            this.visible = true;
            this.getPreptripType();
            this.getRouteDirection();
            this.routesubList = [];
            if (this.model.serviceType) {
                //加载子线路
                this.getRoutesub(null, this.model.routeSubId);
                //加载线路站点
                this.getRouteStation(1);
            }
            this.$nextTick(() => {
                this.form.setFieldsValue(
                    pick(
                        this.model,
                        'preptripName',
                        'direction',
                        'routeSubId',
                        'serviceName',
                        'serviceNumber',
                        'serviceType',
                        'dispatchText',
                        'mileage',
                        'firstStationId',
                        'firstStationName',
                        'lastStationId',
                        'lastStationName',
                        'createUser',
                        'lastUpdated',
                        'tripdefineId',
                        // 'orderNum',
                        'dateCreated',
                        'serviceTypeRemark',
                        'interval',
                        'createTime',
                        'createBy',
                        'updateBy',
                        'updateTime',
                        'sysOrgCode',
                        'tenantId',
                        'delFlag'
                    )
                );
            });
        },
        close() {
            this.$emit('close');
            this.visible = false;
        },
        handleOk() {
            const that = this;
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
                    let formData = Object.assign(this.model, values);
                    // this.model.serviceNumber = this.serviceNumber
                    if (formData.mileage !== null && formData.mileage !== undefined && formData.mileage !== '') {
                        if (formData.mileage < 0) {
                            this.$message.warning('里程不能小于0');
                            return;
                        } else {
                            formData.mileage = Math.round(formData.mileage * 100) / 100;
                        }
                    }
                    if (formData.preptripId === undefined) {
                        formData.preptripId = Date.now();
                    }
                    // formData.serviceNumber = this.serviceNumber
                    console.log('表单提交数据', formData);
                    that.$emit('ok', formData);
                    that.close();
                }
            });
        },
        handleCancel() {
            this.close();
        },
        popupCallback(row) {
            this.form.setFieldsValue(
                pick(
                    row,
                    'preptripName',
                    'direction',
                    'routeSubId',
                    'serviceName',
                    'serviceNumber',
                    'serviceType',
                    'dispatchText',
                    'mileage',
                    'firstStationId',
                    'firstStationName',
                    'lastStationId',
                    'lastStationName',
                    'createUser',
                    'lastUpdated',
                    'tripdefineId',
                    // 'orderNum',
                    'dateCreated',
                    'serviceTypeRemark',
                    'interval',
                    'createTime',
                    'createBy',
                    'updateBy',
                    'updateTime',
                    'sysOrgCode',
                    'tenantId',
                    'delFlag'
                )
            );
        },
        //初始化预案类别
        getPreptripType() {
            this.dictOptions = [];
            getAction(this.url.getDictItem).then(res => {
                if (res.success) {
                    this.dictOptions = res.result;
                }
            });
        },
        //初始化方向
        getRouteDirection() {
            this.directionList = [];
            // 用全程子线路作为方向
            httpAction(this.url.getRoutesubList, { routeId: this.routeId, serviceType: '1' }, 'post')
                .then(res => {
                    if (res.success) {
                        for (let t of res.result) {
                            let item = [];
                            item.key = t.direction;
                            item.value = t.firstStationName + '--' + t.lastStationName;
                            this.directionList.push(item);
                        }
                    }
                })
                .finally(() => {
                    let item = [];
                    item.key = '2';
                    item.value = '无方向';
                    this.directionList.push(item);
                    if (this.model.direction) {
                        this.form.setFieldsValue({ direction: this.model.direction });
                    } else {
                        this.form.setFieldsValue({ direction: this.directionList[0].key });
                        this.model.direction = this.directionList[0].key;
                    }
                    console.log(this.directionList);
                });
        },
        //预案类别change
        serviceTypeChange(e) {
            console.log('serviceType', e);
            this.serviceType = e;
            this.model.serviceType = e;
            // this.form.setFieldsValue({ serviceNumber: null })
            this.getRoutesub(null);
            for (let t of this.dictOptions) {
                if (this.serviceType === t.value) {
                    this.model.serviceName = t.type;
                    break;
                }
            }
            //清缓存
            this.model.routeSubId = '';
            this.form.setFieldsValue({ firstStationName: '' });
            this.form.setFieldsValue({ firstStationId: '' });
            this.form.setFieldsValue({ lastStationName: '' });
            this.form.setFieldsValue({ lastStationId: '' });
        },
        //方向change
        directionChange(e) {
            this.model.direction = e;
            // this.form.setFieldsValue({ serviceNumber: null })
            this.getRoutesub(null);
            //清缓存
            this.model.routeSubId = '';
            this.form.setFieldsValue({ firstStationName: '' });
            this.form.setFieldsValue({ firstStationId: '' });
            this.form.setFieldsValue({ lastStationName: '' });
            this.form.setFieldsValue({ lastStationId: '' });
        },
        //加载子线路
        getRoutesub(name, id) {
            this.routesubList = [];
            if (!this.model.serviceType) {
                this.$message.warning('请选择类别');
                return;
            }
            if (this.model.serviceType < 0) {
                postAction(this.url.bsRouteFix, { id: id, routeFixName: name, pageNo: 1, pageSize: 50 })
                    .then(res => {
                        if (res.success) {
                            for (let t of res.result.data) {
                                t.serviceNumber = this.serviceNumberFix;
                            }
                            this.routesubList = res.result.data;
                        }
                    })
                    .finally(() => {});
            } else {
                let direction1 = this.model.direction;
                if (direction1 === 2) {
                    direction1 = null;
                }
                postAction(this.url.getRoutesubList, { routeId: this.routeId, direction: direction1 })
                    .then(res => {
                        if (res.success) {
                            this.routesubList = res.result;
                        }
                    })
                    .finally(() => {});
            }
        },
        //子线路change
        serviceNumberChange(routesubId) {
            console.log('routesubId', routesubId);
            for (let t of this.routesubList) {
                if (routesubId === t.id) {
                    this.routeStationList = [];
                    this.form.setFieldsValue({ mileage: t.mileage });
                    this.form.setFieldsValue({ serviceNumber: t.serviceNumber });
                    this.model.routeSubId = t.id;
                    this.getRouteStation();
                    break;
                }
            }
            //清缓存
            this.form.setFieldsValue({ firstStationName: '' });
            this.form.setFieldsValue({ firstStationId: '' });
            this.form.setFieldsValue({ lastStationName: '' });
            this.form.setFieldsValue({ lastStationId: '' });
        },
        //加载线路站点
        getRouteStation(args) {
            if (this.model.serviceType < 0) {
                //专线
                postAction(this.url.bsRouteStaFix, { routeId: this.model.routeSubId, pageNo: 1, pageSize: 50 })
                    .then(res => {
                        if (res.success) {
                            this.routeStationList = res.result.data;
                            console.info('this.routeStationList(bsRouteStaFix)==', this.routeStationList);
                        }
                    })
                    .finally(() => {
                        this.routeStationInit(args);
                    });
            } else {
                //子线路
                postAction(this.url.getRouteStationList, { id: this.model.routeSubId })
                    .then(res => {
                        if (res.success) {
                            this.routeStationList = res.result;
                            console.info('this.routeStationList(bsRouteSta)==', this.routeStationList);
                        }
                    })
                    .finally(() => {
                        this.routeStationInit(args);
                    });
            }
        },
        routeStationInit(args) {
            if (args == 1) {
                let fsId = this.model.firstStationId;
                let fsName = this.model.firstStationName;
                this.form.setFieldsValue({ firstStationId: fsId });
                this.form.setFieldsValue({ firstStationName: fsName });

                let lsId = this.model.lastStationId;
                let lsName = this.model.lastStationName;
                this.form.setFieldsValue({ lastStationId: lsId });
                this.form.setFieldsValue({ lastStationName: lsName });
            } else {
                let fsId = this.routeStationList[0].stationId;
                let fsName = this.routeStationList[0].routeStationName;
                this.form.setFieldsValue({ firstStationId: fsId });
                this.form.setFieldsValue({ firstStationName: fsName });
                this.model.firstStationId = fsId;
                this.model.firstStationName = fsName;

                let last = this.routeStationList.length - 1;
                let lsId = this.routeStationList[last].stationId;
                let lsName = this.routeStationList[last].routeStationName;
                this.form.setFieldsValue({ lastStationId: lsId });
                this.form.setFieldsValue({ lastStationName: lsName });
                this.model.lastStationId = lsId;
                this.model.lastStationName = lsName;
            }
        },
        //站台change
        routeStationChange(e) {
            for (let t of this.routeStationList) {
                if (e === t.stationId) {
                    console.info('e1=', e);
                    this.form.setFieldsValue({ firstStationName: t.routeStationName });
                    this.form.setFieldsValue({ firstStationId: t.stationId });
                    this.model.firstStationId = t.stationId;
                    this.model.firstStationName = t.routeStationName;
                    break;
                }
            }
        },
        routeStationChange2(e) {
            for (let t of this.routeStationList) {
                if (e === t.stationId) {
                    console.info('e2=', e);
                    this.form.setFieldsValue({ lastStationName: t.routeStationName });
                    this.form.setFieldsValue({ lastStationId: t.stationId });
                    this.model.lastStationId = t.stationId;
                    this.model.lastStationName = t.routeStationName;
                    break;
                }
            }
        },
        filterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        }
    }
};
</script>
