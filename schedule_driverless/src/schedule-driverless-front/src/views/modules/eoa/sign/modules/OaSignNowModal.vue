<template>
  <a-modal
    title="打卡"
    :width="360"
    :visible="visible"
    :confirmLoading="loading"
    :footer="false"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading || loading" size="large" class="spin">
      <div v-if="!loading" class="content">

        <div v-if="showOkIcon||showWarnIcon||showErrorIcon" class="icon-box">
          <a-icon v-if="showOkIcon" type="check-circle" theme="filled"/>
          <a-icon v-if="showWarnIcon" type="exclamation-circle" theme="filled"/>
          <a-icon v-if="showErrorIcon" type="close-circle" theme="filled"/>
        </div>

        <div class="current-type">{{ forceSignText || signToDayStatus.label || signToDayStatus.text }}</div>
        <div class="current-time">{{ currentDateTime }}</div>

        <div class="btn-box">
          <a-button class="btn-sign" @click="handleSignNow">
            <div class="time">{{currentHour}}<span class="sec">:</span>{{currentMinute}}</div>
            <div class="text">{{ confirmLoading?'打卡中':'立即打卡' }}</div>
          </a-button>
        </div>
      </div>
    </a-spin>

  </a-modal>
</template>

<script>
  import moment from 'moment/moment'
  import { getEnumByValue } from '../utils/util'
  import { SignToDayStatusEnum } from '../utils/constant'
  import { getAction, postAction } from '@api/manage'

  export default {
    name: 'OaSignNowModal',
    props: {
      visible: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        SignToDayStatusEnum,
        loading: false,
        confirmLoading: false,
        // 模拟数据：打卡规则
        signRule: { offTimeEarly: '18:00' },
        currentDateTime: '',
        currentHour: '',
        currentMinute: '',
        // 我的打卡状态
        myStatus: -1,
        signToDayStatus: {},
        forceSignText: null,
        url: {
          statusAndRule: '/sign/info/statusAndRule',
          signNow: '/sign/info/signNow',
          add: '/sign/info/add',
          edit: '/sign/info/edit',
        },
      }
    },
    computed: {
      showOkIcon() {
        return this.signToDayStatus === SignToDayStatusEnum.OK
      },
      showWarnIcon() {
        return (this.signToDayStatus === SignToDayStatusEnum.USER_NO_NEED)
          || (this.signToDayStatus === SignToDayStatusEnum.TO_DAY_NO_NEED)
      },
      showErrorIcon() {
        return this.signToDayStatus === SignToDayStatusEnum.NO_RULE
      }
    },
    watch: {
      visible() {
        if (this.visible) {
          this.initialStatus()
        }
      }
    },
    created() {
      this.setCurrentStatus()
    },
    methods: {

      // 初始化打卡状态
      initialStatus() {
        this.forceSignText = null
        this.currentDateTime = moment().format('ll dddd')
        this.timer = setInterval(this.setCurrentStatus, 1000)
        this.querySignStatus()
      },

      setCurrentStatus() {
        let time = moment()
        this.currentHour = time.format('HH')
        this.currentMinute = time.format('mm')
        this.signToDayStatus = this.getMySignToDayStatus()
      },

      // 查询打卡状态
      querySignStatus() {
        this.loading = true
        getAction(this.url.statusAndRule).then(res => {
          if (res.success) {
            let { status, signRule } = res.result
            this.myStatus = status
            this.signRule = signRule
            this.setCurrentStatus()
          } else {
            throw res.message
          }
        }).catch(e => {
          this.$warning({
            title: '打卡状态查询失败',
            content: (e || {}).message || e
          })
          this.close()
        }).finally(() => {
          this.loading = false
        })
      },

      getMySignToDayStatus() {
        let current = moment()
        let currentDate = current.format('YYYY-MM-DD')
        // 今天还没有打卡
        if (this.myStatus === SignToDayStatusEnum.NO.value) {
          return SignToDayStatusEnum.ON
        }
        // 今天只打了上班卡
        if (this.myStatus === SignToDayStatusEnum.ON.value) {
          return SignToDayStatusEnum.OFF
        }
        // 今天只打了下班卡或已完成
        if (this.myStatus === SignToDayStatusEnum.OFF.value || this.myStatus === SignToDayStatusEnum.OK.value) {
          return SignToDayStatusEnum.OK
        }
        return getEnumByValue(this.myStatus, SignToDayStatusEnum, false)
      },

      handleSignNow() {
        this.confirmLoading = true
        this.checkSignType().then(() => {
          return postAction(this.url.signNow)
        }, () => {
        }).then(res => {
          if (res) {
            if (res.success) {
              this.signToDayStatus = SignToDayStatusEnum.OK
              clearInterval(this.timer)
              this.forceSignText = res.message
            } else throw res.message
          }
        }).catch(e => {
          this.$warning({
            title: '打卡失败，请稍后重试',
            content: (e || {}).message || e
          })
        }).finally(() => {
          this.confirmLoading = false
        })
      },
      // 判断打卡类型，并给出相应的提示
      checkSignType() {
        let current = moment()
        let currentDate = current.format('YYYY-MM-DD')
        return new Promise((resolve, reject) => {
          if (this.signToDayStatus === SignToDayStatusEnum.OFF) {
            // 判断是否已到打卡时间
            let offTime = moment(currentDate + ' ' + this.signRule.offTimeEarly)
            if (current < offTime) {
              this.$confirm({
                title: '下班打卡',
                content: '当前还没有到下班时间，如果这个时候打卡将会被视为早退，确定要打卡吗？',
                onOk: () => resolve(),
                onCancel: () => reject()
              })
            } else {
              resolve()
            }
          } else if (this.signToDayStatus === SignToDayStatusEnum.OK) {
            reject()
          } else {
            resolve()
          }
        })
      },

      close() {
        if (!this.confirmLoading) {
          clearInterval(this.timer)
          if (this.forceSignText != null) {
            this.$emit('ok')
          }
          this.$emit('update:visible', false)
        }
      },
      handleOk() {
        this.close()
      },
      handleCancel() {
        this.close()
      },
    },
    beforeDestroy() {
      clearInterval(this.timer)
    }
  }
</script>

<style lang="less" scoped>
  .spin {
    min-height: 320px;
    max-height: 320px;
    min-width: 100%;
    overflow: hidden;
  }

  .content {
    text-align: center;

    .current-type {
      font-size: 24px;
      color: #389E0D;
      margin-top: 12px;
    }

    .current-time {
      font-size: 18px;
      color: #aaaaaa;
      margin-top: 12px;

    }

    .icon-box {
      margin: 20px 0 60px 0;

      /deep/ .anticon {
          font-size: 120px;
          &-check-circle {
            color: #32CD32;
          }
          &-exclamation-circle {
            color: #FFA500;
          }
          &-close-circle {
            color: #EE0000;
          }
        }
    }

    .btn-box {
      margin-top: 60px;

      .btn-sign {
        width: 120px;
        height: 120px;
        color: #000000;
        border-radius: 50%;
        border: 4px solid #FF8C00;

        &:hover {
          background-color: #f6f6f6;
        }

        &:active {
          background-color: #efefef;
        }

        .time {
          font-size: 32px;
          position: relative;
          top: -8px;

          @keyframes secFlash {
            0% {
              opacity: 1;
            }
            60% {
              opacity: 0;
            }
            80% {
              opacity: 1;
            }
          }

          .sec {
            position: relative;
            top: -2px;
            margin: 0 4px;
            animation: secFlash 1s infinite;
          }
        }

        .text {
          position: relative;
          top: -4px;
        }

      }
    }

  }

</style>