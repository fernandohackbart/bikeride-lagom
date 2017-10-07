FROM bikeride/bikeride-backend:base
MAINTAINER Fernando Hackbart<fhackbart@gmail.com>
ARG bundleVersion
ARG bundlePort
ADD files/biker-lagom-impl-${bundleVersion}.zip /u01/
RUN mv /u01/biker-lagom-impl-${bundleVersion} /u01/biker-lagom-impl
EXPOSE 9012
ENTRYPOINT /u01/biker-lagom-impl/bin/biker-lagom-impl
