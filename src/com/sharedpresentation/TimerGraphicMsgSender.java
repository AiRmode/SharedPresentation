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

    public TimerGraphicMsgSender() {

    }

    @Override
    public void run() {
        while (true) {
            try {
                //Need the guarantee, that data will be sent to all clients
                Map<Session, MySharedPresentation> peersMap = MySharedPresentation.getClientsMap();
                System.out.println("In timer there are " + peersMap.size() + " clients");

                for (Session session : peersMap.keySet()) {
                    synchronized (this) {
                        if (session != null && session.isOpen()) {
                            sendStringDataToClient(session, getGraphicFileBase64Representation());
                        }
                    }
                }

                Thread.sleep(REFRESH_TIME_DELAY);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendStringDataToClient(Session s, String s1) {
        try {
            s.getBasicRemote().sendText(s1);
        } catch (IOException e) {
            e.printStackTrace();
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
