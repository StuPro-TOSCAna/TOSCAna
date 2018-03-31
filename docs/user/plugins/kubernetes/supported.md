# Supported operating systems

The Kubernetes plugin currently only supports Linux based operating systems.
To be more precise, the following distributions are explicitly supported:

 - Ubuntu
 - Debian
 - CentOS
 - Fedora
 - Alpine Linux

# Supported TOSCA types

All types listed here are can be transform with the Kubernetes plugin. 
Types not listed will probably not get transformed properly.

## Non-normative types

| TOSCA Type                               | Transformation behavior         |
|-----------------------------------------|---------------------------------|
| MySQLDBMS                                | Gets mapped to the `library/mysql` image, Defined TOSCA Properties get set trough environment variables used to configure the image like `MYSQL_ROOT_PASSWORD` |
| MySQLDatabase                            | Requires MySQLDBMS as the parent node, Defined Properties get mapped to configuration Environment variables (Just like MySQLDBMS). `.sql` artifacts get copied into a special directory to be executed once before the application launches (`/docker-entrypoint-initdb.d/`) |
| Apache                                   | Uses the `library/php:httpd` image (we consider Apache to also contain PHP), installs `mysqli` if a child has a connection to a MySQL Database / DBMS |

## Normative Types

| TOSCA Type                                | Note                    |
| ----------------------------------------- | -------------------     |
| WebApplication                            | considered intermediate |
| Database                                  | considered intermediate             |
| WebServer                                 | considered intermediate             |
| DBMS                                      | considered intermediate             |
| SoftwareComponent                         | considered intermediate             |
| Java Runtime (TOSCAna custom type)        | considered intermediate             |
| Java Application (TOSCAna custom type)    | considered intermediate             |

### What is a intermediate type?

A intermediate type is a type that can be described completely by scripts.
To get more information about that read [here](creating-models.md)
