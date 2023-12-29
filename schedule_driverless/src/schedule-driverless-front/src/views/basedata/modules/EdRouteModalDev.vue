<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item label="审核结果" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['auditResult']" placeholder="请输入审核结果"></a-input>
        </a-form-item>
        <a-form-item label="审核时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择审核时间" v-decorator="['auditTime']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="审核人" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['auditUser']" placeholder="请输入审核人"></a-input>
        </a-form-item>
        <a-form-item label="开始日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择开始日期" v-decorator="['beginDate']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="结束日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择结束日期" v-decorator="['endDate']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="起草人" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['createUser']" placeholder="请输入起草人"></a-input>
        </a-form-item>
        <a-form-item label="创建日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择创建日期" v-decorator="['dateCreated', validatorRules.dateCreated]" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="下行首发时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['downFirstTime']" placeholder="请输入下行首发时间"></a-input>
        </a-form-item>
        <a-form-item label="下行末发时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['downLatestTime']" placeholder="请输入下行末发时间"></a-input>
        </a-form-item>
        <a-form-item label="首站编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['firstStationId']" placeholder="请输入首站编码" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="首战名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['firstStationName']" placeholder="请输入首战名称"></a-input>
        </a-form-item>
        <a-form-item label="末站编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['lastStationId']" placeholder="请输入末站编码" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="末站名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['lastStationName']" placeholder="请输入末站名称"></a-input>
        </a-form-item>
        <a-form-item label="最近修改日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择最近修改日期" v-decorator="['lastUpdated', validatorRules.lastUpdated]" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="线路里程" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['mileage']" placeholder="请输入线路里程" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="运营类型(1:普通,2:高峰,3:旅游,4:临时,5:大学城专线,6:地铁接驳线,7:商务专线,8:节假日专线)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['operatorType']" placeholder="请输入运营类型(1:普通,2:高峰,3:旅游,4:临时,5:大学城专线,6:地铁接驳线,7:商务专线,8:节假日专线)"></a-input>
        </a-form-item>
        <a-form-item label="运行区域(0:大巴线路,1:中小巴线路,2:BRT通道内,3:BRT通道外)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['runningPlace']" placeholder="请输入运行区域(0:大巴线路,1:中小巴线路,2:BRT通道内,3:BRT通道外)"></a-input>
        </a-form-item>
        <a-form-item label="所属机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['organId', validatorRules.organId]" placeholder="请输入所属机构" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="运营机构" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['organRunId']" placeholder="请输入运营机构" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="记录状态(起草->申请审核->审核通过->:审核不通过->发布)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['recordStatus', validatorRules.recordStatus]" placeholder="请输入记录状态(起草->申请审核->审核通过->:审核不通过->发布)"></a-input>
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['remark']" placeholder="请输入备注"></a-input>
        </a-form-item>
        <a-form-item label="线路编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeCode', validatorRules.routeCode]" placeholder="请输入线路编码"></a-input>
        </a-form-item>
        <a-form-item label="线路名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeName', validatorRules.routeName]" placeholder="请输入线路名称"></a-input>
        </a-form-item>
        <a-form-item label="线路版本" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeVersion', validatorRules.routeVersion]" placeholder="请输入线路版本"></a-input>
        </a-form-item>
        <a-form-item label="线路简称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['shortName', validatorRules.shortName]" placeholder="请输入线路简称"></a-input>
        </a-form-item>
        <a-form-item label="班次类型(0:日班,1:夜班,2:全班)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['tripsType']" placeholder="请输入班次类型(0:日班,1:夜班,2:全班)"></a-input>
        </a-form-item>
        <a-form-item label="上行首发时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['upFirstTime']" placeholder="请输入上行首发时间"></a-input>
        </a-form-item>
        <a-form-item label="上行末发时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['upLatestTime']" placeholder="请输入上行末发时间"></a-input>
        </a-form-item>
        <a-form-item label="站标" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['stationTarget']" placeholder="请输入站标" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="排序(线路排序用)" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['sort']" placeholder="请输入排序(线路排序用)" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="最近修改人" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['lastOperator']" placeholder="请输入最近修改人"></a-input>
        </a-form-item>
        <a-form-item label="归属行政区" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['zoneBelond']" placeholder="请输入归属行政区"></a-input>
        </a-form-item>
        <a-form-item label="定时发布时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择定时发布时间" v-decorator="['publishTime']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择创建时间" v-decorator="['createTime']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['createBy']" placeholder="请输入创建人"></a-input>
        </a-form-item>
        <a-form-item label="更新人" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['updateBy']" placeholder="请输入更新人"></a-input>
        </a-form-item>
        <a-form-item label="更新时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择更新时间" v-decorator="['updateTime']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="所属业务部门" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['sysOrgCode']" placeholder="请输入所属业务部门"></a-input>
        </a-form-item>
        <a-form-item label="租户ID" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['tenantId']" placeholder="请输入租户ID"></a-input>
        </a-form-item>
        <a-form-item label="删除标识：0-未删除，1-已删除" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['delFlag']" placeholder="请输入删除标识：0-未删除，1-已删除" style="width: 100%"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import JDate from '@/components/jeecg/JDate'

  export default {
    name: 'EdRouteModal',
    components: {
      JDate
    },
    data () {
      return {
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
          dateCreated: {
            rules: [
              { required: true, message: '请输入创建日期!' }
            ]
          },
          lastUpdated: {
            rules: [
              { required: true, message: '请输入最近修改日期!' }
            ]
          },
          organId: {
            rules: [
              { required: true, message: '请输入所属机构!' }
            ]
          },
          recordStatus: {
            rules: [
              { required: true, message: '请输入记录状态(起草->申请审核->审核通过->:审核不通过->发布)!' }
            ]
          },
          routeCode: {
            rules: [
              { required: true, message: '请输入线路编码!' }
            ]
          },
          routeName: {
            rules: [
              { required: true, message: '请输入线路名称!' }
            ]
          },
          routeVersion: {
            rules: [
              { required: true, message: '请输入线路版本!' }
            ]
          },
          shortName: {
            rules: [
              { required: true, message: '请输入线路简称!' }
            ]
          }
        },
        url: {
          add: '/bsroute/edRoute/add',
          edit: '/bsroute/edRoute/edit'
        }
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'auditResult', 'auditTime', 'auditUser', 'beginDate', 'endDate', 'createUser', 'dateCreated', 'downFirstTime', 'downLatestTime', 'firstStationId', 'firstStationName', 'lastStationId', 'lastStationName', 'lastUpdated', 'mileage', 'operatorType', 'runningPlace', 'organId', 'organRunId', 'recordStatus', 'remark', 'routeCode', 'routeName', 'routeVersion', 'shortName', 'tripsType', 'upFirstTime', 'upLatestTime', 'stationTarget', 'sort', 'lastOperator', 'zoneBelond', 'publishTime', 'createTime', 'createBy', 'updateBy', 'updateTime', 'sysOrgCode', 'tenantId', 'delFlag'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
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
            console.log('表单提交数据', formData)
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
      handleCancel () {
        this.close()
      },
      popupCallback(row) {
        this.form.setFieldsValue(pick(row, 'auditResult', 'auditTime', 'auditUser', 'beginDate', 'endDate', 'createUser', 'dateCreated', 'downFirstTime', 'downLatestTime', 'firstStationId', 'firstStationName', 'lastStationId', 'lastStationName', 'lastUpdated', 'mileage', 'operatorType', 'runningPlace', 'organId', 'organRunId', 'recordStatus', 'remark', 'routeCode', 'routeName', 'routeVersion', 'shortName', 'tripsType', 'upFirstTime', 'upLatestTime', 'stationTarget', 'sort', 'lastOperator', 'zoneBelond', 'publishTime', 'createTime', 'createBy', 'updateBy', 'updateTime', 'sysOrgCode', 'tenantId', 'delFlag'))
      }

    }
  }
</script>
