<template>
  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="标签名">
          <a-input placeholder="请输入标签名称" v-decorator="['labelName', validatorRules.labelName]"/>
        </a-form-item>
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="标签颜色">-->
<!--          <colorPicker v-model="labelColor"/>-->
<!--          &lt;!&ndash;          <a-input placeholder="请输入标签颜色" v-decorator="['labelColor', {}]" />&ndash;&gt;-->
<!--        </a-form-item>-->

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
    import {httpAction,getAction} from '@/api/manage'
    import pick from 'lodash.pick'
    import moment from "moment"
    import store from '@/store'
    export default {
        name: "EoaMailboxCategoryModal",
        data() {
            return {
                title: "操作",
                visible: false,
                model: {},
                labelCol: {
                    xs: {span: 24},
                    sm: {span: 5},
                },
                wrapperCol: {
                    xs: {span: 24},
                    sm: {span: 16},
                },
                labelColor: "",
                confirmLoading: false,
                form: this.$form.createForm(this),
                validatorRules: {
                    labelName:{ rules:[{required: true, message: "请填写类别名称!"}, { validator: this.labelName,}]}
                },
                url: {
                    add: "/email/eoaMailboxCategory/add",
                    edit: "/email/eoaMailboxCategory/edit",
                    check:"/email/eoaMailboxCategory/check"
                },
            }
        },
        created() {
        },
        methods: {
            add() {
                this.edit({});
            },
            edit(record) {
                this.form.resetFields();
                this.model = Object.assign({}, record);
                this.visible = true;
                this.$nextTick(() => {
                    this.form.setFieldsValue(pick(this.model, 'sysOrgCode', 'userId', 'labelName', 'labelColor'))
                    //时间格式化
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
                        if (!this.model.id) {
                            httpurl += this.url.add;
                            method = 'post';
                        } else {
                            httpurl += this.url.edit;
                            method = 'put';
                        }
                        let formData = Object.assign(this.model, values);
                        //时间格式化
                        formData.userId=store.getters.userInfo.id;
                        console.log(formData)
                        httpAction(httpurl, formData, method).then((res) => {
                            if (res.success) {
                                that.$message.success(res.message);
                                that.$emit('ok');
                            } else {
                                that.$message.warning(res.message);
                            }
                        }).finally(() => {
                            that.confirmLoading = false;
                            that.close();
                        })
                    }
                })
            },
            handleCancel() {
                this.close()
            },
            labelName(rule, value, callback) {
                console.log(value)
                if (value) {
                    var param = [];
                    param.name = value;
                    param.id = this.model.id;
                    param.userId = store.getters.userInfo.id;
                    getAction(this.url.check, param).then((res) => {
                        if (res.success) {
                            callback();
                        } else {
                            callback(res.message);
                        }
                    })
                } else {
                    callback();
                }
            }
        }
    }
</script>

<style lang="less" scoped>

</style>