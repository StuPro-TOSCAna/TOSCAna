# Deploying CloudFoundry using BOSH-Lite

One of the options to deploy CF locally is using BOSH-Lite (running inside a virtual machine)

**IMPORTANT NOTE**: Currently deployment of CF using this method did not work. After ~3 Hours the deployment fails. Consider using [PCF Dev](deploy-cf-pcf-dev.md) instead!

## Setup

This guide assumes the following Prerequisites:
- VT-x or AMD-v virtualization enabled
- At least 16 GB of RAM
- Ubuntu 16.04 as the Host OS
- More than 100GB of free disk space

### Deploying BOSH-Lite

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

#### Installing the BOSH CLI

```bash
wget https://s3.amazonaws.com/bosh-cli-artifacts/bosh-cli-2.0.45-linux-amd64
chmod +x bosh-cli-2.0.45-linux-amd64
sudo mv bosh-cli-2.0.45-linux-amd64 /usr/local/sbin/bosh
```

#### Install BOSH Dependencies

```bash
sudo apt-get install -y build-essential zlibc zlib1g-dev \
  ruby ruby-dev openssl libxslt-dev libxml2-dev \
  libssl-dev libreadline6 libreadline6-dev \
  libyaml-dev libsqlite3-dev sqlite3
```

### Deploy BOSH-Lite

```bash
git clone https://github.com/cloudfoundry/bosh-deployment ~/workspace/bosh-deployment
mkdir -p ~/deployments/vbox
cd ~/deployments/vbox
bosh create-env ~/workspace/bosh-deployment/bosh.yml \
  --state ./state.json \
  -o ~/workspace/bosh-deployment/virtualbox/cpi.yml \
  -o ~/workspace/bosh-deployment/virtualbox/outbound-network.yml \
  -o ~/workspace/bosh-deployment/bosh-lite.yml \
  -o ~/workspace/bosh-deployment/bosh-lite-runc.yml \
  -o ~/workspace/bosh-deployment/jumpbox-user.yml \
  --vars-store ./creds.yml \
  -v director_name="Bosh Lite Director" \
  -v internal_ip=192.168.50.6 \
  -v internal_gw=192.168.50.1 \
  -v internal_cidr=192.168.50.0/24 \
  -v outbound_network_name=NatNetwork
```

### Setup BOSH-CLI environment alias

```bash
bosh alias-env vbox -e 192.168.50.6 --ca-cert <(bosh int ./creds.yml --path /director_ssl/ca)
export BOSH_CLIENT=admin
export BOSH_CLIENT_SECRET=`bosh int ./creds.yml --path /admin_password`
```

### Check that it worked
When executing this command:
```bash
bosh -e vbox env
```
It should return this (with a different UUID):
```
Using environment '192.168.50.6' as client 'admin'

Name      Bosh Lite Director
UUID      7dfc1985-06a5-4fa8-aaef-1c049bad5b03
Version   264.3.0 (00000000)
CPI       warden_cpi
Features  compiled_package_cache: disabled
          config_server: disabled
          dns: disabled
          snapshots: disabled
User      admin

Succeeded
```

### Add IP Route

```bash
sudo ip route add   10.244.0.0/16 via 192.168.50.6
```

## Deploying CloudFoundry

### Clone the CF-Deployment Repository

```bash
cd ~
git clone https://github.com/cloudfoundry/cf-deployment
cd cf-deployment
```

### Upload the BOSH-Lite Cloud config

This step requires User confirmation (you have to enter `y`)

```bash
bosh -e vbox update-cloud-config iaas-support/bosh-lite/cloud-config.yml
```

### Upload the BOSH-Lite Stemcell

```bash
bosh -e vbox upload-stemcell https://bosh.io/d/stemcells/bosh-warden-boshlite-ubuntu-trusty-go_agent
```

### Deploy CloudFoundry

Be prepared to wait here. This takes very long (~1-2 Hours)

This step requires User confirmation (you have to enter `y`) after several minutes.
The Longer part comes after the confirmation.

```bash
bosh -e vbox -d cf deploy cf-deployment.yml \
  -o operations/bosh-lite.yml \
  --vars-store deployment-vars.yml \
  -v system_domain=bosh-lite.com
```

## Install CF CLI on the host

```bash
wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | sudo apt-key add -
echo "deb http://packages.cloudfoundry.org/debian stable main" | sudo tee /etc/apt/sources.list.d/cloudfoundry-cli.list
sudo apt-get update
sudo apt-get install -y cf-cli
```

## Continue after VM Restart

```bash
cd ~/deployments/vbox
```
Remove `current_manifest_sha` line from `state.json` to force a redeploy.
```bash
bosh create-env ~/workspace/bosh-deployment/bosh.yml \
  --state ./state.json \
  -o ~/workspace/bosh-deployment/virtualbox/cpi.yml \
  -o ~/workspace/bosh-deployment/virtualbox/outbound-network.yml \
  -o ~/workspace/bosh-deployment/bosh-lite.yml \
  -o ~/workspace/bosh-deployment/bosh-lite-runc.yml \
  -o ~/workspace/bosh-deployment/jumpbox-user.yml \
  --vars-store ./creds.yml \
  -v director_name="Bosh Lite Director" \
  -v internal_ip=192.168.50.6 \
  -v internal_gw=192.168.50.1 \
  -v internal_cidr=192.168.50.0/24 \
  -v outbound_network_name=NatNetwork
bosh cck -e vbox -d DeploymentName
```


## Sources

Deploying BOSH-Lite:
 - VirtualBox Setup: https://www.virtualbox.org/wiki/Linux_Downloads
 - BOSH-Lite Deployment: https://bosh.io/docs/bosh-lite#install
 - BOSH-CLI Setup: https://bosh.io/docs/cli-v2.html#install
 - BOSH create-env Dependencies: https://bosh.io/docs/cli-env-deps.html

Deploying CloudFoundry:
 - http://www.starkandwayne.com/blog/running-cloud-foundry-locally-on-bosh-lite-with-bosh2/)
 - https://github.com/cloudfoundry/cf-deployment/blob/master/deployment-guide.md
