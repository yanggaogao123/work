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
                <a-form-item label="任务名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input
                        v-decorator="['stationName', validatorRules.stationName]"
                        placeholder="请输入站台名（例如：景泰坑）"
                    ></a-input>
                </a-form-item>
                <a-form-item label="机构名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input v-decorator="['stationAlias']" placeholder="请输入站台别名"></a-input>
                </a-form-item>
                <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input v-decorator="['shortName']" placeholder="请输入站名简称"></a-input>
                </a-form-item>
                <a-form-item label="终端组" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input v-decorator="['voiceUrl']" placeholder="请输入报站语音地址"></a-input>
                </a-form-item>
                <a-form-item label="文件开始生效时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-date-picker
                        v-model="startValue"
                        :disabled-date="disabledStartDate"
                        show-time
                        format="YYYY-MM-DD HH:mm:ss"
                        placeholder="请选择日期时间"
                        @openChange="handleStartOpenChange"
                    />
                </a-form-item>
                <a-form-item label="任务下发开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-date-picker
                        v-model="startValue"
                        :disabled-date="disabledStartDate"
                        show-time
                        format="YYYY-MM-DD HH:mm:ss"
                        placeholder="请选择日期时间"
                        @openChange="handleStartOpenChange"
                    />
                </a-form-item>
                <a-form-item label="任务下发截止时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-date-picker
                        v-model="startValue"
                        :disabled-date="disabledStartDate"
                        show-time
                        format="YYYY-MM-DD HH:mm:ss"
                        placeholder="请选择日期时间"
                        @openChange="handleStartOpenChange"
                    />
                </a-form-item>
            </a-form>
        </a-spin>
    </j-modal>
</template>

<script>
import { httpAction } from '@/api/manage';
import pick from 'lodash.pick';
import { validateDuplicateValue } from '@/utils/util';
import JDate from '@/components/jeecg/JDate';
import moment from 'moment';

export default {
    name: 'FmPlusModal',
    components: {
        JDate
    },
    data() {
        return {
            form: this.$form.createForm(this),
            title: '新增线路文件',
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
                stationName: {
                    rules: [{ required: true, message: '请输入站台名称(终端)!' }]
                }
            },
            startValue: null,
            url: {
                add: '/base/bsStationName/add',
                edit: '/base/bsStationName/edit'
            }
        };
    },
    created() {},
    methods: {
        moment,
        add() {
            this.edit({});
        },
        edit(record) {
            this.form.resetFields();
            console.log(record);
            this.model = Object.assign({}, record);
            this.visible = true;
            this.$nextTick(() => {
                this.form.setFieldsValue(
                    pick(
                        this.model,
                        'createBy',
                        'createTime',
                        'updateBy',
                        'updateTime',
                        'sysOrgCode',
                        'orgCode',
                        'stationNameId',
                        'version',
                        'createUser',
                        'dateCreated',
                        'lastUpdated',
                        'remark',
                        'shortName',
                        'stationName',
                        'voiceUrl',
                        'ptStationName',
                        'enStationName',
                        'stationAlias'
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
                    that.confirmLoading = true;
                    let httpurl = '';
                    let method = '';
                    console.log(this.model);
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
                            } else {
                                that.$message.warning(res.message);
                            }
                        })
                        .finally(() => {
                            that.confirmLoading = false;
                            that.close();
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
                    'createBy',
                    'createTime',
                    'updateBy',
                    'updateTime',
                    'sysOrgCode',
                    'orgCode',
                    'stationNameId',
                    'version',
                    'createUser',
                    'dateCreated',
                    'lastUpdated',
                    'remark',
                    'shortName',
                    'stationName',
                    'voiceUrl',
                    'ptStationName',
                    'enStationName',
                    'stationAlias'
                )
            );
        }
    }
};
</script>
