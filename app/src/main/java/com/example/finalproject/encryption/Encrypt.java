package com.example.finalproject.encryption;

import static com.example.finalproject.encryption.KeyUtils.getKey;

import android.content.Context;
import android.util.Base64;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
    public String encrypt(Context context, String input) throws Exception {
        Key key = getKey(context,".txt");
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes("UTF-8"));

        byte[] ivAndEncrypted = Arrays.copyOf(iv, iv.length + encryptedBytes.length);
        System.arraycopy(encryptedBytes, 0, ivAndEncrypted, iv.length, encryptedBytes.length);

        return Base64.encodeToString(ivAndEncrypted, Base64.DEFAULT);
    }
}