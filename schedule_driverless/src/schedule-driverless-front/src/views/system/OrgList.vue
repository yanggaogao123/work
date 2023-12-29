<template xmlns:background-color="http://www.w3.org/1999/xhtml">
  <a-row :gutter="10">
    <a-col :md="12" :sm="24" style="overflow-y: hidden;height: 50rem">
      <a-card :bordered="false">

        <!-- 按钮操作区域 -->
        <a-row style="margin-left: 14px">
          <a-button @click="handleAdd(2)" type="primary" :disabled="currSelected.isShare">添加子组织</a-button>
          <a-button @click="handleAdd(1)" type="primary" v-show="showAddFirst">添加一级组织</a-button>
          <a-button type="primary" icon="download" @click="handleExportXls('组织信息')">导出</a-button>
          <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
            <a-button type="primary" icon="import">导入</a-button>
          </a-upload>
          <a-button @click="refresh()" type="primary">刷新</a-button>
          <a-button v-has="'clearAllCache'" @click="refreshCache()" type="primary">刷新缓存</a-button>
<!--          <a-button v-show="showSync2hyjg" @click="sync2hyjg()" type="primary">同步共用机构</a-button>-->

<!--          <a-button title="删除多条数据" @click="batchDel" type="default">批量删除</a-button>-->
          <!--<a-button @click="refresh" type="default" icon="reload" :loading="loading">刷新</a-button>-->
        </a-row>
        <div style="background: #fff;padding-left:16px;height: 100%; margin-top: 5px">
          <a-alert type="info" :showIcon="true">
            <div slot="message">
              当前选择：
              <a v-if="this.currSelected.title">{{ getCurrSelectedTitle() }}</a>
              <a v-if="this.currSelected.title" style="margin-left: 10px" @click="onClearSelected">取消选择</a>
            </div>
          </a-alert>
          <a-input-search @search="onSearch" style="width:100%;margin-top: 10px" placeholder="请输入组织名称"/>
          <!-- 树-->
          <a-col :md="12" :sm="24" style="overflow-y: auto;height: 36rem;width: 100%">
            <template>
              <a-dropdown :trigger="[this.dropTrigger]" @visibleChange="dropStatus">
               <span style="user-select: none">
            <a-tree
              checkable
              multiple
              @select="onSelect"
              @check="onCheck"
              @rightClick="rightHandle"
              :selectedKeys="selectedKeys"
              :checkedKeys="checkedKeys"
              :treeData="orgTree"
              :checkStrictly="checkStrictly"
              :expandedKeys="iExpandedKeys"
              :autoExpandParent="autoExpandParent"
              @expand="onExpand"/>
                </span>
                <!--新增右键点击事件,和增加添加和删除功能-->
                <a-menu slot="overlay">
                  <a-menu-item v-show="showAddFlag" @click="handleAdd(3)" key="1">添加</a-menu-item>
                  <a-menu-item v-show="showDeleteFlag" @click="handleDelete" key="2">删除</a-menu-item>
                  <a-menu-item @click="closeDrop" key="3">取消</a-menu-item>
                </a-menu>
              </a-dropdown>
            </template>
          </a-col>
        </div>
      </a-card>
      <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
      <div class="drawer-bootom-button">
        <a-dropdown :trigger="['click']" placement="topCenter">
          <a-menu slot="overlay">
            <a-menu-item key="1" @click="switchCheckStrictly(1)">父子关联</a-menu-item>
            <a-menu-item key="2" @click="switchCheckStrictly(2)">取消关联</a-menu-item>
            <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
            <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
            <a-menu-item key="5" @click="expandAll">展开所有</a-menu-item>
            <a-menu-item key="6" @click="closeAll">合并所有</a-menu-item>
          </a-menu>
          <a-button>
            树操作 <a-icon type="up" />
          </a-button>
        </a-dropdown>
      </div>
      <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
    </a-col>
    <a-col :md="12" :sm="24" style="overflow-y: auto;height: 50rem">
      <a-card :bordered="false" style="height: 100%">
        <a-form :form="form">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="机构名称">
            <a-input placeholder="请输入机构/组织名称" v-decorator="['orgName', validatorRules.orgName ]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="机构简称">
            <a-input placeholder="请输入机构简称" v-decorator="['orgNameAbbr', validatorRules.orgName ]"/>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级机构">
            <a-tree-select
              style="width:100%"
              :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
              :treeData="treeData"
              :disabled="disable"
              v-decorator="['parentId', validatorRules.parentId ]"
              placeholder="无">
            </a-tree-select>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="法人机构">
            <a-tree-select
              style="width:100%"
              :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
              :treeData="treeData"
              :disabled="disable"
              v-decorator="['legalOrganId']"
              placeholder="无">
            </a-tree-select>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="机构编码">
            <a-input
              :disabled="disable" placeholder="请输入机构编码" v-decorator="['orgCode', validatorRules.orgCode ]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="业务编码">
            <a-input  placeholder="业务编码" v-decorator="['serviceCode', validatorRules.serviceCode ]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="机构类型">
            <j-dict-select-tag  v-decorator="['orgCategory']" type="radio" :triggerChange="true" placeholder="请选择机构类型"
                  dictCode="org_category"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="是否共用">
            <a-radio-group v-decorator="[ 'isShare']" :disabled="shareOrgDisableFlag">
              <a-radio :value="true">是</a-radio>
              <a-radio :value="false">否</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="排序">
            <a-input-number v-decorator="[ 'orgOrder',{'initialValue':0}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="手机号">
            <a-input placeholder="请输入手机号" v-decorator="['mobile', {'initialValue':''}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="地址">
            <a-input placeholder="请输入地址" v-decorator="['address', {'initialValue':''}]"/>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="备注">
            <a-textarea placeholder="请输入备注" v-decorator="['memo', {'initialValue':''}]"/>
          </a-form-item>
        </a-form>
        <div class="anty-form-btn">
          <a-button @click="emptyCurrForm" type="default" htmlType="button" icon="sync">重置</a-button>
          <a-button @click="submitCurrForm" type="primary" htmlType="button" icon="form">修改并保存</a-button>
        </div>
      </a-card>
    </a-col>
    <org-modal ref="orgModal" @ok="loadTree"></org-modal>
    <share-org-modal ref="shareOrgModal" @ok="loadTree"></share-org-modal>
  </a-row>
</template>
<script>
  import OrgModal from './modules/OrgModal'
  import pick from 'lodash.pick'
  import { queryOrgTreeList, searchByKeywords, deleteByOrgId, duplicateCheck } from '@/api/api'
  import {httpAction, deleteAction} from '@/api/manage'
  import {JeecgListMixin} from '@/mixins/JeecgListMixin'
  import JDictSelectTag from '@/components/dict/JDictSelectTag'
  import ShareOrgModal from "./modules/ShareOrgModal";
  import {getAction} from "../../api/manage";

  // 表头
  const columns = [
    {
      title: '机构名称',
      dataIndex: 'orgName'
    },
    {
      title: '机构类型',
      align: 'center',
      dataIndex: 'orgType'
    },
    {
      title: '机构编码',
      dataIndex: 'orgCode',
    },
    {
      title: '手机号',
      dataIndex: 'mobile'
    },
    {
      title: '传真',
      dataIndex: 'fax'
    },
    {
      title: '地址',
      dataIndex: 'address'
    },
    {
      title: '排序',
      align: 'center',
      dataIndex: 'orgOrder'
    },
    {
      title: '操作',
      align: 'center',
      dataIndex: 'action',
      scopedSlots: {customRender: 'action'}
    }
  ]
  export default {
    name: 'OrgList',
    mixins: [JeecgListMixin],
    components: {
      ShareOrgModal,
      OrgModal,
      JDictSelectTag
    },
    data() {
      return {
        showAddFirst: true,
        showShareFlag: false,
        showAddFlag: false,
        showDeleteFlag: false,
        shareOrgDisableFlag: true,//默认不允许更新共用属性
        showSync2hyjg: false,//默认不展示同步行业监管按钮
        replaceFields:{children: 'children', title: 'title', key: 'key', value: 'id' },
        selectOrgId: '',
        iExpandedKeys: [],
        loading: false,
        autoExpandParent: true,
        currFlowId: '',
        currFlowName: '',
        disable: true,
        treeData: [],
        visible: false,
        orgTree: [],
        rightClickSelectedKey: '',
        hiding: true,
        model: {},
        dropTrigger: '',
        org: {},
        columns: columns,
        disableSubmit: false,
        checkedKeys: [],
        selectedKeys: [],
        autoIncr: 1,
        currSelected: {},

        allTreeKeys:[],
        checkStrictly: true,

        form: this.$form.createForm(this),
        labelCol: {
          xs: {span: 24},
          sm: {span: 5}
        },
        wrapperCol: {
          xs: {span: 24},
          sm: {span: 16}
        },
        graphDatasource: {
          nodes: [],
          edges: []
        },
        validatorRules: {
          orgName: {rules: [{required: true, message: '请输入机构名称!'},{max: 50, message: '机构名称最多只能输入50个字符!'}]},
          orgNameAbbr: {rules: [{required: true, message: '请输入机构简称!'},{max: 20, message: '机构简称最多只能输入20个字符!'}]},
          orgCode: {rules: [{required: true, message: '请输入机构编码!'},{max: 30, message: '机构编码最多只能输入30个字符!'}]},
          serviceCode: {rules: [{required: true, message: '请输入业务编码!'},{max: 30, message: '机构编码最多只能输入30个字符!'},{validator: this.validateServiceCode}]},
          orgCategory: {rules: [{required: true, message: '请输入机构类型!'}]},
          parentId: {rules: [{validator: this.validateParentId,trigger: 'change'}]},
          legalOrganId: {rules: [{validator: this.validateParentId,trigger: 'change'}]},
          mobile: {rules: [{validator: this.validateMobile}]},
        },
        url: {
          delete: '/sys/sysOrg/delete',
          edit: '/sys/sysOrg/edit',
          deleteBatch: '/sys/sysOrg/deleteBatch',
          exportXlsUrl: "sys/sysOrg/exportXls",
          importExcelUrl: "sys/sysOrg/importExcel",
          syncOrg2Hyjg: "/sys/orgEx/syncOrg2Hyjg",
          refreshAllCache: "/common/sys/clearAllCache",
        },
        orgCategoryDisabled:false,
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      loadData() {
        this.refresh();
      },
      changeParentId(val){
        console.log('parentId变化', val);
        this.form.setFieldsValue('parentId',val)
        // this.$nextTick(() => {
        //   // this.$refs.form.validateField('parentId');
        //
        // })
        // this.form.getFieldDecorator('parentId',this.validatorRules.parentId)
      },
      loadTree() {
        var that = this
        that.treeData = []
        that.orgTree = []
        queryOrgTreeList().then((res) => {
          if (res.success) {
            for (let i = 0; i < res.result.length; i++) {
              let temp = res.result[i]
              that.treeData.push(temp)
              that.orgTree.push(temp)
              // 设置默认不展开，当机构树多的时候页面很长
              // that.setThisExpandedKeys(temp)
              that.getAllKeys(temp);
              // console.log(temp.id)
            }
            if(res.result && res.result.length > 0){
              this.showAddFirst = false
            }
            this.loading = false
          }
        })
      },
      setThisExpandedKeys(node) {
        if (node.children && node.children.length > 0) {
          this.iExpandedKeys.push(node.key)
          for (let a = 0; a < node.children.length; a++) {
            this.setThisExpandedKeys(node.children[a])
          }
        }
      },
      refresh() {
        this.loading = true
        this.loadTree()
      },
      refreshCache() {
        getAction(this.url.refreshAllCache,{}).then(res => {
          if(res.success){
            this.$message.success("刷新缓存成功")
          }else {
            this.$message.success("刷新缓存失败")
          }
        })
      },
      // 右键操作方法
      rightHandle(node) {
        this.dropTrigger = 'contextmenu'
        console.log(node.node.eventKey)
        this.rightClickSelectedKey = node.node.eventKey
        console.log('node',node)
        this.rightClickSelectedKey = node.node.eventKey

        this.showAddFlag = true //默认不显示添加按钮
        this.showDeleteFlag = false //默认不显示删除按钮

        if(node.node.isLeaf) {
          if (node.node.dataRef.isShare) {
            this.showDeleteFlag = false //共用节点不允许删除
            this.showAddFlag = false  //共用节点不允许添加
          } else {
            this.showDeleteFlag = true
            this.showAddFlag = true
          }
        }
      },
      onExpand(expandedKeys) {
        console.log('onExpand', expandedKeys)
        // if not set autoExpandParent to false, if children expanded, parent can not collapse.
        // or, you can remove all expanded children keys.
        this.iExpandedKeys = expandedKeys
        this.autoExpandParent = false
      },
      backFlowList() {
        this.$router.back(-1)
      },
      // 右键点击下拉框改变事件
      dropStatus(visible) {
        if (visible == false) {
          this.dropTrigger = ''
        }
      },
      // 右键店家下拉关闭下拉框
      closeDrop() {
        this.dropTrigger = ''
      },
      addRootNode() {
        this.$refs.nodeModal.add(this.currFlowId, '')
      },
      batchDel: function () {
        console.log(this.checkedKeys)
        if (this.checkedKeys.length <= 0) {
          this.$message.warning('请选择一条记录！')
        } else {
          var ids = ''
          for (var a = 0; a < this.checkedKeys.length; a++) {
            ids += this.checkedKeys[a] + ','
          }
          var that = this
          this.$confirm({
            title: '确认删除',
            content: '确定要删除所选中的 ' + this.checkedKeys.length + ' 条数据，以及子节点数据吗?',
            onOk: function () {
              deleteAction(that.url.deleteBatch, {ids: ids}).then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.loadTree()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.message)
                }
              })
            }
          })
        }
      },
      onSearch(value) {
        let that = this
        if (value) {
          searchByKeywords({keyWord: value}).then((res) => {
            if (res.success) {
              that.orgTree = []
              for (let i = 0; i < res.result.length; i++) {
                let temp = res.result[i]
                that.orgTree.push(temp)
              }
            } else {
              that.$message.warning(res.message)
            }
          })
        } else {
          that.loadTree()
        }

      },
      nodeModalOk() {
        this.loadTree()
      },
      nodeModalClose() {
      },
      hide() {
        console.log(111)
        this.visible = false
      },
      onCheck(checkedKeys, info) {
        console.log('onCheck', checkedKeys, info)
        this.hiding = false
        //this.checkedKeys = checkedKeys.checked
        // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
        if(this.checkStrictly){
          this.checkedKeys = checkedKeys.checked;
        }else{
          this.checkedKeys = checkedKeys
        }
        // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
      },
      onSelect(selectedKeys, e) {
        console.log('selected', selectedKeys, e)
        this.hiding = false
        let record = e.node.dataRef
        console.log('onSelect-record', record)
        this.currSelected = Object.assign({}, record)
        this.model = this.currSelected
        console.log('this.mode',this.model)
        this.selectedKeys = [record.key]
        this.model.parentId = record.parentId
        this.model.isLeaf = record.isLeaf
        this.setValuesToForm(record)
        this.selectOrgId = record.id
        if(this.model.isLeaf === false){
          //非叶子节点不允许修改共用属性
          this.shareOrgDisableFlag = true
        }else {
          this.shareOrgDisableFlag = false
        }
        if(process.env.VUE_APP_SYSTEM === 'hyjg'){
          this.showShareFlag = true,
            //行业监管不允许修改共用节点配置
            this.shareOrgDisableFlag = true
        }


        // this.form.setFieldsValue('parentId',record.parentId)


      },
      // 触发onSelect事件时,为组织树右侧的form表单赋值
      setValuesToForm(record) {
        if(record.orgCategory == '1'){
          this.orgCategoryDisabled = true;
        }else{
          this.orgCategoryDisabled = false;
        }
        // this.form.getFieldDecorator('fax', {initialValue: ''})
        this.form.setFieldsValue(pick(record,'legalOrganId','isShare', 'orgNameAbbr','orgName','orgCategory', 'orgCode', 'serviceCode', 'orgOrder', 'mobile', 'fax', 'address', 'memo','parentId'))
      },
      getCurrSelectedTitle() {
        return !this.currSelected.title ? '' : this.currSelected.title
      },
      onClearSelected() {
        this.hiding = true
        this.checkedKeys = []
        this.currSelected = {}
        this.form.resetFields()
        this.selectedKeys = []
      },
      handleNodeTypeChange(val) {
        this.currSelected.nodeType = val
      },
      notifyTriggerTypeChange(value) {
        this.currSelected.notifyTriggerType = value
      },
      receiptTriggerTypeChange(value) {
        this.currSelected.receiptTriggerType = value
      },
      submitCurrForm() {
        console.log('提交',this.form)
        this.form.validateFields((err, values) => {
          console.log('err',err)
          console.log('values',values)
          if (!err) {
            if (!this.currSelected.id) {
              this.$message.warning('请点击选择要修改组织!')
              return
            }
            console.log('currSelected',this.currSelected)
            let formData = Object.assign(this.currSelected, values)
            console.log('Received values of form: ', formData)
            httpAction(this.url.edit, formData, 'put').then((res) => {
              if (res.success) {
                this.$message.success('保存成功!')
                this.loadTree()
              } else {
                this.$message.error(res.message)
              }
            })
          }
        })
      },
      emptyCurrForm() {
        this.form.resetFields()
      },
      nodeSettingFormSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
          }
        })
      },
      openSelect() {
        this.$refs.sysDirectiveModal.show()
      },
      handleRefresh(){
        load()
      },
      handleAdd(num) {
        if (num == 1) {
          this.$refs.orgModal.add()
          this.$refs.orgModal.title = '新增'
        } else if (num == 2) {
          let key = this.currSelected.key
          if (!key) {
            this.$message.warning('请先选中一条记录!')
            return false
          }
          this.$refs.orgModal.add(this.selectedKeys)
          this.$refs.orgModal.title = '新增'
        } else {
          this.$refs.orgModal.add(this.rightClickSelectedKey)
          this.$refs.orgModal.title = '新增'
        }
      },
      handleDelete() {
        deleteByOrgId({id: this.rightClickSelectedKey}).then((resp) => {
          if (resp.success) {
            this.$message.success('删除成功!')
            this.loadTree()
          } else {
            this.$message.warning(resp.message)
          }
        })
      },
      selectDirectiveOk(record) {
        console.log('选中指令数据', record)
        this.nodeSettingForm.setFieldsValue({directiveCode: record.directiveCode})
        this.currSelected.sysCode = record.sysCode
      },
      getFlowGraphData(node) {
        this.graphDatasource.nodes.push({
          id: node.id,
          text: node.flowNodeName
        })
        if (node.children.length > 0) {
          for (let a = 0; a < node.children.length; a++) {
            let temp = node.children[a]
            this.graphDatasource.edges.push({
              source: node.id,
              target: temp.id
            })
            this.getFlowGraphData(temp)
          }
        }
      },
     // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
      expandAll () {
        this.iExpandedKeys = this.allTreeKeys
      },
      closeAll () {
        this.iExpandedKeys = []
      },
      checkALL () {
        this.checkStrictly = false
        this.checkedKeys = this.allTreeKeys
      },
      cancelCheckALL () {
        //this.checkedKeys = this.defaultCheckedKeys
        this.checkedKeys = []
      },
      switchCheckStrictly (v) {
        if(v==1){
          this.checkStrictly = false
        }else if(v==2){
          this.checkStrictly = true
        }
      },
      getAllKeys(node) {
        // console.log('node',node);
        this.allTreeKeys.push(node.key)
        if (node.children && node.children.length > 0) {
          for (let a = 0; a < node.children.length; a++) {
            this.getAllKeys(node.children[a])
          }
        }
      },
      showShareOrgs(){
          this.$refs.shareOrgModal.init(this.rightClickSelectedKey)
      },
      sync2hyjg(){
          getAction(this.url.syncOrg2Hyjg,{}).then(res => {
            if(res.success){
              this.$message.success("同步成功")
            }else {
              this.$message.warning("同步失败，失败信息："+res.message)
            }
          })
      },
      validateServiceCode(rule, value, callback){
        if(!value){ //空值跳过
          callback()
          return
        }
        var params = {
          tableName: 'bs_organ',
          fieldName: 'service_code',
          fieldVal: value,
          dataId: this.selectOrgId,
          dataField: 'organ_id',
        };
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback("业务编码已存在!")
            return
          }
        })
      },
      validateParentId(rule, value, callback){
        if(!value){ //空值跳过
          callback()
          return
        }
        if(value == this.selectOrgId){
          callback("当前机构不能作为父级机构!")
        }else {
          callback()
        }
      },
      validateMobile(rule,value,callback){
        if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)){
          callback();
        }else{
          callback("您的手机号码格式不正确!");
          return
        }

      },
      // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
    },
    created() {
      this.currFlowId = this.$route.params.id
      this.currFlowName = this.$route.params.name
      // this.loadTree()
      let userType = this.$store.getters.userInfo.type
      if(userType === 9999){//超管角色
        this.disable = false
        if(process.env.VUE_APP_SYSTEM != 'hyjg'){
          this.showSync2hyjg = true
        }
      }
      console.log('SYSTEM',process.env.VUE_APP_SYSTEM)
      if(process.env.VUE_APP_SYSTEM === 'hyjg'){
        this.showShareFlag = true,
          //行业监管不允许修改共用节点配置
          this.shareOrgDisableFlag = true
      }
    },

  }
</script>
<style scoped>
  .ant-card-body .table-operator {
    margin: 15px;
  }

  .anty-form-btn {
    width: 100%;
    text-align: center;
  }

  .anty-form-btn button {
    margin: 0 5px;
  }

  .anty-node-layout .ant-layout-header {
    padding-right: 0
  }

  .header {
    padding: 0 8px;
  }

  .header button {
    margin: 0 3px
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }

  #app .desktop {
    height: auto !important;
  }

  /** Button按钮间距 */
  .ant-btn {
    margin-left: 3px
  }

  .drawer-bootom-button {
    /*position: absolute;*/
    bottom: 0;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: left;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>