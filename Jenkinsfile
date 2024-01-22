pipeline {
    agent any
    
    environment {
        JENKINS_SSH_KEY = '/var/lib/jenkins/.ssh/id_rsa'
        REMOTE_USER = 'mohcineboudenjal'
        REMOTE_HOST = 'production-server'
        REMOTE_PATH = '/home/mohcineboudenjal/smartassurance/prod'
        JAR_NAME = 'microservice-client'  // Replace with your actual jar name
    }

    stages {
        stage('Connect SSH to remote and create directory of jar file with dockerfile') {
            steps {
                script {
                    // Connect to the production server using SSH
                    sh """
                        ssh -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} '
                            mkdir -p ${REMOTE_PATH}/${JAR_NAME} &&
                            echo \"FROM openjdk:17-alpine\" > ${REMOTE_PATH}/${JAR_NAME}/Dockerfile &&
                            echo \"ARG JAR_FILE=${JAR_NAME}-0.0.1-SNAPSHOT.jar\" >> ${REMOTE_PATH}/${JAR_NAME}/Dockerfile &&
                            echo \"WORKDIR /opt/app\" >> ${REMOTE_PATH}/${JAR_NAME}/Dockerfile &&
                            echo \"COPY ${JAR_NAME}-0.0.1-SNAPSHOT.jar app.jar\" >> ${REMOTE_PATH}/${JAR_NAME}/Dockerfile &&
                            echo 'ENTRYPOINT ["java","-jar","app.jar"]' >> ${REMOTE_PATH}/${JAR_NAME}/Dockerfile
                        '
                    """
                }
            }
        }

        stage('Build and Copy JAR file into directory') {
            steps {
                script {
                    // Build the app locally
                    sh "mvn clean install"

                    // Copy the JAR to the remote path
                    sh "scp -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no /var/lib/jenkins/workspace/${JAR_NAME}/target/${JAR_NAME}-0.0.1-SNAPSHOT.jar ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PATH}/${JAR_NAME}"
                }
            }
        }
// stage('reload docker images') {
//     steps {
//         script {
//             // Reconnect to SSH
//             sh "ssh -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH}'"

//             // Remove existing containers
//             sh "ssh -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH} && docker rm -f \$(docker ps -aq)'"

//             // Run Docker Compose
//             sh "ssh -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH} && docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v \"/home/mohcineboudenjal/smartassurance/prod:/home/mohcineboudenjal/smartassurance/prod\" -w=\"/home/mohcineboudenjal/smartassurance/prod\" docker/compose:1.25.5 up'"
//         }
//     }
// }
stage('reload docker images') {
    steps {
        script {
            // Reconnect to SSH
            sh "ssh -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH}'"

            // Remove existing containers
            def containers = sh(script: 'docker ps -a -q', returnStdout: true).trim()
            if (containers) {
                sh "docker rm $containers"
            }

            // Run Docker Compose
            sh "ssh -i ${JENKINS_SSH_KEY} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'cd ${REMOTE_PATH} && docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v \"/home/mohcineboudenjal/smartassurance/prod:/home/mohcineboudenjal/smartassurance/prod\" -w=\"/home/mohcineboudenjal/smartassurance/prod\" docker/compose:1.25.5 up'"
        }
    }
}


}
}
