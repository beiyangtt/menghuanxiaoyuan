<script>
  import config from './config'
  import { getToken } from '@/utils/auth'

  export default {
    onLaunch: function() {
      this.initApp()
    },
    methods: {
      // 初始化应用
      initApp() {
        // 初始化应用配置
        this.initConfig()
        // 根据登录状态跳转到对应页面
        this.handleStartupRoute()
      },
      initConfig() {
        this.globalData.config = config
      },
      handleStartupRoute() {
        const token = getToken()
        if (token) {
          this.$tab.switchTab('/pages/index')
        } else {
          this.$tab.reLaunch('/pages/login')
        }
      }
    }
  }
</script>

<style lang="scss">
  @import '@/static/scss/index.scss'
</style>
