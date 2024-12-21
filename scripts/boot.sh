#!/bin/bash
PROJECT_DIR="/home/projects/training-management-backend/"

# export POSTGRES_USER=$(echo $POSTGRES_SECRETS | jq -r '.username')

cd $PROJECT_DIR &&

# bring up redis and rabbitmq services only if they are down
docker compose -f docker-compose.yml up redis rabbitmq  -d --remove-orphans

services=()

for service in "${services[@]}"; do
        docker compose -f docker-compose.yml up "$service" -d --remove-orphans
        echo "$service is up"
done



