package com.example.finalproject.encryption;

import static com.example.finalproject.encryption.KeyUtils.getKey;

import android.content.Context;
import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decrypt {
    public String decrypt(Context context, String input) throws Exception {
        Key key = getKey(context,".txt");

        byte[] ivAndEncrypted = Base64.decode(input, Base64.DEFAULT);

        byte[] iv = new byte[16];
        System.arraycopy(ivAndEncrypted, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        byte[] encryptedBytes = new byte[ivAndEncrypted.length - iv.length];
        System.arraycopy(ivAndEncrypted, iv.length, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}
