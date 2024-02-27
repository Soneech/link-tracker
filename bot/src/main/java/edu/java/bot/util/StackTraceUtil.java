package edu.java.bot.util;

import java.util.ArrayList;
import java.util.List;

public class StackTraceUtil {
    private StackTraceUtil() { }

    public static List<String> getStackTrace(Exception e) {
        List<String> stackTrace = new ArrayList<>();
        for (var element: e.getStackTrace()) {
            stackTrace.add(element.toString());
        }
        return stackTrace;
    }
}
