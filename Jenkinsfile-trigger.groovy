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
                mail to: 'mahaboob.basha@walkingtree.tech',
                     subject: "Approval Needed: Execute Job",
                     body: "Please approve the execution of the job by clicking the following link: ${env.BUILD_URL}input/"

                timeout(time: 30, unit: 'MINUTES') {
                    script {
                        def approvedBy = input(
                            id: 'approvalInput',
                            message: 'Please enter your username for approval verification:',
                            parameters: [
                                string(defaultValue: '', description: 'Username', name: 'APPROVED_BY')
                            ]
                        ).APPROVED_BY

                        if (approvedBy == 'admin') {
                            echo "Job approved by admin. Proceeding with job execution."
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
