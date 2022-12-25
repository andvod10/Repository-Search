pipeline {
  agent any
  triggers {
      pollSCM 'H/2 * * * *'
  }
  
  stages {
    stage('Tool Versions') {
      parallel {
        stage('Tool Versions') {
          steps {
            sh 'ls -l gradlew'
            sh 'chmod +x gradlew'
            sh './gradlew --version'
            sh 'git --version'
            sh 'java -version'
          }
        }

        stage('Check if build.gradle exists') {
          steps {
            script {
              if (fileExists('build.gradle.kts')) {
                echo 'Yes'
              } else {
                echo 'No'
              }
            }
          }
        }
      }
    }

    stage('Build Project') {
      steps {
        sh 'ls -l gradlew'
        sh 'chmod +x gradlew'
        sh './gradlew clean assemble'
      }
    }

    stage('Test Project') {
      steps {
        sh 'ls -l gradlew'
        sh 'chmod +x gradlew'
        sh './gradlew test'
      }
    }

    stage('Docker Build') {
      environment {
        DOCKER_HUB_LOGIN = credentials('docker-hub')
      }
      steps {
        script {
          if (fileExists('Dockerfile')) {
            echo 'Yes'
          } else {
            echo 'No'
          }
          if (fileExists('build/libs/vcs-repository-search.jar')) {
            echo 'Yes'
          } else {
            echo 'No'
          }
        }
        sh 'docker --help'
        sh 'docker build -t andvod/vcs-repository-search-image:latest .'
        sh 'docker login --username=$DOCKER_HUB_LOGIN_USR --password=$DOCKER_HUB_LOGIN_PSW'
        sh 'docker push andvod/vcs-repository-search-image:latest'
      }
    }
  }
  post {
    always {
      sh 'docker logout'
    }
  }
}
