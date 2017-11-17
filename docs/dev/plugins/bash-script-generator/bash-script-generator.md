# Bash script generator

To provide the user a easy to use target artifact we provide him several build and deploy scripts. Since every plugin has to create those scripts is is better to build a generator that simplifies this job and ensures that the plugins do not produce duplicated code.

## Idea

Provide a general so called **BashScriptGenerator** that functions as interface for a general `utils.sh` script which contains different functions.

## Example

This function can be used to check if a executable is installed on the system the script is run on.

**Bash function:**

```bash
function check_executable () {
  echo "Check if $1 is available."                                                      
  if ! [ -x "$(command -v $1)" ]; then                                                 
    echo "Error: $1 is not installed." >&2                                             
    exit 1                                                                                 
  fi   
}
```

**Java wrapper:**

```Java
public void checkExecutable (String executable) {
    list.add("check_executable " + executable);
}
```

A plugin can extend the `BashScriptGenerator` if it has additional functions.

In general the scripts are saved in the `output\scripts\utils\` folder in the target artifact. If a plugin provides is own utils functions the script containing them is saved with the name `utils-<plugin-name>.sh`.
