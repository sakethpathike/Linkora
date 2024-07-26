package com.sakethh.linkora.data.remote.releases

import com.sakethh.linkora.data.remote.releases.model.GitHubReleaseDTOItem

sealed class GitHubReleasesResult {
    data class Success(val data: GitHubReleaseDTOItem) : GitHubReleasesResult()
    data class Failure(val message: String) : GitHubReleasesResult()
}