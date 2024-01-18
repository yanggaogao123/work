<template>
    <div id="feedback">
        <header>
            <img src="../../assets/feedback-head.png" alt="">
        </header>
        <section>
            <div class="area-type clearfix">
                <span class="fl">反馈类型</span>
                <div class="fr" @click="showToggle()">
                    <span v-if="showItem">请选择</span>
                    <span class="selectItem">{{conSelect.name}}</span>
                    <i class="fa fa-angle-down"></i>
                </div>
            </div>
            <textarea @blur="showLimit" v-model="msg" maxlength="500" id="ipt-feedback" placeholder="请输入您要反馈的内容，字数限制10-500"></textarea>

            <div class="area-upload">
                <van-uploader class="photo-upload" v-model="fileList" upload-text='上传图片' :max-count="3" :after-read="afterRead"/>
                <!-- <a-upload action='' list-type="picture-card" :file-list="fileList" @preview="handlePreview"
                     @change="handleChange">
                    <div v-if="fileList.length < 3">
                        <a-icon type="plus" />
                        <div class="ant-upload-text">
                            Upload
                        </div>
                    </div>
                </a-upload> -->
            </div>

            <div class="area-phone">
                <span>联系电话</span>
                <input v-model="msgNum" type="text" placeholder="请输入" class="fr" maxlength='11' onkeyup="(this.v=function(){this.value=this.value.replace(/[^0-9-]+/,'');}).call(this)" onblur="this.v();">
            </div>
            <div class="area-name clearfix">
                <span>您的称呼（选填）</span>
                <input v-model="msgName" placeholder="请输入" type="text" maxlength="4" class="fr">
            </div>



            <div class="bg" v-show="show" @click="showToggle()"></div>
            <ul class="type-select" v-show="show">
                <li v-for="(item,index) in liList" @click="select(item)">{{item.name}}</li>
            </ul>

        </section>
        <footer>
            <button @click="submit()">提交</button>
        </footer>
    </div>
</template>

<script>
    import axios from 'axios';

    function getBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
        });
        
    }
    function base64(file){
        var reader = new FileReader();
        reader.readAsDataURL(file);
        let base64 = ''
        reader.onloadend = function () {
            base64 = reader.result;
        }
         console.log(base64);
        return base64;
    }
    export default {
        name: "feedback",
        data() {
            return {
                show: false,
                msg: '',
                msgNum: '',
                msgName: '',
                showItem: true,
                conSelect: {
                    'name': '请选择',
                    'id': '',
                },
                // li选项
                liList: [{
                    "id": "3",
                    "name": "取消"
                }],
                fileList: [],
                picList:[],
            }
        },
        methods: {
            hideIt() {
                this.show = false;
            },
            showToggle() {
                // console.log(this.show);
                this.show = !this.show;
            },
            select(data) {
                var type = data.id;
                // console.log(type);
                if (type == 3) {
                    this.showToggle();
                } else {
                    this.show = false;
                    this.conSelect.id = data.id;
                    this.conSelect.name = data.name;
                }
            },
            showLimit() {
                if (this.msg.length > 0 && (this.msg.length < 10 || this.msg.length > 500)) {
                    this.$toast("反馈内容请输入至少10个字");
                }
            },
            afterRead(file) {
                console.log(this.fileList);

            },

            upload(data){
                console.log(data);
                 let file = {  
                    uid: data.file.uid,    // 文件唯一标识，建议设置为负数，防止和内部产生的 id 冲突  
                    name: data.file.name,   // 文件名  
                    status: 'done', // 状态有：uploading done error removed  
                    response: '{"status": "success"}', // 服务端响应内容  
                };
                console.log(file);
                this.uploadList.push(file)
            },
            handleCancel() {
                this.previewVisible = false;
            },
            async handlePreview(file) {
                if (!file.url && !file.preview) {
                    file.preview = await getBase64(file.originFileObj);
                }
                this.previewImage = file.url || file.preview;
                this.previewVisible = true;
                console.log(this.previewImage);
            },
            handleChange({
                fileList
            }) {
                let that = this;
                // console.log(fileList);
                // let a = fileList[0];
                // let file = {  
                //     uid: a.uid,    // 文件唯一标识，建议设置为负数，防止和内部产生的 id 冲突  
                //     name: a.name,   // 文件名  
                //     status: 'done', // 状态有：uploading done error removed  
                //     response: '{"status": "success"}', // 服务端响应内容  
                // };
                // console.log(file);
                this.fileList= fileList;

                console.log(this.fileList)

                let list = fileList;
                that.picList = [];
                list.forEach((item,index) => {
                    getBase64(item.originFileObj).then((res)=>{
                        // console.log(res);
                        item.base64 = res.replace(/^data:image\/\w+;base64,/, '');
                        that.picList.push({'base64':item.base64,'index':index});
                    })
                });
            },

            submit() {
                let that = this;
                // textarea
                var msg = this.msg;
                // 昵称
                var msgName = this.msgName;
                // 电话
                var msgNum = this.msgNum;
                // 反馈类型
                var type = this.conSelect.id;


                if (type == "") {
                    this.$toast("请选择反馈类型");
                    return false;
                }

                if (!(/^1[3456789]\d{9}$/.test(msgNum))) {
                    this.$toast("手机号码有误，请重填");
                    return false;
                }

                // const Get = async  =>{

                // }

                let pic = ''
                if (this.fileList != []) {
                    pic = this.fileList.map((item) => {
                        return item.content.replace(/^data:image\/\w+;base64,/, '')
                    })
                    // console.log(that.picList);

                    // console.log(pic);

                    pic = pic.toString()
                }

                // console.log(pic)

                if (msg.length >= 10 && msg.length <= 500 && msgNum.length > 0) {
                    let data = {
                        "content": msg,
                        "nickName": msgName,
                        "tel": msgNum,
                        "typeId": type,
                        "pic": pic,
                    }

                    let param = that.data_to_string(data);

                    axios.post(`${process.env.VUE_APP_LIST_API}/feedBackMessage/add`, param, {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }).then((res) => {
                        console.log(res)
                        if (res.data.retCode != 0) {
                            that.$toast("留言失败请重试")
                            return
                        }
                        that.$toast("留言成功");
                        setTimeout(function() {
                            var u = navigator.userAgent,
                                app = navigator.appVersion;
                            var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
                            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
                            if (isiOS) {
                                window.GCI.startClose("1");
                            } else if (isAndroid) {
                                AndroidApp.ExcActivity();
                            }

                        }, 3000)
                    })

                    // 提交接口
                    //     $.ajax({
                    //         type: "post",
                    //         url: "/xxt_app/feedBackMessage/add",
                    //         data: {
                    //             "content": msg,
                    //             "nickName": msgName,
                    //             "tel": msgNum,
                    //             "typeId": type
                    //         },
                    //         dataType: "json",
                    //         success: function success(data) {
                    //             if (data.retCode == 0) {
                    //                 $.alert('提交成功');
                    //                 setTimeout(function() {
                    //                     var u = navigator.userAgent,
                    //                         app = navigator.appVersion;
                    //                     var isAndroid = u.indexOf('Android') > -1; //android终端或者uc浏览器
                    //                     var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
                    //                     if (isiOS) {
                    //                         window.GCI.startClose("1");
                    //                     } else if (isAndroid) {
                    //                         AndroidApp.ExcActivity();
                    //                     }

                    //                 }, 3000)

                    //             } else {
                    //                 $.alert('提交失败，请稍后重试');
                    //             }
                    //         },
                    //         error: function error(res) {
                    //             $.alert('提交失败，请稍后重试');
                    //         }
                    //     })
                    // } else {
                    //     $.alert('请正确填写信息')
                    // }
                } else {
                    that.$toast("请填写留言内容");
                }
            },
            data_to_string(data) {
                let tmp = new FormData();
                for (var key in data) {
                    tmp.append(key, data[key])
                }
                return tmp
            },
        },
        created() {
            var that = this;
            if (that.liList.length < 2) {
                axios.post(`${process.env.VUE_APP_LIST_API}/feedBackMessage/getFeedbackType?type=4`).then((res) => {
                    console.log(res);
                    if (res.data.retCode != 0) {
                        that.$toast('系统报错，请重试');
                        return
                    }
                    let data = res.data.retData;
                    that.liList = data;
                    var cancel = {
                        "id": "3",
                        "name": "取消"
                    };
                    that.liList.push(cancel);
                    that.showItem = false;
                })
                // $.ajax({
                //     type: "post",
                //     url: "/xxt_app/feedBackMessage/getFeedbackType",
                //     data: {
                //         "type": 4
                //     },
                //     dataType: "json",
                //     success: function success(data) {
                //         if (data.retCode == 0) {
                //             vm.liList = data.retData;
                //             var cancel = {
                //                 "id": "3",
                //                 "name": "取消"
                //             };
                //             vm.liList.push(cancel);
                //             vm.showItem = false;
                //             $(".selectItem").css("display", "inline-block")
                //         }
                //     }
                // })
            }
        }
    }
</script>

<style scoped lang="less">
    html,
    body {
        width: 100%;
        min-height: 100%;
    }

    body {
        background: #EEEEEE;
        position: relative;
    }

    #feedback {
        width: 100%;

        header {
            width: 100%;

            img {
                display: block;
                width: 100%;
            }
        }

        section {
            width: 100%;
            background: #fff;

            .area-type {
                height: 44px;
                line-height: 44px;
                font-size: 14px;
                box-sizing: border-box;
                padding: 0 10px;
                border-bottom: 1px solid #DFDFDF;

                span {
                    display: inline-block;
                }

                div {
                    display: inline-block;
                }
            }

            #ipt-feedback {
                display: block;
                width: 100%;
                height: 176px;
                box-sizing: border-box;
                padding: 10px;
                border: none;
                resize: none;
                outline: none;
                -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
            }

            .area-phone,
            .area-name {
                height: 44px;
                line-height: 44px;
                font-size: 14px;
                box-sizing: border-box;
                padding: 0 10px;
                border-top: 1px solid #DFDFDF;
                border-bottom: 1px solid #DFDFDF;

                input {
                    display: inline-block;
                    // line-height: 42px;
                    height: 40px;
                    margin-top: 2px;
                    text-align: right;
                    border: none;
                    outline: none;
                    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
                }
            }

            .area-name {
                border-top: none;
            }

            .area-upload {
                width: 100%;
                padding: 0 20px;
                box-sizing: border-box;

                .photo-upload {}
            }

            .bg {
                // display: none;
                position: fixed;
                width: 100%;
                height: 100%;
                background: #000;
                opacity: 0.4;
                top: 0;
                left: 0;
                z-index: 1;
            }

            .type-select {
                // display: none;
                position: fixed;
                z-index: 2;
                width: 100%;
                left: 0;
                bottom: 0px;
                background: #EEEEEE;

                li {
                    height: 60px;
                    text-align: center;
                    font-size: 20px;
                    line-height: 60px;
                    background: #fff;
                    border-bottom: 1px solid #DFDFDF;
                }

                li:nth-child(1) {
                    border-top: 1px solid #DFDFDF;
                }

                li:nth-child(3) {
                    margin-bottom: 10px;
                }
            }
        }

        footer {
            button {
                width: 86%;
                height: 44px;
                border: none;
                border-radius: 22px;
                background: #FD8034;
                color: #fff;
                font-size: 14px;
                display: block;
                margin: 20px auto;
                outline: none;
                -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
            }
        }
    }
</style>