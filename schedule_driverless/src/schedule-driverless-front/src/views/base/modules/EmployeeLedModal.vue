<template>
  <a-modal
    title="批量修改LED显示控制"
    :width="1200"
    :visible="visible"
    @cancel="cancle()"
    @ok="ok()"
    footer=""
  >
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="LED显示控制">
              <a-select placeholder="请选择LED显示控制" v-model="queryParam.ledShow" allow-clear>
                <a-select-option value="1">不显示</a-select-option>
                <a-select-option value="0">显示</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="ok" icon="check-circle">确定</a-button>
                <a-button type="primary" @click="cancle" icon="undo" style="margin-left: 8px">取消</a-button>
              </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="employeeId"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      class="j-table-force-nowrap"
      @change="handleTableChange">
    </a-table>
  </a-modal>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { postAction } from '@/api/manage';

export default {
  name: 'EmployeeLedModal',
  mixins: [JeecgListMixin],
  components: {
  },
  data() {
    return {
      modalFooter: {},
      queryParam: {
        ledShow: undefined
      },
      params: undefined, // 传过来原本的请求参数
      visible: false,
      selectValue: '',
      originalSelectedRowKeys: [],
      description: '员工LED显示控制',
      // 表头
      columns: [
        {
          title: '序号',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function (t, r, index) {
            return parseInt(index) + 1;
          }
        },
        {
          title: '所属机构',
          align: 'center',
          dataIndex: 'organName'
        },
        {
          title: '所属线路',
          align: 'center',
          dataIndex: 'routeName'
        },
        {
          title: '员工姓名',
          align: 'center',
          dataIndex: 'employeeName'
        },
        {
          title: '资格证号',
          align: 'center',
          dataIndex: 'qualification'
        },
        {
          title: '员工类别',
          align: 'center',
          dataIndex: 'employeeType',
          customRender: function (text) {
            return text === '0' ? '司机' : (text === '1' ? '调度员' : '乘务员');
          }
        },
        {
          title: '电话号码',
          align: 'center',
          dataIndex: 'mobile'
        },
        {
          title: 'LED显示控制',
          align: 'center',
          dataIndex: 'ledShow',
          customRender: function (text) {
            return text === '0' ? '显示' : '不显示';
          }
        }
      ],
      url: {
        updateEmployeeLedList: '/base/employee/updateEmployeeLedList',
        getEmployeeLedByIds: '/base/employee/getEmployeeLedByIds'
      }
    }
  },
  methods: {
    loadData(arg) {
    },
    edit(selectedRowKeys, selectionRows) {
      this.dataSource = selectionRows;
      this.originalSelectedRowKeys = selectedRowKeys
      this.ipagination.total = selectionRows.length
      this.visible = true
    },
    cancle() {
      this.visible = false
    },
    ok() {
      if (this.queryParam.ledShow === undefined) {
        this.$message.warn('LED显示控制 不能为空')
        return
      }
      const params = {
        ledShow: this.queryParam.ledShow,
        employeeLedList: this.dataSource
      }
      postAction(this.url.updateEmployeeLedList, params).then(res => {
        console.log('res', res)
        if (res.success) {
          // 刷新弹窗数据
          postAction(this.url.getEmployeeLedByIds, this.originalSelectedRowKeys).then(ress => {
            this.dataSource = ress.result;
            this.ipagination.total = ress.result.length
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>