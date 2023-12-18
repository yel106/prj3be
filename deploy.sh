#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/prj3-be
cd $REPOSITORY
echo "> 현재 구동중인 어플리케이션 pid 확인"
CURRENT_PID=$(sudo lsof -i tcp:80 | awk 'NR!=1 {print$2}')

if [ -z "$CURRENT_PID" ];then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -9 $CURRENT_PID
  sleep 5
fi

echo "> 새 어플리케이션 배포 "
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
rm -rf $REPOSITORY/deploy.log $REPOSITORY/deploy-err.log
nohup sudo java -jar $JAR_NAME --spring.profiles.active=prod --spring.config.location=$REPOSITORY/application.properties >> $REPOSITORY/deploy.log 2> $REPOSITORY/deploy-err.log &
