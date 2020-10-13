package com.tnt.authjwt.config.oauth.resourceserver;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@customUserDetailsService.getAuthUser(#this)")
public @interface CurrentUser {}