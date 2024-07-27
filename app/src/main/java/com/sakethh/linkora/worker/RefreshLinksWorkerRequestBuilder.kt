package com.sakethh.linkora.worker

import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import java.util.UUID
import javax.inject.Inject

class RefreshLinksWorkerRequestBuilder @Inject constructor(private val workManager: WorkManager) {
    companion object {
        val REFRESH_LINKS_WORKER_TAG: UUID = UUID.fromString("d267865d-e1c9-42b7-be38-1ab6db0e312b")
    }

    fun request(): OneTimeWorkRequest {
        Log.d("Linkora Log", "OneTimeWorkRequestBuilder")
        val request = OneTimeWorkRequestBuilder<RefreshLinksWorker>()
            .setId(REFRESH_LINKS_WORKER_TAG)
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        Log.d(
            "Linkora Log",
            workManager.getWorkInfoById(REFRESH_LINKS_WORKER_TAG).get().state.isFinished.toString()
        )
        workManager.enqueue(request)
        Log.d(
            "Linkora Log",
            workManager.getWorkInfoById(REFRESH_LINKS_WORKER_TAG).get().state.isFinished.toString()
        )
        return request
    }
}