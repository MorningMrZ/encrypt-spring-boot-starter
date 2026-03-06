package com.z.ai.encrypt.annotation;

import java.lang.annotation.*;

/**
 * Annotation for enabling API encryption/decryption
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDecrypt {

    /**
     * Whether to decrypt request body
     */
    boolean in() default false;

    /**
     * Whether to encrypt response body
     */
    boolean out() default false;
}
