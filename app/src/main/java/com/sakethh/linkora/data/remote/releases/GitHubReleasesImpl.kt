package com.sakethh.linkora.data.remote.releases

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.remote.releases.model.GitHubReleaseDTOItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class GitHubReleasesImpl @Inject constructor(
    private val ktorClient: HttpClient
) : GitHubReleasesRepo {
    override suspend fun getLatestVersionData(): RequestResult<GitHubReleaseDTOItem> {
        return try {
            val latestRelease =
                ktorClient.get("https://api.github.com/repos/sakethpathike/Linkora/releases")
                    .body<List<GitHubReleaseDTOItem>>().first().apply {
                        if (!releaseName.startsWith("v")) {
                            releaseName = "v$releaseName"
                        }
                    }
            RequestResult.Success(latestRelease)
        } catch (e: Exception) {
            e.printStackTrace()
            RequestResult.Failure("Failed at getLatestVersionData")
        }
    }
}