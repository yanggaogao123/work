<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :loading="loading"
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
              label="栏目">
              <a-tree-select
                showSearch
                style="width: 100%"
                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                :treeData="treeData"
                placeholder='请选择栏目'
                allowClear
                :getPopupContainer="node => node.parentNode"
                v-decorator="['columnId',validatorRules.columnId]"
                @change="handleChangePigsty('other',$event)"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="标题">
              <a-input placeholder="请输入标题" v-decorator="['title',{ rules: [{ required: true, message: '标题不允许为空!' }] }]"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="作者">
              <a-input placeholder="请输入作者" v-decorator="['author', {}]" disabled/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="关键字">
              <a-input placeholder="请输入关键字" v-decorator="['keywords', {}]"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="是否发布">
              <a-radio-group :options="plainOptions" v-decorator="['isPublish', {}]" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="文章类型">
              <a-select v-decorator="['type',{initialValue:'article'}]" @change="typeChange">
                <a-select-option value="article">文章</a-select-option>
                <a-select-option value="file">文件</a-select-option>
                <a-select-option value="url">链接</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :span="24">
          <a-col style="margin-left: 2%">
            <a-form-item
              :labelCol="labelCol1"
              :wrapperCol="wrapperCol1"
              label="摘要">
              <a-textarea placeholder="请输入摘要" v-decorator="['summary', {}]" class="summaryTextarea"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              label="封面图片">
              <j-image-upload text="上传" v-model="imgList" ></j-image-upload>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              v-if="type=='url'"
              label="链接地址">
              <a-input  placeholder="请输入链接地址" v-decorator="['linkUrl', {}]" required/>
            </a-form-item>
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              v-else-if="type=='file'"
              label="文件">
              <j-upload text="上传" v-model="fileList" :number="1"></j-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :span="24">
          <a-col :md="12" :sm="24">
            <a-form-item
              :labelCol="labelCol"
              :wrapperCol="wrapperCol"
              v-if="type=='article'"
              label="内容">
              <j-editor  v-model="message" ref="editor" style="width: 966px;"></j-editor>
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
  import JEditor from '@/components/jeecg/JEditor.vue';
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import store from '@/store'
  import JImageUpload from '@/components/jeecg/JImageUpload'
  import JUpload from '@/components/jeecg/JUpload'
  export default {
    name: "EoaCmsArticleModal",
    components: {
      JImageUpload,
      JEditor,
      JUpload
    },
    data () {
      return {
        title:"操作",
        type:"article",
        visible: false,
        imageHref:"",
        model: {},
        plainOptions:[
          { label: '是', value: '1' },
          { label: '否', value: '0' }
        ],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol1: {
          xs: { span: 24 },
          sm: { span: 2 },
        },
        wrapperCol1: {
          xs: { span: 24 },
          sm: { span: 22 },
        },
        loading:false,
        message:'',
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          columnId: {
            rules: [
              {required: true, message: '请选择栏目!'},
            ]
          }
        },
        url: {
          add: "/cms/eoaCmsArticle/add",
          edit: "/cms/eoaCmsArticle/edit",
          fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
          selectTree:"/chat/eoaCmsMenu/selectTree"
        },
        columnId:null,
        imgList:[],
        fileList:[],
        treeData:[],
        headers:{},
        uploadFileUid:0,
        previewVisible:false,
        previewImage:""
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.loading=true

        getAction(this.url.selectTree).then((res) =>{
        if(res.success){
          this.treeData=res.result;
        }
      });
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'title','columnId','summary','serialNumber','keywords','type','linkUrl'))
        //时间格式化
        this.message=record.content;

        this.form.setFieldsValue({author:record.author?record.author:store.getters.userInfo.realname});
        this.form.setFieldsValue({isPublish:record.isPublish?record.isPublish:"1"});
      });
        if(this.model.id){
          setTimeout(() => {
            if(record.type){
              this.type=record.type
            }
            if(record.imageHref){
              this.imgList = record.imageHref
            }
            if(record.fileUrl){
              this.fileList = record.fileUrl
            }
          }, 5)
        }
        this.loading=false
      },
      close () {
        this.$emit('close');
        this.type="article";
        this.imgList=[];
        this.fileList=[];
        this.visible = false;
      },
      handleOk () {
        const that = this;
        if(this.type=='file'&&(!this.fileList||this.fileList.length == 0)){
          that.$message.warning("上传文件不能为空");
          return;
        }
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
            formData.content=this.message
            if(this.imgList && this.imgList != ''){
              formData.imageHref=this.imgList;
            }
            if(this.fileList && this.fileList != ''){
              formData.fileUrl=this.fileList;
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
      typeChange(value){
        console.log("typeChange",value);
        this.type=value;
      },
      handleChangePigsty(flag,value) {
        this.columnId = value
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

 /deep/ .summaryTextarea{
   width: 93%;
  }

</style>