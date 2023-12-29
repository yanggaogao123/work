<template>
  <!-- 定义在这里的参数都是不可在外部覆盖的，防止出现问题 -->
  <j-select-biz-component
    :value="value"
    :ellipsisLength="25"
    :listUrl="url.list"
    :columns="columns"
    v-on="$listeners"
    v-bind="attrs"
  />
</template>

<script>
import JSelectBizComponent from './JSelectBizComponent'

export default {
  name: 'JSelectMultiUser',
  components: { JSelectBizComponent },
  props: ['value'],
  data() {
    return {
      url: { list: '/common/sys/queryPageListRoute' },
      columns: [
        { title: '线路', align: 'center', width: '50%', widthRight: '70%', dataIndex: 'routeName' },
        { title: '线路ID', align: 'center', width: '50%', widthRight: '70%', dataIndex: 'routeId' }
      ],
      // 定义在这里的参数都是可以在外部传递覆盖的，可以更灵活的定制化使用的组件
      default: {
        name: '线路',
        width: 1200,
        displayKey: 'routeName',
        returnKeys: ['id', 'routeId'],
        queryParamText: '线路'
      },
      id: ''
    }
  },
  created() {
    console.log(this.value)
    // this.id = this.value
  },
  computed: {
    attrs() {
      return Object.assign(this.default, this.$attrs)
    }
  }
}
</script>

<style lang="less" scoped></style>
