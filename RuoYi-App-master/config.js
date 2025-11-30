// 应用全局配置
const DEFAULT_DEV_BASE_URL = 'http://localhost:8080'
const DEFAULT_PROD_BASE_URL = '/prod-api'

function normalizeBaseUrl(url) {
  if (!url || typeof url !== 'string') {
    return ''
  }
  // 去掉多余的斜杠，保留一个结尾斜杠，便于后续字符串拼接
  return url.trim().replace(/\/+$/, '') || '/'
}

const envBaseUrl =
  process.env.UNI_APP_BASE_API ||
  process.env.VITE_APP_BASE_API ||
  process.env.VITE_API_BASE ||
  process.env.BASE_API ||
  ''

const resolvedBaseUrl = normalizeBaseUrl(
  envBaseUrl ||
    (process.env.NODE_ENV === 'development'
      ? DEFAULT_DEV_BASE_URL
      : DEFAULT_PROD_BASE_URL)
)

module.exports = {
  baseUrl: resolvedBaseUrl,
  // 应用信息
  appInfo: {
    // 应用名称
    name: "梦幻校园",
    // 应用版本
    version: "1.0.0",
    // 应用logo
    logo: "/static/logo.png",
    // 官方网站
    site_url: "http://www.beiyangtt.com",
    // 政策协议
    agreements: [{
        title: "隐私政策",
		url: "/static/privacy.html"
      },
      {
        title: "用户服务协议",
		url: "/static/privacy.html"
      }
    ]
  }
}
