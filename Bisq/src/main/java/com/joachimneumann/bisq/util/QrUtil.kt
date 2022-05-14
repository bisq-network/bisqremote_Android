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
