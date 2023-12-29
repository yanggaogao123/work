<template>
  <a-modal
    :width="modalWidth"
    :visible="visible"
    :title="title"
    @ok="handleSubmit"
    @cancel="close"
    cancelText="关闭"
    style="margin-top: -70px"
    wrapClassName="ant-modal-cust-warp"
  >
        <a-card :bordered="false">
          会议室名称:
          <a-input-search
            :style="{width:'150px',marginBottom:'15px'}"
            placeholder="请输入名称"
            v-model="queryParam.name"
            @search="onSearch"
          ></a-input-search>
          <a-button @click="searchReset(1)" style="margin-left: 20px" icon="redo">重置</a-button>
          <!--用户列表-->
          <a-table
            ref="table"
            :scroll="scrollTrigger"
            size="middle"
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: getType}"
            :loading="loading"
            @change="handleTableChange">
          </a-table>
        </a-card>
  </a-modal>
</template>

<script>
  import {filterObj} from '@/utils/util'
  import {getMettingRoomList} from '@/api/api'

  export default {
    name: 'JSelectMettingRoomModal',
    components: {},
    props: ['modalWidth', 'multi', 'roomIds'],
    data() {
      return {
        queryParam: {
          name: "",
        },
        columns: [
          {
            title: '名称',
            align: 'center',
            dataIndex: 'name'
          },
          {
            title: '位置',
            align: 'center',
            dataIndex: 'position'
          },
          {
            title: '容纳人数',
            align: 'center',
            dataIndex: 'peopleNumber',
          },
          {
            title: '规模',
            align: 'center',
            dataIndex: 'scale_dictText'
          }
        ],
        scrollTrigger: {},
        dataSource: [],
        selectedRowKeys: [],
        selectRoomRows: [],
        selectRoomIds: [],
        title: '选择地点',
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc'
        },
        selectedDepIds: [],
        departTree: [],
        scaleDictOptions: [],
        visible: false,
        form: this.$form.createForm(this),
        loading: false,
        expandedKeys: [],
      }
    },
    computed: {
      // 计算属性的 getter
      getType: function () {
        return this.multi == true ? 'checkbox' : 'radio';
      }
    },
    watch: {
      roomIds: {
        immediate: true,
        handler() {
          this.initUserNames()
        }
      },
    },
    created() {
      // 该方法触发屏幕自适应
      this.resetScreenSize();
      this.loadData()
    },
    methods: {
      initUserNames() {
        console.log("initUserNames");
        if (this.roomIds) {
          // 这里最后加一个 , 的原因是因为无论如何都要使用 in 查询，防止后台进行了模糊匹配，导致查询结果不准确
          let values = this.roomIds.split(',') + ','
          getMettingRoomList({
            id: values,
            pageNo: 1,
            pageSize: values.length
          }).then((res) => {
            if (res.success) {
              let selectedRowKeys = []
              let realNames = []
              res.result.records.forEach(room => {
                realNames.push(room['name'])
                selectedRowKeys.push(room['id'])
              })
              this.selectedRowKeys = selectedRowKeys
              this.$emit('initComp', realNames.join(','))
            }
          })
        } else {
          // JSelectUserByDep组件bug issues/I16634
          this.$emit('initComp', '')
        }
      },
      async loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true
        let params = this.getQueryParams()//查询条件
        await getMettingRoomList(params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
        }).finally(() => {
          this.loading = false
        })

      },
      // 触发屏幕自适应
      resetScreenSize() {
        let screenWidth = document.body.clientWidth;
        if (screenWidth < 500) {
          this.scrollTrigger = {x: 800};
        } else {
          this.scrollTrigger = {};
        }
      },
      showModal() {
        this.visible = true;
        this.initUserNames()
        this.loadData();
        this.form.resetFields();
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      getQueryField() {
        let str = 'id,';
        for (let a = 0; a < this.columns.length; a++) {
          str += ',' + this.columns[a].dataIndex;
        }
        return str;
      },
      searchReset(num) {
        let that = this;
        if (num !== 0) {
          that.queryParam = {};
          that.loadData(1);
        }
        that.selectedRowKeys = [];
        that.selectRoomIds = [];
      },
      close() {
        this.searchReset(0);
        this.visible = false;
      },
      handleTableChange(pagination, filters, sorter) {
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc';
        }
        this.ipagination = pagination;
        this.loadData();
      },
      handleSubmit() {
        let that = this;
        this.getSelectRoomRows();
        that.$emit('ok', that.selectRoomRows, that.selectRoomIds);
        that.searchReset(0)
        that.close();
      },
      //获取选择用户信息
      getSelectRoomRows(rowId) {
        let dataSource = this.dataSource;
        let roomIds = "";
        this.selectRoomRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedRowKeys.includes(dataSource[i].id)) {
            this.selectRoomRows.push(dataSource[i]);
            roomIds = roomIds + "," + dataSource[i].id
          }
        }
        this.selectRoomIds = roomIds.substring(1);
      },

      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      onSearch() {
        this.loadData(1);
      },
      modalFormOk() {
        this.loadData();
      }
    }
  }
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td {
    padding-top: 10px;
    padding-bottom: 10px;
  }

  #components-layout-demo-custom-trigger .trigger {
    font-size: 18px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color .3s;
  }
</style>