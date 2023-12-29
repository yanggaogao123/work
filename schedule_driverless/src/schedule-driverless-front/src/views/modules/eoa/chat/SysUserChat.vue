<template>
</template>

<script>
    import Vue from 'vue'
    import { ACCESS_TOKEN } from "@/store/mutation-types"
    export default {
        name: "SysUserChat",
        components: {
        },
        data () {
            return {
            }
        },
        created () {
            const token = Vue.ls.get(ACCESS_TOKEN);
            window.open(window._CONFIG['domianURL']+"/oa/im/index?token="+token)
        },
        methods: {
        }
    }
</script>

<style lang="less" scoped>
</style>
