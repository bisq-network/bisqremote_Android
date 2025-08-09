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

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

object QrUtil {

    fun createQrImage(contents: String, width: Int = 1024, height: Int = 1024): Bitmap {
        val bitMatrix = buildBitMatrix(contents, width, height)
        val pixels = buildPixelArray(bitMatrix)
        return createBitmap(pixels, width, height)
    }

    private fun buildBitMatrix(
        contents: String,
        width: Int,
        height: Int
    ): BitMatrix {
        val bitMatrix: BitMatrix
        val writer = QRCodeWriter()
        bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, width, height)
        return bitMatrix
    }

    private fun buildPixelArray(
        bitMatrix: BitMatrix
    ): IntArray {
        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return pixels
    }

    private fun createBitmap(
        pixels: IntArray,
        width: Int,
        height: Int
    ): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bmp.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmp
    }
}
