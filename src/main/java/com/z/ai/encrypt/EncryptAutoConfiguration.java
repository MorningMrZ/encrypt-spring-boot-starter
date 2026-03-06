package com.z.ai.encrypt;

import com.z.ai.encrypt.advice.EncryptRequestAdvice;
import com.z.ai.encrypt.advice.EncryptResponseAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto configuration for encryption/decryption
 */
@AutoConfiguration
@EnableConfigurationProperties(EncryptProperties.class)
@ConditionalOnProperty(prefix = "encrypt", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EncryptAutoConfiguration {

    @Bean
    public EncryptService encryptService(EncryptProperties properties) {
        return new DefaultEncryptService(properties);
    }

    @Bean
    public EncryptRequestAdvice encryptRequestAdvice() {
        return new EncryptRequestAdvice();
    }

    @Bean
    public EncryptResponseAdvice encryptResponseAdvice() {
        return new EncryptResponseAdvice();
    }
}
