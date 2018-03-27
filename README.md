![](docs/assets/logo.png)
# TOSCAna
[![Build Status](https://travis-ci.org/StuPro-TOSCAna/TOSCAna.svg?branch=ci)](https://travis-ci.org/StuPro-TOSCAna/TOSCAna)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d002dc08115145e6992ba64aa494893e)](https://www.codacy.com/app/stupro-toscana/TOSCAna?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=StuPro-TOSCAna/TOSCAna&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/StuPro-TOSCAna/TOSCAna/branch/master/graph/badge.svg)](https://codecov.io/gh/StuPro-TOSCAna/TOSCAna)
[![Documentation Status](https://readthedocs.org/projects/toscana/badge/?version=latest)](http://toscana.readthedocs.io/en/latest/?badge=latest)

## Introduction
The TOSCAna project allows the transformation of TOSCA CSARs into other cloud formats.
Currently supported target platforms are Kubernetes, CloudFoundry and AWS CloudFormation.

Check out the [wiki](http://toscana.readthedocs.io/en/latest/) for detailed information.

## Contributing
See our [contribution guidelines](CONTRIBUTING.md) for detailed information on how to contribute to TOSCAna.

## Project Structure
- [`docs/user`](docs/user) - User documentation
- [`docs/dev`](docs/dev) - Developer documentation
- [`server`](server) - server module
- [`cli`](cli) - command line interface module
- [`retrofit-wrapper`](retrofit-wrapper) - Java interface for REST api
- [`app`](app) - web app

## Tools
Tools that are used in this project:

- IDE: [IntelliJ](https://www.jetbrains.com/idea/)
- Code generation: [Project Lombok](https://projectlombok.org/)
- UML Modelling: [Lucidchart](https://www.lucidchart.com/)
- Project management: [ZenHub](https://www.zenhub.com/)
- CI: [TravisCI](https://travis-ci.org/StuPro-TOSCAna/TOSCAna)
- Code analysis: [Codacy](https://www.codacy.com/app/stupro-toscana/TOSCAna/dashboard)
- Code coverage: [Codecov](https://codecov.io/gh/StuPro-TOSCAna/TOSCAna), [Get browser extension](https://github.com/codecov/browser-extension)
## License
TOSCAna is licenced under the [Apache License 2.0](LICENSE).
