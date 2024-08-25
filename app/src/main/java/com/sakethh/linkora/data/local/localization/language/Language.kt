package com.sakethh.linkora.data.local.localization.language

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("language")
data class Language(
    @PrimaryKey
    val languageCode: String,
    val languageName: String,
    val localizedStringsCount: Int,
    val contributionLink: String
)