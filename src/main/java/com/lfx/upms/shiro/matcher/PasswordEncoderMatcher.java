package com.lfx.upms.shiro.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-03-30 20:26
 */
public class PasswordEncoderMatcher implements CredentialsMatcher {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderMatcher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        SimpleAuthenticationInfo authenticationInfo = (SimpleAuthenticationInfo) info;
        String rawPassword = new String(upToken.getPassword());
        String encodedPassword = (String) authenticationInfo.getCredentials();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
