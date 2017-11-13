echo "Check if docker is available."                                                      
if ! [ -x "$(command -v docker)" ]; then                                                 
  echo 'Error: docker is not installed.' >&2                                             
  exit 1                                                                                 
fi                                                                                       
                                                                                            
if ! [ -x "$(pgrep -f docker > /dev/null)" ]; then                                        
  echo 'Error: docker daemon is not running.' >&2                                      
  exit 1                                                                               
fi
