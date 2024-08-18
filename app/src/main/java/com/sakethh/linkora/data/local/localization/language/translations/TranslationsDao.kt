package com.sakethh.linkora.data.local.localization.language.translations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocalizedStrings(translation: List<Translation>)

    @Query("SELECT stringValue FROM translation WHERE stringName=:stringName and languageCode=:languageCode")
    suspend fun getLocalizedStringValueFor(stringName: String, languageCode: String): String?
}