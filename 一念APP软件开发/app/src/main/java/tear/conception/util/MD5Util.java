package tear.conception.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    private static final String DEVELOPER_QQ = "523341786";
    private static final String DEVELOPER_PASSWORD_MD5 = "5797094e601ff1a8567320f304fb6642";
    //loveouyanying
    public static String encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isDeveloper(String qqNumber) {
        return DEVELOPER_QQ.equals(qqNumber);
    }

    public static boolean verifyDeveloperPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        String encrypted = encrypt(password);
        return DEVELOPER_PASSWORD_MD5.equals(encrypted);
    }
}
