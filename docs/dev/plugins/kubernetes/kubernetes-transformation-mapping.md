# Kubernetes Transformation Mapping
This document is intended as an overview of the transformation mapping from TOSCA to Kubernetes.

It details the conversion of TOSCA node-, relationship, and capability types to Kubernetes compliant resource files in order to build the target artifact (See [target artifact conventions](..\target-artifact-conventions\target-artifact-conventions.md)). These mapping definitions serve as a base for the automatic transformation implemented by the Kubernetes Plugin which takes place in the **transform**-phase as described in the [transformation lifecycle](..\transformation-lifecycle\transformation-lifecycle.md).

First, the normative and non-normative types and capabilities supported by the Kubernetes Plugin are introduced and their respective transformations explained. Then these transformations are further illustrated by example through a demonstration of the transformation of two complete TOSCA service templates for separate applications.

## Table of contents
- [Normative Types](#normative-types)
  - Node Types
  - Relationship Types
  - Capability Types
- [Non-normative Types](#non-normative-types)
  - Node Types
  - Relationship Types
  - Capability Types
- [Example Scenarios](#example-scenarios)
  - [Simple Docker App](#simple-docker-app)
  - [LAMP-Stack](#lamp-stack)

## Normative Types
This section explains the transformation of normative TOSCA types to Kubernetes resource files.

### Node Types
#### tosca.nodes.Compute

#### tosca.nodes.WebApplication

### Relationship Types
#### tosca.relationships.HostedOn

### Capability types
#### tosca.capabilities.Endpoint

#### tosca.capabilities.Endpoint.Database

#### tosca.capabilities.Container

#### tosca.capabilities.OperatingSystem

## Non-Normative Types
This section explains the transformation of non-normative TOSCA types to Kubernetes resource files.

### Node Types
#### tosca.nodes.WebServer.Apache

#### tosca.nodes.Database.MySQL

#### tosca.nodes.DBMS.MySQL

#### tosca.nodes.Container.Application.Docker

### Capability types
#### tosca.capabilities.Container.Docker

## Example Scenarios
In this section two full TOSCA service templates are presented and the creation their respective Kubernetes resource files is explained.

### Simple Docker App
This example is based on the simple-task-docker-template we use for testing our Plugins.
It can be found under [`server/src/test/resources/csars/yaml/valid/simple-task-csar`](server/src/test/resources/csars/yaml/valid/simple-task-csar).

The topology consists of simple simple PHP application represented by the DockerApp node hosted on a DockerEngine node.

### LAMP-Stack
This example is based on the lamp-stack-template we use for testing our Plugins.
It can be found under [`server/src/test/resources/csars/yaml/valid/simple-task-csar`](server/src/test/resources/csars/yaml/valid/lamp-stack-csar).

The topology consists of 5 different nodes that make up the LAMP-stack. These are the non-normative node types for Apache, MySQL and DBMS.MySQL representing the web server, the database and the database management system and the normative node types for WebApplication and Compute representing the PHP application and the ubuntu server.
