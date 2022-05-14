package com.joachimneumann.bisq.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.joachimneumann.bisq.R

class DialogBuilder {

    companion object Builder {
        fun choicePrompt(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String,
            negativeButtonText: String,
            positiveActionListener: DialogInterface.OnClickListener,
            negativeActionListener: DialogInterface.OnClickListener? = null
        ): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(positiveButtonText, positiveActionListener)
            if (negativeActionListener != null) {
                builder.setNegativeButton(
                    negativeButtonText, negativeActionListener
                )
            } else {
                builder.setNegativeButton(
                    negativeButtonText
                ) { dialog, _ ->
                    dialog.cancel()
                }
            }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener { dialog ->
                val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(ContextCompat.getColor(context, R.color.primary))
                val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(Color.RED)
            }

            return alertDialog
        }
    }

}
