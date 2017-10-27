pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn install -DskipTests'
        archiveArtifacts(onlyIfSuccessful: true, artifacts: 'core/target/*.jar')
      }
    }
    stage('Test (Fast Tests)') {
      steps {
        sh 'export TEST_MODE=fast && export DATADIR=$(pwd)/toscana-data && rm -r -f $(pwd)/toscana-data && mkdir $(pwd)/toscana-data && mvn test -pl core'
      }
      post {
        always {
          junit(testResults: 'core/target/surefire-reports/*.xml', allowEmptyResults: true)
          junit(testResults: 'cli/target/surefire-reports/*.xml', allowEmptyResults: true)
          sh 'cd core/target/site && zip -r coverage-core-fast.zip jacoco && cd ../../../; exit 0'
          sh 'cd cli/target/site && zip -r coverage-cli-fast.zip jacoco && cd ../../../; exit 0'
          archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
        }
      }
    }
    stage('Test (Slow Tests)') {
      steps {
        sh 'export TEST_MODE=slow && export DATADIR=$(pwd)/toscana-data && rm -r -f $(pwd)/toscana-data && mkdir $(pwd)/toscana-data && mvn test -pl core'
      }
      post {
        always {
          junit(testResults: 'core/target/surefire-reports/*.xml', allowEmptyResults: true)
          sh 'cd core/target/site && zip -r coverage-core-slow.zip jacoco && cd ../../../; exit 0'
          archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
        }
      }
    }
    stage('Test (System Tests)') {
      steps {
        sh 'export TEST_MODE=system && export DATADIR=$(pwd)/toscana-data && rm -r -f $(pwd)/toscana-data && mkdir $(pwd)/toscana-data && mvn test -pl core'
      }
      post {
        always {
          junit(testResults: 'core/target/surefire-reports/*.xml', allowEmptyResults: true)
          sh 'cd core/target/site && zip -r coverage-core-system.zip jacoco && cd ../../../; exit 0'
          archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/site/*.zip'
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
