package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.data.local.localization.language.translations.Translation

sealed class LocalizationResult {
    data class Success(val data: List<Translation>) : LocalizationResult()
    data class Failure(val message: String) : LocalizationResult()
}