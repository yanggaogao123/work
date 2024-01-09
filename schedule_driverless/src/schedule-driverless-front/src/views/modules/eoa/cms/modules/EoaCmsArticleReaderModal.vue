<template>
  <a-modal
    :title="title"
    :width="1200"
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
  const columns=[
    {
      title: '序号',
      dataIndex: '',
      key:'rowIndex',
      width:60,
      align:"center",
      customRender:function (t,r,index) {
        return parseInt(index)+1;
      }
    },
    {
      title: "阅读人",
      dataIndex: 'userId_dictText',
      align:"center",
    },
    {
      title: '阅读次数',
      dataIndex: 'readTimes',
      align:"center",
    }
  ];
  const columns1=[
    {
      title: '序号',
      dataIndex: '',
      key:'rowIndex',
      width:60,
      align:"center",
      customRender:function (t,r,index) {
        return parseInt(index)+1;
      }
    },
    {
      title: "下载人",
      dataIndex: 'userId_dictText',
      align:"center",
    },
    {
      title: '下载次数',
      dataIndex: 'readTimes',
      align:"center",
    }
  ];
  export default {
    name: "EoaCmsArticleReaderModal",
    data () {
      return {
        title:"阅读情况",
        visible: false,
        loading: false,
        columns:columns,
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
        articleId:"",
        list: "/cms/eoaCmsArticleRead/list"
      }
    },
    created () {
    },
    methods: {
      loadReader (item) {
        this.visible = true;
        this.articleId=item.id;
        if(item.fileUrl){
          this.columns=columns1
        }
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
        var params = this.getQueryParams();//查询条件
        this.loading = true;
        getAction(this.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            console.log("this.dataSource==>",this.dataSource)
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

<style>
  .rich-img{
    /*display: table-cell;*/
    /*margin:0 auto;*/
    overflow: hidden;
    max-width: 1000px;
  }
  .rich-p{
    color: #333333;
    font-size: 16px;
    line-height: 45px;
  }
  .title{
    font-size: 24px;
    word-wrap: break-word
  }
  .body{
    position: relative;
    padding: 0 24px 16px;
  }
  div{
    padding: 0;
    margin: 0;
  }
  .author{
    height: 55px;
    color: #858585;
    overflow: hidden;
    text-align: right;
  }
  .summary{
    color: rgba(0,0,0,.75);
    font-variant-ligatures: common-ligatures;
    line-height: 1.625;
  }
  p{
    margin-bottom: 2em;
  }
</style>