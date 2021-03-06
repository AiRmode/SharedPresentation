package com.sharedpresentation;

import com.sharedpresentation.commons.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.websocket.Session;
import java.util.Map;

/**
 * Created by Admin on 14.06.14.
 */
public class TimerTextMsgSender implements Runnable {
    private final int REFRESH_TIME_DELAY = 200;
    private final ThreadUtils threadUtils = new ThreadUtils();
    private static Logger logger = Logger.getLogger(TimerTextMsgSender.class);

    public TimerTextMsgSender() {

    }

    @Override
    public void run() {
        boolean flag = ThreadUtils.isTextMsgTimerState();
        while (flag) {
            try {
                //Need the guarantee, that data will be sent to all clients
                Map<Session, MySharedPresentation> peersMap = MySharedPresentation.getClientsMap();

                if (MySharedPresentation.getTextMessagesQueue().isEmpty()) {
                    threadUtils.threadSleep(REFRESH_TIME_DELAY);
                    continue;
                }

                String message = MySharedPresentation.getTextMessagesQueue().poll();
                while (message != null) {
                    for (Session session : peersMap.keySet()) {
                        boolean sendResult = WSUtils.sendStringMessage(session, message);
                        while (!sendResult && session != null && session.isOpen()) {
                            sendResult = WSUtils.sendStringMessage(session, message);
                            Thread.yield();
                        }
                    }
                    message = MySharedPresentation.getTextMessagesQueue().poll();
                }
                threadUtils.threadSleep(REFRESH_TIME_DELAY);
            } catch (Exception e) {
                logger.error(ExceptionUtils.createExceptionMessage(e));
            }
        }
        logger.info(getClass().getSimpleName() + " exit....");
    }

}
