pipeline {
    agent any

    environment {
        // --- Credentials IDs in Jenkins ---
        DOCKER_HUB_CREDENTIALS = 'dockerhub-push-id'
        SONAR_TOKEN_ID = 'sonarqube-analysis-token'
        GITHUB_CREDENTIALS = 'github-token'
        KUBE_CONFIG_ID = 'jenkins-kubeconfig'

        // --- Image & project config ---
        IMAGE_NAME = 'oussema2025/student-management'
        IMAGE_TAG = 'latest'
        PROJECT_KEY = 'Student-Management-App'

        // --- Server addresses ---
        WSL_IP = '172.28.8.70' // SonarQube server IP
        K8S_NAMESPACE = 'devops'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du projet depuis GitHub...'
                git branch: 'main',
                    url: 'https://github.com/oussemaDn/student-management.git',
                    credentialsId: env.GITHUB_CREDENTIALS
            }
        }

        stage('Build Project') {
            steps {
                echo 'Build du projet Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Starting SonarQube Analysis...'
                    withCredentials([string(credentialsId: env.SONAR_TOKEN_ID, variable: 'SONAR_TOKEN')]) {
                        sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${env.PROJECT_KEY} \
                            -Dsonar.host.url=http://${env.WSL_IP}:9000 \
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    echo "Pushing image to DockerHub..."
                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_HUB_CREDENTIALS,
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    echo "Déploiement de l'application sur Kubernetes..."
                    withKubeConfig([credentialsId: env.KUBE_CONFIG_ID, namespace: env.K8S_NAMESPACE]) {
                        // Déploiement MySQL
                        sh 'kubectl apply -f mysql-deployment.yaml'
                        sh 'kubectl apply -f spring-deployment.yaml'

                        // Vérification
                        sh 'kubectl get pods -n ${K8S_NAMESPACE}'
                        sh 'kubectl get svc -n ${K8S_NAMESPACE}'
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline exécutée avec succès !'
        }
        failure {
            echo 'Pipeline échouée. Vérifiez les logs.'
        }
    }
}
