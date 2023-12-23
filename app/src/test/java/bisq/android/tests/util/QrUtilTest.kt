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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import bisq.android.util.QrUtil
import com.google.zxing.WriterException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(BitmapFactory::class, Bitmap::class)
class QrUtilTest {
    @Mock
    private val bitmap: Bitmap? = null

    @Before
    fun setup() {
        mockStatic(Bitmap::class.java)
        mockStatic(BitmapFactory::class.java)
        `when`(Bitmap.createBitmap(anyInt(), anyInt(), any())).thenReturn(bitmap)
        `when`(BitmapFactory.decodeFile(anyString())).thenReturn(bitmap)
    }

    @Test
    fun testCreateQrImageWithSimpleContent() {
        QrUtil.createQrImage("text")
    }

    @Test
    fun testCreateQrImageWithCustomDimensions() {
        QrUtil.createQrImage("text", 640, 640)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateQrImageWithEmptyContentsThrowsException() {
        QrUtil.createQrImage("")
    }

    @Test(expected = WriterException::class)
    fun testCreateQrImageWithTooLongContents() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val stringLength = 10000
        val randomString =
            (1000..stringLength)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        QrUtil.createQrImage(randomString)
    }
}
