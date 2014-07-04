package com.sharedpresentation;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by alshevchuk on 04.07.2014.
 */
public class RunInCmd {
    public void runInCmd(String cmd) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec(cmd);
            System.out.println(cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Cp866"));
            if(true){
                p.waitFor();
            }
            String inputData;
            if(br.ready()) {
                while ((inputData = br.readLine()) != null) {
                    System.out.println(inputData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
