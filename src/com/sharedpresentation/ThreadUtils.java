package com.sharedpresentation;


import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Created by Admin on 14.06.14.
 */
public class ThreadUtils {
    private static volatile boolean graphicMsgTimerState = true;
    private static volatile boolean textMsgTimerState = true;
    private static Logger logger = Logger.getLogger(ThreadUtils.class);

    public void threadSleep(long sleepingPeriod){
        try {
            Thread.sleep(sleepingPeriod);
        } catch (InterruptedException e) {
            logger.error(ExceptionUtils.createExceptionMessage(e.getStackTrace()));
        }
    }

    public static boolean isGraphicMsgTimerState() {
        return graphicMsgTimerState;
    }

    public static boolean isTextMsgTimerState() {
        return textMsgTimerState;
    }
}
