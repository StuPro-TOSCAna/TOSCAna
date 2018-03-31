## General Note

Apart from the restrictions from the Effective Model, the following has to be considered regarding TOSCA service templates.

## Writing scripts for Intermediate Types

The behaviour of an intermediate type (e.g., WebApplication, SoftwareComponent, Database...) can be described using scripts.
The scripts have to follow these guidelines in order to be executed properly, i.e. to produce a working output:

 1. Init Systems are not supported: We currently do not support any kind of init system, that means that applications cannot get considered as a service and commands like `systemctl` (systemd) will not work.
 2. You should choose a supported operating system. We currently support
    1. Ubuntu (using `library/ubuntu`)
    2. Debian (using `library/debian`)
    3. CentOS (using `library/centos`)
    4. Fedora (using `library/fedora`)
    5. Alpine Linux (using `library/alpine`)
 3. Scripts within a standard lifecycle should not depend on each other, because they might get renamed. Dependencies do not get renamed. This means they should have a unique name to avoid collisions.
 4. Expect the containers to be empty (nothing installed). You should also run `apt-get update` (or corresponding) to ensure the package repositories are up to date.

## Handling dependencies (DependsOn and ConnectsTo)

The Kubernetes Plugin does not deploy the applications in the proper order, based on dependencies.

Instead we just create everything at once and assume that the application will just crash (exit with error code `!= 0`) if the dependencies are not fulfilled. This simple assumption will cause Kubernetes to restart the container (Pod state: `CrashLoopBackOff`) after a certain interval.
