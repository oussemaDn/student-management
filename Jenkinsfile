pipeline {
    agent any // Use any available Jenkins agent

    environment {
        // --- CREDENTIAL IDs (Must match Jenkins Credentials -> IDs) ---
        DOCKER_HUB_CREDENTIALS = 'dockerhub-push-id' 
        SONAR_TOKEN_ID = 'sonarqube-analysis-token'
        
        // --- IMAGE & PROJECT CONFIGURATION (Must match your accounts) ---
        IMAGE_NAME = 'oussema2025/student-management'
        IMAGE_TAG = 'latest'
        PROJECT_KEY = 'Student-Management-App' // Must match your SonarQube Project Key
        
        // --- SERVER ADDRESSES (Must be the current IP) ---
        WSL_IP = '172.28.8.70' // <-- VERIFY THIS IP ADDRESS!
    }

    stages {
        // 1. COMPILE: Builds the Java project and creates the JAR file
        stage('Build Project') {
            steps {
                echo 'Building Java project and packaging JAR...'
                sh 'mvn clean package -DskipTests' 
            }
        }
        
        // 2. SCAN: Analyzes the source code using the SonarQube Scanner
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Starting SonarQube Analysis...'
                    // Securely retrieves the SonarQube Token to authenticate the analysis
                    withCredentials([string(credentialsId: env.SONAR_TOKEN_ID, variable: 'SONAR_TOKEN')]) {
                        sh "mvn sonar:sonar \
                            -Dsonar.projectKey=${env.PROJECT_KEY} \
                            -Dsonar.host.url=http://${env.WSL_IP}:9000 \
                            -Dsonar.token=${SONAR_TOKEN}"
                    }
                }
            }
        }

        // 3. BUILD DOCKER: Creates the final application image
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
            }
        }
        
        // 4. PUSH: Authenticates and sends the image to Docker Hub
        stage('Push to DockerHub') {
            steps {
                script {
                    echo "Pushing image to DockerHub..."
                    // Securely retrieves Docker Hub credentials for login
                    withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        sh "docker logout"
                    }
                }
            }
        }
    }
}
