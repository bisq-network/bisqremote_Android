package com.joachimneumann.bisq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar bisqToolbar = findViewById(R.id.bisq_toolbar);
        bisqToolbar.setTitle("");
        setSupportActionBar(bisqToolbar);
        TextView tv = findViewById(R.id.textView2);
        tv.setText("1. Go to your computer \n" +
                "2. Start the Bisq App \n" +
                "3. Open \"Bisq Remote\" \n" +
                "4. Press next \n" +
                "5. Scan the QR code");
        Button nextButton = findViewById(R.id.registerNextButton);
        nextButton.setOnClickListener(this);    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerNextButton) {
            Intent myIntent = new Intent(RegisterActivity.this, TransferCodeActivity.class);
            startActivity(myIntent);
        }
    }
}
