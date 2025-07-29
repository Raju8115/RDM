FROM nginx:alpine

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built React files
COPY dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Fix permissions for OpenShift
RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx \
    && chmod -R 777 /var/cache/nginx /var/run /var/log/nginx \
    && chown -R 1001:0 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html

# Run as OpenShift non-root user
USER 1001

# Expose non-root port
EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
