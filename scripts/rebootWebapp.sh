#!/usr/bin/sh
PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin
JAVA=/usr/bin/java
MY_SERVER=/home/ubuntu/webapp/cloudwebapp-0.0.1-SNAPSHOT.jar
USER=ubuntu
/bin/su - $USER -c "nohup $JAVA -jar $MY_SERVER > /home/ubuntu/webapplog.txt 2> /home/ubuntu/webapplog.txt < /home/ubuntu/webapplog.txt &"