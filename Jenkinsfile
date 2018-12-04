def version = "${env.BUILD_NUMBER}"
def REPONAME = "${scm.getUserRemoteConfigs()[0].getUrl()}"

node {

    
    stage('clean workspace') {
        cleanWs()
    }

    stage('Clone sources') {
      git(
           url: "${REPONAME}",
           credentialsId: 'username***REMOVED***-github-automation-uk-gov-dft',
           branch: "${BRANCH_NAME}"
        )
     }

    stage ('Gradle build') {
        // Set Environment Vairable if the CI env variable is set.
        script {
            env.SPRING_APPLICATION_JSON = '{"spring":{"datasource":{"url":"jdbc:postgresql://postgresql:5432/bb_dev?currentSchema=usermanagement"}}}'
        }
        try {
            sh './gradlew --no-daemon --profile --configure-on-demand clean build bootJar artifactoryPublish artifactoryDeploy'
            sh 'mv build/reports/profile/profile-*.html build/reports/profile/index.html'
        }
        finally {
            junit '**/TEST*.xml'
        }
        publishHTML (target: [
          allowMissing: false,
          alwaysLinkToLastBuild: false,
          keepAll: true,
          reportDir: 'build/reports/profile',
          reportFiles: 'index.html',
          reportName: "Gradle Profile Report"
        ])
    }

    stage ('OWASP Dependency Check') {
        sh './gradlew dependencyCheckUpdate dependencyCheckAggregate'

        publishHTML (target: [
         allowMissing: false,
         alwaysLinkToLastBuild: false,
         keepAll: true,
         reportDir: 'build/reports',
         reportFiles: 'dependency-check-report.html',
         reportName: "OWASP Dependency Check"
        ])
    }

    stage('SonarQube analysis') {

        withSonarQubeEnv('sonarqube') {
            def ver = readFile('VERSION').trim()
            echo "Version: " + ver
            // requires SonarQube Scanner for Gradle 2.1+
            // It's important to add --info because of SONARJNKNS-281
            sh "./gradlew --info sonarqube -Dsonar.projectName=authorisation-service -Dsonar.projectVersion=${ver} -Dsonar.branch=${BRANCH_NAME}"
        }
    }

    stage("Quality Gate") {
        timeout(time: 5, unit: 'MINUTES') {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
    }
    stage("Acceptance Tests") {
        node('Functional') {
            git(
               url: "${REPONAME}",
               credentialsId: 'dft-buildbot-valtech',
               branch: "${BRANCH_NAME}"
            )

            timeout(time: 10, unit: 'MINUTES') {
                try {
                    sh 'bash -c "echo $PATH && cd acceptance-tests && ./run-regression.sh"'
                }
                finally {
                    junit '**/TEST*.xml'
                }
            }
        }
    }
}
