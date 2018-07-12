package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView





class ActivitySettings : AppCompatActivity() {

    private lateinit var settingsRegisterAgainButton:         Button
    private lateinit var settingsAddExampleButton:            Button
    private lateinit var settingsDeleteAllNotifcationsButton: Button
    private lateinit var settingsMarkAsReadButton:            Button
    private lateinit var settingsKeyTextView:     TextView
    private lateinit var settingsTokenTextView:   TextView
    private lateinit var settingsVersionTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        settingsRegisterAgainButton         = bind(R.id.settingsRegisterAgainButton)
        settingsAddExampleButton            = bind(R.id.settingsAddExampleButton)
        settingsDeleteAllNotifcationsButton = bind(R.id.settingsDeleteAllNotifcationsButton)
        settingsMarkAsReadButton            = bind(R.id.settingsMarkAsReadButton)
        settingsKeyTextView                 = bind(R.id.settingsKeyTextView)
        settingsTokenTextView               = bind(R.id.settingsTokenTextView)
        settingsVersionTextView             = bind(R.id.settingsVersionTextView)

        settingsRegisterAgainButton.setOnClickListener {
            Phone.instance.reset()
            startActivity(Intent(this, ActivityWelcome::class.java))
        }

        val phone = Phone.instance
        if (phone.key != null)      {   settingsKeyTextView.text = "key   "+phone.key!!.substring(0, 8)+"..." }
        if (phone.token != null) { settingsTokenTextView.text = "token "+phone.token!!.substring(0, 8)+"..." }
        settingsVersionTextView.text = this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }

}