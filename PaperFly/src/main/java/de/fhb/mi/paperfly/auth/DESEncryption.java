package de.fhb.mi.paperfly.auth;


import android.util.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Decrypts password and username for login.
 * <p/>
 * http://sanjaal.com/java/186/java-encryption/tutorial-java-des-encryption-and-decryption/
 * www.sanjaal.com/java
 * Last Modified On 06-19-2009
 *
 * @author Kushal Paudyal
 */
public class DESEncryption {

    public static final String DES_ENCRYPTION_SCHEME = "DES";
    private static final String UNICODE_FORMAT = "UTF8";
    byte[] keyAsBytes;
    SecretKey key;
    private KeySpec myKeySpec;
    private SecretKeyFactory mySecretKeyFactory;
    private Cipher cipher;
    private String myEncryptionKey;
    private String myEncryptionScheme;

    public DESEncryption() throws Exception {
        myEncryptionKey = "HansPeter";
        myEncryptionScheme = DES_ENCRYPTION_SCHEME;
        keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        myKeySpec = new DESKeySpec(keyAsBytes);
        mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = mySecretKeyFactory.generateSecret(myKeySpec);
    }

    /**
     * Returns String From An Array Of Bytes
     */
    private static String bytes2String(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            stringBuilder.append((char) aByte);
        }
        return stringBuilder.toString();
    }

    /**
     * Method To Decrypt An Ecrypted String
     */
    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decode(encryptedString, Base64.NO_WRAP);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = bytes2String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    /**
     * Method To Encrypt The String
     */
    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedString = bytes2String(Base64.encode(encryptedText, Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

}
