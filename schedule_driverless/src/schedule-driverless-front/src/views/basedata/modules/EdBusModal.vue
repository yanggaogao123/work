<template>
    <j-modal
        :title="title"
        :width="width"
        :visible="visible"
        :confirmLoading="confirmLoading"
        switchFullscreen
        @ok="handleOk"
        @cancel="handleCancel"
        :okButtonProps="{ props: { disabled: disableSubmit } }"
        cancelText="关闭"
    >
        <a-spin :spinning="confirmLoading">
            <a-form :form="form">
                <a-form-item label="车辆名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['busName', validatorRules.busName]"
                        placeholder="请输入车辆名称"
                        maxlength="10"
                        oninput="if(value.length>8)value=value.slice(0,8);"
                    ></a-input>
                </a-form-item>
                <a-form-item label="车辆编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['busCode', validatorRules.busCode]"
                        placeholder="请输入车辆编码"
                        maxlength="8"
                        oninput="if(value.length>8)value=value.slice(0,8);"
                    ></a-input>
                </a-form-item>
                <a-form-item label="车辆牌照" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['numberPlate', validatorRules.numberPlate]"
                        placeholder="请输入车辆牌照"
                    ></a-input>
                </a-form-item>
                <a-form-item label="是否启用" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-dict-select-tag
                        v-decorator="['isActive', { initialValue: '0' }]"
                        :triggerChange="true"
                        placeholder="请选择车辆是否启用"
                        disabled
                        dictCode="bus_is_active"
                    />
                </a-form-item>
                <a-form-item label="车辆状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-dict-select-tag
                        v-decorator="['busStatus', { initialValue: '0' }]"
                        :triggerChange="true"
                        placeholder="请选择车辆状态"
                        disabled
                        dictCode="bus_status"
                    />
                </a-form-item>
                <a-form-item label="所属机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <GCIOrgTreeSelect
                        @change="changeOrg"
                        v-decorator="['organId', validatorRules.organId]"
                        placeholder="请选择所属机构"
                        :selectType="selectType"
                        :isGetOption="isGetOption"
                    ></GCIOrgTreeSelect>
                </a-form-item>

                <a-form-item label="所属线路(日班)" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <!-- <a-select
            allowClear
            v-decorator="['routeId', validatorRules.routeId]"
            placeholder="请输入线路名搜索"
            show-search
            :getPopupContainer="triggerNode => triggerNode.parentNode"
            :filter-option="filterOption"
            @search="handleRouteSearch"
            :loading="routeLoading"
          >
            <a-select-option
              v-for="(item, index) in routeList"
              :key="index"
              :value="item.routeId"
              :text="item.routeName"
            >
              <span style="display: inline-block;width: 100%" :title="item.routeName">
                {{ item.routeName }}
              </span>
            </a-select-option>
          </a-select> -->
                    <GCIRouteSelect
                        item="id"
                        v-decorator="['routeId', validatorRules.routeId]"
                        @change="changeRouteSelect"
                        :width="routeWidth"
                    />
                </a-form-item>
                <a-form-item label="所属线路(夜班)" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <!-- <a-select
                        allowClear
                        v-decorator="['routeIdNight', validatorRules.routeIdNight]"
                        placeholder="请输入线路名搜索"
                        show-search
                        :getPopupContainer="triggerNode => triggerNode.parentNode"
                        :filter-option="filterOption"
                        @search="handleRouteNightSearch"
                        :loading="routeNightLoading"
                    >
                        <a-select-option
                            v-for="(item, index) in routeIdNightList"
                            :key="index"
                            :value="item.routeId"
                            :text="item.routeName"
                        >
                            <span style="display: inline-block;width: 100%" :title="item.routeName">
                                {{ item.routeName }}
                            </span>
                        </a-select-option>
                    </a-select> -->
                    <GCIRouteSelect
                        item="id"
                        v-decorator="['routeIdNight', validatorRules.routeIdNight]"
                        @change="changeRouteSelect"
                        :width="routeWidth"
                    />
                </a-form-item>
                <a-form-item label="车辆类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-dict-select-tag
                        v-decorator="['busType', validatorRules.busType, { initialValue: '1' }]"
                        placeholder="请选择车辆类型"
                        :triggerChange="true"
                        dictCode="bus_type"
                    />
                </a-form-item>
                <a-form-item label="终端芯片编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                        show-search
                        v-decorator="['obuChipCode']"
                        placeholder="请输入终端芯片编号搜索"
                        :default-active-first-option="false"
                        :filter-option="false"
                        :show-arrow="false"
                        :not-found-content="null"
                        @search="handleSearch"
                    >
                        <a-select-option
                            v-for="e in equipmentData"
                            :key="e.obuChipCode"
                            @click="handleInput(e.equipmentId)"
                        >
                            <span style="display: inline-block;width: 100%" :title="e.obuChipCode">
                                {{ e.obuChipCode }}
                            </span>
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="终端ID" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['obuId', validatorRules.obuId]"
                        placeholder="请输入终端ID"
                        style="width: 100%"
                    />
                </a-form-item>
                <a-form-item label="无障碍设施情况" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-dict-select-tag
                        v-decorator="['isFacilities', validatorRules.isFacilities]"
                        placeholder="无障碍设施情况"
                        :triggerChange="true"
                        dictCode="bus_isFacilities"
                    />
                </a-form-item>
                <a-form-item label="核定载客量" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input-number
                        v-decorator="['carryCapacity']"
                        placeholder="请输入核定载客量,最大值100"
                        style="width: 100%"
                        :min="0"
                        :precision="0"
                        :max="100"
                    />
                </a-form-item>
                <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-textarea v-decorator="['remark', validatorRules.remark]" rows="4" placeholder="请输入备注" />
                </a-form-item>
            </a-form>
        </a-spin>
    </j-modal>
</template>

<script>
import { httpAction, getAction } from '@/api/manage';
import pick from 'lodash.pick';
import { validateText, checkedPlatenum, validateRemark } from '@/utils/validate';
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import { postAction } from '../../../api/manage';

export default {
    name: 'BsBusModal',
    props: Object,
    components: { GCIOrgTreeSelect, GCIRouteSelect },
    data() {
        return {
            selectType: 'id',
            isGetOption: true,
            disableSubmit: true,
            form: this.$form.createForm(this),
            title: '操作',
            width: 800,
            visible: false,
            model: {},
            labelCol: {
                xs: { span: 24 },
                sm: { span: 5 }
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 16 }
            },
            confirmLoading: false,
            validatorRules: {
                obuId: {
                    rules: [
                        { required: true, message: '请输入终端ID!' },
                        { max: 10, message: '长度最大为 10 个字符', trigger: 'blur' },
                        { validator: validateText }
                    ]
                },
                busCode: {
                    rules: [
                        { required: true, message: '请输入车辆编码!' },
                        { max: 8, message: '长度最大为 8 个字符', trigger: 'blur' },
                        { validator: validateText }
                    ]
                },
                busName: {
                    rules: [
                        { required: true, message: '请输入车辆名称!' },
                        { max: 8, message: '长度最大为 8 个字符', trigger: 'blur' },
                        { validator: validateText }
                    ]
                },
                isActive: {
                    rules: [{ required: true, message: '请选择是否激活!' }]
                },
                organId: {
                    rules: [{ required: true, message: '请选择所属机构!' }]
                },
                routeId: {
                    rules: [{ required: true, message: '请选择所属线路(日班)!' }]
                },
                numberPlate: {
                    rules: [{ required: true, message: '请输入车辆牌照!' }, { validator: checkedPlatenum }]
                },
                busType: {
                    rules: [{ required: true, message: '请选择车辆类型!' }]
                },
                obuChipCode: {
                    rules: []
                },
                isFacilities: {
                    rules: [{ required: true, message: '无障碍设施不能为空!' }]
                },
                carryCapacity: {
                    rules: []
                },
                remark: {
                    rules: [
                        { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' },
                        { validator: validateRemark }
                    ]
                }
            },
            url: {
                add: '/edBus/save',
                edit: '/edBus/save',
                routeList: '/bsroute/bsRoute/getRouteByName',
                equipment: '/basedata/bsEquipment/list',
                getRouteUrl: '/baseservice/bsRoute/getRouteList'
            },
            equipmentId: '',
            organId: '',
            routeId: '',
            routeIdNight: '',
            equipmentData: [],
            treeData: [],
            routeList: [],
            routeIdNightList: [],
            routeLoading: false,
            routeNightLoading: false
        };
    },
    watch: {
        organId(newValue, oldValue) {
            // this.changeRouteByOrgId(newValue, oldValue)
        }
    },
    created() {},
    methods: {
        filterOption(value, option) {
            return option.componentOptions.children[0].children[0].text.indexOf(value) >= 0;
        },
        changeOrg(value) {
            if (!value) {
                return;
            }
            this.organId = value;
            this.changeRouteByOrgId(value, '', 1);
            this.changeRouteByOrgId(value, '', 2);
            this.handleSearch();
        },
        changeRouteByOrgId(organId, routeName, type) {
            let that = this;
            console.log('新的机构', organId);
            if (!organId) {
                this.$message.warning('请先选择机构');
                return;
            }
            if (type == 1) {
                // 查询日班线路
                let param = {
                    organRunId: organId,
                    tripsType: '0',
                    pageNo: 1,
                    pageSize: 10,
                    routeName: routeName
                };
                this.routeLoading = true;
                this.routeList = [];
                this.form.setFieldsValue({ routeId: '' });
                getAction(this.url.getRouteUrl, param)
                    .then(res => {
                        if (res.success) {
                            this.routeList = res.result.records;
                        }
                    })
                    .finally(res => {
                        that.routeLoading = false;
                    });
            }

            if (type == 2) {
                // 查询夜班线路
                let param2 = {
                    organRunId: organId,
                    tripsType: '1',
                    pageNo: 1,
                    pageSize: 10,
                    routeName: routeName
                };
                this.routeNightLoading = true;
                this.routeIdNightList = [];
                this.form.setFieldsValue({ routeIdNight: '' });
                getAction(this.url.getRouteUrl, param2)
                    .then(res => {
                        if (res.success) {
                            this.routeIdNightList = res.result.records;
                        }
                    })
                    .finally(res => {
                        that.routeNightLoading = false;
                    });
            }
        },
        handleRouteSearch(value) {
            this.routeNameSearch = value;
            this.changeRouteByOrgId(this.organId, value, 1);
        },
        handleRouteNightSearch(value) {
            this.routeNameSearch = value;
            this.changeRouteByOrgId(this.organId, value, 2);
        },
        selectObuChipCode(value, option) {
            console.log('value', value);
            console.log('option', option);
        },
        addFilterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        handleInput(e) {
            this.model.equipmentId = e;
        },
        add() {
            this.handleSearch();
            this.edit({});
        },
        edit(record) {
            this.visible = true;
            this.form.resetFields();
            this.organId = record.organId;
            this.routeId = record.routeId;
            this.routeIdNight = record.routeIdNight;
            this.equipmentId = record.equipmentId;
            this.model = Object.assign({}, record);
            console.log('this.model', this.model);
            console.log('record.organId', record.organId);
            if (record.organId) {
                this.organId = record.organId;
                this.changeOrg(record.organId);
            }
            this.handleSearch();
            this.$nextTick(() => {
                this.form.setFieldsValue(
                    pick(
                        this.model,
                        'obuChipCode',
                        'auditResult',
                        'auditTime',
                        'auditUser',
                        'busCode',
                        'busName',
                        'busStatus',
                        'equipmentId',
                        'isActive',
                        'numberPlate',
                        'obuId',
                        'orgCode',
                        'remark',
                        'routeId',
                        'productor',
                        'busType',
                        'isFacilities',
                        'routeIdNight',
                        'carryCapacity',
                        'plateColor',
                        'vin',
                        'engineNo',
                        'fuelType',
                        'length',
                        'publishTime'
                    )
                );
            });
            setTimeout(() => {
                this.form.setFieldsValue({
                    equipmentId: record.equipmentId,
                    routeIdNight: record.routeIdNight,
                    routeId: record.routeId,
                    organId: record.organId
                });
            }, 100);
        },
        handleSearch(value) {
            this.form.setFieldsValue({ obuChipCode: '' });
            let param = { obuChipCode: value, organId: this.organId, pageNo: 1, pageSize: 50 };
            postAction(this.url.equipment, param).then(res => {
                if (res.success) {
                    this.equipmentData = res.result.records;
                } else {
                    this.$message.warning(res.message);
                }
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
                    this.disableSubmit = true;
                    that.confirmLoading = true;
                    let httpurl = '';
                    let method = '';
                    if (!this.model.id) {
                        httpurl += this.url.add;
                        method = 'post';
                    } else {
                        httpurl += this.url.edit;
                        method = 'put';
                    }
                    let formData = Object.assign(this.model, values);
                    console.log('表单提交数据', formData);
                    httpAction(httpurl, formData, method)
                        .then(res => {
                            if (res.success) {
                                that.$message.success(res.message);
                                that.$emit('ok');
                                that.close();
                            } else {
                                if (res.code === 501) {
                                    that.$message.warning(res.message);
                                } else {
                                    that.$message.warning(res.message);
                                }
                            }
                        })
                        .finally(() => {
                            that.confirmLoading = false;
                            that.disableSubmit = false;
                        });
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
                    'obuChipCode',
                    'auditResult',
                    'auditTime',
                    'auditUser',
                    'busCode',
                    'busName',
                    'busStatus',
                    'equipmentId',
                    'isActive',
                    'numberPlate',
                    'obuId',
                    'orgCode',
                    'remark',
                    'routeId',
                    'productor',
                    'busType',
                    'facilities',
                    'routeIdNight',
                    'carryCapacity',
                    'plateColor',
                    'vin',
                    'engineNo',
                    'fuelType',
                    'length',
                    'publishTime'
                )
            );
        }
    }
};
</script>
