# Requirements

In this document the terms *'Must have'*, *'Should have'*, *'Could have'* and *'Won't have'* refer to the [MoSCoW method](https://en.wikipedia.org/wiki/MoSCoW_method).


### Backend
| What | How | Importance |Comment|
|------|-----|------------|-------|
|Language|Java|Should Have|No node.js (Must have)|
|Webserver|Tomcat|Should have| |
|Interface|REST-API|Must have| |
|Platform| Linux | Must have | |
| | Windows, OSX | Should have | |


### User Interface (UI)
| What | How | Importance |Comment|
|------|-----|------------|-------|
|Type|Web Application|Must have| |
|Supported browsers|Firefox, Chrome| Must have| |
|Technology|Angular|Should have| |
|Communcation|REST-calls|Must have| |
|Ease of Use | Very Simple | Must Have | The UI must be simple and easy to use. Within a handful of clicks the transformation should be achieved |
|Look and Feel  | The UI must look awesome. No further requirements. | Must Have | Is taste relative..?|

### Command Line Interface (CLI)
| What | How | Importance |Comment|
|------|-----|------------|-------|
|Communication|REST-calls|Must have| |

### Transformations
| What | How | Importance |Comment|
|------|-----|------------|-------|
|Support at least 3 target platforms | | Should have |  Transformation to every supported target platform must function properly |
| TOSCA Spec version | Latest YAML specification | Should have | if YAML seems not to be working out, switching to the older XML specification is acceptable |
|Target Host OS| Linux | Must have | |
|TOSCA --> AWS Cloud Formation| CSAR -> Cloud Formation Template & Orchestration, if needed|Should have| Preferable as first transformation; it is acceptable to optimize the topology |
|TOSCA --> Kubernetes |  CSAR -> Kubernetes Artifacts & Orchestration, if needed|Should have| it is acceptable to optimize the topology|
|TOSCA --> Cloud Foundry| CSAR -> Cloud Foundry Artifacts & Orchestration, if needed|Should have| it is acceptable to optimize the topology |
| Validation of input (CSAR) | syntax | Must Have | | 
| Validation of input (CSAR) | semantics | Must Have | Not before execution; but if an error is found while executing the transformation, an error must be reported | 
| Topology Optimization Mode | Implement normal and topology optimizing transformation mode | Could have| 'Tuning option' |
| Adapt to target platform updates | If target platforms change, change transformator accordingly | Won't have | Beyound the scope of project; and most probably not relevant due to backward compatibilty | 

### TOSCA Support 
| What | How | Importance |Comment|
|------|-----|------------|-------|
| Specification support | most important elements | Must Have | Not included: Policies. Leave out the bells and whistles ('fancy stuff'). However, build architecture in an extendable way, so the full TOSCA spec can be supported in the future |
| Node Support | Must be deployable  via Lifecycle Interface | Must have | |
| NodeType, RelationshipType Support | Support from YAML-Profile | Must have | A basic LAMP-Stack must be deployable with the transformator; the level of mininum node type and relationship type support can be derived from this requirement |
| NodeType Support | Support custom Node Types of Institute | Could Have | |
| BPMN Support | Translator support BPMN part of TOSCA specification | Won't have | would be awesome; too hard; for our project, only declarative (no imperative) node description must be supported | 

### Transformation Center
| What | How | Importance |Comment|
|------|-----|------------|-------|
| Deploy app |After transformation, offer mechanism for deployment| Must Have (implicit)|Although not explicitly required by client: Some details of the TOSCA model might not be representable in the target platforms modelling language, hence orchestration will be neccessary. note: we have to develop this anyways for testing purposes|
|Status information | during deployment, show current status of deployment | Could Have | Client would appreciate that very much | 
| Endpoint of deployed app | After deployment, the user must find information about how to reach the deployed app | Must Have | only if information is found in boundary definition of the TOSCA model |
| Request additional data | If not contained in the TOSCA model itself, the web app must request missing data from the user | Must Have | e.g. credentials. in cli mode not required (throw error instead) | 
| Manage multiple CSARs at once | | Won't have | This would force us building a whole cloud deployment center, which is considered to be beyound the scope of our project | 
| Adapt deployment at runtime | If CSAR changes, the changes must be reflected in the deployment | Won't have | too complex | 
| App Monitoring | Provice health monitoring of deployed app | Could have | very nice to have but very unrealistic|
| Error handling | If transformation is not possible, the user gets informed about the problem and the state of the deployment, if any | Must Have | |
| | The application recognizes the problem and offers auto correction | Could Have | in form of topology optimiziation |
| | If deployment was started, but not finished correctly, compensate changes | Could Have | Considered to be quite hard | 

### Misc
| What | How | Importance |Comment|
|------|-----|------------|-------|
| Build TOSCA XML -> TOSCA YAML Converter | | Could Have | In order to support the OpenTOSCA Winery tool |
| Be Apache2.0 compliant | Usage of third party Open Source software is possible as long as their licence is compliant to Apache2.0 | Must have | Therefore, usage of GPL is prohibited |
| Performance of transformations | There are no specific performance requirements. Eventually the transformation must be completed.| Won't have | | 
