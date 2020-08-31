package com.autotest.eagle.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author wuranxu
 * @date 2020/8/31 5:02 下午
 */
public class Sha1Util {

    private static final String SALT = "eagle";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return buf.toString();
    }


    public static String encode(String pwd) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("sha1");
        String s = pwd + SALT;
        sha1.update(s.getBytes());
        return getFormattedText(sha1.digest());
    }
}
