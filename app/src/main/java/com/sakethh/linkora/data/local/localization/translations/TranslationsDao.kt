package com.sakethh.linkora.data.local.localization.translations

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TranslationsDao {
    @Insert
    suspend fun addANewLocalizedString(translation: Translation)

    @Delete
    suspend fun deleteAnExistingLocalizedString(translation: Translation)

    @Query("DELETE FROM translation WHERE id=:id")
    suspend fun deleteAnExistingLocalizedString(id: Long)

    @Query("SELECT stringValue FROM translation WHERE stringName=:stringName and languageCode=:languageCode")
    suspend fun getLocalizedStringValueFor(stringName: String, languageCode: String): String
}