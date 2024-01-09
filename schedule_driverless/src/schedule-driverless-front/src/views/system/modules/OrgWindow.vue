<template>
  <a-modal
    :width="modalWidth"
    :visible="visible"
    title="部门搜索"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭"
    wrapClassName="ant-modal-cust-warp"
    >
    <!--部门树-->
    <template>
      <a-form :form="form">
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级部门">
      <a-tree
        multiple
        treeCheckable="tree"
        checkable
        autoExpandParent
        :checkedKeys="checkedKeys"
        :expandedKeys.sync="expandedKeys"
        allowClear="true"
        :checkStrictly="true"
        @check="onCheck"
        :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
        :treeData="orgTree"
        placeholder="请选择上级部门"
        >
      </a-tree>
      </a-form-item>
      </a-form>
    </template>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import { getAction } from '@/api/manage'
  import { queryIdTree,queryOrgTreeByUserId } from '@/api/api'
  export default {
    name: "OrgWindow",
    data () {
      return {
        expandedKeys:[],//展开指定的树节点
        checkedKeys:[], // 存储选中的部门id
        userId:"", // 存储用户id
        model:{}, // 存储SysUserOrgsVO表
        userOrgModel:{userId:'',orgIdList:[]}, // 存储用户id一对多部门信息的对象
        orgList:[], // 存储部门信息
        modalWidth:400,
        orgTree:[],
        title:"操作",
        visible: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        headers:{},
        form:this.$form.createForm(this),
        url: {
          userId:"/sys/user/generateUserId", // 引入生成添加用户情况下的url
        },
      }
    },
    methods: {
      add (checkedOrgKeys,userId) {
        this.checkedKeys = checkedOrgKeys;
        this.expandedKeys = this.checkedKeys;
        this.userId = userId;
        this.edit({});
      },
      edit (record) {
        this.orgList = [];
        this.queryOrgTree();
        this.form.resetFields();
        this.visible = true;
        this.model = Object.assign({}, record);
        let filedsVal = pick(this.model,'id','userId','orgIdList');
        this.$nextTick(() => {
          this.form.setFieldsValue(filedsVal);
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.orgList = [];
        this.checkedKeys = [];
      },
      handleSubmit () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err) => {
          if (!err) {
            that.confirmLoading = true;
            if(this.userId == null){
              getAction(this.url.userId).then((res)=>{
                if(res.success){
                  let formData = {userId:res.result,
                  orgIdList:this.orgList}
                  console.log(formData)
                  that.$emit('ok', formData);
                }
              }).finally(() => {
                that.orgList = [];
                that.confirmLoading = false;
                that.close();
              })
            }else {
              let formData = {userId:this.userId,
                orgIdList:this.orgList}
              console.log(formData)
              that.orgList = [];
              that.$emit('ok', formData);
              that.confirmLoading = false;
              that.close();
            }
          }
        })
      },
      handleCancel () {
        this.close()
      },

      // 选择部门时作用的API
      onCheck(checkedKeys, info){
        this.orgList = [];
        this.checkedKeys = checkedKeys.checked;
        let checkedNodes = info.checkedNodes;
        for (let i = 0; i < checkedNodes.length; i++) {
          let de = checkedNodes[i].data.props;
          let org = {key:"",value:"",title:""};
          org.key = de.value;
          org.value = de.value;
          org.title = de.title;
          this.orgList.push(org);
        }
        console.log('onCheck', checkedKeys, info);
      },
      queryOrgTree(){
        queryOrgTreeByUserId({userId:this.$store.getters.userInfo.id}).then((res)=>{
            if(res.success){
            this.orgTree = res.result;
          }
        })  
        // queryIdTree().then((res)=>{
        //   if(res.success){
        //     this.orgTree = res.result;
        //   }
        // })
      },
      modalFormOk(){

      }
      },
  }
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }
</style>