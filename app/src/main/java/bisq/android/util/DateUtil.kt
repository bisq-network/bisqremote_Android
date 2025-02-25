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

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtil {
    private val LOCALE = Locale.US
    private const val PATTERN = "yyyy-MM-dd HH:mm:ss"

    fun format(
        date: Long,
        locale: Locale = LOCALE,
        pattern: String = PATTERN,
        timezone: TimeZone = TimeZone.getDefault()
    ): String? {
        val formatter = SimpleDateFormat(pattern, locale)
        formatter.timeZone = timezone
        return formatter.format(date)
    }
}
