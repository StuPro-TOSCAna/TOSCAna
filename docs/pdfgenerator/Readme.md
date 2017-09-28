# How to build the docs?

## Requirements

1. Python
2. Sphinx : `pip install sphinx`
3. A latex installation, on Linux use TexLive
4. Some latex packages, if you got TexLive you can run the `install_texlive_packages.sh` script
5. A `index.rst` file in the root folder of your documentation

    example:
    ```
    Welcome to TOSCAna's documentation!
    ===================================

    .. toctree::
       :maxdepth: 2
       :caption: Contents:

       welcome
    ```
6. To build the doc pdf run the `build_pdf.sh` script
