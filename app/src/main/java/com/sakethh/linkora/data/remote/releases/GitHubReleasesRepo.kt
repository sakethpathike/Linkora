package com.sakethh.linkora.data.remote.releases


interface GitHubReleasesRepo {
    suspend fun getLatestVersionData(): GitHubReleasesResult
}