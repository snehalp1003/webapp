#!/bin/bash
echo "Running after install script"

cd /home/ubuntu/webapp
sudo chown -R ubuntu:ubuntu /home/ubuntu/*
sudo chmod +x cloudwebapp-0.0.1-SNAPSHOT.jar

#Kill application if already running
kill -9 $(ps -ef|grep cloudwebapp-0.0.1 | grep -v grep | awk '{print $2}')

source /etc/environment
#Running webapp and appending logs
nohup java -jar cloudwebapp-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapplog.txt 2> /home/ubuntu/webapplog.txt < /home/ubuntu/webapplog.txt &

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/webapp/cloudwatch-agent.json -s

crontab -l > webappcron
echo "@reboot java -jar /home/ubuntu/webapp/cloudwebapp-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapplog.txt 2> /home/ubuntu/webapplog.txt < /home/ubuntu/webapplog.txt &" >> webappcron
crontab webappcron
rm webappcron