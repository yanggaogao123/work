<template>
    <div class="main">
        <div class="login-box">
            <div class="bg"></div>

            <div class="left-bg">
                <img class="img-bg" src="@assets/login/bg_top1.png" alt="" />
                <img class="img-logo" src="@assets/login/logo_bus.png" alt="" />
            </div>

            <div class="tit">广州智能公交管理系统</div>
            <a-form :form="form" class="user-layout-login" ref="formLogin" id="formLogin" @submit="handleSubmit">
                <!-- <a-tabs
        :activeKey="customActiveKey"
        :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
        @change="handleTabClick"> -->
                <!-- <a-tab-pane key="tab1" tab="账号密码登陆"> -->
                <!-- <a-form-item>
                    <div class="tit">广州智能公交管理系统</div>
                </a-form-item> -->
                <a-form-item>
                    <a-input
                        size="large"
                        v-decorator="['username', validatorRules.username, { validator: this.handleUsernameOrEmail }]"
                        type="text"
                        placeholder="请输入帐户名"
                        style="height:54px"
                    >
                        <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }" />
                    </a-input>
                </a-form-item>

                <a-form-item>
                    <a-input
                        v-decorator="['password', validatorRules.password]"
                        size="large"
                        type="password"
                        autocomplete="false"
                        placeholder="请输入密码"
                        style="height:54px"
                    >
                        <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }" />
                    </a-input>
                </a-form-item>

                <a-row :gutter="0">
                    <a-col :span="16">
                        <a-form-item>
                            <a-input
                                v-decorator="['inputCode', validatorRules.inputCode]"
                                size="large"
                                type="text"
                                @change="inputCodeChange"
                                placeholder="请输入验证码"
                                style="height:54px"
                            >
                                <a-icon slot="prefix" type="safety" :style="{ color: 'rgba(0,0,0,.25)' }" />
                            </a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :span="8" style="text-align: right">
                        <img
                            v-if="requestCodeSuccess"
                            style="margin-top: 10px;width:80%;"
                            :src="randCodeImage"
                            @click="handleChangeCheckCode"
                        />
                        <img
                            v-else
                            style="margin-top: 2px;"
                            src="../../assets/checkcode.png"
                            @click="handleChangeCheckCode"
                        />
                    </a-col>
                </a-row>
                <!-- </a-tab-pane> -->
                <!-- </a-tabs> -->

                <a-form-item>
                    <div
                        size="large"
                        type="primary"
                        style="float: right;border: none;background:#fff;cursor: pointer;color:#1890FF;"
                        @click.stop.prevent="showForgetPwd"
                    >
                        忘记密码
                    </div>
                    <button
                        size="large"
                        type="primary"
                        html-Type="submit"
                        class="login-button"
                        :loading="loginBtn"
                        @click.stop.prevent="handleSubmit"
                        style="border: none;background:#1890FF;cursor: pointer;color:#fff;"
                        :disabled="loginBtn"
                    >
                        登录
                    </button>
                </a-form-item>
            </a-form>

            <div class="footer">
                <div class="copyright">
                    Copyright &copy; 2023 <a href="javascript:void(0);" target="">广州交信投科技股份有限公司</a> 出品
                </div>
                <div class="links">
                    <a href="javascript:void(0);" target="">联系我们</a>
                    <a-divider type="vertical" />
                    <a href="javascript:void(0);" target="">帮助</a>
                    <a-divider type="vertical" />
                    <a href="javascript:void(0);" target="">网站地图</a>
                </div>
            </div>
        </div>

        <two-step-captcha
            v-if="requiredTwoStepCaptcha"
            :visible="stepCaptchaVisible"
            @success="stepCaptchaSuccess"
            @cancel="stepCaptchaCancel"
        ></two-step-captcha>

        <a-modal title="登录部门选择" :width="450" :visible="departVisible" :closable="false" :maskClosable="false">
            <template slot="footer">
                <a-button type="primary" @click="departOk">确认</a-button>
            </template>

            <a-form>
                <a-form-item
                    :labelCol="{ span: 4 }"
                    :wrapperCol="{ span: 20 }"
                    style="margin-bottom:10px"
                    :validate-status="validate_status"
                >
                    <a-tooltip placement="topLeft">
                        <template slot="title">
                            <span>您隶属于多部门，请选择登录部门</span>
                        </template>
                        <a-avatar style="backgroundColor:#87d068" icon="gold" />
                    </a-tooltip>
                    <a-select
                        @change="departChange"
                        :class="{ 'valid-error': validate_status == 'error' }"
                        placeholder="请选择登录部门"
                        style="margin-left:10px;width: 80%"
                    >
                        <a-icon slot="suffixIcon" type="gold" />
                        <a-select-option v-for="d in departList" :key="d.id" :value="d.orgCode">
                            {{ d.orgName }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
            </a-form>
        </a-modal>
        <a-modal title="忘记密码" width="450px" :visible="forgetPwdFlag" :maskClosable="false" :closable="false">
            <a-form :form="forgetForm">
                <a-form-item>
                    <a-input
                        style="width: 400px"
                        type="text"
                        placeholder="请输入帐户名"
                        v-decorator="['forgetUser', validatorRules.forgetUser]"
                    >
                        <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }" />
                    </a-input>
                </a-form-item>
                <a-form-item>
                    <a-input placeholder="请输入邮箱地址" v-decorator="['checkEmail', validatorRules.email]" />
                </a-form-item>
            </a-form>
            <template slot="footer">
                <a-button type="primary" @click="confirmForget">确认忘记密码</a-button>
                <a-button type="primary" @click="cancelForget">取消</a-button>
            </template>
        </a-modal>

        <a-modal title="登录平台选择" :width="450" :visible="platformVisible" :closable="false" :maskClosable="false">
            <template slot="footer">
                <a-button type="primary" @click="platformOk">确认</a-button>
            </template>

            <a-form>
                <a-form-item
                    :labelCol="{ span: 4 }"
                    :wrapperCol="{ span: 20 }"
                    style="margin-bottom:10px"
                    :validate-status="platform_validate"
                >
                    <a-tooltip placement="topLeft">
                        <template slot="title">
                            <span>请选择登录平台</span>
                        </template>
                        <a-avatar style="backgroundColor:#87d068" icon="gold" />
                    </a-tooltip>
                    <a-select
                        @change="platformChange"
                        :class="{ 'valid-error': platform_validate == 'error' }"
                        placeholder="请选择登录平台"
                        style="margin-left:10px;width: 80%"
                    >
                        <a-icon slot="suffixIcon" type="gold" />
                        <a-select-option v-for="p in platformList" :key="p.id" :value="p.platformCode">
                            {{ p.platformName }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
            </a-form>
        </a-modal>
        <user-password ref="userPassword" @close="passwordModalClose"></user-password>
    </div>
</template>

<script>
//import md5 from "md5"
import api from '@/api';
import pick from 'lodash.pick';
import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha';
import { mapActions } from 'vuex';
import { timeFix } from '@/utils/util';
import Vue from 'vue';
import { ACCESS_TOKEN, ENCRYPTED_STRING, USER_CUR_PLATFORMS } from '@/store/mutation-types';
import { putAction, postAction, getAction } from '@/api/manage';
import { encryption, getEncryptedString } from '@/utils/encryption/aesEncrypt';
import store from '@/store/';
import { USER_INFO } from '@/store/mutation-types';
import UserPassword from '../../components/tools/UserPassword';

export default {
    components: {
        TwoStepCaptcha,
        UserPassword
    },
    data() {
        return {
            forgetPwdFlag: false,
            forgetUser: '',
            checkEmail: '',
            customActiveKey: 'tab1',
            loginBtn: false,
            // login type: 0 email, 1 username, 2 telephone
            loginType: 0,
            requiredTwoStepCaptcha: false,
            stepCaptchaVisible: false,
            form: this.$form.createForm(this),
            forgetForm: this.$form.createForm(this),
            encryptedString: {
                key: '',
                iv: ''
            },
            state: {
                time: 60,
                smsSendBtn: false
            },
            validatorRules: {
                username: {
                    rules: [{ required: true, message: '请输入用户名!' }, { validator: this.handleUsernameOrEmail }]
                },
                password: { rules: [{ required: true, message: '请输入密码!', validator: 'click' }] },
                mobile: { rules: [{ validator: this.validateMobile }] },
                captcha: { rule: [{ required: true, message: '请输入验证码!' }] },
                inputCode: { rules: [{ required: true, message: '请输入验证码!' }] },
                email: {
                    rules: [
                        { required: true, message: '请输入邮箱!' },
                        {
                            validator: this.validateEmail
                        }
                    ]
                },
                forgetUser: {
                    rules: [{ required: true, message: '请输入用户账号名!' }]
                }
            },
            verifiedCode: '',
            inputCodeContent: '',
            inputCodeNull: true,

            departList: [],
            departVisible: false,
            departSelected: '',
            currentUsername: '',
            validate_status: '',
            currdatetime: '',
            randCodeImage: '',
            requestCodeSuccess: false,
            platformVisible: false,
            platformList: [],
            platform_validate: '',
            platformSelected: '',
            loginData: undefined,
            url: {
                forgetPwd: 'sys/forgetPwd',
                checkResetPassword: '/sys/user/checkResetPassword'
            }
        };
    },
    created() {
      let lsLength = window.localStorage.length
      for(let i= 0; i < lsLength; i++){
        let key = window.localStorage.key(i)
        console.log('key',i,key)
        if(key && key.startsWith('custom_')){
          window.localStorage.removeItem(key)
        }
      }
        this.currdatetime = new Date().getTime();
        this.getRouterData();
        this.handleChangeCheckCode();
        // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
        this.getEncrypte();
        // update-end- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
        document.querySelector('#app').style = 'overflow:auto;';
    },
    mounted() {
        console.log(document.querySelector('.user-layout-login .ant-input-lg'));
        let lg = document.querySelectorAll('.user-layout-login .ant-input-lg');
        lg.forEach(ele => {
            ele.style = 'height: 54px !important;';
        });
    },
    methods: {
        ...mapActions(['Login', 'Logout', 'PhoneLogin', 'ThirdLogin']),
        //第三方登录
        onThirdLogin(source) {
            let url = window._CONFIG['domianURL'] + `/thirdLogin/render/${source}`;
            window.open(
                url,
                `login ${source}`,
                'height=500, width=500, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no'
            );
            let that = this;
            let receiveMessage = function(event) {
                var origin = event.origin;
                console.log('origin', origin);

                let token = event.data;
                console.log('event.data', token);
                that.ThirdLogin(token).then(res => {
                    if (res.success) {
                        that.loginSuccess();
                    } else {
                        that.requestFailed(res);
                    }
                });
            };
            window.addEventListener('message', receiveMessage, false);
        },
        showForgetPwd() {
            let loginUser = this.form.getFieldsValue(['username']); // 获取某一个值
            this.forgetPwdFlag = true;
            this.$nextTick(() => {
                this.forgetForm.setFieldsValue({ forgetUser: loginUser.username });
            });
        },
        confirmForget() {
            this.forgetForm.validateFields((err, values) => {
                console.log('err', err);
                console.log('values', values);
                if (!err) {
                    let param = {
                        userName: values.forgetUser,
                        email: values.checkEmail
                    };
                    let that = this;
                    getAction(this.url.forgetPwd, param).then(res => {
                        if (res.success && res.code && res.code === 200) {
                            this.$confirm({
                                title: '重置密码',
                                content: h => '重置密码链接已发送到账户邮箱：' + res.message,
                                onOk() {},
                                onCancel() {}
                            });
                        } else {
                            let msg = res.message && res.message.length ? res.message : ',' + res.message;

                            this.$confirm({
                                title: '重置密码',
                                content: h => '重置密码失败：' + msg,
                                onOk() {},
                                onCancel() {}
                            });
                        }
                    });
                }
            });
        },
        cancelForget() {
            this.forgetPwdFlag = false;
            this.forgetUser = '';
            this.checkEmail = '';
            this.forgetForm = this.$form.createForm(this);
        },
        // handler
        handleUsernameOrEmail(rule, value, callback) {
            const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
            if (regex.test(value)) {
                this.loginType = 0;
            } else {
                this.loginType = 1;
            }
            callback();
        },
        validateEmail(rule, value, callback) {
            if (!value) {
                callback();
            } else {
                if (
                    new RegExp(
                        /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                    ).test(value)
                ) {
                    callback();
                } else {
                    callback('请输入正确格式的邮箱!');
                }
            }
        },
        handleTabClick(key) {
            this.customActiveKey = key;
            // this.form.resetFields()
        },
        handleSubmit() {
            let that = this;
            let loginParams = {};
            that.loginBtn = true;
            // 使用账户密码登陆
            if (that.customActiveKey === 'tab1') {
                if (
                    !that.encryptedString ||
                    !that.encryptedString.key ||
                    that.encryptedString.key === '' ||
                    !that.encryptedString.iv ||
                    that.encryptedString.iv === ''
                ) {
                    this.$notification['error']({
                        message: '登录失败',
                        description: '密码加密失败，即将刷新页面',
                        duration: 5
                    });
                    location.reload();
                    return;
                }
                that.form.validateFields(
                    ['username', 'password', 'inputCode', 'rememberMe'],
                    { force: true },
                    (err, values) => {
                        if (!err) {
                            loginParams.username = values.username;
                            // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
                            //loginParams.password = md5(values.password)
                            loginParams.password = encryption(
                                values.password,
                                that.encryptedString.key,
                                that.encryptedString.iv
                            );
                            // loginParams.password = values.password
                            loginParams.remember_me = values.rememberMe;
                            // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
                            loginParams.captcha = that.inputCodeContent;
                            loginParams.checkKey = that.currdatetime;
                            console.log('登录参数', loginParams);
                            that.Login(loginParams)
                                .then(res => {
                                    this.loginData = res;
                                    this.checkResetPassword(res, loginParams.username);
                                })
                                .catch(err => {
                                    that.requestFailed(err);
                                });
                        } else {
                            that.loginBtn = false;
                        }
                    }
                );
                // 使用手机号登陆
            } else if (that.customActiveKey === 'tab2') {
                that.form.validateFields(['mobile', 'captcha', 'rememberMe'], { force: true }, (err, values) => {
                    if (!err) {
                        loginParams.mobile = values.mobile;
                        loginParams.captcha = values.captcha;
                        loginParams.remember_me = values.rememberMe;
                        that.PhoneLogin(loginParams)
                            .then(res => {
                                console.log(res.result);
                                this.loginData = res;
                                this.checkResetPassword(res, loginParams.username);
                            })
                            .catch(err => {
                                that.requestFailed(err);
                            });
                    }
                });
            } else if (that.customActiveKey === 'tab3') {
            }
        },
        checkResetPassword(res, userName) {
            getAction(this.url.checkResetPassword).then(res2 => {
                if (res2.success) {
                    if (res2.result) {
                        if (res2.result.checkFlag === true) {
                            //缓存信息
                            console.log('登录结果', res);
                            Vue.ls.set('departs', res.result.departs, 7 * 24 * 60 * 60 * 1000);
                            store.commit('SET_INFO', res.result.departs);
                            Vue.ls.set('platforms', res.result.platforms, 7 * 24 * 60 * 60 * 1000);
                            store.commit('SET_INFO', res.result.platforms);
                            this.platfoConfirm(res);
                        } else {
                            this.$message.warning(res2.result.msg);
                            this.$refs.userPassword.show(userName);
                        }
                    }
                }
            });
        },
        passwordModalClose() {
            this.platfoConfirm(this.loginData);
        },
        getCaptcha(e) {
            e.preventDefault();
            let that = this;
            this.form.validateFields(['mobile'], { force: true }, (err, values) => {
                if (!values.mobile) {
                    that.cmsFailed('请输入手机号');
                } else if (!err) {
                    this.state.smsSendBtn = true;
                    let interval = window.setInterval(() => {
                        if (that.state.time-- <= 0) {
                            that.state.time = 60;
                            that.state.smsSendBtn = false;
                            window.clearInterval(interval);
                        }
                    }, 1000);

                    const hide = this.$message.loading('验证码发送中..', 0);
                    let smsParams = {};
                    smsParams.mobile = values.mobile;
                    smsParams.smsmode = '0';
                    postAction('/sys/sms', smsParams)
                        .then(res => {
                            if (!res.success) {
                                setTimeout(hide, 0);
                                this.cmsFailed(res.message);
                            }
                            console.log(res);
                            setTimeout(hide, 500);
                        })
                        .catch(err => {
                            setTimeout(hide, 1);
                            clearInterval(interval);
                            that.state.time = 60;
                            that.state.smsSendBtn = false;
                            this.requestFailed(err);
                        });
                }
            });
        },
        stepCaptchaSuccess() {
            this.loginSuccess();
        },
        stepCaptchaCancel() {
            this.Logout().then(() => {
                this.loginBtn = false;
                this.stepCaptchaVisible = false;
            });
        },
        handleChangeCheckCode() {
            this.$nextTick(() => {
                this.form.setFieldsValue({ inputCode: '' });
            });
            this.currdatetime = new Date().getTime();
            getAction(`/sys/randomImage/${this.currdatetime}`)
                .then(res => {
                    if (res.success) {
                        this.randCodeImage = res.result;
                        this.requestCodeSuccess = true;
                    } else {
                        this.$message.error(res.message);
                        this.requestCodeSuccess = false;
                    }
                })
                .catch(() => {
                    this.requestCodeSuccess = false;
                });
        },
        loginSuccess() {
            // update-begin- author:sunjianlei --- date:20190812 --- for: 登录成功后不解除禁用按钮，防止多次点击
            // this.loginBtn = false
            // update-end- author:sunjianlei --- date:20190812 --- for: 登录成功后不解除禁用按钮，防止多次点击
            this.$router.push({ path: '/dashboard/analysis' });
            this.$notification.success({
                message: '欢迎',
                description: `${timeFix()}，欢迎回来`
            });
        },
        cmsFailed(err) {
            this.$notification['error']({
                message: '登录失败',
                description: err,
                duration: 4
            });
        },
        requestFailed(err) {
            this.$notification['error']({
                message: '登录失败',
                description: ((err.response || {}).data || {}).message || err.message || '请求出现错误，请稍后再试',
                duration: 4
            });
            this.loginBtn = false;
            this.handleChangeCheckCode();
        },
        validateMobile(rule, value, callback) {
            if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)) {
                callback();
            } else {
                callback('您的手机号码格式不正确!');
            }
        },
        validateInputCode(rule, value, callback) {
            if (!value || this.verifiedCode == this.inputCodeContent) {
                callback();
            } else {
                callback('您输入的验证码不正确!');
            }
        },
        generateCode(value) {
            this.verifiedCode = value.toLowerCase();
        },
        inputCodeChange(e) {
            this.inputCodeContent = e.target.value;
        },
        departConfirm(res) {
            if (res.success) {
                let multi_depart = res.result.multi_depart;
                //0:无部门 1:一个部门 2:多个部门
                if (multi_depart == 0) {
                    this.loginSuccess();
                    this.$notification.warn({
                        message: '提示',
                        description: `您尚未归属部门,请确认账号信息`,
                        duration: 3
                    });
                } else if (multi_depart == 2) {
                    this.departVisible = true;
                    this.currentUsername = this.form.getFieldValue('username');
                    this.departList = res.result.departs;
                } else {
                    this.loginSuccess();
                }
            } else {
                this.requestFailed(res);
                this.Logout();
            }
        },
        departOk() {
            if (!this.departSelected) {
                this.validate_status = 'error';
                return false;
            }
            let obj = {
                orgCode: this.departSelected,
                username: this.form.getFieldValue('username')
            };
            putAction('/sys/selectDepart', obj).then(res => {
                if (res.success) {
                    const userInfo = res.result.userInfo;
                    Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000);
                    store.commit('SET_INFO', userInfo);
                    //console.log("---切换组织机构---userInfo-------",store.getters.userInfo.orgCode);
                    this.departClear();
                    this.loginSuccess();
                } else {
                    this.requestFailed(res);
                    this.Logout().then(() => {
                        this.departClear();
                    });
                }
            });
        },
        departClear() {
            this.departList = [];
            this.departSelected = '';
            this.currentUsername = '';
            this.departVisible = false;
            this.validate_status = '';
        },
        departChange(value) {
            this.validate_status = 'success';
            this.departSelected = value;
        },
        platfoConfirm(res) {
            if (res.success) {
                let multi_platform = res.result.multi_platform;
                //0:无部门 1:1个平台 2:多个平台
                if (multi_platform == 2) {
                    this.platformVisible = true;
                    this.platformList = res.result.platforms;
                } else {
                    this.departConfirm(res);
                }
            }
        },
        platformOk() {
            let that = this;
            if (!this.platformSelected) {
                this.platform_validate = 'error';
                return false;
            } else {
                Vue.ls.set(USER_CUR_PLATFORMS, this.platformSelected, 7 * 24 * 60 * 60 * 1000);
                putAction('/sys/selectPlatform', {
                    platformCode: that.platformSelected,
                    username: that.form.getFieldValue('username')
                }).then(res => {
                    if (res.success) {
                        const userInfo = res.result.userInfo;
                        Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000);
                        store.commit('SET_INFO', userInfo);
                        that.departConfirm(that.loginData);
                    } else {
                        that.requestFailed(res);
                        that.Logout().then(() => {
                            that.departClear();
                        });
                    }
                });
            }
        },
        platformChange(value) {
            this.platform_validate = 'success';
            this.platformSelected = value;
        },
        getRouterData() {
            this.$nextTick(() => {
                if (this.$route.params.username) {
                    this.form.setFieldsValue({
                        username: this.$route.params.username
                    });
                }
            });
        },
        //获取密码加密规则
        getEncrypte() {
            var encryptedString = Vue.ls.get(ENCRYPTED_STRING);
            if (encryptedString == null) {
                getEncryptedString().then(data => {
                    this.encryptedString = data;
                });
            } else {
                this.encryptedString = encryptedString;
            }
        }
    }
};
</script>

<style lang="less" scoped>
/deep/html,
body,
#app {
    overflow: auto !important;
}
.login-box {
    width: 1269px;
    height: 714px;
    border-radius: 24px;
    // background: #fff;
    position: relative;
    box-shadow: 0px 0px 40px 0px rgba(30, 116, 216, 0.43);
    .bg {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        // z-index: -1;
        background: #fff;
        opacity: 0.9;
        border-radius: 24px;
    }
    .left-bg {
        position: absolute;
        width: 56%;
        height: 250px;
        top: 0;
        left: 0;
        .img-bg {
            position: absolute;
            width: 148%;
            right: 0;
            top: -100%;
        }
        .img-logo {
            position: absolute;
            width: 110%;
            top: -44%;
            left: -23%;
        }
    }
    .tit {
        position: absolute;
        top: 80px;
        right: 5%;
        font-size: 53px;
        height: 78px;
        line-height: 78px;
        font-weight: 500;
        margin-bottom: 10px;
        text-align: center;
        color: #333333;
    }
    .user-layout-login {
        width: 404px;
        position: absolute;
        top: 197px;
        right: 10%;

        label {
            font-size: 14px;
        }

        /deep/.ant-input-affix-wrapper {
            font-size: 20px !important;
            /deep/.ant-input {
                height: 54px !important;
            }
        }
        /deep/.ant-input-lg {
            height: 54px !important;
        }

        /deep/.ant-input-affix-wrapper .ant-input:not(:first-child) {
            padding-left: 40px !important;
            height: 54px !important;
        }

        .getCaptcha {
            display: block;
            width: 100%;
            height: 40px;
        }

        .forge-password {
            font-size: 14px;
        }

        button.login-button {
            padding: 0 15px;
            font-size: 16px;
            height: 54px;
            width: 100%;
        }

        .user-login-other {
            text-align: left;
            margin-top: 24px;
            line-height: 22px;

            .item-icon {
                font-size: 24px;
                color: rgba(0, 0, 0, 0.2);
                margin-left: 16px;
                vertical-align: middle;
                cursor: pointer;
                transition: color 0.3s;

                &:hover {
                    color: #1890ff;
                }
            }

            .register {
                float: right;
            }
        }
    }
    .footer {
        position: absolute;
        width: 100%;
        bottom: 0;
        padding: 0 16px;
        margin: 48px 0 0 0;
        text-align: center;

        .links {
            margin-bottom: 23px;
            font-size: 14px;
            a {
                // color: rgba(0, 0, 0, 0.45);
                color: #888888;
                transition: all 0.3s;
                // &:not(:last-child) {
                //     margin-right: 40px;
                // }
            }
            .ant-divider {
                margin: 0 30px;
            }
        }
        .copyright {
            // color: rgba(0, 0, 0, 0.45);
            color: #888888;
            font-size: 14px;
            margin-bottom: 11px;
            a {
                color: #888888;
            }
        }
    }
}
</style>
<style>
.valid-error .ant-select-selection__placeholder {
    color: #f5222d;
}
</style>
