package com.sharedpresentation;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Admin on 14.06.14.
 */
public class TimerTextMsgSender implements Runnable{
    private final int REFRESH_TIME_DELAY = 500;

    public TimerTextMsgSender(){

    }

    @Override
    public void run() {
        while (true) {
            try {
                //Need the guarantee, that data will be sent to all clients
                Map<Session, MySharedPresentation> peersMap = MySharedPresentation.getClientsMap();
                System.out.println("In timer there are " + peersMap.size() + " clients");

                String message;
                while ((message = MySharedPresentation.getTextMessagesQueue().poll()) != null) {
                    for (Session session : peersMap.keySet()) {
                        synchronized (this) {
                            if (session.isOpen()) {
                                sendTextMsgToClient(session, message);
                            }
                        }
                    }
                }

                Thread.sleep(REFRESH_TIME_DELAY);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendTextMsgToClient(Session s, String s1) {
        try {
            s.getBasicRemote().sendText(s1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
