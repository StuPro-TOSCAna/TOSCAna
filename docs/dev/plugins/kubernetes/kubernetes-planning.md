# Kubernetes Plugin "Roadmap"

## Next steps

- Map "Compute" node to base image (Issue [#218](https://github.com/StuPro-TOSCAna/TOSCAna/issues/218))
  - What processor architectures will we support?
    - Base image docs form docker: [here](https://github.com/docker-library/official-images/blob/master/README.md)

- What about hardenend images?
- Dockerfile Builder (Helper to simplify the building of dockerfiles)
    - What library? Probably [this one](https://github.com/docker-java/docker-java)
    - Building docker images in parallel (async)
    - image export (async)
- Handle sudo (sometimes its not found because docker does not use any user management in many cases)
- Connection between pods
- How to test kubernetes (python and bash scripts to test), TestSuite as Docker image?
- Investigate TOSCA yaml/Effective Model to Kubernetes resource file mapping
  - Create document
  - (Create Test csars)
- Maybe use other kubernetes Client library
  - current [fabric8io/kubernetes-client](https://github.com/fabric8io/kubernetes-client)
  - maybe the "offical" kubernetes client

## Important in later sprints
  - TBD
