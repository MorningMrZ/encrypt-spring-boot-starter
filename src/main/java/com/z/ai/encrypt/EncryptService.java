package com.z.ai.encrypt;

import com.alibaba.fastjson.JSONObject;

/**
 * Encryption service interface
 * Users need to implement this interface and inject their own encryption implementation
 */
public interface EncryptService {

    /**
     * Decrypt request body
     *
     * @param jsonObject encrypted request data
     * @return decrypted data
     */
    JSONObject decrypt(JSONObject jsonObject);

    /**
     * Encrypt response body
     *
     * @param jsonObject plaintext response data
     * @return encrypted data
     */
    JSONObject encrypt(JSONObject jsonObject);

    /**
     * Encrypt string content
     *
     * @param content plaintext content
     * @return encrypted content
     */
    String encryptString(String content);
}
