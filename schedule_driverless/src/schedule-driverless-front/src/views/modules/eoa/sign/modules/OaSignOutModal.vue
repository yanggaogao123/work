<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <oa-sign-out-form ref="outForm" :modal-mode="true" :loading.sync="loading" @ok="handleFormOk"/>

  </a-modal>
</template>

<script>
  import OaSignOutForm from './OaSignOutForm'

  export default {
    name: 'OaSignOutModal',
    components: { OaSignOutForm },
    data() {
      return {
        title: '操作',
        visible: false,
        loading: false,
      }
    },
    created() {
    },
    methods: {
      open(record, action) {
        this.getOutForm(true).then(ref => ref.setValue(record, action))
      },
      getOutForm(visible) {
        this.visible = visible
        return new Promise((resolve, reject) => {
          setInterval(() => {
            let { outForm } = this.$refs
            if (outForm) {
              resolve(outForm)
            }
          }, 10)
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleOk() {
        this.getOutForm(this.visible).then(ref => ref.handleOk())
      },
      handleFormOk() {
        this.close()
        this.$emit('ok')
      },
      handleCancel() {
        this.close()
      }
    }
  }
</script>

<style lang="less" scoped>

</style>