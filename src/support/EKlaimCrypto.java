package support;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EKlaimCrypto {

    private final byte[] key;

    public EKlaimCrypto(String hexKey) {
        this.key = hexStringToByteArray(hexKey);
        if (this.key.length != 32) {
            throw new IllegalArgumentException("Needs a 256-bit key!");
        }
    }

    public String encrypt(String data) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));

            byte[] signature = computeSignature(encrypted);

            byte[] combined = new byte[signature.length + iv.length + encrypted.length];
            System.arraycopy(signature, 0, combined, 0, signature.length);
            System.arraycopy(iv, 0, combined, signature.length, iv.length);
            System.arraycopy(encrypted, 0, combined, signature.length + iv.length, encrypted.length);

            return chunkSplit(Base64.getEncoder().encodeToString(combined));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String str) {
        try {
            byte[] decoded = Base64.getDecoder().decode(str.replaceAll("\\s+", ""));

            byte[] signature = Arrays.copyOfRange(decoded, 0, 10);
            byte[] iv = Arrays.copyOfRange(decoded, 10, 26);
            byte[] encrypted = Arrays.copyOfRange(decoded, 26, decoded.length);

            byte[] calcSignature = computeSignature(encrypted);

            if (!constantTimeCompare(signature, calcSignature)) {
                return "SIGNATURE_NOT_MATCH";
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            return new String(cipher.doFinal(encrypted), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    private byte[] computeSignature(byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        byte[] hmac = mac.doFinal(data);
        return Arrays.copyOfRange(hmac, 0, 10);
    }

    private static boolean constantTimeCompare(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private static String chunkSplit(String str) {
        StringBuilder sb = new StringBuilder();
        int chunkSize = 76;
        for (int i = 0; i < str.length(); i += chunkSize) {
            sb.append(str, i, Math.min(i + chunkSize, str.length()));
            sb.append("\r\n");
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
