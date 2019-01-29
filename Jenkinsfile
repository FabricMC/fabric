pipeline {
   agent any
   stages {

      stage ('Build') {
         when {
            branch 'master'
         }
         steps {
            sh "rm -rf build/libs/"
            sh "chmod +x gradlew"
            sh "./gradlew build uploadArchives curseforge --refresh-dependencies --stacktrace"

            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
         }
      }
   }
}