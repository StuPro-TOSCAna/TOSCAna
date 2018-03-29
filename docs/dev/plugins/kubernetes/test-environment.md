# Setting up a Development/Test Environment

In order to test the functionality of the Kubernetes plugin you need some sort of Kubernetes cluster.
This short guide will describe to simple ways to setup a Kubernetes test and development environment.

## Using Minikube

[Minikube](https://github.com/kubernetes/minikube) allows you to locally run a single node Kubernetes cluster inside of a virtual machine.

After you have installed (Please consider reading the Official documentation to find out how to install it) Minikube just run:
```Bash
minikube start
```
to launch the local cluster. Running this command will also set the `kubectl` context to use the Minikube cluster as default.

For further information on how to use Minikube please visit: https://kubernetes.io/docs/getting-started-guides/minikube/

## Using kubeadm

Maybe testing with one node is not enough, for some reason.
[Kubeadm](https://kubernetes.io/docs/setup/independent/create-cluster-kubeadm/) can be used to easily deploy a Kubernetes cluster with multiple nodes.

To do so the machines have to be in the same network (possibly the same local network). You also have to install Kubeadm and Docker on every machine.

During development i used the following setup:
- A hypervisor running [Proxmox VE](https://www.proxmox.com/de/proxmox-ve)
  - The hypervisor is running 4 instances of
    - [CoreOS Container Linux](https://coreos.com/os/docs/latest/) - Version 1702.1.0
    - 2 GB RAM per VM (node)
    - Kubeadm installed on each node: [Install Guide](https://kubernetes.io/docs/setup/independent/install-kubeadm/#installing-kubeadm-kubelet-and-kubectl)

### Creating the cluster

After ensuring the machines are in the same network
(if there is a firewall between the nodes you have to ensure that the ports listed [here](https://kubernetes.io/docs/setup/independent/install-kubeadm/#check-required-ports) are open)
and installing the required software on every node (Kubeadm, Kubelet, Docker and Kubectl).
You just run `kubeadm init --pod-network-cidr=10.244.0.0/16` (as root) on the node that should be the master node.
This will initialize the master node.
The command also prints a "Login" command for new nodes.
Running this command on the remaining nodes (as root) will join these nodes into the cluster.

### Getting access to the cluster as non-root user

The master nodes `kubectl` can be used to manage the cluster, however this currently only works for the `root` user.
To also allow access as a non root user run the following commands:
```bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

To control the cluster on another machine, just copy the `~/.kube` folder to your local machines `~/.kube` folder using SCP.
However keep in mind that this will override all current local Kubectl configurations.

### Deploying a network overlay

[Canal](https://github.com/projectcalico/canal) was used as the network overlay for the cluster.
Canal can be deployed by running:
```bash
kubectl apply -f https://raw.githubusercontent.com/projectcalico/canal/master/k8s-install/1.7/rbac.yaml
kubectl apply -f https://raw.githubusercontent.com/projectcalico/canal/master/k8s-install/1.7/canal.yaml
```

**Note**: Deploying a network overlay is required to ensure the pods can communicate.

### Joining the Cluster

The "Login" command looks like this:
```Bash
kubeadm join --token <token> <master-ip>:<master-port> --discovery-token-ca-cert-hash sha256:<hash>
```

After waiting some time, Kubernetes will download the system pods on every node and deploy them,
your cluster is ready to be used for development/testing,

### Further reading
- Kubeadm - install guide: https://kubernetes.io/docs/setup/independent/install-kubeadm
- Kubeadm - deploy guide: https://kubernetes.io/docs/setup/independent/create-cluster-kubeadm/
