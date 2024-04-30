#!groovy

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'gcloud  auth activate-service-account   --key-file=/tmp/cloud-sa.json'
                sh 'gcloud builds submit --config cloudbuild.yaml  --no-source'
            }
        }
    }
}
