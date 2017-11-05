#!/bin/bash
IMAGE_COUNT=`docker images |grep bikeride|grep impl |wc -l`
if [ ${IMAGE_COUNT} -gt 0 ]
then
  IMAGES_TO_REMOVE= `docker images |grep bikeride|grep impl |awk '{print $3 " "}' |tr -d '\n'`
  docker rmi ${IMAGES_TO_REMOVE}
fi