## Limitations of the node stack building algorithm

### Multiple application host

To illustrate this limitation the following sample topology will be used:

![](img/webapp-y-unsupported-topology.png)

Following the behavior of the current NodeStack building Algorithm this will result in the following two node stacks:

![](img/webapp-y-unsupported-node-stacks.png)

If we assume, that the Port of the WebApplications is determined by the parent web server (for example: Apache or Tomcat)
this configuration will result in a port conflict, because the web servers in each node stack
feature the same configuration (i.e. the same port) and the same compute node, meaning they will land in the same pod.
A Pod wont work properly if two containers want the same port, therefore such a topology cannot be transformed properly.

if the WebApplications determine the port, the given topology can be transformed if the ports are different,
this could be the case when messing with NodeJS applications.

A similar issue can also be applied to Databases.

## Limitations of the connection graph

### Multiple outgoing connections to the same node stack

The Connection graph currently only considers One Connection per direction in between two node stacks.
To illustrate this limitation we will take a look at the following topology.
(We use abstract types, because we currently do not support types that might cause this issue):

![](img/connection-graph-unsupported-topology.png)

The resulting connection graph would look like this:

![](img/connection-graph-unsupported-stack-graph.png)

However due to the current implementation of the ConnectionGraph we are only able to store one of the edges.
