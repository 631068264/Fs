package com.fs.fs.utils;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wyx on 2017/1/14.
 */

public class EncryptUtils {
    private static final int BLOCK_SIZE = 16;
    private static final byte PADDING = '{';
    private static final String ALGORITHM_NAME = "AES/ECB/PKCS5Padding";
    private static final byte[] SECRET = "VymiluoOWUL6twaK".getBytes();

    public static String md5(String input) {
        return bytes2HexString(hash(input.getBytes(), "MD5"));
    }

    public static String hmacSHA1(String key, String input) {
        return bytes2HexString(hmac(key.getBytes(), input.getBytes(), "HmacSHA1"));
    }

    private static byte[] hmac(byte[] key, byte[] data, String algorithm) {
        if (data == null || data.length == 0 || key == null || key.length == 0) return null;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] hash(byte[] data, String algorithm) {
        if (data == null || data.length <= 0) return null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String bytes2HexString(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder hexString = new StringBuilder();
        for (byte aSrc : bytes) {
            String hex = Integer.toHexString(0xFF & aSrc);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    private static byte[] hexString2Bytes(String s) {
        if (s == null || "".equals(s)) {
            return null;
        }
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String AESEncode(String input) {
        if (input == null || "".equals(input)) {
            return "";
        }
        try {
            byte[] bytes = input.getBytes();
            byte[] result = new byte[bytes.length + (BLOCK_SIZE - bytes.length % BLOCK_SIZE)];
            for (int i = 0; i < result.length; i++) {
                result[i] = i < bytes.length ? bytes[i] : PADDING;
            }
            Cipher c = Cipher.getInstance(ALGORITHM_NAME);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET, "AES"));
            return EncodeUtils.base64Encode(c.doFinal(result));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String AESDecode(String input) {
        try {
            Cipher c = Cipher.getInstance(ALGORITHM_NAME);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET, "AES"));
            String contentWithPadding = new String(c.doFinal(EncodeUtils.base64Decode(input)));
            return contentWithPadding.substring(0, contentWithPadding.indexOf(PADDING));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

//    private static byte[] encrypt(byte[] data) throws Exception {
//        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance(ALGORITHM_NAME);
//        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET, "AES"));
//        return c.doFinal(data);
//    }
//
//    private static byte[] decrypt(byte[] data) throws Exception {
//        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance(ALGORITHM_NAME);
//        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET, "AES"));
//        return c.doFinal(data);
//    }

//    public static String encodeEAS(byte[] data) {
//        if (data == null) return "";
//        try {
//            byte[] result = new byte[data.length + (BLOCK_SIZE - data.length % BLOCK_SIZE)];
//            for (int i = 0; i < result.length; i++) {
//                result[i] = i < data.length ? data[i] : PADDING;
//            }
//            return BinAscii.base64encode(encrypt(result));
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    public static String decodeEAS(String content) {
//        if (content == null) return "";
//        try {
//            String contentWithPadding = new String(decrypt(BinAscii.base64decode(content)));
//            return contentWithPadding.substring(0, contentWithPadding.indexOf("{"));
//        } catch (Exception e) {
//            return "";
//        }
//    }


}
