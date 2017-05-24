package com.github.gfx.static_gson;

public class Logger {
    private static Delegate delegate;

    public static void setDelegate(Delegate delegate) {
        Logger.delegate = delegate;
    }

    public static void log(Exception ex) {
        if (delegate != null) {
            delegate.log(ex);
        }
    }

    public interface Delegate {
        void log(Exception ex);
    }
}
