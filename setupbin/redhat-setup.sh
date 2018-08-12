#!/bin/bash

set -ex

echo "To replace crappy open jdk java with oracle java"

sudo yum -y remove java
sudo yum -y remove python-javapackages-3.4.1-11.el7.noarch

wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jre-8u60-linux-x64.rpm"

sudo yum -y localinstall jre-8u60-linux-x64.rpm


set -ex

sudo yum -y update

echo "INFO: Installing chrome"
wget http://chrome.richardlloyd.org.uk/install_chrome.sh
chmod u+x install_chrome.sh
sudo ./install_chrome.sh -f

echo "INFO: Installing atom editor"
# Was trying it to see if it's any better than sublime - doesn't appear to be :(
wget https://github.com/atom/atom/releases/download/v1.8.0/atom.x86_64.rpm
sudo yum -y localinstall atom.x86_64.rpm

echo "INFO: Installing emacs"
sudo yum -y install emacs

echo "INFO: Installing scala repl"
wget http://www.scala-lang.org/files/archive/scala-2.11.8.rpm
sudo yum -y localinstall scala-2.11.8.rpm

echo "INFO: Installing docker"
sudo tee /etc/yum.repos.d/docker.repo <<-EOF
[dockerrepo]
name=Docker Repository
baseurl=https://yum.dockerproject.org/repo/main/centos/7
enabled=1
gpgcheck=1
gpgkey=https://yum.dockerproject.org/gpg
EOF

sudo yum -y install docker-engine
sudo service docker start

echo "INFO: Installing Redhat VNC"
sudo yum -y install tigervnc-server

me=`whoami`

sudo cp /usr/lib/systemd/system/vncserver@.service /etc/systemd/system/vncserver@.service

sudo sed -i.bak "s/<USER>/$me/g" /etc/systemd/system/vncserver@.service

sudo systemctl daemon-reload

echo "INFO: Need to set a password for VNC"

vncpasswd

systemctl start vncserver@:1.service

service sshd start



