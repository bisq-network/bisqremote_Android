package com.joachimneumann.bisq

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import android.media.RingtoneManager


class ActivityRegisterQR: AppCompatActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var status: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var register_qr_instructions: TextView
    private lateinit var registerQROK: ImageView
    private lateinit var instruction_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_qr)
        qrImage = bind(R.id.qrImageView)
        status = bind(R.id.register_qr_status)
        status.setText(getString(R.string.waiting))
        register_qr_instructions = bind(R.id.register_qr_instructions)
        register_qr_instructions.visibility = View.INVISIBLE
        progressBar = bind(R.id.registerQRProgressBar)
        progressBar.visibility = View.INVISIBLE
        registerQROK = bind(R.id.registerQROK)
        registerQROK.visibility = View.INVISIBLE
        instruction_button = bind(R.id.register_qr_instructions_button)
        instruction_button.setOnClickListener { instructionPressed() }
        createQR()
    }

    fun confirmed() {
        this.runOnUiThread(java.lang.Runnable {
            try {
                val notificationTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(applicationContext, notificationTone).play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            status.setText(getString(R.string.register_qr_confirmation_received))
            progressBar.visibility = View.VISIBLE
            registerQROK.visibility = View.VISIBLE
            Handler().postDelayed(java.lang.Runnable {
                val i = Intent(Intent(this,NotificationTable::class.java))
                startActivity(i)
            }, 2500)
        })
    }

    private fun createQR() {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(Phone.instance.phoneID(), BarcodeFormat.QR_CODE, 1024, 1024)
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
            register_qr_instructions.visibility = View.VISIBLE
        } else {
            qrImage.visibility = View.VISIBLE
            register_qr_instructions.visibility = View.INVISIBLE
        }
    }

}