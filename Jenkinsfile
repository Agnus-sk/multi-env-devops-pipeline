pipeline {
    agent any

    environment {
        DOCKER_USER = "agnussk"
        IMAGE_NAME = "multi-env-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
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

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-token', usernameVariable: 'DOCKER_USER_NAME', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                    echo \$DOCKER_PASS | docker login -u \$DOCKER_USER_NAME --password-stdin
                    docker push ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }

        stage('Deploy to DEV') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-jenkins', variable: 'KUBECONFIG')]) {
                    sh """
                    export KUBECONFIG=\$KUBECONFIG
                    sed -i 's|image:.*|image: ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}|' k8s/dev/deployment.yaml
                    kubectl apply -f k8s/dev/deployment.yaml -n dev --validate=false
                    kubectl apply -f k8s/dev/service.yaml -n dev --validate=false
                    """
                }
            }
        }

        stage('Approval for TEST') {
            steps {
                input message: "Deploy to TEST environment?"
            }
        }

        stage('Deploy to TEST') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-jenkins', variable: 'KUBECONFIG')]) {
                    sh """
                    export KUBECONFIG=\$KUBECONFIG
                    sed -i 's|image:.*|image: ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}|' k8s/test/deployment.yaml
                    kubectl apply -f k8s/test/deployment.yaml -n test --validate=false
                    kubectl apply -f k8s/test/service.yaml -n test --validate=false
                    """
                }
            }
        }

        stage('Approval for PROD') {
            steps {
                input message: "Deploy to PROD environment?"
            }
        }

        stage('Deploy to PROD') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-jenkins', variable: 'KUBECONFIG')]) {
                    sh """
                    export KUBECONFIG=\$KUBECONFIG
                    sed -i 's|image:.*|image: ${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}|' k8s/prod/deployment.yaml
                    kubectl apply -f k8s/prod/deployment.yaml -n prod --validate=false
                    kubectl apply -f k8s/prod/service.yaml -n prod --validate=false
                    """
                }
            }
        }

    }
}
