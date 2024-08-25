package com.sakethh.linkora.data.remote.releases

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.remote.releases.model.GitHubReleaseDTOItem


interface GitHubReleasesRepo {
    suspend fun getLatestVersionData(): RequestResult<GitHubReleaseDTOItem>
}