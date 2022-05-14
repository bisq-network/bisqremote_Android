package android.util;

public final class Log {
    public static int i(String tag, String msg) {
        System.out.print(tag + msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.print(tag + msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.print(tag + msg);
        return 0;
    }
}
