package com.joachimneumann.bisq

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.TextView

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val bisqToolbar = findViewById<Toolbar>(R.id.bisq_toolbar)
        bisqToolbar.title = ""
        setSupportActionBar(bisqToolbar)
        val tv = findViewById<TextView>(R.id.textView2)
        tv.text = "1. Go to your computer \n" +
                "2. Start the Bisq App \n" +
                "3. Open \"Bisq Remote\" \n" +
                "4. Press next \n" +
                "5. Scan the QR code"
        val nextButton = findViewById<Button>(R.id.registerNextButton)
        nextButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.registerNextButton) {
            val myIntent = Intent(this@RegisterActivity, TransferCodeActivity::class.java)
            startActivity(myIntent)
        }
    }
}
