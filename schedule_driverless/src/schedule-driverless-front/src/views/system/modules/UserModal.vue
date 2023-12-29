<template>
  <a-drawer
    :title="title"
    :maskClosable="false"
    :width="drawerWidth"
    placement="right"
    :closable="true"
    @close="handleCancel"
    :visible="visible"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">

    <template slot="title">
      <div style="width: 100%;">
        <span>{{ title }}</span>
        <span style="display:inline-block;width:calc(100% - 51px);padding-right:10px;text-align: right">
          <a-button @click="toggleScreen" icon="appstore" style="height:20px;width:20px;border:0px"></a-button>
        </span>
      </div>

    </template>

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="用户类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select placeholder="请选择用户类型" :disabled="userTypeDisabled" v-decorator="['type', validatorRules.type]" @change="onChangeType">
            <a-select-option v-for="(item, key) in userTypeOptions" :key="key" :value="item.value">
              <span style="display: inline-block;width: 100%" :title=" item.text || item.label ">
                {{ item.text || item.label }}
              </span>
            </a-select-option>
          </a-select>
          <!-- <j-dict-select-tag ref="dictSelectTag" :disabled="userTypeDisabled" v-decorator="['type', validatorRules.type]" :triggerChange="true" placeholder="请选择用户类型" dictCode="user_type" /> -->
        </a-form-item>
        <a-form-item label="用户账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入用户账号" v-decorator.trim="[ 'username', validatorRules.username]" :readOnly="!!model.id"/>
        </a-form-item>

        <template v-if="!model.id">
          <a-form-item label="登陆密码" :labelCol="labelCol" :wrapperCol="wrapperCol" >
            <a-input type="password" placeholder="请输入登陆密码" v-decorator="[ 'password', validatorRules.password]" />
          </a-form-item>

          <a-form-item label="确认密码" :labelCol="labelCol" :wrapperCol="wrapperCol" >
            <a-input type="password" @blur="handleConfirmBlur" placeholder="请重新输入登陆密码" v-decorator="[ 'confirmpassword', validatorRules.confirmpassword]"/>
          </a-form-item>
        </template>

        <a-form-item label="真实名字" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <a-input placeholder="请输入真实名字" v-decorator.trim="[ 'realname', validatorRules.realname]" />
        </a-form-item>

        <a-form-item label="工号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="工号为空，默认使用用户账号作为工号" v-decorator.trim="[ 'workNo', validatorRules.workNo]" />
        </a-form-item>

<!--        <a-form-item label="职务" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--          <j-select-position placeholder="请选择职务" :multiple="false" v-decorator="['post', {}]"/>-->
<!--        </a-form-item>-->
        <!--行政机构-->
        <a-form-item label="行政机构" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!departDisabled">
          <a-select
            allowClear
            showSearch
            style="width: 100%"
            placeholder="请选择行政机构"
            optionFilterProp = "children"
            v-decorator="[ 'orgCode', validatorRules.orgCode]"
            :getPopupContainer= "(target) => target.parentNode"
            @change="onChangeOrgCode"
            >
            <a-select-option v-for="item in orgCodeList" :key="item.orgId" :value="item.orgCode">
              {{ item.orgName }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <!--行政部门-->
<!--        <a-form-item label="行政部门" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!departDisabled">-->
<!--          <a-select-->
<!--            allowClear-->
<!--            showSearch-->
<!--            style="width: 100%"-->
<!--            placeholder="请选择行政部门"-->
<!--            optionFilterProp = "children"-->
<!--            v-decorator="[ 'departCode', validatorRules.departCode]"-->
<!--            :getPopupContainer= "(target) => target.parentNode"-->
<!--            @focus="adminDepartFocus"-->
<!--            >-->
<!--            <a-select-option v-for="item in adminDepartList" :key="item.orgId" :value="item.orgCode">-->
<!--              {{ item.orgName }}-->
<!--            </a-select-option>-->
<!--          </a-select>-->
<!--        </a-form-item>-->

        <!--可登录部门-->
        <a-form-item label="可登录部门" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!departDisabled">
          <a-input-search
            placeholder="点击选择部门"
            readOnly
            v-decorator="[ 'checkedDepartNameString', validatorRules.checkedDepartNameString]"
            @search="onSearch">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>

        <!-- 角色分配 -->
        <a-form-item label="角色分配" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!roleDisabled" >
          <a-input-search
          placeholder="点击选择角色分配"
          readOnly
          v-decorator="[ 'selectedRole', validatorRules.selectedRole]"
          @search="onSearchRole">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>
        <a-form-item label="邮箱" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入邮箱" v-decorator="[ 'email', validatorRules.email]" />
        </a-form-item>

       <!-- update--begin--autor:wangshuai-----date:20200108------for：新增身份和负责部门------ -->
<!--        <a-form-item label="身份" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--          <a-radio-group-->
<!--            v-model="identity"-->
<!--            @change="identityChange" :disabled="true">-->
<!--            <a-radio value="1">普通用户</a-radio>-->
<!--            <a-radio value="2">上级</a-radio>-->
<!--          </a-radio-group>-->
<!--        </a-form-item>-->
        <!-- update--end--autor:wangshuai-----date:20200108------for：新增身份和负责部门------ -->
<!--        <a-form-item label="头像" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--          <j-image-upload class="avatar-uploader" text="上传" :bizPath="bizPath" v-model="fileList" ></j-image-upload>-->
<!--        </a-form-item>-->

        <a-form-item label="生日" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择生日"
            v-decorator="['birthday', {initialValue:!model.birthday?null:moment(model.birthday,dateFormat)}]"
            :getCalendarContainer="node => node.parentNode"/>
        </a-form-item>

        <a-form-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select v-decorator="[ 'sex', {}]" placeholder="请选择性别" :getPopupContainer= "(target) => target.parentNode">
            <a-select-option :value="1">男</a-select-option>
            <a-select-option :value="2">女</a-select-option>
          </a-select>
        </a-form-item>

        <!--   :disabled="isDisabledAuth('user:form:phone')"     -->
        <a-form-item label="手机号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入手机号码"  v-decorator="[ 'phone', validatorRules.phone]" />
        </a-form-item>

        <a-form-item label="座机" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input placeholder="请输入座机" v-decorator="[ 'telephone', validatorRules.telephone]"/>
        </a-form-item>

<!--        <a-form-item label="工作流引擎" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--          <j-dict-select-tag  v-decorator="['activitiSync', {}]" placeholder="请选择是否同步工作流引擎" :type="'radio'" :triggerChange="true" dictCode="activiti_sync"/>-->
<!--        </a-form-item>-->

      </a-form>
    </a-spin>
    <!-- 可登录部门 -->
    <depart-window ref="departWindow" @ok="modalFormOk" :selectedOrgCodeValue="selectedOrgCodeValue" :selectedOrgCodeKey="selectedOrgCodeKey"></depart-window>
    <!-- 角色分配 -->
    <RoleWindow ref="roleWindow" @ok="modalFormOkRole" :selectedOrgCodeValue="selectedOrgCodeValue" :selectType="selectType" />

    <div class="drawer-bootom-button" v-show="!disableSubmit">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from 'moment'
  import Vue from 'vue'
  // 引入搜索部门弹出框的组件
  import RoleWindow from './RoleWindow'
  import departWindow from './DepartWindow2'
  import JSelectPosition from '@/components/jeecgbiz/JSelectPosition'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import { getAction } from '@/api/manage'
  import {addUser,editUser,queryUserRole,queryall, queryRolesWithPermission } from '@/api/api'
  import { disabledAuthFilter } from "@/utils/authFilter"
  import {duplicateCheck,ajaxGetDictItems } from '@/api/api'
  import JImageUpload from '../../../components/jeecg/JImageUpload'
  import {encryption, getEncryptedString} from "../../../utils/encryption/aesEncrypt";
  import {ENCRYPTED_STRING} from "../../../store/mutation-types";

  export default {
    name: "UserModal",
    components: {
      JImageUpload,
      RoleWindow,
      departWindow,
      JSelectPosition
    },
    data () {
      return {
        encryptedString: {}, //是否是我的部门调用该页面
        departDisabled: false, //是否是我的部门调用该页面
        roleDisabled: false, //是否是角色维护调用该页面
        modalWidth:800,
        drawerWidth:700,
        modaltoggleFlag:true,
        confirmDirty: false,
        platformCode: undefined,
        platformList:[], //平台列表
        selectType: "", // 选择的用户类型
        orgCodeList: [], // 行政机构的下拉框列表
        orgCodeList2: [], // 行政机构的下拉框列表(记录最初的行政机构数据)
        selectedOrgCodeValue:"", //保存用户选择行政机构的orgCode
        selectedOrgCodeKey: "", //保存用户选择行政机构的key
        adminDepartList: [], // 行政部门的下拉列表
        selectedAdminDepartKeys:"", //保存用户选择行政部门的id
        adminDepart: "", //保存用户选择行政部门的名称title
        selectedDepartKeys:[], //保存用户选择部门id
        checkedDepartKeys:[],
        checkedDepartNames:[], // 保存部门的名称 =>title
        checkedDepartNameString:"", // 保存部门的名称 =>title
        resultDepartOptions:[],
        userId:"", //保存用户id
        disableSubmit:false,
        userDepartModel:{userId:'',departIdList:[]}, // 保存SysUserDepart的用户部门中间表数据需要的对象
        dateFormat:"YYYY-MM-DD",
        validatorRules:{
          type: {
            rules: [{
              required: true,
              message: '请选择用户类型!'
            }]
          },
          selectedRole: {
            rules: [{
              required: true, message: '请选择角色!'
            }]
          },
          username:{
            rules: [{
              required: true, message: '请输入用户账号!'
            },{
              max: 20,message: '用户账号不能超过20个字符'
            },{
              validator: this.validateUsername,
            }]
          },
          password:{
            rules: [{
              required: true,
              pattern:/^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,./]).{8,}$/,
              message: '密码由8位数字、大小写字母和特殊符号组成!'
            }, {
              validator: this.validateToNextPassword,
            }],
          },
          confirmpassword:{
            rules: [{
              required: true, message: '请重新输入登陆密码!',
            }, {
              validator: this.compareToFirstPassword,
            }],
          },
          realname:{rules: [
              {required: true, message: '请输入用户名称!' },
              {
                max: 30,message: '真实名字不能超过30个字符'
              }
            ]},
          phone:{rules: [{validator: this.validatePhone}]},
          email:{
            rules: [{required: true,message:'请输入邮箱!',},{
              validator: this.validateEmail
            }],
          },
          roles:{},
          //  sex:{initialValue:((!this.model.sex)?"": (this.model.sex+""))}
          workNo: {
            rules: [
              // { required: true, message: '请输入工号' },
              // { validator: this.validateWorkNo }
            ]
          },
          checkedRoles: {
            rules: [
              { required: true, message: '请选择角色' }
            ]
          },
          orgCode: {
            rules: [
              { required: true, message: '请选择行政机构'},
            ]
          },
          departCode: {
            rules: [
              { required: true, message: '请选择行政部门'},
            ]
          },
          checkedDepartNameString: {
            rules: [
              { required: true, message: '请选择部门'},
            ]
          },
          platformCode:{
            rules: [
              { required: true, message: '请选择平台' },
            ]
          },
          telephone: {
            rules: [
              { pattern: /^0\d{2,3}-[1-9]\d{6,7}$/, message: '请输入正确的座机号码' },
            ]
          }
        },
        departIdShow:false,
        departIds:[], //负责部门id
        title:"新增",
        visible: false,
        model: {},
        roleList:[],
        selectedRole:[], // 用户已经分配的角色列表id（编辑时）
        selectedRoleObj: [], // 用户已经分配的角色列表对象集合（编辑时）
        selectedRoleName:[], // 角色分配选中的角色名列表
        selectedRoleIds:[], // 角色分配选中的角色id列表
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        uploadLoading:false,
        confirmLoading: false,
        headers:{},
        form:this.$form.createForm(this),
        picUrl: "",
        url: {
          fileUpload: window._CONFIG['domianURL'] + "/files/up",
          imgerver: window._CONFIG['domianURL'] + "/files/find",
          userWithDepart: "/sys/user/userOrgList", // 引入为指定用户查看部门信息需要的url
          userOrg: "sys/user/userOrg",// 指定用户的行政机构url
          userId:"/sys/user/generateUserId", // 引入生成添加用户情况下的url
          syncUserByUserName:"/process/extActProcess/doSyncUserByUserName",//同步用户到工作流
          platformList: "/sys/platform/list/all",
          queryCurrentAndNextLevelOrg: "/sys/sysOrg/queryCurrentAndNextLevelOrg", // 获取行政机构的下拉列表
          querySubordinateDeparts: "/sys/sysOrg/queryCurrentOrgAndDeparts", // 获取行政部门的下拉列表
          roleList: "/sys/role/queryRoleTreeByOrg", // 角色列表
        },
        identity:"1",
        fileList:[],
        userTypeOptions: [],
        bizPath: process.env.VUE_APP_OSS_PATH
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
      this.getOrgCodeList()
      this.getEncrypte();
    },
    computed:{
      uploadAction:function () {
        return this.url.fileUpload;
      },
      userTypeDisabled: function () {
        return this.model.username === 'admin' ? true : false;
      },
    },
    methods: {
      isDisabledAuth(code){
        return disabledAuthFilter(code);
      },
      //窗口最大化切换
      toggleScreen(){
        if(this.modaltoggleFlag){
          this.modalWidth = window.innerWidth;
        }else{
          this.modalWidth = 800;
        }
        this.modaltoggleFlag = !this.modaltoggleFlag;
      },
      // initialRoleList(){
      //   // 使用带有权限过滤的角色列表接口
      //   queryRolesWithPermission().then((res)=>{
      //     if(res.success){
      //       this.roleList = res.result;
      //     }else{
      //       console.log(res.message);
      //     }
      //   });
      // },
      //加载用户类型数据
      loadUserTypes() {
        //根据字典Code, 初始化字典数组
        ajaxGetDictItems('user_type', null).then((res) => {
          if (res.success) {
            this.userTypeOptions = res.result.filter((item) => {
              if(this.$store.getters.userInfo.type == '9999'){ //超管
                return item
              }
              if(this.$store.getters.userInfo.type == '1' && item.value != '9999'){ //普通管理员
                return item
              }
              if(this.$store.getters.userInfo.type == '0' && item.value == '0'){ //普通用户
                return item
              }
            });
          }
        })
      },
      // 获取用户已经分配的角色（编辑时使用）
      loadUserRoles(userid){
        queryUserRole({userid:userid}).then((res)=>{
          if(res.success){
            this.selectedRole = res.result;
          }else{
            this.$message.warning(res.message)
          }
        }).catch(err => {
          this.$message.warning(err.message)
        })
      },
      // 获取当前所选行政机构下所有的角色列表（用于编辑时匹配用户已经分配的角色）
      getRoleTreeList(){
        getAction(this.url.roleList, {
          orgCode: this.selectedOrgCodeValue,
        }).then(res => {
          let arr = []
          if(res.result && res.result.length){
            res.result.forEach(item => {
              if(item.roles && item.roles.length){
                item.roles.forEach(val => {
                  arr.push({
                    ...val,
                    checked2: true,
                    platformCode: item.platformCode,
                    platformName: item.platformName,
                  })
                })
              }
            })
          }
          arr.forEach(item => {
            this.selectedRole.forEach(val => {
              if(val == item.id){
                this.selectedRoleObj.push(item)
              }
            })
          })
          this.selectedRoleIds = this.selectedRoleObj.map(item => item.id)
          this.selectedRoleName = this.selectedRoleObj.map(item => item.roleName)
          this.form.setFieldsValue({
            selectedRole: this.selectedRoleName.join(',')
          })
        })
      },
      loadPlatformList(record){
        let that = this;
        if(record.hasOwnProperty("platformCode")){
          that.platformCode = record.platformCode;
        }
        getAction(this.url.platformList).then((res)=>{
          if(res.success){
            that.platformList = res.result.records;
          }else{
            console.log(res.message);
          }
        })
      },
      refresh () {
          this.selectedOrgCodeValue = ""
          this.selectedOrgCodeKey = ""
          this.selectedDepartKeys=[];
          this.checkedDepartKeys=[];
          this.checkedDepartNames=[];
          this.checkedDepartNameString = "";
          this.selectedRoleObj = [],
          this.selectedRoleIds = [],
          this.selectedRoleName = [],
          this.userId=""
          this.resultDepartOptions=[];
          this.departId=[];
          this.departIdShow=false;
      },
      add () {
        this.picUrl = "";
        this.refresh();
        this.edit({activitiSync:'1'});
      },
      edit (record) {
        this.resetScreenSize(); // 调用此方法,根据屏幕宽度自适应调整抽屉的宽度
        let that = this;
        that.loadUserTypes();
        that.onChangeType(record.type)
        //that.loadPlatformList(record);
        // that.initialRoleList();
        that.checkedDepartNameString = "";
        that.form.resetFields();
        if(record.hasOwnProperty("id")){
          that.loadUserRoles(record.id);
          setTimeout(() => {
            this.fileList = record.avatar;
          }, 5)
        }
        that.userId = record.id;
        that.visible = true;
        that.model = Object.assign({}, record);
        //DictSelectTag值需要String类型，因此这里做一个转换，防止报错
        if (that.model.type!=undefined) {
          that.model.type = that.model.type + ''
        }
        that.$nextTick(() => {
          that.form.setFieldsValue(pick(this.model,
            'type',
            'username',
            'departCode',
            'sex',
            'realname',
            'email',
            'phone',
            'activitiSync',
            'workNo',
            'telephone',
            'post'
          ))
        });
        this.selectType = (record.type).toString() ? (record.type).toString() : ''
        //身份为上级显示负责部门，否则不显示
        if(this.model.userIdentity=="2"){
            this.identity="2";
            this.departIdShow=true;
        }else{
            this.identity="1";
            this.departIdShow=false;
        }
        // 调用查询用户对应的部门信息的方法
        that.checkedDepartKeys = [];
        that.loadCheckedDeparts();
        that.loadCheckedOrgCode()
      },
      // 获取选中的行政机构(编辑时)
      loadCheckedOrgCode(){
        if(!this.userId){return}
        getAction(this.url.userOrg, {
          userId: this.userId
        }).then(res =>{
          if(res.success){
            this.form.setFieldsValue({
              orgCode: res.result.orgCode
            })
            this.selectedOrgCodeValue = res.result.orgCode
            this.selectedOrgCodeKey = res.result.value
            this.getAdminDepartList(this.selectedOrgCodeValue)
            this.getRoleTreeList()
          } else {
            this.$message.warning(res.message)
          }
        }).catch(err => {
          this.$message.warning(err.message)
        })
      },
      // 获取选中的可登录部门
      loadCheckedDeparts(){
        let that = this;
        if(!that.userId){return}
        getAction(that.url.userWithDepart,{userId:that.userId}).then((res)=>{
          that.checkedDepartNames = [];
          if(res.success){
            var depart=[];
            var departId=[];
            for (let i = 0; i < res.result.length; i++) {
              that.checkedDepartNames.push(res.result[i].title);
              this.checkedDepartNameString = this.checkedDepartNames.join(",");
              that.checkedDepartKeys.push(res.result[i].key);
              //新增负责部门选择下拉框
              depart.push({
                  key:res.result[i].key,
                  title:res.result[i].title
              })
              departId.push(res.result[i].key)
            }
            this.form.setFieldsValue({
              checkedDepartNameString: this.checkedDepartNameString
            })
            that.resultDepartOptions=depart;
            //判断部门id是否存在，不存在择直接默认当前所在部门
            if(this.model.departIds){
                this.departIds=this.model.departIds.split(",");
            }else{
                this.departIds=departId;
            }
            that.userDepartModel.departIdList = that.checkedDepartKeys
          }else{
            console.log(res.message);
          }
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.disableSubmit = false;
        this.selectedRole = [];
        this.userDepartModel = {userId:'',departIdList:[]};
        this.selectedOrgCodeValue = "";
        this.selectedOrgCodeKey = ""
        this.checkedDepartNames = [];
        this.checkedDepartNameString='';
        this.selectedRoleObj = [],
        this.selectedRoleIds = [],
        this.selectedRoleName = [],
        this.checkedDepartKeys = [];
        this.selectedDepartKeys = [];
        this.resultDepartOptions=[];
        this.departIds=[];
        this.departIdShow=false;
        this.identity="1";
        this.fileList=[];
        this.platformCode = undefined
      },
      moment,
      //获取密码加密规则
      getEncrypte(){
        var encryptedString = Vue.ls.get(ENCRYPTED_STRING);
        if(encryptedString == null){
          getEncryptedString().then((data) => {
            this.encryptedString = data
          });
        }else{
          this.encryptedString = encryptedString;
        }
      },
      handleSubmit () {
        const that = this;
        console.log('提交信息',that)
        console.log('提交表单',this.form)
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            if(!values.birthday){
              values.birthday = '';
            }else{
              values.birthday = values.birthday.format(this.dateFormat);
            }
            let formData = Object.assign(this.model, values);
            //formData.avatar = avatar;
            if(that.fileList != ''){
              formData.avatar = that.fileList;
            }else{
              formData.avatar = null;
            }

            var roleArr = this.selectedRoleIds
            formData.selectedroles = roleArr.length > 0 ? roleArr.join(",") : ''

            // formData.selectedroles = this.selectedRole.length>0?this.selectedRole.join(","):'';
            formData.selecteddeparts = this.userDepartModel.departIdList.length>0?this.userDepartModel.departIdList.join(","):'';
            formData.userIdentity=this.identity;
            //如果是上级择传入departIds,否则为空
            if(this.identity==="2"){
              formData.departIds=this.departIds.join(",");
            }else{
              formData.departIds="";
            }
            //formData.platformCode = this.platformCode;
            // that.addDepartsToUser(that,formData); // 调用根据当前用户添加部门信息的方法
            console.log('表单',formData);
            that.confirmLoading = true;

            if(!formData.workNo){
              formData.workNo = formData.username
            }

            let obj;
            if(!this.model.id){
              //密码加密
              formData.password =  encryption(values.password,that.encryptedString.key,that.encryptedString.iv);
              formData.confirmpassword =  encryption(values.confirmpassword,that.encryptedString.key,that.encryptedString.iv);

              obj=addUser(formData);
            }else{
              obj=editUser(formData);
            }
            obj.then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.close();
                that.$emit('ok');
                that.checkedDepartNames = [];
                that.userDepartModel.departIdList = {userId:'',departIdList:[]};
              }else{
                that.$message.warning(res.message);
              }
            }).catch(err => {
              that.$message.warning(err.message);
            })
            .finally(() => {
              that.confirmLoading = false;
            })

          }
        })
      },
      handleCancel () {
        this.close()
      },
      validateToNextPassword  (rule, value, callback) {
        const form = this.form;
        const confirmpassword=form.getFieldValue('confirmpassword');

        if (value && confirmpassword && value !== confirmpassword) {
          callback('两次输入的密码不一样！');
        }
        if (value && this.confirmDirty) {
          form.validateFields(['confirm'], { force: true })
        }
        callback();
      },
      compareToFirstPassword  (rule, value, callback) {
        const form = this.form;
        if (value && value !== form.getFieldValue('password')) {
          callback('两次输入的密码不一样！');
        } else {
          callback()
        }
      },
      validatePhone(rule, value, callback){
        if(!value){
          callback()
        }else{
          //update-begin--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
          if(new RegExp(/^1[2|3|4|5|6|7|8|9][0-9]\d{8}$/).test(value)){
            //update-end--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
            callback()
          }else{
            callback("请输入正确格式的手机号码!");
          }
        }
      },
      validateEmail(rule, value, callback){
        if(!value){
          callback()
        }else{
          if(new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/).test(value)){
            callback()
          }else{
            callback("请输入正确格式的邮箱!")
          }
        }
      },
      validateUsername(rule, value, callback){
        if(!value){
          callback()
          return
        }
        var params = {
          tableName: 'PRI_USER',
          fieldName: 'ACCOUNT_NAME',
          fieldVal: value,
          dataId: this.userId
        };
        duplicateCheck(params).then((res) => {
          if (res.success) {
          callback()
        } else {
          callback("用户名已存在!")
        }
      })
      },
      validateWorkNo(rule, value, callback){
        var params = {
          tableName: 'PRI_USER',
          fieldName: 'work_no',
          fieldVal: value,
          dataId: this.userId
        };
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback("工号已存在!")
          }
        })
      },
      handleConfirmBlur  (e) {
        const value = e.target.value;
        this.confirmDirty = this.confirmDirty || !!value
      },

      normFile  (e) {
        console.log('Upload event:', e);
        if (Array.isArray(e)) {
          return e
        }
        return e && e.fileList
      },
      beforeUpload: function(file){
        var fileType = file.type;
        if(fileType.indexOf('image')<0){
          this.$message.warning('请上传图片');
          return false;
        }
      },
      handleChange (info) {
        this.picUrl = "";
        if (info.file.status === 'uploading') {
          this.uploadLoading = true;
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response;
          this.uploadLoading = false;
          console.log(response);
          if (response.success) {
                      this.model.avatar = response.result.path;
                      this.picUrl = this.model.avatar;
                    } else {
                      this.$message.warning(response.message);
                    }
        }
      },
      // 选择用户类型
      onChangeType(value){
        this.selectType = value
        let type = this.$store.getters.userInfo.type // 登录用户的用户类型
        let orgCode = this.$store.getters.userInfo.orgCode // 登录用户的行政机构
        this.selectedOrgCodeValue = ""
        this.selectedOrgCodeKey = ""
        this.selectedAdminDepartKeys = ""
        this.adminDepart = ""
        this.checkedDepartNames = [];
        this.selectedDepartKeys = [];
        this.checkedDepartKeys = [];
        this.checkedDepartNameString = ""
        this.selectedRole = []
        this.selectedRoleObj = []
        this.selectedRoleName = []
        this.selectedRoleIds = []
        this.form.setFieldsValue({
          orgCode: "",
          checkedDepartNameString: "",
          departCode: '',
          selectedRole: ""
        })
        if(type == 9999){ // 登录用户为超级管理员
          if(value == 9999) { // 用户类型为超级管理员
            // 行政机构本机和下级都可以选择
            this.orgCodeList = this.orgCodeList2.filter(item => {
              return item.parentId == null
            })
          } else {
            //超管可管理任意机构的普通管理员和普通用户
            this.orgCodeList = this.orgCodeList2
          }
        } else if(type == 1){ // 登录用户为管理员
          if(value == 1) { // 用户类型为管理员
            // 行政机构只能选择下级
            this.orgCodeList = this.orgCodeList2.filter(item => {
              return item.orgCode !== orgCode
            })
          } else if(value == 0){  // 用户类型为普通员工
            // 行政机构只能选择本机
            this.orgCodeList = this.orgCodeList2.filter(item => {
              return item.orgCode == orgCode
            })
          }
        } else if(type == 0) { // 登录用户为普通员工
          // 行政机构只能选择本机
          this.orgCodeList = this.orgCodeList2.filter(item => {
            return item.orgCode == orgCode
          })
        }

      },
      // 获取行政机构列表
      getOrgCodeList(){
        console.log('this.$store.getters.userInfo',this.$store.getters.userInfo)
        getAction(this.url.queryCurrentAndNextLevelOrg, {
          orgCode: this.$store.getters.userInfo.orgCode
        }).then(res => {
          if(res.success){
            this.orgCodeList = res.result
            this.orgCodeList2 = res.result
          } else {
            this.$message.warning(res.message)
          }
        }).catch(err => {
          this.$message.warning(err.message)
        })
        console.log('行政机构列表',this.orgCodeList)
      },
      // 选择行政机构
      onChangeOrgCode(value,option){
        this.selectedOrgCodeValue = value
        if(value){
          this.getAdminDepartList(value)
          this.selectedOrgCodeKey = option.data.key
        } else {
          this.adminDepartList = []
        }
        this.form.setFieldsValue({
          orgCode: value
        })
        this.checkedDepartNames = [];
        this.selectedDepartKeys = [];
        this.checkedDepartKeys = [];
        this.selectedRole = []
        this.selectedRoleObj = []
        this.selectedRoleName = []
        this.selectedRoleIds = []
        this.form.setFieldsValue({
          checkedDepartNameString: "",
          departCode: '',
          selectedRole: ""
        })
      },
      // 行政部门
      adminDepartFocus(){
        if(!this.selectedOrgCodeValue){
          this.$message.warning("请先选择行政机构")
        }
      },
      // 获取行政部门下拉列表
      getAdminDepartList(orgCode){
        getAction(this.url.querySubordinateDeparts, {orgCode}).then(res => {
          if(res.success){
            this.adminDepartList = res.result
          } else {
            this.$message.warning(res.message)
          }
        }).catch(err => {
          this.$message.warning(err.message)
        })
      },
      // 可登录部门
      onSearch(){
        if(!this.selectedOrgCodeValue){
          this.$message.warning("请先选择行政机构")
          return
        }
        this.$refs.departWindow.add(this.checkedDepartKeys,this.userId);
      },
      // 角色分配
      onSearchRole(){
        if(!this.selectType){
          this.$message.warning("请先选择用户类型")
          return
        }
        if(!this.selectedOrgCodeValue){
          this.$message.warning("请先选择行政机构")
          return
        }
        this.$refs.roleWindow.show(this.selectedRoleObj);
      },
      // 角色分配弹窗提交返回的数据
      modalFormOkRole(formData){
        console.log('返回参数',formData);
        this.selectedRoleIds = []
        this.selectedRoleName = []
        this.form.setFieldsValue({
          selectedRole: ''
        })
        formData.forEach(item => {
          this.selectedRoleIds.push(item.id)
          this.selectedRoleName.push(item.roleName)
          this.form.setFieldsValue({
            selectedRole: this.selectedRoleName.join(",")
          })
        })
      },
      // 获取用户对应部门弹出框提交给返回的数据
      modalFormOk (formData) {
        this.checkedDepartNames = [];
        this.selectedDepartKeys = [];
        this.checkedDepartNameString = '';
        this.userId = formData.userId;
        this.userDepartModel.userId = formData.userId;
        this.departIds=[];
        this.resultDepartOptions=[];
        var depart=[];
        for (let i = 0; i < formData.departIdList.length; i++) {
          this.selectedDepartKeys.push(formData.departIdList[i].key);
          this.checkedDepartNames.push(formData.departIdList[i].title);
          this.checkedDepartNameString = this.checkedDepartNames.join(",");
          //新增部门选择，如果上面部门选择后不为空直接付给负责部门
          depart.push({
              key:formData.departIdList[i].key,
              title:formData.departIdList[i].title
          })
          this.departIds.push(formData.departIdList[i].key)
        }
        this.form.setFieldsValue({
          checkedDepartNameString: this.checkedDepartNameString
        })
        this.resultDepartOptions=depart;
        this.userDepartModel.departIdList = this.selectedDepartKeys;
        this.checkedDepartKeys = this.selectedDepartKeys  //更新当前的选择keys
       },
      // 根据屏幕变化,设置抽屉尺寸
      resetScreenSize(){
        let screenWidth = document.body.clientWidth;
        if(screenWidth < 500){
          this.drawerWidth = screenWidth;
        }else{
          this.drawerWidth = 700;
        }
      },
      identityChange(e){
        if(e.target.value==="1"){
            this.departIdShow=false;
        }else{
            this.departIdShow=true;
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }

  /deep/ .ant-spin-container{
    padding-bottom: 15px;
  }

  .drawer-bootom-button {
    position: absolute;
    bottom: 0px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
    z-index: 2;
  }
</style>