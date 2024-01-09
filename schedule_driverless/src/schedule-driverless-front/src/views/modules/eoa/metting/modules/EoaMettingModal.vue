<template>
  <j-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    destroyOnClose
    style="top:10px"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form" hideRequiredMark>
        <a-tabs v-model="defaultActiveKey">
          <a-tab-pane tab="基本信息" key="1">
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="名称">
                  <a-input placeholder="请输入名称" v-decorator="['name', {rules: [{ required: true, message: '名称不允许为空!' }] }]" :readOnly="disabled"/>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="类型">
                  <j-dict-select-tag v-decorator="['type', {}]" title="类型" dictCode="mettingType" placeholder="请选择类型" :triggerChange="true" :disabled="disabled"/>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="召集人">
                  <j-select-user-by-dep v-decorator="['originator', {}]" :multi="false" :disabled="disabled"></j-select-user-by-dep>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="联系人">
                  <j-select-user-by-dep v-decorator="['contacts', {}]" :multi="false" :disabled="disabled"></j-select-user-by-dep>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="联系方式">
                  <a-input placeholder="请输入联系方式" v-decorator="['concatMode', validatorRules.phone]" :readOnly="disabled"/>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="纪要上传人">
                  <j-select-user-by-dep v-decorator="['summaryUploader', {}]" :multi="false" :disabled="disabled"></j-select-user-by-dep>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="提醒方式">
                  <j-dict-select-tag v-decorator="['remindMode', {}]" title="提醒方式" dictCode="remindMode" placeholder="请选择提醒方式" :triggerChange="true" :disabled="disabled"/>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="提醒时间">
                  <j-dict-select-tag v-decorator="['remind', {}]" title="提醒时间" dictCode="remindTime" placeholder="请选择提醒时间" :triggerChange="true" :disabled="disabled"/>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="开始时间">
                  <a-date-picker showTime format="YYYY-MM-DD HH:mm:ss" v-decorator="[ 'beginTime', {rules: [{ required: true, message: '请选择开始时间' }]}]" style="width: 100%" :disabled="disabled"/>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="结束时间">
                  <a-date-picker showTime format="YYYY-MM-DD HH:mm:ss" v-decorator="[ 'endTime', {rules: [{ required: true, message: '请选择结束时间'}]}]" style="width: 100%" :disabled="disabled"/>
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
                    <j-select-metting-room v-decorator="['location', {rules: [{ required: !customRequired, message: '会议地点不允许为空!',trigger: 'change' }] }]" :multi="false" @change="mettingRoomChange" :disabled="disabled"></j-select-metting-room>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="!disabled">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  >
                   <a style="color:#19a5ff;margin-left: 50px" @click="showUse">会议室使用情况</a>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="自定义会议地点">
                  <a-input placeholder="请输入自定义会议地点" v-decorator="['customLocation', {rules: [{ required: customRequired, message: '自定义会议地点不允许为空!',trigger: 'change' }] }]"  :readOnly="disabled" @input="customRoomChange"/>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="是否审批">
                  <j-dict-select-tag type="radio" v-decorator="['approvalStatus', {initialValue:'0'}]"  dictCode="yn" placeholder="请选择" :triggerChange="true" :disabled="approvalDisabled"/>
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
                  <j-select-user-by-dep v-decorator="['joinPerson', {}]" :multi="true" :disabled="disabled"></j-select-user-by-dep>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="其他人员">
                  <a-input placeholder="请输入其他人员" v-decorator="['otherPerson', {}]" :readOnly="disabled"/>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%;">
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="会议内容">
                  <a-textarea placeholder="请输入会议内容" v-decorator="['content', {}]" :readOnly="disabled"/>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  label="备注">
                  <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" :readOnly="disabled"/>
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
              <a-col :span="12" v-if="disabled">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  v-if="model.summaryUploader==username"
                  label="会议纪要">
                  <j-upload v-model="summaryFile" :number="1" ></j-upload>
                </a-form-item>
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  v-else-if="model.summaryFile"
                  label="会议纪要">
                  <a style="margin-left:10px;color:#19a5ff":href="getFileDownloadUrl(model.summaryFile)" target="_blank" >下载</a>
                </a-form-item>
              </a-col>
            </a-row>

          </a-tab-pane>
          <a-tab-pane tab="签到信息" key="2" forceRender v-if="!disabled">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="背景颜色">
              <span class="colorSpan">{{ bgColor }} <color-picker v-model="bgColor" @change="bgColorChange"></color-picker></span>
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
              <span class="colorSpan">{{ fontColor }}<color-picker v-model="fontColor" @change="fontColorChange"></color-picker></span>
            </a-form-item>
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="字体大小">
              <a-input-number
                placeholder="请输入字体大小"
                v-decorator="['fontSize', {}]"
                :min="24"
                :max="100"
                :precision="0"
                style="width: 100px"/>
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
              <a-switch @change="departChange" v-model="showDepart"/>
            </a-form-item>
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="显示签到信息">
              <a-input v-decorator="['showSign', {}]" hidden/>
              <a-switch @change="signChange" v-model="showSign"/>
            </a-form-item>
          </a-tab-pane>
        </a-tabs>
      </a-form>
    </a-spin>
    <template slot="footer">
      <a-button @click="handleCancel" style="margin-right: 5px">关闭</a-button>
      <a-button type="primary" @click="previewSign" v-if="defaultActiveKey=='2'&&!disabled" style="margin-right: 5px">签到预览</a-button>
      <a-button type="primary" @click="handleOk">确认</a-button>
    </template>
    <eoa-metting-room-use-modal ref="roomUseForm" v-if="!disabled"></eoa-metting-room-use-modal>
  </j-modal>
</template>

<script>
  import { httpAction,getAction,getFileAccessHttpUrl } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import store from '@/store/'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import JUpload from '@/components/jeecg/JUpload'
  import JImageUpload from '@/components/jeecg/JImageUpload.vue'
  import JSelectMettingRoom from './JSelectMettingRoom'
  import EoaMettingRoomUseModal from './EoaMettingRoomUseModal'
  import { VueColorpicker } from 'vue-pop-colorpicker'
  export default {
    name: "EoaMettingModal",
    components: {
      EoaMettingRoomUseModal,
      JSelectMettingRoom,
      JDictSelectTag,JSelectUserByDep,JUpload,JImageUpload,
      'color-picker': VueColorpicker
    },
    data () {
      return {
        title:"操作",
        visible: false,
        showAvatar: false,
        showDepart: false,
        showSign: false,
        disabled: false,
        approvalDisabled: false,
        name: "",
        customRequired:false,
        defaultActiveKey:"1",
        bgColor: "#006699",
        fontColor: "#FFFFFF",
        fileList:[],
        imgList:[],
        summaryFile:[],
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
          phone:{rules: [{validator: this.validatePhone}]},
        },
        url: {
          add: "/metting/eoaMetting/add",
          edit: "/metting/eoaMetting/edit",
          queryById: "/metting/eoaMetting/queryById",
        },
      }
    },
    created () {
      this.username = store.getters.userInfo.username;
    },
    methods: {
      initDictConfig(){
        console.log("--初始化字典!")
      },
      previewSign(){
        let size=this.form.getFieldValue("fontSize");
        let routeUrl = this.$router.resolve({
          path: "/metting/eoaMettingPreview",
          query: {
            bgColor:this.bgColor,
            fontColor:this.fontColor,
            fontSize:size,
            id:this.model.id,
            userid:store.getters.userInfo.id
          }
        });
        window.open(routeUrl.href, '_blank');
      },
      eventAdd(info){
        if(info&&info.start&&info.end){
            this.edit({beginTime:info.start,endTime:info.end});
        }else{
          this.edit({});
        }
      },
      eventEdit(info){
        if(info&&info.event&&info.event.id){
          getAction(this.url.queryById, {id:info.event.id}).then((res) => {
            if (res.success) {
            this.edit(res.result);
            }
          })
        }
      },
      getFileDownloadUrl: function (path) {
        return getFileAccessHttpUrl(path)
      },
      add () {
        this.edit({});
      },
      detail(record){
        this.disabled=true;
        this.edit(record);
      },
      edit (record) {
        this.model={};
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'repeatMode','type','name','originator','contacts','concatMode','location','customLocation','content','status','intervalDay','repeatStrategy','remindMode','joinPerson','otherPerson','approvalStatus','files','remark','bgColor','bgImg','fontColor','fontSize','showAvatar','showDepart','showSign','bpmStatus','summaryUploader'))
          this.form.setFieldsValue({originator:record.originator?record.originator:store.getters.userInfo.username});
          this.form.setFieldsValue({contacts:record.contacts?record.contacts:store.getters.userInfo.username});
          //时间格式化
          this.form.setFieldsValue({beginTime:this.model.beginTime?moment(this.model.beginTime):null})
          this.form.setFieldsValue({endTime:this.model.endTime?moment(this.model.endTime):null})
        });
        if(this.model.id){
          console.log("record::::>",record)
          setTimeout(() => {
            this.fileList = record.files;
            this.imgList = record.bgImg;
            this.summaryFile = record.summaryFile;
            this.showAvatar=record.showAvatar=='1'?true:false;
            this.showDepart=record.showDepart=='1'?true:false;
            this.showSign=record.showSign=='1'?true:false;
            this.customRequired=(record.customLocation&&record.customLocation.length>0)?true:false;
            this.bgColor=record.bgColor;
            this.fontColor=record.fontColor;
          }, 5)
        }
      },
      close () {
        this.initalDate()
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
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            formData.beginTime = formData.beginTime?formData.beginTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.endTime = formData.endTime?formData.endTime.format('YYYY-MM-DD HH:mm:ss'):null;
            if(that.fileList&&that.fileList.length>0 &&!that.disabled){
              formData.files=that.fileList
            }
            if(that.imgList&&that.imgList.length>0 &&!that.disabled){
              formData.bgImg=that.imgList
            }
            if(that.summaryFile&&that.summaryFile.length>0){
              formData.summaryFile=that.summaryFile
            }
            if(that.fontColor&&!that.disabled){
              formData.fontColor=that.fontColor
            }
            if(that.bgColor&&!that.disabled){
              formData.bgColor=that.bgColor
            }
            console.log("formData==>",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
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
      mettingRoomChange(room,processStatus){
          this.form.setFieldsValue(pick({'location':room}, 'location'));
          this.form.setFieldsValue(pick({'approvalStatus':processStatus}, 'approvalStatus'));
          if(processStatus=='1'){
            this.approvalDisabled=true;
          }else{
            this.approvalDisabled=false;
          }
        this.customRequired=false
        this.$nextTick(() => {
          this.form.validateFields(['location'],{ force:true });
          this.form.validateFields(['customLocation'],{ force:true });
        });
      },
      customRoomChange(value){
        let fieldValue = this.form.getFieldValue('location')
        if(!fieldValue){
          this.customRequired=true
          this.approvalDisabled=false;
          this.$nextTick(() => {
            this.form.validateFields(['location'],{ force:true });
            this.form.validateFields(['customLocation'],{ force:true });
          });
        }
      },
      showUse(){
          this.$refs.roomUseForm.show()
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
      },
      validatePhone(rule, value, callback){
        if(!value){
          callback()
        }else{
          if(new RegExp(/^1[3|4|5|7|8|9][0-9]\d{8}$/).test(value)){
            callback()
          }else{
            callback("请输入正确格式的手机号码!");
          }
        }
      },
     bgColorChange (color) {
       this.bgColor=this.rgbToHexadecimal(color);
      },
      fontColorChange(color){
        this.fontColor=this.rgbToHexadecimal(color);
      },
     rgbToHexadecimal(color){
       if(color.startsWith("rgb")){
         let RGB = color.substring(4,color.length-1).split(",")
         let hexcode = "#";
         let c = "0123456789ABCDEF"
         for (var x = 0; x < 3; x++) {
           let n = RGB[x];
           let b = ""
           let a = n % 16;
           b = c.substr(a, 1);
           a = (n - a) / 16;
           hexcode += c.substr(a, 1) + b
         }
        return hexcode;
       }else{
         return "#ffffff";
       }
     }
    }
  }
</script>

<style lang="less" scoped>
  .vue-colorpicker{
    border: 0 !important;
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