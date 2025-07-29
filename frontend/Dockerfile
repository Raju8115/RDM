# Stage: Serve with Nginx
FROM nginx:alpine

# Remove default nginx HTML files
RUN rm -rf /usr/share/nginx/html/*

# Copy your built files
COPY dist /usr/share/nginx/html

# Fix permissions for OpenShift (important)
RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx \
    && chmod -R 777 /var/cache/nginx /var/run /var/log/nginx \
    && chown -R 1001:0 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html

# Use non-root user for OpenShift security
USER 1001

# Expose port 8080 (not 5173 — that’s only for local Vite dev)
EXPOSE 8080

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
