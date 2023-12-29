<template>
<a-spin :spinning="confirmLoading">
  <a-form :form="form">
    <a-tabs  v-model="defaultActiveKey">
      <a-tab-pane tab="基本信息" key="1">
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="名称">
              <a-input placeholder="请输入名称" v-decorator="['name', {rules: [{ required: true, message: '名称不允许为空!' }] }]" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="类型">
              <j-dict-select-tag v-decorator="['type', {}]" title="类型" dictCode="mettingType" placeholder="请选择类型" :triggerChange="true"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="召集人">
              <j-select-user-by-dep v-decorator="['originator', {}]" :multi="false"></j-select-user-by-dep>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="联系人">
              <j-select-user-by-dep v-decorator="['contacts', {}]" :multi="false"></j-select-user-by-dep>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="联系方式">
              <a-input placeholder="请输入联系方式" v-decorator="['concatMode', {rules: [{ required: false, message: '' }] }]" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="提醒方式">
              <j-dict-select-tag v-decorator="['remindMode', {}]" title="提醒方式" dictCode="remindMode" placeholder="请选择提醒方式" :triggerChange="true"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="提醒时间">
              <j-dict-select-tag v-decorator="['remind', {}]" title="提醒时间" dictCode="remindTime" placeholder="请选择提醒时间" :triggerChange="true"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="开始时间">
              <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'beginTime', {}]" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="结束时间">
              <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'endTime', {}]" style="width: 100%"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="会议地点">
              <a-input placeholder="请输入会议地点" v-decorator="['location', {}]" hidden />
              <div>
                <j-select-metting-room v-decorator="['location', {rules: [{ required: !customRequired, message: '会议地点不允许为空!' }] }]" :multi="false" @change="mettingRoomChange"></j-select-metting-room>
              </div>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="自定义会议地点">
              <a-input placeholder="请输入自定义会议地点" v-decorator="['customLocation', {rules: [{ required: customRequired, message: '自定义会议地点不允许为空!' }] }]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="参与人员">
              <a-input placeholder="请输入参与人员" v-decorator="['joinPerson', {}]" hidden/>
              <j-select-user-by-dep v-decorator="['joinPerson', {}]" :multi="true"></j-select-user-by-dep>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="其他人员">
              <a-input placeholder="请输入其他人员" v-decorator="['otherPerson', {}]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="是否审批">
              <a-select v-decorator="['approvalStatus', {}]" :disabled="disabled">
                <a-select-option value="0">否</a-select-option>
                <a-select-option value="1">是</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="会议内容">
              <a-textarea placeholder="请输入会议内容" v-decorator="['content', {}]" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="备注">
              <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row style="width: 100%;">
          <a-col :span="12">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="相关文件">
              <j-upload v-model="fileList"></j-upload>
            </a-form-item>
          </a-col>
        </a-row>

      </a-tab-pane>
      <a-tab-pane tab="签到信息" key="2" forceRender>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="背景颜色">
          <span class="colorSpan">{{bgColor}}<colorPicker v-model="bgColor" style="margin-left: 10px"/></span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="背景图片">
          <j-image-upload v-model="imgList"></j-image-upload>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="字体颜色">
          <span class="colorSpan">{{fontColor}}<colorPicker v-model="fontColor" style="margin-left: 10px"/></span>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="字体大小">
          <a-input-number placeholder="请输入字体大小" v-decorator="['fontSize', {}]"  :min="5" :max="100" :precision="0" style="width: 100px"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="显示头像">
          <a-input v-decorator="['showAvatar', {}]" hidden/>
          <a-switch @change="avatarChange" v-model="showAvatar"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="显示部门">
          <a-input v-decorator="['showDepart', {}]" hidden/>
          <a-switch  @change="departChange" v-model="showDepart"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="显示签到信息">
          <a-input  v-decorator="['showSign', {}]" hidden/>
          <a-switch  @change="signChange" v-model="showSign"/>
        </a-form-item>
      </a-tab-pane>
    </a-tabs>
  </a-form>
</a-spin>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import store from '@/store'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import JUpload from '@/components/jeecg/JUpload'
  import JImageUpload from '@/components/jeecg/JImageUpload.vue'
  import JSelectMettingRoom from './JSelectMettingRoom'
  import { validateTables } from '@/utils/JEditableTableUtil'

  export default {
    name: "EoaMettingForm",
    components: {
      JSelectMettingRoom,
      JDictSelectTag,JSelectUserByDep,JUpload,JImageUpload
    },
    props: {
      formData: {
        type: Object,
        default: () => ({})
      },
      // 是否运行在 modal 模式，modal模式下不进行查询操作
      modalMode: {
        type: Boolean,
        default: false
      },
      loading: {
        type: Boolean,
        default: false
      },
    },
    data () {
      return {
        title:"操作",
        visible: false,
        showAvatar: false,
        showDepart: false,
        showSign: false,
        disabled: false,
        customRequired:false,
        defaultActiveKey:"1",
        bgColor: "#006699",
        fontColor: "#FFFFFF",
        fileList:[],
        imgList:[],
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{

        },
        url: {
          add: "/metting/eoaMetting/add",
          edit: "/metting/eoaMetting/edit",
          queryById: "/metting/eoaMetting/queryById",
        },
      }
    },
    created () {
      this.queryById()
    },
    methods: {
      queryById() {
        getAction(this.url.queryById, {
          id: this.formData.dataId
        }).then(res => {
          if (res.success) {
            this.edit(res.result)
          } else throw res.message
        }).catch(e => {
          this.$warning({ title: '查询会议信息失败', content: (e || {}).message || e })
        })
      },
      previewSign(){
        let routeUrl = this.$router.resolve({
          path: "/metting/eoaMettingPreview",
          query: {bgColor:this.bgColor,bgImg:this.imgList}
        });
        //window.open(routeUrl.href, '_blank','toolbar=yes, width=1300, height=900');
        window.open(routeUrl.href, '_blank');
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'repeatMode','type','name','originator','contacts','concatMode','location','customLocation','content','status','intervalDay','repeatStrategy','remindMode','joinPerson','otherPerson','approvalStatus','files','remark','bgColor','bgImg','fontColor','fontSize','showAvatar','showDepart','showSign','bpmStatus'))
          this.form.setFieldsValue({originator:record.originator?record.originator:store.getters.userInfo.username});
          this.form.setFieldsValue({contacts:record.contacts?record.contacts:store.getters.userInfo.username});
          //时间格式化
          this.form.setFieldsValue({beginTime:this.model.beginTime?moment(this.model.beginTime):null})
          this.form.setFieldsValue({endTime:this.model.endTime?moment(this.model.endTime):null})
        });
        if(this.model.id){
          setTimeout(() => {
            this.fileList = record.files;
            this.imgList = record.bgImg;
            this.showAvatar=record.showAvatar=='1'?true:false;
            this.showDepart=record.showDepart=='1'?true:false;
            this.showSign=record.showSign=='1'?true:false;
            this.bgColor=record.bgColor;
            this.fontColor=record.fontColor;
          }, 5)
        }
      },
      initalDate(){
        this.defaultActiveKey ="1";
        this.fileList =[];
        this.imgList = [];
        this.showAvatar=false;
        this.showDepart=false;
        this.showSign=false;
        this.bgColor="#006699";
        this.fontColor="#FFFFFF";
      },
      avatarChange(checked){
        this.showAvatar=checked;
        if(this.showAvatar){
          this.form.setFieldsValue(pick({'showAvatar':'1'}, 'showAvatar'));
        }else{
          this.form.setFieldsValue(pick({'showAvatar':'0'}, 'showAvatar'));
        }
      },
      departChange(checked){
        this.showDepart=checked;
        if(this.showDepart){
          this.form.setFieldsValue(pick({'showDepart':'1'}, 'showDepart'));
        }else{
          this.form.setFieldsValue(pick({'showDepart':'0'}, 'showDepart'));
        }
      },
      signChange(checked){
        this.showSign=checked;
        if(this.showSign){
          this.form.setFieldsValue(pick({'showSign':'1'}, 'showSign'));
        }else{
          this.form.setFieldsValue(pick({'showSign':'0'}, 'showSign'));
        }
      }

    }
  }
</script>

<style lang="less" scoped>
  /deep/ .m-colorPicker .box {
    width: 250px;
    z-index:10000;
  }
  /deep/ .m-colorPicker .colorBtn{
    border:1px solid #d9d9d9;
  }
  .colorSpan{
    display: flex;
    align-items: center;
    justify-content: center;
    border:1px solid #d9d9d9;
    border-radius: 5px;
    width: 100px;
   }
</style>