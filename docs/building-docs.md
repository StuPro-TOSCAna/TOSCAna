# Building the Documentation

We use MkDocs, with the 'rtd-dropdown' theme, to build our documentation.

## Installing MkDocs localy

The first option to use MkDocs is to install it manually.

### Install MkDocs itself

To install MkDocs please consult the following installation guide:
http://www.mkdocs.org/#installation

### Install RTD-Dropdown theme

We the ReadTheDocs - Dropdown theme. This can be installed using
```
sudo pip install mkdocs-rtd-dropdown
```

In case this command fails you can also clone the repository (https://github.com/cjsheets/mkdocs-rtd-dropdown) and run `sudo python3 setup.py install` to install the theme

## Using the Docker-Image

To make the process described above we created a Docker Image that contains all the programs needed to serve and build the documentation.

The image assumes the Directory containing the `mkdocs.yml` is mounted at `/repo`

The Commands shown below assume that your current working directory is the root folder in which the `mkdocs.yml` is located. The Commands can be Added as aliases to ease the use.

The image does not support the `mkdocs gh-deploy` command!

### Building the Documentation

For Bash/Zsh:
```bash
docker run -it --rm -v $(pwd):/repo -u $(id -u):$(id -g) toscana/mkdocs:latest mkdocs build
```

For Fish Shell:
```fish
docker run -it --rm -v (pwd):/repo -u (id -u):(id -g) toscana/mkdocs:latest mkdocs build
```

*Note:* It is recomended to add the `-u $(id -u):$(id -g)` part, otherwise the `site` directory will be owned by root

### Serving the documentation

Serving the Documentation uses the `mkdocs serve` command to allow the Real-Time editing of the files.

#### Serve as Daemon

For Bash/Zsh:
```bash
docker run --rm -d -v $(pwd):/repo -p 8000:8000 toscana/mkdocs:latest
```

For Fish Shell:
```fish
docker run --rm -d -v (pwd):/repo -p 8000:8000 toscana/mkdocs:latest
```

#### Serve as Command

For Bash/Zsh:
```bash
docker run -it --rm -v $(pwd):/repo -p 8000:8000 toscana/mkdocs:latest
```

For Fish Shell:
```fish
docker run -it --rm -v (pwd):/repo -p 8000:8000 toscana/mkdocs:latest
```
