<template>
  <a-modal :width="modalWidth" :visible="visible" destroyOnClose title="角色分配选择" :confirmLoading="confirmLoading"
    @ok="handleOk" @cancel="handleCancel" cancelText="关闭" wrapClassName="ant-modal-cust-warp">
    <!--角色分配树-->
    <template>
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :span="10">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="平台">
              <a-select allowClear labelInValue :value="{ key: this.platformCode }" @change="platformChange">
                <a-select-option value="">请选择平台</a-select-option>
                <a-select-option v-for="(item, key) in platformList" :key="key" :value="item.platformCode">{{
                  item.platformName
                }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <div class="checkDiv">
        <div class="radioDiv">
          <div class="title">未选</div>
          <div style="padding: 5px 10px;">
            <a-radio :style="radioStyle" v-for="item in roleList" :key="item.id" :value="item.id"
              :checked="item.checked" @change="onChangeLeft">
              {{item.platformName}}-{{ item.roleName }} - {{ item.roleType == 1 ? '机构级' : '用户级' }}
            </a-radio>
          </div>
        </div>
        <div class="radioDiv">
          <div class="title">已选</div>
          <div class="right-checkbox">
            <a-checkbox v-for="item in selectRoleList" :style="radioStyle" :key="item.id" :value="item.id"
              :checked="item.checked2" @change="onChangeRight">
              {{item.platformName}}-{{ item.roleName }} - {{ item.roleType == 1 ? '机构级' : '用户级' }}
            </a-checkbox>
          </div>
        </div>
      </div>
    </template>
  </a-modal>
</template>

<script>
import { getAction } from '@/api/manage'
export default {
  name: "RoleWindow",
  props: {
    selectedOrgCodeValue: {
      type: String,
      default: ""
    },
    selectType: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      modalWidth: 800,
      visible: false,
      labelCol: {
        xs: { span: 4 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 18 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      platformCode: '', // 选择的平台code
      platformList: [], // 平台列表
      roleList: [], // 某个平台下的角色列表（右边框列表）
      selectRoleList: [], // 选中的角色列表（左边框列表）
      url: {
        roleList: "/sys/role/queryRoleTreeByOrg", // 角色列表
      },
      radioStyle: {
        display: 'block',
        height: '30px',
        lineHeight: '30px',
      },
    };
  },
  methods: {
    show(selectedRoleObj) {
      console.log(selectedRoleObj);
      if(selectedRoleObj){
        this.selectRoleList = selectedRoleObj
      } else {
        this.selectRoleList = []
      }
      this.visible = true;
      this.loadPlatformList()
      this.initData()
    },
    initData(){
      this.platformCode = ''
      this.roleList = []
    },
    close() {
      this.$emit("close");
      this.visible = false;
    },
    handleOk() {
      console.log('this.selectRoleList',this.selectRoleList)
      this.$emit('ok', this.selectRoleList)
      this.close();
    },
    handleCancel() {
      this.close();
    },
    // 获取平台下拉列表
    loadPlatformList() {
      this.platformList = this.$store.getters.userPlatforms
    },
    // 选择平台
    platformChange(selVal) {
      if (selVal) {
        this.platformCode = selVal.key
        this.getRoleTreeData()
      } else {
        this.platformCode = ''
      }
    },
    // 获取角色数据
    async getRoleTreeData() {
      await getAction(this.url.roleList, {
        orgCode: this.selectedOrgCodeValue,
        roleType: this.selectType == 9999 ? 1 : this.selectType == 1 ? 1 : 2,
        platformCode: this.platformCode
      }).then(res => {
        if (res.success) {
          console.log(res.result);
          if(res.result && res.result.length){
            if (res.result[0].roles) {
              this.roleList = res.result[0].roles.map(item => {
                return {
                  ...item,
                  checked: false,
                  platformCode: res.result[0].platformCode,
                  platformName: res.result[0].platformName,
                }
              })
            }
          } else {
            this.$message.warning('该行政机构，当前平台无角色')
          }
        }
        else {
          this.$message.warning(res.message);
        }
      }).catch(err => {
        this.$message.warning(err.message);
      });
    },
    // 左边框单选
    onChangeLeft(e) {
      this.roleList.forEach(item => {
        if (item.id == e.target.value) {
          item.checked = true
          if (!this.selectRoleList.length) {
            this.selectRoleList.push({
              ...item,
              checked2: true
            })
          } else {
            let index = this.selectRoleList.findIndex(val => val.platformCode == item.platformCode)
            if (index == -1) {
              this.selectRoleList.push({
                ...item,
                checked2: true
              })
            } else {
              this.selectRoleList.splice(index, 1)
              this.selectRoleList.push({
                ...item,
                checked2: true
              })
            }
          }
        } else {
          item.checked = false
        }
      })
    },
    // 右边框多选
    onChangeRight(e) {
      this.roleList.forEach(item => {
        if (item.id == e.target.value) {
          item.checked = false
        }
      })
      console.log(this.roleList);
      let index = this.selectRoleList.findIndex(val => val.id == e.target.value)
      this.selectRoleList.splice(index, 1)
    }
  },
}
</script>

<style lang="less" scoped>
.checkDiv {
  width: 100%;
  display: flex;
  justify-content: space-between;

  .radioDiv {
    width: 45%;
    height: 300px;
    overflow-y: scroll;
    border: 1px solid #d9d9d9;

    .title {
      border-bottom: 1px solid #d9d9d9;
      height: 40px;
      line-height: 40px;
      padding: 0 10px;
    }

    .right-checkbox {
      padding: 5px 10px;
      display: flex;
      flex-direction: column;
      align-items: baseline;
    }
  }

  /deep/ .ant-checkbox-wrapper+.ant-checkbox-wrapper {
    margin-left: 0;
  }

}
</style>