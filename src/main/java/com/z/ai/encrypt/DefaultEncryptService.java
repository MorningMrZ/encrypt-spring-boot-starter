package com.z.ai.encrypt;

import com.alibaba.fastjson.JSONObject;
import com.z.ai.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Key;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Default RSA encryption implementation
 */
@Slf4j
public class DefaultEncryptService implements EncryptService {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private final EncryptProperties properties;

    public DefaultEncryptService(EncryptProperties properties) {
        this.properties = properties;
    }

    @Override
    public JSONObject decrypt(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof String) {
                String decryptedValue = decryptString((String) value);
                result.put(key, decryptedValue);
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public JSONObject encrypt(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof String) {
                String encryptedValue = encryptString((String) value);
                result.put(key, encryptedValue);
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public String encryptString(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }

        try {
            String privateKey = properties.getPrivateKey();
            if (!StringUtils.hasText(privateKey)) {
                throw new EncryptException("Private key is not configured");
            }

            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            Key key = keyFactory.generatePrivate(getKeySpec(privateKey));

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new EncryptException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public String decryptString(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }

        try {
            String appPublicKey = properties.getAppPublicKey();
            if (!StringUtils.hasText(appPublicKey)) {
                throw new EncryptException("Public key is not configured");
            }

            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            Key key = keyFactory.generatePublic(getPublicKeySpec(appPublicKey));

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(content));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new EncryptException("Decryption failed: " + e.getMessage(), e);
        }
    }

    private PKCS8EncodedKeySpec getKeySpec(String privateKey) {
        String key = privateKey.replaceAll("\\s*-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----\\s*", "")
                .replaceAll("\\s*", "");
        return new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
    }

    private X509EncodedKeySpec getPublicKeySpec(String publicKey) {
        String key = publicKey.replaceAll("\\s*-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----\\s*", "")
                .replaceAll("\\s*", "");
        return new X509EncodedKeySpec(Base64.getDecoder().decode(key));
    }
}
