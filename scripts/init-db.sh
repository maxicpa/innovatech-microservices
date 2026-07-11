#!/bin/bash
set -e
CONTAINER=$(docker ps -qf "name=innovatech_postgres" | head -n1)
if [ -z "$CONTAINER" ]; then
  CONTAINER=$(docker ps -qf "name=postgres" | head -n1)
fi

DATABASES=("innovatech" "innovatech_resources" "innovatech_analytics")

for db in "${DATABASES[@]}"; do
  EXISTS=$(docker exec "$CONTAINER" psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$db'")
  if [ "$EXISTS" = "1" ]; then
    echo "Base $db ya existe, se omite."
  else
    docker exec "$CONTAINER" psql -U postgres -c "CREATE DATABASE $db"
    echo "Base $db creada."
  fi
done