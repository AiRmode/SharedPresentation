package com.sharedpresentation.commons;

/**
 * Created by Admin on 12.07.14.
 */
public class ExceptionUtils {
    public synchronized static String createExceptionMessage(StackTraceElement[] stackTraceElements) {
        StringBuffer exceptionMessage = new StringBuffer("");
        if (stackTraceElements == null) {
            return "exception stackTraceElements == null";
        }
        exceptionMessage.append("\n");
        for (int i = 0; i < stackTraceElements.length; i++) {
            exceptionMessage.append(stackTraceElements[i]);
            exceptionMessage.append("\n");
        }
        return exceptionMessage.toString();
    }

    public synchronized static String createExceptionMessage(Exception exception) {
        StringBuffer exceptionMessage = new StringBuffer("");
        if (exception == null) {
            return "exception object == null";
        }

        exceptionMessage.append(exception.getMessage());
        exceptionMessage.append("\n");

        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            exceptionMessage.append(stackTraceElements[i]);
            exceptionMessage.append("\n");
        }
        return exceptionMessage.toString();
    }
}
