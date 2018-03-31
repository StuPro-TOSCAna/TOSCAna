# The base image mapper

## The Problem

When Trying to convert TOSCA typologies to Docker images you have to find out on what these images should be based on.  
I.e. you have to find a docker base image that will probably allow the creation of the docker image.

What we try to do is mapping this:
```yaml
- os:
    type: linux
    distribution: ubuntu
    version: 16.04
    architecture: x86_64
```

to a docker base image like the `library/ubuntu` one.

According to the TOSCA specification, none of these values are required.
The `BaseImageMapper` will try to find the best fitting base image.

## Solution approach

Before mapping can even start, we have to get the latest information about the docker base image from DockerHub (and later maybe other registries).
When the application launches, the BaseImageMapper tries to retrieve a list of all tags for the supported docker base images.
In its first revision, the `BaseImageMapper` will support the following base images:

- `ubuntu`
- `debian`
- `alpine`
- `centos`
- `opensuse`
- `busybox`

On startup the application will download the latest tags from DockerHub.
Once this process is done the downloaded tag list will be stored in memory.
Update of the mappings will be performed every 24 hours (will be a changeable property).
The key to change this value using Spring properties is `toscana.docker.base-image-mapper.update-interval`

The mapping works as follows:

1. The mapper checks if the given capability is empty (optionals might not contain a value). If that is the case, it returns the default image (`library/ubuntu:latest`, the latest Ubuntu LTS version (currently 16.04))
2. The type attribute of the Capability gets checked. If the value is not set or is `linux` the mapping will continue and fail otherwise.
3. The distribution value is checked.
  - If it does not match one of the operating systems described above, the mapping fails.
  - If the field is empty the image defaults to `library/ubuntu:latest`
4. The mapping will proceed with the version check if none of the conditions described above are triggered.
  - If no version is set `latest` will be used.
  - If the version can be directly mapped to a tag (identical name) it will use the found tag
  - if the version is not directly found, an attempt to detect a viable tag is performed using semantic versioning. This is done as follows: The list of tags first gets sorted by their mayor (has to be equal) and minor (has to be greater or equal) version. Everything that's smaller than the version given by the capability is not considered a version candidate and gets ignored.
    - If the resulting list only contains a single element. This element is returned as the mapped base image
    - If the resulting list is empty, the mapping will fail (no possible images found)
    - If multiple ones are found, the mapper will return the image that has the latest bug fix version of the minor version that is the closest to the one given in the capability.
5. Check the architecture. If its not supported by the found tag (a invalid one is given) the mapping will fail. If it's empty, `amd64` will be used and the specified one afterwards.
6. Return the resulting base image.

If a mapping fails, the `mapToBaseImage()` method throws a `UnsupportedOperationException`
