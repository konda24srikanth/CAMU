pipeline {
    agent any

    options {
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5'))
    }

    stages {
        stage('Checkout the Code') {
            steps {
                echo "git clone"
                // Add git checkout steps here if needed
                // git branch: 'test-k8s', credentialsId: 'Git-Token', url: 'https://github.com/Akieni-Yao/dms.git'
            }
        }

        stage('Trigger Job After Approval') {
            steps {
                // Send approval request email to konda.srikanth
                mail to: 'konda.srikanth@walkingtree.tech',
                     subject: "Approval Needed: Execute Job",
                     body: "Please approve the execution of the job by replying to this email with 'APPROVE' in the subject line."

                // Wait for approval via email
                timeout(time: 30, unit: 'MINUTES') {
                    script {
                        def approved = waitForApproval()

                        if (approved) {
                            echo "Job approved by konda srikanth. Proceeding with job execution."
                            // Add job execution steps here
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

def waitForApproval() {
    // Implement logic to wait for email approval from konda srikanth
    def approvalReceived = false

    // Continuously check for approval email
    // In a real implementation, use a proper mechanism to monitor and process incoming emails
    // For demonstration, simulate the approval check based on manual input or external system integration
    // Example: Monitor a specific mailbox or webhook for approval responses
    // Here, use a simple manual input simulation (replace with actual email processing logic)
    def userInput = input(
        id: 'approvalInput',
        message: 'Has konda srikanth approved the job? (Type "yes" to simulate approval)',
        parameters: [
            string(defaultValue: '', description: 'Approval', name: 'APPROVAL')
        ]
    ).APPROVAL.trim().toLowerCase()

    if (userInput == 'yes') {
        approvalReceived = true
    }

    return approvalReceived
}
