# Challenges
- `database_host` as convention
  - problem is to recognize the semantic, because there is no host property in the nodes
- most of the user provided data cannot be accounted because Cloud Foundry will create automatically their own data
- additional buildpacks
- direct connection between applications
- always select the free plan of a service, not regarding the properties of the service like size 


### connectsTo connection between not-service nodes
To provide the connectsTo connection between top nodes in CloudFoundry, container-to-container networking is used. Therefore the plugin needs to know the source-app and the destination app.
Also the used protocol TCP or UDP and the used ports. Therefore the plugin checks the properties of the connectsTo-relation.
Maybe we could add this step to the steps before.
This also influence the order of deployment. The destination application must be created first.
Afterwards a network policy have to be created in the deploy scripts. (CF CLI)
Detailled information how to create a Container-to-Container Networking you find [here](https://docs.pivotal.io/pivotalcf/1-12/devguide/deploy-apps/cf-networking.html)
