# Kubernetes Basic Transformation Workflow

This document outlines the workflow for the deployment of a simple application on the Kubernetes platform in order to help design the basic Kubernetes plugin for TOSCAna.

#### Basic Information about Kubernetes
- Packs applications in isolated containers instead of VMs (one application per container)
- Deploys containers based on operating-system-level virtualization rather than hardware virtualization
- Kubernetes mainly uses Docker for containers

## Automated Workflow
Describes the planned automated deployment of the simple application based on our [transformation workflow](https://github.com/StuPro-TOSCAna/TOSCAna/blob/transformation-flow/docs/dev/architecture/workflow.md).

### Transformation
1. Step: Parse model and determine stack of nodes, that means decide which Nodes will be in the same docker image and then decide which of them will be in the same pod.
2. Step: Check if given Nodes have:  
    Case a) A repository (repo) path to a docker image.  
    Case b) A DockerApp with a Dockerfile.  
    Case c) Nothing that has to do with a DockerApp.
3. Step: Decide what to do, in Case:  
    a) If repo is private ask for credentials for the registry where the repo lays
    b) Ask for a registry and corresponding credentials
    c) Check if you can generate a Dockerfile out of the given Nodes. (f. ex. Stack on Windows not possible with Kubernetes.) If **yes** procede with **b)**.
5. Build target artifact, in Case:  
    a) Build resources with credentials. If user rejected to enter credentials leave them empty.  
    b) Build script that generates images from Dockerfiles and pushes them to a given repository. If in Step 3 the user rejected to enter a registry and credentials generate a script and leave the registry information empty.  
    c) Generate Dockerfiles out of the given Nodes, then procede with **a)** and **b)**.

### Deployment [WIP, not important right now]
Create script that applies generated Kubernetes resources file to a given  kubernetes host.

---

**Additional Information**:
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
