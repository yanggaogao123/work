<template>
  <j-modal
    :title="title"
    :width="1500"
    :style="{top:'10px'}"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="名称">
              <a-input placeholder="请输入名称" v-decorator="['name', { rules: [{ required: true, message: '名称不允许为空!' }] }]" allowClear/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="位置">
              <a-input placeholder="请输入位置" v-decorator="['position', {rules: [{ required: true, message: '位置不允许为空!' }]}]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="规模">
              <j-dict-select-tag v-decorator="['scale', {initialValue:'S'}]"  dictCode="meeting_scale" placeholder="请选择规模" :triggerChange="true" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="类型">
              <a-select v-decorator="[ 'type', {initialValue:'common'}]" placeholder="请选择类型">
                <a-select-option value="common">普通会议室</a-select-option>
                <a-select-option value="temp">临时会议室</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="容纳人数">
              <a-input-number v-decorator="[ 'peopleNumber', {}]"  style="width:100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="管理部门">
              <j-select-depart v-model="departId" :multi="true"></j-select-depart>
              <a-input v-decorator="[ 'manageDepart', {}]" hidden></a-input>
            </a-form-item>
          </a-col>

        </a-row>

        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="办公地点">
              <a-input placeholder="请输入办公地点" v-decorator="['officeLocation', {}]" />
            </a-form-item>
          </a-col>
        </a-row>


        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="流程审批">
              <j-dict-select-tag type="radio" v-decorator="['processStatus', {initialValue:'0'}]"  dictCode="yn" placeholder="请选择流程审批" :triggerChange="true" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="适用流程">
              <a-select
                placeholder="请选择适用流程"
                v-decorator="['fitProcess', {initialValue:'desform_eoa_metting'}]">
                <template v-for="(obj,index) in flowNoDict">
                  <a-select-option :key="index" :value="obj.flowNo">{{ obj.bizName }}</a-select-option>
                </template>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="备注">
              <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <!-- 子表单区域 -->
      <a-tabs v-model="activeKey" @change="handleChangeTabs">
        <a-tab-pane tab="会议室设备表" :key="refKeys[0]" :forceRender="true">
          <j-editable-table
            :ref="refKeys[0]"
            :loading="eoaMettingDeviceTable.loading"
            :columns="eoaMettingDeviceTable.columns"
            :dataSource="eoaMettingDeviceTable.dataSource"
            :maxHeight="300"
            :rowNumber="true"
            :rowSelection="true"
            :actionButton="true"/>
        </a-tab-pane>
      </a-tabs>
    </a-spin>
  </j-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
  import { getAction } from '@/api/manage'
  import { VALIDATE_NO_PASSED, validateFormAndTables } from '@/utils/JEditableTableUtil'
  export default {
    name: "EoaMettingRoomModal",
    mixins: [JEditableTableMixin],
    components: {
      JSelectDepart
    },
    data () {
      return {
        title:"操作",
        visible: false,
        manageDepartString:"",
        checkedDepartKeys:[],
        selectedDepartKeys:[],
        departId:"",
        validatorRules:{
        },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        refKeys: ['eoaMettingDevice', ],
        activeKey: 'eoaMettingDevice',
        // 会议室设备表
        eoaMettingDeviceTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '设备名称',
              key: 'deviceName',
              type: FormTypes.input,
              defaultValue: '',
              placeholder: '请输入设备名称',
            },
            {
              title: '设备数量',
              key: 'deviceNumber',
              type: FormTypes.inputNumber,
              defaultValue: '',
              placeholder: '请输入设备数量',
            },
            {
              title: '设备说明',
              key: 'deviceDesc',
              type: FormTypes.input,
              defaultValue: '',
              placeholder: '请输入设备说明',
            }
          ]
        },
        flowNoDict:[],
        url: {
          add: "/metting/eoaMettingRoom/add",
          edit: "/metting/eoaMettingRoom/edit",
          queryFlowSelect:"/metting/eoaMetting/queryFlowSelect",
          eoaMettingDevice: {
            list: '/metting/eoaMettingRoom/queryEoaMettingDeviceByMainId'
          },
        },
      }
    },
    created () {
      this.loadFlowSelect()
    },
    methods: {
      // 加载本组件需要的数据字典数据
      loadFlowSelect(){
        getAction(this.url.queryFlowSelect).then((res)=>{
          console.log("loadFlowSelect::==>",res.result)
          if(res.success){
            this.flowNoDict = res.result;
          }
        })
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'name', 'position', 'peopleNumber', 'scale', 'officeLocation', 'manageDepart', 'type', 'processStatus', 'fitProcess', 'remark', ))
          // 时间格式化
          this.departId=this.model.manageDepart;
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.eoaMettingDevice.list, params, this.eoaMettingDeviceTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        //时间格式化
        return {
          ...main, // 展开
          eoaMettingDeviceList: allValues.tablesValue[0].values,
        }
      },
      /** 确定按钮点击事件 */
      handleOk() {
        /** 触发表单验证 */
        this.getAllTable().then(tables => {
          /** 一次性验证主表和所有的次表 */
          return validateFormAndTables(this.form, tables)
        }).then(allValues => {
          if (typeof this.classifyIntoFormData !== 'function') {
            throw this.throwNotFunction('classifyIntoFormData')
          }
          let formData = this.classifyIntoFormData(allValues)
          formData.manageDepart=this.departId;
          console.log("formData===>",formData)
          // 发起请求
          return this.request(formData)
        }).catch(e => {
          if (e.error === VALIDATE_NO_PASSED) {
            // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
            this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index]
          } else {
            console.error(e)
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>

</style>