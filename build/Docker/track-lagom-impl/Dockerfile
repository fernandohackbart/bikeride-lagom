FROM bikeride/bikeride-backend:base
MAINTAINER Fernando Hackbart<fhackbart@gmail.com>
ADD files /u01/
EXPOSE 9000
ENTRYPOINT /u01/track-lagom-impl/bin/track-lagom-impl \
  "$(eval "echo $SERVICE_DEBUG")" \
  -Dhttp.address="$(eval "echo $SERVICE_BIND_IP")" \
  -Dhttp.port="$(eval "echo $SERVICE_BIND_PORT")" \
  -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_HOST")" \
  -Dakka.remote.netty.tcp.bind-hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" \
  -Dakka.remote.netty.tcp.port="$(eval "echo $AKKA_REMOTING_PORT")"  \
  -Dakka.remote.netty.tcp.bind-port="$(eval "echo $AKKA_REMOTING_BIND_PORT")" \
  $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo " -Dakka.cluster.seed-nodes.$I=akka.tcp://bikeride-track@$NODE "; I=$(expr $I + 1); done)