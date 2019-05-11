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
            sh "./gradlew clean --stacktrace"
            sh "./gradlew build publish curseforge --refresh-dependencies --stacktrace"

            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
         }
      }
   }
}
