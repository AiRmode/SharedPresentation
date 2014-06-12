package com.sharedpresentation;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 09.06.14.
 */
public class TimerDate implements Runnable {

    private MySharedPresentation presentation;
    private final int REFRESH_TIME_DELAY = 2000;

    public TimerDate() {

    }

    @Override
    public void run() {
        while (true) {
            try {
                //Need the guarantee, that data will be sent to all clients

                Map<Session,MySharedPresentation> m = MySharedPresentation.getClientsMap();
                for(Session s : m.keySet()){
                    if (s.isOpen()){
                        MySharedPresentation myPresentation = m.get(s);
//                        myPresentation.onMessage("");
//                        myPresentation.onMessage(ByteBuffer.wrap(new byte[]{0,0}));
                    }
                }
                /*presentation.onMessage(new Date().toString());
                while (!presentation.isAllTextDataSend()) {
                    Thread.sleep(1000);
                }*/

                Thread.sleep(REFRESH_TIME_DELAY);

                /*presentation.onMessage(getBinary());
                while (!presentation.isAllBinaryDataSend()) {
                    Thread.sleep(1000);
                }*/
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ByteBuffer getBinary() {
        try {
//            File pictureFile = new File("../res/image.jpg");
            File pictureFile = new File("../res/image.jpg");
            String prefix = "data:image/jpg;base64,";
            byte[] prefixBytes = prefix.getBytes();
            byte[] fileBytes = Base64.encodeBase64(FileUtils.readFileToByteArray(pictureFile));
            byte[] allBytes = new byte[prefixBytes.length+fileBytes.length];
            System.arraycopy(prefixBytes, 0, allBytes, 0, prefixBytes.length);
            System.arraycopy(fileBytes,0,allBytes,prefixBytes.length,fileBytes.length);
            ByteBuffer bbuf = ByteBuffer.wrap(allBytes);
            System.out.println("Send binaty bytes = " + allBytes.length);
            return bbuf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getBinaryArray() {
        try {
            File pictureFile = new File("../res/image.jpg");
            return Base64.encodeBase64(FileUtils.readFileToByteArray(pictureFile));
//            Base64.encode(FileUtils.readFileToByteArray(pictureFile));
//            BufferedImage image = ImageIO.read(new File("/webapp/image.jpg"));
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "jpg", baos);
//            byte[] byteArray = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
