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
                        sh 'export DATADIR=$(pwd)/toscana-data && rm -r -f $(pwd)/toscana-data && mkdir $(pwd)/toscana-data && mvn test -pl core'
                    }
                    post {
                        always {
                            junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
                            sh 'cd core/target/site && zip -r coverage-core-fast.zip jacoco && cd ../../../; exit 0'
                            sh 'cd cli/target/site && zip -r coverage-cli-fast.zip jacoco && cd ../../../; exit 0'
                            archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
                        }
                    }
                }
                stage('Test (Slow Tests)') {
                    environment {
                        TEST_MODE = 'slow'
                    }
                    steps {
                        sh 'export DATADIR=$(pwd)/toscana-data2 && rm -r -f $(pwd)/toscana-data2 && mkdir $(pwd)/toscana-data2 && mvn test -pl core'
                    }
                    post {
                        always {
                            junit(testResults: 'core/target/surefire-reports/*.xml', allowEmptyResults: true)
                            sh 'cd core/target/site && zip -r coverage-core-slow.zip jacoco && cd ../../../; exit 0'
                            archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
                        }
                    }
                }
            }
        }
        stage('Cleanup') {
            steps {
                sh 'mvn clean'
            }
        }
    }
}
