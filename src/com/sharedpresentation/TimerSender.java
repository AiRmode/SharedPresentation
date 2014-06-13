package com.sharedpresentation;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by Admin on 09.06.14.
 */
public class TimerSender implements Runnable {

    private MySharedPresentation presentation;
    private final int REFRESH_TIME_DELAY = 5000;
    private final String pictureFileName = "sport_soccer.jpg";
    private final String pictureFilePath = "../res/";
    private final String imagePrefix = "data:image/jpg;base64,";

    public TimerSender() {

    }

    @Override
    public void run() {
        while (true) {
            try {
                //Need the guarantee, that data will be sent to all clients
                Map<Session, MySharedPresentation> m = MySharedPresentation.getClientsMap();
                System.out.println("In timer there are " + m.size() + " clients");
                for (Session s : m.keySet()) {
//                    synchronized (this) {
                        if (s.isOpen()) {
                            MySharedPresentation myPresentation = m.get(s);
                            myPresentation.sendToAll(getGraphicFileBase64Representation());
//                            myPresentation.onMessage(getGraphicFileBase64Representation());
//                            myPresentation.onMessage(ByteBuffer.wrap(new byte[]{0, 0}));
                        }
//                    }
                }

                Thread.sleep(REFRESH_TIME_DELAY);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getGraphicFileBase64Representation() {
        String result;
        File pictureFile = new File(pictureFilePath + pictureFileName);
        byte[] imageFileBytes = getBinaryArrayFromFile(pictureFile);
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
