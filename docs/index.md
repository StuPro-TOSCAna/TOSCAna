![](assets/logo-small.png)
# Introduction

TOSCAna is a open source project developed by students at the University of Stuttgart.

## Purpose

The TOSCA modelling language allows everyone to create a model of their system architecture that later can be deployed by an orchestrator.
But what if the user has an existing model and wants to transform it to the modelling language of a platform like Kubernetes?
This is where TOSCAna comes in: It provides the functionality to transform a TOSCA CSAR to the platform of your choice.

## Strengths

For end users TOSCAna already comes with a web application, were the they can upload CSARs, create transformations and do much more. 
Users calling the terminal their home can do the same things using the CLI.

Due to the modular approach developers can add add support for their own platforms.
The REST API allows to develop much more applications that can make use of TOSCAna, like mobile apps.

## Weaknesses

Currently TOSCAna only supports three platforms: Amazon Cloud Formation, Cloud Foundry and Kubernetes.
The support for some platforms are very limited regarding the supported node types.
Currently they only support the most common node types and use cases.

![system overview](c4/system-level.png)
