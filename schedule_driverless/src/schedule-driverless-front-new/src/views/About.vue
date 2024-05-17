<template>
  <div class="about">
    <h1>请从验证渠道进入</h1>
    <p>{{path}}</p>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        path:''
      }
    },
    created(){
      this.getList()
    },
    methods:{
      getList(){
        let path = JSON.parse(sessionStorage.getItem('href'));
        this.path = path;
      }
    }
  }
</script>

<style lang="less" scoped>
  .about {
    h1 {
      text-align: center;
    }
  }
</style>