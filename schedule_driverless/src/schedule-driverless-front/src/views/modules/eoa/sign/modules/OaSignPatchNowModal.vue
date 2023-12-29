<template>
  <a-modal
    title="补卡"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    @cancel="handleCancel">

    <a-spin :spinning="loading">

      <a-tabs :class="{'hide-tabs-bar':true}" v-model="activeKey">
        <a-tab-pane tab="选择补卡信息" :key="tabs.select.key" forceRender>

          <a-alert showIcon type="info">
            <div slot="message">请选择一项异常打卡信息</div>
          </a-alert>

          <a-tabs v-model="activeKeySelectType" size="small">
            <a-tab-pane tab="补异常卡" :key="1" forceRender>
              <a-table v-bind="tabs.select.table">
                <template slot="signStatus" slot-scope="text, record">
                  <a-tag v-if="text === SignStatusEnum.NO_SIGN.value" color="#D3D3D3">缺卡</a-tag>
                  <a-tag v-else-if="text === SignStatusEnum.NORMAL.value" color="green">正常</a-tag>
                  <a-tag v-else-if="text === SignStatusEnum.LATE.value" color="red">迟到</a-tag>
                  <a-tag v-else-if="text === SignStatusEnum.ABSENT.value" color="red">旷工</a-tag>
                  <a-tag v-else-if="text === SignStatusEnum.LEAVE_EARLY.value" color="red">早退</a-tag>
                  <span v-else>{{text}}</span>
                </template>
              </a-table>
            </a-tab-pane>
            <a-tab-pane tab="补缺卡" :key="2" forceRender>
              <a-row>
                <a-col :span="12">
                  <a-calendar :fullscreen="false" v-model="calendarValue" :disabledDate="handleDisabledDate"/>
                </a-col>
                <a-col :span="12">
                  <h3 style="text-align: center;line-height: 32px;">{{ calendarValue.format('ll dddd') }}</h3>
                  <a-empty v-if="false" description="当天无任何缺卡记录" style="margin-top: 60px;"/>
                  <a-list>
                    <a-list-item v-for="(item,index) of missingDataSource" :key="index" style="padding: 20px;">
                      <span style="margin-right: 10px">{{getSignType(item.signType)}}</span>
                      <a-tag v-if="item.signStatus === SignStatusEnum.NO_SIGN.value" color="#D3D3D3">缺卡</a-tag>
                      <a-tag v-else-if="item.signStatus === SignStatusEnum.NORMAL.value" color="green">正常</a-tag>
                      <a-tag v-else-if="item.signStatus === SignStatusEnum.LATE.value" color="red">迟到</a-tag>
                      <a-tag v-else-if="item.signStatus === SignStatusEnum.ABSENT.value" color="red">旷工</a-tag>
                      <a-tag v-else-if="item.signStatus === SignStatusEnum.LEAVE_EARLY.value" color="red">早退</a-tag>
                      <span v-else>{{item.signStatus}}</span>
                      <a-button v-if="item.isPatch" type="primary" size="small" disabled>已补卡</a-button>
                      <a-button v-else-if="item.signStatus === SignStatusEnum.NO_SIGN.value" type="primary" size="small" @click="handlePatchMissing(item)">立即补卡</a-button>
                      <a-button v-else-if="item.signStatus === SignStatusEnum.NORMAL.value" type="primary" size="small" disabled>无需补卡</a-button>
                      <a-button v-else type="primary" size="small" disabled>此处无法补异常卡</a-button>
                    </a-list-item>
                  </a-list>
                  <div>

                  </div>
                </a-col>
              </a-row>
            </a-tab-pane>
          </a-tabs>

        </a-tab-pane>
        <a-tab-pane tab="填写补卡原因" :key="tabs.form.key" forceRender>

          <a-form :form="tabs.form.form">

            <a-row :gutter="10">
              <a-col :md="12" :sm="24">
                <a-form-item label="补卡类型 " v-bind="tabs.form.formItemProps">
                  <a-input disabled v-model="tabs.form.model.signType"/>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item label="异常状态 " v-bind="tabs.form.formItemProps">
                  <a-input disabled v-model="tabs.form.model.abnormalStatus"/>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="10">
              <a-col :md="12" :sm="24">
                <a-form-item label="异常时间" v-bind="tabs.form.formItemProps">
                  <a-input disabled v-model="tabs.form.model.abnormalTime"/>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item label="正常时间" v-bind="tabs.form.formItemProps">
                  <a-input disabled v-model="tabs.form.model.patchTime"/>
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item
              label="补卡原因"
              :labelCol="{sm : { span: 24 },  md: { span: 3 } }"
              :wrapperCol="{ sm: { span: 24 }, md: { span: 24-3 } }"
            >
              <a-textarea placeholder="请输入补卡原因" :rows="5" v-decorator="['patchReason', tabs.form.validatorRules.patchReason]"/>
            </a-form-item>

          </a-form>

        </a-tab-pane>
      </a-tabs>

    </a-spin>

    <div slot="footer">
      <a-button @click="handleCancel">关闭</a-button>
      <template v-if="activeKey === tabs.select.key && activeKeySelectType === 1">
        <a-button type="primary" :disabled="tableBtnDisabled" @click="handleClickConfirmSelect">确定</a-button>
      </template>
      <template v-else-if="activeKey === tabs.form.key">
        <a-button type="primary" ghost @click="handleClickReselect">返回重选</a-button>
        <a-button type="primary" @click="handleClickSubmit">确定</a-button>
      </template>
    </div>

  </a-modal>
</template>

<script>
  import moment from 'moment/moment'
  import { getSignStatus, getSignType } from '../utils/util'
  import { getAction, postAction } from '@api/manage'
  import { SignStatusEnum, SignTypeEnum } from '../utils/constant'

  export default {
    name: 'OaSignPatchNowModal',
    props: {
      visible: {
        type: Boolean,
        default: false
      }
    },
    data() {
      const tabs = {
        select: {
          key: 1,
          table: {
            rowKey: 'id',
            size: 'middle',
            bordered: true,
            pagination: false,
            columns: [
              { title: '打卡时间', key: 'signTime', align: 'center', dataIndex: 'signTime' },
              { title: '类型', align: 'center', dataIndex: 'signType', customRender: (text) => getSignType(text) },
              { title: '状态', align: 'center', dataIndex: 'signStatus', scopedSlots: { customRender: 'signStatus' } },
            ],
            dataSource: [],
            rowSelection: {
              type: 'radio',
              selectionRows: [],
              selectedRowKeys: [],
              onChange: (selectedRowKeys, selectionRows) => {
                this.tabs.select.table.rowSelection.selectionRows = selectionRows
                this.tabs.select.table.rowSelection.selectedRowKeys = selectedRowKeys
              }
            },
            customRow: (record) => {
              return {
                on: { click: (event) => this.tabs.select.table.rowSelection.onChange([record.id], [record]) }
              }
            }
          },
        },
        form: {
          key: 2,
          model: { abnormalTime: null, abnormalStatus: null, signType: null, patchTime: null },
          form: this.$form.createForm(this),
          formItemProps: {
            labelCol: { sm: { span: 24 }, md: { span: 6 } },
            wrapperCol: { sm: { span: 24 }, md: { span: 24 - 6 } },
          },
          validatorRules: {
            patchReason: { rules: [{ required: true, message: '请输入补卡原因' }] }
          }
        }
      }
      let today = moment(moment().format('YYYY-MM-DD'))
      let yesterday = moment(today).set('date', (today.get('date') - 1))
      return {
        today,
        yesterday,
        getSignType,
        SignStatusEnum,
        loading: false,
        tabs: tabs,
        activeKey: tabs.select.key,
        activeKeySelectType: 1,
        signRule: {},
        calendarValue: yesterday,
        missingDataSource: [],
        url: {
          getMissingSignInfo: '/sign/patch/getMissingSignInfo',
          getAbnormalSignInfoAndRule: '/sign/patch/getAbnormalSignInfoAndRule',
          addPatch: '/sign/patch/addPatch',
          addMissingPatch: '/sign/patch/addMissingPatch',
        }
      }
    },
    watch: {
      visible() {
        if (this.visible) {
          this.queryMissingSignInfo()
          this.queryAbnormalSignInfoAndRule()
        }
      },
      calendarValue() {
        this.queryMissingSignInfo()
      },
    },
    computed:{
      tableBtnDisabled(){
        return this.tabs.select.table.rowSelection.selectedRowKeys.length === 0
      }
    },
    created() {
    },
    methods: {

      queryAbnormalSignInfoAndRule() {
        this.loading = true
        getAction(this.url.getAbnormalSignInfoAndRule).then(res => {
          if (res.success) {
            let { signRule, abnormalSignInfos } = res.result
            this.signRule = signRule
            this.tabs.select.table.dataSource = abnormalSignInfos
          } else throw res.message
        }).catch(e => {
          this.$warning({ title: '查询打卡信息失败', content: (e || {}).message || e })
        }).finally(() => {
          this.loading = false
        })
      },

      queryMissingSignInfo() {
        this.loading = true
        getAction(this.url.getMissingSignInfo, {
          date: this.calendarValue.format('YYYY-MM-DD')
        }).then(res => {
          if (res.success) {
            this.missingDataSource = res.result
          } else throw res.message
        }).catch(e => {
          this.$warning({ title: '查询打卡信息失败', content: (e || {}).message || e })
        }).finally(() => {
          this.loading = false
        })
      },

      handlePatchMissing(record) {
        let tabForm = this.tabs.form

        let { id, signType, signStatus } = record
        let signTime = this.calendarValue.format('YYYY-MM-DD HH:mm:ss')
        let signTypeText = getSignType(signType)


        tabForm.model.id = null
        tabForm.model.abnormalTime = signTime
        tabForm.model.abnormalStatus = SignStatusEnum.NO_SIGN.text
        tabForm.model.abnormalStatusValue = SignStatusEnum.NO_SIGN.value
        tabForm.model.signType = signTypeText
        tabForm.model.patchType = signType

        let date = moment(signTime).format('YYYY-MM-DD ')
        if (signType === SignTypeEnum.ON_WORK.value) {
          tabForm.model.patchTime = date + this.signRule.onTime + ':00'
        } else {
          tabForm.model.patchTime = date + this.signRule.offTime + ':00'
        }
        this.activeKey = tabForm.key
      },

      handleClickConfirmSelect() {
        let { selectionRows, selectedRowKeys } = this.tabs.select.table.rowSelection
        if (selectedRowKeys.length === 0) {
          return
        }
        let tabForm = this.tabs.form

        let { id, signTime, signType, signStatus } = selectionRows[0]
        let signTypeText = getSignType(signType)
        let signStatusText = getSignStatus(signStatus)

        tabForm.model.id = id
        tabForm.model.abnormalTime = signTime
        tabForm.model.abnormalStatus = signStatusText
        tabForm.model.signType = signTypeText
        tabForm.model.patchType = signType

        let date = moment(signTime).format('YYYY-MM-DD ')
        if (signType === SignTypeEnum.ON_WORK.value) {
          tabForm.model.patchTime = date + this.signRule.onTime + ':00'
        } else {
          tabForm.model.patchTime = date + this.signRule.offTime + ':00'
        }
        this.activeKey = tabForm.key
      },

      handleClickReselect() {
        this.activeKey = this.tabs.select.key
      },

      handleClickSubmit() {
        let tabForm = this.tabs.form
        tabForm.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            let formData = Object.assign({
              signInfoId: tabForm.model.id,
              signTime: tabForm.model.patchTime,
              signType: tabForm.model.patchType
            }, values)
            let url = this.url.addPatch
            if (tabForm.model.abnormalStatusValue === SignStatusEnum.NO_SIGN.value) {
              url = this.url.addMissingPatch
            }
            postAction(url, formData).then((res) => {
              if (res.success) {
                this.loading = false
                this.close(true)
                this.$message.success('添加补卡信息成功')
              } else throw res.message
            }).catch(e => {
              this.$warning({ title: '添加补卡信息失败', content: (e || {}).message || e })
            }).finally(() => {
              this.loading = false
            })
          }
        })
      },

      handleDisabledDate(date) {
        return date >= this.today
      },

      handleCancel() {
        this.close()
      },

      close(ok = false) {
        if (this.loading) {
          return
        }
        if (ok) {
          this.$emit('ok')
        }
        this.$emit('update:visible', false)
        setTimeout(() => {
          this.clearRowSelected()
          this.activeKey = this.tabs.select.key
          this.tabs.select.table.dataSource = []
        }, 300)
      },

      clearRowSelected() {
        this.tabs.select.table.rowSelection.onChange([], [])
      }

    }
  }
</script>

<style lang="less" scoped>

  .hide-tabs-bar {

    /deep/ & > .ant-tabs-bar {
      display: none;
    }
  }

  .bottom-btn {
    width: 100%;
    background: #fff;
    text-align: right;
    padding-top: 10px;

    &.border {
      border-top: 1px solid #e9e9e9
    }

  }

</style>