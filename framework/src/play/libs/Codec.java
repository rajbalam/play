package play.libs;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import play.Play;
import play.exceptions.UnexpectedException;

/**
 * Codec utils
 */
public class Codec {

    /**
     * @return an UUID String
     */
    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Encode a String to base64
     * @param value The plain String
     * @return The base64 encoded String
     */
    public static String encodeBASE64(String value) {
        try {
            return new String(Base64.encodeBase64(value.getBytes("utf-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Encode binary data to base64 
     * @param value The binary data
     * @return The base64 encoded String
     */
    public static String encodeBASE64(byte[] value) {
        return new String(Base64.encodeBase64(value));
    }

    /**
     * Decode a base64 value
     * @param value The base64 encoded String
     * @return decoded binary data
     */
    public static byte[] decodeBASE64(String value) {
        try {
            return Base64.decodeBase64(value.getBytes("utf-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Build an hexadecimal MD5 hash for a String
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexMD5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Build an hexadecimal SHA1 hash for a String
     * @param value The String to hash
     * @return An hexadecimal Hash
     */    
    public static String hexSHA1(String value) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = new byte[40];
            md.update(value.getBytes("utf-8"));
            digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Encrypt a String with the AES encryption standard using the application secret
     * @param value The String to encrypt
     * @return An hexadecimal encrypted string
     */
    public static String encryptAES(String value) {
        return encryptAES(value, Play.configuration.getProperty("application.secret").substring(0, 16));
    }

    /**
     * Encrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     * @param value The String to encrypt
     * @param privateKey The key used to encrypt
     * @return An hexadecimal encrypted string
     */
    public static String encryptAES(String value, String privateKey) {
        try {
            byte[] raw = privateKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return byteToHexString(cipher.doFinal(value.getBytes()));
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    /**
     * Decrypt a String with the AES encryption standard using the application secret
     * @param value An hexadecimal encrypted string
     * @return The decrypted String
     */
    public static String decryptAES(String value) {
        return decryptAES(value, Play.configuration.getProperty("application.secret").substring(0, 16));
    }

    /**
     * Decrypt a String with the AES encryption standard. Private key must have a length of 16 bytes
     * @param value An hexadecimal encrypted string
     * @param privateKey The key used to encrypt
     * @return The decrypted String
     */
    public static String decryptAES(String value, String privateKey) {
        try {
            byte[] raw = privateKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return new String(cipher.doFinal(hexStringToByte(value)));
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    public static String byteToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            int v = bytes[i];
            if (v < 0) {
                v += 256;
            }
            String n = Integer.toHexString(v);
            if(n.length() == 1)
                n = "0" + n;
            builder.append(n);
        }

        return builder.toString();
    }

    public static byte[] hexStringToByte(String hexString) {
        byte[] raw = new byte[16];
        for(int i=0;i<16;i++) {
            raw[i] = Integer.decode("0x" + hexString.substring(i*2, i*2+2)).byteValue();
        }
        return raw;
    }

}
