<template>
  <a-modal
    :title="title"
    :width="800"
    :ok=false
    :visible="visible"
    :confirmLoading="confirmLoading"
    :okButtonProps="{ props: {disabled: disableSubmit} }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" :hidden="seen" label="上级部门" hasFeedback>
          <a-tree-select
            style="width:100%"
            :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
            :treeData="orgTree"
            v-model="model.parentId"
            placeholder="请选择上级部门"
            :disabled="condition">
          </a-tree-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="绑定的机构">
          <a-select show-search :filter-option="searchShareOrg" mode="multiple" @change="changeShareOrg" v-model="selectShareOrgIds">
            <a-select-option v-for="(item, index) in shareOrgs" :key="index" :value="item.id">
              {{ item.orgName }}({{item.orgCode}})
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>

  </a-modal>
</template>

<script>
import {getAction, postAction} from "../../../api/manage";
  import pick from "lodash.pick";
  import {queryIdTree} from "../../../api/api";
  export default {
    name: "ShareOrgModal",
    components: {  },
    data () {
      return {
        orgTree:[],
        orgTypeData:[],
        orgName:"",
        selectShareOrgIds:[],
        title:"绑定共用机构",
        seen:false,
        visible: false,
        condition:true,
        disableSubmit:false,
        parentId:'',
        model: {},
        shareOrgs:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        url: {
          list: "/sys/orgEx/listShareOrgs",
          bind: "/sys/orgEx/bindShareOrgs",
        }
      }
    },
    created () {
    },
    methods: {

      init(parentId){
        this.visible = true
        this.selectShareOrgIds = []
        this.model = {}
        let that = this
        queryIdTree().then((res)=>{
          if(res.success){
            that.orgTree = [];
            for (let i = 0; i < res.result.length; i++) {
              let temp = res.result[i];
              that.orgTree.push(temp);
            }
          }

        })
        getAction(this.url.list,{pageNo:1,pageSiz:10000}).then(res => {
          if(res.code === 200 && res.result){
            this.shareOrgs = res.result
          }
        })

        this.form.resetFields();
        this.model = Object.assign({}, {});
        this.model.parentId = parentId;
      },
      close () {
        this.$emit('close');
        this.disableSubmit = false;
        this.selectShareOrgIds = []
        this.visible = false;
      },
      handleOk(){
        console.log('selectShareOrgIds',this.selectShareOrgIds)
        let params = {
          parentId : this.model.parentId,
          ids: this.selectShareOrgIds
        }
        this.confirmLoading = true
        postAction(this.url.bind,params).then(res=>{
          if(res.code === 200){
            this.$message.success("绑定成功！")
            this.confirmLoading = false
            this.close()
          }else {
            this.$message.warning(res.message)
            this.confirmLoading = false
          }
        })

      },
      handleCancel () {
        this.close()
      },
      searchShareOrg(value, option){
        return option.componentOptions.children[0].text.indexOf(value) >= 0
      },
      changeShareOrg(value,op){
        console.log('valeu',value)
        console.log('op',op)
        this.selectShareOrgIds = value
      }
    }
  }
</script>

<style scoped>

</style>