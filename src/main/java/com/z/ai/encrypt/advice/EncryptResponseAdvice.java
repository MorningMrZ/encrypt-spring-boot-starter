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
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Response body encryption advice
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private EncryptProperties properties;

    @Autowired
    private EncryptService encryptService;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        if (!properties.isEnabled()) {
            return false;
        }

        // Skip in dev environment if configured
        if (shouldSkip()) {
            return false;
        }

        // Check if method has @ApiDecrypt annotation with out=true
        Method method = methodParameter.getMethod();
        if (method == null) {
            return false;
        }

        ApiDecrypt annotation = method.getAnnotation(ApiDecrypt.class);
        return annotation != null && annotation.out();
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }

        try {
            String jsonString = JSONObject.toJSONString(body);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            JSONObject encrypted = encryptService.encrypt(jsonObject);
            return encrypted;
        } catch (Exception e) {
            log.error("Response body encryption failed", e);
            throw new RuntimeException("Response body encryption failed: " + e.getMessage(), e);
        }
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
