package com.sharedpresentation;

/**
 * Created by alshevchuk on 04.07.2014.
 */
public class RunInCmd {
    public void runInCmd(String cmd){
        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec(cmd);
        } catch (Exception e){

        }
    }
}
