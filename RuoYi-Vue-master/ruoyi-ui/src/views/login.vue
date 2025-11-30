<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">{{title}}</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="login-code">
          <div v-if="codeLoading" class="code-loading">
            <i class="el-icon-loading"></i>
          </div>
          <img 
            v-else-if="codeUrl" 
            :src="codeUrl" 
            @click="getCode" 
            @error="handleCodeError"
            class="login-code-img"
            alt="验证码"
          />
          <div v-else class="code-error" @click="getCode">
            <i class="el-icon-refresh"></i>
            <span>点击刷新</span>
          </div>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%; border-radius: 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; height: 45px; font-size: 16px; color: #fff;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading" style="color: #fff;">登 录</span>
          <span v-else style="color: #fff;">登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2025 梦幻校园管理系统 All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      codeLoading: false,
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      this.codeLoading = true
      this.codeUrl = ""
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          // 预加载图片
          const img = new Image()
          img.onload = () => {
            this.codeUrl = "data:image/gif;base64," + res.img
            this.loginForm.uuid = res.uuid
            this.codeLoading = false
          }
          img.onerror = () => {
            this.codeLoading = false
            this.$message.error('验证码加载失败，请重试')
          }
          img.src = "data:image/gif;base64," + res.img
        } else {
          this.codeLoading = false
        }
      }).catch(() => {
        this.codeLoading = false
        this.$message.error('验证码获取失败，请重试')
      })
    },
    handleCodeError() {
      this.codeUrl = ""
      this.$message.warning('验证码图片加载失败，请点击刷新')
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #999;
  font-size: 28px;
  font-weight: bold;
}

.login-form {
  border-radius: 16px;
  background: #ffffff;
  width: 400px;
  padding: 40px 35px 30px 35px;
  z-index: 1;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  .el-input {
    height: 45px;
    input {
      height: 45px;
      border-radius: 8px;
      color: #666;
      &::placeholder {
        color: #999;
      }
    }
  }
  .input-icon {
    height: 45px;
    width: 14px;
    margin-left: 2px;
  }
  .el-checkbox {
    color: #666;
    .el-checkbox__label {
      color: #666;
    }
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  display: flex;
  align-items: center;
  justify-content: center;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
  .code-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 38px;
    i {
      font-size: 20px;
      color: #409eff;
      animation: rotating 1s linear infinite;
    }
  }
  .code-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 38px;
    cursor: pointer;
    color: #999;
    font-size: 12px;
    i {
      font-size: 16px;
      margin-bottom: 2px;
    }
    &:hover {
      color: #409eff;
    }
  }
}
@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.login-code-img {
  height: 38px;
}
</style>
