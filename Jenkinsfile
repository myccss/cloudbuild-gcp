#!groovy

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'gcloud builds submit --config cloudbuild.yaml --git-source-dir "https://github.com/owner/repo" --git-source-revision=main'
            }
        }
    }
}
