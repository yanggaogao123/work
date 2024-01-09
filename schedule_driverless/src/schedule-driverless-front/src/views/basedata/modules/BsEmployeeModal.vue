<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    :okButtonProps="{ props: {disabled: disableSubmit} }"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="所属机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <GCIOrgTreeSelect  @change="changeOrg" v-decorator="['organId', validatorRules.organId]" placeholder="请选择所属机构" :selectType="'id'" :isGetOption="true"></GCIOrgTreeSelect>
        </a-form-item>

        <a-form-item label="所属线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            allowClear
            v-decorator="['routeId', validatorRules.routeId]"
            placeholder="请输入线路名搜索"
            show-search
            :filter-option="false" >
            <a-select-option v-for="(item,index) in routeList" :key="index" :value="item.routeId">
              <span style="display: inline-block;width: 100%" :title=" item.routeName ">
                {{ item.routeName }}
              </span>
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="员工姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['employeeName', validatorRules.employeeName]"
            placeholder="请输入员工姓名"
          ></a-input>
        </a-form-item>
        <a-form-item label="资格证号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['qualification', validatorRules.qualification]"
            placeholder="请输入资格证号"
          ></a-input>
        </a-form-item>
        <a-form-item label="员工类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag
            v-decorator="['employeeType', { initialValue: '0' }]"
            :triggerChange="true"
            placeholder="请选择员工类别"
            dictCode="base_employeeType"
          />
        </a-form-item>
        <a-form-item label="员工工号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['employeeCode', validatorRules.employeeCode]"
            placeholder="请输入员工工号"
          ></a-input>
        </a-form-item>
        <a-form-item label="邮编" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['postCode', validatorRules.postCode]"
            placeholder="请输入邮编"
          ></a-input>
        </a-form-item>

        <a-form-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag
            v-decorator="['sex', { initialValue: 1 }]"
            :triggerChange="true"
            placeholder="请选择员工性别"
            dictCode="base_sex"
          />
        </a-form-item>

        <a-form-item label="生日" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date
            placeholder="请选择生日"
            v-decorator="['birthDay']"
            :trigger-change="true"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="身份证" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['identityCard', validatorRules.identityCard]"
            placeholder="请输入身份证"
          ></a-input>
        </a-form-item>
        <a-form-item label="联系地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['contactAddress',validatorRules.contactAddress]"
            placeholder="请输入联系地址"
          ></a-input>
        </a-form-item>
        <a-form-item label="住址" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['dwellingPlace',validatorRules.dwellingPlace]" placeholder="请输入住址"></a-input>
        </a-form-item>
        <a-form-item label="电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['mobile',validatorRules.mobile]" placeholder="请输入电话"></a-input>
        </a-form-item>
        <a-form-item label="手机" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['telephone', validatorRules.telephone]"
            placeholder="请输入手机"
          ></a-input>
        </a-form-item>
        <a-form-item label="邮件" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['email', validatorRules.email]"
            placeholder="请输入邮件"
          ></a-input>
        </a-form-item>
        <a-form-item label="记录状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag
            v-decorator="['recordStatus', { initialValue: '0' }]"
            :triggerChange="true"
            placeholder="请选择记录状态"
            disabled
            dictCode="employee_record_type"
          />
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-textarea
            v-decorator="['remark', validatorRules.remark]"
            rows="4"
            placeholder="请输入备注"
          />
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
    import { httpAction, getAction } from '@/api/manage';
    import pick from 'lodash.pick';
    import JDate from '@/components/jeecg/JDate';
    import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect';
    import { validateText, validateRemark, validateNumber} from '@/utils/validate'
    import GCIRouteSelect from '@/components/gci/GCIRouteSelect'

    export default {
        name: 'BsEmployeeModal',
        components: {
            JDate, GCIOrgTreeSelect, GCIRouteSelect
        },
        data() {
            return {
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
                    email: {
                        rules: [
                            { max: 60, message: '邮箱地址超过60位了' },
                            {
                                pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/,
                                message: '请输入正确的电子邮件!'
                            }
                        ]
                    },
                    organId: {
                        rules: [{ required: true, message: '请选择所属机构!' }]
                    },
                    qualification: {
                        rules: [
                            { required: true, message: '请输入资格证号!' },
                            { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' },
                            { validator: validateText }
                        ]
                    },
                    employeeCode: {
                        rules: [
                            { required: true, message: '请输入员工编码!' },
                            { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' },
                            { validator: validateText }
                        ]
                    },
                    employeeName: {
                        rules: [
                            { required: true, message: '请输入员工姓名!' },
                            { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' },
                            { validator: validateRemark }
                        ]
                    },
                    identityCard: {
                        rules: [
                            { validator: this.validateIdNo }
                        ]
                    },
                    telephone: {
                        rules: [{ validator: this.validatePhone }]
                    },
                    remark: {
                        rules: [{max: 50, message: '长度在 0 到 50 个字符', trigger: 'blur' },
                            { validator: validateRemark }]
                    },
                    dwellingPlace: {
                        rules: [{ max: 30, message: '长度在 0 到 30 个字符', trigger: 'blur' },
                            { validator: validateRemark }]
                    },
                    postCode: {
                        rules: [{max: 6, message: '长度为6个数字', trigger: 'blur' },
                            { validator: validateNumber }]
                    },
                    contactAddress: {
                        rules: [{ max: 30, message: '长度在 0 到 30 个字符', trigger: 'blur' },
                            { validator: validateRemark }]
                    },
                    mobile: {
                        rules: [
                            { validator: this.validateMobile }
                        ]
                    }
                },
                url: {
                  add: '/base/employee/saveEmployee',
                  edit: '/base/employee/saveEmployee',
                  getRouteUrl: '/baseservice/bsRoute/getRouteList'
                },
                treeData: [],
                routeList: []
            };
        },
        created() {
            // this.initOrganInfo();
        },
        methods: {
          changeOrg(organId) {
            // 查询线路
            let param = {
              organRunId: organId,
              pageNo: 1,
              pageSize: 1000
            }
            this.routeList = [];
            this.form.setFieldsValue({'routeId':''});
            getAction(this.url.getRouteUrl, param).then((res) => {
              if (res.success) {
                this.routeList = res.result.records;
              }
            })
          },
            add() {
                this.edit({});
            },
            edit(record) {
                this.form.resetFields();
                this.changeOrg(record.organId)
                this.model = Object.assign({}, record);
                this.visible = true;
                this.$nextTick(() => {
                    this.form.setFieldsValue(
                        pick(
                            this.model,
                            'birthDay',
                            'contactAddress',
                            'dateCreated',
                            'dwellingPlace',
                            'email',
                            'employeeCode',
                            'employeeName',
                            'employeeType',
                            'identityCard',
                            'lastUpdated',
                            'mobile',
                            'organId',
                            'photo',
                            'postCode',
                            'qualification',
                            'remark',
                            'sex',
                            'telephone',
                            'writeOffMan',
                            'writeOffTime',
                            'isRetirement',
                            'recordStatus'
                        )
                    );
                    if (this.model.routeId) {
                        this.form.setFieldsValue({ routeId: this.model.routeId });
                    }
                    if (this.model.orgCode) {
                        this.form.setFieldsValue({ orgCode: this.model.orgCode + '' });
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
                        formData.recordStatus = '0'

                      console.log('表单提交数据', formData);
                      httpAction(httpurl, formData, method)
                        .then((res) => {
                          if (res.success) {
                            that.$message.success(res.message);
                            that.$emit('ok');
                            that.confirmLoading = false;
                            that.close();
                          } else {
                            that.confirmLoading = false;
                            this.disableSubmit = false;
                            that.$message.warning(res.message);
                          }
                        }).catch(() => {
                        that.confirmLoading = false;
                        that.close();
                      })
                    }
                });
            },
            handleCancel() {
                this.close();
            },
            handleChange(value) {
                console.log(value);
                this.model.routeId = value;
            },
            popupCallback(row) {
                this.form.setFieldsValue(
                    pick(
                        row,
                        'birthDay',
                        'contactAddress',
                        'dateCreated',
                        'dwellingPlace',
                        'email',
                        'employeeCode',
                        'employeeName',
                        'employeeType',
                        'identityCard',
                        'lastUpdated',
                        'mobile',
                        'orgCode',
                        'photo',
                        'postCode',
                        'qualification',
                        'remark',
                        'sex',
                        'telephone',
                        'writeOffMan',
                        'writeOffTime',
                        'isRetirement',
                        'status'
                    )
                );
            },
            validatePhone(rule, value,callback) {
              if(value==this.model.telephone) {
                callback();
              }
              const reg =/^[1][3-9][0-9]{9}$/;
              if(value==''||value==undefined||value==null){
                callback();
              }else {
                if ((!reg.test(value)) && value != '') {
                  callback(new Error('请输入正确的手机号码'));
                } else {
                  callback();
                }
              }
            },
            validateMobile(rule, value,callback) {
              if(value==this.model.mobile) {
                callback();
              }
              const reg =/^(0\d{2,3}-)?\d{7,8}$/;
              if(value==''||value==undefined||value==null){
                callback();
              }else {
                if ((!reg.test(value)) && value != '') {
                  callback(new Error('请输入正确的电话号码'));
                } else {
                  callback();
                }
              }
            },
            validateIdNo(rule, value,callback) {
              if(value==this.model.identityCard) {
                callback();
              }
              const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
              if(value==''||value==undefined||value==null){
                callback();
              }else {
                if ((!reg.test(value)) && value != '') {
                  callback(new Error('请输入正确的身份证号码'));
                } else {
                  callback();
                }
              }
            }
        },
    };
</script>
