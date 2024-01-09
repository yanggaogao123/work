<template>
  <div style="display: block">
    <iframe
      :id="id"
      :src="url"
      frameborder="0"
      width="100%"
      height="850px"
      scrolling="auto">
    </iframe>
  </div>
</template>

<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  export default {
    name: 'EoaFileView',
    data () {
      return {
        url:  window._CONFIG['domianURL'],
        id:"eoaFileIframe",
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.url = this.url + "/filemanage/view?token="+token
    },
  }
</script>

<style scoped>

</style>