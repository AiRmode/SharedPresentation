package com.sharedpresentation;

import com.sharedpresentation.commons.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by alshevchuk on 04.07.2014.
 */
public class RunInCmd {
    private static Logger logger = Logger.getLogger(RunInCmd.class);

    public void runInCmd(String cmd) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            logger.info("try: " + cmd);
            p = rt.exec(cmd);
            logger.info("done: " + cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Cp866"));
            if (true) {
                p.waitFor();
            }
            String inputData;
            if (br.ready()) {
                while ((inputData = br.readLine()) != null) {
                    logger.info(inputData);
                }
            }

        } catch (Exception e) {
            logger.error(ExceptionUtils.createExceptionMessage(e));
        }
    }

    public synchronized void runBatFileInCmd(final String name, final String path) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Runtime rt = Runtime.getRuntime();
                Process p;
                try {
                    logger.info("try: " + path + " " + name);
                    p = rt.exec("cmd /c " + name, null, new File(path));
                    logger.info("done: " + path + " " + name);

                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Cp866"));
                    if (true) {
                        p.waitFor();
                    }
                    String inputData;
                    if (br.ready()) {
                        while ((inputData = br.readLine()) != null) {
                            logger.info(inputData);
                        }
                    }

                } catch (Exception e) {
                    logger.error(ExceptionUtils.createExceptionMessage(e));
                }
            }
        });
        t.start();
    }

    public static String getPath() {
        String path = System.getProperty("user.dir");
        char[] cpath = path.toCharArray();
        for (int i = 0; i < cpath.length; i++) {
            if (cpath[i] == '\\') {
                cpath[i] = '/';
            }
        }
        path = String.valueOf(cpath);
        path += "/";
//		System.out.println("path() to run CMD-> "+path);
        return path;
    }
}
