package com.joachimneumann.bisq;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private Activity mActivity;
    private WelcomeHelp helpActivity;

    private ConstraintLayout mConstraintLayout;
    private ImageButton mButton;
    private Button nextButton;

    private PopupWindow mPopupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Important: The Phone needs to be called with the Context first.
        // Only afterwards, Phone.getInstance() is allowed
        Phone phone = Phone.getInstance(this);

        setContentView(R.layout.activity_welcome);
        Toolbar bisqToolbar = findViewById(R.id.bisq_toolbar);
        bisqToolbar.setTitle("");
        setSupportActionBar(bisqToolbar);
        mContext = getApplicationContext();
        mActivity = WelcomeActivity.this;
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.main);
        mButton = (ImageButton) findViewById(R.id.helpButton);
        mButton.setOnClickListener(this);
        nextButton = findViewById(R.id.welcomeNextButton);
        nextButton.setOnClickListener(this);
    }

    public void bisqWebpagePressed(View view) {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bisq.network"));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void okPressed(View view) {
        helpActivity.dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.helpButton) {
            helpActivity = new WelcomeHelp(this);
        }
        if (view.getId() == R.id.welcomeNextButton) {
            Intent myIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(myIntent);
        }
    }
}
