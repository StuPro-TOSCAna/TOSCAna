pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn install -B -P build'
                archiveArtifacts(onlyIfSuccessful: true, artifacts: '**/target/*.jar')
            }
        }
        stage('Run Unit Tests') {
            parallel {
              stage('Server') {
                  steps {
                      sh 'mvn test -B -pl server || exit 0'
                      sh 'mvn jacoco:report -pl server'
                  }
                  post {
                      always {
                          sh '[ -d server/target/site ] && cd server/target/site && zip -r coverage-server.zip jacoco && cd -; exit 0'
                      }
                  }
              }
              stage('Retrofit Wrapper') {
                  steps {
                      sh 'mvn test -B -pl retrofit-wrapper || exit 0'
                      sh 'mvn jacoco:report -pl retrofit-wrapper'
                  }
                  post {
                      always {
                          sh '[ -d retrofit-wrapper/target/site ] && cd retrofit-wrapper/target/site && zip -r coverage-wrapper.zip jacoco && cd -; exit 0'
                      }
                  }
              }
              stage('CLI') {
                  steps {
                      sh 'mvn test -B -pl cli || exit 0'
                      sh 'mvn jacoco:report -pl cli'
                  }
                  post {
                      always {
                          sh '[ -d cli/target/site ] && cd cli/target/site && zip -r coverage-cli.zip jacoco && cd -; exit 0'
                      }
                  }
              }
            }
            post {
                always {
                    junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
                }
            }
        }
        stage('Integration Test (Server)') {
            steps {
                sh 'mvn integration-test -P integration-test -pl server || exit 0'
                sh 'mvn jacoco:report -pl server'
            }
            post {
                always {
                    sh '[ -d server/target/site ] && cd server/target/site && zip -r coverage-server-integration.zip jacoco-it && cd -; exit 0'
                    junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true)
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
                }
            }
        }
        stage('Deploy') {
            when {
                allOf {
                  branch 'master'
                  environment name: 'TOSCANA_DEPLOY_ON_BUILD', value: 'true'
                }
            }
            steps {
                 sh 'chmod +x utils/jenkins/deploy-to-local-docker.sh && utils/jenkins/deploy-to-local-docker.sh'
            }
        }
    }
    post {
        always {
            sh 'mvn clean'
        }
    }
}
