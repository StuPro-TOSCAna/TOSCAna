## TOSCAna custom types

#### [toscana.nodes.JavaApplication](java-runtime.yaml)
A node representing a java application. A `jar` file must be supplied as deployment artifact.
#### [toscana.nodes.JavaRuntime](java-application.yaml)
A node representing a java runtime installation. 
Note: The `component_version` shall be specified as `1.7` (developer version), not `7` (product version). 
If the `component_version` is omitted, orchestrators shall use the latest available version.
