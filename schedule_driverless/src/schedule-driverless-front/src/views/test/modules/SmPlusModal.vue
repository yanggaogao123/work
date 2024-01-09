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
                <a-row :gutter="24">
                    <a-col :span="24">
                        <a-form-item label="发布名称" :labelCol="{ span: 3 }" :wrapperCol="{ span: 20 }">
                            <a-input
                                v-decorator="['stationName', validatorRules.stationName]"
                                placeholder="请输入站台名（例如：景泰坑）"
                            ></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="发布类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-decorator="['stationAlias']" placeholder="请输入站台别名"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="机构名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-decorator="['shortName']" placeholder="请输入站名简称"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="前置条件" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-decorator="['voiceUrl']" placeholder="请输入报站语音地址"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="终端组" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-input v-decorator="['voiceUrl']" placeholder="请输入报站语音地址"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-date-picker
                                v-model="startValue"
                                :disabled-date="disabledStartDate"
                                show-time
                                format="YYYY-MM-DD HH:mm:ss"
                                placeholder="请选择日期时间"
                                @openChange="handleStartOpenChange"
                                style="width:100%;"
                            />
                        </a-form-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-item label="结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                            <a-date-picker
                                v-model="startValue"
                                :disabled-date="disabledStartDate"
                                show-time
                                format="YYYY-MM-DD HH:mm:ss"
                                placeholder="请选择日期时间"
                                @openChange="handleStartOpenChange"
                                style="width:100%;"
                            />
                        </a-form-item>
                    </a-col>
                </a-row>
                <div class="area-upload">
                    <ul class="table-upload">
                        <li class="tit-upload">
                            <ul>
                                <li>序号</li>
                                <li>文件名称</li>
                                <li>发送到终端路径</li>
                                <li>文件</li>
                                <li>操作</li>
                            </ul>
                        </li>
                        <li class="li-upload" v-for="(item, i) in uploadList">
                            <ul>
                                <li>{{ i + 1 }}</li>
                                <li title="用于XX终端的 xx线路文件用于XX终端的 xx线路文件">
                                    用于XX终端的 xx线路文件用于XX终端的 xx线路文件
                                </li>
                                <li title="C:\Documents\Newsletters\Sum">C:\Documents\Newsletters\Sum</li>
                                <li>上传中10%</li>
                                <li>删除</li>
                            </ul>
                        </li>
                    </ul>
                </div>
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
    name: 'SmPlusModal',
    components: {
        JDate
    },
    data() {
        return {
            form: this.$form.createForm(this),
            title: '新增任务文件',
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
            labelCol1: {
                xs: { span: 12 }
            },
            confirmLoading: false,
            validatorRules: {
                stationName: {
                    rules: [{ required: true, message: '请输入站台名称(终端)!' }]
                }
            },
            startValue: null,
            uploadList: [1, 2, 3, 4, 5, 6],
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

<style scoped lang="less">
ol,
ul {
    list-style: none;
    padding: 0;
}
li {
    list-style-type: none;
}
.area-upload {
    width: 100%;
    height: 250px;
    box-sizing: border-box;
    padding: 10px 0;
    border: 1px solid #d9d9d9;
    .table-upload {
        width: 100%;
        height: 100%;
        padding: 0;
        overflow-y: auto;
        .tit-upload,
        .li-upload {
            width: 100%;
            height: 36px;
            ul {
                display: flex;
                text-align: center;
                li:nth-child(1) {
                    flex: 1;
                }
                li:nth-child(2) {
                    flex: 3;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    width: 100%;
                }
                li:nth-child(3) {
                    flex: 3;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    width: 100%;
                }
                li:nth-child(4) {
                    flex: 1;
                }
                li:nth-child(5) {
                    flex: 1;
                }
            }
        }
    }
}
</style>
