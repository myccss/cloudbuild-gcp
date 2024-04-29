#!groovy

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'gcloud builds submit --config cloudbuild.yaml --source-directory ./'
            }
        }
    }
}
