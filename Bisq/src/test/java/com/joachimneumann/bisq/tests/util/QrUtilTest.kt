package bisq.android.tests.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import bisq.android.util.QrUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
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

    @Test(expected = Exception::class)
    fun testCreateQrImageWithTooLongContents() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val stringLength = 10000
        val randomString = (1000..stringLength)
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        QrUtil.createQrImage(randomString)
    }

}
