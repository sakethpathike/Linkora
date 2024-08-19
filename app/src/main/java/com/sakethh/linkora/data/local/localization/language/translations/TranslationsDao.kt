package com.sakethh.linkora.data.local.localization.language.translations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocalizedStrings(translation: List<Translation>)

    @Query("SELECT EXISTS(SELECT * FROM translation WHERE languageCode = :languageCode)")
    suspend fun doesStringsPackForThisLanguageExists(languageCode: String): Boolean

    @Query("DELETE FROM translation WHERE languageCode=:languageCode")
    suspend fun deleteAllLocalizedStringsForThisLanguage(languageCode: String)

    @Query("SELECT stringValue FROM translation WHERE stringName=:stringName and languageCode=:languageCode")
    suspend fun getLocalizedStringValueFor(stringName: String, languageCode: String): String?
}