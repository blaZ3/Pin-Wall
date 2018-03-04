package com.example.downloader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vivek on 04/03/18.
 */

public class Utils {


    public static final String THREAD_PREFIX = "Downloader-";

    public static final String ROOT = "http://pastebin.com/raw/";

    public static String getMD5Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}
