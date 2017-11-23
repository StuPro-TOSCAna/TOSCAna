# checks if given program is available in path
# $1: basename of program
function check () {
  echo "Check if "$1" is available."                                                      
  if ! [ -x "$(command -v "$1")" ]; then                                                 
    echo "Error: "$1" is not installed." >&2                                             
    return 1                                                                                 
  fi
}
