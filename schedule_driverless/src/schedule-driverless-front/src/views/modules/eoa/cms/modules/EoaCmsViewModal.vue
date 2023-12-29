<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    @cancel="handleCancel">
    <template slot="footer">
      <a-button key="back" @click="handleCancel">关闭</a-button>
    </template>
      <a-form :form="form">
        <div class="body">
          <div style="text-align: left">
            <h3>主题：{{tittle}}</h3>
          </div>
          <div style="text-align: left">
            <div>
              <span>作者：{{author}}</span>
            </div>
          </div>
          <div style="margin-top: 20px;border-bottom: 1px solid #e0e0e0" class="author">
            <div>
              <span>{{publishDate}}</span>
            </div>
          </div>
          <div style="margin-top: 20px;">
            <div>
              <span>{{summary}}</span>
            </div>
            <div v-html="message" style="margin-top: 20px;"></div>
          </div>
          <div>
            <j-upload  :text="text" v-model="fileList"></j-upload>
          </div>
        </div>
      </a-form>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from "moment"
  import Vue from 'vue'
  import { getAction } from '@/api/manage'
  export default {
    name: "EoaCmsViewModal",
    data () {
      return {
        title:"详情",
        text:"上传",
        visible: false,
        previewVisible: false,
        model: {},
        treeData:[],
        imageHref:"",
        fileList:[],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        tittle:"",
        author:"",
        summary:"",
        publishDate:"",
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
          sm: { span: 18 },
        },
        message:"",
        form: this.$form.createForm(this),
        list: "/cms/eoaCmsArticle/list"
      }
    },
    created () {
    },
    methods: {
      edit (record) {
        this.form.resetFields();
        this.visible = true;

        var that=this
        getAction(this.list,{id:record}).then((res) =>{
          if(res.success){
            var result=res.result.records[0]
          that.tittle=result.title
          that.author=result.author
          that.summary=result.summary
          that.publishDate=moment(result.publishDate).format("YYYY年MM月DD日 hh:mm:ss")
          that.message=result.content;
          }
        })
      },
      close () {
        this.$emit('close');
        this.tittle="";
        this.author="";
        this.summary="";
        this.publishDate="";
        this.message="";
        this.visible = false;
      },
      handleOk () {
        this.close()
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>

<style>
  .rich-img{
    /*display: table-cell;*/
    /*margin:0 auto;*/
    overflow: hidden;
    max-width: 1000px;
  }
  .rich-p{
    color: #333333;
    font-size: 16px;
    line-height: 45px;
  }
  .title{
    font-size: 24px;
    word-wrap: break-word
  }
  .body{
    position: relative;
    padding: 0 24px 16px;
  }
  div{
    padding: 0;
    margin: 0;
  }
  .author{
    height: 55px;
    color: #858585;
    overflow: hidden;
    text-align: right;
  }
  .summary{
    color: rgba(0,0,0,.75);
    font-variant-ligatures: common-ligatures;
    line-height: 1.625;
  }
  p{
    margin-bottom: 2em;
  }
</style>