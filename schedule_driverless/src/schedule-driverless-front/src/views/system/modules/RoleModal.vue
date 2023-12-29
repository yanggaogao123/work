<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    style="top:5%;height: 85%;overflow-y: hidden">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

  <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="所属部门">
              <g-org-select showSearch treeNodeFilterProp="title" placeholder='请选择所属部门' :treeDefaultExpandAll="false"
                            v-decorator="[ 'orgCode',validatorRules.orgCode]" @change="onChange" type="role"></g-org-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="所属平台">
              <g-platform-select isSetDefaultValue placeholder="请选择平台" :triggerChange="true" @change="handlePlatformChange" v-decorator="[ 'platformCode', validatorRules.platformCode]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="角色类型">
          <a-select v-decorator="['roleType',validatorRules.roleType]">
            <a-select-option :value="1" v-if="orgCodeLevel">
              机构级
            </a-select-option>
            <a-select-option :value="2" v-if="userLevel">
              用户级
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="角色名称">
          <a-input placeholder="请输入角色名称" v-decorator.trim="[ 'roleName', validatorRules.roleName]" />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="角色编码">
          <a-input placeholder="角色编码可自定义或者由系统自动生成" :disabled="roleDisabled" v-decorator.trim="[ 'roleCode', validatorRules.roleCode]" />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="描述">
          <a-textarea :rows="5" placeholder="..." v-decorator="[ 'description', validatorRules.description ]" />
        </a-form-item>

      </a-form>
    </a-spin>
    <org-select ref="orgSelect" :closable="true" title="部门切换"></org-select>

  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import {addRole,editRole,duplicateCheck } from '@/api/api'
   import OrgSelect from '@/components/tools/OrgSelect'
    import GPlatformSelect from '../components/GPlatformSelect'
    import GOrgSelect from '../components/GOrgSelect'

  export default {
    name: "RoleModal",
      components: {
          OrgSelect,
          GPlatformSelect,
          GOrgSelect
        },
    data () {
      return {
        title:"操作",
        visible: false,
        roleDisabled: false,
        roleTypes:[],
        roleType:'',
        orgCodeLevel: false,
        userLevel:false,
        userType: "", // 当前登录用户的类型
        orgCode: "", // 挡墙登录用户的行政机构
        orgCategorys: [0,1,5,7],
        multiple: false,
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
          orgCode:{
            rules: [
              { required: true,message: '请选择所属机构' }
            ]},
          platformCode:{
            rules: [
              { required: true ,message: '请选择平台'}
            ]},
          roleType:{
            rules: [
              { required: true ,message: '请选择角色类型'}
            ]},
          roleName:{
            rules: [
              { required: true, message: '请输入角色名称!' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
            ]},
          roleCode:{
            rules: [
              { required: false, message: '请输入角色名称!'},
              { min: 0, max: 64, message: '长度不超过 64 个字符', trigger: 'blur' },
              { validator: this.validateRoleCode}
            ]},
          description:{
            rules: [
              { min: 0, max: 126, message: '长度不超过 126 个字符', trigger: 'blur' }
            ]}
        },
      }
    },
    created () {
    },
    mounted() {
      this.userType = this.$store.getters.userInfo.type, // 9999: 超级管理员；1: 普通管理员；0: 普通用户；
      this.orgCode = this.$store.getters.userInfo.sysOrgCode
    },
    computed: {
          currentOrgCode() {
            return this.$store.getters.userInfo.sysOrgCode
          }
        },
    methods: {
      add () {
        this.edit({});
        this.orgCodeLevel = true
        this.userLevel = true
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;

        if(record){
          this.changeRoleTypes(record.orgCode)
        }

        //编辑页面禁止修改角色编码
        if(this.model.id){
          this.roleDisabled = true;
        }else{
          this.roleDisabled = false;
        }
        this.$nextTick(() => {
         this.form.setFieldsValue(pick(this.model, 'orgCode','sysOrgCode','roleName', 'description', 'roleCode', 'platformCode','roleType'))
          if (!this.currentOrgCode) {
            this.form.setFieldsValue({
              'sysOrgCode': this.currentOrgCode
            })
          }
        });

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
            let formData = Object.assign(this.model, values);
            let obj;
            console.log(formData)
            if(!this.model.id){
              obj=addRole(formData);
            }else{
              obj=editRole(formData);
            }
            obj.then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
                that.close();
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
        })
      },
      handleCancel () {
        this.close()
      },
      validateRoleCode(rule, value, callback){
        if(!value){
          callback();
          return
        }
        if(/[\u4E00-\u9FA5]/g.test(value)){
          callback("角色编码不可输入汉字!");
        }else{
          var params = {
            tableName: "sys_role",
            fieldName: "role_code",
            fieldVal: value,
            dataId: this.model.id,
          };
          duplicateCheck(params).then((res)=>{
            if(res.success){
              callback();
            }else{
              callback(res.message);
            }
          });
        }
      },
      handleRoleCodeChange(e) {
        this.$nextTick(() => {
          let roleCode = e.target.value
          if (!roleCode.startsWith(this.model.platformCode + "_")) {
            roleCode = this.model.platformCode + "_" + roleCode
          }
          this.form.setFieldsValue({
            'roleCode': roleCode
          })
        });
      },
      handlePlatformChange(e){
        this.model.platformCode=e
      },
      onChange(value) {
        console.log(value)
        // this.sysOrgIds = value
        this.model.orgCode=value

        //同步修改用户角色
        this.changeRoleTypes(value)
      },
      changeRoleTypes(orgCode){
        console.log('当前机构：'+this.orgCode+",传入机构："+orgCode)
        console.log('当前用户类型：'+this.userType)
        if(this.userType == '9999') { // 超级管理员
          this.userLevel = true
          this.orgCodeLevel = true
        } else if(this.userType == '1') { // 普通管理员
          if(orgCode == this.orgCode){ // 选择的机构是当前机构
            // 角色类型只能是用户级，不展示机构级
            this.userLevel = true
            this.orgCodeLevel = false
          } else { // 选择的机构是当前机构的下一级
            // 角色类型只能是机构级，不展示用户级
            this.orgCodeLevel = true
            this.userLevel = false
          }
        } else if(this.userType == '0') { // 普通用户
          // 角色类型只能是用户级，不展示机构级
          this.userLevel = true
          this.orgCodeLevel = false
        }
      },
    }
  }
</script>

<style scoped>

</style>