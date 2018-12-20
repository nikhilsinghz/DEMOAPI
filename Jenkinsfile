pipeline{
  agent any
  stages{
      stage('Build'){
         steps{
             sh 'mvn test'
         }
         post{
            success{
                echo 'Now Archiving..'
                archiveArtifacts artifacts: '**/target/*.war'
            }
         }
      }
  }
}
