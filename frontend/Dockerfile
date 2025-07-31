# Stage 1 - Build React App
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2 - Serve with Nginx
FROM nginx:alpine

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy React build output (from Vite or CRA)
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Ensure non-root Nginx can write to cache dirs
RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx \
    && chmod -R 777 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html

USER 1001
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
