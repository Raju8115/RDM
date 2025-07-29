# --------------------
# Stage 1: Build (optional, can skip if you build locally)
# --------------------
# Uncomment this stage if you want to build inside Docker
# FROM node:20 AS build
# WORKDIR /app
# COPY package*.json ./
# RUN npm install
# COPY . .
# RUN npm run build

# --------------------
# Stage 2: Serve with Nginx
# --------------------
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy Vite build output (dist folder) to nginx html folder
COPY dist /usr/share/nginx/html

# Optional: Custom Nginx config for SPA routing (React Router support)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose HTTP port
EXPOSE 8080

# Run Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
