#!/bin/bash
cat << EOF | mysql -u root --password=${database_password}
CREATE DATABASE ${database_name};
EXIT
EOF
