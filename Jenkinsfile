pipeline {
  agent any
  stages {
    stage('Tool Versions') {
      steps {
        sh '''gradle --version

git --version

java -version'''
      }
    }

  }
}