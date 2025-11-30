<template>
  <view class="normal-login-container">
    <view class="logo-content align-center justify-center flex">
      <image style="width: 100rpx;height: 100rpx;" :src="globalConfig.appInfo.logo" mode="widthFix">
      </image>
      <text class="title">梦幻校园登录</text>
    </view>
    <view class="login-form-content">
      <view class="input-item flex align-center">
        <view class="iconfont icon-user icon"></view>
        <input v-model="loginForm.username" class="input" type="text" placeholder="请输入账号" maxlength="30" />
      </view>
      <view class="input-item flex align-center">
        <view class="iconfont icon-password icon"></view>
        <input v-model="loginForm.password" :type="showPassword ? 'text' : 'password'" class="input" placeholder="请输入密码" maxlength="20" />
        <view class="password-eye" @click="togglePassword">
          <uni-icons :type="showPassword ? 'eye-slash' : 'eye'" size="20" color="#999"></uni-icons>
        </view>
      </view>
      <view class="input-item flex align-center" style="width: 60%;margin: 0px;" v-if="captchaEnabled">
        <view class="iconfont icon-code icon"></view>
        <input v-model="loginForm.code" type="number" class="input" placeholder="请输入验证码" maxlength="4" />
        <view class="login-code"> 
          <image :src="codeUrl" @click="getCode" class="login-code-img"></image>
        </view>
      </view>
      <view class="action-btn">
        <view class="remember-password-wrapper">
          <view class="remember-password" @click="toggleRememberPassword">
            <view class="checkbox" :class="{ 'checked': rememberPassword }">
              <uni-icons v-if="rememberPassword" type="checkmarkempty" size="16" color="#409EFF"></uni-icons>
            </view>
            <text class="remember-text">记住密码</text>
          </view>
        </view>
        <button @click="handleLogin" class="login-btn cu-btn block bg-blue lg round">登录</button>
      </view>
      <view class="reg text-center" v-if="register">
        <text class="text-grey1">没有账号？</text>
        <text @click="handleUserRegister" class="text-blue">立即注册</text>
      </view>
      <view class="xieyi text-center">
        <text class="text-grey1">登录即代表同意</text>
        <text @click="handleUserAgrement" class="text-blue">《用户协议》</text>
        <text @click="handlePrivacy" class="text-blue">《隐私协议》</text>
      </view>
    </view>
     
  </view>
</template>

<script>
  import { getCodeImg } from '@/api/login'
  import { getToken } from '@/utils/auth'

  export default {
    data() {
      return {
        codeUrl: "",
        captchaEnabled: true,
        // 用户注册开关
        register: false,
        globalConfig: getApp().globalData.config,
        showPassword: false, // 控制密码显示/隐藏
        rememberPassword: false, // 记住密码开关
        loginForm: {
          username: "",
          password: "",
          code: "",
          uuid: ""
        }
      }
    },
    created() {
      this.getCode()
    },
    onLoad() {
      //#ifdef H5
      if (getToken()) {
        this.$tab.reLaunch('/pages/index')
      }
      //#endif
      // 加载记住的密码
      this.loadRememberedPassword()
    },
    methods: {
      // 切换密码显示/隐藏
      togglePassword() {
        this.showPassword = !this.showPassword
      },
      // 切换记住密码
      toggleRememberPassword() {
        this.rememberPassword = !this.rememberPassword
        // 如果取消记住密码，清除保存的账号密码
        if (!this.rememberPassword) {
          uni.removeStorageSync('remembered_username')
          uni.removeStorageSync('remembered_password')
        }
      },
      // 加载记住的密码
      loadRememberedPassword() {
        const rememberedUsername = uni.getStorageSync('remembered_username')
        const rememberedPassword = uni.getStorageSync('remembered_password')
        const rememberFlag = uni.getStorageSync('remember_password_flag')
        
        if (rememberFlag === 'true' && rememberedUsername && rememberedPassword) {
          this.rememberPassword = true
          this.loginForm.username = rememberedUsername
          this.loginForm.password = rememberedPassword
        }
      },
      // 保存记住的密码
      saveRememberedPassword() {
        if (this.rememberPassword) {
          uni.setStorageSync('remembered_username', this.loginForm.username)
          uni.setStorageSync('remembered_password', this.loginForm.password)
          uni.setStorageSync('remember_password_flag', 'true')
        } else {
          uni.removeStorageSync('remembered_username')
          uni.removeStorageSync('remembered_password')
          uni.removeStorageSync('remember_password_flag')
        }
      },
      // 用户注册
      handleUserRegister() {
        this.$tab.redirectTo(`/pages/register`)
      },
      // 隐私协议
      handlePrivacy() {
        let site = this.globalConfig.appInfo.agreements[0]
        this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
      },
      // 用户协议
      handleUserAgrement() {
        let site = this.globalConfig.appInfo.agreements[1]
        this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
      },
      // 获取图形验证码
      getCode() {
        getCodeImg().then(res => {
          this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
          if (this.captchaEnabled) {
            this.codeUrl = 'data:image/gif;base64,' + res.img
            this.loginForm.uuid = res.uuid
          }
        })
      },
      // 登录方法
      async handleLogin() {
        if (this.loginForm.username === "") {
          this.$modal.msgError("请输入账号")
        } else if (this.loginForm.password === "") {
          this.$modal.msgError("请输入密码")
        } else if (this.loginForm.code === "" && this.captchaEnabled) {
          this.$modal.msgError("请输入验证码")
        } else {
          this.$modal.loading("登录中，请耐心等待...")
          this.pwdLogin()
        }
      },
      // 密码登录
      async pwdLogin() {
        this.$store.dispatch('Login', this.loginForm).then(() => {
          this.$modal.closeLoading()
          // 登录成功后保存记住的密码
          this.saveRememberedPassword()
          this.loginSuccess()
        }).catch(() => {
          if (this.captchaEnabled) {
            this.getCode()
          }
        })
      },
      // 登录成功后，处理函数
      loginSuccess(result) {
        // 设置用户信息
        this.$store.dispatch('GetInfo').then(res => {
          this.$tab.reLaunch('/pages/index')
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  page {
    background-color: #ffffff;
  }

  .normal-login-container {
    width: 100%;

    .logo-content {
      width: 100%;
      font-size: 21px;
      text-align: center;
      padding-top: 15%;

      image {
        border-radius: 4px;
      }

      .title {
        margin-left: 10px;
      }
    }

    .login-form-content {
      text-align: center;
      margin: 20px auto;
      margin-top: 15%;
      width: 80%;

      .input-item {
        margin: 20px auto;
        background-color: #f5f6f7;
        height: 45px;
        border-radius: 20px;

        .icon {
          font-size: 38rpx;
          margin-left: 10px;
          color: #999;
        }

        .input {
          flex: 1;
          font-size: 14px;
          line-height: 20px;
          text-align: left;
          padding-left: 15px;
          border: none;
          outline: none;
          background-color: transparent;
        }

        .password-eye {
          padding: 0 15px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

      }

      .action-btn {
        position: relative;
        margin-top: 40px;
      }

      .remember-password-wrapper {
        position: absolute;
        top: -30px;
        right: 0;
        z-index: 10;
      }

      .remember-password {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        cursor: pointer;
      }

      .checkbox {
        width: 32rpx;
        height: 32rpx;
        border: 2rpx solid #ddd;
        border-radius: 4rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 8rpx;
        background-color: #fff;
        transition: all 0.3s ease;
      }

      .checkbox.checked {
        border-color: #409EFF;
        background-color: #fff;
      }

      .remember-text {
        font-size: 24rpx;
        color: #666;
      }

      .login-btn {
        height: 45px;
      }
      
      .reg {
        margin-top: 15px;
      }
      
      .xieyi {
        color: #333;
        margin-top: 20px;
      }
      
      .login-code {
        height: 38px;
        float: right;
      
        .login-code-img {
          height: 38px;
          position: absolute;
          margin-left: 10px;
          width: 200rpx;
        }
      }
    }
  }

</style>
