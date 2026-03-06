package com.z.ai.encrypt.advice;

import com.alibaba.fastjson.JSONObject;
import com.z.ai.encrypt.EncryptProperties;
import com.z.ai.encrypt.EncryptService;
import com.z.ai.encrypt.annotation.ApiDecrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Request body decryption advice
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EncryptRequestAdvice implements RequestBodyAdvice {

    @Autowired
    private EncryptProperties properties;

    @Autowired
    private EncryptService encryptService;

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        if (!properties.isEnabled()) {
            return false;
        }

        // Skip in dev environment if configured
        if (shouldSkip()) {
            return false;
        }

        // Check if method has @ApiDecrypt annotation with in=true
        Method method = methodParameter.getMethod();
        if (method == null) {
            return false;
        }

        ApiDecrypt annotation = method.getAnnotation(ApiDecrypt.class);
        return annotation != null && annotation.in();
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                           Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        if (body == null) {
            return body;
        }

        try {
            String jsonString = JSONObject.toJSONString(body);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            JSONObject decrypted = encryptService.decrypt(jsonObject);
            return decrypted;
        } catch (Exception e) {
            log.error("Request body decryption failed", e);
            throw new RuntimeException("Request body decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                  Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return body;
    }

    private boolean shouldSkip() {
        if (!properties.isDevSkip()) {
            return false;
        }

        String profile = properties.getProfile();
        if (!StringUtils.hasText(profile)) {
            return false;
        }

        String[] devProfiles = {"dev", "development"};
        return Arrays.asList(devProfiles).contains(profile.toLowerCase());
    }
}
