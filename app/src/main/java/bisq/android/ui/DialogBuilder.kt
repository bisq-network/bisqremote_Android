/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.android.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import bisq.android.R

object DialogBuilder {

    @Suppress("LongParameterList")
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
        builder.setCancelable(true)
        builder.setPositiveButton(positiveButtonText, positiveActionListener)
        if (negativeActionListener != null) {
            builder.setNegativeButton(
                negativeButtonText,
                negativeActionListener
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

    fun prompt(
        context: Context,
        title: String,
        message: String,
        buttonText: String,
        actionListener: DialogInterface.OnClickListener? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(true)
        if (actionListener != null) {
            builder.setPositiveButton(
                buttonText,
                actionListener
            )
        } else {
            builder.setPositiveButton(
                buttonText
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener { dialog ->
            val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.primary))
        }

        return alertDialog
    }
}
