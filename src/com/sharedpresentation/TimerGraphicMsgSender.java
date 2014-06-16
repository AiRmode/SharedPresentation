package com.sharedpresentation;

import javax.websocket.Session;
import java.util.Map;

/**
 * Created by Admin on 09.06.14.
 */
public class TimerGraphicMsgSender implements Runnable {

    private final int REFRESH_TIME_DELAY = 5000;
    private final ThreadUtils threadUtils = new ThreadUtils();
    private GraphicFileUtils graphicFileUtils = new GraphicFileUtils();

    public TimerGraphicMsgSender() {

    }

    @Override
    public void run() {
        boolean flag = ThreadUtils.isGraphicMsgTimerState();
        while (flag) {
            try {
                //Need the guarantee, that data will be sent to all clients
                Map<Session, MySharedPresentation> peersMap = MySharedPresentation.getClientsMap();

                for (Session session : peersMap.keySet()) {
                    boolean sendResult = WSUtils.sendStringMessage(session, graphicFileUtils.getGraphicFileBase64Representation());
                    while (!sendResult && session != null && session.isOpen()) {
                        sendResult = WSUtils.sendStringMessage(session, graphicFileUtils.getGraphicFileBase64Representation());
                        Thread.yield();
                    }
                }
                threadUtils.threadSleep(REFRESH_TIME_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
