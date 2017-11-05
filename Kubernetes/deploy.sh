#!/bin/bash

# Minikube

#virsh destroy minikube
#virsh undefine minikube
#virsh net-destroy docker-machines
#virsh net-undefine docker-machines
#minikube delete

virsh list --all
virsh net-list

minikube config set vm-driver kvm
minikube config set WantReportErrorPrompt false
minikube start --memory 7600 --cpus 3 --disk-size 40g

kubectl get po -n kube-system

#https://github.com/fernandohackbart/bikeride-lagom/issues/162
generate-minikube-proxy.sh

mkdir -p /opt/kubernetes
git clone https://github.com/fernandohackbart/bikeride-lagom.git

cd bikeride-lagom/Kubernetes

################################################################
kubectl create -f jenkins-ephemeral.yml
kubectl get po

# kubectl logs <POD id> to get the key to unlock Jenkins
# http://192.168.1.200:30010/ to unlock Jenkins
# Install suggested plugins
# Install sbt plugin
# Create ssh credentials for the docker build machine
# Create configuration for SBT
## SBT at docker
## /opt/sbt/bin/sbt-launch.jar
# Configure nodes
## Reduce executor of master to 0
## Create docker
### Name: docker
### # of executors: 1
### remote root: /opt/jenkins
### labels: docker
### Laung methods: Launch slave agents via SSH
#### Host: <IP of the node>
#### Credentials:  <root credentials with ssh key>
#### Host Key verification strategy: Non verifying Verification Strategy
### Tools Locations:
#### (Sbt) SBT at docker
#### Home: /opt/sbt


# Create Pipeline
## https://jenkins.io/doc/book/pipeline/

dcoker login


################################################################
# Kong
# https://github.com/Kong/kong-dist-kubernetes/tree/master/minikube
kubectl create -f cassandra.yml
kubectl create -f kong_migration_cassandra.yml
kubectl delete -f kong_migration_cassandra.yml
kubectl create -f kong_cassandra.yml

################################################################
# Bikeride microservices
# kubectl create -f bikeride-namespace.yml
kubectl create -f authentication-deploy.yml
kubectl create -f authentication-service.yml
kubectl create -f biker-deploy.yml
kubectl create -f biker-service.yml
kubectl create -f track-deploy.yml
kubectl create -f track-service.yml
kubectl create -f ride-deploy.yml
kubectl create -f ride-service.yml

kubectl get all -n default
#kubectl get all -n bikeride

export KONG_IP=`minikube ip`

curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=authentication-api' --data 'strip_uri=false' --data 'uris=/api/authn' --data 'upstream_url=http://_lagom._tcp.bikeride-authentication.default.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=biker-api' --data 'strip_uri=false' --data 'uris=/api/biker,/api/bikers' --data 'upstream_url=http://_lagom._tcp.bikeride-biker.default.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=track-api' --data 'strip_uri=false' --data 'uris=/api/track' --data 'upstream_url=http://_lagom._tcp.bikeride-track.default.svc.cluster.local'
curl -i -X POST --url http://${KONG_IP}:30022/apis/ --data 'name=ride-api' --data 'strip_uri=false' --data 'uris=/api/ride' --data 'upstream_url=http://_lagom._tcp.bikeride-ride.default.svc.cluster.local'

