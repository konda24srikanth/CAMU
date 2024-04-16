pipeline {
    agent any

    stages {
        stage('Trigger Job After Approval') {
            steps {
                mail to: 'konda.srikanth@walkingtree.tech',
                     subject: "Approval Needed: Execute Job",
                     body: "Please approve the execution of the job by clicking the following link: ${env.BUILD_URL}input/"

                timeout(time: 30, unit: 'MINUTES') {
                    script {
                        // Define a choice parameter for approval
                        def userInput = input(
                            id: 'userInput',
                            message: 'Do you approve the execution of this job?',
                            parameters: [
                                choice(choices: ['Yes', 'No'], description: 'Please select Yes to approve or No to reject', name: 'APPROVE_JOB')
                            ]
                        )

                        // Get the Jenkins User ID of the user who triggered the approval
                        def approvedBy = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause).userId

                        // Validate the approval and the user
                        if (userInput == 'Yes' && approvedBy == 'admin1') {
                            echo "Job approved by user: admin"
                        } else {
                            echo "Job not approved. Exiting the pipeline."
                            currentBuild.result = 'ABORTED'
                            error("Job execution aborted by unauthorized user or not approved.")
                        }
                    }
                }
            }
        }
    }
}
