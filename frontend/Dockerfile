 # Stage 1: Build app
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy build output
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Fix permissions for OpenShift (important!)
RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx \
    && chmod -R 777 /var/cache/nginx /var/run /var/log/nginx

# Change owner to OpenShift random user
RUN chown -R 1001:0 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html

# Expose port 8080
EXPOSE 8080

# Switch to non-root user for OpenShift
USER 1001

CMD ["nginx", "-g", "daemon off;"]
