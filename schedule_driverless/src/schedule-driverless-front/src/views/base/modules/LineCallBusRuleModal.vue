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
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['routeId', validatorRules.routeId]"
            allowClear
            placeholder="请选择线路"
            show-search
            @change="handleSelectRoute">
            <a-select-option v-for="(item,index) in routeDataParam" :key="index" :value="item.id">
              {{ item.routeName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="方向" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            placeholder="请选择"
            v-decorator="['direction', validatorRules.direction]"
            allowClear
            show-search
            :filter-option="addFilterOption"
            @change="handleSelectDirection">
            <a-select-option v-for="(item, key) in directionOptions" :key="key" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="规则顺序号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['orderNumber', validatorRules.orderNumber]"
            placeholder="请选择"
          ></a-input>
        </a-form-item>
        <a-form-item label="支援方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择" v-decorator="['supportWay', validatorRules.supportWay]">
            <a-select-option v-for="(item, key) in supportWayOptions" :key="key" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="一键叫车站点名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            v-decorator="['routeStationId', validatorRules.routeStationId]"
            allowClear
            placeholder="请选择"
            show-search>
            <a-select-option v-for="(item,index) in stationOptions" :key="index" :value="item.routeStaId">
              {{ item.routeStationName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="叫车人数因素判断操作符" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            placeholder="请选择"
            @change="handleChangeOperator"
            v-decorator="['callBusCountOperator',validatorRules.callBusCountOperator]">
            <a-select-option v-for="(item, key) in operator" :key="key" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="起始叫车人数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['callBusCountBegin', beginFlag==true? {} :validatorRules.callBusCountBegin]"
            placeholder="请输入"
          ></a-input>
        </a-form-item>
        <a-form-item label="终止叫车人数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['callBusCountEnd', endFlag==true? {} :validatorRules.callBusCountEnd]"
            placeholder="请输入"
          ></a-input>
        </a-form-item>
        <a-form-item label="因素关系" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择" v-decorator="['factorLogical', validatorRules.factorLogical]">
            <a-select-option v-for="(item, key) in factorLogicalOptions" :key="key" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="距离站数因素判断操作符" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select
            placeholder="请选择"
            @change="handleChangeStationOperator"
            v-decorator="['stationCountOperator', validatorRules.stationCountOperator]">
            <a-select-option v-for="(item, key) in operator" :key="key" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="起始站数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['stationCountBegin', stationBeginFlag==true? {} :validatorRules.stationCountBegin]"
            placeholder="请选择"
          ></a-input>
        </a-form-item>
        <a-form-item label="终止站数" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['stationCountEnd', stationEndFlag==true? {}:validatorRules.stationCountEnd]"
            placeholder="请选择"
          ></a-input>
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input
            v-decorator="['remark']"
          ></a-input>
        </a-form-item>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import JDate from '@/components/jeecg/JDate'
  import GCIOrgTreeSelect from '@/components/gci/GCIOrgTreeSelect'

  import pick from 'lodash.pick'

  export default {
    name: 'LineCallBusRuleModal',
    components: {
      JDate, GCIOrgTreeSelect
    },
    data() {
      return {
        confirmLoading: false,
        disableSubmit: true,
        beginFlag: true,
        endFlag: true,
        stationBeginFlag: true,
        stationEndFlag: true,
        routeId: undefined,
        routeDataParam: [],
        //  方向
        directionOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '上行',
          value: '0'
        }, {
          label: '下行',
          value: '1'
        },
          {
            label: '无',
            value: '2'
          }
        ],
        //  支援方式
        supportWayOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '启用囤积车辆',
          value: '0'
        }, {
          label: '原线车辆绕行',
          value: '1'
        },
          {
            label: '无',
            value: '2'
          }
        ],
        //  因素关系
        factorLogicalOptions: [{
          label: '请选择',
          value: ''
        }, {
          label: '或',
          value: '2'
        }, {
          label: '和',
          value: '1'
        },
          {
            label: '无',
            value: '99'
          }
        ],
        // 操作符
        operator: [{
          label: '请选择',
          value: ''
        }, {
          label: '>',
          value: '1'
        }, {
          label: '>=',
          value: '2'
        },
          {
            label: '<',
            value: '3'
          },
          {
            label: '<=',
            value: '4'
          },
          {
            label: '=',
            value: '5'
          },
          {
            label: '[A,B]',
            value: '6'
          },
          {
            label: '(A,B)',
            value: '7'
          },
          {
            label: '无',
            value: '99'
          }
        ],
        stationOptions: [],
        form: this.$form.createForm(this),
        title: '操作',
        width: 700,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 10 }
        },
        validatorRules: {
          routeId: {
            rules: [
              {
                /* 必填校验，数字，长度32位以内，空格 */
                required: true, message: '请选择线路'
              }
            ]
          },
          direction: {
            rules: [{
              /* 必填校验 */
              required: true, message: '请选择方向'
            }
            ]
          },
          orderNumber: {
            rules: [{
              required: true, message: '请输入规则顺序号'
            }
            ]
          },
          supportWay: {
            rules: [{
              required: true, message: '请选择支援方式'
            }
            ]
          },
          routeStationId: {
            rules: [{
              required: true, message: '请选择一键叫车站点'
            }
            ]
          },
          callBusCountOperator: {
            rules: [{
              required: true, message: '请选择叫车人数因素判断操作符'
            }
            ]
          },
          callBusCountBegin: {
            rules: [{
              required: true, message: '请输入起始叫车人数'
            }
            ]
          },
          callBusCountEnd: {
            rules: [{
              required: true, message: '请输入终止叫车人数'
            }
            ]
          },
          factorLogical: {
            rules: [{
              required: true, message: '请选择因素关系'
            }
            ]
          },
          stationCountOperator: {
            rules: [{
              required: true, message: '请选择距离站数因素判断操作符'
            }
            ]
          },
          stationCountBegin: {
            rules: [{
              required: true, message: '请输入起始站数'
            }
            ]
          },
          stationCountEnd: {
            rules: [{
              required: true, message: '请输入终止站数'
            }
            ]
          }
        },
        url: {
          add: '/base/lineCallBusRule/add',
          edit: '/base/lineCallBusRule/edit',
          routeStationList: '/base/lineCallBusRule/routeStationList'
        }
      }
    },
    methods: {
      add(record, routeData, operateType) {
        this.edit(record, routeData, operateType)
      },
      edit: function(record, routeData, operateType) {
        this.stationOptions = []
        this.form.resetFields()
        if (operateType === 'edit') {
          this.routeId = record.routeId
          let params = { direction: record.direction, routeId: record.routeId }
          getAction(this.url.routeStationList, params).then((res) => {
            if (res.success) {
              this.stationOptions = res.result
            }
          })
          this.handleChangeOperator(record.callBusCountOperator)
          this.handleChangeStationOperator(record.stationCountOperator)
        }
        this.routeDataParam = routeData
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'routeId', 'direction', 'orderNumber', 'supportWay', 'routeStationId', 'callBusCountOperator',
            'callBusCountBegin', 'callBusCountEnd', 'factorLogical', 'stationCountOperator', 'stationCountBegin', 'stationCountEnd', 'remark'))
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
            } else {
              httpurl += this.url.edit
              method = 'post'
            }
            let formData = Object.assign(this.model, values)
            console.log('表单提交数据', formData)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.confirmLoading = false
                that.close()
              } else {
                if (res.code === 501) {
                  that.confirmLoading = false
                  that.disableSubmit = false
                  that.$message.warning(res.message)
                } else {
                  that.confirmLoading = false
                  that.$message.warning(res.message)
                }
              }
            }).catch(() => {
              that.confirmLoading = false
            })
          }
        })
      },
      handleCancel() {
        this.close()
      },
      handleSelectRoute(value) {
        this.form.setFieldsValue({ routeStationId: undefined })
        this.stationOptions = []
        let direction = this.form.getFieldValue('direction')
        let params = { routeId: value, direction: direction }
        getAction(this.url.routeStationList, params).then((res) => {
          if (res.success) {
            this.stationOptions = res.result
          }
        })
      },
      handleSelectDirection(value) {
        this.form.setFieldsValue({ routeStationId: undefined })
        this.stationOptions = []
        let routeId = this.form.getFieldValue('routeId')
        let params = { direction: value, routeId: routeId }
        getAction(this.url.routeStationList, params).then((res) => {
          if (res.success) {
            this.stationOptions = res.result
          }
        })
      },
      handleChangeOperator(value) {
        // 控制起始叫车人数是否必填
        if (value === '' || value === '99') {
          this.beginFlag = true
          let begin = this.form.getFieldValue('callBusCountBegin')
          if (begin === '' || begin === undefined) {
            this.form.setFieldsValue({ callBusCountBegin: '' })
          }
        } else {
          this.beginFlag = false
        }
        // 控制终止叫车人数是否必填
        if (value === '6' || value === '7') {
          this.endFlag = false
        } else {
          this.endFlag = true
          let end = this.form.getFieldValue('callBusCountEnd')
          if (end === '' || end === undefined) {
            this.form.setFieldsValue({ callBusCountEnd: '' })
          }
        }
      },
      handleChangeStationOperator(value) {
        // 控制起始站数是否必填
        if (value === '' || value === '99') {
          this.stationBeginFlag = true
          let begin = this.form.getFieldValue('stationCountBegin')
          if (begin === '' || begin === undefined) {
            this.form.setFieldsValue({ stationCountBegin: '' })
          }
        } else {
          this.stationBeginFlag = false
        }
        // 控制终止站数是否必填
        if (value === '6' || value === '7') {
          this.stationEndFlag = false
        } else {
          this.stationEndFlag = true
          let end = this.form.getFieldValue('stationCountEnd')
          if (end === '' || end === undefined) {
            this.form.setFieldsValue({ stationCountEnd: '' })
          }
        }
      },
      addFilterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      }
    }
  }
</script>
