pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn install -DskipTests'
                archiveArtifacts(onlyIfSuccessful: true, artifacts: 'core/target/*.jar')
            }
        }
        stage('Test') { 
            parallel { 
                stage('Test (Fast Tests)') { 
                    environment { 
                        TEST_MODE = 'fast' 
                    } 
                    steps { 
                        sh 'export DATADIR=$(pwd)/toscana-data && rm -rf $(pwd)/toscana-data && mkdir $(pwd)/toscana-data && mvn test -B' 
                    } 
                    post { 
                        always { 
                            junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true) 
                            sh '[ -d core/target/site ] && cd core/target/site && zip -r coverage-core-fast.zip jacoco && cd -; exit 0' 
                            sh '[ -d core/target/site ] && cd cli/target/site && zip -r coverage-cli-fast.zip jacoco && cd -; exit 0' 
                            archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip' 
                        } 
                    } 
                } 
                stage('Test (Slow Tests)') { 
                    environment { 
                        TEST_MODE = 'slow' 
                    } 
                    steps { 
                        sh 'export DATADIR=$(pwd)/toscana-data2 && rm -rf $(pwd)/toscana-data2 && mkdir $(pwd)/toscana-data2 && mvn test -B' 
                    } 
                    post { 
                        always { 
                            junit(testResults: 'core/target/surefire-reports/*.xml', allowEmptyResults: true) 
                            sh '[ -d core/target/site ] && cd core/target/site && zip -r coverage-core-slow.zip jacoco && cd -; exit 0' 
                            archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip' 
                        } 
                    } 
                }
            }
        } 
        stage('Deploy') {
            steps {
//  the JENKINS_NODE_COOKIE variable must be set to a different value than the id of this jenkins build in order for subprocesses to live longer than the build process
                 sh 'JENKINS_NODE_COOKIE=dontKillMe toscanad' 
            }
        }
    }
    post {
        always {
            sh 'mvn clean'
        }
    }
}
