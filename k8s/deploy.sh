#!/bin/bash
# ============================================================
# MyLinkedin K8s Deploy Script
# Usage: bash k8s/deploy.sh [--dry-run]
# ============================================================
set -e

DRY_RUN=""
if [ "$1" == "--dry-run" ]; then
  DRY_RUN="--dry-run=client"
  echo "🔍 DRY RUN MODE"
fi

echo "🚀 Deploying MyLinkedin to Kubernetes..."
echo "==========================================="

# 1. Namespace
echo "📁 Creating namespace..."
kubectl apply -f k8s/namespace.yaml $DRY_RUN

# 2. Secrets & ConfigMaps
echo "🔐 Applying secrets & configmaps..."
kubectl apply -f k8s/secrets/app-secrets.yaml $DRY_RUN
kubectl apply -f k8s/configmaps/app-config.yaml $DRY_RUN

# 3. Infrastructure
echo "🏗️  Deploying infrastructure (postgres, redis, kafka, neo4j)..."
kubectl apply -f k8s/infrastructure/postgres-init-configmap.yaml $DRY_RUN
kubectl apply -f k8s/infrastructure/postgres.yaml $DRY_RUN
kubectl apply -f k8s/infrastructure/redis.yaml $DRY_RUN
kubectl apply -f k8s/infrastructure/neo4j.yaml $DRY_RUN
kubectl apply -f k8s/infrastructure/kafka.yaml $DRY_RUN

# 4. Wait for infrastructure to be ready
if [ -z "$DRY_RUN" ]; then
  echo "⏳ Waiting for postgres to be ready..."
  kubectl rollout status statefulset/postgres -n mylinkedin --timeout=120s
  echo "⏳ Waiting for redis to be ready..."
  kubectl rollout status statefulset/redis -n mylinkedin --timeout=60s
  echo "⏳ Waiting for kafka to be ready..."
  kubectl rollout status statefulset/kafka -n mylinkedin --timeout=120s
  echo "⏳ Waiting for neo4j to be ready..."
  kubectl rollout status statefulset/neo4j -n mylinkedin --timeout=120s
fi

# 5. Discovery Server first
echo "🔭 Deploying discovery server (Eureka)..."
kubectl apply -f k8s/services/discovery-server.yaml $DRY_RUN
if [ -z "$DRY_RUN" ]; then
  kubectl rollout status deployment/discovery-server -n mylinkedin --timeout=120s
fi

# 6. App Services
echo "⚙️  Deploying app services..."
kubectl apply -f k8s/services/api-gateway.yaml $DRY_RUN
kubectl apply -f k8s/services/user-service.yaml $DRY_RUN
kubectl apply -f k8s/services/post-service.yaml $DRY_RUN
kubectl apply -f k8s/services/connection-service.yaml $DRY_RUN
kubectl apply -f k8s/services/notification-service.yaml $DRY_RUN
kubectl apply -f k8s/services/uploader-service.yaml $DRY_RUN

# 7. Frontend
echo "🌐 Deploying frontend..."
kubectl apply -f k8s/frontend/frontend.yaml $DRY_RUN

# 8. Ingress
echo "🔀 Deploying ingress..."
kubectl apply -f k8s/ingress/ingress.yaml $DRY_RUN

echo ""
echo "✅ All resources applied!"
echo ""
echo "📊 Status:"
kubectl get all -n mylinkedin
echo ""
echo "🌍 Get Load Balancer URL:"
echo "kubectl get ingress -n mylinkedin"
