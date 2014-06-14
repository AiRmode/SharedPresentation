package com.sharedpresentation;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Admin on 09.06.14.
 */
public class TimerGraphicMsgSender implements Runnable {

    private final int REFRESH_TIME_DELAY = 5000;
    private final String pictureFileName = "image.jpg";
    private final String pictureFilePath = "../res/";
    private final String imagePrefix = "data:image/jpg;base64,";
    private final ThreadUtils threadUtils = new ThreadUtils();

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
                    boolean sendResult = WSUtils.sendStringMessage(session, getGraphicFileBase64Representation());
                    while (!sendResult && session != null && session.isOpen()) {
                        sendResult = WSUtils.sendStringMessage(session, getGraphicFileBase64Representation());
                        Thread.yield();
                    }
                }
                threadUtils.threadSleep(REFRESH_TIME_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public String getGraphicFileBase64Representation() {
        String result;
        File graphicFile = new File(pictureFilePath + pictureFileName);
        byte[] imageFileBytes = getBinaryArrayFromFile(graphicFile);
        result = imagePrefix + new String(imageFileBytes);
        return result;
    }

    public byte[] getBinaryArrayFromFile(File file) {
        byte[] imageFileBytes = new byte[1];
        try {
            imageFileBytes = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFileBytes;
    }
}
