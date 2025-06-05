package com.beanpack.Wizard;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class secure {

    // Derive 128-bit AES key from password
    public static SecretKeySpec deriveKey(String password) throws Exception {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"));
        byte[] aesKey = new byte[16]; // use first 128 bits
        System.arraycopy(keyBytes, 0, aesKey, 0, 16);
        return new SecretKeySpec(aesKey, "AES");
    }

    // Encrypt with static IV 
    public static String encrypt(String plaintext, String password) throws Exception {
        SecretKeySpec key = deriveKey(password);
        IvParameterSpec iv = new IvParameterSpec(new byte[16]); // static IV: all zeros

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypt
    public static String decrypt(String base64Cipher, String password) throws Exception {
        SecretKeySpec key = deriveKey(password);
        IvParameterSpec iv = new IvParameterSpec(new byte[16]);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] decoded = Base64.getDecoder().decode(base64Cipher);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }

    public static void main(String[] args) throws Exception{
        String password = "cen-password";
        String plainKey = "1234567890ABCDEF"; 

        String enc = secure.encrypt(plainKey, password);
        System.out.println("Encrypted: " + enc);

        String dec = secure.decrypt(enc, password);
        System.out.println("Decrypted: " + dec);
    }
}
