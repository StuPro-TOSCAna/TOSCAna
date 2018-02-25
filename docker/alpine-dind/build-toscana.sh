#!/bin/bash
echo "Cloning TOSCAna"
git clone https://github.com/StuPro-TOSCAna/TOSCAna.git

echo "Builing TOSCAna"
cd TOSCAna
mvn install -DskipTests -P build
cd ..
cp TOSCAna/server/target/server-1.0-SNAPSHOT.jar server.jar
rm -rf TOSCAna
