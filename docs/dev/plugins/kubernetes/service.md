# Kubernetes Services

# Introduction

In Kubernetes services are used to couple the backend, for example pods with the frontend. There are 4 different types of services: **ClusterIP**, **NodePort**, **LoadBalancer** and **ExternalName**.

# Overview
> The information is taken from the Kubernetes [Services Guide](https://kubernetes.io/docs/concepts/services-networking/service/)

| Type          | Description                                                                                                 | Use Case |
| :------------ | :---------------------------------------------------------------------------------------------------------- | :------- |
| **ClusterIP** | Exposes the service on a cluster internal IP. That means a Service can only reached from within the cluster | It can be used to expose f.ex. the IP from a Service that points to a Pod containing a MySql Database. Other Pods in the Cluster can now connect to the Database without knowing the exact IP adress of the MySql Pod they only need to know the service IP.         |
| **NodePort**|Exposes the service on each Node's IP at a static port. Creating a NodePort automatically creates a **ClusterIP** to which the NodePort service will route. The **NodePort** service adress from outside is **\<NodeIp>:\<NodePort>**| This can be used to adress a service on a given from outside the cluster. |
|**LoadBalancer**|Exposes the service externally using a cloud provider's load balancer.| This can be used to expose application in Pods that are distributed over several nodes. It can be used to transform the **tosca.nodes.LoadBalancer**. But currently only Amazon Web Services, Microsoft Azure and the Google Kubernetes Engine support this feature.  |
|**ExternalIP**|This service allows to expose services on an external IP that routes to a node in the cluster. Incoming traffic with the external IP as destination IP on the service port will be routed to one of the service endpoints. | This can be used if you want to expose a Ingress LoadBalancer like nginx.|

# DNS

> The information is taken from the Kubernetes [DNS Pods and Services Guide](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/#services)

DNS can be used to look up the IP of a service. This can be used f.ex. to connect a PHP app with a Database. To use DNS kubedns has to be installed, then every service is assigned a DNS name.
If we have the service **foo** in the namespace  **bar**, a pod running in the namespace **bar** can simply look up **foo**'s adress by doing a DNS query for **foo**. A pod running in another namespace hast to do a DNS query for **foo.bar** to lookup **foo**'s adress.
