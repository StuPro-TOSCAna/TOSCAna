# Deploying CloudFoundry using Pivotal PCF Dev

## Setup

This guide assumes the following Prerequisites:
- VT-x or AMD-v virtualization enabled
- At least 3 GB of RAM and 50 GB of Space
- Ubuntu 16.04 as the Host OS
- SSH access to Host

### Prerequisites PCF Dev

#### Installing VirtualBox

```bash
echo "deb http://download.virtualbox.org/virtualbox/debian xenial contrib" | sudo tee -a /etc/apt/sources.list
wget -q https://www.virtualbox.org/download/oracle_vbox_2016.asc -O- | sudo apt-key add -
wget -q https://www.virtualbox.org/download/oracle_vbox.asc -O- | sudo apt-key add -
sudo apt-get update
sudo apt-get install virtualbox-5.2 dkms -y
```

#### Installing the VBox Extension Pack (Optional)

```bash
wget http://download.virtualbox.org/virtualbox/5.2.2/Oracle_VM_VirtualBox_Extension_Pack-5.2.2-119230.vbox-extpack
echo y | sudo vboxmanage extpack install Oracle_VM_VirtualBox_Extension_Pack-5.2.2-119230.vbox-extpack
rm Oracle_VM_VirtualBox_Extension_Pack-5.2.2-119230.vbox-extpack
```

#### Install CF CLI on the host

```bash
wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | sudo apt-key add -
echo "deb http://packages.cloudfoundry.org/debian stable main" | sudo tee /etc/apt/sources.list.d/cloudfoundry-cli.list
sudo apt-get update
sudo apt-get install -y cf-cli
```

## Install PCF Dev
Pivotal Account needed for the following steps!
- Download the latest version of PCF Dev on Pivotal Network: https://network.pivotal.io/
- Upload it to your Server
- move to the Directory and unzip:
```bash
unzip pcfdev-VERSION-linux.zip
```
- Install PCF Dev plugin
```bash
./pcfdev-VERSION-linux
```
- Start PCF Dev (Pivotal Login required):
```bash
cf dev start
```
- after the Banner appears, login with Username: `admin`, Password: `admin`
```bash
cf login -a https://api.local.pcfdev.io --skip-ssl-validation
```
- upload your Application, navigate into the Directory and deploy:
```bash
cf push application_name
```
- to stop PCF Dev:
```bash
cf dev stop
```

### Use of PCF Dev on Vsphere
If you are using PCF Dev on an environment like Vsphere without Root access, here is a workaround to get it displayed:

- Install lightweight LXDE Desktop on the Host
```bash
sudo apt install lxde
```
- Install Chromium Browser on the Host
```bash
sudo apt install chromium-browser
```
- If you use Linux, make sure XServer is installed, on Windows MobaXterm or on Mac x11.
- Connect to the Host and use the local Chromium Browser
```bash
ssh -X YOUR_USER@HOST_IP chromium-browser
```
- The local browser should be displayed now. Open https://api.local.pcfdev.io and login with Username: `admin`, Password: `admin`
- Your pushed Apps should be available under their own specific URLs

## Sources

PCF Dev Overview: https://docs.pivotal.io/pcf-dev/
PCF Dev install on Linux: https://docs.pivotal.io/pcf-dev/install-linux.html
