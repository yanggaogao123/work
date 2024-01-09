<template>
    <div>
        <a-select
            show-search
            :disabled="disabled"
            v-model="dataSelect"
            :placeholder="placeholder"
            :mode="mode"
            allowClear
            option-filter-prop="children"
            :style="style"
            :filter-option="filterOption"
            :getPopupContainer="triggerNode => triggerNode.parentNode"
            @search="routeSearch"
            @focus="handleFocus"
            @blur="handleBlur"
            @change="handleChange"
            @popupScroll="popupScroll"
        >
            <a-select-option
                v-for="item in routeList"
                :key="item['' + routeItem + '']"
                :value="item['' + routeItem + '']"
            >
                {{ item.routeName }}
            </a-select-option>
            <!-- <a-select-option :key="427" value="427">
        {{ `1路` }}
      </a-select-option> -->
        </a-select>
    </div>
</template>
<script>
import { getAction, postAction } from '@/api/manage';
import { queryListRoute, queryPageListRoute } from '@/api/api';

export default {
    name: 'GCIRouteSelect',
    props: {
        //mode:默认单选，多选multiple 返回","分隔的字符串
        mode: {
            type: String,
            default: 'mode'
        },
        //item:指定返回线路字段，默认返回线路id字段
        item: {
            type: String,
            default: 'id'
        },
        value: [Number, String, Array],
        placeholder: '',
        allowClear: true,
        width: {
            type: [Number, String],
            default: '100%'
        },
        // paramValue: '',
        disabled: {
            type: [Boolean],
            default: false
        }
    },
    components: {},
    data() {
        return {
            isUserParent: false,
            routeItem: this.item,
            visible: false,
            dataSelect: undefined,
            route: '',
            routeListAll: [],
            filterList: [], //前端搜索过滤用
            routeList: [],
            isLoading: false,
            page: 1,
            routeName: ''
        };
    },
    computed: {
        // 计算属性的 getter
        style: function() {
            return 'width:' + this.width + 'px';
        }
    },
    watch: {
        value: {
            deep: true, // 解决清空问题
            handler(newVal, oldVal) {
                let that = this;
                console.log('newVal', newVal);
                console.log('oldVal', oldVal);

                console.log('this.value', this.value);
                setTimeout(() => {
                    console.log(this.routeList);
                    if (this.value && !this.isUserParent) {
                        this.dataSelect = [this.value];
                    }
                    console.log('watch.value=' + newVal);
                    if (this.value === undefined) {
                        this.dataSelect = undefined;
                    } else if (this.dataSelect == undefined) {
                        this.dataSelect = [this.value];
                    }
                    console.log('select', this.dataSelect);

                    // 回显参数如果在 当前页的线路数据 没有就从 所有线路数据 中找
                    // if (
                    //     this.routeList.filter(item => item[this.item] == this.dataSelect).length == 0 &&
                    //     this.dataSelect
                    // ) {
                    //     this.routeList = this.routeList.concat(
                    //         this.routeListAll.filter(item => item[this.item] == this.value)
                    //     );
                    // }
                    if (this.dataSelect) {
                        var isFound = this.routeList.some(obj => obj[this.item == this.valus]);
                        if (!isFound) {
                            this.routeList = this.routeListAll.filter(item => item[this.item] == this.value);
                            // this.routeList.push(obj);
                        }
                    }
                    console.log(this.routeList);
                }, 500);
            }
        }
        // paramValue: {
        //   handler(newVal, oldVal) {
        //     console.log('watch.paramValue=' + newVal)
        //     this.routeName = newVal
        //     this.loadData()
        //   }
        // }
    },
    created() {
        this.page = 1;
        this.routeList = [];
        this.loadData('');
        setTimeout(() => {
            console.log(this.value);
            if (this.value) {
                console.log('进来了');
                this.dataSelect = this.value;
            }
        }, 500);
    },
    methods: {
        handleChange(value) {
            console.log('value change', value);
            console.log('mode change', this.mode);
            console.log(this.dataSelect);
            this.dataSelect = value;
            // 清空选项时，是输入名称搜索过的话再调一次接口初始化数据
            if ((this.mode == 'mode' && value == undefined) || (this.mode == 'multiple' && value.length == 0)) {
                this.routeList = this.routeListAll.slice(0, 30);
            }
            this.isUserParent = true;
            let val;
            if (value instanceof Array) {
                val = value.join(',');
            } else {
                val = value;
            }
            console.log(('单选返回', val));
            this.$emit('input', val);
            this.$emit('change', val);
        },
        filterOption(input, option) {
            return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        },
        loadData() {
            let that = this;
            let sendData = {
                routeName: this.routeName
                // pageNo: this.page,
                // pageSize: 30,
            };
            this.isLoading = true;

            queryListRoute(sendData).then(res => {
                if (res.success) {
                    // 前端分页用
                    if (!this.routeName) {
                        that.routeListAll = res.result;
                        console.log('routeListAll', that.routeListAll);
                    }
                    that.routeList = res.result.slice(0, 30);
                    console.log('value', this.value);
                    if (this.value) {
                        // 回显参数如果在 当前页的线路数据 没有就从 所有线路数据 中找
                        console.log(this.routeList);

                        var isFound = this.routeList.some(obj => obj[this.item == this.valus]);

                        if (!isFound) {
                            this.routeList = this.routeListAll.filter(item => item[this.item] == this.value);
                            // this.routeList.push(obj);
                        }
                        console.log(this.routeList);
                        // if(this.routeList.filter(item=>item[this.item] == this.value))
                        // if (this.routeList.filter(item => item[this.item] == this.value).length == 0 && this.value) {
                        //   this.routeList = this.routeListAll.filter(item => item[this.item] == this.value)
                        //   console.log('routeList', this.routeList)
                        // }

                        // if (this.routeList.filter(item => item.id != this.value)) {
                        //   this.routeList = this.routeListAll.filter(item => item[this.item] == this.value)
                        //   console.log('routeList', this.routeList)
                        // }
                    }

                    // 后端分页用
                    // if(that.page == 1){
                    // that.routeList = res.result.records
                    // }else {
                    //   let tmpArr = that.routeList
                    //   that.routeList = []
                    //   that.routeList = tmpArr.concat(res.result.records)
                    // }
                }
                that.isLoading = false;
            });
        },
        // 搜索/过滤
        routeSearch(value) {
            console.log(value);
            this.page = 1;
            // 后端分页用
            this.routeName = value;
            // this.loadData()

            // 前端分页用
            // this.filterList = this.routeList.slice(0).filter(item => item.routeName.indexOf(value) != -1)
            // if (this.routeName) {
            this.routeList = this.routeListAll.filter(item => item.routeName.indexOf(value) != -1).slice(0, 30);
            // } else {
            //   this.routeList = this.routeListAll.slice(0, 30)
            // }
        },
        // 加载下一页数据
        popupScroll(e) {
            const { target } = e;
            if (target.scrollTop + target.offsetHeight === target.scrollHeight && !this.isLoading) {
                this.page += 1;
                // 后端分页用
                // this.loadData()
                // 前端分页用
                console.log(this.routeList);
                if (this.routeName == '') {
                    this.routeList = this.routeList.concat(
                        this.routeListAll.slice(30 * (this.page - 1), 30 * this.page)
                    );
                    // this.routeList = this.routeListAll.slice(0, 30 * this.page)
                }
                // else {
                //   this.routeList = this.routeListAll
                //     .filter(item => item.routeName.indexOf(this.routeName) != -1)
                //     .slice(30 * (this.page - 1), 30 * this.page)
                // }
                console.log(this.routeList);
            }
        },
        handleFocus() {
            console.log('RouteSelect Focus');
        },
        handleBlur() {
            console.log('RouteSelect Blur');
        }
    }
};
</script>
<style lang="scss" scoped></style>
