<template>

  <a-tree-select :showSearch="showSearch" :allowClear="allowClear" :multiple="multiple" :placeholder="placeholder" :treeDefaultExpandAll="treeDefaultExpandAll"
   :disabled="disabled" :treeNodeFilterProp="treeNodeFilterProp" v-model="arrayValue" @change="handleInput" :treeData="orgTree"
                 :getPopupContainer="triggerNode => {return triggerNode.parentNode || document.body;}"
                 :dropdown-style="{ maxHeight: '500px', overflow: 'auto' }">
  </a-tree-select>

</template>

<script>
  import {
    queryOrgTreeByUserId
  } from '@/api/api'
  import {queryOrgTreeForRole, queryTreeOrganByOrgType} from "../../../api/api";

  export default {
    name: "GOrgSelect",
    props: {
      showSearch:Boolean,
      placeholder: String,
      disabled: Boolean,
      value: String,
      allowClear:Boolean,
      isSetDefaultValue:Boolean,
      multiple:Boolean,
      treeDefaultExpandAll:Boolean,
      treeNodeFilterProp:String,
      type:String,
      orgCategorys:{type:Array,default: () => []},
      orgCode:String,

    },
    data() {
      return {
        orgTree: [],
        arrayValue:[]
      }
    },
    watch: {
      value(val){
        if(!val){
          this.arrayValue = []
        }else{
          // if(Object.prototype.toString.call(valu)==='[object Array]'){}
          this.arrayValue = this.value.split(',')
        }
      }
    },
    created() {
      //获取数据
      this.queryOrgTree();
      if(this.isSetDefaultValue){
        this.arrayValue[0]=this.$store.getters.userInfo.orgCode
      }
    },
    methods: {
      //机构树key,value采用orgcode表示，默认为id
      orgTreeNodeFromIdToCode(node) {
        node.forEach(item => {
          item.key = item.orgCode
          item.value = item.orgCode
          if (item.children) {
            this.orgTreeNodeFromIdToCode(item.children)
          }
          return item
        })
      },
      queryOrgTree() {
        console.log('type',this.type)
        if(this.type == 'role'){
          queryOrgTreeForRole({
            userId: this.$store.getters.userInfo.id
          }).then((res) => {
            if (res.success) {
              //由于这里需要orgcode，因此把节点的Id
              this.orgTreeNodeFromIdToCode(res.result)
              this.orgTree = res.result;
            }
          })
          return
        }
        if(this.type == 'orgCategorys'){
          queryTreeOrganByOrgType({
            orgCategorys: this.orgCategorys,
            orgCode: this.$store.getters.userInfo.orgCode
          }).then((res) => {
            if (res.success) {
              //由于这里需要orgcode，因此把节点的Id
              this.orgTreeNodeFromIdToCode(res.result)
              this.orgTree = res.result;
            }
          })
          return
        }
        queryOrgTreeByUserId({
          userId: this.$store.getters.userInfo.id
        }).then((res) => {
          if (res.success) {
            //由于这里需要orgcode，因此把节点的Id
            this.orgTreeNodeFromIdToCode(res.result)
            this.orgTree = res.result;
            // console.log(this.orgTree)
          }
        })
      },
      handleInput(e) {
        if(Object.prototype.toString.call(e)==='[object Array]'){
          this.$emit('change', e.join(','));
        }else{
          this.$emit('change', e);
        }
      },
      setCurrentTreeData(orgTree) {
        this.orgTree = orgTree
      },
      getCurrentTreeData() {
        return this.orgTree
      }
    }
  }
</script>

<style scoped>
</style>