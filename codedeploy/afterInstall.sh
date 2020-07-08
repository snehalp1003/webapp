#!/bin/bash
echo "Running after install script"

cd /home/ubuntu/webapp
sudo chown -R ubuntu:ubuntu /home/ubuntu/*
sudo chmod +x cloudwebapp-0.0.1-SNAPSHOT.jar

sudo chmod +x rebootWebapp.sh
sudo cp /home/ubuntu/webapp/rebootWebapp.sh /etc/init.d
update-rc.d rebootWebapp.sh defaults

#Kill application if already running
kill -9 $(ps -ef|grep cloudwebapp-0.0.1 | grep -v grep | awk '{print $2}')

source /etc/environment
#Running application and appending logs
nohup java -jar cloudwebapp-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapplog.txt 2> /home/ubuntu/webapplog.txt < /home/ubuntu/webapplog.txt &

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/webapp/cloudwatch-agent.json -s
