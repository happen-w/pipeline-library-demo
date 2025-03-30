#!/usr/bin/env groovy

// 定义函数
def call(String beforeBranch, String currentBranch) {
  def beforeBranchCommitHash = sh(
          script: "git rev-parse origin/${beforeBranch}",
          returnStdout: true
  ).trim()
  echo "Last commit on ${beforeBranch}: ${beforeBranchCommitHash}"

  def contains = sh(
          script: "git merge-base --is-ancestor  ${beforeBranchCommitHash}  origin/${params.CURRENT_BRANCH}  && echo true || echo false",
          returnStdout: true
  ).trim().toBoolean()
  echo "${currentBranch} ${contains ? 'contains' : 'not contains'} ${beforeBranch}"
  return contains
}