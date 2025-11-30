<template>
  <view class="normal-login-container">
    <view class="logo-content align-center justify-center flex">
      <image style="width: 100rpx;height: 100rpx;" :src="globalConfig.appInfo.logo" mode="widthFix">
      </image>
      <text class="title">梦幻校园注册</text>
    </view>
    <view class="login-form-content">
      <view class="input-item flex align-center">
        <view class="iconfont icon-user icon"></view>
        <input v-model="registerForm.username" class="input" type="text" placeholder="请输入账号" maxlength="30" />
      </view>
      <view class="input-item flex align-center">
        <view class="iconfont icon-password icon"></view>
        <input v-model="registerForm.password" type="password" class="input" placeholder="请输入密码" maxlength="20" />
      </view>
      <view class="input-item flex align-center">
        <view class="iconfont icon-password icon"></view>
        <input v-model="registerForm.confirmPassword" type="password" class="input" placeholder="请输入重复密码" maxlength="20" />
      </view>
      <view class="input-item flex align-center" style="width: 60%;margin: 0px;" v-if="captchaEnabled">
        <view class="iconfont icon-code icon"></view>
        <input v-model="registerForm.code" type="text" class="input" placeholder="请输入验证码" maxlength="6" />
        <view class="login-code"> 
          <image :src="codeUrl" @click="getCode" class="login-code-img"></image>
        </view>
      </view>
      <view class="action-btn">
        <button @click="handleRegister()" class="register-btn cu-btn block bg-blue lg round">注册</button>
      </view>
    </view>
    <view class="xieyi text-center">
      <text @click="handleUserLogin" class="text-blue">使用已有账号登录</text>
    </view>
  </view>
</template>

<script>
  import { getCodeImg, register } from '@/api/login'

  export default {
    data() {
      return {
        codeUrl: "",
        captchaEnabled: true,
        globalConfig: getApp().globalData.config,
        registerForm: {
          username: "",
          password: "",
          confirmPassword: "",
          code: "",
          uuid: ""
        }
      }
    },
    created() {
      this.getCode()
    },
    methods: {
      // 用户登录
      handleUserLogin() {
        this.$tab.navigateTo(`/pages/login`)
      },
      // 获取图形验证码
      getCode() {
        getCodeImg().then(res => {
          this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
          if (this.captchaEnabled) {
            this.codeUrl = 'data:image/gif;base64,' + res.img
            this.registerForm.uuid = res.uuid
            // 清空验证码输入框
            this.registerForm.code = ""
          }
        }).catch(() => {
          this.$modal.msgError("获取验证码失败，请重试")
        })
      },
      // 注册方法
      async handleRegister() {
        // 验证用户名
        if (this.registerForm.username === "") {
          this.$modal.msgError("请输入您的账号")
          return
        } else if (this.registerForm.username.length < 2 || this.registerForm.username.length > 20) {
          this.$modal.msgError("用户账号长度必须介于 2 和 20 之间")
          return
        }
        // 验证密码
        if (this.registerForm.password === "") {
          this.$modal.msgError("请输入您的密码")
          return
        } else if (this.registerForm.password.length < 5 || this.registerForm.password.length > 20) {
          this.$modal.msgError("用户密码长度必须介于 5 和 20 之间")
          return
        }
        // 验证确认密码
        if (this.registerForm.confirmPassword === "") {
          this.$modal.msgError("请再次输入您的密码")
          return
        } else if (this.registerForm.password !== this.registerForm.confirmPassword) {
          this.$modal.msgError("两次输入的密码不一致")
          return
        }
        // 验证验证码
        if (this.registerForm.code === "" && this.captchaEnabled) {
          this.$modal.msgError("请输入验证码")
          return
        }
        // 开始注册
        this.$modal.loading("注册中，请耐心等待...")
        this.register()
      },
      // 用户注册
      async register() {
        // 构建注册数据，只发送后端需要的字段
        const registerData = {
          username: this.registerForm.username,
          password: this.registerForm.password,
          code: this.registerForm.code,
          uuid: this.registerForm.uuid
        }
        
        register(registerData).then(res => {
          this.$modal.closeLoading()
          this.$modal.msgSuccess("注册成功！")
          uni.showModal({
          	title: "系统提示",
          	content: "恭喜你，您的账号 " + this.registerForm.username + " 注册成功！",
          	showCancel: false,
          	success: (res) => {
          		if (res.confirm) {
                uni.redirectTo({ url: `/pages/login` });
          		}
          	}
          })
        }).catch((error) => {
          this.$modal.closeLoading()
          // 注册失败后刷新验证码
          if (this.captchaEnabled) {
            this.getCode()
          }
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
          width: 100%;
          font-size: 14px;
          line-height: 20px;
          text-align: left;
          padding-left: 15px;
        }

      }

      .register-btn {
        margin-top: 40px;
        height: 45px;
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
