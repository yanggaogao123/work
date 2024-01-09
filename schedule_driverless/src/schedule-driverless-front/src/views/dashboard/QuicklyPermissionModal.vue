<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    switchFullscreen
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item label="序号" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input-number  v-decorator="['sortNo']" placeholder="排序">
          </a-input-number>
        </a-form-item>
        <a-form-item label="菜单" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-select show-search v-decorator="['permissionId']" placeholder="请选择" optionFilterProp="label">
            <a-select-option v-for="(item, index) in menuData" :key="item.id" :label="item.name">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validateDuplicateValue } from '@/utils/util'
  import store from '@/store'




  export default {
    name: "QuicklyPermissionModal",
    components: {
    },
    props: {
      menus:{
        type: Array,
        required: true
      }
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:550,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 10 },
        },
        confirmLoading: false,
        validatorRules: {
        },
        url: {
          add: "/sys/sysQuicklyPermission/add",
          edit: "/sys/sysQuicklyPermission/edit",
        },
        menuData: []
      }
    },
    created () {
      this.setMenuValue();
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'userId','permissionId','tenantId','platformCode','sortNo'))
        })
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
            console.log("表单提交数据",formData)
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
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'userId','permissionId','tenantId','platformCode'))
      },
      setMenuValue(){
        store.getters.permissionList.forEach((value, i) => {
          this.addTree(value, this.menuData)
        });
      },
      addTree(tree, list){
        let tmp = {
          id: tree.id,
          name: tree.meta.title
        }
        list.push(tmp);
        if (tree.children){
          tree.children.forEach((value, i) => {
            this.addTree(value, list)
          })
        }
      }


    }
  }
</script>