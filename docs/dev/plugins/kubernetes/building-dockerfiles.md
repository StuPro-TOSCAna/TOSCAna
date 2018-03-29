# Default node behavior

The default behavior gets implemented by the abstract node types such as `SoftwareComponent` this behavior will look at the standard lifecycle operations.
If all of them are present they are added in the following order into the Dockerfile:

1. `create`
2. `configure`
3. `start`

The scripts and their dependencies for each phase get copied into the Dockerfile,
the corresponding properties get set as environment variable,
and the command gets executed (`create` and `configure`)

The `start` script does not get executed during the building process it gets copied just like all other scripts,
environment variables also get set normally, however the script will not get executed using a `RUN` command instead
it will be added to the entrypoint list that is responsible to run the `start` commands once the container gets created.

# Custom Node behavior

**NOTE**: All current implementations of the custom node only work this way if the do not feature a custom standard lifecycle.
If they have one, we refer to the default behavior.

## Apache

We assume that Apache always comes with PHP, that is the reason why we use the `library/php:httpd` image.
Furthermore we expect, that all child nodes (WebApplications) have a create or configure script that copies the contents
to the `/var/www` folder. These scripts are executed as a root user.

## MySQL (including Database)

MySQL Defaults to the `library/mysql:latest` image, the predefined properties are taken (such as root password)
and set as configuration environment variables.
If a child Database contains a `.sql` file the file will be copied in a special directory that is executed when starting the container.

## Java Runtime and Application

The java runtime and application types will use the `library/openjdk` image by default and the
jar set in the JavaApplication type will be copied into the Dockerfile (including its dependencies) a
`java -jar <JAR_FILE>` command is called as the entrypoint to launch the application.
