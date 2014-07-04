package com.sharedpresentation;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;

/**
 * Created by Admin on 16.06.14.
 */
public class GraphicFileUtils {
    private final String pictureFileName = "image.jpg";
    private final String pictureFilePath = "../../res/";
    private final String imagePrefix = "data:image/jpg;base64,";

    public String getGraphicFileBase64Representation() {
        String result;
        File graphicFile = new File(pictureFilePath + pictureFileName);
        System.out.println("ser_path "+graphicFile.getAbsolutePath());
        byte[] imageFileBytes = getBinaryArrayFromFile(graphicFile);
        result = imagePrefix + new String(imageFileBytes);
        return result;
    }

    public byte[] getBinaryArrayFromFile(File file) {
        byte[] imageFileBytes = new byte[1];
        try {
            imageFileBytes = Base64.encodeBase64(org.apache.commons.io.FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFileBytes;
    }
}
