# Supported operating systems

The Kubernetes plugin currently only supports Linux based operating systems.
To be more precise the following Distributions are explicitly supported:

 - Ubuntu
 - Debian
 - CentOS
 - Fedora
 - Alpine Linux

# Supported TOSCA types

All types listed here are supported for Transformation, types that are not supported and will probably not get transformed.

## Non normative types

| TOSCA Type                               | Transformation behavior         |
|:-----------------------------------------|:---------------------------------|
| MySQLDBMS                                | Gets mapped to the `library/mysql` image, Defined TOSCA Properties get set trough environment variables used to configure the image like `MYSQL_ROOT_PASSWORD` |
| MySQLDatabase                            | Requires MySQLDBMS as the parent node, Defined Properties get mapped to configuration Environment variables (Just like MySQLDBMS). `.sql` artifacts get copied into a special directory to be executed once before the application launches (`/docker-entrypoint-initdb.d/`) |
| Apache                                   | Uses the `library/php:httpd` image (we consider Apache to also contain PHP), installs `mysqli` if a child has a connection to a MySQL Database / DBMS |

## Normative Types

| TOSCA Type                               | Note                |
| -----------------------------------------| ------------------- |
| WebApplication                           | considered abstract |
| Database                                 | considered abstract |
| WebServer                                | considered abstract |
| DBMS                                     | considered abstract |
| SoftwareComponent                        | considered abstract |
| Java Runtime (TOSCAna custom type)       | considered abstract |
| Java Application (TOSCAna custom type)   | considered abstract |

### What is a abstract type?

A abstract type is a type that can be described completely by scripts.
To get more information about that read [here](creating-models.md)
