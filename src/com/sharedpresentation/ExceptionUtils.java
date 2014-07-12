package com.sharedpresentation;

/**
 * Created by Admin on 12.07.14.
 */
public class ExceptionUtils {
    public synchronized static String createExceptionMessage(StackTraceElement[] stackTraceElements) {
        StringBuffer exceptionMessage = new StringBuffer("");
        if (stackTraceElements == null) {
            return "exception stackTraceElements == null";
        }
        for (int i = 0; i < stackTraceElements.length; i++) {
            exceptionMessage.append(stackTraceElements[i]);
            exceptionMessage.append("\n");
        }
        return exceptionMessage.toString();
    }
}
