package com.sharedpresentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by alshevchuk on 04.07.2014.
 */
public class RunInCmd {
    public void runInCmd(String cmd) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            System.out.println("try: " + cmd);
            p = rt.exec(cmd);
            System.out.println("done: " + cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Cp866"));
            if (true) {
                p.waitFor();
            }
            String inputData;
            if (br.ready()) {
                while ((inputData = br.readLine()) != null) {
                    System.out.println(inputData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void runBatFileInCmd(final String name, final String path) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Runtime rt = Runtime.getRuntime();
                Process p;
                try {
                    System.out.println("try: " + path + " " + name);
                    p = rt.exec("cmd /c " + name, null, new File(path));
                    System.out.println("done: " + path + " " + name);

                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Cp866"));
                    if (true) {
                        p.waitFor();
                    }
                    String inputData;
                    if (br.ready()) {
                        while ((inputData = br.readLine()) != null) {
                            System.out.println(inputData);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
