package com.sharedpresentation;

import org.apache.log4j.Logger;

import javax.websocket.Session;

/**
 * Created by Admin on 14.06.14.
 */
public class WSUtils {
    private static volatile boolean isSendingInProgress = false;
    private static volatile Object lock = new Object();
    private static Logger logger = Logger.getLogger(WSUtils.class);

    public static boolean sendStringMessage(Session session, String message) {
        boolean result = false;
        try {
            synchronized (lock) {
                isSendingInProgress = true;
                if(session.isOpen())
                    session.getBasicRemote().sendText(message);
                isSendingInProgress = false;
                result = true;
            }
        } catch (Exception e) {
            isSendingInProgress = false;
            logger.error(e.getMessage());
        }
        return result;
    }

    public static boolean isSendingInProgress() {
        return isSendingInProgress;
    }
}
