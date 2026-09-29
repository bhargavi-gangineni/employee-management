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
                        docker buildx build --load -t employee-management:1.0 .

                        docker save -o employee-management.tar employee-management:1.0

                        scp -o StrictHostKeyChecking=no -i "%SSH_KEY%" employee-management.tar %SSH_USER%@40.192.16.224:/home/ubuntu/employee-management.tar

                        ssh -o StrictHostKeyChecking=no -i "%SSH_KEY%" %SSH_USER%@40.192.16.224 "docker load -i /home/ubuntu/employee-management.tar && cd /home/ubuntu/employee-management && ansible-playbook -i ansible/inventory ansible/deploy.yml && rm -f /home/ubuntu/employee-management.tar"
                        del /Q employee-management.tar
                    '''
                }
            }
        }
    }
}