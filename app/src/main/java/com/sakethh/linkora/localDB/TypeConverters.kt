package com.sakethh.linkora.localDB

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FoldersTypeConverter {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun convertToString(value: List<FoldersTable>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun convertToList(value: String): List<FoldersTable> {
        return json.decodeFromString(value)
    }
}

class LinksTypeConverter {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun convertToString(value: List<LinksTable>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun convertToList(value: String): List<LinksTable> {
        return json.decodeFromString(value)
    }
}

class LinkTypeConverter {
    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun convertToString(value: LinksTable): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun convertToList(value: String): LinksTable {
        return json.decodeFromString(value)
    }
}