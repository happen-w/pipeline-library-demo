#!/usr/bin/env groovy

def call(Object server, String servers_credentials_id, String remote_shell) {
    withCredentials([sshUserPrivateKey(credentialsId: "$servers_credentials_id", usernameVariable: 'REMOTE_USER', keyFileVariable: 'SSH_KEY_FILE')]
            ,string(credentialsId: "$remote_shell", variable: 'REMOTE_SHELL')
        ) {
        sh("ssh -oStrictHostKeyChecking=no -i $SSH_KEY_FILE $REMOTE_USER@${server.host} \"${REMOTE_SHELL}\"")
    }
}