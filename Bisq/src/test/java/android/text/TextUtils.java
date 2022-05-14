package android.text;

import org.jetbrains.annotations.Nullable;

public class TextUtils {
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }
}
