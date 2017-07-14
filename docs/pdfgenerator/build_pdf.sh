#!/bin/bash

cp conf.py ../
pandoc --from=markdown_github+auto_identifiers --to=rst --output=../welcome.rst ../welcome.md
make latexpdf
rm ../conf.py
