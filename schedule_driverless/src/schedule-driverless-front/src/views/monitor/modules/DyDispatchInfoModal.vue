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

        <a-form-item label="车辆编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['busCode']" placeholder="请输入车辆编码"></a-input>
        </a-form-item>
        <a-form-item label="消息内容" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['infoContent']" placeholder="请输入消息内容"></a-input>
        </a-form-item>
        <a-form-item label="月台名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['stationName']" placeholder="请输入月台名称"></a-input>
        </a-form-item>
        <a-form-item label="月台id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['stationId']" placeholder="请输入月台id" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="线路类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['serviceType']" placeholder="请输入线路类型"></a-input>
        </a-form-item>
        <a-form-item label="子线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['routeSubId']" placeholder="请输入子线路" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="运营线路" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeName']" placeholder="请输入运营线路"></a-input>
        </a-form-item>
        <a-form-item label="运营线路ID" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['routeId']" placeholder="请输入运营线路ID" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="obuid" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['obuId']" placeholder="请输入obuid"></a-input>
        </a-form-item>
        <a-form-item label="车牌号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['numberPlate']" placeholder="请输入车牌号码"></a-input>
        </a-form-item>
        <a-form-item label="资讯类型 0－调度指令；1－上班签到；2－运营签到；3－交接班；4－运营签退；5－下班签退；6－发车提示；7－通知；8－触发指令；9－进总站；A－固定资讯（不需要调度员应答）；B－固定资讯（需要调度员应答））" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['infoType']" placeholder="请输入资讯类型 0－调度指令；1－上班签到；2－运营签到；3－交接班；4－运营签退；5－下班签退；6－发车提示；7－通知；8－触发指令；9－进总站；A－固定资讯（不需要调度员应答）；B－固定资讯（需要调度员应答））"></a-input>
        </a-form-item>
        <a-form-item label="失效时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择失效时间" v-decorator="['infoTimeOut']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="调度时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择调度时间" v-decorator="['infoTime']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="资讯状态 0－初始状态 1－超时无应答 2－同意调度 3－不同意调度 4－触发成功 5－触发失败 6－同意任务申请 7－不同意任务申请 8－已阅读 9－通信成功" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['infoStatus']" placeholder="请输入资讯状态 0－初始状态 1－超时无应答 2－同意调度 3－不同意调度 4－触发成功 5－触发失败 6－同意任务申请 7－不同意任务申请 8－已阅读 9－通信成功"></a-input>
        </a-form-item>
        <a-form-item label="资讯内容历史" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['infoContentHist']" placeholder="请输入资讯内容历史"></a-input>
        </a-form-item>
        <a-form-item label="调度用户" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['dispatchUser']" placeholder="请输入调度用户"></a-input>
        </a-form-item>
        <a-form-item label="发车时间（调度）" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择发车时间（调度）" v-decorator="['dispatchTime']" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="创建日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择创建日期" v-decorator="['dateCreated', validatorRules.dateCreated]" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="信息流水" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['dataSerial']" placeholder="请输入信息流水" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="车辆" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['busId']" placeholder="请输入车辆" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="固定资讯ID" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['fixMessageId', validatorRules.fixMessageId]" placeholder="请输入固定资讯ID"></a-input>
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
    name: 'DyDispatchInfoModal',
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
          fixMessageId: {
            rules: [
              { required: true, message: '请输入固定资讯ID!' }
            ]
          }
        },
        url: {
          add: '/monitor/dyDispatchInfo/add',
          edit: '/monitor/dyDispatchInfo/edit'
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
          this.form.setFieldsValue(pick(this.model, 'busCode', 'infoContent', 'stationName', 'stationId', 'serviceType', 'routeSubId', 'routeName', 'routeId', 'obuId', 'numberPlate', 'infoType', 'infoTimeOut', 'infoTime', 'infoStatus', 'infoContentHist', 'dispatchUser', 'dispatchTime', 'dateCreated', 'dataSerial', 'busId', 'fixMessageId'))
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
        this.form.setFieldsValue(pick(row, 'busCode', 'infoContent', 'stationName', 'stationId', 'serviceType', 'routeSubId', 'routeName', 'routeId', 'obuId', 'numberPlate', 'infoType', 'infoTimeOut', 'infoTime', 'infoStatus', 'infoContentHist', 'dispatchUser', 'dispatchTime', 'dateCreated', 'dataSerial', 'busId', 'fixMessageId'))
      }

    }
  }
</script>
