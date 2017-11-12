# Target artifact conventions
> **Target artifact:** the ZIP file a transformation produces

The purpose of this document is to specify conventions for the target artifact to ensure that they are as unified as possible. It is necessary that every plugin implements and follows this convention. If the conventions will be implemented, we provide a better user experience since using transformation artifacts from different platforms will be a lot more easier.

## Files and folders

**Structure:**
```
<target-artifact-name>.zip
├── Readme.html
├── docs/
└── output/
    └── scripts/
```

## Must have
- `Readme.html` - a Readme file in the root folder of the target artifact is mandatory. It holds the basic information of the target artifact, also if the plugin needs a short usage guide include it in this file.
- `output` - the `output` - folder holds everything generated during the transformation.
  - `output/scripts` - `scripts` is a subfolder of `output` it holds executable scripts generated during the transformation, that are needed to deploy the artifact. Scripts are all lowercase and to seperate logical words use `-`.

## Recommended

- `docs` - if the transformation artifact needs a more advanced documentation put it into the `docs` folder. And link to it in the `Readme.html`. To allow the user to browse the docs they are written in HTML.
