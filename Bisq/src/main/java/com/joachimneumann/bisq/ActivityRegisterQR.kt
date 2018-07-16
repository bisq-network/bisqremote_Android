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
import android.media.RingtoneManager
import android.support.constraint.ConstraintLayout
import android.view.Gravity


class ActivityRegisterQR: AppCompatActivity() {

    private lateinit var qrImage: ImageView
    private lateinit var instructionsLabel: TextView
    private lateinit var instructionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_qr)

        qrImage = bind(R.id.qrImageView)
        instructionsLabel = bind(R.id.register_qr_instructions)
        instructionsLabel.visibility = View.INVISIBLE
        instructionButton = bind(R.id.register_qr_instructions_button)
        instructionButton.setOnClickListener { instructionPressed() }
        createQR()
        val layout = findViewById<ConstraintLayout>(R.id.layout_register_qr)
        layout.setOnClickListener({ _ -> toggleInstructions() })

    }

    fun confirmed() {
        this.runOnUiThread(java.lang.Runnable {
            try {
                val notificationTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(applicationContext, notificationTone).play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            instructionsLabel.text = getString(R.string.register_qr_confirmation_received)
            instructionsLabel.textSize = 20f
            instructionsLabel.gravity = Gravity.CENTER
            qrImage.visibility = View.INVISIBLE
            instructionsLabel.visibility = View.VISIBLE
            instructionButton.text = "SHOW NOTIFICATIONS"
        })
    }

    private fun createQR() {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(Phone.instance.pairingToken(), BarcodeFormat.QR_CODE, 1024, 1024)
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

    private fun toggleInstructions() {
        if (qrImage.visibility == View.VISIBLE) {
            qrImage.visibility = View.INVISIBLE
            instructionsLabel.visibility = View.VISIBLE
        } else {
            qrImage.visibility = View.VISIBLE
            instructionsLabel.visibility = View.INVISIBLE
        }
    }


    private fun instructionPressed() {
        if (Phone.instance.confirmed) {
            val i = Intent(Intent(this,ActivityNotificationTable::class.java))
            startActivity(i)
        } else {
            toggleInstructions()
        }
    }

}