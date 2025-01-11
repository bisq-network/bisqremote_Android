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

package bisq.android.rules

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import androidx.test.core.app.takeScreenshot
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.pathString

/**
 * Takes screenshots during test execution.
 */
class ScreenshotRule : TestWatcher() {
    companion object {
        private val screenshotsPath = Path("bisq", "screenshots")
    }

    override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        val parentFolderPath = Path("failures", description.className)
        saveScreenshot(takeScreenshot(), parentFolderPath.pathString, description.methodName)
    }

    private fun saveScreenshot(bitmap: Bitmap, parentFolderPath: String = "", screenshotName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$screenshotName.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Path(DIRECTORY_PICTURES, screenshotsPath.pathString, parentFolderPath).pathString
                )
            }
            val contentResolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
            val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val uri = contentResolver.insert(contentUri, contentValues)

            contentResolver.openOutputStream(uri ?: return)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } else {
            val imagePath = Path(
                getExternalStoragePublicDirectory(DIRECTORY_PICTURES).path,
                screenshotsPath.pathString,
                parentFolderPath
            )
            imagePath.createDirectories()
            val image = File(imagePath.pathString, "$screenshotName.png")
            image.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
