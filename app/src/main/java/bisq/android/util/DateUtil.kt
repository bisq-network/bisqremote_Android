/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.android.util

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
