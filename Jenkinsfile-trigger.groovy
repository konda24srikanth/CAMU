pipeline {
    agent any
options {
  buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
}
    stages {
        stage('Checkout the Code') {
            steps {
               git branch: 'test-k8s', credentialsId: 'Git-Token', url: 'https://github.com/Akieni-Yao/dms.git'
            }
        }
 stage('Trigger Job After Approval') {
            steps {
                mail to: 'mahaboob.basha@walkingtree.tech',
                     subject: "Approval Needed: Execute Job",
                     body: "Please approve the execution of the job by clicking the following link: ${env.BUILD_URL}input/"


                timeout(time: 30, unit: 'MINUTES') {

                    script {
                        def userInput = input(
                            id: 'userInput',
                            message: 'Do you approve the execution of this job?',
                            parameters: [
                                [
                                    $class: 'BooleanParameterDefinition',
                                    defaultValue: false,
                                    description: 'Approve?',
                                    name: 'APPROVE_JOB'
                                ]
                            ]
                        )


                        if (userInput) {
                            echo "Job approved by user."

                        } else {
                            echo "Job not approved. Exiting the pipeline."
                            currentBuild.result = 'ABORTED'
                            error("Job execution aborted by user.")
                        }
                    }
                }
            }
        }