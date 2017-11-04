#!/bin/bash

dcoker login
docker push bikeride/bikeride-backend:base
docker push bikeride/authentication-lagom-impl:0.0.1-SNAPSHOT
docker push bikeride/biker-lagom-impl:0.0.1-SNAPSHOT
docker push bikeride/track-lagom-impl:0.0.1-SNAPSHOT
docker push bikeride/ride-lagom-impl:0.0.1-SNAPSHOT

# Minikube
minikube start
#https://github.com/Kong/kong-dist-kubernetes/tree/master/minikube
kubectl create -f cassandra.yml
kubectl create -f kong_migration_cassandra.yml
kubectl delete -f kong_migration_cassandra.yml
kubectl create -f kong_cassandra.yml
# Bikeride microservices
kubectl create -f bikeride-namespace.yml
kubectl create -f authentication-deploy.yml
kubectl create -f authentication-service.yml
kubectl create -f biker-deploy.yml
kubectl create -f biker-service.yml
kubectl create -f track-deploy.yml
kubectl create -f track-service.yml
kubectl create -f ride-deploy.yml
kubectl create -f ride-service.yml

kubectl get all -n default
kubectl get all -n bikeride



export KONG_IP=`minikube ip`
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=authentication-api' --data 'hosts=authentication.api.bikeride.com' --data 'upstream_url=http://_authentication._tcp.bikerride-authentication-proxy.bikeride.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=biker-api' --data 'hosts=biker.api.bikeride.com' --data 'upstream_url=http://_biker._tcp.bikerride-biker-proxy.bikeride.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=track-api' --data 'hosts=track.api.bikeride.com' --data 'upstream_url=http://_track._tcp.bikerride-track-proxy.bikeride.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=ride-api' --data 'hosts=ride.api.bikeride.com' --data 'upstream_url=http://_ride._tcp.bikerride-ride-proxy.bikeride.svc.cluster.local'