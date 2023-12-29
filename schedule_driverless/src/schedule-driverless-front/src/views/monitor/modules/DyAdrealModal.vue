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

        <a-form-item label="终端设备id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['obuid', validatorRules.obuid]" placeholder="请输入终端设备id"></a-input>
        </a-form-item>
        <a-form-item label="班次id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['tripId']" placeholder="请输入班次id"></a-input>
        </a-form-item>
        <a-form-item label="线路id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['routeId']" placeholder="请输入线路id" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="线路编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeCode']" placeholder="请输入线路编码"></a-input>
        </a-form-item>
        <a-form-item label="线路名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeName']" placeholder="请输入线路名称"></a-input>
        </a-form-item>
        <a-form-item label="服务号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['serviceNumber']" placeholder="请输入服务号"></a-input>
        </a-form-item>
        <a-form-item label="子线路id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['routesubId']" placeholder="请输入子线路id" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="子线路名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['routeSubName']" placeholder="请输入子线路名称"></a-input>
        </a-form-item>
        <a-form-item label="服务类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['serviceType']" placeholder="请输入服务类型"></a-input>
        </a-form-item>
        <a-form-item label="车辆站台编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['busStopCode']" placeholder="请输入车辆站台编码"></a-input>
        </a-form-item>
        <a-form-item label="线路站台id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['routeStaId']" placeholder="请输入线路站台id" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="站台id" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['stationId']" placeholder="请输入站台id" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="站台名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['stationName']" placeholder="请输入站台名称"></a-input>
        </a-form-item>
        <a-form-item label="顺序号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number v-decorator="['orderNumber']" placeholder="请输入顺序号" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="报站类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['reportType']" placeholder="请输入报站类型"></a-input>
        </a-form-item>
        <a-form-item label="电子路牌" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['runningboard']" placeholder="请输入电子路牌"></a-input>
        </a-form-item>
        <a-form-item label="进站标志" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="['adFlag']" placeholder="请输入进站标志"></a-input>
        </a-form-item>
        <a-form-item label="进站时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择进站时间" v-decorator="['adTime', validatorRules.adTime]" :trigger-change="true" style="width: 100%"/>
        </a-form-item>
        <a-form-item label="进站时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-date placeholder="请选择进站时间" v-decorator="['inTime', validatorRules.inTime]" :trigger-change="true" style="width: 100%"/>
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
    name: 'DyAdrealModal',
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
          obuid: {
            rules: [
              {
                required: true,
                message: '请输入终端设备id!'
              }
            ]
          },
          adTime: {
            rules: [
              {
                required: true,
                message: '请输入进站时间!'
              }
            ]
          },
          inTime: {
            rules: [
              {
                required: true,
                message: '请输入进站时间!'
              }
            ]
          }
        },
        url: {
          add: '/monitor/dyAdreal/add',
          edit: '/monitor/dyAdreal/edit'
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
          this.form.setFieldsValue(pick(this.model, 'obuid', 'tripId', 'routeId', 'routeCode', 'routeName', 'serviceNumber', 'routesubId', 'routeSubName', 'serviceType', 'busStopCode', 'routeStaId', 'stationId', 'stationName', 'orderNumber', 'reportType', 'runningboard', 'adFlag', 'adTime', 'inTime'))
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
        this.form.setFieldsValue(pick(row, 'obuid', 'tripId', 'routeId', 'routeCode', 'routeName', 'serviceNumber', 'routesubId', 'routeSubName', 'serviceType', 'busStopCode', 'routeStaId', 'stationId', 'stationName', 'orderNumber', 'reportType', 'runningboard', 'adFlag', 'adTime', 'inTime'))
      }

    }
  }
</script>
