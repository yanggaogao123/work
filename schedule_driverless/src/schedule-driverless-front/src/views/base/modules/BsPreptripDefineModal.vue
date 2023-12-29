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
        :destroyOnClose="true"
    >
        <a-spin :spinning="confirmLoading">
            <a-form :form="form">
                <a-form-item label="预案类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                        v-decorator="['taskTypeId', validatorRules.taskTypeId]"
                        optionFilterProp="children"
                        :filterOption="filterOption"
                        @change="serviceTypeChange"
                        showSearch
                    >
                        <a-select-option v-for="item in dictOptions" :key="item.id" :value="item.value">
                            {{ item.type }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="预案名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['keytitle', validatorRules.keytitle]"
                        placeholder="请输入预案名称"
                        @change="keytitleChange"
                    ></a-input>
                </a-form-item>
                <a-form-item label="下发调度内容" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['ordercontent', validatorRules.ordercontent]"
                        placeholder="请输入下发调度内容"
                        @change="ordercontentChange"
                    ></a-input>
                </a-form-item>
                <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <!-- <a-select
            allowClear
            v-decorator="['routeId', validatorRules.routeId]"
            show-search
            :filterOption="filterOption"
            @search="getRoute"
            @change="routeChange"
            :disabled="routeUpdate">
            <a-select-option v-for="(item,index) in routeList" :key="index" :value="item.id">
                {{ item.routeName }}
            </a-select-option>
          </a-select> -->
                    <GCIRouteSelect
                        placeholder="请选择线路"
                        v-decorator="['routeId', validatorRules.routeId]"
                        mode="mode"
                        :disabled="routeUpdate"
                    ></GCIRouteSelect>
                </a-form-item>
                <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-dict-select-tag
                        v-decorator="['status', validatorRules.status]"
                        :triggerChange="true"
                        placeholder="请选择状态"
                        dictCode="preptrip_status"
                    />
                </a-form-item>
            </a-form>
        </a-spin>

        <BsPreptripList ref="bsPreptripList" @ok="addList" @checkRoute="checkRoute"></BsPreptripList>
    </j-modal>
</template>

<script>
import { httpAction } from '@/api/manage';
import pick from 'lodash.pick';
import JDate from '@/components/jeecg/JDate';
import BsPreptripList from '../BsPreptripList';
import JSearchSelectTag from '@/components/dict/JSearchSelectTag';
import { getAction, postAction } from '../../../api/manage';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import { validateRemark } from '@/utils/validate';

export default {
    name: 'BsPreptripDefineModal',
    components: {
        JDate,
        BsPreptripList,
        JSearchSelectTag,
        GCIRouteSelect
    },
    data() {
        return {
            form: this.$form.createForm(this),
            title: '操作',
            width: 1200,
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
                taskTypeId: {
                    rules: [{ required: true, message: '请选择预案类型!' }]
                },
                keytitle: {
                    rules: [
                        { required: true, message: '请输入预案名称!' },
                        { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }
                    ]
                },
                routeId: {
                    rules: [{ required: true, message: '请选择线路!' }]
                },
                ordercontent: {
                    rules: [
                        { required: true, message: '请输入下发调度内容!' },
                        { max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }
                    ]
                },
                status: {
                    rules: [{ required: true, message: '请选择状态!' }]
                }
            },
            url: {
                add: '/base/bsPreptripDefine/saveItem',
                edit: '/base/bsPreptripDefine/saveItem',
                getRouteUrl: '/common/sys/queryListRoute',
                getDictItem: '/base/bsPreptripTypeAlias/getTaskTypeList'
            },
            routeUpdate: false,
            routeName: '',
            routeList: [],
            dictOptions: []
        };
    },
    created() {},
    methods: {
        add() {
            this.edit({});
        },
        filterOption(value, option) {
            return option.componentOptions.children[0].text.indexOf(value) >= 0;
        },
        edit(record) {
            let that = this;
            if (record.tripdefineId !== undefined) {
                this.routeUpdate = true;
            }
            console.log('record==', record);
            this.form.resetFields();
            this.routeName = record.routeName;
            this.model = Object.assign({}, record);
            this.visible = true;
            this.model.status = this.model.status ? this.model.status : '1';
            this.getPreptripType();
            //this.getRoute(record.routeName);
            this.$nextTick(() => {
                this.form.setFieldsValue(
                    pick(
                        this.model,
                        'taskTypeId',
                        'taskTypeName',
                        'keytitle',
                        'organId',
                        'status',
                        'routeId',
                        'ordercontent',
                        'updateBy',
                        'updateTime',
                        'sysOrgCode',
                        'orgCode'
                    )
                );
            });
            setTimeout(() => {
                if (record.routeId) {
                    this.form.setFieldsValue({ routeId: record.routeId });
                    this.form.setFieldsValue({ ordercontent: record.ordercontent });
                    this.form.setFieldsValue({ taskTypeId: record.taskTypeId });
                }
                var routeId = this.form.getFieldValue('routeId');
                this.$refs.bsPreptripList.showList(this.model.tripdefineId, routeId);
            }, 500);
        },
        close() {
            this.$emit('close');
            this.visible = false;
            this.routeUpdate = false;
        },
        handleOk(type) {
            const that = this;
            // 触发表单验证
            this.form.validateFields((err, values) => {
                if (!err) {
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
                    formData.preptripList = that.$refs.bsPreptripList.dataSource;
                    formData.deleteIds = that.$refs.bsPreptripList.deleteIds;
                    console.log('表单提交数据', formData);
                    //空格处理
                    formData.keytitle = formData.keytitle.replaceAll('\t', '').trim();
                    formData.ordercontent = formData.ordercontent.replaceAll('\t', '').trim();
                    //预案明细校验
                    if (formData.preptripList.length < 1) {
                        that.$message.warning('请添加预案明细');
                        return false;
                    }
                    var mapArr = formData.preptripList.map(item => parseInt(item.orderNum));
                    var setArr = new Set(mapArr); //去重复
                    if (setArr.size < mapArr.length) {
                        this.$message.warning('预案明细：序号不能重复');
                        return;
                    }

                    that.confirmLoading = true;
                    httpAction(httpurl, formData, method)
                        .then(res => {
                            if (res.success) {
                                that.$message.success(res.message);
                                that.$emit('ok');
                                that.close();
                            } else {
                                that.$message.warning(res.message);
                            }
                        })
                        .finally(() => {
                            that.confirmLoading = false;
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
                    'taskTypeId',
                    'taskTypeName',
                    'keytitle',
                    'organId',
                    'status',
                    'routeId',
                    'ordercontent',
                    'updateBy',
                    'updateTime',
                    'sysOrgCode',
                    'orgCode'
                )
            );
        },
        getRoute(name) {
            getAction(this.url.getRouteUrl).then(res => {
                if (res.success) {
                    this.routeList = res.result.records;
                }
            });
        },
        keytitleChange(e) {
            var value = e.target.value || '';
            value = value.replaceAll('\t', '').trim();
            e.target.value = value;
            this.form.setFieldsValue({ ordercontent: value });
        },
        ordercontentChange(e) {
            var value = e.target.value || '';
            value = value.replaceAll('\t', '').trim();
            this.form.setFieldsValue({ ordercontent: value });
        },
        addList() {},
        checkRoute(item) {
            var routeId = this.form.getFieldValue('routeId');
            var orgCode = this.model.orgCode;
            if (routeId) {
                this.$refs.bsPreptripList.routeId = routeId;
                this.$refs.bsPreptripList.orgCode = orgCode;
                this.$refs.bsPreptripList.queryParam.tripdefineId = this.model.id;
                console.log('item===', item);
                this.$refs.bsPreptripList.handleModal(item);
            } else {
                this.$message.warning('请先选择线路');
            }
        },
        serviceTypeChange(e) {
            this.model.taskTypeId = e;
            for (let t of this.dictOptions) {
                if (e === t.value) {
                    this.model.taskTypeName = t.type;
                    break;
                }
            }
        },
        routeChange(e) {
            console.log('eeee', e);
            for (let t of this.routeList) {
                if (e === t.routeId) {
                    // this.model.organId = t.organId;
                    this.model.orgCode = t.orgCode;
                    break;
                }
            }
        },
        getPreptripType() {
            this.dictOptions = [];
            getAction(this.url.getDictItem).then(res => {
                if (res.success) {
                    this.dictOptions = res.result;
                }
            });
        }
    }
};
</script>
