# Purpose

Some scripts might assume, that the current user is not `root` and they might use the `sudo`
command to perform operations that require elevated privileges (root privileges).
Because the default user in a Docker image is always `root`, acquiring the elevated privileges is not necessary.

The problem is that most Docker base images do not come with the `sudo` command pre installed.
This will result in a build failure with `ash: sudo: not found` (or similar) errors.

The easiest solution for this problem is to just install the sudo commmand.
This will cause the commands (with `sudo`) to execute normally,
because running `sudo` as `root` just executes the command normally.

# Supported Platforms

The sudo detection currently supports the following Docker Images:

 - `library/ubuntu` - Used for `OSCapabilites` with Ubuntu as operating system (set in the compute node)
 - `library/debian` - Used for `OSCapabilites` with Ubuntu as operating system (set in the compute node)
 - `library/alpine` -Used for `OSCapabilites` with Alpine Linux as operating system (set in the compute node)
  - `library/centos` - Used for `OSCapabilites` with CentOS as operating system (set in the compute node)
  - `library/fedora` - Used for `OSCapabilites` with Fedora as operating system (set in the compute node)
 - `library/php` - Used in conjunction with the Apache type
 - `library/mysql` - Used in conjunction with the MySQL DBMS type
 - `library/node` - Used in conjuction with the NodeJS type

# Functionality

To find out if sudo is needed, we scan every artifact for the String `sudo ` if this string is present,
we will install the sudo command.
The install command for a specific image is defined in a mapping file (`resources/kubernetes/docker/sudo-commands`).
If a mapping cannot be performed the transformation will still continue, but due to the fact that sudo is probably needed
for the execution the building procedure of the Docker image (from the created Dockerfile) will probably fail.
