<template>

  <a-modal
    title="调整用户信息"
    :width="650"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @cancel="handleCancel"
    @ok="handleOk"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="用户列表">
          <!-- table区域-begin -->
          <div>
            <a-table
              size="middle"
              bordered
              :columns="columns"
              :loading="loading"
              :scroll="{ x: true }"
              :data-source="data"
              :pagination="false"
              @change="handleTableChange"
            >
            </a-table>
        </div>
        </a-form-item>
        <a-form-item label="用户类型"
                     :labelCol="labelCol"
                     :wrapperCol="wrapperCol">
          <j-dict-select-tag v-model="selectType" dict-code="user_type" :exclude-values="excludeUserTypes"/>
        </a-form-item>
        <a-form-item label="行政机构"
                     :labelCol="labelCol"
                     :wrapperCol="wrapperCol">
          <g-org-select type="orgCategorys" :orgCategorys="orgCategorys"
                        showSearch :multiple="multiple" allowClear treeNodeFilterProp="title" placeholder='请选择行政机构'
                        :treeDefaultExpandAll="false" v-model="orgCode" @change="onChangeOrg"></g-org-select>
        </a-form-item>
        <!--可登录部门-->
        <a-form-item label="可登录部门" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-search
            placeholder="点击选择部门"
            readOnly
            v-decorator="[ 'checkedDepartNameString']"
            @search="onSearch">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>
        <a-form-item label="角色分配" :labelCol="labelCol" :wrapperCol="wrapperCol"  >
          <a-input-search
            placeholder="点击选择角色分配"
            readOnly
            v-decorator="[ 'selectedRole']"
            @search="onSearchRole">
            <a-button slot="enterButton" icon="search">选择</a-button>
          </a-input-search>
        </a-form-item>
      </a-form>
    </a-spin>
    <!-- 可登录部门 -->
    <depart-window ref="departWindow" @ok="selectLoginOrgs" :selectedOrgCodeValue="orgCode"
                   :selectedOrgCodeKey="orgCode"></depart-window>
    <!-- 角色分配 -->
    <RoleWindow ref="roleWindow" @ok="modalFormOkRole" :selectedOrgCodeValue="orgCode" :selectType="selectType" />
  </a-modal>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import GOrgSelect from "../components/GOrgSelect";
import {postAction} from "../../../api/manage";
import departWindow from "./DepartWindow2";
import DictItemList from "../DictItemList";
import RoleWindow from "./RoleWindow";
export default {
  name: "AdjustUserOrgModal",
  components: {DictItemList, GOrgSelect,departWindow,RoleWindow},
  mixins: [JeecgListMixin, mixinDevice],
  data() {
    return {
      visible: false,
      description: '调整的用户信息',
      disableMixinCreated: true,
      multiple: false,
      orgCategorys:[0,1,5,7],
      excludeUserTypes:['9999'],
      selectedOrgCodeValue: '',
      selectedOrgCodeKey: '',
      selectUserName: '',
      checkedDepartNames: [],
      selectedDepartKeys:[],
      checkedDepartNameString:'',
      departIds:[],
      selectedRoleObj: [],
      selectType: "", // 选择的用户类型
      resultDepartOptions:[],
      departIdList:[],
      orgCode:'',
      data:[],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      form: this.$form.createForm(this),
      /* 排序参数 */
      isorter:{
        column: 'updateTime',
        order: 'desc',
      },
      // 表头
      columns: [
        {
          title: '账号',
          align: "center",
          dataIndex: 'userName',
          width: 120,
        },
        {
          title: '真实名字',
          align: "center",
          width: 100,
          dataIndex: 'realname',
        },
        {
          title: '行政机构',
          align: "center",
          width: 180,
          dataIndex: 'orgName'
        },
        // {
        //   title: '登录机构',
        //   align: "center",
        //   width: 180,
        //   dataIndex: 'loginOrgs'
        // },
      ],
      url: {
        adjustOrg: '/sys/sysOrg/adjustUserOrg',
      },
      width: 600,
      confirmLoading: false,
      importVisible: false,
      format: false,
      fileList: [],
      dictOptions: {},
      userIds:[],
    }
  },
  methods: {
    initData(records){
      this.userIds = []
      this.orgCode = ''
      this.selectType = ''
      this.selectedRoleIds = []
      this.selectedDepartKeys = []
      this.selectedRoleIds = []
      this.selectedRoleName = []
      this.selectedRoleObj = []
      this.form.setFieldsValue({
        selectedRole: '',
        checkedDepartNameString:''
      })
      this.queryParam.orgCode = ''
      console.log('record ',records)
      this.visible = true
      this.data = records
      if(records){
        for(let i=0;i < records.length; i++){
          let user = records[i]
          console.log('user',user)
          if(user && user.id){
            this.userIds.push(user.id)
          }
        }
      }
    },
    handleCancel(){
      this.visible = false
      this.$emit('ok')
    },
    handleOk(){

      console.log('userIds',this.userIds)
      if(this.userIds.length === 0){
        this.$message.warning("没有选中用户")
        return
      }
      if(!this.orgCode){
        this.$message.warning("请先选择行政机构")
        return
      }
      if(!this.selectType){
        this.$message.warning("请先选择用户类型")
        return
      }
      if(!this.selectedDepartKeys || this.selectedDepartKeys.length === 0){
        this.$message.warning("请先选择用户可登录机构")
        return
      }

      if(!this.selectedRoleIds || this.selectedRoleIds.length === 0){
        this.$message.warning("请先选择用户的角色")
        return
      }


      this.$confirm({
        title: '确认操作',
        content: '该操作会调整当前批次用户的相关信息，请确认是否需要执行？',
        onOk: () => {
          let params = {
            userIds:this.userIds,
            orgCode: this.orgCode,
            loginOrgIds: this.selectedDepartKeys,
            userType: this.selectType,
            roleIds:this.selectedRoleIds
          }
          postAction(this.url.adjustOrg,params).then(res=>{
            if(res.success){
              this.$message.success("调整用户信息成功")
              this.handleCancel()
              return
            }else {
              this.$message.success("调整用户信息失败："+res.message)
            }
          })
        }
      })
    },
    onChangeOrg(value) {
      this.orgCode = value
      this.form.setFieldsValue({
        checkedDepartNameString: "",
        departCode: '',
        selectedRole: ""
      })
    },
    selectLoginOrgs(formData){
      this.checkedDepartNames = [];
      this.selectedDepartKeys = [];
      this.checkedDepartNameString = '';
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
      this.checkedDepartKeys = this.selectedDepartKeys  //更新当前的选择keys
    },
    onChange(value) {
      console.log(value)
      this.orgCodes = value
      this.queryParam.orgCode = value
    },
    onSearch() {
      if(!this.orgCode){
        this.$message.warning("请选择行政机构")
        return
      }
      this.$refs.departWindow.add([this.orgCode],'');
    },
    onSelect() {
      console.log(...arguments)
    },
    onSearchRole(){
      if(!this.selectType){
        this.$message.warning("请选择用户类型")
        return
      }
      if(!this.orgCode){
        this.$message.warning("请选择行政机构")
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

  }

}
</script>

<style scoped>
@import '~@assets/less/common.less';
</style>