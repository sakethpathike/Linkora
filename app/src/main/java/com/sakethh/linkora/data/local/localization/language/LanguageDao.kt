package com.sakethh.linkora.data.local.localization.language

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {
    @Insert
    suspend fun addANewLanguage(language: Language)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewLanguages(languages: List<Language>)

    @Delete
    suspend fun deleteALanguage(language: Language)

    @Query("DELETE FROM language WHERE languageName=:languageName")
    suspend fun deleteALanguage(languageName: String)

    @Query("DELETE FROM language WHERE languageCode=:languageCode")
    suspend fun deleteALanguageBasedOnLanguageCode(languageCode: String)


    @Query("SELECT languageName FROM language WHERE languageCode=:languageCode")
    suspend fun getLanguageNameForTheCode(languageCode: String): String

    @Query("SELECT languageCode FROM language WHERE languageName=:languageName")
    suspend fun getLanguageCodeForTheLanguageNamed(languageName: String): String

    @Query("SELECT * from language")
    fun getAllLanguages(): Flow<List<Language>>
}