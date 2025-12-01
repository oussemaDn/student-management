pipeline {

    agent any // Tells Jenkins where to run the pipeline (e.g., on any available agent)



    environment {

        DOCKER_HUB_CREDENTIALS = 'dockerhub-push-id' 

        SONAR_TOKEN_ID = 'sonarqube-analysis-token'

        

        IMAGE_NAME = 'oussema2025/student-management'

        IMAGE_TAG = 'latest'

        PROJECT_KEY = 'Student-Management-App'

        WSL_IP = '172.28.8.70' 

    }



    stages {

        stage('Build Project') {

            steps {

                echo 'Building Java project and packaging JAR...'

                sh 'mvn clean package -DskipTests' 

            }

        }

        

        stage('SonarQube Analysis') {

            steps {

                script {

                    echo 'Starting SonarQube Analysis...'

                    withCredentials([string(credentialsId: env.SONAR_TOKEN_ID, variable: 'SONAR_TOKEN')]) {

                        sh "mvn sonar:sonar \

                            -Dsonar.projectKey=${env.PROJECT_KEY} \

                            -Dsonar.host.url=http://${env.WSL_IP}:9000 \

                            -Dsonar.token=${SONAR_TOKEN}"

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
