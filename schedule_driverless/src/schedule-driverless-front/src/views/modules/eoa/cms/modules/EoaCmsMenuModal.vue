<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row class="form-row" :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="栏目名称">
              <a-input placeholder="请输入栏目名称" v-decorator="['menuName', { rules: [{ required: true, message: '栏目名称不允许为空!' }] }]"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="栏目类型">
              <j-dict-select-tag  type="radio" v-decorator="[ 'menuType', {}]" :trigger-change="true" dictCode="cms_menu_type" @change="optionChange"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :span="24">
          <a-col :md="12" :sm="24" v-if="menuType==1">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="栏目">
              <a-tree-select
                showSearch
                style="width: 100%"
                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                :treeData="treeData"
                placeholder='请选择分类列表'
                allowClear
                :getPopupContainer="node => node.parentNode"
                v-decorator="['parentId']"
                @change="handleChangePigsty('other',$event)"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24" v-if="menuType==2">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="链接地址">
              <a-input placeholder="请输入链接地址" v-decorator="['linkUrl', {}]"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="模版编码">
              <a-input placeholder="请输入模版编码" v-decorator="['templateCode', {}]"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="序号">
              <a-input-number v-decorator="[ 'serialNumber', {}]" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="显示">
              <a-radio-group :options="plainOptions" v-decorator="['isShow', {}]" style="width: 100%"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="图片上传">
              <j-image-upload :text="text" v-model="fileList" :isMultiple="true"></j-image-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import {duplicateCheck } from '@/api/api'
  import JImageUpload from '../../../../../components/jeecg/JImageUpload'
  export default {
    name: "EoaCmsMenuModal",
    components: { JImageUpload },
    data () {
      return {
        title:"操作",
        visible: false,
        plainOptions:[
          { label: '显示', value: '1' },
          { label: '不显示', value: '0' }
        ],
        options:[
          { label: '列表', value: '1' },
          { label: '链接', value: '2' }
        ],
        previewVisible: false,
        model: {},
        treeData:[],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        headers:{
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          add: "/chat/eoaCmsMenu/add",
          edit: "/chat/eoaCmsMenu/edit",
          selectTree:"/chat/eoaCmsMenu/selectTree",
          fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload'
        },
        parentId:null,
        fileList:[],
        uploadFileUid:0,
        menuType:"1",
        show:true,
        imageHref:"",
        previewImage:"",
        text:"上传"
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    methods: {
      add () {
        this.edit();
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        getAction(this.url.selectTree).then((res) =>{
          if(res.success){
            let treeArr=res.result;
            if(record&&record.id&&treeArr.length>0){
              this.treeData=treeArr.filter(item=>item.id!==record.id);
            }else{
              this.treeData=treeArr;
            }

          }
        });
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'menuName','parentId','menuType','linkUrl','templateCode','serialNumber'))
        this.form.setFieldsValue({isShow:this.model.isShow?this.model.isShow:"1"})
        this.form.setFieldsValue({menuType:this.model.menuType?this.model.menuType:"1"})
		   //时间格式化
        });
        if(this.model.id){
          setTimeout(() => {
            this.fileList = record.imageHref;
          }, 5)
        }
      },
      close () {
        this.$emit('close');
        this.fileList=[];
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
            if(this.fileList != '') {
              formData.imageHref = this.fileList;
            }else{
              formData.imageHref = '';
            }
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
      handleChangePigsty(flag,value) {
        this.parentId = value
//        this.onChange(flag, value)
      },
      optionChange(e){
       var value=e
        if(value=="1"){
          this.menuType=value
        }else{
          this.menuType=value
        }
      },
      handleCancel(code) {
        this.close();
        if(code==='pic'){
          this.previewVisible = false;
        }else{
          this.visible = false;
        }
      },
    }
  }
</script>

<style lang="less" scoped>

</style>