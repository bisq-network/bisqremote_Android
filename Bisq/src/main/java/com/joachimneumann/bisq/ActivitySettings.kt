package com.joachimneumann.bisq

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.joachimneumann.bisq.Database.BisqNotification
import java.util.Date


class ActivitySettings : AppCompatActivity() {
    companion object {
        private var counter = 1
    }

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
            Phone.instance.clearPreferences(this)
            startActivity(Intent(this, ActivityWelcome::class.java))
        }


        settingsAddExampleButton.setOnClickListener {
            val mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
            val new = BisqNotification()
            if (counter % 5 == 0) {
                new.type = "TRADE"
                new.title = "Trade confirmed"
                new.message = "The trade with ID 38765384 is confirmed."
            }
            if (counter % 5 == 1) {
                new.type = "OFFER"
                new.title = "Offer taken"
                new.message = "Your offer with ID 39847534 was taken"
            }
            if (counter % 5 == 2) {
                new.type = "DISPUTE"
                new.title = "Dispute message"
                new.message = "You received a dispute message for trade with ID 34059340"
            }
            if (counter % 5 == 3) {
                new.type = "PRICE"
                new.title = "Price below 5000 Euro"
                new.message = "Your price alert got triggered. The current Euro price is below 5000"
            }
            if (counter % 5 == 4) {
                new.type = "MARKET"
                new.title = "New offer"
                new.message = "A new offer offer with price 5600 Euro (5% below market price) and payment method SEPA was published to the Bisq offerbook.\nThe offer ID is 34534"
            }
            counter += 1

            val now = Date()
            new.sentDate = now.time - 1000 * 60 * 60 // 1 hour earlier
            new.receivedDate = now.time
            mViewModel.insert(new)
        }

        settingsDeleteAllNotifcationsButton.setOnClickListener {
            val mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
            mViewModel.nukeTable()
            finish()
        }

        settingsMarkAsReadButton.setOnClickListener {
            val mViewModel = ViewModelProviders.of(this).get(BisqNotificationViewModel::class.java)
            mViewModel.markAllAsRead()
            finish()
        }

        val phone = Phone.instance
        if (phone.key != null)      {   settingsKeyTextView.text = "key   "+phone.key!!.substring(0, 10)+"..." }
        if (phone.token != null) { settingsTokenTextView.text = "token "+phone.token!!.substring(0, 10)+"..." }
        settingsVersionTextView.text = "Version 0.4"
    }

}