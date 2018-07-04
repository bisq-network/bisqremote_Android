package com.joachimneumann.bisq

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    private var mContext: Context? = null
    private var mActivity: Activity? = null
    private var helpActivity: WelcomeHelp? = null

    private var mConstraintLayout: ConstraintLayout? = null
    private var mButton: ImageButton? = null
    private var nextButton: Button? = null

    private val mPopupWindow: PopupWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
        val bisqToolbar = findViewById<Toolbar>(R.id.bisq_toolbar)
        bisqToolbar.title = ""
        setSupportActionBar(bisqToolbar)
        mContext = applicationContext
        mActivity = this@WelcomeActivity
        mConstraintLayout = findViewById<View>(R.id.main) as ConstraintLayout
        mButton = findViewById<View>(R.id.helpButton) as ImageButton
        mButton!!.setOnClickListener(this)
        nextButton = findViewById(R.id.welcomeNextButton)
        nextButton!!.setOnClickListener(this)
    }

    fun bisqWebpagePressed() {
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bisq.network"))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application can handle this request." + " Please install a webbrowser", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    fun okPressed() {
        helpActivity!!.dismiss()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.helpButton) {
            helpActivity = WelcomeHelp(this)
        }
        if (view.id == R.id.welcomeNextButton) {
            val myIntent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
            startActivity(myIntent)
        }
    }
}
