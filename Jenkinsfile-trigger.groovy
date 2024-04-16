pipeline {
    agent any

    stages {
        stage('Trigger Job After Approval') {
            steps {
                script {
                    // Define the designated approver's email
                    def designatedApproverEmail = 'konda.srikanth@walkingtree.tech'

                    // Send approval email only to the designated approver
                    mail to: designatedApproverEmail,
                        subject: "Approval Needed: Execute Job",
                        body: "Please approve the execution of the job by clicking the following link: ${env.BUILD_URL}input/"

                    // Wait for approval
                    timeout(time: 30, unit: 'MINUTES') {
                        // Wait for user input
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

                        // Validate user approval and email
                        if (userInput == true && env.CHANGE_AUTHOR_EMAIL == designatedApproverEmail) {
                            echo "Job approved by designated approver."
                            // Add job execution steps here
                            // For example:
                            sh 'echo "Executing job..."'
                        } else {
                            echo "Job not approved or unauthorized. Exiting the pipeline."
                            currentBuild.result = 'ABORTED'
                            error("Job execution aborted or unauthorized.")
                        }
                    }
                }
            }
        }
    }
}
