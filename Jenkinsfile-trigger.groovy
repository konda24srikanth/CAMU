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

