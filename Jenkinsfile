pipeline {
    agent any

    tools {
        maven 'Maven-3.9.16'
    }

    stages {
        stage('Build and Test') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Deploy to AWS EC2') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'ec2-employee-management',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    bat '''
                        @echo on
                        set "DOCKER_EXE=C:/Users/suneel chowdary/AppData/Local/Programs/DockerDesktop/resources/bin/docker.exe"

                        icacls "%SSH_KEY%" /inheritance:r /grant:r "SYSTEM:F" || exit /b 1

                        "%DOCKER_EXE%" info || exit /b 1
                        "%DOCKER_EXE%" build -t employee-management:1.0 . || exit /b 1
                        "%DOCKER_EXE%" save -o employee-management.tar employee-management:1.0 || exit /b 1

                        scp -o StrictHostKeyChecking=no -i "%SSH_KEY%" employee-management.tar %SSH_USER%@40.192.16.224:/home/ubuntu/employee-management.tar || exit /b 1

                        ssh -o StrictHostKeyChecking=no -i "%SSH_KEY%" %SSH_USER%@40.192.16.224 "docker load -i /home/ubuntu/employee-management.tar && cd /home/ubuntu/employee-management && ansible-playbook -i ansible/inventory ansible/deploy.yml && rm -f /home/ubuntu/employee-management.tar" || exit /b 1

                        del /Q employee-management.tar
                    '''
                }
            }
        }
    }
}