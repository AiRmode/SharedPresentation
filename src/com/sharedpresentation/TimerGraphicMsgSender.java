package com.sharedpresentation;

import com.sharedpresentation.commons.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.websocket.Session;
import java.util.Map;

/**
 * Created by Admin on 09.06.14.
 */
public class TimerGraphicMsgSender implements Runnable {

    private final int REFRESH_TIME_DELAY = 500;
    private final ThreadUtils threadUtils = new ThreadUtils();
    private GraphicFileUtils graphicFileUtils = new GraphicFileUtils();
    private volatile String prevPicture = "";
    private static Logger logger = Logger.getLogger(TimerGraphicMsgSender.class);

    public TimerGraphicMsgSender() {

    }

    @Override
    public void run() {
        boolean flag = ThreadUtils.isGraphicMsgTimerState();
        while (flag) {
            try {
                threadUtils.threadSleep(REFRESH_TIME_DELAY);
                //Need the guarantee, that data will be sent to all clients
                Map<Session, MySharedPresentation> peersMap = MySharedPresentation.getClientsMap();

                String newPicture = graphicFileUtils.getGraphicFileBase64Representation();
                if (prevPicture == null || newPicture == null)
                    continue;
                if (prevPicture.equals(newPicture))
                    continue;
                else
                    prevPicture = newPicture;

                for (Session session : peersMap.keySet()) {
                    boolean sendResult = WSUtils.sendStringMessage(session, newPicture);
                    while (!sendResult && session != null && session.isOpen()) {
                        sendResult = WSUtils.sendStringMessage(session, newPicture);
                        Thread.yield();
                    }
                }
            } catch (Exception e) {
                logger.info(ExceptionUtils.createExceptionMessage(e));
            }

        }
    }

}
