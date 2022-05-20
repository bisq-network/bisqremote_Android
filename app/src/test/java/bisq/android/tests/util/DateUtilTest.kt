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
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.*


class DateUtilTest {

    private val dateUtil = DateUtil()

    @Test
    fun testDefaultFormatReturnsFormattedString() {
        assertEquals("2022-05-08 01:46:43", DateUtil.format(1651974403000L))
    }

    @Test
    fun testFormatWithSpecifiedLocaleReturnsFormattedString() {
        assertEquals("2022-05-08 01:46:43", DateUtil.format(1651974403000L, Locale.GERMAN))
    }

    @Test
    fun testFormatWithSpecifiedPatternReturnsFormattedString() {
        assertEquals("08/05/2022", DateUtil.format(1651974403000L, Locale.US, "dd/MM/yyyy"))
    }

    @Test
    fun testToDateReturnsDate() {
        assertEquals(Date(1651974403000L), dateUtil.toDate(1651974403000L))
    }

    @Test
    fun testToDateWithNullParameterReturnsNull() {
        assertEquals(null, dateUtil.toDate(null))
    }

    @Test
    fun testToLongReturnsLong() {
        assertEquals(1651974403000L, dateUtil.toLong(Date(1651974403000L)))
    }

    @Test
    fun testToLongWithNullParameterReturnsNull() {
        assertEquals(null, dateUtil.toLong(null))
    }

    @Test
    fun testDeserializeReturnsDate() {
        val json = "{\"date\":\"2022-05-08 01:46:43\"}"
        assertEquals(
            Date(1651974403000L),
            dateUtil.deserialize(
                Gson().fromJson(json, JsonObject::class.java).get("date"),
                null,
                null
            )
        )
    }

    @Test
    fun testDeserializeInvalidDateReturnsNull() {
        val json = "{\"date\":\"invalid\"}"
        assertNull(
            dateUtil.deserialize(
                Gson().fromJson(json, JsonObject::class.java).get("date"),
                null,
                null
            )
        )
    }

}
