package com.sakethh.linkora.data.remote.releases

import com.sakethh.linkora.data.remote.releases.model.GitHubReleaseDTOItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class GitHubReleasesImpl @Inject constructor(
    private val ktorClient: HttpClient
) : GitHubReleasesRepo {
    override suspend fun getLatestVersionData(): GitHubReleasesResult {
        return try {
            val latestRelease =
                ktorClient.get("https://api.github.com/repos/sakethpathike/Linkora/releases")
                    .body<List<GitHubReleaseDTOItem>>().first().apply {
                        if (!releaseName.startsWith("v")) {
                            releaseName = "v$releaseName"
                        }
                    }
            GitHubReleasesResult.Success(latestRelease)
        } catch (e: Exception) {
            e.printStackTrace()
            GitHubReleasesResult.Failure("Failed at getLatestVersionData")
        }
    }
}