<template>
  <div>
    <a-input-search
      placeholder="点击选择车辆"
      :style="style"
      v-model="numberPlate"
      readOnly
      @search="onSearchBus"
    >
      <a-button slot="enterButton" icon="search" >选择</a-button>
    </a-input-search>
    <GCIBusSelectWin :routeId="routeId" :organId="organId" ref="gciBusSelectWin" @ok="busSelectOk"/>
  </div>
</template>
<script>
  import GCIBusSelectWin from '@/components/gci/GCIHisBusSelectWin'
  export default {
    name: 'GCIHisBusSelect',
    props: {
      routeId: [String, Number],
      organId: [String, Number],
      value: [String, Number],
      // item:指定返回车辆字段，默认obuId
      item: {
        type: String,
        default: 'obuId'
      },
      width: {
        type: [String, Number],
        default: '200'
      }
    },
    computed: {
      // 计算属性的 getter
      style: function () {
        return 'width:' + this.width + 'px'
      }
    },
    components: { GCIBusSelectWin },
    data() {
      return {
        busItem: this.item,
        items: {},
        numberPlate: ''
      }
    },
    watch: {
      value: {
        deep: true, // 解决清空问题
        handler(newVal, oldVal) {
          console.log('watch' + newVal)
          if (this.value === undefined) { this.numberPlate = undefined }
        }
      }
    },
    methods: {
      onSearchBus() {
        this.$refs.gciBusSelectWin.open()
      },
      busSelectOk(item) {
        this.items = item;
        this.numberPlate = item.numberPlate
        this.$emit('input', item['' + this.busItem + ''])
        this.$emit('change', item['' + this.busItem + ''])
      }

    }
  }
</script>
<style lang="scss" scoped>

</style>
