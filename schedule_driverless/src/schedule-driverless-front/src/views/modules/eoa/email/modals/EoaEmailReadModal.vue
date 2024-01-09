<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    @cancel="handleCancel">
    <template slot="footer">
      <a-button key="back" @click="handleCancel">关闭</a-button>
    </template>
    <a-table
      ref="table"
      size="small"
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading">
    </a-table>
  </a-modal>
</template>

<script>
  import { getAction } from '@/api/manage'
  import { filterObj } from '@/utils/util';

  export default {
    name: 'EoaEmailReadModal',
    data () {
      return {
        title:"阅读情况",
        visible: false,
        loading: false,
        columns: [
          {
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function (t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '收件人',
            align: 'center',
            dataIndex: 'userId_dictText'
          },
          {
            title: '阅读状态',
            align: 'center',
            dataIndex: 'readFlag',
            customRender: (t) => t === '1' ? '已读' : '未读'
          },
          {
            title: '撤回状态',
            align: 'center',
            dataIndex: 'withdrawFlag',
            customRender: (t) => t === '1' ? '已撤回' : ''
          }
        ],
        userIdTitle:'阅读人',
        dataSource: [],
        /* 分页参数 */
        ipagination:{
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        mailId:"",
        list: "/email/eoaMailboxInfo/receiverPageList"
      }
    },
    created () {
    },
    methods: {
      loadReader (id) {
        this.visible = true;
        this.mailId = id;
        this.loadData()
      },
      loadData(arg) {
        if(!this.list){
          this.$message.error("请设置list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true;
        getAction(this.list, {mailId:this.mailId}).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total;
          }
          if(res.code===510){
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      getQueryParams() {
        //获取查询条件
        let param = {}
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        param.articleId = this.articleId;
        return filterObj(param);
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        this.close();
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>

<style scoped>

</style>