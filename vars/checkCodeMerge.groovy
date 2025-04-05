#!/usr/bin/env groovy

// 检查当前分支 currentBranch 是否包含指定分支 beforeBranch 的提交记录
def call(String beforeBranch, String currentBranch) {
  def beforeBranchCommitHash = sh(
          script: "git rev-parse origin/${beforeBranch}",
          returnStdout: true
  ).trim()
  echo "最后一次提交Hash值为 ${beforeBranch}: ${beforeBranchCommitHash}"

  def notContains = sh(
          script: "git merge-base --is-ancestor  ${beforeBranchCommitHash}  origin/${params.CURRENT_BRANCH}",
          returnStatus: true
  )
  echo "${currentBranch} ${notContains ? '不包含' : '包含'} ${beforeBranch}"
  return !notContains
}