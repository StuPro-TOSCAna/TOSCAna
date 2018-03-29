# TOSCAna Types

In addition to TOSCA's normative and non-normative types, TOSCAna natively supports several own type definitions.  
Just like normative or non-normative TOSCA types, TOSCAna types can simply be used in a TOSCA service template.
The correlated type definition doesn't have to be contained in template.

#### toscana.nodes.JavaApplication
A node representing a java application. A `jar` file must be supplied as deployment artifact.

```yml
toscana.nodes.JavaApplication: 
  derived_from: tosca.nodes.SoftwareComponent 
  properties:
    vm_options:
      type: string
      description: The options which are given to the jvm on startup
    arguments:
      type: string
      description: The arguments which are given to the application on startup
  requirements:
    - host:
        node: toscana.nodes.JavaRuntime
```

#### toscana.nodes.JavaRuntime
A node representing a java runtime installation.  
The `component_version` shall be specified like `1.7` (developer version), not like `7` (product version).   
If `component_version` is omitted, orchestrators shall use the latest available version.

```yml
toscana.nodes.JavaRuntime: 
  derived_from: tosca.nodes.SoftwareComponent 
  capabilities:
    host:
      valid_source_types: [toscana.nodes.JavaApplication]
```
