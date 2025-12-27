package com.javasm.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author: wilson
 * @date: 2022-07-26 15:48
 * @version: 1.0
 */
public class PhoneNumAuthenticationToken extends AbstractAuthenticationToken {

    private final Object phone;
    private Object code;

    public PhoneNumAuthenticationToken(Object phone, Object code) {
        super(null);
        this.phone = phone;
        this.code = code;
        this.setAuthenticated(false);
    }

    public PhoneNumAuthenticationToken(Object phone, Object code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phone = phone;
        this.code = code;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return phone;
    }

    public Object getPhone() {
        return phone;
    }

    public Object getCode() {
        return code;
    }
}
