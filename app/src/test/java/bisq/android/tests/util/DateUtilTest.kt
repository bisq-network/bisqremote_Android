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

package bisq.android.tests.util

import bisq.android.util.DateUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assume.assumeFalse
import org.junit.Assume.assumeTrue
import org.junit.BeforeClass
import org.junit.Test
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtilTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun init() {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        }
    }

    @Test
    fun testDefaultFormatReturnsFormattedString() {
        assertThat(DateUtil.format(1651974403000L))
            .isEqualTo("2022-05-08 01:46:43")
    }

    @Test
    fun testFormatWithSpecifiedLocaleReturnsFormattedString() {
        assertThat(DateUtil.format(1651974403000L, Locale.GERMAN))
            .isEqualTo("2022-05-08 01:46:43")
    }

    @Test
    fun testFormatWithSpecifiedPatternReturnsFormattedString() {
        assertThat(DateUtil.format(1651974403000L, pattern = "dd/MM/yyyy"))
            .isEqualTo("08/05/2022")
    }

    @Test
    fun testFormatWithSpecifiedTimezoneReturnsFormattedString() {
        val tz = TimeZone.getTimeZone("Pacific/Galapagos") // UTC-6 always
        assumeFalse("In daylight time", tz.inDaylightTime(Date()))
        assertThat(DateUtil.format(1651974403000L, timezone = tz))
            .isEqualTo("2022-05-07 19:46:43")
    }

    @Test
    fun testFormatWithSpecifiedTimezoneDSTReturnsFormattedString() {
        val tz = TimeZone.getTimeZone("Europe/Helsinki") // UTC+3 when DST in effect
        assumeTrue("Not in daylight time", tz.inDaylightTime(Date()))
        assertThat(DateUtil.format(1651974403000L, timezone = tz))
            .isEqualTo("2022-05-08 04:46:43")
    }
}
