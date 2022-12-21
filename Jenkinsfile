pipeline {
    agent any

    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Tool Versions') {
          steps {
            sh 'gradle --version'
            sh 'git --version'
            sh 'java -version'
        }
        stage('Build') {
          steps {
            sh './gradlew assemble'
          }
        }
        stage('Test') {
          steps {
            sh './gradlew test'
          }
        }
        stage('Build Docker image') {
          steps {
            sh './gradlew docker'
          }
        }
        stage('Push Docker image') {
          environment {
            DOCKER_HUB_LOGIN = credentials('docker-hub')
          }
          steps {
            sh 'docker login --username=$DOCKER_HUB_LOGIN_USR --password=$DOCKER_HUB_LOGIN_PSW'
            sh './gradlew dockerPush'
          }
        }
    }
}