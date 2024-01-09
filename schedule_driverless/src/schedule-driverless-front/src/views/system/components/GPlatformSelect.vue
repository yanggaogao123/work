<template>

  <a-select :allowClear="allowClear" :getPopupContainer="(target) => target.parentNode" :placeholder="placeholder"
    :disabled="disabled" :defaultValue="defaultValue" :value="value" @change="handleInput">
    <a-select-option v-for="(item, key) in dictOptions" :key="key" :value="item.value">
      <span style="display: inline-block;width: 100%" :title=" item.text || item.label ">
        {{ item.text || item.label }}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
  import {
    getPlatformList
  } from '@/api/api'

  export default {
    name: "GPlatformSelect",
    props: {
      dictCode: String,
      placeholder: String,
      triggerChange: Boolean,
      disabled: Boolean,
      value: String,
      allowClear:Boolean,
    },
    data() {
      return {
        dictOptions: [],
        tagType: "",
        defaultValue:''
      }
    },
    watch: {
      value(val){
        this.defaultValue=val
      }

    },
    created() {
      //获取字典数据
      this.initDictData();
    },
    methods: {
      initDictData() {
        let that=this
        //根据字典Code, 初始化字典数组
        getPlatformList().then((res) => {
          if (res.success) {
            console.log(res.result)
            if (res.result.records.length > 0) {
              res.result.records.forEach(element => {
                element.key = element.platformCode
                element.value = element.platformCode
                element.text = element.platformName
                that.dictOptions.push(element)
              });
            }
          }
        })
      },
      handleInput(e) {
        if (this.triggerChange) {
          this.$emit('change', e);
        } else {
          this.$emit('input', e);
        }
      },
      setCurrentDictOptions(dictOptions) {
        this.dictOptions = dictOptions
      },
      getCurrentDictOptions() {
        return this.dictOptions
      }
    }
  }
</script>

<style scoped>
</style>