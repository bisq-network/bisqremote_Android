package com.joachimneumann.bisq

import android.content.Intent
import android.content.IntentFilter
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
    private lateinit var emailButton: Button
    private var receiver: BisqNotificationReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_qr)

        qrImage = this.bind(R.id.qrImageView)
        emailButton = bind(R.id.email_button)
        emailButton.setOnClickListener { emailPressed() }
        createQR()
    }

    fun confirmed() {
        this.runOnUiThread(java.lang.Runnable {
            try {
                val notificationTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(applicationContext, notificationTone).play()

                val i = Intent(Intent(this,ActivityCongratulations::class.java))
                startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        if (receiver == null) {
            receiver = BisqNotificationReceiver(this)
        }
        val filter = IntentFilter()
        filter.addAction(this.getString(R.string.bisq_broadcast))
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
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


    private fun emailPressed() {
       val i = Intent(Intent(this,ActivityRegisterEmail::class.java))
       startActivity(i)
    }

}