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
        <a-form-item label="所属机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <GCIOrgTreeSelect
            v-decorator="['orgCode', validatorRules.orgCode]"
            placeholder="请选择所属机构"
          ></GCIOrgTreeSelect>
        </a-form-item>

        <!--    SIM卡晶片号码    -->
        <a-form-item label="IMSI识别码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['simChipCode', validatorRules.simChipCode]"
            placeholder="请输入15位数字及字母IMSI识别码"
            v-model="simChipCode"
            @change="checkRepeatIMSI"
          ></a-input>
        </a-form-item>

        <!--    SIM卡编码    -->
        <a-form-item label="ICCID" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['simCode', validatorRules.simCode]"
            placeholder="请输入11-21位数字及字母ICCID"
            v-model="simCode"
            @change="checkRepeatICCID"
          ></a-input>
        </a-form-item>

        <!--  SIM卡号码   -->
        <a-form-item label="手机号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['phoneNumber', validatorRules.phoneNumber]"
            placeholder="请输入手机号码"
            v-model="phoneNumber"
            @change="checkRepeatPN"
            oninput="if(value.length>11)value=value.slice(0,11)"
          ></a-input>
        </a-form-item>

        <a-form-item label="是否启用" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag
            v-decorator="['isActive', validatorRules.isActive]"
            :triggerChange="true"
            placeholder="请选择是否启用"
            dictCode="is_active"
          />
        </a-form-item>
        <a-form-item label="SIM卡状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag
            v-decorator="['simStatus', validatorRules.simStatus]"
            :triggerChange="true"
            placeholder="请选择SIM卡状态"
            dictCode="sim_status"
          />
        </a-form-item>

        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['remark', validatorRules.remark]"></a-input>
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import pick from 'lodash.pick'
import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'
import { validateRemark, isNumbersAndLetters } from '@/utils/validate'

export default {
  name: 'BsSimModal',
  components: { GCIOrgTreeSelect },
  data() {
    return {
      phoneNumber: '',
      simCode: '',
      simChipCode: '',
      disableSubmit: true,
      updateId: '',
      orgTree: [],
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
        simCode: {
          rules: [
            { required: true, message: '请输入ICCID!' },
            { validator: isNumbersAndLetters },
            { max: 21, min: 11, message: 'ICCID长度在11-21位之间' }
          ]
        },
        simChipCode: {
          rules: [
            { required: true, message: '请输入IMSI识别码!' },
            { validator: isNumbersAndLetters },
            { max: 15, message: 'IMSI长度最大为15位' }
          ]
        },
        orgCode: {
          rules: [{ required: true, message: '请输入机构!' }]
        },
        isActive: {
          rules: [{ required: true, message: '请输入是否启用!' }],
          initialValue: '1'
        },
        phoneNumber: {
          rules: [{ required: false, message: '请输入手机号码!' }, { validator: this.validatePhone }]
        },
        simStatus: {
          rules: [{ required: false, message: '请选择状态!' }],
          initialValue: '0'
        },
        remark: {
          rules: [{ max: 50, message: '长度最大为 50 个字符', trigger: 'blur' }, { validator: validateRemark }]
        }
      },
      url: {
        add: '/base/bsSim/add',
        edit: '/base/bsSim/edit',
        checkQuote: '/base/bsSim/checkQuote',
        checkUpdate: '/base/bsSim/checkUpdate',
        cleanUpdateSign: '/base/bsSim/cleanUpdateSign',
        checkRepeat: '/base/bsSim/checkRepeat'
      }
    }
  },
  methods: {
    checkRepeatPN() {
      if (this.phoneNumber.length === 11) {
        getAction(this.url.checkRepeat, { phoneNumber: this.phoneNumber, simId: this.updateId }).then(res => {
          if (res.success && res.result !== null) {
            this.$message.warning('手机号码已经存在')
          }
        })
      }
    },
    checkRepeatIMSI() {
      if (this.simChipCode.length === 15) {
        getAction(this.url.checkRepeat, { simChipCode: this.simChipCode, simId: this.updateId }).then(res => {
          if (res.success && res.result !== null) {
            this.$message.warning('IMSI已经存在')
          }
        })
      }
    },
    checkRepeatICCID() {
      if (this.simCode.length >= 11 && this.simCode.length <= 21) {
        getAction(this.url.checkRepeat, { simCode: this.simCode, simId: this.updateId }).then(res => {
          if (res.success && res.result !== null) {
            this.$message.warning('ICCID已经存在')
          }
        })
      }
    },
    validatePhone(rule, value,callback) {
      if(value==this.model.phoneNumber) {
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
    add() {
      this.edit({})
    },
    edit(record) {
      if (!record.id) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(
            pick(this.model, 'orgCode', 'phoneNumber', 'simCode', 'simChipCode', 'isActive', 'simStatus', 'remark')
          )
        })
      } else {
        //校验是否正在修改
        this.updateId = record.id
        getAction(this.url.checkUpdate, { id: record.id }).then(res => {
          if (res.success) {
            this.form.resetFields()
            this.model = Object.assign({}, record)
            this.visible = true
            this.$nextTick(() => {
              this.form.setFieldsValue(
                pick(this.model, 'orgCode', 'phoneNumber', 'simCode', 'simChipCode', 'isActive', 'simStatus', 'remark')
              )
            })
          } else {
            this.$message.warning(res.message)
          }
        })
      }
    },
    close() {
      getAction(this.url.cleanUpdateSign, { id: this.updateId }).then(res => {
        if (res.success) {
        } else {
          this.$message.warning(res.message)
        }
      })
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          this.disableSubmit = true
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
            let formData = Object.assign(this.model, values)
            console.log('表单提交数据', formData)
            httpAction(httpurl, formData, method)
              .then(res => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.$emit('ok')
                  that.close()
                } else {
                  if (res.code === 501) {
                    that.$message.warning(res.message)
                  } else {
                    that.$message.warning(res.message)
                  }
                }
              })
              .finally(() => {
                that.confirmLoading = false
                that.disableSubmit = false
              })
          } else {
            httpurl += this.url.edit
            method = 'put'
            //校验是否被其他模块引用
            getAction(this.url.checkQuote, { simCode: this.model.simCode })
              .then(res => {
                if (res.success) {
                  if (res.message !== '') {
                    this.$confirm({
                      title: res.message,
                      content: '是否更改该sim卡以及被引用的模块?',
                      onOk: () => {
                        let formData = Object.assign(this.model, values)
                        console.log('表单提交数据', formData)
                        httpAction(httpurl, formData, method)
                          .then(res => {
                            if (res.success) {
                              that.$message.success(res.message)
                              that.$emit('ok')
                              that.close()
                            } else {
                              that.$message.warning(res.message)
                            }
                          })
                          .finally(() => {
                            that.confirmLoading = false
                            that.disableSubmit = false
                          })
                      },
                      onCancel: () => {
                        that.confirmLoading = false
                        that.disableSubmit = false
                      }
                    })
                  } else {
                    let formData = Object.assign(this.model, values)
                    console.log('表单提交数据', formData)
                    httpAction(httpurl, formData, method)
                      .then(res => {
                        if (res.success) {
                          that.$message.success(res.message)
                          that.$emit('ok')
                          that.close()
                        } else {
                          that.$message.warning(res.message)
                        }
                      })
                      .finally(() => {
                        that.confirmLoading = false
                        that.disableSubmit = false
                      })
                  }
                } else {
                  this.$message.warning(res.message)
                }
              })
              .finally(() => {
                that.confirmLoading = false
                that.disableSubmit = false
              })
          }
        }
      })
    },
    handleCancel() {
      this.confirmLoading = false
      this.close()
    },
    popupCallback(row) {
      this.form.setFieldsValue(
        pick(row, 'isActive', 'organId', 'phoneNumber', 'remark', 'simChipCode', 'simCode', 'simStatus')
      )
    }
  }
}
</script>
