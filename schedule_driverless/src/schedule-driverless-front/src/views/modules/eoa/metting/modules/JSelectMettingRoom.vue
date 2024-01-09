<template>
  <div>
    <a-tooltip>
      <template slot="title">
        {{names?names:'暂无地点'}}
      </template>
      <div>
        <a-input-search
          v-model="names"
          placeholder="请选择会议地点"
          readOnly
          @search="onSearchRoom">
          <a-button slot="enterButton" :disabled="disabled">选择地点</a-button>
        </a-input-search>
      </div>
    </a-tooltip>
    <j-select-metting-room-modal ref="selectModal" :modal-width="modalWidth" :multi="multi" @ok="selectOK" :room-ids="value" @initComp="initComp"></j-select-metting-room-modal>
  </div>
</template>

<script>

  import JSelectMettingRoomModal from './JSelectMettingRoomModal'

  export default {
    name: 'JSelectMettingRoom',
    components: { JSelectMettingRoomModal},
    props: {
      modalWidth: {
        type: Number,
        default: 1250,
        required: false
      },
      value: {
        type: String,
        required: false
      },
      disabled: {
        type: Boolean,
        required: false,
        default: false
      },
      multi: {
        type: Boolean,
        default: true,
        required: false
      },
    },
    data() {
      return {
        roomIds: "",
        names: ""
      }
    },
    mounted() {
      this.roomIds = this.value
    },
    watch: {
      value(val) {
        this.roomIds = val
      }
    },
    model: {
      prop: 'value',
      event: 'change'
    },
    methods: {
      initComp(names) {
        console.log("initComp")
        this.names = names
      },
      onSearchRoom() {
        this.$refs.selectModal.showModal()
      },
      selectOK(rows, idstr) {
        let processStatus='0';
        if (!rows) {
          this.names = ''
          this.roomIds = ''
        } else {
          let temp = ''
          for (let item of rows) {
            temp += ',' + item.name
            processStatus=item.processStatus
          }
          this.names = temp.substring(1)
          this.roomIds = idstr;
        }
        this.$emit("change", this.roomIds,processStatus)
      }
    }
  }
</script>

<style scoped>

</style>