package com.pro.easyfood.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters
class MealTypeConverter {
    @TypeConverter
    fun fromAnyToString(attribute: Any?): String {
        if (attribute == null) {
            return ""
        }
        return attribute as String
    }

    @TypeConverter
    fun fromStringAnyToAny(attribute: String?): Any {
        if (attribute == null) return ""
        return attribute
    }
}