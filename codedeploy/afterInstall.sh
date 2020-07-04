echo "Running after install script"
cd /home/ubuntu/webapp
sudo chown -R ubuntu:ubuntu /home/ubuntu/*
sudo rm -rf /home/ubuntu/webapp/*.jpg
sudo rm -rf /home/ubuntu/webapp/*.jpeg
sudo rm -rf /home/ubuntu/webapp/*.png
sudo chmod +x cloudwebapp-0.0.1-SNAPSHOT.jar

#Kill application if already running
kill -9 $(ps -ef|grep demo-0.0.1 | grep -v grep | awk '{print $2}')

source /etc/environment
#Running application and appending logs
nohup java -jar cloudwebapp-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapplog.txt 2> /home/ubuntu/webapplog.txt < /home/ubuntu/webapplog.txt &
