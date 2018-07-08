package com.joachimneumann.bisq

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.joachimneumann.bisq.R.id.resend_email_button

class ActivityQR: AppCompatActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var statusTextView: TextView
    private lateinit var register_qr_instructions_textview: TextView
    private lateinit var instruction_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_qr)
        qrImage = bind(R.id.qrImageView)
        statusTextView = bind(R.id.register_qr_status_textview)
        statusTextView.setText(getString(R.string.waiting))
        register_qr_instructions_textview = bind(R.id.register_qr_instructions_textview)
        instruction_button = bind(R.id.register_qr_instructions_button)
        instruction_button.setOnClickListener { instructionPressed() }
        createQR()
    }

    fun confirmed() {
        this.runOnUiThread(java.lang.Runnable {
            statusTextView.setText("ok")
        })


    }

    private fun createQR() {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(Phone.getInstance(this).phoneID(), BarcodeFormat.QR_CODE, 1024, 1024)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            qrImage.setImageBitmap(bmp)

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun instructionPressed() {
        if (qrImage.visibility == View.VISIBLE) {
            qrImage.visibility = View.INVISIBLE
            register_qr_instructions_textview.visibility = View.VISIBLE
        } else {
            qrImage.visibility = View.VISIBLE
            register_qr_instructions_textview.visibility = View.INVISIBLE
        }
    }

}