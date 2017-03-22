/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Max
 */
public class VersionNumber {

    private static VersionNumber instance = new VersionNumber();

    public static VersionNumber getInstance() {
        return instance;
    }

    VersionNumber() {
        
        setVersionNumber(Config.getInstance().getVersionNumber());
    }

    public void setVersionNumber(String version) {
        String tmp[]= version.split("\\.");
        if ( tmp.length != 3) {
            return;
        }
        major = new Integer(tmp[0]);
        minor = new Integer(tmp[1]);
        micro = new Integer(tmp[2]);
        return;
    }

    public static String getVersionNumber() {
        return major+"."+minor+"."+micro;
    }
    
    public static Integer getVersionNumberint() {
        return (major*100)+(minor*10)+micro;
    }

    public static boolean isOlderThan(String version) {
        Integer mymajor;
        Integer myminor;
        Integer mymicro;

        String [] tmp= version.split("\\.");
        if ( tmp.length != 3) {
            return false;
        }

        mymajor = new Integer(tmp[0]);
        myminor = new Integer(tmp[1]);
        mymicro = new Integer(tmp[2]);


        if (major.intValue() < mymajor.intValue()) {
            return true;
        }

        if (minor.intValue() < myminor.intValue()) {
            return true;
        }

        if (micro.intValue() < mymicro.intValue()) {
            return true;
        }

        return false;
    }

    
    private static Integer major;
    private static Integer minor;
    private static Integer micro;

}
