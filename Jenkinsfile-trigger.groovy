pipeline {
    agent any

    stages {
        stage('Trigger Job After Approval') {
            steps {
                // Send approval request email to designated approver
                mail to: 'konda.srikanth@walkingtree.tech',
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

                    // Validate user approval by checking the username of the user who approved
                    def approvedBy = env.APPROVED_BY ?: 'unknown'

                    if (userInput && approvedBy == 'srikanth') {
                        echo "Job approved by user 'admin'. Proceeding with job execution..."
                        // Add job execution steps here
                        // For example:
                        sh 'echo "Executing job..."'
                    } else {
                        echo "Job not approved by user 'admin'. Exiting the pipeline."
                        currentBuild.result = 'ABORTED'
                        error("Job execution aborted or unauthorized.")
                    }
                }
            }
        }
    }
}
