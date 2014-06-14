package com.sharedpresentation;

/**
 * Created by Admin on 14.06.14.
 */
public class ThreadUtils {
    private static volatile boolean graphicMsgTimerState = true;
    private static volatile boolean textMsgTimerState = true;

    public void threadSleep(long sleepingPeriod){
        try {
            Thread.sleep(sleepingPeriod);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isGraphicMsgTimerState() {
        return graphicMsgTimerState;
    }

    public static boolean isTextMsgTimerState() {
        return textMsgTimerState;
    }
}
