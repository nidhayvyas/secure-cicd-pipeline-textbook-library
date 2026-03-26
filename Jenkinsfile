pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'OpenJDK 23'
    }

    environment {
        AWS_DEFAULT_REGION = 'us-east-1'
        TF_IN_AUTOMATION = 'true'
        IMAGE_NAME = 'nidhay/textbook-library'
        IMAGE_TAG = "${BUILD_NUMBER}"
        DEP_CHECK_DATA = "${JENKINS_HOME}/dependency-check-data"
        PATH = "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Running Maven build...'
                sh 'mvn clean compile'
            }
        }

        stage('Dependency Scanning Parallel') {
            parallel {
                stage('OWASP Dependency Check') {
                    steps {
                        echo 'Running OWASP Dependency Check...'
                        sh 'mkdir -p "$DEP_CHECK_DATA"'
                        withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
                            sh '''
                                mvn -B org.owasp:dependency-check-maven:check \
                                  -DfailBuildOnCVSS=9 \
                                  -DnvdApiKey="$NVD_API_KEY" \
                                  -DdataDirectory="$DEP_CHECK_DATA" \
                                  -Dformats=HTML,XML
                            '''
                        }
                    }
                    post {
                        always {
                            stash name: 'dependency-check-reports', includes: 'target/dependency-check-report.*', allowEmpty: true
                        }
                    }
                }

                stage('Maven Dependency Audit') {
                    steps {
                        echo 'Running Maven dependency audit...'
                        sh 'mvn versions:display-dependency-updates'
                    }
                }
            }
        }

        stage('Publish Dependency Check Results') {
            steps {
                echo 'Publishing dependency check reports...'
                unstash 'dependency-check-reports'
                dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target',
                    reportFiles: 'dependency-check-report.html',
                    reportName: 'OWASP Dependency Check Report'
                ])
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo 'Running integration tests...'
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'mvn failsafe:integration-test failsafe:verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                echo 'Generating JaCoCo coverage report...'
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'mvn jacoco:report'
                }
            }
            post {
                always {
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=textbook-library \
                              -Dsonar.projectName="SE Textbook Library" \
                              -Dsonar.java.binaries=target/classes \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -Dsonar.token="$SONAR_TOKEN"
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo 'Waiting for SonarQube quality gate result...'
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn -DskipTests package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Check Docker') {
            steps {
                echo 'Checking Docker availability...'
                sh '''
                    echo "PATH=$PATH"
                    which docker || true
                    /usr/local/bin/docker --version || true
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        export PATH=/usr/local/bin:$PATH
                        /usr/local/bin/docker --version
                        echo "$DOCKER_PASS" | /usr/local/bin/docker login -u "$DOCKER_USER" --password-stdin
                        /usr/local/bin/docker build -t $IMAGE_NAME:$IMAGE_TAG .
                        /usr/local/bin/docker tag $IMAGE_NAME:$IMAGE_TAG $IMAGE_NAME:latest
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo 'Pushing Docker image to DockerHub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        export PATH=/usr/local/bin:$PATH
                        /usr/local/bin/docker --version
                        echo "$DOCKER_PASS" | /usr/local/bin/docker login -u "$DOCKER_USER" --password-stdin
                        /usr/local/bin/docker push $IMAGE_NAME:$IMAGE_TAG
                        /usr/local/bin/docker push $IMAGE_NAME:latest
                    '''
                }
            }
        }

        stage('Check Terraform and AWS') {
            steps {
                echo 'Checking Terraform and AWS availability...'
                sh '''
                    echo "PATH=$PATH"
                    which terraform || true
                    which aws || true
                    /usr/local/bin/terraform version || true
                    /usr/local/bin/aws --version || true
                '''
            }
        }

        stage('Terraform Provisioning') {
            steps {
                echo 'Provisioning infrastructure with Terraform...'
                withCredentials([usernamePassword(
                    credentialsId: 'aws-creds',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    dir('terraform') {
                        sh '''
                            export PATH=/usr/local/bin:$PATH
                            /usr/local/bin/terraform init
                            /usr/local/bin/terraform validate
                            /usr/local/bin/terraform plan -out=tfplan
                            /usr/local/bin/terraform apply -auto-approve tfplan
                        '''
                    }
                }
            }
        }

        stage('Automated Deployment') {
            steps {
                echo 'Deploying application to EC2...'
                withCredentials([usernamePassword(
                    credentialsId: 'aws-creds',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    dir('terraform') {
                        script {
                            def ec2PublicIp = sh(
                                script: '/usr/local/bin/terraform output -raw ec2_public_ip',
                                returnStdout: true
                            ).trim()

                            echo "EC2 Public IP: ${ec2PublicIp}"

                            sshagent(['ec2-ssh-key']) {
                                sh """
                                    ssh -o StrictHostKeyChecking=no ec2-user@${ec2PublicIp} '
                                        sudo yum install -y docker || true
                                        sudo systemctl enable docker || true
                                        sudo systemctl start docker || true

                                        sudo docker pull ${IMAGE_NAME}:latest
                                        sudo docker stop textbook-library || true
                                        sudo docker rm textbook-library || true
                                        sudo docker run -d --name textbook-library -p 3001:3001 ${IMAGE_NAME}:latest
                                    '
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                echo 'Validating deployment...'
                withCredentials([usernamePassword(
                    credentialsId: 'aws-creds',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    dir('terraform') {
                        script {
                            def ec2PublicIp = sh(
                                script: '/usr/local/bin/terraform output -raw ec2_public_ip',
                                returnStdout: true
                            ).trim()

                            sh "curl -I http://${ec2PublicIp}:3001 || true"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
        always {
            cleanWs()
        }
    }
}
