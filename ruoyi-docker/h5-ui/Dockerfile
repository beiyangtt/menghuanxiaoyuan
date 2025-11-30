FROM nginx:alpine
# 创建templates目录（envsubst需要从此目录读取模板）
RUN mkdir -p /etc/nginx/templates
# 复制静态文件
COPY dist/ /usr/share/nginx/html
# 复制nginx配置模板到templates目录
COPY nginx.conf /etc/nginx/templates/default.conf.template
# 复制启动脚本
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 81
ENTRYPOINT ["/entrypoint.sh"]