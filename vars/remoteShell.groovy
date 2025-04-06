#!/usr/bin/env groovy

def call(String remote_shell, String servers_credentials_id) {
    withCredentials([sshUserPrivateKey(credentialsId: "$SERVERS_CREDENTIALS_ID", usernameVariable: 'REMOTE_USER', keyFileVariable: 'SSH_KEY_FILE')]) {
        sh("ssh -oStrictHostKeyChecking=no -i $SSH_KEY_FILE $REMOTE_USER@${server.host} \"${remote_shell}\"")
    }
}