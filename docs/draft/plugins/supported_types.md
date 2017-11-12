# Supported TOSCA Types

This document shows the current plugin support status for normative types in TOSCAna.

## Supported Node Types

| #   | NodeTypes             | Kubernetes | CloudFoundry | AWS CloudFormation |
| --- | --------------------- | ---------- | ------------ | ------------------ |
| 1   | DockerApp             | Sprint 4   | Sprint 4     | Sprint 4           |
| 2   | LoadBalancer          | Sprint 4   | No           | No                 |
| 3   | Root                  | No         | No           | No                 |
| 4   | Compute               | No         | No           | No                 |
| 5   | SoftwareComponent     | No         | No           | No                 |
| 6   | WebServer             | No         | No           | No                 |
| 7   | WebApplication        | No         | No           | No                 |
| 8   | DBMS                  | No         | No           | No                 |
| 9   | ObjectStorage         | No         | No           | No                 |
| 10  | BlockStorage          | No         | No           | No                 |
| 11  | Container-Runtime     | No         | No           | No                 |
| 12  | Container-Application | No         | No           | No                 |
| 13  | LoadBalancer          | No         | No           | No                 |

## Supported Relationship Types

| #   | NodeTypes  | Kubernetes | CloudFoundry | AWS CloudFormation |
| --- | ---------- | ---------- | ------------ | ------------------ |
| 1   | HostedOn   | No         | No           | No                 |
| 2   | ConnectsTo | No         | No           | No                 |
| 3   | DependsOn  | No         | No           | No                 |
| 4   | AttachesTo | No         | No           | No                 |
| 5   | RoutesTo   | No         | No           | No                 |

## Supported Capabilities Types

| #   | NodeTypes       | Kubernetes | CloudFoundry | AWS CloudFormation |
| --- | --------------- | ---------- | ------------ | ------------------ |
| 1   | Root            | No         | No           | No                 |
| 2   | Endpoint        | No         | No           | No                 |
| 3   | Container       | No         | No           | No                 |
| 4   | OperatingSystem | No         | No           | No                 |
| 5   | Scalable        | No         | No           | No                 |
| 6   | Network         | No         | No           | No                 |
