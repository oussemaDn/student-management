pipeline {
    // 1. Agent: Specifies where the pipeline will run (your Jenkins master/agent)
    agent any

    // 2. Environment: Defines variables and credentials used throughout the pipeline
    environment {
        // --- CREDENTIAL IDs (These MUST match the IDs in Jenkins Credentials) ---
        DOCKER_HUB_CREDENTIALS = 'dockerhub-push-id'
        SONAR_TOKEN_ID = 'sonarqube-analysis-token'
        
        // --- IMAGE & PROJECT CONFIGURATION ---
        IMAGE_NAME = 'oussema2025/student-management'
        IMAGE_TAG = 'latest'
        PROJECT_KEY = 'Student-Management-App' // Your SonarQube Project Key
        
        // --- SERVER ADDRESSES (CRITICAL: Confirm this IP is still correct) ---
        WSL_IP = '172.28.8.70' 
    }

    // 3. Stages: Defines the sequence of work
    stages {
        
        // STAGE 1: COMPILE PROJECT
        stage('Build Project') {
            steps {
                echo 'Building Java project and packaging JAR...'
                sh 'mvn clean package -DskipTests' 
            }
        }
        
        // STAGE 2: SONARQUBE ANALYSIS
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Starting SonarQube Analysis...'
                    // Uses the SonarQube token credential securely
                    withCredentials([string(credentialsId: env.SONAR_TOKEN_ID, variable: 'SONAR_TOKEN')]) {
                        sh "mvn sonar:sonar \
                            -Dsonar.projectKey=${env.PROJECT_KEY} \
                            -Dsonar.host.url=http://${env.WSL_IP}:9000 \
                            -Dsonar.token=${SONAR_TOKEN}"
                    }
                }
            }
        }

        // STAGE 3: BUILD DOCKER IMAGE
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                // The '.' command tells Docker to use the Dockerfile in the current directory
                sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
            }
        }
        
        // STAGE 4: PUSH TO DOCKER HUB
        stage('Push to DockerHub') {
            steps {
                script {
                    echo "Pushing image to DockerHub..."
                    // Uses the Docker Hub credentials securely for login
                    withCredentials([usernamePassword(credentialsId: env.DOCKER_HUB_CREDENTIALS, passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                        sh "docker logout" // Always log out after pushing
                    }
                }
            }
        }
        
    } // This closing brace MUST be here to contain all stages.
} // This closing brace MUST be the very last line to end the pipeline.
