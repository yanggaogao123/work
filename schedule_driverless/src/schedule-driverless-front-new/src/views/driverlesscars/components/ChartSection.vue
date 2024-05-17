<template>
  <div class="bs-chart-section">
    <header class="bs-chart-section__header">{{ title }}</header>
    <section style="height: calc(100% - 27px); width: 100%;">
      <slot></slot>
    </section>
  </div>
</template>

<script>
export default {
  props: ["title"],
};
</script>

<style scoped>
.bs-chart-section {
  height: 230px;
  width: 928px;
  position: relative;
}

.bs-chart-section__header {
  height: 27px;
  width: 100%;
  background: url("../modules/chart_header.png");
  background-size: 100% 100%;
  background-repeat: no-repeat;
  color: #fff;
  font-weight: bold;
  line-height: 26px;
  text-indent: 28px;
}
</style>
