<template>
    <div v-if="visible_edit">
        <j-modal
            :title="title"
            :width="width"
            :visible="visible_edit"
            :confirmLoading="confirmLoading"
            :maskClosable="false"
            switchFullscreen
            @ok="handleOk"
            @cancel="handleEditCancel"
            cancelText="关闭"
        >
            <a-spin :spinning="confirmLoading">
                <a-form :form="edit_form">
                    <a-form-item label="用户" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <j-select-multi-user-new
                            disabled
                            v-decorator="['userName', validatorRules.userName]"
                            placeholder="请点击选择用户"
                        />

                        <!-- <j-select-multi-user
                            disabled
                            v-decorator="['userName', validatorRules.userName]"
                            placeholder="请点击选择用户"
                        /> -->
                    </a-form-item>
                    <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol" disabled>
                        <!-- <GCIRouteSelect
              v-decorator="['routeId', validatorRules.routeIds]"
              placeholder="请选择线路"
              mode="multiple"
              :width="500"
              :value="model.routeId"
              item="id"
            ></GCIRouteSelect> -->
                        <GCIRouteSelectNew
                            v-decorator="['routeId', validatorRules.routeIds]"
                            placeholder="请选择线路"
                            mode="multiple"
                            :width="500"
                            @receive="receiveSth"
                            disabled
                        ></GCIRouteSelectNew>
                    </a-form-item>
                    <a-form-item label="查询" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <a-switch
                            v-decorator="[
                                'isQuery',
                                { rules: [{ required: true, message: '请选择' }], valuePropName: 'checked' }
                            ]"
                            checked-children="开"
                            un-checked-children="关"
                            @change="onQueryChange"
                        />
                    </a-form-item>
                    <a-form-item label="调度" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <a-switch
                            v-decorator="[
                                'isDispatch',
                                { rules: [{ required: true, message: '请选择' }], valuePropName: 'checked' }
                            ]"
                            checked-children="开"
                            un-checked-children="关"
                            @change="onDispatchChange"
                        />
                    </a-form-item>
                </a-form>
            </a-spin>
        </j-modal>
    </div>

    <div v-else-if="visible">
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
                    <a-form-item label="用户" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <!-- <j-select-multi-user
                            v-decorator="['userName', validatorRules.userName]"
                            placeholder="请点击选择用户"
                        /> -->
                        <j-select-multi-user-new
                            v-decorator="['userName', validatorRules.userName]"
                            placeholder="请点击选择用户"
                        />
                    </a-form-item>
                    <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <!-- <GCIRouteSelect
              v-decorator="['routeIds', validatorRules.routeIds]"
              placeholder="请选择线路"
              mode="multiple"
              :width="500"
              :value="model.routeId"
              item="id"
            ></GCIRouteSelect>
            GCIRouteSelectNew -->
                        <GCIRouteSelectNew
                            v-decorator="['routeIds', validatorRules.routeIds]"
                            placeholder="请选择线路"
                            mode="multiple"
                            :width="500"
                            item="id"
                            :send="sendSth"
                            @receive="receiveSth"
                            @receiveName="receiveName"
                        >
                        </GCIRouteSelectNew>
                    </a-form-item>
                    <a-form-item label="查询" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <a-switch
                            v-decorator="[
                                'isQuery',
                                {
                                    rules: [{ required: true, message: '请选择' }],
                                    initialValue: true,
                                    valuePropName: 'checked'
                                }
                            ]"
                            checked-children="开"
                            un-checked-children="关"
                            @change="onQueryChange"
                        />
                    </a-form-item>
                    <a-form-item label="调度" :labelCol="labelCol" :wrapperCol="wrapperCol">
                        <a-switch
                            v-decorator="[
                                'isDispatch',
                                {
                                    rules: [{ required: true, message: '请选择' }],
                                    initialValue: true,
                                    valuePropName: 'checked'
                                }
                            ]"
                            checked-children="开"
                            un-checked-children="关"
                            @change="onDispatchChange"
                        />
                    </a-form-item>
                </a-form>
            </a-spin>
        </j-modal>
    </div>
</template>
<script>
import { httpAction } from '@/api/manage';
import pick from 'lodash.pick';
import { validateDuplicateValue } from '@/utils/util';
import JSearchSelectTag from '@/components/dict/JSearchSelectTag';
import AFormItem from 'ant-design-vue/es/form/FormItem';
import JMultiSelectTag from '@/components/dict/JMultiSelectTag';
import JSelectMultiUser from '@/components/jeecgbiz/JSelectMultiUser';
import JSelectMultiUserNew from '@/components/jeecgbiz/JSelectMultiUserNew';
import GCIRouteSelect from '@/components/gci/GCIRouteSelect';
import GCIRouteSelectNew from '@/components/gci/GCIRouteSelectNew';

export default {
    name: 'PriUserRouteModal',
    components: {
        AFormItem,
        JSearchSelectTag,
        JMultiSelectTag,
        JSelectMultiUser,
        JSelectMultiUserNew,
        GCIRouteSelect,
        GCIRouteSelectNew
    },
    data() {
        return {
            disableSubmit: true,
            edit_form: this.$form.createForm(this),
            form: this.$form.createForm(this),
            title: '操作',
            width: 800,
            visible: false,
            visible_edit: false,
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
                userName: {
                    rules: [{ required: true, message: '请选择用户!' }]
                },
                routeIds: {
                    rules: [{ required: true, message: '请选择线路!' }]
                },
                routeName: {
                    rules: [{ required: true, message: '请选择线路!' }]
                }
            },
            url: {
                add: '/routeuser/priUserRoute/add',
                edit: '/routeuser/priUserRoute/edit'
            },
            queryStatus: true,
            dispatchtatus: true,
            receiveRouteId: '',
            sendSth: {}
        };
    },
    created() {},
    methods: {
        onQueryChange(checked) {
            // console.log(`a-switch to ${checked}`);
            this.queryStatus = checked;
            console.log('queryStatus', checked);
        },
        onDispatchChange(checked) {
            // console.log(`a-switch to ${checked}`);
            this.dispatchtatus = checked;
            console.log('dispatchtatus', checked);
        },
        add() {
            let default_recode = { isQuery: true, isDispatch: true };
            this.form.resetFields();
            this.model = Object.assign({}, default_recode);
            this.visible = true;
            this.$nextTick(() => {
                this.form.setFieldsValue(
                    pick(this.model, 'userName', 'routeId', 'isDispatch', 'isQuery', 'createTime', 'createBy')
                );
            });
        },
        edit(record) {
            console.log('编辑record', record);

            this.edit_form.resetFields();
            this.model = Object.assign({}, record);
            this.sendSth = this.model;
            if (record.authorize) {
                this.model.isDispatch = record.authorize.indexOf('3') > -1;
                this.model.isQuery = record.authorize.indexOf('0') > -1;
            } else {
                this.model.isDispatch = false;
                this.model.isQuery = false;
            }

            this.model.userName = record.accountName;
            console.log(this.model);
            this.visible_edit = true;
            this.$nextTick(() => {
                console.log('当前数据模型：' + JSON.stringify(this.model));
                this.edit_form.setFieldsValue(
                    pick(this.model, 'userName', 'routeId', 'isDispatch', 'isQuery', 'createTime', 'createBy')
                );
            });
            this.form = this.edit_form;
        },
        close() {
            this.$emit('close');
            this.visible = false;
        },
        closeEdit() {
            this.$emit('close');
            this.visible_edit = false;
        },
        handleOk() {
            const that = this;
            // 触发表单验证
            this.form.validateFields((err, values) => {
                console.log(('表单验证数据', values));
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
                    formData.isQuery = that.queryStatus;
                    formData.isDispatch = that.dispatchtatus;
                    if (!formData.routeIds) {
                        formData.routeIds = formData.routeId;
                    }

                    if (this.receiveRouteId) {
                        // let str = this.receiveRouteId.split(',')
                        formData.routeId = this.receiveRouteId;
                        formData.routeIds = this.receiveRouteId;
                    }
                    console.log('表单提交数据', formData);
                    httpAction(httpurl, formData, method)
                        .then(res => {
                            if (res.success) {
                                that.$message.success(res.message);
                                that.$emit('ok');
                                that.confirmLoading = false;
                                that.close();
                                that.closeEdit();
                            } else {
                                if ('数据库中已存在该记录' === res.message) {
                                    that.confirmLoading = false;
                                    this.disableSubmit = false;
                                    that.$message.warning(res.message);
                                } else {
                                    that.confirmLoading = false;
                                    that.$message.warning(res.message);
                                    that.close();
                                }
                            }
                        })
                        .catch(() => {
                            that.confirmLoading = false;
                            if (!this.model.id) {
                                that.close();
                            } else {
                                that.closeEdit();
                            }
                        });
                }
            });
        },
        handleCancel() {
            this.close();
        },
        handleEditCancel() {
            this.closeEdit();
        },
        popupCallback(row) {
            this.form.setFieldsValue(
                pick(row, 'userName', 'routeId', 'isDispatch', 'isQuery', 'createTime', 'createBy')
            );
        },
        receiveSth(data) {
            console.log(data);
            this.receiveRouteId = data.toString();

            // this.form.setFieldsValue({ routeName: '123路' }, { routeName: '123路' });
        },
        receiveName(data) {
            console.log(data);
        }
        // getValueQuery(value, prevValue, allValues) {
        //     if (!value && allValues.isDispatch) {
        //         allValues.isDispatch = false;
        //         let model = Object.assign({}, allValues);
        //         allValues.userId_dictText = allValues.userId_dictText == null ? null : allValues.userId_dictText
        //         allValues.routeId = allValues.routeId == null ? null : allValues.routeId
        //         console.log("当前数据模型：" + JSON.stringify(this.model));
        //         this.form.setFieldsValue(pick(model, 'userId_dictText', 'routeId', 'isDispatch', 'isQuery'))
        //     }
        //     return value == 1;
        // }, getValueDispatch(value, prevValue, allValues) {
        //     if (value && !allValues.isQuery) {
        //         allValues.isQuery = true;
        //         let model = Object.assign({}, allValues);
        //         allValues.userId_dictText = allValues.userId_dictText == null ? null : allValues.userId_dictText
        //         allValues.routeId = allValues.routeId == null ? null : allValues.routeId
        //         console.log("当前数据模型：" + JSON.stringify(this.model));
        //         this.form.setFieldsValue(pick(model, 'userId_dictText', 'routeId', 'isDispatch', 'isQuery'))
        //     }
        //     return value == 1;
        // }
    }
};
</script>
