package com.z.ai.encrypt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "encrypt")
public class EncryptProperties {

    /**
     * Whether to enable encryption/decryption
     */
    private boolean enabled = true;

    /**
     * Application public key
     */
    private String appPublicKey;

    /**
     * Private key
     */
    private String privateKey;

    /**
     * Environment profile for skip judgment
     */
    private String profile = "dev";

    /**
     * Whether to skip encryption/decryption in dev environment
     */
    private boolean devSkip = true;
}
