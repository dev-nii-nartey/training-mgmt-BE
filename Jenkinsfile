@Library('shared-libraries')

def appName = 'training-management-backend'

def availableServices = ["API-Gateway-Service", "authentication-service", "Cohort-Management-Service", "Email-Service" , "Eureka-Service", "User-Service", "specialization-service", "User-Profile-Service", "curriculum-management-service", "Grading-service", "Live-Quiz", "Assessment-Service"]
def changedServices = []

def runMavenCommand(command) {
    def mvn = tool 'maven'
    try {
        sh "${mvn}/bin/mvn ${command}"
    } catch (Exception e) {
        echo "Maven command '${command}' failed: ${e.getMessage()}"
        throw e
    }
}

def prepareDeploymentFiles(imageRegistry, gitSha, imageName, changedServices) {
    def servicesString = changedServices.collect { "\"$it\"" }.join(' ')
    echo "services string: ${servicesString}"
    sh 'mkdir -p app/'
    sh "cp docker-compose.yml app/"
    sh "cp -r scripts/ app/"
    sh "cp appspec.yml app/"
    sh "sed -i 's|services=()|services=(${servicesString})|g' app/scripts/boot.sh"
    sh "cd app/scripts/ && chmod +x boot.sh after-install.sh before-install.sh"
    for (service in changedServices) {
        def serviceInLower = service.toLowerCase()
        sh "sed -i 's|image: ${imageRegistry}/training-management:${serviceInLower}-.*|image: ${imageRegistry}/${imageName}:${serviceInLower}-${gitSha}|g' app/docker-compose.yml"
    }
}

def awsCreds = [
    region: 'eu-west-1',
    iamCredId: 'aws-cred-training-center'
]

def deployConfig = [
    main: [
        revisionTag: appName,
        revisionLocation: 'gtp-artifacts-2',
        assetsPath: "app/",
        codeDeployAppName: 'gtp',
        codeDeployGroup: appName
    ],

    'dev': [
        revisionTag: appName,
        revisionLocation: 'gtp-artifacts-2',
        assetsPath: appName,
        codeDeployAppName: 'gtp',
        codeDeployGroup: appName
    ],

    'mini_dev': [
        revisionTag: appName,
        revisionLocation: 'gtp-artifacts-2',
        assetsPath: "app/",
        codeDeployAppName: 'gtp',
        codeDeployGroup: appName
    ],
    
]

def sharedBranches = ['main', 'dev', 'mini_dev']

pipeline {
    agent any

    tools {
        maven "maven"
        jdk "jdk21"
    }

    environment {
        currentBranch = "${env.BRANCH_NAME}"
        gitUser = sh(script: 'git log -1 --pretty=format:%ae', returnStdout: true).trim()
        gitSha = sh(script: 'git log -n 1 --pretty=format:"%H"', returnStdout: true).trim()
        imageRegistry = '909544387219.dkr.ecr.eu-west-1.amazonaws.com'
        imageName = 'training-management'
    }

    stages{
        stage('Identify Changed Services') {
            when {
                allOf {
                    expression { sharedBranches.contains(env.BRANCH_NAME) }
                    expression {
                        def isMerge = sh(
                            script: """
                                git log -1 --pretty=%B | grep -i 'merge pull request'
                            """,
                            returnStatus: true
                        ) == 0
                        return isMerge
                    }
                }
            }
            steps {
                script {

                    def lastMergeCommit = sh(
                        script: "git log --merges --pretty=format:'%H' --grep='Merge pull request' -n 2 origin/${currentBranch} | sed -n '2p'",
                        returnStdout: true
                    ).trim()

                    echo "Last merge commit: ${lastMergeCommit}..."

                    detectedChanges = sh(
                        script: "git checkout -f ${currentBranch} >/dev/null 2>&1 && git diff --name-only ${lastMergeCommit}...HEAD | sort -u"
                    , returnStdout: true).trim()

                    echo "Detected changes: ${detectedChanges}.."

                    def getServiceNames = { String changes ->
                        return changes.split('\n').collect { change ->
                            def matcher = change =~ /^([^\/]+)/
                            matcher.find() ? matcher.group(1) : null
                        }.findAll { it != null }.unique()
                    }

                    changedServices = getServiceNames(detectedChanges).findAll { service ->
                        availableServices.contains(service)
                    }

                    if (changedServices) {
                        echo "Changes detected in services: ${changedServices}"
                    } else {
                        echo "No changes detected in available services."
                    }

                    echo "Changed services to build: ${changedServices}"
                }
            }
        }
        stage('mvn Clean') {
            steps{
                script {
                    changedServices.each { service ->
                    dir("${service}"){
                        try {
                            runMavenCommand('clean')
                        } catch (Exception e) {
                            echo "Maven clean failed: ${e.getMessage()}"
                        }
                    }
                  }
                }
            }
        }

       stage('mvn Validate') {
          steps{
            script {
              changedServices.each { service ->
                dir("${service}"){
                    try {
                        echo "Validating ${service}..."
                        runMavenCommand('validate')
                    } catch (Exception e) {
                        echo "Maven validate failed: ${e.getMessage()}"
                    }
                }
              }
            }
          }
        }

        stage('Build Changed Service images') {
            when {
              expression { return sharedBranches.contains(env.BRANCH_NAME) }
            }
            steps {
                script {
                    changedServices.each { service ->
                        stage("Build ${service}") {
                            def serviceInLower = service.toLowerCase()
                            try {
                                echo "Building ${service}..."
                                buildDockerImage(
                                    imageTag: "${imageRegistry}/${imageName}:${serviceInLower}-${gitSha}", 
                                    buildContext: "./${service}"
                                )
                            } catch (Exception e) {
                                echo "Build for ${service} failed: ${e.message}"
                            }
                        }
                    }
                }

            }
        }

        stage('Push Changed Service images To ECR') {
            when {
              expression { return sharedBranches.contains(env.BRANCH_NAME) }
            }
            steps {
                script {
                    changedServices.each { service ->
                        stage("Build ${service}") {
                            def serviceInLower = service.toLowerCase()
                            try {
                                echo "Pushing ${service}..."
                                pushDockerImage(image: "${imageRegistry}/${imageName}:${serviceInLower}-${gitSha}", registry: imageRegistry, awsCreds: awsCreds)
                            } catch (Exception e) {
                                echo "Push for ${service} failed: ${e.message}"
                            }
                        }
                    }
                }
            }
        }

        stage('Prepare Deploy Changed Services') {
            when {
              expression { return sharedBranches.contains(env.BRANCH_NAME) }
            }
            steps {
                script {
                    prepareDeploymentFiles(imageRegistry, gitSha, imageName, changedServices)
                    prepareToDeployECR(environment: currentBranch, deploymentConfig: deployConfig, awsCreds: awsCreds)
                }
            }
        }

        stage('Deploying Changed Services') {
                    when {
                        expression { return sharedBranches.contains(env.BRANCH_NAME) }
                    }
                    steps {
                        script {
                            makeDeploymentECR(environment: currentBranch, deploymentConfig: deployConfig, awsCreds: awsCreds)
                        }
                    }
                }
            }
    

    post {
            always {
                echo "Pipeline execution completed."
                script {
                try {
                    changedServices.each { service ->
                    def serviceInLower = service.toLowerCase()
                    sh "docker rmi ${imageRegistry}/${imageName}:${serviceInLower}-${gitSha} || true"
                    }
                    sh 'docker system prune -f'
                    cleanWs()
                } catch (Exception e) {
                    echo "Cleanup failed: ${e.getMessage()}"
                }
                }
            }
            success {
                echo "Successfull :)"
            }
            failure {
                echo "Failed :("
            }
        }    
}
