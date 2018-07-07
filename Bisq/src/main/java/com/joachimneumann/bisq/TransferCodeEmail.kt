package com.joachimneumann.bisq

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class TransferCodeEmail : Fragment(), View.OnClickListener {
    private var bisqPhoneID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        val myView = inflater.inflate(R.layout.fragment_email, container, false)
//        bisqPhoneID = arguments!!.getString("bisqPhoneID")
//        val b = myView.findViewById<View>(R.id.email_button) as Button
//        b.setOnClickListener(this)
//        return myView
//    }

    override fun onClick(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, "your_email_address")
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        val emailBody = getString(R.string.email_content1) + bisqPhoneID + getString(R.string.email_content2)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
        startActivity(Intent.createChooser(intent, "Send Email"))
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
}// Required empty public constructor