<template>
  <a-tree-select
    showSearch
    allowClear
    :multiple="multiple"
    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
    :treeData="orgTree"
    :placeholder="placeholder"
    :getPopupContainer="triggerNode => triggerNode.parentNode"
    :treeDefaultExpandAll="treeDefaultExpandAll"
    :value="getValueSting"
    @change="onChange"
    treeNodeFilterProp="title"
  >
  </a-tree-select>
</template>
<script>
import { getAction } from '@/api/manage'
import { queryTreeOrgan } from '@/api/api'

export default {
  name: 'GCIOrgTreeSelect',
  props: {
    //mode:默认单选，多选multiple 返回","分隔的字符串
    multiple: {
      type: [Boolean, String],
      default: false
    },
    //isGetOption:如果需要接受其他机构字段信息 设置为true，changeOptions事件会返回整个机构对象
    isGetOption: {
      type: [Boolean, String],
      default: false
    },
    // 默认key为orgCode,如果需要key该为id(机构id,organ_id),可以传字符串id过来
    selectType: {
      type: String,
      default: 'orgCode'
    },
    value: {
      type: [String, Array, Number],
      required: false
    },
    placeholder: {
      type: String,
      default: '请选择所属企业',
      required: false
    },
    // 是否默认选择最高机构，'1'默认选择
    type: {
      type: String,
      required: false
    },
    // 是否默认展开
    treeDefaultExpandAll: {
      type: Boolean,
      required: false
    }
  },
  data() {
    return {
      orgTree: [],
      sysOrgCode: null,
      selectOptions: []
    }
  },
  watch: {},
  created() {
    this.queryOrgTree()
  },
  computed: {
    //数据回显
    getValueSting() {
      if (this.multiple) {
        return this.value
      } else {
        return this.value != null ? this.value.toString() : null
      }
    }
  },
  methods: {
    queryOrgTree() {
      queryTreeOrgan().then(res => {
        if (res.success) {
          console.log('res.result')
          console.log(res.result)
          //初始选择最高级机构
          if (this.type == '1') {
            this.onChange(res.result[0].orgCode)
          }
          this.orgTreeNodeFromIdToCode(res.result)
          this.orgTree = res.result
        }
      })
    },
    //机构树key,value采用orgcode表示，默认为id
    orgTreeNodeFromIdToCode(node) {
      node.forEach(item => {
        item.key = item[this.selectType]
        item.value = item[this.selectType]
        if (item.children) {
          this.orgTreeNodeFromIdToCode(item.children)
        }
        return item
      })
    },
    onChange(value, label, extra) {
      this.sysOrgCode = value
      this.$emit('change', value)
      if (this.isGetOption) {
        //返回其他数据
        if (this.multiple) {
          //多选
          if (value.length > this.selectOptions.length) {
            this.selectOptions.push(extra.triggerNode.dataRef)
          } else {
            this.selectOptions = this.selectOptions.filter(item => item.orgCode != value)
          }
          this.$emit('changeOptions', this.selectOptions)
        } else {
          if (value != undefined) {
            this.$emit('changeOptions', extra.triggerNode.dataRef)
          } else {
            this.$emit('changeOptions', undefined)
          }
        }
      }
    }
  },
  //2.2新增 在组件内定义 指定父组件调用时候的传值属性和事件类型 这个牛逼
  model: {
    prop: 'value',
    event: 'change'
  }
}
</script>
