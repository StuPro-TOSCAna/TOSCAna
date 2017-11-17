# The Base Image Mapper

## The Problem

When Trying to convert TOSCA topologies to Docker images you have to find out on what these images should be based on. I.e. you have to find a Docker base image that will probably allow the creation of the Docker image.

What we try to do is Mapping this:
```yaml
- os:
    type: linux
    distribution: ubuntu
    version: 16.04
    architecture: x86_64
```

to a docker base image like the `library/ubuntu` one.

According to the TOSCA Specification, none of these values is required. The BaseImageMapper will try to find the best fitting base image. If possible.

## Solution approach

Before mapping can even start, we have to get the latest information about the Docker base image from Docker Hub (and later maybe other registries).
When the application launches the BaseImageMapper trys to retrieve a list of all tags for the supported docker base images.
The BaseImageMapper will support the following base images in the first revision:
- `ubuntu`
- `debian`
- `alpine`
- `centos`
- `opensuse`
- `busybox`

On launch the application will download the latest tags from DockerHub.
Once this process is done the Downloaded tag list will be stored in memory.
update of the Mappings will be performed every 24 hours (will be a changeable property).
The Key to change this value using Spring properties is `toscana.docker.base-image-mapper.update-interval`

The Mapping works as follows:
1. The mapper checks if the given capability is empty (all optionals do not conatin a value). If thats the case, it returns the default image (`library/ubuntu:latest`, the latest Ubuntu LTS version (currently 16.04))
2. The type attribute of the Capability gets checked. If the value is not set or is `linux` the mapping will continue and fail otherwise.
3. The distribution value is checked.
  - If it does not match one of the Oses described above, the mapping fails.
  - If the field is empty the image defaults to `library/ubuntu:latest`
4. The mapping will proceed with the version check if none of the conditions described above are triggered.
  - If no version is set `latest` will be used.
  - If the version can be directly mapped to a tag (identical name) it will use the found tag
  - if the version is not directly found a attempt to detect a viable tag is performed using Semantic versioning. This is done as follows. The list of tags first gets sorted by their mayor (has to be equal) and minor (has to be greater or equal) version. Everything thats smaller than the version given by the capability is not considered a version candidate and gets ignoresd.
    - If the resulting list only contains 1 element. This element is returned as the mapped base image
    - If the resulting list is empty, the mapping will fail (No possible images found)
    - If multiple ones are found, the mapper will return the image that has the latest bugfix version of the minor version thats the closest to the one given in the capability.
5. Check the architecture. if its not supported by the found tag (a invalid one is given) the mapping will fail, if its empty `amd64` will be used and the specified one afterwards.
6. Return the resulting base image. 

If a mapping fails, the `mapToBaseImage()` method throws a `UnsupportedOperationException`
