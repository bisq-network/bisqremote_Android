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
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QrUtil {

    companion object {
        fun createQrImage(contents: String, width: Int = 1024, height: Int = 1024): Bitmap {
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(
                    contents, BarcodeFormat.QR_CODE, width, height
                )
                val bitMatrixWidth = bitMatrix.width
                val bitMatrixHeight = bitMatrix.height

                val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
                for (y in 0 until height) {
                    val offset = y * width
                    for (x in 0 until width) {
                        pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                    }
                }

                val bmp =
                    Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)

                bmp.setPixels(pixels, 0, width, 0, 0, width, height)

                return bmp
            } catch (e: WriterException) {
                throw Exception("Failed to generate QR code", e)
            }
        }
    }

}
