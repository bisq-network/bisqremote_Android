package com.joachimneumann.bisq;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TransferCodeEmail  extends Fragment implements View.OnClickListener {
    private String bisqPhoneID;
    public TransferCodeEmail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_email, container, false);
        bisqPhoneID = getArguments().getString("bisqPhoneID");
        Button b = (Button) myView.findViewById(R.id.email_button);
        b.setOnClickListener(this);
        return myView;
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "your_email_address");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        String emailBody = getString(R.string.email_content1)+bisqPhoneID+getString(R.string.email_content2);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        startActivity(Intent.createChooser(intent, "Send Email"));
//
//
//        if (view.getId() == R.id.email_button) {
//            String mailto = "mailto:insertyouremailhere" +
//                    "&subject=" + Uri.encode(getString(R.string.email_subject)) +
//                    "&body=" + Uri.encode(emailBody);
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//            emailIntent.setData(Uri.parse(mailto));
//            try {
//                startActivity(emailIntent);
//            } catch (ActivityNotFoundException e) {
//                //TODO: Handle case where no email app is available
//            }
//        }
    }
}