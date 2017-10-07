FROM centos:7
MAINTAINER Fernando Hackbart<fhackbart@gmail.com>
#ENV JAVA_HOME /usr/lib/jvm/java
RUN yum install -y java-1.8.0-openjdk.x86_64 java-1.8.0-openjdk-devel.x86_64 && \
    yum clean all && \
    mkdir -p /u01 && \
    chmod a+xr /u01