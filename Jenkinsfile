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

    stage('Deploy to AWS') {
        environment {
            DOCKER_HUB_LOGIN = credentials('docker-hub')
            VERSION = sh './gradlew properties | grep ^version'
            NAME = 'vcs-repository-search'
            STACK = 'stack-repository-search'
        }
        steps {
            withAWS(credentials: 'aws-credentials', region: env.AWS_REGION) {
                cfnValidate(file:'ecs.yml')
                cfnUpdate(stack:STACK,
                    create:true,
//                     timeoutInMinutes:20,
                    file:'ecs.yml',
                    params:['SubnetID': SUBNET_ID, 'ServiceName': DOCKER_HUB_LOGIN_USR, 'ServiceVersion': DOCKER_HUB_LOGIN_USR, 'DockerHubUsername': DOCKER_HUB_LOGIN_USR],
                    keepParams:['Version'],
                    pollInterval:1000
                )
            }
        }
    }
  }
  post {
    always {
      sh 'docker logout'
    }
  }
}
