package com.sakethh.linkora.localDB.typeConverters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ChildIDFolderTypeConverter {

    @TypeConverter
    fun toJson(jsonString: String?): List<Long?>? {
        return jsonString?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun toString(idList: List<Long?>?): String {
        return Json.encodeToString(idList)
    }
}