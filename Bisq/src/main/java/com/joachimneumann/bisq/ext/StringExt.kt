package bisq.android.ext

import android.text.TextUtils

fun String.hexStringToByteArray() = ByteArray(this.length / 2) {
    this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
}

fun String.capitalizeEachWord(): String {
    if (TextUtils.isEmpty(this)) {
        return this
    }
    val arr = this.toCharArray()
    var capitalizeNext = true

    val phrase = StringBuilder()
    for (c in arr) {
        if (capitalizeNext && Character.isLetter(c)) {
            phrase.append(Character.toUpperCase(c))
            capitalizeNext = false
            continue
        } else if (Character.isWhitespace(c)) {
            capitalizeNext = true
        }
        phrase.append(c)
    }

    return phrase.toString()
}
