package com.joachimneumann.bisq.util

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil : JsonDeserializer<Date> {

    companion object {
        private const val TAG = "DateDeserializer"
        private val LOCALE = Locale.US
        private const val PATTERN = "yyyy-MM-dd HH:mm:ss"

        fun format(
            date: Long,
            locale: Locale = LOCALE,
            pattern: String = PATTERN
        ): String? {
            val formatter = SimpleDateFormat(pattern, locale)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(date)
        }
    }

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return value?.time
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        arg1: Type?,
        arg2: JsonDeserializationContext?
    ): Date? {
        val date = element.asString
        val formatter = SimpleDateFormat(PATTERN, LOCALE)
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            formatter.parse(date)
        } catch (e: ParseException) {
            Log.e(TAG, "Failed to parse date: $e")
            null
        }
    }
}
