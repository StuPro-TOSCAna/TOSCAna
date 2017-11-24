pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn install -DskipTests'
                archiveArtifacts(onlyIfSuccessful: true, artifacts: '**/target/*.jar')
            }
        }
        stage('Test') {
            parallel {
              stage('Test Server') {
                  steps {
                      sh 'mvn test -B -pl server'
                      sh 'mvn jacoco:report -pl server'
                  }
                  post {
                      always {
                          sh '[ -d server/target/site ] && cd server/target/site && zip -r coverage-server.zip jacoco && cd -; exit 0'
                      }
                  }
              }
              stage('Test Retrofit Wrapper') {
                  steps {
                      sh 'mvn test -B -pl retrofit-wrapper'
                      sh 'mvn jacoco:report -pl retrofit-wrapper'
                  }
                  post {
                      always {
                          sh '[ -d retrofit-wrapper/target/site ] && cd retrofit-wrapper/target/site && zip -r coverage-wrapper.zip jacoco && cd -; exit 0'
                      }
                  }
              }
              stage('Test CLI') {
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
