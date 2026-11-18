# Stage 1 — Build React app
FROM node:20-alpine AS build
WORKDIR /app

# Cache node_modules layer
COPY package*.json ./
RUN npm ci --silent

# Build with production API gateway URL
COPY . .
RUN npm run build

# Stage 2 — Serve with Nginx
FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx page
RUN rm -rf ./*

# Copy built React app
COPY --from=build /app/dist .

# Copy nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
