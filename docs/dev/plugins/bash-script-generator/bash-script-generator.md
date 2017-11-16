# Bash script generator

To provide the user a easy to use target artifact we provide him several build and deploy scripts. Since every plugin has to create those scripts is is better to build a generator that simplifies this job and ensures that the plugins do not produce duplicated code.

## Idea 
Provide a general so called **BashScriptGenerator** that can build bash scripts out of different components. A component could be the following snippet:
```bash
echo "Check if <executable> is available."                                                      
if ! [ -x "$(command -v <executable>)" ]; then                                                 
  echo 'Error: <executable> is not installed.' >&2                                             
  exit 1                                                                                 
fi     
```
This component can be used to check if a executable is installed on the system the script is run on.

If a plugin needs a component that is not implemented yet it just can add it and maybe the other plugins profit from it.
