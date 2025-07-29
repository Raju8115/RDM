# Build Stage
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built React files
COPY dist/ /usr/share/nginx/html/

# Copy custom nginx config (with port 8080)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Fix permissions for OpenShift random UID
RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx \
    && chmod -R g+rwX /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html \
    && chown -R 1001:0 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html

# Run as non-root (OpenShift will override UID)
USER 1001

# Expose non-root port
EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
