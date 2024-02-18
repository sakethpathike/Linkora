package com.sakethh.linkora.localDB.typeConverters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ChildNameFolderTypeConverter {


    @TypeConverter
    fun toJson(jsonString: String?): List<String?>? {
        return jsonString?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun toString(namesList: List<String?>): String {
        return Json.encodeToString(namesList)
    }
}