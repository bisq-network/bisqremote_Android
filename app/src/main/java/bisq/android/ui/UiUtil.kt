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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import bisq.android.R

object UiUtil {
    fun loadWebPage(context: Context, uri: String) {
        DialogBuilder.choicePrompt(
            context,
            context.getString(R.string.confirm),
            context.getString(R.string.load_web_page_confirmation, uri),
            context.getString(R.string.yes),
            context.getString(R.string.no),
            { _, _ ->
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                } catch (ignored: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.cannot_launch_browser),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ).show()
    }
}
