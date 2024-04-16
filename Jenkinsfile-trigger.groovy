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
                        def userInput = input(
                            id: 'userInput',
                            message: 'Do you approve the execution of this job?',
                            parameters: [                     
                                [
                                    $class: 'SubmitterParameterDefinition', // Use SubmitterParameter for user-specific approval
                                    name: 'APPROVE_JOB',
                                    submitter: 'admin', // Specify the Jenkins User ID for approval
                                    defaultValue: 'Proceed', // Default value for the approval button
                                    description: 'Approve this job execution' // Description of the approval parameter
                                ]
                            ]
                        )

                        // Check the user input
                        if (userInput == 'Proceed') {
                            echo "Job approved by user: admin"
                        } else {
                            echo "Job not approved. Exiting the pipeline."
                            currentBuild.result = 'ABORTED'
                            error("Job execution aborted by user.")
                        }
                    }
                }
            }
        }
    }
}
