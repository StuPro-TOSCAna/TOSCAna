# Target artifact conventions
> **Target artifact:** the ZIP file a transformation produces

The purpose of this document is to specify conventions for the target artifact to ensure that they are as unified as possible. It is necessary that every plugin implements and follows this convention. If the conventions will be implemented, we provide a better user experience since using transformation artifacts from different platforms will be a lot more easier.

## Files and folders

**Structure:**
```
<target-artifact-name>.zip
├── readme.html
├── docs/
└── output/
    └── scripts/
```

## Must have
- `readme.html` - a Readme file in the root folder of the target artifact is mandatory. It holds the basic information of the target artifact. A readme file must contain at least the following sections:
   - explanation of the ZIP contents
      - folder structure
      - output contents
   - usage guide
      - step by step guide on how to handle the deployment of the target artifact
      - which scripts are included, what do they do and how should they be used
   - links to further documentation (see `docs`)
   
- `output` - the `output` - folder holds everything generated during the transformation.
  - `output/scripts` - `scripts` is a subfolder of `output` it holds executable scripts generated during the transformation, that are needed to deploy the artifact. In the [scripts](#scripts) section you can find more about script conventions.

## Recommended

- `docs` - if the transformation artifact needs a more advanced documentation put it into the `docs` folder. And link to it in the `Readme.html`. To allow the user to browse the docs they are written in HTML.


## Scripts

As mentioned before our target artifact folder has a `scripts` folder.
To prevent random names scripts also have to follow conventions.

### Script conventions
- Scripts are all lowercase and to seperate logical words use `-`.
- Scripts need to have the following format:
  > `<action>-<short-title>.sh`

Short title should be the short name of the object on which the action is performed on.

Actions should be as general as possible. Here is a list of script names that can be used:
- build scripts - `build-<short-title>.sh`
- deploy scripts - `deploy-<short-title>.sh`
- clean scripts - `clean-<short-title>.sh`

**Examples:**

To build the given docker images there can be a script named:
```
build-docker-images.sh
```

To deploy only a given MySql database there can be a script named:
```
deploy-mysql-database.sh
```

To deploy the whole stack there can be a script named:
```
deploy-example-app.sh
```
