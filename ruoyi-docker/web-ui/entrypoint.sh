#!/bin/sh
set -e

# 设置默认值（如果环境变量未设置）
detect_backend_host() {
    if [ -n "$BACKEND_HOST" ]; then
        echo "$BACKEND_HOST"
        return
    fi
    if getent hosts ruoyi-backend >/dev/null 2>&1; then
        echo "ruoyi-backend"
    elif getent hosts backend >/dev/null 2>&1; then
        echo "backend"
    else
        echo "ruoyi-backend"
    fi
}

export BACKEND_HOST=$(detect_backend_host)
export BACKEND_PORT=${BACKEND_PORT:-8080}

# 从/etc/resolv.conf读取DNS服务器地址（Kubernetes Pod会自动配置）
# 如果读取失败，使用默认值
if [ -f /etc/resolv.conf ]; then
    DNS_SERVER=$(grep -m1 nameserver /etc/resolv.conf | awk '{print $2}')
    if [ -z "$DNS_SERVER" ]; then
        DNS_SERVER="127.0.0.11"  # Docker默认DNS
    fi
else
    DNS_SERVER="127.0.0.11"  # Docker默认DNS
fi
export DNS_SERVER

# 确保配置目录存在
mkdir -p /etc/nginx/conf.d

# 使用envsubst替换nginx.conf中的环境变量
echo "正在生成nginx配置..."
echo "BACKEND_HOST=${BACKEND_HOST}"
echo "BACKEND_PORT=${BACKEND_PORT}"
echo "DNS_SERVER=${DNS_SERVER}"

envsubst '${BACKEND_HOST} ${BACKEND_PORT} ${DNS_SERVER}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf

# 验证生成的配置文件
echo "生成的nginx配置："
cat /etc/nginx/conf.d/default.conf

# 注意：不执行nginx -t测试，因为使用变量时upstream在启动时无法解析
# nginx会在启动时自动验证配置，如果有语法错误会输出错误信息
echo "启动nginx（使用变量时upstream在运行时解析）..."

# 启动nginx
exec nginx -g 'daemon off;'

