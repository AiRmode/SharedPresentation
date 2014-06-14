package com.sharedpresentation;

import javax.websocket.Session;

/**
 * Created by Admin on 14.06.14.
 */
public class WSUtils {
    private static volatile boolean isSendingInProgress = false;
    private static volatile Object lock = new Object();

    public static boolean sendStringMessage(Session session, String message) {
        boolean result = false;
        try {
            synchronized (lock) {
                isSendingInProgress = true;
                session.getBasicRemote().sendText(message);
                isSendingInProgress = false;
                result = true;
            }
        } catch (Exception e) {
            isSendingInProgress = false;
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isSendingInProgress() {
        return isSendingInProgress;
    }
}
