# How to setup kubernetes for testing

## Prerequisites
- VT-x or AMD-v virtualization must be enabled in your computer’s BIOS.

## Install necessary tools
- [Install kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Install minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/)

## Run minikube
- `minikube start` (the first start takes some time because minikube needs to pull its image)
- To reuse the docker daemon run `eval $(minikube docker-env)`.
  The local docker running on your host machine should now be talking to the docker daemon running inside the minikube VM. This step is necessary if you want to use local docker images.
- now run `docker build -t mytinytodo .` (replace `mytinytodo` with the tag you want)
- the image should now be accessible inside the minikube VM.
  You can check this by running `minikube ssh` to ssh into the VM and then running `docker images`, it should show now something like that:
  ```
  $ docker images
  REPOSITORY                                             TAG                 IMAGE ID            CREATED             SIZE
  mytinytodo                                             v1                  f0f3406e932a        12 hours ago        392 MB
  php                                                    5.6-apache          5623ea52d608        9 days ago          377.7 MB
  ```
- You now should be also able to acces the minikube dashboard via `minikube dashboard`
- To apply a Kubernetes config file you can run:
  `kubectl apply -f frontend-service.yaml`
  The service or pods now appear in the dashboard or can be viewed with `kubectl get services` or `kubectl get pods`.

## Further reading
This is a quick tutorial to check out how to install and configure `minikube` and `kubernetes` visit:
- [minikube](https://github.com/kubernetes/minikube) (Official minikube repo)
- [Kubernetes guestbook example](https://kubernetes.io/docs/tutorials/stateless-application/guestbook/) (Learn how to use Kuberntes)
