pipeline {
    agent any

    environment {
        IMAGE_NAME = "multi-env-app"
        DOCKER_IMAGE = ""
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Scan') {
            steps {
                withSonarQubeEnv('sonarqube-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t multi-env-app .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-token', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    docker tag multi-env-app $DOCKER_USER/multi-env-app:latest
                    docker push $DOCKER_USER/multi-env-app:latest
                    '''
                }
            }
        }

        stage('Deploy to DEV') {
            steps {
                sh '''
                kubectl apply -f k8s/dev/deployment.yaml -n dev
                kubectl apply -f k8s/dev/service.yaml -n dev
                '''
            }
        }

        stage('Approval for TEST') {
            steps {
                input message: "Deploy to TEST environment?"
            }
        }

        stage('Deploy to TEST') {
            steps {
                sh '''
                kubectl apply -f k8s/test/deployment.yaml -n test
                kubectl apply -f k8s/test/service.yaml -n test
                '''
            }
        }

        stage('Approval for PROD') {
            steps {
                input message: "Deploy to PROD environment?"
            }
        }

        stage('Deploy to PROD') {
            steps {
                sh '''
                kubectl apply -f k8s/prod/deployment.yaml -n prod
                kubectl apply -f k8s/prod/service.yaml -n prod
                '''
            }
        }

    }
}
