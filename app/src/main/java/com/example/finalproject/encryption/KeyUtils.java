package com.example.finalproject.encryption;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class KeyUtils {

    public static Key getKey(Context context, String fileName) {
        Key key = readKeyFromFile(context, fileName);
        if (key == null) {
            key = generateKey();
            saveKeyToFile(key, context, fileName);
        }
        return key;
    }


    public static Key generateKey(){

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveKeyToFile(Key key, Context context, String fileName) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
            fos.write(encodedKey.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Key readKeyFromFile(Context context, String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists()) {
                return null;
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            if (bytes.length == 0) {
                return null;
            }
            byte[] decodedKey = Base64.getDecoder().decode(new String(bytes));
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
