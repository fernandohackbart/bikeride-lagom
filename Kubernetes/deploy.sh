#!/bin/bash
# Minikube
minikube start
#https://github.com/Kong/kong-dist-kubernetes/tree/master/minikube
kubectl create -f cassandra.yaml
kubectl create -f kong_migration_cassandra.yaml
kubectl delete -f kong_migration_cassandra.yaml
kubectl create -f kong_cassandra.yaml
# Bikeride microservices
kubectl create -f bikeride-namespace.yaml
kubectl create -f authentication-deploy.yaml
kubectl create -f authentication-service.yaml
kubectl create -f biker-deploy.yaml
kubectl create -f biker-service.yaml
kubectl create -f track-deploy.yaml
kubectl create -f track-service.yaml
kubectl create -f ride-deploy.yaml
kubectl create -f ride-service.yaml

export KONG_IP=`minikube ip`
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=authentication-api' --data 'hosts=authentication.api.bikeride.com' --data 'upstream_url=http://_authentication._tcp.bikerride-authentication-proxy.bikeride.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=biker-api' --data 'hosts=biker.api.bikeride.com' --data 'upstream_url=http://_biker._tcp.bikerride-biker-proxy.bikeride.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=track-api' --data 'hosts=track.api.bikeride.com' --data 'upstream_url=http://_track._tcp.bikerride-track-proxy.bikeride.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=ride-api' --data 'hosts=ride.api.bikeride.com' --data 'upstream_url=http://_ride._tcp.bikerride-ride-proxy.bikeride.svc.cluster.local'