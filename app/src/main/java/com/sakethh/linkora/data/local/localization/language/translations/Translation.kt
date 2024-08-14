package com.sakethh.linkora.data.local.localization.language.translations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("translation")
data class Translation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val languageCode: String,
    val stringName: String,
    val stringValue: String
)