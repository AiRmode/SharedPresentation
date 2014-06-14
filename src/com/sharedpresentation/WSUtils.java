package com.sharedpresentation;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by Admin on 14.06.14.
 */
public class WSUtils {
    public static boolean sendStringMessage(Session session, String message) {
        boolean result = false;
            try {
                session.getBasicRemote().sendText(message);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
