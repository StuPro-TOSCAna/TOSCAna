# TOSCA Simple Profile Normative Types Support

This document shows the current plugin support status for normative types in TOSCAna.

## Supported Node Types

#  | NodeTypes             | Kubernetes | CloudFoundry | AWS CloudFormation
-- | --------------------- | ---------- | ------------ | ------------------
1  | Root                  | Sprint 4   | Sprint 4           | Sprint 4
2  | Compute               | Sprint 4   | Sprint 4           | Sprint 4
3  | SoftwareComponent     | Sprint 4   | Sprint 4           | Sprint 4
4  | WebServer             | Sprint 4         | Sprint 4     | Sprint 4
5  | WebApplication        | Sprint 4         | Sprint 4           | Sprint 4
6  | Database              | Sprint 4         | Sprint 4           | Sprint 4
7  | DBMS                  | No         | No           | No
8  | ObjectStorage         | No         | No           | No
9  | BlockStorage          | No         | No           | No
10 | Container-Runtime     | No         | No           | No
11 | Container-Application | No         | No           | No
12 | LoadBalancer          | No         | No           | No

## Supported Relationship Types

# | NodeTypes  | Kubernetes | CloudFoundry | AWS CloudFormation
- | ---------- | ---------- | ------------ | ------------------
1 | Root       | Sprint 4   | Sprint 4     | Sprint 4
2 | HostedOn   | Sprint 4   | Sprint 4     | Sprint 4
3 | ConnectsTo | Sprint 4   | Sprint 4     | Sprint 4
4 | DependsOn  | No         | No           | No
5 | AttachesTo | No         | No           | No
6 | RoutesTo   | No         | No           | No

## Supported Artifact Types

# | NodeTypes            | Kubernetes | CloudFoundry | AWS CloudFormation
- | -------------------- | ---------- | ------------ | ------------------
1 | Root                 | Sprint 4         | Sprint 4           | Sprint 4
2 | File                 | Sprint 4         | Sprint 4           | Sprint 4
3 | Deployment Types     | Sprint 4         | Sprint 4           | Sprint 4
4 | Implementation Types | Sprint 4         | Sprint 4           | Sprint 4

## Supported Capabilities Types

# | NodeTypes       | Kubernetes | CloudFoundry | AWS CloudFormation
- | --------------- | ---------- | ------------ | ------------------
1 | Root            | Sprint 4   | Sprint 4     | Sprint 4
2 | Endpoint        | Sprint 4   | Sprint 4     | Sprint 4
3 | Container       | No         | No           | No
4 | OperatingSystem | No         | No           | No
5 | Scalable        | No         | No           | No
6 | Network         | No         | No           | No
