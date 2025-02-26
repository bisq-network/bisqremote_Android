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

import bisq.android.util.MaskingUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MaskingUtilTest {
    @Test
    fun shouldMaskMiddleOfLongStringWithDefaultVisibleChars() {
        val result = MaskingUtil.maskSensitive("mySuperSecretPassword12345")
        assertThat(result).isEqualTo("mySup****************12345")
    }

    @Test
    fun shouldMaskMiddleOfStringWithCustomVisibleChars() {
        val result = MaskingUtil.maskSensitive("1234567890abcdef", 4)
        assertThat(result).isEqualTo("1234********cdef")
    }

    @Test
    fun shouldMaskMiddleOfStringWithCustomVisibleCharsAndCustomMaskCharacter() {
        val result = MaskingUtil.maskSensitive("VerySensitiveKeyHere", 6, '#')
        assertThat(result).isEqualTo("VerySe########eyHere")
    }

    @Test
    fun shouldMaskEntireStringIfShorterThanTwiceVisibleChars() {
        val result = MaskingUtil.maskSensitive("short", 3)
        assertThat(result).isEqualTo("*****")
    }

    @Test
    fun shouldMaskEntireStringIfShorterThanTwiceVisibleCharsWithCustomChar() {
        val result = MaskingUtil.maskSensitive("test", 3, '#')
        assertThat(result).isEqualTo("####")
    }

    @Test
    fun shouldReturnEmptyStringWhenInputIsNull() {
        val result = MaskingUtil.maskSensitive(null)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun shouldReturnSameStringWhenInputIsEmpty() {
        val result = MaskingUtil.maskSensitive("")
        assertThat(result).isEqualTo("")
    }

    @Test
    fun shouldMaskEntireStringIfExactlyEqualToTwiceVisibleChars() {
        val result = MaskingUtil.maskSensitive("abcdefgh", 4)
        assertThat(result).isEqualTo("********")
    }

    @Test
    fun shouldHandleVeryLongStringWithLargeVisibleChars() {
        val result = MaskingUtil.maskSensitive("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 10)
        assertThat(result).isEqualTo("ABCDEFGHIJ****************1234567890")
    }
}
