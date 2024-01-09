<template>
  <div id="components-grid-demo-playground">
    <div style="margin-bottom:16px">
      <QuicklyPermission-modal ref="modalForm" @ok="modalFormOk" :menus="dataSource"></QuicklyPermission-modal>
      <a-card v-if="showEdit">
        <div>
          <a-button type="primary"  @click="showMenuEdit">隐藏配置</a-button>
          <a-button  @click="defaultSetting" style="margin-left:20px;">默认配置</a-button>
        </div>
        <span style="margin-right: 6px">水平间距: </span>
        <div style="width:50%">
          <a-slider
            v-model="gutterKey"
            :min="0"
            :max="Object.keys(gutters).length - 1"
            :marks="gutters"
            :step="null"
            @change="changegutters"
          />
        </div>
        <span style="margin-right: 6px">垂直间距: </span>
        <div style="width: 50%">
          <a-slider
            v-model="vgutterKey"
            :min="0"
            :max="Object.keys(vgutters).length - 1"
            :marks="vgutters"
            :step="null"
            @change="changevgutters"
          />
        </div>
        <span style="margin-right:6px">每行菜单数:</span>
        <div style="width:50%">
          <a-slider
            v-model="colCountKey"
            :min="0"
            :max="Object.keys(colCounts).length - 1"
            :marks="colCounts"
            :step="null"
            @change="changecountgutters"
          />
        </div>
      </a-card>
    </div>
    <a-card size="small">
      <a-row>
        <a-col :span="4">
          <div class="icon-tag" @click="handleAdd">
            <div class="tag-icon">
              <a-icon type="plus" />
            </div>
            <div class="tag-name">新增快捷菜单</div>
          </div>
        </a-col>
        <a-col :span="4">
          <div class="icon-tag" @click="showMenuEdit">
            <div class="tag-icon">
              <a-icon type="setting" />
            </div>
            <div class="tag-name">配置常用菜单样式</div>
          </div>
        </a-col>
      </a-row>
    </a-card>
    <a-card >
      <a-row
        justify="center"
        v-for="(num, rowIndex) in rowCount"
        :key="num"
        :gutter="[gutters[gutterKey], vgutters[vgutterKey]]">
        <a-col
          v-for="(item, index) in colCounts[colCountKey]"
          :key="(rowIndex) * colCounts[colCountKey] + index"
          :span="24 / colCounts[colCountKey]"
        >
          <div class="icon-tag"
               v-if="dataSource[(rowIndex) * colCounts[colCountKey] + index] !== undefined"
               @click="jump(dataSource[(rowIndex) * colCounts[colCountKey] + index])">
            <div class="tag-delete" @click.stop="deleteMenu(dataSource[(rowIndex) * colCounts[colCountKey] + index])">
              <a-icon type="close" />
            </div>
            <div class="tag-edit" @click.stop="handleEdit(dataSource[(rowIndex) * colCounts[colCountKey] + index])">
              <a-icon type="edit" />
            </div>
            <div class="tag-icon">
              <a-icon v-if="dataSource[(rowIndex) * colCounts[colCountKey] + index].icon" :type="dataSource[(rowIndex) * colCounts[colCountKey] + index].icon" />
              <a-icon v-else type="align-center" />
            </div>
            <div class="tag-name">{{dataSource[(rowIndex) * colCounts[colCountKey] + index].name}}</div>

          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>

</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import QuicklyPermissionModal from './QuicklyPermissionModal'
import { postAction, getAction } from '@/api/manage'
import Vue from 'vue'
import {ACCESS_TOKEN} from "../../store/mutation-types";
import {encryption} from "../../utils/encryption/aesEncrypt";



export default {
  mixins:[JeecgListMixin, mixinDevice],
  components: {
    QuicklyPermissionModal
  },
  data() {
    const cgutter = "CGUTTER_VALUE"
    const vgutter = "VGUTTER_VALUE"
    const countgutter = "GUTTER_COUNT_VALUE"
    const gutters = {};
    const colCounts = {};
    const vgutters = {};
    [8, 16, 24, 32, 40, 48].forEach((value, i) => {
      gutters[i] = value;
    });
    [8, 16, 24, 32, 40, 48].forEach((value, i) => {
      vgutters[i] = value;
    });
    [2, 3, 4, 6, 8, 12].forEach((value, i) => {
      colCounts[i] = value;
    });
    return {
      gutterKey: 1,
      vgutterKey: 2,
      colCountKey: 3,
      colCounts,
      gutters,
      vgutters,
      cgutter,
      vgutter,
      countgutter,
      url: {
        list: "/sys/sysQuicklyPermission/list",
        delete: "/sys/sysQuicklyPermission/delete",
        deleteBatch: "/sys/sysQuicklyPermission/deleteBatch",
        exportXlsUrl: "/sys/sysQuicklyPermission/exportXls",
        importExcelUrl: "sys/sysQuicklyPermission/importExcel",
      },
      quickMenuList:[],
      columns:[],
      dataSource: [],
      showEdit: false,
    };
  },
  computed: {
    rowCount(){
      console.log(this.dataSource)
      return parseInt(this.dataSource.length / this.colCounts[this.colCountKey]) + 1
    },
  },
  methods: {
    loadData() {
      let cgut = Vue.ls.get(this.cgutter);
      let vgut = Vue.ls.get(this.vgutter);
      let countgut = Vue.ls.get(this.countgutter);
      if (cgut){
        this.gutterKey = cgut;
      }
      if (vgut){
        this.vgutterKey = vgut;
      }
      if (countgut){
        this.colCountKey = countgut;
      }
      let params = {}
      getAction(this.url.list, params).then(res => {
        if (res.success) {
          this.dataSource = res.result;
        } else throw res.message
      })
    },
    jump(menu) {
      console.log('跳转菜单',menu)
      let url = menu.url
      let internalOrExternal = menu.internalOrExternal
      let component = menu.component
      let appId = menu.appId
      if(internalOrExternal != undefined && internalOrExternal==true){
        if(component == 'layouts/IframePageView' && appId){
          let token = Vue.ls.get(ACCESS_TOKEN);
          let menuId =  menu.permissionId
          let manageUrl = process.env.VUE_APP_DOMAIN_URL + process.env.VUE_APP_BASE_API
          let time = new Date ().getTime ()
          console.log("------url------"+url)
          let params = [manageUrl, token, menuId, time].join(";")
          this.getEncrypte()
          let paramString = encryption(params,this.encryptedString.key, this.encryptedString.iv)

          if (url.lastIndexOf("?") == -1) {
            url += "?paramString=" + paramString
          } else {
            url += "&paramString=" + paramString
          }
        }
        if(url.indexOf("http") === 0){
          window.open(url);
        }else {
          window.open('http://'+url);
        }
      }else {
        if(component == 'layouts/IframePageView'){ //iframe组件暂时使用外链跳转
          let token = Vue.ls.get(ACCESS_TOKEN);
          let menuId =  menu.permissionId
          let manageUrl = process.env.VUE_APP_DOMAIN_URL + process.env.VUE_APP_BASE_API
          let time = new Date ().getTime ()
          console.log("------url------"+url)
          let params = [manageUrl, token, menuId, time].join(";")
          this.getEncrypte()
          let paramString = encryption(params,this.encryptedString.key, this.encryptedString.iv)

          if (url.lastIndexOf("?") == -1) {
            url += "?paramString=" + paramString
          } else {
            url += "&paramString=" + paramString
          }
          url += "&language=zh_CN";
          if(url.indexOf("http") === 0){
            window.open(url);
          }else {
            window.open('http://'+url);
          }
          return
        }
        this.$router.push(url);
      }
    },
    showMenuEdit(){
      this.showEdit = !this.showEdit;
    },
    deleteMenu(record){
      let that = this;
      this.$confirm({
        title: '确认删除该菜单?',
        onOk() {
          that.handleDelete(record.id);
        },
        onCancel() {
          console.log('Cancel');
        },
        class: 'test',
      });
    },
    changegutters(val){
      Vue.ls.set(this.cgutter,val);
    },
    changevgutters(val){
      Vue.ls.set(this.vgutter,val);
    },
    changecountgutters(val){
      Vue.ls.set(this.countgutter,val);
    },
    defaultSetting(){
      this.gutterKey = 1;
      this.vgutterKey = 2;
      this.colCountKey = 3;
      Vue.ls.set(this.cgutter,1);
      Vue.ls.set(this.vgutter,2);
      Vue.ls.set(this.countgutter,3);
    }

  }
};
</script>
<style lang="less" scoped>
.icon-tag {
  width: 100%;
  margin: 0 auto;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  flex-flow: column;
  align-items: center;
  cursor: pointer;
  .tag-edit {
    display: none;
  }
  .tag-delete{
    display: none;
  }
  .tag-icon {
    margin: 12px 0 8px;
    // background: #fff;
    font-size: 36px;
  }
  .tag-name {
    padding-bottom: 4px;
    color: #333;
  }
}
.icon-tag:hover {
  background-color: #1890ff;
  color: #fff;
  position: relative;
  .tag-edit {
    font-size: 18px;
    color: #fff;
    display: block;
    position: absolute;
    top: 2px;
    right: 2px;
    cursor: pointer;
  }
  .tag-delete{
    font-size: 18px;
    color: #fff;
    display: block;
    position: absolute;
    top: 2px;
    left: 2px;
    cursor: pointer;
  }
  .tag-icon {
    color: #fff;
    transform: scale(1.4);
    transition: transform .3s ease-in-out;
    will-change: transform;
  }
  .tag-name {
    color: #fff;
  }
}
</style>