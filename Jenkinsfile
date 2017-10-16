library 'git-commit-version-number'


pipeline {
    agent { label 'master' }
    stages {
        def MY_VERSION=gitCommitVersion{}
        stage('Prepare') {
            steps {
                echo "Version is: ${env.VERSION}"
            }
        }
        stage('Build') {
            agent { 
                dockerfile { 
                    label 'docker' 
                    additionalBuildArgs '--build-arg VERSION=${env.VERSION}'
                } 
            }
            steps {
                archive includes: 'build/libs/*.jar'
            }
        }
    }
}

