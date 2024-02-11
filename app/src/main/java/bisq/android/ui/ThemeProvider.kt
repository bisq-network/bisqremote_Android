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

import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import bisq.android.R
import java.security.InvalidParameterException

class ThemeProvider(private val context: Context) {
    fun getThemeFromPreferences(): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val selectedTheme = sharedPreferences.getString(
            context.getString(R.string.theme_preferences_key),
            context.getString(R.string.system_theme_preference_value)
        )

        return selectedTheme?.let {
            getTheme(it)
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun getThemeDescriptionForPreference(preferenceValue: String?): String =
        when (preferenceValue) {
            context.getString(R.string.dark_theme_preference_value) ->
                context.getString(R.string.dark_theme_description)

            context.getString(R.string.light_theme_preference_value) ->
                context.getString(R.string.light_theme_description)

            else -> context.getString(R.string.system_theme_description)
        }

    fun getTheme(selectedTheme: String): Int = when (selectedTheme) {
        context.getString(R.string.dark_theme_preference_value) -> UiModeManager.MODE_NIGHT_YES
        context.getString(R.string.light_theme_preference_value) -> UiModeManager.MODE_NIGHT_NO
        context.getString(R.string.system_theme_preference_value) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        else -> throw InvalidParameterException("Theme not defined for $selectedTheme")
    }
}
