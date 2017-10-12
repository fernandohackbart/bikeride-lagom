FROM bikeride/bikeride-backend:base
MAINTAINER Fernando Hackbart<fhackbart@gmail.com>
ADD files /u01/
EXPOSE 9000
ENTRYPOINT /u01/authentication-lagom-impl/bin/authentication-lagom-impl -d -Dlagom.cluster.join-self=on